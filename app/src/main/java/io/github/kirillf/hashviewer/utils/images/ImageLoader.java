package io.github.kirillf.hashviewer.utils.images;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.exceptions.ImageTaskException;
import io.github.kirillf.hashviewer.utils.future.CommonFuture;
import io.github.kirillf.hashviewer.utils.future.Filter;
import io.github.kirillf.hashviewer.utils.future.Future;
import io.github.kirillf.hashviewer.utils.future.FutureCallback;
import io.github.kirillf.hashviewer.utils.future.FutureExecutor;
import io.github.kirillf.hashviewer.utils.http.HttpRequest;
import io.github.kirillf.hashviewer.utils.http.HttpResponse;
import io.github.kirillf.hashviewer.utils.http.HttpService;

/**
 * ImageLoader based on non-blocking future concept.
 */
public class ImageLoader {
    private static final String TAG = ImageLoader.class.getName();
    private static ImageLoader imageLoader;
    private ImageCache imageCache;
    private HttpService httpService;

    public ImageLoader() {
        httpService = HttpService.getInstance();
        imageCache = new ImageCache();
    }

    public synchronized static ImageLoader getInstance() {
        if (imageLoader == null) {
            imageLoader = new ImageLoader();
        }
        return imageLoader;
    }

    private class FutureDrawable extends BitmapDrawable {
        private final WeakReference<ImageTask> imageTaskReference;

        public FutureDrawable(Resources res, Bitmap bitmap, ImageTask imageTask) {
            super(res, bitmap);
            imageTaskReference = new WeakReference<>(imageTask);
        }

        public ImageTask getImageTask() {
            return imageTaskReference.get();
        }
    }

    /**
     * Filter converting HttpResponse content to Bitmap
     */
    private class ImageFuture implements Filter<HttpRequest, HttpRequest, HttpResponse, Bitmap> {
        private boolean isCancelled;

        public void cancel() {
            isCancelled = true;
        }

        @Override
        public Future<Bitmap> apply(final HttpRequest httpRequest, FutureExecutor<HttpRequest, HttpResponse> executor) {
            final CommonFuture<Bitmap> futureBitmap = new CommonFuture<>();
            if (!isCancelled) {
                executor.apply(httpRequest).onSuccess(new FutureCallback<HttpResponse>() {
                    @Override
                    public void apply(HttpResponse result) {
                        if (result.getResponseCode() == Constants.HTTP_OK) {
                            byte[] content = result.getContent();
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
                            if (bitmap != null) {
                                futureBitmap.setFutureResult(bitmap);
                            } else {
                                futureBitmap.setFutureFailed(new ImageTaskException("Response code: " + result.getResponseCode()));
                            }
                        }
                    }
                }).onFailure(new FutureCallback<Throwable>() {
                    @Override
                    public void apply(Throwable result) {
                        futureBitmap.setFutureFailed(result);
                    }
                });
            }
            if (isCancelled) {
                futureBitmap.cancel();
            }
            return futureBitmap;
        }
    }

    /**
     * General image loading and displaying task
     */
    private class ImageTask {
        private WeakReference<ImageView> imageViewReference;
        private String url;
        private int placeholder;
        private ImageFuture imageService;
        private Future<Bitmap> imageFuture;
        private boolean isCancelled;

        public ImageTask(String url, ImageView imageView, int placeholder) {
            this.imageViewReference = new WeakReference<>(imageView);
            this.url = url;
            this.placeholder = placeholder;
        }

        public String getUrl() {
            return url;
        }

        public void cancel() {
            this.isCancelled = true;
            if (imageService != null) {
                imageService.cancel();
            }
            if (imageFuture != null) {
                imageFuture.cancel();
            }
        }

        public void execute() {
            final HttpRequest request = new HttpRequest(url);
            request.setMethod(HttpRequest.Method.GET);
            imageService = new ImageFuture();
            imageFuture = imageService.apply(request, httpService);
            imageFuture.onSuccess(new FutureCallback<Bitmap>() {
                @Override
                public void apply(Bitmap result) {
                    if (result != null) {
                        imageCache.put(url, result);
                        if (!isCancelled) {
                            placeBitmap(imageViewReference, result);
                        }
                    }
                }
            }).onFailure(new FutureCallback<Throwable>() {
                @Override
                public void apply(Throwable result) {
                    Log.w(TAG, result);
                    placeResource(imageViewReference, placeholder);
                }
            });
        }

        private void placeBitmap(WeakReference<ImageView> imageViewReference, final Bitmap bitmap) {
            if (imageViewReference != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        }

        private void placeResource(WeakReference<ImageView> imageViewReference, final int resource) {
            if (imageViewReference != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(resource);
                        }
                    });
                }
            }
        }
    }

    private ImageTask getImageTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof FutureDrawable) {
                final FutureDrawable futureDrawable = (FutureDrawable) drawable;
                return futureDrawable.getImageTask();
            }
        }
        return null;
    }


    private boolean cancelPotentialWork(String url, ImageView imageView) {
        final ImageTask imageTask = getImageTask(imageView);

        if (imageTask != null) {
            final String taskUrl = imageTask.getUrl();
            if (!taskUrl.equals(url)) {
                imageTask.cancel();
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Image loader interface method. Asynchronously download bitmap and display in ImageView
     * @param context method caller context
     * @param url image url
     * @param imageView target view to place bitmap
     * @param errorPlaceholder placeholder resource id for failed downloads
     * @param placeholder placeholder bitmap for loading
     */
    public void loadImage(Context context, final String url, final ImageView imageView, final int errorPlaceholder, final Bitmap placeholder) {
        if (cancelPotentialWork(url, imageView)) {
            final Bitmap bitmap = imageCache.get(url);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                return;
            }
            ImageTask imageTask = new ImageTask(url, imageView, errorPlaceholder);
            final FutureDrawable futureDrawable = new FutureDrawable(context.getResources(), placeholder, imageTask);
            imageView.setImageDrawable(futureDrawable);
            imageTask.execute();
        }
    }
}


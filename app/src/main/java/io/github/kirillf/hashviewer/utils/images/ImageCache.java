package io.github.kirillf.hashviewer.utils.images;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Simple in-memory image cache based on LRUCache.
 * Cache parameter predefined.
 */
public class ImageCache {
    private static final int MAX_SIZE = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
    private LruCache<String, Bitmap> imageCache;

    public ImageCache() {
        imageCache = new LruCache<>(MAX_SIZE);
    }

    public void put(String url, Bitmap image) {
        if ((url != null || image != null) && get(url) == null) {
            imageCache.put(url, image);
        }
    }

    public Bitmap get(String url) {
        return imageCache.get(url);
    }
}

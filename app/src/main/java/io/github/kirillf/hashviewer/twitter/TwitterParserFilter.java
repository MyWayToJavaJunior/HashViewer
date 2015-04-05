package io.github.kirillf.hashviewer.twitter;

import android.util.Log;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.exceptions.TwitterException;
import io.github.kirillf.hashviewer.utils.future.CommonFuture;
import io.github.kirillf.hashviewer.utils.future.Filter;
import io.github.kirillf.hashviewer.utils.future.Future;
import io.github.kirillf.hashviewer.utils.future.FutureCallback;
import io.github.kirillf.hashviewer.utils.future.FutureExecutor;
import io.github.kirillf.hashviewer.utils.http.HttpRequest;
import io.github.kirillf.hashviewer.utils.http.HttpResponse;

public class TwitterParserFilter implements Filter<HttpRequest, HttpRequest, HttpResponse, List<TwitterObject>> {
    private static final String TAG = TwitterParserFilter.class.getName();
    private static TwitterParser parser = new TwitterParser();

    @Override
    public Future<List<TwitterObject>> apply(HttpRequest httpRequest, FutureExecutor<HttpRequest, HttpResponse> executor) {
        final ParserFuture listFuture = new ParserFuture();
        Future<HttpResponse> httpResponseFuture = executor.apply(httpRequest);
        listFuture.setHttpFuture(httpResponseFuture);
        httpResponseFuture.onSuccess(new FutureCallback<HttpResponse>() {
            @Override
            public void apply(HttpResponse result) {
                int code = result.getResponseCode();
                if (code == Constants.HTTP_OK) {
                    try {
                        String content = new String(result.getContent(), "UTF-8");
                        List<TwitterObject> twitterObjects = parser.parse(content);
                        listFuture.setFutureResult(twitterObjects);
                    } catch (UnsupportedEncodingException | JSONException e) {
                        Log.w(TAG, "Unable to parse tweet: " + e.toString());
                    }
                } else {
                    listFuture.setFutureFailed(new TwitterException("Api response codeL " + code));
                }
            }
        }).onFailure(new FutureCallback<Throwable>() {
            @Override
            public void apply(Throwable result) {
                Log.w(TAG, result);
                listFuture.setFutureFailed(result);
            }
        });
        return listFuture;
    }

    private class ParserFuture extends CommonFuture<List<TwitterObject>> {
        private Future<HttpResponse> httpResponseFuture;

        public void setHttpFuture(Future<HttpResponse> httpResponseFuture) {
            this.httpResponseFuture = httpResponseFuture;
        }

        @Override
        public void cancel() {
            super.cancel();
            if (httpResponseFuture != null) {
                httpResponseFuture.cancel();
            }
        }
    }

}

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

    @Override
    public Future<List<TwitterObject>> apply(HttpRequest httpRequest, FutureExecutor<HttpRequest, HttpResponse> executor) {
        final CommonFuture<List<TwitterObject>> listFuture = new CommonFuture<>();
        final TwitterParser parser = new TwitterParser();
        executor.apply(httpRequest).onSuccess(new FutureCallback<HttpResponse>() {
            @Override
            public void apply(HttpResponse result) {
                int code = result.getResponseCode();
                if (code == Constants.HTTP_OK) {
                    try {
                        String content = new String(result.getContent(), "UTF-8");
                        List<TwitterObject> twitterObjects = parser.parse(content);
                        listFuture.setFutureResult(twitterObjects);
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
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
}

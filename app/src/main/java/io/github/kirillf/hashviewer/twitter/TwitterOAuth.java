package io.github.kirillf.hashviewer.twitter;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.utils.future.AbstractFuture;
import io.github.kirillf.hashviewer.utils.future.Future;
import io.github.kirillf.hashviewer.utils.future.FutureCallback;
import io.github.kirillf.hashviewer.utils.future.FutureExecutor;
import io.github.kirillf.hashviewer.utils.http.HttpRequest;
import io.github.kirillf.hashviewer.utils.http.HttpResponse;
import io.github.kirillf.hashviewer.utils.http.HttpService;

class TwitterOAuth implements FutureExecutor<TwitterCredentials, String> {
    private static final String TAG = TwitterOAuth.class.getName();
    private HttpService httpService;

    public TwitterOAuth(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public Future<String> apply(final TwitterCredentials credentials) {
        final HttpRequest request = new HttpRequest(Constants.TWITTER_OAUTH_URL);
        final ResultFuture resultFuture = new ResultFuture();
        request.setMethod(HttpRequest.Method.POST);
        String authParams = prepareRequestParams(credentials.getKey(), credentials.getSecret());
        String basic = "Basic " + authParams;
        request.addHeader("Authorization", basic);
        request.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        request.setBody("grant_type=client_credentials");
        final Future<HttpResponse> responseFuture = httpService.apply(request);
        responseFuture.onSuccess(new FutureCallback<HttpResponse>() {
            @Override
            public void apply(HttpResponse result) {
                int code = result.getResponseCode();
                Log.i(TAG, String.valueOf(code));
                byte[] content = result.getContent();
                if (content.length > 0) {
                    try {
                        String strResult = new String(content, "UTF-8");
                        String bearerToken = getBearerToken(strResult);
                        resultFuture.setBearerToken(bearerToken);
                    } catch (UnsupportedEncodingException | JSONException e) {
                        resultFuture.setRequestFailed(e);
                    }
                }
            }
        });
        responseFuture.onFailure(new FutureCallback<Throwable>() {
            @Override
            public void apply(Throwable result) {
                resultFuture.setRequestFailed(result);
                Log.e(TAG, String.valueOf(result));
            }
        });
        return resultFuture;
    }

    private class ResultFuture extends AbstractFuture<String> {
        public void setBearerToken(String token) {
            setResult(token);
        }

        public void setRequestFailed(Throwable t) {
            setThrowable(t);
            setFailed();
        }
    }

    private String getBearerToken(String content) throws JSONException {
        JSONObject jsonObject = new JSONObject(content);
        String tokenType = jsonObject.getString("token_type");
        String accessToken = jsonObject.getString("access_token");
        if (tokenType.equals("bearer")) {
            return accessToken;
        }
        return null;
    }

    private String prepareRequestParams(String key, String secret) {
        String requestParams = null;
        try {
            String encodedKey = URLEncoder.encode(key, "UTF-8");
            String encodedSecret = URLEncoder.encode(secret, "UTF-8");
            requestParams = encodedKey + ":" + encodedSecret;
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e);
        }
        assert requestParams != null;
        return Base64.encodeToString(requestParams.getBytes(), Base64.NO_WRAP);
    }
}

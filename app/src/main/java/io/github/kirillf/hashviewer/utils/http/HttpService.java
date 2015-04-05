package io.github.kirillf.hashviewer.utils.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.kirillf.hashviewer.utils.future.AbstractFuture;
import io.github.kirillf.hashviewer.utils.future.Future;
import io.github.kirillf.hashviewer.utils.future.FutureExecutor;

/**
 * HttpService object - singleton service implementation of FutureExecutor.
 * Service, performing http operation.
 * Based on ExecutorService and ScheduledThreadPoolExecutor.
 * Http requests and responses performed by HttpUrlConnection class.
 *
 */
public class HttpService implements FutureExecutor<HttpRequest, HttpResponse> {
    private static final String TAG = HttpService.class.getName();
    private static ExecutorService executor;
    private static final int POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private static HttpService httpService;

    private HttpService() {
        executor = Executors.newScheduledThreadPool(POOL_SIZE);
    }

    public static HttpService getInstance() {
        if (httpService == null) {
            httpService = new HttpService();
        }
        return httpService;
    }

    @Override
    public Future<HttpResponse> apply(HttpRequest request) {
        HttpResponseFuture responseFuture = new HttpResponseFuture();
        HttpTask task = new HttpTask(request, responseFuture);
        executor.submit(task, request.getDelay());
        return responseFuture;
    }

    private class HttpResponseFuture extends AbstractFuture<HttpResponse> {

        public void onResponse(HttpResponse httpResponse) {
            setResult(httpResponse);
        }

    }

    private class HttpTask implements Runnable {
        private HttpRequest request;
        private HttpResponseFuture result;

        HttpTask(HttpRequest request, HttpResponseFuture result) {
            this.request = request;
            this.result = result;
        }

        @Override
        public void run() {
            if (!result.isCancelled()) {
                try {
                    URL url = new URL(request.getUrl());
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod(request.getMethod().getNativeParam());
                    Log.i(TAG, httpURLConnection.getRequestMethod());
                    httpURLConnection.setConnectTimeout(request.getConnectionTimeout());
                    httpURLConnection.setReadTimeout(request.getReadTimeout());
                    httpURLConnection.setDoInput(true);
                    for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                        httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                    if (request.getMethod().equals(HttpRequest.Method.POST)) {
                        httpURLConnection.setDoOutput(true);
                        String body = request.getBody();
                        if (body != null) {
                            byte[] byteBody = body.getBytes();
                            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(byteBody.length));
                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            outputStream.write(byteBody);
                            outputStream.close();
                        }
                    }
                    httpURLConnection.connect();
                    Log.i(TAG, httpURLConnection.getResponseMessage());
                    int responseCode = httpURLConnection.getResponseCode();
                    HttpResponse response = new HttpResponse(responseCode);
                    try {
                        response.setContent(getResponseContent(httpURLConnection));
                    } catch (IOException e) {
                        Log.w(TAG, getErrorMessage(httpURLConnection));
                        Log.w(TAG, e);
                    }
                    result.onResponse(response);
                    httpURLConnection.disconnect();
                } catch (IOException e) {
                    Log.w(TAG, e);
                    result.getFailureCallback().apply(e);
                }
            }
        }
    }

    private String getErrorMessage(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = httpURLConnection.getErrorStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private byte[] getResponseContent(HttpURLConnection connection) throws IOException {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            inputStream = connection.getInputStream();
            int data;
            while ((data = inputStream.read()) != -1) {
                byteArrayOutputStream.write(data);
            }
            return byteArrayOutputStream.toByteArray();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            byteArrayOutputStream.close();
        }
    }
}

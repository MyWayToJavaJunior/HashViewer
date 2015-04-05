package io.github.kirillf.hashviewer.utils.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents http request context. Configurable request params,
 * headers and request body.
 */
public class HttpRequest {
    private final String url;
    private Method method;
    private int readTimeout = 9000;
    private int connectionTimeout = 9000;
    private Map<String, String> headers;
    private String body;

    public HttpRequest(String url) {
        this.url = url;
        headers = new LinkedHashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public enum Method {
        GET("GET"),
        PUT("PUT"),
        POST("POST");

        private String nativeParam;

        Method(String nativeParam) {
            this.nativeParam = nativeParam;
        }

        public String getNativeParam() {
            return nativeParam;
        }
    }
}

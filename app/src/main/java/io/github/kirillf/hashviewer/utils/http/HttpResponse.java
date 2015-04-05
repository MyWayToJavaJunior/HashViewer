package io.github.kirillf.hashviewer.utils.http;

/**
 * Represents http response context.
 * Response content represented as byte array.
 */
public class HttpResponse {
    private int responseCode;
    private byte[] content;
    private String contentEncoding;

    public HttpResponse(int responseCode) {
        this.responseCode = responseCode;
    }


    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getResponseCode() {
        return responseCode;
    }


    public byte[] getContent() {
        return content;
    }
}

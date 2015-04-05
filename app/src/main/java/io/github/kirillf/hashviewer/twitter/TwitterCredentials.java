package io.github.kirillf.hashviewer.twitter;

public class TwitterCredentials {
    private final String key;
    private final String secret;

    public TwitterCredentials(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }
}

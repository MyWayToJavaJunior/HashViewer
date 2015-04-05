package io.github.kirillf.hashviewer.twitter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.utils.future.Future;
import io.github.kirillf.hashviewer.utils.http.HttpRequest;
import io.github.kirillf.hashviewer.utils.http.HttpService;

/**
 * Service for communicating with Twitter REST API.
 * Based on HttpService and FutureExecutor.
 * Use non-blocking futures.
 * Singleton object.
 */
public class TwitterHttpService {
    private HttpService httpService;
    private String bearerToken;

    private static TwitterHttpService twitterService;

    private TwitterHttpService(HttpService service) {
        this.httpService = service;
    }

    public static TwitterHttpService getInstance(HttpService service) {
        if (twitterService == null) {
            twitterService = new TwitterHttpService(service);
        }
        return twitterService;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    /**
     * Twitter Oauth request.
     * Perform application-only authorization.
     * @param key application api key
     * @param secret application api secret
     * @return future with String bearer token
     */
    public Future<String> authenticate(String key, String secret) {
        TwitterOAuth oAuth = new TwitterOAuth(httpService);
        TwitterCredentials credentials = new TwitterCredentials(key, secret);
        return oAuth.apply(credentials);
    }

    /**
     * Twitter Search API request
     * @param searchQueue request string
     * @param id id for using with max_id request parameter (search for tweet before this id).
     * @return future object with list of TwitterObjects
     */
    public Future<List<TwitterObject>> query(String searchQueue, long id) {
        String query = prepareQuery(searchQueue);
        String url = null;
        try {
            url = Constants.TWITTER_SEARCH_API + "?q=" + URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (id > 0) {
            url += "&max_id=" + id;
        }
        final HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.GET);
        request.addHeader("Authorization", "Bearer " + bearerToken);
        TwitterParserFilter parserFilter = new TwitterParserFilter();
        return  parserFilter.apply(request, httpService);
    }

    private String prepareQuery(String query) {
        String[] queryParams = query.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (String s : queryParams) {
            if (s.charAt(0) != '#') {
                sb.append("#");
            }
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }
}

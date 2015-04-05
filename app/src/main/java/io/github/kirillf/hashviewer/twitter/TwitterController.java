package io.github.kirillf.hashviewer.twitter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.List;

import io.github.kirillf.hashviewer.Constants;
import io.github.kirillf.hashviewer.events.Event;
import io.github.kirillf.hashviewer.events.EventDispatcher;
import io.github.kirillf.hashviewer.exceptions.EmptyResultSetException;
import io.github.kirillf.hashviewer.utils.future.FutureCallback;
import io.github.kirillf.hashviewer.utils.http.HttpService;

/**
 * Controller provides UI calls to background core services.
 * Singleton object.
 */
public class TwitterController {
    private static final String TAG = TwitterController.class.getName();
    private TwitterHttpService twitterHttpService;
    private TwitterDataProvider<TwitterObject> dataSource;
    private EventDispatcher eventDispatcher;
    private String query;
    private Context context;

    private static TwitterController twitterController;

    private TwitterController(Context context) {
        HttpService httpService = HttpService.getInstance();
        eventDispatcher = EventDispatcher.getInstance();
        twitterHttpService = TwitterHttpService.getInstance(httpService);
        dataSource = TwitterDataSource.getInstance(eventDispatcher);
        this.context = context;
    }

    public static synchronized TwitterController getInstance(Context context) {
        if (twitterController == null) {
            twitterController = new TwitterController(context);
        }
        return twitterController;
    }

    /**
     * Authenticate in Twitter.
     * Use preset auth params (WARNING: not safe. Temporary solution)
     */
    public void authenticate() {
        if (!isInternetConnected()) {
            Event event = new Event(Event.EventType.NO_CONNECTION, "NO_CONNECTION");
            eventDispatcher.notify(event);
        }
        twitterHttpService.authenticate(Constants.consumerKey, Constants.consumerSecret).onSuccess(new FutureCallback<String>() {
            @Override
            public void apply(String result) {
                twitterHttpService.setBearerToken(result);
                Event event = new Event(Event.EventType.AUTHORIZE, result);
                eventDispatcher.notify(event);
            }
        }).onFailure(new FutureCallback<Throwable>() {
            @Override
            public void apply(Throwable result) {
                Event event = new Event(Event.EventType.ERROR, result.toString());
                eventDispatcher.notify(event);
            }
        });
    }

    /**
     * Reset data source.
     * Cleanup previous search results.
     */
    public void reset() {
        Event event = new Event(Event.EventType.RESET, "RESET");
        eventDispatcher.notify(event);
        dataSource.reset();
    }

    /**
     * Load more data with current query.
     * Infinite feed purposes.
     */
    public void loadMore() {
        if (query != null) {
            try {
                searchTweets(query, dataSource.getLastId() - 1);
            } catch (EmptyResultSetException e) {
                Log.w(TAG, e);
            }
        }
    }


    private void searchTweets(String query, long id) {
        if (!isInternetConnected()) {
            Event event = new Event(Event.EventType.NO_CONNECTION, "NO_CONNECTION");
            eventDispatcher.notify(event);
        }
        this.query = query;
        twitterHttpService.query(query, id).onSuccess(new FutureCallback<List<TwitterObject>>() {
            @Override
            public void apply(List<TwitterObject> result) {
                dataSource.addSearchResults(result);
            }
        }).onFailure(new FutureCallback<Throwable>() {
            @Override
            public void apply(Throwable result) {
                Event event = new Event(Event.EventType.ERROR, result.toString());
                eventDispatcher.notify(event);
            }
        });
    }

    /**
     * Execute search query method.
     * @param query query
     */
    public void searchTweets(String query) {
        searchTweets(query, -1);
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

package io.github.kirillf.hashviewer;

import android.app.Application;
import android.util.Log;

import io.github.kirillf.hashviewer.events.EventDispatcher;
import io.github.kirillf.hashviewer.exceptions.InitializeException;
import io.github.kirillf.hashviewer.twitter.TwitterController;
import io.github.kirillf.hashviewer.twitter.TwitterDataSource;
import io.github.kirillf.hashviewer.twitter.TwitterHttpService;
import io.github.kirillf.hashviewer.utils.http.HttpService;

public class HashViewerApplication extends Application {
    private static final String TAG = HashViewerApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        HttpService httpService = HttpService.getInstance();
        TwitterDataSource.init(eventDispatcher);
        TwitterHttpService.init(httpService);
        try {
            TwitterController.init(this, TwitterHttpService.getInstance(),
                    eventDispatcher, TwitterDataSource.getInstance());
        } catch (InitializeException e) {
            Log.e(TAG, e.toString());
        }
    }
}

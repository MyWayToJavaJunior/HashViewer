package io.github.kirillf.hashviewer.twitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.kirillf.hashviewer.events.Event;
import io.github.kirillf.hashviewer.events.EventDispatcher;
import io.github.kirillf.hashviewer.exceptions.EmptyResultSetException;
import io.github.kirillf.hashviewer.exceptions.InitializeException;

/**
 * In-memory TwitterDataProvider implementation.
 * Based on simple array.
 * Singleton object.
 */
public class TwitterDataSource implements TwitterDataProvider<TwitterObject> {
    private static List<TwitterObject> searchResults = new ArrayList<>();
    private EventDispatcher eventDispatcher;

    private static TwitterDataSource twitterDataSource;

    private TwitterDataSource(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public static void init(EventDispatcher eventDispatcher) {
        twitterDataSource = new TwitterDataSource(eventDispatcher);
    }

    public static synchronized TwitterDataSource getInstance() throws InitializeException {
        if (twitterDataSource == null) {
            throw new InitializeException("Not initialized");
        }
        return twitterDataSource;
    }

    @Override
    public void addSearchResults(Collection<TwitterObject> results) {
        searchResults.addAll(results);
        if (results.size() > 0) {
            Event event = new Event(Event.EventType.DATA_RECEIVED, null);
            eventDispatcher.notify(event);
        } else {
            Event event = new Event(Event.EventType.END_OF_DATA, null);
            eventDispatcher.notify(event);
        }
    }

    public Collection<TwitterObject> getSearchResults() {
        return searchResults;
    }

    public void reset() {
        searchResults.clear();
    }

    public long getLastId() throws EmptyResultSetException {
        int size = searchResults.size();
        if (size > 0) {
            TwitterObject object = searchResults.get(size - 1);
            return object.getId();
        } else {
            throw new EmptyResultSetException();
        }
    }
}

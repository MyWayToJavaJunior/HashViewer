package io.github.kirillf.hashviewer.twitter;

import java.util.Collection;

import io.github.kirillf.hashviewer.exceptions.EmptyResultSetException;

/**
 * Represents data storage interface for search results.
 * @param <T>
 */
public interface TwitterDataProvider<T> {
    void addSearchResults(Collection<T> results);
    Collection<T> getSearchResults();
    void reset();
    long getLastId() throws EmptyResultSetException;
}

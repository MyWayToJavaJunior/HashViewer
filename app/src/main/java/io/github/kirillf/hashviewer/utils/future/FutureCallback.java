package io.github.kirillf.hashviewer.utils.future;

/**
 * Interface representing callback object for non-blocking future
 * @param <T> generic result type
 */
public interface FutureCallback<T> {

    public void apply(T result);
}

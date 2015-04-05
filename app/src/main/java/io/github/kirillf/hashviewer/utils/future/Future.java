package io.github.kirillf.hashviewer.utils.future;

/**
 * Non-blocking interface representing result of async operation
 * @param <T> generic type of result
 */
public interface Future<T> {
    T get();

    Future<T> onSuccess(FutureCallback<T> successCallback);

    Future<T> onFailure(FutureCallback<Throwable> failureCallback);

    void cancel();

    boolean isCancelled();
}

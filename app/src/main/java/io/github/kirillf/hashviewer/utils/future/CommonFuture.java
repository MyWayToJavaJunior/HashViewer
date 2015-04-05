package io.github.kirillf.hashviewer.utils.future;

/**
 * Basic AbstractFuture implementation.
 *
 * @param <T> result type
 */
public class CommonFuture<T> extends AbstractFuture<T> {

    public void setFutureResult(T result) {
        setResult(result);
    }

    public void setFutureFailed(Throwable t) {
        setThrowable(t);
        setFailed();
    }

}

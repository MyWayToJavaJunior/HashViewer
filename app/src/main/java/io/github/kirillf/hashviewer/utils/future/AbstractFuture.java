package io.github.kirillf.hashviewer.utils.future;

/**
 * Basic future interface implementation.
 * Contains basic method realisations.
 * Assumed to be extended.
 *
 * @param <T> result type
 */
public abstract class AbstractFuture<T> implements Future<T> {
    private FutureCallback<T> successCallback;
    private FutureCallback<Throwable> failureCallback;
    private boolean isFailed;
    private boolean isCompleted;
    private boolean isCancelled;
    private T result;
    private Throwable throwable;

    protected void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    protected void setFailed() {
        this.isFailed = true;
        if (failureCallback != null && !isCancelled) {
            failureCallback.apply(throwable);
        }
    }

    protected void setResult(T result) {
        this.result = result;
        isCompleted = true;
        if (successCallback != null && !isCancelled) {
            successCallback.apply(result);
        }
    }

    @Override
    public T get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<T> onSuccess(FutureCallback<T> successCallback) {
        this.successCallback = successCallback;
        if (isCompleted && !isCancelled) {
            successCallback.apply(result);
        }
        return this;
    }

    @Override
    public Future<T> onFailure(FutureCallback<Throwable> failureCallback) {
        this.failureCallback = failureCallback;
        if (isFailed && !isCancelled) {
            failureCallback.apply(throwable);
        }
        return this;
    }

    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    public FutureCallback<Throwable> getFailureCallback() {
        return failureCallback;
    }

    public FutureCallback<T> getSuccessCallback() {
        return successCallback;
    }
}

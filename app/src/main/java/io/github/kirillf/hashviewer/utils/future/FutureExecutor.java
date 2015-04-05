package io.github.kirillf.hashviewer.utils.future;

/**
 * Interface representing executor service for non-blocking futures.
 * Should perform async operations.
 *
 * @param <Params> input type
 * @param <Result> output type
 */
public interface FutureExecutor<Params, Result> {
    public Future<Result> apply(Params params);
}

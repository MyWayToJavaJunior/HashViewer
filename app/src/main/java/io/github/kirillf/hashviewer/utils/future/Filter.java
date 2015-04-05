package io.github.kirillf.hashviewer.utils.future;

/**
 * Interface representing converter for objects in
 * non-blocking future-executor stack.
 * Can be used like some king of functional composition.
 * @param <In> input object type
 * @param <ReqIn> input type for underlying service (to be converted to)
 * @param <RepOut> output type for underlying service (result type)
 * @param <Out> output type of filter (converted service output type)
 */
public interface Filter<In, ReqIn, RepOut, Out> {
    Future<Out> apply(In in, FutureExecutor<ReqIn, RepOut> executor);
}

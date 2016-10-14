package util.async;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public abstract class AsyncEvent<T> {

    public abstract void onComplete(final T item);
}

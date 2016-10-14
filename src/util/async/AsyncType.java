package util.async;

/**
 * Created by Ryan Thomson on 13/10/2016.
 */
public enum AsyncType {

    SINGLE, // launch a task using the single-threaded executor
    MULTI   // launch a task using the multi-threaded executor
}

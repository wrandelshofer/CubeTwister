/*
 * @(#)Dispatcher.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

/**
 * A Dispatcher enques Runnable objects in a Queue for later
 * processing by worker threads.
 *
 * @author  Werner Randelshofer
 */
public interface Dispatcher {
    /**
     * Enqueues a Runnable object and for later processing by
     * worker threads.
     */
    public void dispatch(Runnable r);

    public void join() throws InterruptedException;
}

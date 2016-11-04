/*
 * @(#)Dispatcher.java  1.1  2008-01-02
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */ 

package ch.randelshofer.util;

/**
 * A Dispatcher enques Runnable objects in a Queue for later
 * processing by worker threads.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2008-01-02 Method stop added. 
 * <br>1.0 2002-05-18
 */
public interface Dispatcher {
    /**
     * Enqueues a Runnable object and for later processing by
     * worker threads.
     */
    public void dispatch(Runnable r);

    public void join() throws InterruptedException;
}

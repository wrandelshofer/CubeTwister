/*
 * @(#)Dispatcher.java  1.1  2008-01-02
 *
 * Copyright (c) 2001-2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
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

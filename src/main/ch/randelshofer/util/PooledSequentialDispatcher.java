/*
 * @(#)PooledSequentialDispatcher.java  1.1.1  2007-12-25
 *
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.util;

import java.util.*;
/**
 * Processes Runnable objects sequentially on a pool of processor threads.
 * The order in which the runnable objects are processed is
 * the same in which they were added to the dispatcher.
 * <p>
 * Per default, there is one pool per VM running 5 threads. You can
 * assign the dispatcher to a different thread pool using the constructor
 * or using method <code>setThreadPool</code>.
 * <p>
 * The static method <code>dispatchConcurrently</code> enqueues the
 * runnable object for concurrent execution. That is, it will be
 * processed as soon as one of the pooled threads becomes available.
 * <p>
 * The instance method <code>dispatch</code> enqueues
 * the runnable object for sequential execution. It will be processed
 * by one of the pooled threads when all of the preceedingly enqueued
 * runnables have been processed. There is one queue per instance.
 * <p>
 * Design pattern used: Acceptor
 * Role in design pattern: EventCollector and EventProcessor
 * <p>
 * <b>Example 1</b>
 * <br>The following program prints "one", "two", "three" on
 * concurrent processor threads:
 * <pre>
 * PooledSequentialDispatcher.dispatchConcurrently(
 *     new Runnable(public void run() { System.out.println("one"); });
 * );
 * PooledSequentialDispatcher.dispatchConcurrently(
 *     new Runnable(public void run() { System.out.println("two"); });
 * );
 * PooledSequentialDispatcher.dispatchConcurrently(
 *     new Runnable(public void run() { System.out.println("three"); });
 * );
 * </pre>
 * The order of the output is not granted, since the runnables are
 * executed concurrently. It could be "one","two","three" or "three","one","two"
 * or any other possible combination. Even intermingled output is possible.
 *
 * <p>
 * <b>Example 2</b>
 * <br>The following program prints "one", "two", "three" on
 * sequential processor threads:
 * <pre>
 * PooledSequentialDispatcher dispatcher = new PooledSequentialDispatcher();
 *
 * dispatcher.dispatch(
 *     new Runnable(public void run() { System.out.println("one"); });
 * );
 * dispatcher.dispatch(
 *     new Runnable(public void run() { System.out.println("two"); });
 * );
 * dispatcher.dispatch(
 *     new Runnable(public void run() { System.out.println("three"); });
 * );
 * </pre>
 * Since all runnables are dispatched by the same PoolDispatcherAWT instance,
 * it is granted, that they will be executed in the same order as they were
 * added to the queue. It is also granted, that the output will not be
 * intermingled, because a runnable will be executed only, when its predecessor
 * has finished.
 *
 * @author Werner Randelshofer
 * @version 1.1.1 2007-12-25 Fixed null pointer in static dispatch method. 
 * <br>1.1 2003-04-05 Method stop() invokes wait() now on object 'queue' and
 * not on thisi object.
 * <br>1.0.1 2001-12-31 Comments translated into english.
 * <br>1.0 2001-12-27 Created.
 */
public class PooledSequentialDispatcher implements Dispatcher, Runnable {
    /**
     * This variable holds the global thread pool for processor threads.
     */
    private static final ConcurrentDispatcher globalThreadPool = new ConcurrentDispatcher();
    
    /**
     * This variable holds the local thread pool for processor threads.
     */
    private /*static*/ ConcurrentDispatcher threadPool;
    
    /**
     * The state variable is used to determine the
     * state of the processor thread.
     */
    private volatile int state = STOPPED;
    private final static int ENQUEUEING = 0;
    private final static int STOPPED = 1;
    private final static int STARTING = 2;
    private final static int RUNNING = 3;
    private final static int STOPPING = 4;
    
    /**
     * The queue stores Runnable objects until they
     * can be processed by the processor.
     */
    private final LinkedList<Runnable> queue = new LinkedList<Runnable>();
    
    /**
     * Creates a new PooledSequentialDispatcher which uses
     * the global threadPool for dispatching its queue.
     */
    public PooledSequentialDispatcher() {
        threadPool = globalThreadPool;
    }
    
    /**
     * Sets the maximum number of concurrent threads.
     * @param maxThreadCount Maximal number of concurrent threads.
     * A value of zero or below zero stops the dispatcher
     * when the queue is empty.
     */
    public void setMaxThreadCount(int maxThreadCount) {
        threadPool.setMaxThreadCount(maxThreadCount);
    }
    /**
     * Retunrs the maximal number of concurrent threads.
     */
    public int getMaxThreadCount() {
        return threadPool.getMaxThreadCount();
    }
    
    /**
     * Assigns this dispatcher to the specified thread pool.
     */
    public void setThreadPool(ConcurrentDispatcher threadPool) {
        this.threadPool = threadPool;
    }
    /**
     * Returns the underlying thread pool.
     */
    public ConcurrentDispatcher getThreadPool() {
        return threadPool;
    }
    
    /**
     * Enqueues the Runnable object, and executes
     * it concurrently by one of the processor threads.
     * The runnables are not necesseraly executed
     * in the same order as they were enqueued.
     *
     * @param runner A runnable.
     */
    public static void dispatchConcurrently(Runnable runner) {
        globalThreadPool.dispatch(runner);
    }
    
    /**
     * Enqueues the Runnable object, and executes
     * it sequentially on one of the processor threads
     * of the thread pool associated with this class.
     * The runnables are executed in the same order
     * as they were enqueued.
     *
     * @param runner A runnable.
     */
    public void dispatch(Runnable runner) {
        dispatch(runner, threadPool);
    }
    /**
     * Enqueues the Runnable object, and executes
     * it sequentially on one of the processor threads
     * of the specified thread pool or - if there is already
     * a thread associated with the queue - on that thread.
     * The runnables are executed in the same order
     * as they were enqueued.
     *
     * @param runner A runnable.
     *
     * @see #reassign
     */
    public void dispatch(Runnable runner, ConcurrentDispatcher pool) {
        synchronized(queue) {
            queue.addLast(runner);
            
            if (state == STOPPED) {
                state = STARTING;
                pool.dispatch(this);
            }
        }
    }
    
    /**
     * (Re)starts the
     * Reassigns the queue to the thread pool provided
     * by this class.
     */
    public void reassign() {
        synchronized(queue) {
            stop();
            if (! queue.isEmpty()) {
                state = STARTING;
                threadPool.dispatch(this);
            }
        }
    }
    
    /**
     * Starts the event processor.
     */
    public void start() {
        synchronized(queue) {
            if (state == ENQUEUEING) {
                if (queue.size() > 0) {
                    state = STARTING;
                    threadPool.dispatch(this);
                } else {
                    state = STOPPED;
                }
            }
        }
    }
    /**
     * Stops the event processor and waits until
     * it has finished.
     */
    public void stop() {
        synchronized(queue) {
            if (state == RUNNING) {
                state = STOPPING;
                while (state != STOPPED) {
                    try { queue.wait(); } catch (InterruptedException e) {}
                }
            } else {
                state = STOPPED;
            }
            state = ENQUEUEING;
        }
    }
    
    /**
     * This method is public as a side effect of the
     * implementation of this class. Do not call
     * this method from outside this class.
     * <p>
     * This method dequeues all Runnable objects from the
     * queue and executes them. The method returns
     * when the queue is empty.
     */
    public void run() {
        synchronized (queue) {
            if (state == STARTING) {
                state = RUNNING;
            } else {
                return;
            }
        }
        
        Object runner;
        loop: while (true) {
            synchronized(queue) {
                if (queue.isEmpty() || state != RUNNING) {
                    state = STOPPED;
                    queue.notifyAll();
                    break loop;
                }
                runner = queue.removeFirst();
            }
            
            try {
                ((Runnable) runner).run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public void join() throws InterruptedException {
         threadPool.join();
    }
}
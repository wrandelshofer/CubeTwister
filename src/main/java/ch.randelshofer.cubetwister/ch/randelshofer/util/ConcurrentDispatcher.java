/*
 * @(#)ConcurrentDispatcher.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;

import java.util.LinkedList;

/**
 * Processes Runnable objects concurrently on a pool of processor threads.
 * The order in which the runnable objects are processed is not
 * necesseraly the same in which they were added to the dispatcher.
 * There is one thread pool per instance.
 * <p>
 * Design pattern used: Acceptor
 * Role in design pattern: EventCollector and EventProcessor
 * <p>
 * <b>Example</b>
 * <br>The following program prints "Hello World" on the
 * processor thread:
 * <pre>
 * // Create the Dispatcher.
 * ConcurrentDispatcher dispatcher = new ConcurrentDispatcher();
 *
 * // Create the Runnable object.
 * Runnable runner = new Runnable() {
 *     public void run()  {
 *         System.out.println("Hello World");
 *     }
 * };
 *
 * // Execute the Runnable objekt using the dispatcher.
 * dispatcher.dispatch(runner);
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class ConcurrentDispatcher {

    /**
     * The priority of the processor thread.
     */
    private int priority;
    /**
     * The queue stores the events until they
     * can be processed by a processor thread.
     */
    private final LinkedList<Runnable> queue = new LinkedList<Runnable>();
    /**
     * Holds the worker threads.
     */
    private final LinkedList<Thread> threads = new LinkedList<Thread>();
    /**
     * Maximum number of concurrent threads.
     */
    private int maxThreadCount;
    /**
     * Set the policy to enqueue the runnable
     * for later execution if there are no available
     * threads in the pool.
     */
    public static int ENQUEUE_WHEN_BLOCKED = 0;
    /**
     * Set the policy for blocked execution to be that
     * the current thread executes the command if there
     * are no available threads in the pool.
     */
    public static int RUN_WHEN_BLOCKED = 1;
    /**
     * The policy used when the maximal number of
     * threads is reached.
     */
    private int blockingPolicy = ENQUEUE_WHEN_BLOCKED;

    /**
     * Creates a new ConcurrentDispatcher and
     * sets the priority of the processor thread to
     * java.lang.Thread.NORM_PRIORITY and with
     * up to five concurrent threads in the thread
     * pool.
     */
    public ConcurrentDispatcher() {
        this(Thread.NORM_PRIORITY, 5);
    }

    /**
     * Creates a new ConcurrentDispatcher.
     *
     * @param priority The priority of the processor
     * thread.
     * @param maxThreadCount The maximal number of concurrent
     * threads in the thread pool.
     */
    public ConcurrentDispatcher(int priority, int maxThreadCount) {
        this.priority = priority;
        this.maxThreadCount = maxThreadCount;
    }

    /**
     * Sets the maximum number of concurrent threads.
     * @param maxThreadCount Maximal number of concurrent threads.
     *   A value of zero or below zero stops the dispatcher
     *   when the queue is empty.
     */
    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    /**
     * Returns the maximal number of concurrent threads.
     */
    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    /**
     * Enqueues the Runnable object, and executes
     * it on a processor thread.
     */
    public void dispatch(@Nonnull Runnable runner) {
        synchronized (queue) {
            if (threads.size() < maxThreadCount) {
                queue.addLast(runner);

                Thread processor = new Thread(this + " Processor") {

                    public void run() {
                        processEvents();
                    }
                };
                threads.add(processor);

                // The processor thread must not be a daemon,
                // or else the Java VM might stop before
                // all runnables have been processed.
                try {
                    processor.setDaemon(false);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                try {
                    processor.setPriority(priority);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                processor.start();
                return;

            } else if (blockingPolicy == ENQUEUE_WHEN_BLOCKED) {
                queue.addLast(runner);

                return;
            }
        }

        //implicit: if (threadCount >= maxThreadCount && blockingPolicy == RUN_WHEN_BLOCKED)
        runner.run();
    }

    /**
     * This method dequeues all Runnable objects from the
     * queue and executes them. The method returns
     * when the queue is empty.
     */
    protected void processEvents() {
        Object runner;
        loop:
        while (true) {
            synchronized (queue) {
                if (queue.isEmpty()) {
                    threads.remove(Thread.currentThread());
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
        Thread t;
        do {
            synchronized (queue) {
                t = (threads.size() > 0) ? threads.get(0) : null;
            }
            if (t == Thread.currentThread()) {
                break;
            } else if (t != null) {
                t.join();
            }
        } while (t != null);
    }
}

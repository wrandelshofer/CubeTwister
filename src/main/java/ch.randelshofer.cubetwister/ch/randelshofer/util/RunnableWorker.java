/*
 * @(#)RunnableWorker.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nullable;

import javax.swing.SwingUtilities;

/**
 * This is an abstract class that you subclass to
 * perform GUI-related work in a dedicated event dispatcher.
 * <p>
 * This class is compatible with SwingWorker where it is reasonable
 * to be so. Unlike a SwingWorker it does not use an internal
 * worker thread but has to be dispatched by a dispatcher which
 * handles java.awt.ActiveEvent's.
 *
 * @author Werner Randelshofer
 */
public abstract class RunnableWorker<T> implements Runnable {
    private T value;  // see getValue(), setValue()

    /**
     * Calls #construct on the current thread and invokes
     * #finished on the AWT event dispatcher thread.
     */
    public void run() {
        final Runnable doFinished = new Runnable() {
            public void run() { finished(getValue()); }
        };
        try {
            setValue(construct());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(doFinished);
        }
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    @Nullable
    public abstract T construct();
    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished(T value) {
    }
    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     */
    protected synchronized T getValue() {
        return value;
    }
    /**
     * Set the value produced by worker thread
     */
    private synchronized void setValue(T x) {
        value = x;
    }
}
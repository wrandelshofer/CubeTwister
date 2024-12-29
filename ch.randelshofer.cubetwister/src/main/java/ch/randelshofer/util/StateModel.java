/*
 * @(#)StateModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

/**
 * Generic interface for Objects with state.
 *
 * @author Werner Randelshofer
 */
public interface StateModel {
    /**
     * Adds a listener that wants to be notified about
     * state changes of the model.
     */
    public void addStateListener(StateListener listener);

    /**
     * Removes a listener.
     */
    public void removeStateListener(StateListener listener);

    /**
     * Returns the current state of the model.
     */
    public int getState();
}

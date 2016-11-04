/*
 * @(#)StateModel.java  1.0  1999-10-19
 *
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.util;

import java.awt.Component;

/**
 * Generic interface for Objects with state.
 *
 * @author Werner Randelshofer
 * @version    1.0  1999-10-19
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

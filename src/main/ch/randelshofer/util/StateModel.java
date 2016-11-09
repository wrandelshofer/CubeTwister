/* @(#)StateModel.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.util;

import java.awt.Component;

/**
 * Generic interface for Objects with state.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

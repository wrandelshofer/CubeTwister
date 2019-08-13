/* @(#)StateListener.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import java.util.EventListener;
/**
 * Event for state changes.
 *
 * @author Werner Randelshofer
 */
public interface StateListener
extends EventListener {
    
    public void stateChanged(StateEvent event);
}

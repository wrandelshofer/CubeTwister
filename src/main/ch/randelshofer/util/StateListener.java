/*
 * @(#)StateListener.java  1.0  1999-10-19
 *
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.util;

import java.util.EventListener;
/**
 * Event for state changes.
 *
 * @author Werner Randelshofer
 * @version    1.0  1999-10-19
 */
public interface StateListener
extends EventListener {
    
    public void stateChanged(StateEvent event);
}

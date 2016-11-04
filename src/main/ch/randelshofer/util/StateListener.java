/*
 * @(#)StateListener.java  1.0  1999-10-19
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.util;

import java.util.EventListener;
/**
 * Event for state changes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StateListener
extends EventListener {
    
    public void stateChanged(StateEvent event);
}

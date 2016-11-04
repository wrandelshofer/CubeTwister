/*
 * @(#)SwipeListener.java  1.0  2008-12-31
 * 
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.gui.event;

import java.util.EventListener;

/**
 * SwipeListener.
 *
 * @author Werner Randelshofer
 * @version 1.0 2008-12-31 Created.
 */
public interface SwipeListener extends EventListener {
    public void faceSwiped(SwipeEvent evt);

}

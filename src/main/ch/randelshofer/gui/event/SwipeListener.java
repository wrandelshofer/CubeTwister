/*
 * @(#)SwipeListener.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.event;

import java.util.EventListener;

/**
 * SwipeListener.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface SwipeListener extends EventListener {
    public void faceSwiped(SwipeEvent evt);

}

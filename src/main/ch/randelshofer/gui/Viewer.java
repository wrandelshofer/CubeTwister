/*
 * @(#)Viewer.java 1.0  2001-10-05
 *
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;

/**
 * Defines the requirements for a viewer that displays an object. 
 *
 * @author  Werner Randelshofer
 * @version 1.0 2001-10-05
 */
public interface Viewer {
    /**
     * Sets the value of the viewer to value. 
     *
     * @param parent This is the component into which the viewer will be
     * embedded.
     * @param value This is the object to be displayed.
     */
    public Component getComponent(Component parent, Object value);
}


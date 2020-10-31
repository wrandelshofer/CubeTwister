/*
 * @(#)Viewer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import java.awt.Component;

/**
 * Defines the requirements for a viewer that displays an object.
 *
 * @author Werner Randelshofer
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


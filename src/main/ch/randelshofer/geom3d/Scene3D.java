/*
 * @(#)Scene3D.java  1.0  2008-09-16
 * 
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.geom3d;

/**
 * Represents a scene of a three dimensional universe.
 *
 * @author Werner Randelshofer
 * @version 1.0 2008-09-16 Created.
 */
public class Scene3D extends TransformNode3D {
    private boolean isAdjusting;
    
    public void setAdjusting(boolean newValue) {
        isAdjusting = newValue;
    }
    public boolean isAdjusting() {
        return isAdjusting;
    }
}

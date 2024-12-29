/*
 * @(#)Scene3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.geom3d;

/**
 * Represents a scene of a three dimensional universe.
 *
 * @author Werner Randelshofer
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

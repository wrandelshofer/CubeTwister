/*
 * @(#)CubeEvent.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube;

import org.jhotdraw.annotation.Nonnull;

/**
 * CubeEvent is used to notify interested parties that an event has occured
 * in a Cube object.
 *
 * @author Werner Randelshofer
 */
public class CubeEvent extends java.util.EventObject {
    private final static long serialVersionUID = 1L;
    private int axis;
    private int layerMask;
    private int angle;

    /** Creates a new instance. */
    public CubeEvent(@Nonnull Cube src, int axis, int layerMask, int angle) {
        super(src);
        this.axis = axis;
        this.layerMask = layerMask;
        this.angle = angle;
    }

    @Nonnull
    public Cube getCube() {
        return (Cube) getSource();
    }

    public int getAxis() {
        return axis;
    }

    public int getLayerMask() {
        return layerMask;
    }

    public int getAngle() {
        return angle;
    }

    /**
     * Returns a list of part ID's, for each part location which is affected
     * if a cube is transformed using the axis, layerMaska and angle
     * parameters of this event.
     */
    public int[] getAffectedLocations() {
        Cube c1 = (Cube) getCube().clone();
        c1.reset();
        c1.transform(axis, layerMask, angle);
        return c1.getUnsolvedParts();
    }
}

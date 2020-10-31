/*
 * @(#)CubeListener.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube;

/**
 * The listener interface for receiving cube events.
 *
 * @author  Werner Randelshofer
 */
public interface CubeListener extends java.util.EventListener {
    public void cubeTwisted(CubeEvent evt);
    public void cubeChanged(CubeEvent evt);
}

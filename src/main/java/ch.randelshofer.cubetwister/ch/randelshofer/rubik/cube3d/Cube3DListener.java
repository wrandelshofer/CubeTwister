/*
 * @(#)Cube3DListener.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.cube3d;

/**
 * The listener interface for Cube3D events.
 *
 * @author  Werner Randelshofer
 */
public interface Cube3DListener extends java.util.EventListener {
    /**
     * Invoked when an action on a part of the geometry occured.
     */
    public void actionPerformed(Cube3DEvent evt);

    /**
     * Invoked when the mouse entered a part.
     */
    public void mouseEntered(Cube3DEvent evt);
    /**
     * Invoked when the mouse exited a part.
     */
    public void mouseExited(Cube3DEvent evt);
    /**
     * Invoked when the mouse button is pressed.
     */
    public void mousePressed(Cube3DEvent evt);
    /**
     * Invoked when the mouse button is released.
     */
    public void mouseReleased(Cube3DEvent evt);
}

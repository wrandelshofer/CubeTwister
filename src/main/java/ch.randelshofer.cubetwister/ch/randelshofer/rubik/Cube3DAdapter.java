/* @(#)Cube3DAdapter.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import ch.randelshofer.rubik.cube3d.Cube3D;
import ch.randelshofer.rubik.cube3d.Cube3DEvent;
import ch.randelshofer.rubik.cube3d.Cube3DListener;

/**
 * Abstract adapter class for receiving {@link Cube3D} events.
 *
 * @author Werner Randelshofer
 */
public abstract class Cube3DAdapter implements Cube3DListener {
    public void mouseExited(Cube3DEvent evt) {
    }

    public void mouseEntered(Cube3DEvent evt) {
    }

    public void actionPerformed(Cube3DEvent evt) {
    }

    public void mouseReleased(Cube3DEvent evt) {
    }

    public void mousePressed(Cube3DEvent evt) {
    }
}

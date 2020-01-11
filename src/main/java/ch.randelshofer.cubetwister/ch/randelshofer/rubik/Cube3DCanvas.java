/* @(#)Cube3DCanvas.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

//import ch.randelshofer.geom3d.*;

import ch.randelshofer.rubik.cube3d.Cube3D;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

/**
 * The interface of objects which can display a Cube3D.
 *
 * @author  Werner Randelshofer
 */
public interface Cube3DCanvas {
    public Component getVisualComponent();
    public void reset();
    public void setCube3D(Cube3D newValue);
    public Cube3D getCube3D();
    public void setBackground(Color newValue);
    public void setBackgroundImage(Image newValue);
    public void setCamera(String newValue);
    public void setEnabled(boolean newValue);
    public boolean isEnabled();
    public void setLock(Object lock);
    /** Releases resources held by this object. */
    public void flush();
}

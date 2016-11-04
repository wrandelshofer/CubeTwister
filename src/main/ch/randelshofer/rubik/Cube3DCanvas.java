/*
 * @(#)Cube3DCanvas.java  1.2  2009-11-28
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.rubik;

//import ch.randelshofer.geom3d.*;
import java.awt.*;

/**
 * The interface of objects which can display a Cube3D.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.1 2008-01-02 Added method setLock.
 * <br>1.0  16 February 2005  Created.
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

/*
 * @(#)Cube3DImageProducer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import ch.randelshofer.rubik.cube3d.Cube3D;
import idx3d.idx3d_JCanvas;
import idx3d.idx3d_Scene;
import org.jhotdraw.annotation.Nullable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

/**
 * Cube3DImageProducer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 Apr 28, 2008 Created.
 */
public class Cube3DImageProducer implements Cube3DCanvas {
    private Cube3D cube3D;
    private idx3d_JCanvas idx3dCanvas;
    private Color background;

    public Cube3DImageProducer() {
        idx3dCanvas = new idx3d_JCanvas();
    }

    @Nullable
    public Component getVisualComponent() {
        return null;
    }

    public void reset() {
    }

    public void setCube3D(Cube3D newValue) {
        Cube3D oldValue = cube3D;
        if (cube3D != null) {
            cube3D.removeChangeListener(idx3dCanvas);
            idx3dCanvas.setScene(null);
        }
        cube3D = newValue;
        if (cube3D != null) {
                        idx3dCanvas.setScene((idx3d_Scene) cube3D.getScene());
                        cube3D.addChangeListener(idx3dCanvas);
                        idx3dCanvas.setLock(cube3D.getCube());
        }
     }

    public Cube3D getCube3D() {
        return cube3D;
    }

    public void setBackground(Color newValue) {
        Color oldValue = background;
        background = newValue;
    }

    public void setBackgroundImage(Image newValue) {
    }

    public void setCamera(String newValue) {
    }

    public void setEnabled(boolean newValue) {
    }

    public boolean isEnabled() {
        return true;
    }

    public void setLock(Object lock) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void flush() {
        idx3dCanvas.flush();
    }

}

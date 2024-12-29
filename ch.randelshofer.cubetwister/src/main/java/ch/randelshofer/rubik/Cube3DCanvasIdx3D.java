/*
 * @(#)Cube3DCanvasIdx3D.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import ch.randelshofer.beans.AbstractBean;
import ch.randelshofer.rubik.cube3d.Cube3D;
import idx3d.idx3d_JCanvas;
import idx3d.idx3d_RenderPipeline;
import idx3d.idx3d_Scene;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

/**
 * A Cube3DCanvas which can display a Cube3D which is built using the Idx3D
 * rendering engine.
 *
 * @author Werner Randelshofer
 */
public class Cube3DCanvasIdx3D
        extends AbstractBean
        implements Cube3DCanvas {
    private final static long serialVersionUID = 1L;

    private idx3d_JCanvas canvas3D;
    private Cube3D cube3D;
    private float alpha, beta, gamma;

    /**
     * Creates a new instance.
     */
    public Cube3DCanvasIdx3D() {
        this(new idx3d_JCanvas());
    }

    /**
     * Creates a new instance.
     */
    public Cube3DCanvasIdx3D(@Nonnull idx3d_JCanvas canvas3D) {
        this(canvas3D, null);
    }

    public Cube3DCanvasIdx3D(@Nonnull idx3d_JCanvas canvas3D, Cube3D cube3D) {
        this.canvas3D = canvas3D;
        canvas3D.setSwipeTimeout(Integer.MAX_VALUE);
        canvas3D.setInteractionMode(idx3d_JCanvas.Interaction.ROTATE_AND_SWIPE);
        setCube3D(cube3D);
    }

    public void setSharedRenderPipeline(idx3d_RenderPipeline sharedPipeline) {
        canvas3D.setSharedRenderPipeline(sharedPipeline);
    }

    @Override
    public Component getVisualComponent() {
        return canvas3D;
    }

    public void setInitialOrientation(float alpha, float beta, float gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    @Override
    public void reset() {
        idx3d_Scene scene = canvas3D.getScene();
        scene.matrix.reset();
        scene.normalmatrix.reset();
        scene.rotate(0f, beta, 0f);
        scene.rotate(-alpha, 0f, 0f);
        //scene.rotate(0f, 0f, gamma);
    }

    @Override
    public void setBackground(Color color) {
    }

    @Override
    public void setBackgroundImage(Image newValue) {
    }

    public void setAmbientLightIntensity(double intensity) {
    }

    public void setLightSourceIntensity(double intensity) {
    }
    /*
    public void setLightSource(Point3D p) {
    }*/

    @Override
    public Cube3D getCube3D() {
        return cube3D;

    }

    @Override
    public void setCube3D(Cube3D newValue) {
        Cube3D oldValue = cube3D;

        if (cube3D != null) {
            cube3D.removeChangeListener(canvas3D);
        }

        cube3D = newValue;

        if (cube3D != null) {
            cube3D.addChangeListener(canvas3D);
        canvas3D.setScene((idx3d_Scene) cube3D.getScene());
        canvas3D.setLock(cube3D.getLock());
        canvas3D.setCamera("Front");
        }

        firePropertyChange("cube3D", oldValue, newValue);
    }

    @Override
    public void setCamera(String cameraName) {
    }

    @Override
    public void setEnabled(boolean newValue) {
        canvas3D.setEnabled(newValue);
    }

    @Override
    public boolean isEnabled() {
        return canvas3D.isEnabled();
    }

    @Override
    public void setLock(Object lock) {
        canvas3D.setLock(lock);
    }

    @Override
    public void flush() {
        canvas3D.flush();
    }

}

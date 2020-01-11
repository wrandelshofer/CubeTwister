/* @(#)Cube3DCanvasFlat3D.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import ch.randelshofer.beans.AbstractBean;
import ch.randelshofer.geom3d.JCanvas3D;
import ch.randelshofer.geom3d.Point3D;
import ch.randelshofer.geom3d.Scene3D;
import ch.randelshofer.geom3d.Transform3D;
import ch.randelshofer.rubik.cube3d.Cube3D;
import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

/**
 * A Cube3DCanvas which can display a Cube3D which is built using the Geom3D 
 * rendering engine.
 * 
 * @author Werner Randelshofer
 */
public class Cube3DCanvasGeom3D
        extends AbstractBean
        implements Cube3DCanvas {
    private final static long serialVersionUID = 1L;
    private JCanvas3D canvas3D;
    private Cube3D cube3D;
    private Transform3D transform;

    /**
     * Creates a new instance.
     */
    public Cube3DCanvasGeom3D() {
        this(new JCanvas3D());
    }

    /**
     * Creates a new instance.
     */
    public Cube3DCanvasGeom3D(@Nonnull JCanvas3D canvas3D) {
        this(canvas3D, null);
    }

    public Cube3DCanvasGeom3D(@Nonnull JCanvas3D canvas3D, Cube3D cube3D) {
        this.canvas3D = canvas3D;
        canvas3D.setSwipeDelay(Integer.MAX_VALUE);
        transform = new Transform3D();
        //transform.rotateY(45 / 180f * (float) Math.PI);
        //transform.rotateX(-25 / 180f * (float) Math.PI);
        canvas3D.setScaleFactor(0.02);
        canvas3D.setInteractionMode(JCanvas3D.Interaction.ROTATE_AND_SWIPE);
        setCube3D(cube3D);
    }
    
    @Override
    public Component getVisualComponent() {
        return canvas3D;
    }
    
    public void setInitialOrientation(float alpha, float beta, float gamma) {
        transform = new Transform3D();
        transform.rotateY(beta);
        transform.rotateX(alpha);
        canvas3D.setTransform(transform);
    }
    
    @Override
    public void reset() {
        canvas3D.setTransform(transform);
    }
    
    @Override
    public void setBackground(Color color) {
        canvas3D.setBackground(color);
    }
    
    @Override
    public void setBackgroundImage(Image image) {
        canvas3D.setBackgroundImage(image);
    }
    
    public void setAmbientLightIntensity(double intensity) {
        canvas3D.setAmbientLightIntensity(intensity);
    }
    
    public void setLightSourceIntensity(double intensity) {
        canvas3D.setLightSourceIntensity(intensity);
    }
    
    public void setLightSource(Point3D p) {
        canvas3D.setLightSource(p);
    }
    
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
            canvas3D.setScene((Scene3D) cube3D.getScene());
            canvas3D.setLock(cube3D.getLock());
            cube3D.addChangeListener(canvas3D);
        }
        
        firePropertyChange("cube3D", oldValue, newValue);
    }
    
    @Override
    public void setCamera(String cameraName) {
        if ("Rear".equals(cameraName)) {
        canvas3D.setTransform(new Transform3D(0,Math.PI,Math.PI));
        } else {
        canvas3D.setTransform(new Transform3D());
        }
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
    
    public JCanvas3D getCanvas3D() {
            return canvas3D;
            }

    @Override
    public void flush() {
        canvas3D.flush();
    }
}

/* @(#)AbstractCubeGeom3D.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik;

import ch.randelshofer.geom3d.Scene3D;
import ch.randelshofer.geom3d.Shape3D;
import ch.randelshofer.geom3d.Transform3D;
import ch.randelshofer.geom3d.TransformNode3D;

import java.awt.Color;
/**
 * Abstract base class for the geometrical representation of a {@link Cube}
 * using the Geom3D engine.
 * 
 * @author Werner Randelshofer
 */
public abstract class AbstractCubeGeom3D extends AbstractCube3D {
    protected int cornerCount, edgeCount, sideCount, centerCount;
    protected int partCount;
    protected int cornerOffset, edgeOffset, sideOffset, centerOffset;

    protected final static double HALF_PI = (Math.PI / 2d);
    protected final double PI = Math.PI;
    
    protected Shape3D[] shapes;
    protected Transform3D[] identityTransforms;
    protected TransformNode3D[] transforms;
    protected Scene3D scene;
    protected boolean isInStartedPlayer;
    
    /**
     * Creates a new instance.
     */
    public AbstractCubeGeom3D(int layerCount, int cornerCount, int edgeCount, int sideCount, int centerCount) {
        super(layerCount);
        this.cornerCount = cornerCount;
        this.edgeCount = edgeCount;
        this.sideCount = sideCount;
        this.centerCount = centerCount;
        this.partCount = cornerCount + edgeCount + sideCount + centerCount;
        
        this.cornerOffset = 0;
        this.edgeOffset = cornerCount;
        this.sideOffset = edgeOffset + edgeCount;
        this.centerOffset = sideOffset + sideCount;
    }
    
    final protected void init() {
        shapes = new Shape3D[partCount];
        identityTransforms = new Transform3D[partCount];
        transforms = new TransformNode3D[partCount];
        
        initCorners();
        initEdges();
        initSides();
        initCenter();
        setAttributes(createAttributes());
        initTransforms();
        initActions();
        setCube(createCube());
    }

    protected abstract Cube createCube();
    protected abstract void initCorners();
    protected abstract void initEdges();
    protected abstract void initSides();
    protected abstract void initCenter();
    protected abstract void initTransforms();
    protected abstract void initActions();
    

    @Override
    public Object getScene() {
        return scene;
    }
    
    protected Shape3D getPart(int partIndex) {
        return shapes[partIndex];
    }
    @Override
    public int getPartCount() {
        return partCount;
    }
    
    @Override
    public void updateCube() {
        computeTransformation();
        // Turn reduction on for all parts
        for (int i=0; i < partCount; i++) {
            shapes[i].setReduced(true);
        }
        
        fireStateChanged();
    }
    protected abstract void computeTransformation();
    
    @Override
    protected void updateExplosionFactor(float factor) {
        // FIXME - Implement me
    }
    @Override
    protected void updateScaleFactor(float factor) {
        // FIXME - Implement me
    }
    
    
    @Override
    final public void updateAttributes() {
        updatePartsVisibility();
        updatePartsFillColor();
        updatePartsOutlineColor();
        updateStickersFillColor();
        fireStateChanged();
    }
    
    /** Updates the fill color of the parts.
     */
    abstract protected void updatePartsFillColor();
    
    /** Updates the outline color of the parts.
     */
    abstract protected void updatePartsOutlineColor();
    /**
     * Updates the fill color of the stickers.
     */
   final public void updateStickersFillColor() {
        for (int i=0, n = getStickerCount(); i < n; i++) {
            int partIndex = getPartIndexForStickerIndex(i);
            int faceIndex = getPartFaceIndexForStickerIndex(i);
            Shape3D part = getPart(partIndex);
            CubeAttributes attr = getAttributes();
            Color color;
            if (attr.getPartFillColor(partIndex) == null) {
                color = null;
            } else if (attr.isStickerVisible(i)) {
                color = attr.getStickerFillColor(i);
            } else {
                color = attr.getPartFillColor(partIndex);
            }
            
            part.setFillColor(faceIndex, color);
        }
    }
    /**
     * Updates the visible state of the parts.
     */
    final protected void updatePartsVisibility() {
        for (int i = 0; i < partCount; i++) {
            shapes[i].setVisible(getAttributes().isPartVisible(i));
        }
    }
    /**
     * Updates the fill color of a part.
     * The part Index is interpreted according to the scheme
     * used by method getPart(int);
     */
    @Override
    final protected void updatePartVisibility(int index, float alpha) {
        // XXX implement this method
    }
    @Override
    final protected void updateStickerVisibility(int index, float alpha) {
        // XXX Implement this method
    }
    @Override
    final protected void updateStickersImage() {
        // XXX implement this method
    }
    
    /*final public void cubeTwisted(final CubeEvent evt) {
        if (isAnimated) {
            if (GUIUtilities.isEventDispatchThread()) {
                // If we are running on the AWT event dispatcher
                // thread, we must perform the animation on a
                // separate worker thread.
                getDispatcher().dispatch(
                new Runnable() {
                    public void run() { animateTwist(evt); }
                }
                );
            } else {
                // If we are not running on the AWT event dispatcher
                // thread, we perform the animation on the current
                // thread.
                animateTwist(evt);
            }
        } else {
            updateCube();
        }
    }*/
    
    //protected abstract void animateTwist(CubeEvent evt);

    public final static int TWIST_MODE = 0;
    public final static int PARTS_MODE = 1;
    protected int mode = TWIST_MODE;
    final public void setMode(int mode) {
        Shape3D shape;
        int i;
        
        this.mode = mode;
        switch (mode) {
            case TWIST_MODE :
                for (i=0; i < partCount; i++) {
                    shape = shapes[i];
                    if (shape.isWireframe()) {
                        shape.setWireframe(false);
                        shape.setVisible(false);
                    }
                }
                break;
            case PARTS_MODE :
                for (i=0; i < partCount; i++) {
                    shape = shapes[i];
                    if (! shape.isVisible()) {
                        shape.setWireframe(true);
                        shape.setVisible(true);
                    }
                }
                break;
        }
        fireStateChanged();
    }
    
    @Override
    protected void updateAlphaBeta() {
        computeTransformation();
    }
    
    protected void validateTwist(int[] partIndices, int[] locations, int[] orientations, int length, int axis, int angle, float alpha) {
        synchronized (getCube()) {
            computeTransformation();
            Transform3D rotation = new Transform3D();
            float rad = (float) (HALF_PI*angle*(1f-alpha));
            switch (axis) {
                case 0 :
                    rotation.rotate(-rad, 0, 0);
                    break;
                case 1 :
                    rotation.rotate(0, -rad, 0);
                    break;
                case 2 :
                    rotation.rotate(0, 0, -rad);
                    break;
            }
            for (int i=0; i < length; i++) {
                TransformNode3D tn = transforms[partIndices[i]];
                tn.getTransform().concatenate(rotation);
            }
        }
        fireStateChanged();
    }
    @Override
    public void setStickerBeveling(float newValue) {
        // Do nothing
    }

    @Override
    public void setInStartedPlayer(boolean newValue) {
        isInStartedPlayer=newValue;
        if (!newValue) {
            if (scene!=null) {
                scene.setAdjusting(false);
            }
            fireStateChanged();
        }
    }

    @Override
    public boolean isInStartedPlayer() {
        return isInStartedPlayer;
    }

}

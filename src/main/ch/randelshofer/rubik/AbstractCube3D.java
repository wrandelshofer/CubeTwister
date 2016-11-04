/*
 * @(#)AbstractCube3D.java  8.1.1  2009-01-09
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik;

import ch.randelshofer.gui.event.SwipeEvent;
import ch.randelshofer.gui.event.SwipeListener;
import org.monte.media.*;
import java.beans.*;
import java.awt.event.*;
import ch.randelshofer.util.*;
import javax.swing.event.*;

/**
 * Abstract base class for classes which implement the {@link Cube3D}
 * interface.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>8.1 2008-04-28 Added method setAnimator.
 * <br>8.0 2007-11-15 Upgraded to Java 1.4. 
 * <br>7.1 2007-09-10 Use SplineInterpolator instead of linear Interpolator.
 * <br>7.0 2007-08-28 PartAction is now also a MouseListener.
 * <br>6.0 2005-03-06 Reworked.
 * <br>1.0 December 21, 2003 Created.
 */
public abstract class AbstractCube3D implements Cube3D, CubeListener, PropertyChangeListener,
        ChangeListener {

    protected EventListenerList listenerList = new EventListenerList();
    protected ChangeEvent changeEvent;
    private Cube cube;
    protected CubeAttributes attributes;
    protected boolean isAnimated;
    private Animator animator;
    private boolean isShowGhostParts;
    private Object lock = new Object();
    protected Dispatcher dispatcher;
    protected int layerCount;

    protected class SwipeAction implements SwipeListener {

        private int partIndex, orientation, stickerIndex;
        private float angle;

        public SwipeAction(int partIndex, int orientation, int stickerIndex, float angle) {
            this.partIndex = partIndex;
            this.orientation = orientation;
            this.stickerIndex = stickerIndex;
            this.angle = angle;
        }

        @Override
        public void faceSwiped(SwipeEvent evt) {
//System.out.println("AbstractCube3D.faceSwiped evt:"+SwipeEvent.getModifiersExText(evt.getModifiersEx())+" @"+evt.hashCode()+" t:"+evt.getWhen());
            int fa = (int) ((evt.getAngle() + angle) / Math.PI * 180) % 360;
            if (fa < 0) {
                fa += 360;
            }


            int direction = (fa / 90) % 4;
//System.out.println("@"+hashCode()+" FaceSwiped ori:"+orientation+" evtAngle:"+(int)(evt.getAngle()/Math.PI*180)+" corr:"+(int)(angle/Math.PI*180)+" corrAngle:"+fa+" direction:"+direction);
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);

            fireActionPerformed(
                    new Cube3DEvent(
                    AbstractCube3D.this,
                    partIndex, orientation, sideIndex, stickerIndex, direction, evt));
        }

        @Override
        public String toString() {
            return getClass().getName() + "@" + System.identityHashCode(this) + " [partIndex=" + partIndex + ",orientation=" + orientation + ",stickerIndex=" + stickerIndex + "]";
        }
    }

    /**
     * This action is used to forward user actions on the cube parts to
     * Cube3DListeners.
     */
    protected class PartAction
            implements ActionListener, MouseListener {

        private int partIndex, orientation, stickerIndex;

        public PartAction(int partIndex, int orientation, int stickerIndex) {
            this.partIndex = partIndex;
            this.orientation = orientation;
            this.stickerIndex = stickerIndex;
        }

        /**
         * Invoked when an action occurs.
         * FIXME - The passed in ActionEvent must return modifiersExt when
         * getModifiers() is called!
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);
            // System.out.println("PartAction part="+partIndex+" sticker="+stickerIndex+" orientation:"+orientation+" side:"+sideIndex);
            fireActionPerformed(
                    new Cube3DEvent(
                    AbstractCube3D.this,
                    // Fixme - When we get here, ActionEvent.getModifiers actually returns
                    // modifiersEx!!!
                    partIndex, orientation, sideIndex, stickerIndex, evt.getModifiers()));
        }

        @Override
        public String toString() {
            return getClass().getName() + "@" + System.identityHashCode(this) + " [partIndex=" + partIndex + ",orientation=" + orientation + ",stickerIndex=" + stickerIndex + "]";
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);
            fireActionPerformed(new Cube3DEvent(
                    AbstractCube3D.this,
                    partIndex, orientation, sideIndex, stickerIndex,
                    evt));
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);
            fireMousePressed(new Cube3DEvent(
                    AbstractCube3D.this,
                    partIndex, orientation, sideIndex, stickerIndex, evt));
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);
            fireMouseReleased(new Cube3DEvent(
                    AbstractCube3D.this,
                    partIndex, orientation, sideIndex, stickerIndex, evt));
        }

        @Override
        public void mouseEntered(MouseEvent evt) {
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);
            fireMouseEntered(new Cube3DEvent(
                    AbstractCube3D.this,
                    partIndex, orientation, sideIndex, stickerIndex, evt));
        }

        @Override
        public void mouseExited(MouseEvent evt) {
            int sideIndex = (orientation == -1) ? -1 : AbstractCube3D.this.cube.getPartFace(partIndex, orientation);
            fireMouseExited(new Cube3DEvent(
                    AbstractCube3D.this,
                    partIndex, orientation, sideIndex, stickerIndex, evt));
        }
    }

    /** Creates a new instance. */
    public AbstractCube3D(int layerCount) {
        this.layerCount = layerCount;
    }

    /**
     * Returns the 3D scene of the cube.
     * We return an Object type here, because the type of the 3D scene depends
     * on the 3D engine used.
     */
    @Override
    public abstract Object getScene();

    /**
     * Returns the lock object used for synchronizing model and view changes.
     */
    @Override
    public Object getLock() {
        return lock;
    }

    /**
     * Sets the lock object used for synchronizing model and view changes.
     */
    @Override
    public void setLock(Object o) {
        lock = o;
    }

    /**
     * Sets the dispatcher used to process animations of the 3D geometry.
     */
    @Override
    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Gets the dispatcher used to process animations of the 3D geometry.
     */
    @Override
    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            dispatcher = new PooledSequentialDispatcher();
        }
        return dispatcher;
    }

    /**
     * Updates the sticker imagery.
     */
    protected abstract void updateStickersImage();

    /**
     * Sets whether changes in the permutation model shall cause an animated
     * (multi-frame) change in the 3D geometry or whether they shall be
     * reflected immediately.
     */
    @Override
    public void setAnimated(boolean b) {
        if (b == isAnimated) {
            return;
        }

        isAnimated = b;
        if (b) {
            if (animator == null) {
                setAnimator(new DefaultAnimator());
            }
        } else {
            if (animator != null) {
                animator.stop();
            }
        }
    }

    @Override
    public void setAnimator(Animator newValue) {
        Animator oldValue = animator;
        if (animator != null) {
            animator.removeChangeListener(this);
            animator.setLock(new Object());
        }
        animator = newValue;
        if (animator != null) {
            animator.setLock(getLock());
            animator.addChangeListener(this);
        }
    }

    @Override
    public Animator getAnimator() {
        return animator;
    }

    /**
     * Returns true when the 3D geometry animates permutation changes.
     */
    @Override
    public boolean isAnimated() {
        return isAnimated;
    }

    protected boolean isAdjusting() {
        return attributes.getValueIsAdjusting();
    }

    /**
     * Stops all currently running animations.
     */
    @Override
    public void stopAnimation() {
        if (animator != null) {
            animator.stop();
        }
    }

    /**
     * Sets the underlying permutation model.
     */
    @Override
    public void setCube(Cube cube) {
        if (cube != null && cube.getLayerCount() != layerCount) {
            throw new IllegalArgumentException("cube has " + cube.getLayerCount() + " layers, but should have " + layerCount + " layers.");
        }

        if (isAnimated()) {
            stopAnimation();
        }

        if (this.cube != null) {
            this.cube.removeCubeListener(this);
        }

        this.cube = cube;

        if (this.cube != null) {
            this.cube.addCubeListener(this);
            updateCube();
        }
    }

    /**
     * Gets the underlying permutation model.
     */
    @Override
    public Cube getCube() {
        return cube;
    }

    /**
     * Creates cube attributes (e.g. colors, sticker pictures, ...).
     */
    protected abstract CubeAttributes createAttributes();

    /**
     * Sets cube attributees.
     */
    @Override
    public void setAttributes(CubeAttributes attributes) {
        if (isAnimated()) {
            stopAnimation();
        }

        if (this.attributes != null) {
            this.attributes.removePropertyChangeListener(this);
        }

        this.attributes = attributes;

        if (this.attributes != null) {
            this.attributes.addPropertyChangeListener(this);
            updateAttributes();
        }
    }

    /**
     * Gets cube attributees.
     */
    @Override
    public CubeAttributes getAttributes() {
        return attributes;
    }

    protected abstract int getStickerCount();

    @Override
    public abstract int getPartIndexForStickerIndex(int stickerIndex);

    protected abstract int getPartFaceIndexForStickerIndex(int stickerIndex);

    /**
     * Adds a change listener. The change listener is notified about geometry
     * changes. This is useful for a 3D canvas interested to know when to repaint.
     */
    @Override
    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    /**
     * Removes a change listener.
     */
    @Override
    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * Updates the fill color of a part.
     * The part Index is interpreted according to the scheme
     * used by method getPart(int);
     */
    protected abstract void updatePartVisibility(int index, float alpha);

    protected abstract void updateStickerVisibility(int index, float alpha);

    protected abstract void updateExplosionFactor(float factor);

    protected abstract void updateScaleFactor(float factor);

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Suppress property change events, if we have no attributes
        if (attributes == null) {
            return;
        }

        String name = evt.getPropertyName();

        int index = -1;
        int pos = name.indexOf('.');
        if (pos != -1) {
            index = Integer.decode(name.substring(pos + 1)).intValue();
            name = name.substring(0, pos);
        }
        if (name.equals(CubeAttributes.PART_VISIBLE_PROPERTY) && index != -1) {
            if (isAnimated) {
                final int partIndex = index;
                boolean reverse = ((Boolean) evt.getOldValue()).booleanValue();
                Interpolator interpolator = new SplineInterpolator(0.25f, 0f, 0.75f, 1f, reverse, 1000) {

                    @Override
                    protected void update(float value) {
                        updatePartVisibility(partIndex, value);
                        fireStateChanged();
                    }

                    protected void destroy() {
                    }
                };
                dispatch(interpolator);
            } else {
                updateAttributes();
                fireStateChanged();
            }
        } else if (name.equals(CubeAttributes.STICKER_VISIBLE_PROPERTY) && index != -1) {
            if (isAnimated) {
                final int stickerIndex = index;
                boolean reverse = ((Boolean) evt.getOldValue()).booleanValue();
                Interpolator interpolator = new SplineInterpolator(0.25f, 0f, 0.75f, 1f, reverse, 1000) {

                    @Override
                    protected void update(float value) {
                        updateStickerVisibility(stickerIndex, value);
                        fireStateChanged();
                    }

                    protected void destroy() {
                    }
                };
                dispatch(interpolator);
            } else {
                updateAttributes();
                fireStateChanged();
            }
        } else if (name.equals(CubeAttributes.STICKERS_IMAGE_PROPERTY)) {
            updateStickersImage();
            fireStateChanged();
        } else if (name.equals(CubeAttributes.STICKERS_IMAGE_VISIBLE_PROPERTY)) {
            updateStickersImage();
            fireStateChanged();
        } else if (name.equals(CubeAttributes.STICKERS_IMAGE_VISIBLE_PROPERTY)) {
            updateAttributes();
            fireStateChanged();
        } else if (name.equals(CubeAttributes.ALPHA_PROPERTY) ||
                name.equals(CubeAttributes.BETA_PROPERTY)) {
            updateAlphaBeta();
            fireStateChanged();
        } else if (name.equals(CubeAttributes.PART_EXPLOSION_PROPERTY)) {
            updateExplosionFactor(getAttributes().getExplosionFactor());
            fireStateChanged();
        } else if (name.equals(CubeAttributes.EXPLOSION_FACTOR_PROPERTY)) {
            if (isAnimated) {
                float oldValue = ((Float) evt.getOldValue()).floatValue();
                float newValue = ((Float) evt.getNewValue()).floatValue();
                Interpolator interpolator = new Interpolator(
                        oldValue, newValue, (long) (Math.abs(oldValue - newValue) * 1000)) {

                    @Override
                    protected void update(float value) {
                        updateExplosionFactor(value);
                        fireStateChanged();
                    }

                    @Override
                    public boolean replaces(Interpolator that) {
                        return (that.getClass() == this.getClass());
                    }

                    protected void destroy() {
                    }
                };
                dispatch(interpolator);
            } else {
                updateExplosionFactor(getAttributes().getExplosionFactor());
                fireStateChanged();
            }
        } else if (name.equals(CubeAttributes.SCALE_FACTOR_PROPERTY)) {
            if (isAnimated) {
                float oldValue = ((Float) evt.getOldValue()).floatValue();
                float newValue = ((Float) evt.getNewValue()).floatValue();
                Interpolator interpolator = new Interpolator(
                        oldValue, newValue, (long) (Math.abs(oldValue - newValue) * 1000)) {

                    @Override
                    protected void update(float value) {
                        updateScaleFactor(value);
                        fireStateChanged();
                    }

                    @Override
                    public boolean replaces(Interpolator that) {
                        return (that.getClass() == this.getClass());
                    }

                    protected void destroy() {
                    }
                };
                dispatch(interpolator);
            } else {
                updateScaleFactor(getAttributes().getScaleFactor());
                fireStateChanged();
            }
        } else {
            updateAttributes();
            fireStateChanged();
        }
    }

    @Override
    public void cubeChanged(CubeEvent evt) {
        updateCube();
        //fireStateChanged();
    }

    @Override
    public void cubeTwisted(CubeEvent evt) {
        updateCube();
        //fireStateChanged();
    }

    public void dispatch(Interpolator interpolator) {
        if (animator!=null) {
        animator.dispatch(interpolator);
        }
    }

    protected abstract void updateAttributes();

    protected abstract void updateCube();

    protected abstract void updateAlphaBeta();

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireActionPerformed(Cube3DEvent event) {
        //System.out.println("AbstractCube3D.fireActionPerformed "+event);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == Cube3DListener.class) {
                ((Cube3DListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireMouseEntered(Cube3DEvent event) {
        //System.out.println("AbstractCube3D.fireActionPerformed "+event);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == Cube3DListener.class) {
                ((Cube3DListener) listeners[i + 1]).mouseEntered(event);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireMouseExited(Cube3DEvent event) {
        //System.out.println("AbstractCube3D.fireActionPerformed "+event);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == Cube3DListener.class) {
                ((Cube3DListener) listeners[i + 1]).mouseExited(event);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireMousePressed(Cube3DEvent event) {
        //System.out.println("AbstractCube3D.fireActionPerformed "+event);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == Cube3DListener.class) {
                ((Cube3DListener) listeners[i + 1]).mousePressed(event);
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     */
    protected void fireMouseReleased(Cube3DEvent event) {
        //System.out.println("AbstractCube3D.fireActionPerformed "+event);

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == Cube3DListener.class) {
                ((Cube3DListener) listeners[i + 1]).mouseReleased(event);
            }
        }
    }

    @Override
    public void addCube3DListener(Cube3DListener listener) {
        listenerList.add(Cube3DListener.class, listener);
    }

    @Override
    public void removeCube3DListener(Cube3DListener listener) {
        listenerList.remove(Cube3DListener.class, listener);
    }

    @Override
    public boolean isAnimating() {
        return animator != null && animator.isActive();
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        fireStateChanged();
    }

    @Override
    public boolean isShowGhostParts() {
        return isShowGhostParts;
    }

    @Override
    public void setShowGhostParts(boolean b) {
        if (b == isShowGhostParts) {
            return;
        }

        isShowGhostParts = b;
        if (isAnimated()) {
            float min = b ? 0f : 0.1f;
            float max = b ? 0.1f : 0f;

            for (int i = 0; i < attributes.getPartCount(); i++) {
                if (!attributes.isPartVisible(i)) {
                    final int partIndex = i;
                    Interpolator interpolator = new Interpolator(min, max) {

                        @Override
                        protected void update(float value) {
                            updatePartVisibility(partIndex, value);
                            fireStateChanged();
                        }

                        protected void destroy() {
                        }
                    };
                    dispatch(interpolator);
                }
            }
        } else {
            updateAttributes();
            fireStateChanged();
        }
    }

    @Override
    public void dispose() {
        setCube(null);
        setAttributes(null);
        animator = null;
    }
    /*
    public Object clone() {
    Object that = super.clone();
    return that;
    }*/
}

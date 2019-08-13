/* @(#)DefaultCubeAttributes.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik;

//import ch.randelshofer.gui.event.*;

import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

/**
 * A default implementation of the {@link CubeAttributes} interface.
 *
 * @author  Werner Randelshofer
 */
public class DefaultCubeAttributes implements CubeAttributes {

    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    /**
     * Is true, when the part is visible.
     */
    protected boolean[] partsVisible;
    /**
     * Holds the fill color of the parts.
     */
    protected Color[] partsFillColor;
    /**
     * Holds the outline color of the parts.
     */
    protected Color[] partsOutlineColor;
    /**
     * Is true, when the sticker is visible.
     */
    protected boolean[] stickersVisible;
    /**
     * Holds the fill color of the stickers.
     * This is used only, when stickersImage is null.
     */
    protected Color[] stickersFillColor;
    /**
     * Holds the outline color of the stickers.
     */
    protected Color[] stickersOutlineColor;
    private float explosionFactor = 0f;
    protected float scaleFactor = 1f;
    protected float alpha = (float) (-25 / 180d * Math.PI);
    protected float beta = (float) (45  / 180d * Math.PI);
    protected Image stickersImage;
    protected int[] stickerCountPerFace;
    protected Color frontBgColor;
    protected Image frontBgImage;
    protected Color rearBgColor;
    protected Image rearBgImage;
    protected boolean rearBgImageVisible;
    protected boolean frontBgImageVisible;
    protected boolean stickersImageVisible;
    protected boolean valueIsAdjusting;
    protected float[] partExplosion;
    protected float[] stickerExplosion;
    private int twistDuration = 400;

    /** Creates a new instance. */
    public DefaultCubeAttributes(int partCount, int stickerCount, int[] stickerCountPerFace) {
        partsVisible = new boolean[partCount];
        partsFillColor = new Color[partCount];
        partsOutlineColor = new Color[partCount];
        stickersVisible = new boolean[stickerCount];
        stickersFillColor = new Color[stickerCount];
        stickersOutlineColor = new Color[stickerCount];
        this.stickerCountPerFace = stickerCountPerFace;
        partExplosion = new float[partCount];
        stickerExplosion = new float[stickerCount];

        Arrays.fill(partsVisible, true);
        Arrays.fill(stickersVisible, true);
    }

    /** Adds a property change listener.*/
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /** Removes a property change listener.*/
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public float getBeta() {
        return beta;
    }

    @Override
    public float getExplosionFactor() {
        return explosionFactor;
    }

    @Override
    public float getPartExplosion(int index) {
        return partExplosion[index];
    }

    public void setPartExplosion(int index, float newValue) {
        float oldValue = partExplosion[index];
        partExplosion[index] = newValue;
        changeSupport.firePropertyChange(
                PART_EXPLOSION_PROPERTY,
                new Float(oldValue),
                new Float(newValue));
    }

    @Override
    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setStickersImage(Image newValue) {
        Image oldValue = stickersImage;
        stickersImage = newValue;
        changeSupport.firePropertyChange(STICKERS_IMAGE_PROPERTY, oldValue, newValue);
    }

    @Override
    public Image getStickersImage() {
        return stickersImage;
    }

    public void setAlpha(float newValue) {
        float oldValue = alpha;
        alpha = newValue;
        changeSupport.firePropertyChange(ALPHA_PROPERTY, new Float(oldValue), new Float(newValue));
    }

    public void setBeta(float newValue) {
        float oldValue = beta;
        beta = newValue;
        changeSupport.firePropertyChange(BETA_PROPERTY, new Float(oldValue), new Float(newValue));
    }

    public void setScaleFactor(float newValue) {
        float oldValue = scaleFactor;
        scaleFactor = newValue;
        changeSupport.firePropertyChange(SCALE_FACTOR_PROPERTY, new Float(oldValue), new Float(newValue));
    }

    public void setExplosionFactor(float newValue) {
        float oldValue = explosionFactor;
        explosionFactor = newValue;
        changeSupport.firePropertyChange(EXPLOSION_FACTOR_PROPERTY, new Float(oldValue), new Float(newValue));
    }

    public void setPartVisible(int index, boolean newValue) {
        boolean oldValue = partsVisible[index];
        partsVisible[index] = newValue;
        changeSupport.firePropertyChange(PART_VISIBLE_PROPERTY + "." + index, new Boolean(oldValue), new Boolean(newValue));
    }

    @Override
    public boolean isPartVisible(int index) {
        return partsVisible[index];
    }

    public void setStickerVisible(int index, boolean newValue) {
        boolean oldValue = stickersVisible[index];
        stickersVisible[index] = newValue;
        changeSupport.firePropertyChange(STICKER_VISIBLE_PROPERTY + "." + index, new Boolean(oldValue), new Boolean(newValue));
    }

    @Override
    public boolean isStickerVisible(int index) {
        return stickersVisible[index];
    }

    @Override
    public Color getPartFillColor(int index) {
        return partsFillColor[index];
    }

    public Color getStickerFillColor(int index) {
        return stickersFillColor[index];
    }

    public void setPartFillColor(int index, Color newValue) {
        Color oldValue = partsFillColor[index];
        partsFillColor[index] = newValue;
        changeSupport.firePropertyChange(PART_FILL_COLOR_PROPERTY + "." + index, oldValue, newValue);
    }

    public void setStickerFillColor(int index, Color newValue) {
        Color oldValue = stickersFillColor[index];
        stickersFillColor[index] = newValue;
        changeSupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY + "." + index, oldValue, newValue);
    }

    public Color getPartOutlineColor(int index) {
        return partsOutlineColor[index];
    }

    public Color getStickerOutlineColor(int index) {
        return stickersOutlineColor[index];
    }

    public void setPartOutlineColor(int index, Color newValue) {
        Color oldValue = partsOutlineColor[index];
        partsOutlineColor[index] = newValue;
        changeSupport.firePropertyChange(PART_OUTLINE_COLOR_PROPERTY + "." + index, oldValue, newValue);
    }

    public void setStickerOutlineColor(int index, Color newValue) {
        Color oldValue = stickersOutlineColor[index];
        stickersOutlineColor[index] = newValue;
        changeSupport.firePropertyChange(STICKER_OUTLINE_COLOR_PROPERTY + "." + index, oldValue, newValue);
    }

    public void setPartOutlineColor(Color[] newValue) {
        Color[] oldValue = partsOutlineColor.clone();
        System.arraycopy(newValue, 0, partsOutlineColor, 0, partsOutlineColor.length);
        changeSupport.firePropertyChange(PART_OUTLINE_COLOR_PROPERTY, oldValue, newValue);
    }

    public void setPartFillColor(Color[] newValue) {
        Color[] oldValue = partsFillColor.clone();
        System.arraycopy(newValue, 0, partsFillColor, 0, partsFillColor.length);
        changeSupport.firePropertyChange(PART_FILL_COLOR_PROPERTY, oldValue, newValue);
    }

    public void setStickerFillColor(Color[] newValue) {
        Color[] oldValue = stickersFillColor.clone();
        System.arraycopy(newValue, 0, stickersFillColor, 0, stickersFillColor.length);
        changeSupport.firePropertyChange(STICKER_FILL_COLOR_PROPERTY, oldValue, newValue);
    }

    public void setStickerOutlineColor(Color[] newValue) {
        Color[] oldValue = stickersOutlineColor.clone();
        System.arraycopy(newValue, 0, stickersOutlineColor, 0, stickersOutlineColor.length);
        changeSupport.firePropertyChange(STICKER_OUTLINE_COLOR_PROPERTY, oldValue, newValue);
    }

    public int getPartCount() {
        return partsVisible.length;
    }

    public int getStickerCount() {
        return stickersVisible.length;
    }

    public int getFaceCount() {
        return stickerCountPerFace.length;
    }

    public int getStickerCount(int face) {
        return stickerCountPerFace[face];
    }

    public int getStickerOffset(int face) {
        int offset = 0;
        for (int i = 0; i < face; i++) {
            offset += stickerCountPerFace[i];
        }
        return offset;
    }

    public void reset() {
        for (int i = 0; i < getPartCount(); i++) {
            setPartVisible(i, true);
        }
        for (int i = 0; i < getStickerCount(); i++) {
            setStickerVisible(i, true);
        }
    }

    public void setFrontBgColor(Color newValue) {
        Color oldValue = frontBgColor;
        frontBgColor = newValue;
        changeSupport.firePropertyChange(FRONT_BG_COLOR_PROPERTY, oldValue, newValue);
    }

    public void setFrontBgImage(Image newValue) {
        Image oldValue = frontBgImage;
        frontBgImage = newValue;
        changeSupport.firePropertyChange(FRONT_BG_IMAGE_PROPERTY, oldValue, newValue);
    }

    public Color getFrontBgColor() {
        return frontBgColor;
    }

    public Image getFrontBgImage() {
        return frontBgImage;
    }

    public void setRearBgColor(Color newValue) {
        Color oldValue = rearBgColor;
        rearBgColor = newValue;
        changeSupport.firePropertyChange(REAR_BG_COLOR_PROPERTY, oldValue, newValue);
    }

    public void setRearBgImage(Image newValue) {
        Image oldValue = rearBgImage;
        rearBgImage = newValue;
        changeSupport.firePropertyChange(REAR_BG_IMAGE_PROPERTY, oldValue, newValue);
    }

    public Color getRearBgColor() {
        return rearBgColor;
    }

    public Image getRearBgImage() {
        return rearBgImage;
    }

    public boolean isFrontBgImageVisible() {
        return frontBgImageVisible;
    }

    public boolean isRearBgImageVisible() {
        return rearBgImageVisible;
    }

    public boolean isStickersImageVisible() {
        return stickersImageVisible;
    }

    public void setFrontBgImageVisible(boolean newValue) {
        boolean oldValue = frontBgImageVisible;
        frontBgImageVisible = newValue;
        changeSupport.firePropertyChange(FRONT_BG_IMAGE_VISIBLE_PROPERTY, new Boolean(oldValue), new Boolean(newValue));
    }

    public void setRearBgImageVisible(boolean newValue) {
        boolean oldValue = rearBgImageVisible;
        rearBgImageVisible = newValue;
        changeSupport.firePropertyChange(REAR_BG_IMAGE_VISIBLE_PROPERTY, new Boolean(oldValue), new Boolean(newValue));
    }

    public void setStickersImageVisible(boolean newValue) {
        boolean oldValue = stickersImageVisible;
        stickersImageVisible = newValue;
        changeSupport.firePropertyChange(STICKERS_IMAGE_VISIBLE_PROPERTY, new Boolean(oldValue), new Boolean(newValue));
    }

    public void setValueIsAdjusting(boolean newValue) {
        boolean oldValue = valueIsAdjusting;
        valueIsAdjusting = newValue;
        changeSupport.firePropertyChange(VALUE_IS_ADJUSTING_PROPERTY, new Boolean(oldValue), new Boolean(newValue));

    }

    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }

    public float getStickerExplosion(int index) {
        return stickerExplosion[index];
    }

    public void setStickerExplosion(int index, float newValue) {
        float oldValue = stickerExplosion[index];
        stickerExplosion[index] = newValue;
        changeSupport.firePropertyChange(
                STICKER_EXPLOSION_PROPERTY, new Float(oldValue),
                new Float(newValue));
    }

    public void dispose() {
        if (stickersImage != null) {
            stickersImage.flush();
            stickersImage = null;
        }
    }

    public int getTwistDuration() {
        return twistDuration;
    }

    public void setTwistDuration(int newValue) {
        int oldValue = twistDuration;
        twistDuration = newValue;
        changeSupport.firePropertyChange(TWIST_DURATION_PROPERTY, new Integer(oldValue), new Integer(newValue));
    }

    public void setTo(CubeAttributes that) {
        for (int i = 0; i < getPartCount(); i++) {
            setPartVisible(i, that.isPartVisible(i));
        }
        for (int i = 0; i < getStickerCount(); i++) {
            setStickerVisible(i, that.isStickerVisible(i));
        }
    // FIXME - Implement the rest
    }

    public Object clone() {
        try {
            DefaultCubeAttributes that = (DefaultCubeAttributes) super.clone();
            that.changeSupport = new PropertyChangeSupport(that);
            that.partsVisible = this.partsVisible.clone();
            that.partsFillColor = this.partsFillColor.clone();
            that.partsOutlineColor = this.partsOutlineColor.clone();
            that.stickersVisible = this.stickersVisible.clone();
            that.stickersFillColor = this.stickersFillColor.clone();
            that.stickersOutlineColor = this.stickersOutlineColor.clone();
            that.stickerCountPerFace = this.stickerCountPerFace.clone();
            that.partExplosion = this.partExplosion.clone();
            that.stickerExplosion = this.stickerExplosion.clone();
            return that;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError("clone not supported?");
        }
    }
}

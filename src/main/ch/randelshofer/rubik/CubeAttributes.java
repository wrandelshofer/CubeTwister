/*
 * @(#)CubeAttributes.java  2.4  2008-01-03
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.rubik;

//import ch.randelshofer.gui.event.*;
import ch.randelshofer.util.*;
import java.awt.*;
import java.beans.*;
/**
 * The interface for objects which describe the attributes of a 
 * Rubik's Cube-like puzzle.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>2.3 2007-11-14 Added property twistDuration. Renamed property
 * constants.
 * <br>2.2 2007-08-29 Property partExplosion added. 
 * <br>2.0 2004-10-08 Renamed from CubeColorModel3D to CubeAttributes.
 * <br>1.2 2002-05-05 Methods setAlpha and setBeta added.
 * <br>1.1 Now uses the original Rubik's Cube Color scheme.
 * <br>1.0 2001-08-19 Created.
 *
 */
public interface CubeAttributes extends Cloneable {
    public final static String ALPHA_PROPERTY = "alpha";
    public final static String SCALE_FACTOR_PROPERTY = "scaleFactor";
    public final static String EXPLOSION_FACTOR_PROPERTY = "explosionFactor";
    public final static String PART_EXPLOSION_PROPERTY = "partExplosion";
    public final static String STICKER_EXPLOSION_PROPERTY = "stickerExplosion";
    public final static String BETA_PROPERTY = "beta";
    public final static String STICKERS_IMAGE_PROPERTY = "stickersImage";
    public final static String PART_VISIBLE_PROPERTY = "partVisible";
    public final static String STICKER_VISIBLE_PROPERTY = "stickerVisible";
    public final static String PART_FILL_COLOR_PROPERTY = "partFillColor";
    public final static String PART_OUTLINE_COLOR_PROPERTY = "partOutlineColor";
    public final static String STICKER_FILL_COLOR_PROPERTY = "stickerFillColor";
    public final static String STICKER_OUTLINE_COLOR_PROPERTY = "stickerOutlineColor";
    public final static String FRONT_BG_IMAGE_PROPERTY = "frontBgImage";
    public final static String REAR_BG_IMAGE_PROPERTY = "rearBgImage";
    public final static String FRONT_BG_COLOR_PROPERTY = "frontBgColor";
    public final static String REAR_BG_COLOR_PROPERTY = "rearBgColor";
    public final static String STICKERS_IMAGE_VISIBLE_PROPERTY = "stickersImageVisible";
    public final static String REAR_BG_IMAGE_VISIBLE_PROPERTY = "rearBgImageVisible";
    public final static String FRONT_BG_IMAGE_VISIBLE_PROPERTY = "frontBgImageVisible";
    public final static String VALUE_IS_ADJUSTING_PROPERTY = "valueIsAdjusting";
    public final static String TWIST_DURATION_PROPERTY = "twistDuration";
/*
    public final static String PROP_ = "";
    */
    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /** Gets the alpha orientation of the cube. */
    public float getAlpha();

    /** Gets the beta orientation of the cube. */
    public float getBeta();

    /** Gets the global explosion factor of the cube. */
    public float getExplosionFactor();

    /** Gets the explosion factor of a single part of the cube. */
    public float getPartExplosion(int index);

    /** Gets the explosion factor of a single sticker of the cube. */
    public float getStickerExplosion(int index);

    public float getScaleFactor();
    public Image getStickersImage();
    public Image getFrontBgImage();
    public Image getRearBgImage();
    public boolean isPartVisible(int index);
    public boolean isStickerVisible(int index);
    public boolean isStickersImageVisible();
    public boolean isFrontBgImageVisible();
    public boolean isRearBgImageVisible();
    public Color getPartFillColor(int index);
    public Color getStickerFillColor(int index);
    public Color getPartOutlineColor(int index);
    public Color getStickerOutlineColor(int index);
    public Color getFrontBgColor();
    public Color getRearBgColor();
    public int getPartCount();
    public int getStickerCount();
    public int getFaceCount();
    public int getStickerCount(int face);
    public int getStickerOffset(int face);
    public int getTwistDuration();
    
    /**
     * This attribute indicates that any upcoming changes to the value
     * of the model should be considered a single event. This attribute
     * will be set to true at the start of a series of changes to the value,
     * and will be set to false when the value has finished changing.  Normally
     * this allows a listener to only take action when the final value change in
     * committed, instead of having to do updates for all intermediate values.
     * <p>
     * Sliders and scrollbars use this property when a drag is underway.
     * 
     * @param b true if the upcoming changes to the value property are part of a series
     */
   public void setValueIsAdjusting(boolean b);


    /**
     * Returns true if the current changes to the value property are part 
     * of a series of changes.
     * 
     * @return the valueIsAdjustingProperty.  
     * @see #setValueIsAdjusting
     */
   public boolean getValueIsAdjusting();
    
    public void reset();
    
    /**
     * Gets rid of allocated resources.
     */
    public void dispose();
    
    /**
     * Sets the attributes of this cube to the specified attributes.
     * @param that
     */
    public void setTo(CubeAttributes that);
    
    /**
     * Clones the attributes.
     * @return a clone of the attributes.
     */
    public Object clone();
}

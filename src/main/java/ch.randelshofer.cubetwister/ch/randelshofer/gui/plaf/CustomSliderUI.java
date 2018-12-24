/* @(#)CustomSliderUI.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.plaf;

import ch.randelshofer.gui.*;
import ch.randelshofer.gui.border.BackdropBorder;
import ch.randelshofer.gui.border.ImageBevelBorder;
import javax.swing.border.Border;

import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.beans.*;

import javax.swing.border.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

/**
 * CustomSliderUI draws a BackdropBorder in the background of the slider, and
 * draws an Icon as the slider thumb. 
 * This allows for easy visual customization of sliders.
 * <p>
 * Usage:
 * <pre>
 * JSlider s = new JSlider();
 * s.setUI((SliderUI) CustomSliderUI.createUI(b));
 * s.setBorder(new BackdropBorder(....));
 * s.putClientProperty(CustomSliderUI.THUMB_ICON_CLIENT_PROPERTY, new Icon(....));
 * </pre>
 *
 * <p>XXX This class has been implemented for horizontal
 * sliders whithout labels only. 
 * <p>FIXME The thumb can be dragged too far to the left
 * and to the right.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 2001-10-16 Created.
 */
public class CustomSliderUI extends BasicSliderUI
implements PlafConstants {
    public final static String THUMB_ICON_CLIENT_PROPERTY = "CustomSliderUI.ThumbIcon";
    
    /** Horizontal Track. */
    private ImageBevelBorder trackHorizontalRenderer;
    /** Horizontal Track. */
    private ImageBevelBorder disabledTrackHorizontalRenderer;
    /** Vertical Track. */
    private ImageBevelBorder trackVerticalRenderer;
    
    /** Thumb. */
    private ImageIcon thumbIcon;
    private ImageIcon disabledThumbIcon;
    private ImageIcon pressedThumbIcon;
    
    private static Insets thumbInsets = new Insets(-1, 0, -3, 0);

    protected final int TICK_BUFFER = 4;
    protected boolean filledSlider = false;
    protected static Color thumbColor;
    protected static Color highlightColor;
    protected static Color shadowColor;
    protected static Color darkShadowColor;
    protected static int trackWidth;
    protected static int tickLength;
    protected static javax.swing.Icon horizThumbIcon;
    protected static javax.swing.Icon vertThumbIcon;

    /** 
     * The preffered amount of pixels for moving the slider thumb. 
     */
    private static final int PREFERRED_TRACK_LENGTH = 100;
    
    /** 
     * The minimal amount of pixels for moving the slider thumb. 
     */
    private static final int MINIMUM_TRACK_LENGTH = 10;
    
    
    protected final String SLIDER_FILL = "JSlider.isFilled";

    private final static String propertyPrefix = "Slider" + ".";

    // ********************************
    //          Create PLAF
    // ********************************
    public CustomSliderUI() {
        super(null);
        
        if (trackHorizontalRenderer == null) {
            String id = UIManager.getLookAndFeel().getID();
            String path;
            if ("Metal".equals(id)) {
                path = "images/Metal/";
            } else {
                path = "images/Mac/";
            }
            
            trackHorizontalRenderer = new ImageBevelBorder(
                Toolkit.getDefaultToolkit().createImage(CustomButtonUI.class.getResource(path+"TrackHorizontal.0.png")),
                new Insets(3, 4, 3, 4)
            );
            disabledTrackHorizontalRenderer = new ImageBevelBorder(
                Toolkit.getDefaultToolkit().createImage(CustomButtonUI.class.getResource(path+"TrackHorizontal.1.png")),
                new Insets(3, 4, 3, 4)
            );
            trackVerticalRenderer = new ImageBevelBorder(
                Toolkit.getDefaultToolkit().createImage(CustomButtonUI.class.getResource(path+"TrackVertical.0.png")),
                new Insets(3, 4, 3, 4)
            );

            thumbIcon = new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(CustomButtonUI.class.getResource(path+"Thumb.0.png"))
            );
            disabledThumbIcon = new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(CustomButtonUI.class.getResource(path+"Thumb.1.png"))
            );
            pressedThumbIcon = new ImageIcon(
                Toolkit.getDefaultToolkit().createImage(CustomButtonUI.class.getResource(path+"Thumb.2.png"))
            );
        }
    }
    public static ComponentUI createUI(JComponent c)    {
        return new CustomSliderUI();
    }

    protected String getPropertyPrefix() {
        return propertyPrefix;
    }

    // ********************************
    //          Install
    // ********************************
    public void installUI( JComponent c ) {
        trackWidth = (UIManager.get( "Slider.trackWidth" ) == null) ? 4 : ((Integer)UIManager.get( "Slider.trackWidth" )).intValue();
        tickLength = (UIManager.get( "Slider.majorTickLength") == null) ? 4 : ((Integer)UIManager.get( "Slider.majorTickLength" )).intValue();
        //horizThumbIcon = UIManager.getIcon( "Slider.horizontalThumbIcon" );
        //vertThumbIcon = UIManager.getIcon( "Slider.verticalThumbIcon" );
	super.installUI( c );

        LookAndFeel.installColors(c, getPropertyPrefix()+".background", getPropertyPrefix()+".foreground");

        thumbColor = Color.black; //UIManager.getColor("Slider.thumb");
        highlightColor = Color.white; //UIManager.getColor("Slider.highlight");
        shadowColor = Color.gray; //UIManager.getColor("Slider.darkShadow");
        darkShadowColor = Color.darkGray; //UIManager.getColor("Slider.darkShadow");

        horizThumbIcon = vertThumbIcon = new VectorIcon(new Rectangle2D.Float(0f, 0f, 8f, 8f), thumbColor, thumbColor);
        scrollListener.setScrollByBlock( false );

        Object sliderFillProp = c.getClientProperty( SLIDER_FILL );
        if ( sliderFillProp != null ) {
            filledSlider = ((Boolean)sliderFillProp).booleanValue();
        }
    }
    protected void installDefaults(JSlider c) {
        super.installDefaults(c);
        
        //We don't draw focus on our slider
        //focusInsets = (Insets)UIManager.get( "Slider.focusInsets" );
        focusInsets = new Insets(0, 0, 0, 0);
        
        // The bevel border draws the border and fills the background
        // area of the slider.
        PlafUtils.installBevelBorder(c, getPropertyPrefix() + "border");
    }

    protected PropertyChangeListener createPropertyChangeListener( JSlider slider ) {
        return new MetalPropertyListener();
    }

    protected class MetalPropertyListener extends BasicSliderUI.PropertyChangeHandler {
        public void propertyChange( PropertyChangeEvent e ) {  // listen for slider fill
	    super.propertyChange( e );

	    String name = e.getPropertyName();
	    if ( name.equals( SLIDER_FILL ) ) {
	        if ( e.getNewValue() != null ) {
		    filledSlider = ((Boolean)e.getNewValue()).booleanValue();
		}
		else {
		    filledSlider = false;
		}
	    }
	}
    }
    // ********************************
    //          Paint Methods
    // ********************************
    public void paint(Graphics g, JComponent c) {
        
        g.setColor(c.getBackground());
        g.fillRect(0, 0, c.getWidth(), c.getHeight());

        Border b = c.getBorder();
        if (b instanceof BackdropBorder) {
            ((BackdropBorder) b).getBackgroundBorder().paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());
        }
        //PlafUtils.paintBevel(c, g, 0, 0, c.getWidth(), c.getHeight(), true/*c.isEnabled()*/, false, false);

        recalculateIfInsetsChanged();
	recalculateIfOrientationChanged();
	Rectangle clip = g.getClipBounds();

	if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
	    paintTrack(g);
	}
        if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
            paintTicks(g);
        }
        if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
            paintLabels(g);
        }
	if ( slider.hasFocus() && clip.intersects( focusRect ) ) {
	    paintFocus(g);      
	}
	if ( clip.intersects( thumbRect ) ) {
	    paintThumb(g);
	}
    }

    public void paintThumb(Graphics g)  {
        if (slider.isEnabled()) {
            if (slider.getValueIsAdjusting()) {
                pressedThumbIcon.paintIcon(slider, g, thumbRect.x, thumbRect.y);
            } else {
                thumbIcon.paintIcon(slider, g, thumbRect.x, thumbRect.y);
            }
        } else {
            disabledThumbIcon.paintIcon(slider, g, thumbRect.x, thumbRect.y);
        }
    }

    public void paintTrack(Graphics g)  {
        /*
        Color trackColor = !slider.isEnabled() ? slider.getForeground().darker() :
                           slider.getForeground();

	boolean leftToRight = PlafUtils.isLeftToRight(slider);

        int trackLeft = 0;
        int trackTop = 0;
        int trackRight = 0;
        int trackBottom = 0;

        // Draw the track
	if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            trackBottom = (trackRect.height - 1) - getThumbOverhang();
            trackTop = trackBottom - (getTrackWidth() - 1);
            trackRight = trackRect.width - 1;
	}
	else {
	    if (leftToRight) {
	        trackLeft = (trackRect.width - getThumbOverhang()) - 
		                                         getTrackWidth();
		trackRight = (trackRect.width - getThumbOverhang()) - 1;
	    }
	    else {
	        trackLeft = getThumbOverhang();
		trackRight = getThumbOverhang() + getTrackWidth() - 1;
	    }
            trackBottom = trackRect.height - 1;
	}
*/
	if ( slider.isEnabled() ) {
            trackHorizontalRenderer.paintBorder(slider, g, trackRect.x, trackRect.y + (trackRect.height - 6) / 2, trackRect.width, 6);
	} else {
            disabledTrackHorizontalRenderer.paintBorder(slider, g, trackRect.x, trackRect.y + (trackRect.height - 6) / 2, trackRect.width, 6);
	}
    }

    public void paintFocus(Graphics g)  {        
    }

    protected Dimension getThumbSize() {
        return new Dimension(thumbIcon.getIconWidth(), thumbIcon.getIconHeight());
    }

    
    // ********************************
    //          Dimensions
    // ********************************
    
    /**
     * Gets the preferred size for the component.
     */
    public Dimension getPreferredSize(JComponent c) {
        return getRequiredSize(c, PREFERRED_TRACK_LENGTH);
    }
    /**
     * Gets the minimal size for the component.
     */
    public Dimension getMinimumSize(JComponent c)  {
        return getRequiredSize(c, MINIMUM_TRACK_LENGTH);
    }
    
    /**
     * Helper method for computing the size of the component.
     *
     * @param c The JSlider.
     * @param trackLength The amount of pixels, we want to
     *   be able to move the slider thumb.
     */
    protected Dimension getRequiredSize(JComponent c, int trackLength) {
        recalculateIfInsetsChanged();
        
        Dimension d = new Dimension();
        
        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d.height = trackLength + thumbIcon.getIconHeight()
                + thumbInsets.top + thumbInsets.bottom
                + focusInsets.top + focusInsets.bottom
                + insetCache.top + insetCache.bottom;
            d.width = Math.max(thumbIcon.getIconWidth(), trackRect.width)
                + tickRect.width
                + labelRect.width
                + thumbInsets.left + thumbInsets.right
                + focusInsets.left + focusInsets.right
                + insetCache.left + insetCache.right;
        } else {
            d.width = trackLength + thumbIcon.getIconWidth()
                + thumbInsets.left + thumbInsets.right
                + focusInsets.left + focusInsets.right
                + insetCache.left + insetCache.right;
            d.height = Math.max(thumbIcon.getIconHeight(), trackRect.height)
                + tickRect.height
                + labelRect.height
                + thumbInsets.top + thumbInsets.bottom
                + focusInsets.top + focusInsets.bottom
                + insetCache.top + insetCache.bottom;
        }

        return d;
    }

    /**
     * Recalculates the geometry, if the
     * insets of the slider have changed.
     * /
    protected void recalculateIfInsetsChanged() {
        Insets newInsets = slider.getInsets();
        if ( !newInsets.equals( insetCache ) ) {
	    insetCache = newInsets;
	    calculateGeometry();
	}
    }
    /**
     * The focus rectangle is inside the border insets of the component.
     *
     * <p>Prerequisites: The insetCache variable must have been updated
     * before this method can be called. Otherwise the result is undefined.
     * /
    protected void calculateFocusRect() {
        focusRect.x = insetCache.left;
	focusRect.y = insetCache.top;
	focusRect.width = slider.getWidth() - (insetCache.left + insetCache.right);
	focusRect.height = slider.getHeight() - (insetCache.top + insetCache.bottom);
    }
    /**
     * The content rectangle is inside the focus rectangle of the component.
     *
     * <p>Prerequisites: calculateFocusRect must have been called 
     * before this method can be called. Otherwise the result is undefined.
     * /
    protected void calculateContentRect() {
        contentRect.x = focusRect.x + focusInsets.left;
        contentRect.y = focusRect.y + focusInsets.top;
        contentRect.width = focusRect.width - (focusInsets.left + focusInsets.right);
        contentRect.height = focusRect.height - (focusInsets.top + focusInsets.bottom);
    }
    /**
     * The distance that the track is from the side of the control.
     *
     * <p>Prerequisites: calculateContentRect must have been called 
     * before this method can be called. Otherwise the result is undefined.
     * /
    protected void calculateTrackBuffer() {
       if ( slider.getPaintLabels() && slider.getLabelTable()  != null ) {
            Component highLabel = getHighestValueLabel();
            Component lowLabel = getLowestValueLabel();

            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                trackBuffer = Math.max( highLabel.getBounds().width, lowLabel.getBounds().width ) / 2;
                trackBuffer = Math.max( trackBuffer, thumbRect.width / 2 );
            }
            else {
                trackBuffer = Math.max( highLabel.getBounds().height, lowLabel.getBounds().height ) / 2;
                trackBuffer = Math.max( trackBuffer, thumbRect.height / 2 );
            }
        }
        else {
            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                trackBuffer = thumbRect.width / 2;
            }
            else {
                trackBuffer = thumbRect.height / 2;
            }
        }
    }*/
    /**
     * The track rect is the area, where the track is painted.
     */
    protected void calculateTrackRect() {
	int centerSpacing = 0; // used to center sliders added using BorderLayout.CENTER (bug 4275631)
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    centerSpacing = thumbRect.height;
	    if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
	    if ( slider.getPaintLabels() ) centerSpacing += getHeightOfTallestLabel();
	    trackRect.x = contentRect.x + trackBuffer;
	    //trackRect.y = contentRect.y + (contentRect.height - centerSpacing - 1)/2;
            trackRect.y = (slider.getHeight() - thumbRect.height) / 2;
	    trackRect.width = contentRect.width - (trackBuffer * 2);
	    trackRect.height = thumbRect.height;
	}
	else {
	    centerSpacing = thumbRect.width;
	    if (PlafUtils.isLeftToRight(slider)) {
		if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
	    	if ( slider.getPaintLabels() ) centerSpacing += getWidthOfWidestLabel();
	    } else {
	        if ( slider.getPaintTicks() ) centerSpacing -= getTickLength();
	    	if ( slider.getPaintLabels() ) centerSpacing -= getWidthOfWidestLabel();
	    }
	    trackRect.x = contentRect.x + (contentRect.width - centerSpacing - 1)/2;
	    trackRect.y = contentRect.y + trackBuffer;
	    trackRect.width = thumbRect.width;
	    trackRect.height = contentRect.height - (trackBuffer * 2);
	}

    }

/*    
    protected void calculateThumbLocation() {
        if (slider.getSnapToTicks() ) {
	    int sliderValue = slider.getValue();
	    int snappedValue = sliderValue; 
	    int majorTickSpacing = slider.getMajorTickSpacing();
	    int minorTickSpacing = slider.getMinorTickSpacing();
	    int tickSpacing = 0;
	    
	    if ( minorTickSpacing > 0 ) {
	        tickSpacing = minorTickSpacing;
	    }
	    else if ( majorTickSpacing > 0 ) {
	        tickSpacing = majorTickSpacing;
	    }

	    if ( tickSpacing != 0 ) {
	        // If it's not on a tick, change the value
	        if ( (sliderValue - slider.getMinimum()) % tickSpacing != 0 ) {
		    float temp = (float)(sliderValue - slider.getMinimum()) / (float)tickSpacing;
		    int whichTick = Math.round( temp );
		    snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
		}
		
		if( snappedValue != sliderValue ) { 
		    slider.setValue( snappedValue );
		}
	    }
	}
	
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            int valuePosition = xPositionForValue(slider.getValue());

	    thumbRect.x = valuePosition;
	    thumbRect.y = trackRect.y - (thumbRect.height - trackRect.height) / 2;
        }
        else {
            int valuePosition = yPositionForValue(slider.getValue());
	    
	    thumbRect.x = trackRect.x;
	    thumbRect.y = valuePosition - (thumbRect.height / 2);
        }
    }

    /**
     * Gets the height of the tick area for horizontal sliders and the width of the
     * tick area for vertical sliders.  BasicSliderUI uses the returned value to
     * determine the tick area rectangle.
     * /
    public int getTickLength() {
        return slider.getOrientation() == JSlider.HORIZONTAL ? tickLength + TICK_BUFFER + 1 :
        tickLength + TICK_BUFFER + 3;
    }

    /**
     * Returns the shorter dimension of the track.
     */
    protected int getTrackWidth() {
        return trackWidth;
        /*
        // This strange calculation is here to keep the
        // track in proportion to the thumb.
        final double kIdealTrackWidth = 7.0;
	final double kIdealThumbHeight = 16.0;
        final double kWidthScalar = kIdealTrackWidth / kIdealThumbHeight;

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    return (int)(kWidthScalar * thumbRect.height);
	}
	else {
	    return (int)(kWidthScalar * thumbRect.width);
	}*/
    }
/*
    protected int xPositionForValue(int value)    {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
//        int trackLength = trackRect.width;
        int trackLength = trackRect.width + getThumbOverhang() * 2 - thumbRect.width;
//        int valueRange = slider.getMaximum() - slider.getMinimum();
        int valueRange = max - min;
        double pixelsPerValue = (double)trackLength / (double)valueRange;
//        int trackLeft = trackRect.x;
        int trackLeft = trackRect.x - getThumbOverhang();
//        int trackRight = trackRect.x + (trackRect.width - 1);
        int trackRight = trackRect.x + (trackRect.width - 1) + getThumbOverhang() - thumbRect.width;
        int xPosition;

        if ( !drawInverted() ) {
            xPosition = trackLeft;
            xPosition += Math.round( pixelsPerValue * (double)(value - min) );
        }
        else {
            xPosition = trackRight;
            xPosition -= Math.round( pixelsPerValue * (double)(value - min) );
        }

        xPosition = Math.max( trackLeft, xPosition );
        xPosition = Math.min( trackRight, xPosition );

        return xPosition;
    }

    
    /**
     * Returns a value give an x position.  If xPos is past the track at the left or the
     * right it will set the value to the min or max of the slider, depending if the
     * slider is inverted or not.
     * /
    public int valueForXPosition( int xPos ) {
        int value;
	final int minValue = slider.getMinimum();
	final int maxValue = slider.getMaximum();
	final int trackLength = trackRect.width;
//        int trackLength = trackRect.width + getThumbOverhang() * 2 - thumbRect.width;
//	final int trackLeft = trackRect.x; 
        int trackLeft = trackRect.x - getThumbOverhang();
//	final int trackRight = trackRect.x + (trackRect.width - 1);
        int trackRight = trackRect.x + (trackRect.width - 1) + getThumbOverhang() - thumbRect.width;
	
        //xPos -= getThumbOverhang();

        //System.out.print("ImageSliderui xPos:"+xPos+" trackLeft:"+trackLeft+" trackRight:"+trackRight+" max:"+maxValue+" value:");
        
        
	if ( xPos <= trackLeft ) {
	    value = drawInverted() ? maxValue : minValue;
	} else if ( xPos >= trackRight ) {
	    value = drawInverted() ? minValue : maxValue;
	} else {
	    int distanceFromTrackLeft = xPos - trackLeft;
	    int valueRange = maxValue - minValue;
	    double valuePerPixel = (double)valueRange / (double)trackLength;
	    int valueFromTrackLeft = (int)Math.round( distanceFromTrackLeft * valuePerPixel );
	    
	    value = drawInverted() ? maxValue - valueFromTrackLeft :
	      minValue + valueFromTrackLeft;
	}
	
	return value;
    }
    
    /**
     * Returns the longer dimension of the slide bar.  (The slide bar is only the
     * part that runs directly under the thumb)
     * /
    protected int getTrackLength() {   
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            return trackRect.width; 
        }
        return trackRect.height;
    }*/

    /**
     * Returns the amount that the thumb goes past the slide bar.
     */
    protected int getThumbOverhang() {
        return 6;
    }
/*
    protected void scrollDueToClickInTrack( int dir ) {
        scrollByUnit( dir );
    }

    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        //g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
        g.setColor(shadowColor);
        g.drawLine( x, TICK_BUFFER, x, TICK_BUFFER + (tickLength / 2) );
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        //g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
        g.setColor(shadowColor);
        g.drawLine( x, TICK_BUFFER , x, TICK_BUFFER + (tickLength - 1) );
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        //g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );
         g.setColor(shadowColor);

	if (PlafUtils.isLeftToRight(slider)) {
	    g.drawLine( TICK_BUFFER, y, TICK_BUFFER + (tickLength / 2), y );
	}
	else {
	    g.drawLine( 0, y, tickLength/2, y );
	}
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        //g.setColor( slider.isEnabled() ? slider.getForeground() : MetalLookAndFeel.getControlShadow() );

	if (PlafUtils.isLeftToRight(slider)) {
	    g.drawLine( TICK_BUFFER, y, TICK_BUFFER + tickLength, y );
	}
	else {
	    g.drawLine( 0, y, tickLength, y );
	}
    }*/
}

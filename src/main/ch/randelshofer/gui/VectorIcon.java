/*
 * @(#)VectorIcon.java 1.1  2003-03-16
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;
import javax.swing.*;
/**
 * An Icon which is composed of a set of java.awt.Shape's.
 *
 * @author Werner Randelshofer
 *
 * @version $Id$
 * <br>1.0 2001-09-02 Created.
 */
public class VectorIcon implements javax.swing.Icon {
    private Shape[] shapes;
    private Dimension size;
    private Color outlineColor;
    private Color fillColor;

    /**
     * Creates a new icon.
     *
     * @param p An Array of Shapes.
     * @param width The fixed width of the icon.
     * @param height The fixed height of the icon.
     * @param fillColor The fill color of the icon, or null if not filled.
     * @param outlineColor The outline color of the icon, or null if not outlined.
     */
    public VectorIcon(Shape[] p, int width, int height, Color fillColor, Color outlineColor) {
        shapes = p;
        this.size = new Dimension(width, height);
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
    }
    /**
     * Creates a new icon.
     *
     * @param p The Shape.
     * @param width The fixed width of the icon.
     * @param height The fixed height of the icon.
     * @param fillColor The fill color of the icon, or null if not filled.
     * @param outlineColor The outline color of the icon, or null if not outlined.
     */
    public VectorIcon(Shape p, int width, int height, Color fillColor, Color outlineColor) {
        shapes = new Shape[] { p };
        this.size = new Dimension(width, height);
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
    }
    /**
     * Creates a new icon which is filled and drawn with
     * the foreground color of the component.
     *
     * @param p The Shape.
     * @param width The fixed width of the icon.
     * @param height The fixed height of the icon.
     */
    public VectorIcon(Shape p, int width, int height) {
        shapes = new Shape[] { p };
        this.size = new Dimension(width, height);
    }
    /**
     * Creates a new icon which is filled and drawn with
     * the foreground color of the component. The size
     * of the VectorIcon is the bounding box of the shape.
     *
     * @param p The Shape.
     */
    public VectorIcon(Shape p) {
        shapes = new Shape[] { p };
        Rectangle b = p.getBounds();
        this.size = new Dimension(b.x + b.width, b.y + b.height);
    }
    /**
     * Creates a new icon. The size
     * of the VectorIcon is the bounding box of the shape.
     *
     * @param p The Shape.
     * @param fillColor The fill color of the icon, or null if not filled.
     * @param outlineColor The outline color of the icon, or null if not outlined.
     */
    public VectorIcon(Shape p, Color fillColor, Color outlineColor) {
        shapes = new Shape[] { p };
        Rectangle b = p.getBounds();
        this.size = new Dimension(b.x + b.width, b.y + b.height);
        this.fillColor = fillColor;
        this.outlineColor = outlineColor;
    }
    /**
     * Draws the icon at the specified location. Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        
        boolean enabled = c.isEnabled();
        if (enabled) 
            g.setColor(c.getForeground());
        else
            g.setColor(Color.gray);
        
        g.translate(x,y);
        if (shapes != null) {
            for (int i=0; i < shapes.length; i++) {
                if (fillColor != null) {
                    if (enabled) g.setColor(fillColor);
                    g.fill(shapes[i]);
                }
                if (outlineColor != null) {
                    if (enabled) g.setColor(outlineColor);
                    g.draw(shapes[i]);
                }
            }
        }
        g.translate(-x,-y);
    }

    /**
     * Gets the width of the icon.
     *
     * @return The fixed width of the icon.
     */
    public int getIconWidth() {
        return size.width;
    }

    /**
     * Gets the height of the icon.
     *
     * @return The fixed height of the icon.
     */
     public int getIconHeight() {
        return size.height;
    }
}

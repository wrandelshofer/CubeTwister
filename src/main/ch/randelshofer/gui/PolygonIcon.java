/*
 * @(#)PolygonIcon.java   1.4  2002-05-06
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;
import javax.swing.*;
/**
 * An Icon which is composed of java.awt.Polygon's.
 *
 * @author Werner Randelshofer
 *
 * @version 1.4 2002-05-06 Polygon Icons are now cloneable.
 * <br>1.3.2 2001-08-14 Comments added.
 * <br>1.3.1 2001-08-02 Method setFillColor added.
 * <br>1.3 2001-07-24 Upgraded for JDK 1.3.
 * <br>1.2    2001-02-27  When the component is disabled,
 * the polygon is drawn in gray.
 * <br>  1999-05-13  Polygons are stroked and filled to provide
 *           better visual compatibility on various Java VM's.
 * <br>  1999-05-02  Support added for array of polygons.
 * <br>  1999-02-14 Created.
 */
public class PolygonIcon
implements javax.swing.Icon, Cloneable {
    private Polygon[] polygons;
    private Dimension size;
    private Color color;
    private Color fillColor;

    /**
     * Creates a new icon.
     *
     * @param p An Array of polygons.
     * @param size The fixed width/height of the icon.
     */
    public PolygonIcon(Polygon[] p, Dimension size) {
        polygons = p;
        this.size = size;
    }
    /**
     * Creates a new icon.
     *
     * @param p The polygon.
     * @param size The fixed width/height of the icon.
     */
    public PolygonIcon(Polygon p, Dimension size) {
        polygons = new Polygon[] { p };
        this.size = size;
    }
    /**
     * Sets the fill color of the polygons.
     *
     * @param color The fill color. 
     *              If this is null, the polygons are filled
     *              with the foreground color.
     */
    public void setFillColor(Color color) {
        fillColor = color;
    }
    
    /**
     * Sets the foreground color of the polygons.
     *
     * @param color The foreground color. 
     *              If this is null, the polygons are filled 
     *              with the foreground color of the Component
     *              hosting the icon.
     */
    public void setForeground(Color color) {
        this.color = color;
    }
    public Color getForeground() {
        return color;
    }
    public Color getFillColor() {
        return fillColor;
    }
    /**
     * Draws the icon at the specified location. Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color bcol = (c.isEnabled())
            ? ((getForeground() != null) ? getForeground() : c.getForeground())
            : Color.gray;
        Color fcol = getFillColor();
        g.setColor(bcol);
        g.translate(x,y);
        if (polygons != null) {
            for (int i=0; i < polygons.length; i++) {
                if (fcol != null) {
                    g.setColor(fcol);
                    g.fillPolygon(polygons[i]);
                    g.setColor(bcol);
                } else {
                    g.fillPolygon(polygons[i]);
                }
                g.drawPolygon(polygons[i]);
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
     
     public Object clone() {
         try {
         return super.clone();
         } catch (CloneNotSupportedException e) {
             throw new InternalError(e.getMessage());
         }
     }
}

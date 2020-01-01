/* @(#)PolygonIcon.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
/**
 * An Icon which is composed of java.awt.Polygon's.
 *
 * @author Werner Randelshofer
 *
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
    public void paintIcon(@Nonnull Component c, @Nonnull Graphics g, int x, int y) {
        Color bcol = (c.isEnabled())
                ? ((getForeground() != null) ? getForeground() : c.getForeground())
                : Color.gray;
        Color fcol = getFillColor();
        g.setColor(bcol);
        g.translate(x, y);
        if (polygons != null) {
            for (int i = 0; i < polygons.length; i++) {
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

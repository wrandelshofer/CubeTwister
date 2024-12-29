/*
 * @(#)UIDefaultsCellRenderer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.debug;

import ch.randelshofer.gui.PolygonIcon;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;

/**
 * ObjectTableCellRenderer.
 *
 * @author Werner Randelshofer
 */
public class UIDefaultsCellRenderer extends DefaultTableCellRenderer {
    private final static long serialVersionUID = 1L;
    @Nonnull
    private PolygonIcon colorIcon = new PolygonIcon(
            new Polygon(
                    new int[] {0, 20, 20, 0},
                    new int[] {0, 0, 12, 12},
                    4
            ),
            new Dimension(20, 12)
    );

    /** Creates a new instance. */
    public UIDefaultsCellRenderer() {
    }

    public void paint(Graphics g) {
        // Work around ClassClastException's in some Icons of the Aqua LAF
        try {
            super.paint(g);
        } catch (ClassCastException e) {
            setIcon(null);
            super.paint(g);
        }
    }

    @Nonnull
    public Component getTableCellRendererComponent(
            JTable table, Object object,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Object value = object;
        setIcon(null);

        if (object instanceof Color) {
            Color v = (Color) object;
            setIcon(colorIcon);
            colorIcon.setFillColor(v);
            colorIcon.setForeground(v.darker());
            value = "Color ["+v.getRed()+","+v.getGreen()+","+v.getBlue()+"]";
        } else if (object instanceof Insets) {
            Insets v = (Insets) object;
            value = "Insets ["+v.top+","+v.left+","+v.bottom+","+v.right+"]";
        } else if (object instanceof Dimension) {
            Dimension v = (Dimension) object;
            value = "Dimension ["+v.width+","+v.height+"]";
        } else if (object instanceof Font) {
            Font v = (Font) object;
            value = "Font ["+v.getName()+","+v.getSize()+","+(v.isPlain()?"plain":(v.isBold()?"bold":"")+(v.isItalic()?"italic":""))+"]";
        } else if (object instanceof javax.swing.Icon) {
            javax.swing.Icon v = (javax.swing.Icon) object;
            setIcon(v);
            value = "Icon ["+v.getIconWidth()+","+v.getIconHeight()+"]";
        /*
        } else if (object instanceof Border) {
            Border v = (Border) object;
            setIcon(new BorderIcon(v, 20, 12));
         */
        }

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setFont(new Font("Lucida Grande", Font.PLAIN, 11));
        return this;
    }
}

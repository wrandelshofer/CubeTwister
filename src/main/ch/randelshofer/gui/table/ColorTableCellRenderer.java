/* @(#)ColorTableCellRenderer.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import ch.randelshofer.gui.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * ColorTableCellRenderer.
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ColorTableCellRenderer extends DefaultTableCellRenderer {
    private final static long serialVersionUID = 1L;
    private PolygonIcon icon;
    
    /** Creates new ColorModelCellRenderer */
    public ColorTableCellRenderer() {
        icon = new PolygonIcon(
        new Polygon(
        new int[] {0, 24, 24, 0},
        new int[] {0, 0, 12, 12},
        4
        ),
        new Dimension(25, 13)
        );
        setIcon(icon);
    }
    /*
    private String pad(int number) {
        StringBuilder buf = new StringBuilder();
        String sn = Integer.toString(number);
        for (int i=3; i > sn.length(); i--) {
            buf.append("  ");
        }
        buf.append(sn);
        return buf.toString();
    }
    */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Color color = (Color) value;
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        icon.setFillColor(color);
        if (color == null) {
            icon.setForeground(Color.black);
            setText("---");
        } else {
        icon.setForeground(Colors.shadow(color, 38));
        //setText(pad(color.getRed()) + "r "+ pad(color.getGreen()) + "g " + pad(color.getBlue())+"b");
        setText(color.getRed() + "r "+ color.getGreen() + "g " + color.getBlue()+"b");
        }
        /*
        if (table instanceof MutableJTable) {
            MutableJTable mtable = (MutableJTable) table;
            if (mtable.isStriped() && ! hasFocus) {
                Color bg;
                Color fg;
                if (isSelected) {
                    bg = UIManager.getColor("Table.selectionBackground");
                    fg = UIManager.getColor("Table.selectionForeground");
                } else {
                    bg = (row % 2 == 0 ? mtable.getAlternateColor() : mtable.getBackground());
                    fg = mtable.getForeground();
                }
                setBackground(bg);
                setForeground(fg);
            }
        }*/
        return c;
    }
}

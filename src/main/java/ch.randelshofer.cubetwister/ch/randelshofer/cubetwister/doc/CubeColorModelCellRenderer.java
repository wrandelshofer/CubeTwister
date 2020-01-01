/* @(#)CubeColorModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.Colors;
import ch.randelshofer.gui.PolygonIcon;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Polygon;

/**
 * Renders a color in the "Parts" and "Stickers" page of CubeView.
 *
 * @author Werner Randelshofer
 */
public class CubeColorModelCellRenderer
extends DefaultTableCellRenderer
implements ListCellRenderer {
    private final static long serialVersionUID = 1L;
    PolygonIcon icon;
//    private boolean isOpaque;
    
    /**
     * Creates new CubeColorModelCellRenderer
     */
    public CubeColorModelCellRenderer() {
        icon = new PolygonIcon(
        new Polygon(
        new int[] {0, 24, 24, 0},
        new int[] {0, 0, 12, 12},
        4
        ),
        new Dimension(25, 13)
        );
        setIcon(icon);
//        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, @Nonnull Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ((PolygonIcon) getIcon()).setFillColor(((CubeColorModel) value).getColor());
        icon.setForeground(Colors.shadow(((CubeColorModel) value).getColor(), 38));

        if (!hasFocus) {
            // Set the background color
            if (isSelected) {
                setBackground(UIManager.getColor("Table.selectionBackground"));
            }
            
            // Set the foreground color
            Color fg;
            if (isSelected) {
                fg = UIManager.getColor("Table.selectionForeground");
            } else {
                fg = UIManager.getColor("Table.foreground");
            }
            setForeground(fg);
            
        }


        return c;
    }

    /*
        public void setOpaque(boolean b) {
            isOpaque = b;
        }
        public boolean isOpaque() {
            return isOpaque;
        }
    */
    @Nonnull
    public Component getListCellRendererComponent(@Nonnull JList list, @Nullable Object value, int index, boolean isSelected, boolean hasFocus) {
        if (isSelected) {
            //  if (hasFocus) {
            //      setBackground(UIManager.getColor("Table.focusCellBackground"));
            //     setForeground(UIManager.getColor("Table.focusCellForeground"));
            // } else {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            //  }
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        //if (value instanceof Icon) {
        //    setIcon((Icon)value);
       // } else {
            setText((value == null) ? "" : value.toString());
        //}
        
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setBorder((hasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        
        icon.setFillColor((value == null) ? null : ((CubeColorModel) value).getColor());
        icon.setForeground((value == null) ? null : Colors.shadow(((CubeColorModel) value).getColor(), 38));
        return this;
    }
    
}

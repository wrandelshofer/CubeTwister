/* @(#)ColumnMappingCellRenderer.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.impexp.csv;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
/**
 * ColumnMappingCellRenderer.
 * @author  werni
 */
public class ColumnMappingCellRenderer
extends DefaultTableCellRenderer
implements ListCellRenderer {
        private final static long serialVersionUID = 1L;

    /** Creates a new instance of ColumnMappingCellRenderer */
    public ColumnMappingCellRenderer() {
    }
    
    public String getDisplayTextFor(Object value) {
        String text;
        if (value instanceof Integer) {
            int index = ((Integer) value).intValue();
            text = (index == -1) ? "ignore" : CSVImporter.supportedColumns[index];
        } else {
            text = ""+value;
        }
        return text;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, getDisplayTextFor(value), isSelected, hasFocus, row, column);
        
        return c;
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        setText(getDisplayTextFor(value));
        
        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setBorder((hasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        
        return this;
    }
    
}

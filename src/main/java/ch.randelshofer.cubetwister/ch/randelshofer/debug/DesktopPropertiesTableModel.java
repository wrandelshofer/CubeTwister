/* @(#)DesktopPropertiesTableModel.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.debug;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
/**
 * DesktopPropertiesTableModel.
 *
 * @author  Werner Randelshofer
 */
public class DesktopPropertiesTableModel extends AbstractTableModel {
    private final static long serialVersionUID = 1L;
    private Object[][] data;
    private int rowCount;
    private final static String[] columnNames = { "Key", "Value" };

    private final static String[] wellKnownNames = {
        "DnD.Autoscroll.initialDelay",
        "DnD.Autoscroll.interval",
        "DnD.Autoscroll.cursorHysteresis",
        "DnD.isDragImageSupported",
        "DnD.Cursor.MoveDrop",
        "DnD.Cursor.LinkDrop",
        "DnD.Cursor.CopyNoDrop",
        "DnD.Cursor.MoveNoDrop",
        "DnD.Cursor.LinkNoDrop",
        "awt.multiClickInterval",
        "awt.cursorBlinkRate",
    };
    
    /** Creates a new instance. */
    public DesktopPropertiesTableModel() {
        ArrayList<String> propNames = new ArrayList<String>();
        propNames.addAll(Arrays.asList(wellKnownNames));
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        String[] names = (String[])toolkit.getDesktopProperty("win.propNames");
        if (names != null) {
            propNames.addAll(Arrays.asList(names));
        }
        
        Collections.sort(propNames);
        
        data = new Object[propNames.size()][2];
        rowCount = 0;
        for (Iterator<String> i=propNames.iterator(); i.hasNext(); ) {
            String name = i.next();
            Object value = toolkit.getDesktopProperty(name);
            if (value != null) {
                data[rowCount][0] = name;
                data[rowCount++][1] = value;
                }
            }
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {
        return rowCount;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }
    
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }
    
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}

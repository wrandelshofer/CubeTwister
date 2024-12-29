/*
 * @(#)SystemColorsTableModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.debug;

import javax.swing.table.AbstractTableModel;
import java.awt.SystemColor;

/**
 *
 * @author Werner Randelshofer
 */
public class SystemColorsTableModel extends AbstractTableModel {
    private final static long serialVersionUID = 1L;
    private Object[][] data;
    private final static String[] columnNames = { "Key", "Value" };

    public SystemColorsTableModel() {
        setSystemColors();
    }
    public void setSystemColors() {
        data = new Object[][] {
            { "Active Caption", SystemColor.activeCaption },
            { "Active Caption Border", SystemColor.activeCaptionBorder },
            { "Active Caption Text", SystemColor.activeCaptionText },
            { "Control", SystemColor.control },
            { "Control Dark Shadow", SystemColor.controlDkShadow },
            { "Control Highlight", SystemColor.controlHighlight },
            { "Control Light Highlight", SystemColor.controlLtHighlight },
            { "Control Shadow", SystemColor.controlShadow },
            { "Control Text", SystemColor.controlText },
            { "Desktop", SystemColor.desktop },
            { "Inactive Caption", SystemColor.inactiveCaption },
            { "Inactive Caption Border", SystemColor.inactiveCaptionBorder },
            { "Inactive Caption Text", SystemColor.inactiveCaptionText },
            { "Info", SystemColor.info },
            { "Info Text", SystemColor.infoText },
            { "Menu", SystemColor.menu },
            { "Menu Text", SystemColor.menuText },
            { "Scrollbar", SystemColor.scrollbar },
            { "Text", SystemColor.text },
            { "Text Highlight", SystemColor.textHighlight },
            { "Text Highlight Text", SystemColor.textHighlightText },
            { "Text Inactive Text", SystemColor.textInactiveText },
            { "Text Text", SystemColor.textText },
            { "Window", SystemColor.window },
            { "Window Border", SystemColor.windowBorder },
            { "Window Text", SystemColor.windowText }
        };
        fireTableDataChanged();
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        return data.length;
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
}
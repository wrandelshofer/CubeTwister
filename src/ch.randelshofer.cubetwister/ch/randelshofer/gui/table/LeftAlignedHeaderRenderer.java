/* @(#)LeftAlignedHeaderRenderer.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
/**
 * LeftAlignedHeaderRenderer.
 * The header renderer. All this does is make the text left aligned.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class LeftAlignedHeaderRenderer extends DefaultTableCellRenderer {
    private final static long serialVersionUID = 1L;
    public Component getTableCellRendererComponent(JTable table,
    Object value, boolean selected, boolean focused,
    int row, int column) {
        super.getTableCellRendererComponent(table, value,
        selected, focused, row, column);
        setBorder(new CompoundBorder(
        UIManager.getBorder("TableHeader.cellBorder"),
        new EmptyBorder(0,2,0,0)
        ));
        setFont(UIManager.getFont("TableHeader.font"));
        return this;
    }
}

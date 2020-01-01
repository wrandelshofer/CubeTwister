/* @(#)LeftAlignedHeaderRenderer.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
/**
 * LeftAlignedHeaderRenderer.
 * The header renderer. All this does is make the text left aligned.
 *
 * @author  Werner Randelshofer
 */
public class LeftAlignedHeaderRenderer extends DefaultTableCellRenderer {
    private final static long serialVersionUID = 1L;

    @Nonnull
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean selected, boolean focused,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value,
                selected, focused, row, column);
        setBorder(new CompoundBorder(
                UIManager.getBorder("TableHeader.cellBorder"),
                new EmptyBorder(0, 2, 0, 0)
        ));
        setFont(UIManager.getFont("TableHeader.font"));
        return this;
    }
}

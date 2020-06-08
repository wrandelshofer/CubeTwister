/*
 * @(#)PasswordCellEditor.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.DefaultCellEditor;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTree;
import java.awt.Component;

/**
 * PassworCellEditor.
 *
 * @author Werner Randelshofer
 */
public class PasswordCellEditor extends DefaultCellEditor {
    private final static long serialVersionUID = 1L;
//
//  Constructors
//

    /**
     * Constructs a new instance.
     */
    public PasswordCellEditor() {
        super(new JPasswordField());
    }

//
//  Modifying
//

//
//  Implementing the TreeCellEditor Interface
//

    /**
     * Implements the <code>TreeCellEditor</code> interface.
     */
    public Component getTreeCellEditorComponent(@Nonnull JTree tree, Object value,
                                                boolean isSelected,
                                                boolean expanded,
                                                boolean leaf, int row) {
        String stringValue = tree.convertValueToText(value, isSelected,
                expanded, leaf, row, false);

        delegate.setValue(stringValue);
        ((JPasswordField) editorComponent).selectAll();
        return editorComponent;
    }

//
//  Implementing the CellEditor Interface
//
    /** Implements the <code>TableCellEditor</code> interface. */
    public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int column) {
        delegate.setValue(value);
        ((JPasswordField) editorComponent).selectAll();
	return editorComponent;
    }
}

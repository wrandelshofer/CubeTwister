/*
 * @(#)PassworCellEditor.java  1.0  August 24, 2003
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.table;

import java.awt.*;
import javax.swing.*;

/**
 * PassworCellEditor.
 *
 * @author Werner Randelshofer
 * @version 1.0 August 24, 2003  Created.
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

    /** Implements the <code>TreeCellEditor</code> interface. */
    public Component getTreeCellEditorComponent(JTree tree, Object value,
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

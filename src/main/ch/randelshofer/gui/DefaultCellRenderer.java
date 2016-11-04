/*
 * @(#)DefaultCellRenderer.java  1.0  April 11, 2004
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
/**
 * DefaultCellRenderer.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class DefaultCellRenderer implements TableCellRenderer, ListCellRenderer {
    /** The Swing component being rendered. */
    protected JComponent renderComponent;
    /**
     * The delegate class which handles all methods sent from the
     * <code>CellEditor</code>.
     */
    protected RenderDelegate delegate;
    
    /**
     * Constructs a <code>DefaultCellRenderer</code> that uses a text field.
     *
     * @param textField  a <code>JTextField</code> object
     */
    public DefaultCellRenderer(final JTextField textField) {
        renderComponent = textField;
        delegate = new RenderDelegate() {
            public void setValue(Object value) {
		textField.setText((value != null) ? value.toString() : "");
            }
        };
    }

    /**
     * Constructs a <code>DefaultCellRenderer</code> object that uses a check box.
     *
     * @param checkBox  a <code>JCheckBox</code> object
     */
    public DefaultCellRenderer(final JCheckBox checkBox) {
        renderComponent = checkBox;
        delegate = new RenderDelegate() {
            public void setValue(Object value) { 
            	boolean selected = false; 
		if (value instanceof Boolean) {
		    selected = ((Boolean)value).booleanValue();
		}
		else if (value instanceof String) {
		    selected = "true".equals(value);
		}
		checkBox.setSelected(selected);
            }
        };
    }

    /**
     * Constructs a <code>DefaultCellRenderer</code> object that uses a
     * combo box.
     *
     * @param comboBox  a <code>JComboBox</code> object
     */
    public DefaultCellRenderer(final JComboBox comboBox) {
        renderComponent = comboBox;
	comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        delegate = new RenderDelegate() {
	    public void setValue(Object value) {
		comboBox.setSelectedItem(value);
            }
        };
    }
    
    public Component getTableCellRendererComponent(JTable parent, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        delegate.setValue(value);
        
        if (hasFocus) {
            renderComponent.setBackground(UIManager.getColor("Table.focusCellBackground"));
            renderComponent.setForeground(UIManager.getColor("Table.focusCellForeground"));
        } else if (isSelected) {
            renderComponent.setBackground(parent.getSelectionBackground());
            renderComponent.setForeground(parent.getSelectionForeground());
        } else {
            renderComponent.setBackground(parent.getBackground());
            renderComponent.setForeground(parent.getForeground());
        }
        renderComponent.setFont(parent.getFont());
        /*
        if (parent instanceof MutableJTable) {
            MutableJTable mtable = (MutableJTable) parent;
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
                renderComponent.setBackground(bg);
                renderComponent.setForeground(fg);
                
            }
        }*/

        return renderComponent;
    }
    
    public Component getListCellRendererComponent(JList parent, Object value, int index, boolean isSelected, boolean hasFocus) {
        delegate.setValue(value);

        if (isSelected) {
            renderComponent.setBackground(parent.getSelectionBackground());
            renderComponent.setForeground(parent.getSelectionForeground());
        } else {
            renderComponent.setBackground(parent.getBackground());
            renderComponent.setForeground(parent.getSelectionForeground());
        }
        renderComponent.setFont(parent.getFont());
        /*
        if (parent instanceof MutableJList) {
            MutableJList mlist = (MutableJList) parent;
            if (mlist.isStriped() && ! hasFocus) {
                Color bg;
                Color fg;
                if (isSelected) {
                    bg = UIManager.getColor("List.selectionBackground");
                    fg = UIManager.getColor("List.selectionForeground");
                } else {
                    bg = (row % 2 == 0 ? mtable.getAlternateColor() : mtable.getBackground());
                    fg = mtable.getForeground();
                }
                renderComponent.setBackground(bg);
                renderComponent.setForeground(fg);
                
            }
        }
        */
        return renderComponent;
    }
    
    
    /**
     * The protected <code>RenderDelegate</code> class.
     */
    protected interface RenderDelegate {
       /**
        * Sets the value of this cell. 
        * @param value the new value of this cell
        */
    	public void setValue(Object value);
    }
}
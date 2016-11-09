/* @(#)PasswordCellRenderer.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import java.io.Serializable;
/**
 * PasswordCellRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PasswordCellRenderer extends JPasswordField 
implements TableCellRenderer {
    private final static long serialVersionUID = 1L;
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1); 
    
    // We need a place to store the color the JLabel should be returned 
    // to after its foreground and background colors have been set 
    // to the selection background color. 
    // These ivars will be made protected when their names are finalized. 
    private Color unselectedForeground; 
    private Color unselectedBackground; 
    
    /** Creates a new instance. */
    public PasswordCellRenderer() {
        setBorder(null);
    }

    /**
     * Overrides <code>JComponent.setForeground</code> to assign
     * the unselected-foreground color to the specified color.
     * 
     * @param c set the foreground color to this value
     */
    public void setForeground(Color c) {
        super.setForeground(c); 
        unselectedForeground = c; 
    }
    
    /**
     * Overrides <code>JComponent.setBackground</code> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground(Color c) {
        super.setBackground(c); 
        unselectedBackground = c; 
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((String) value);

	if (isSelected) {
           super.setForeground(table.getSelectionForeground());
	   super.setBackground(table.getSelectionBackground());
	} else {
	    super.setForeground((unselectedForeground != null) ? unselectedForeground 
	                                                       : table.getForeground());
	    super.setBackground((unselectedBackground != null) ? unselectedBackground 
	                                                       : table.getBackground());
	}

	setFont(table.getFont());

	if (hasFocus) {
             setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
	    if (table.isCellEditable(row, column)) {
	        super.setForeground( UIManager.getColor("Table.focusCellForeground") );
	        super.setBackground( UIManager.getColor("Table.focusCellBackground") );
	    }
	} else {
	    setBorder(noFocusBorder);
	}

        return this;
    }    
    private static class Dummy extends JPasswordField {
    private final static long serialVersionUID = 1L;
    /*
     * The following methods are overridden as a performance measure to 
     * to prune code-paths are often called in the case of renders
     * but which we know are unnecessary.  Great care should be taken
     * when writing your own renderer to weigh the benefits and 
     * drawbacks of overriding methods like these.
     */

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public boolean isOpaque() { 
	Color back = getBackground();
	Component p = getParent(); 
	if (p != null) { 
	    p = p.getParent(); 
	}
	// p should now be the JTable. 
	boolean colorMatch = (back != null) && (p != null) && 
	    back.equals(p.getBackground()) && 
			p.isOpaque();
	return !colorMatch && super.isOpaque(); 
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void validate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void revalidate() {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void repaint(long tm, int x, int y, int width, int height) {}

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void repaint(Rectangle r) { }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {	
	// Strings get interned...
	if (propertyName=="text") {
	    super.firePropertyChange(propertyName, oldValue, newValue);
	}
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a> 
     * for more information.
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) { }


    /**
     * Sets the <code>String</code> object for the cell being rendered to
     * <code>value</code>.
     * 
     * @param value  the string value for this cell; if value is
     *		<code>null</code> it sets the text value to an empty string
     * @see JLabel#setText
     * 
     */
    protected void setValue(Object value) {
	setText((value == null) ? "" : value.toString());
    }
	

    /**
     * A subclass of <code>DefaultTableCellRenderer</code> that
     * implements <code>UIResource</code>.
     * <code>DefaultTableCellRenderer</code> doesn't implement
     * <code>UIResource</code>
     * directly so that applications can safely override the
     * <code>cellRenderer</code> property with
     * <code>DefaultTableCellRenderer</code> subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends DefaultTableCellRenderer 
        implements javax.swing.plaf.UIResource
    {
    private final static long serialVersionUID = 1L;
    }
    }
}

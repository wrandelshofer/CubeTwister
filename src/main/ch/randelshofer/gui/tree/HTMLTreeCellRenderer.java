/*
 * @(#)HTMLTreeCellRenderer.java  1.0  2003-11-04
 *
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.gui.tree;

import javax.swing.*;
import javax.swing.tree.*;
/**
 * A TreeCellRenderer designed for tree cells with HTML text.
 * <p>
 * This TreeCellRenderer implements the workaround suggested in Bug Id  4743195
 * of the JavaSoft Bug Parade.
 *
 * @author  Werner Randelshofer
 * @version 1.0 2003-11-04 Created.
 */
public class HTMLTreeCellRenderer extends DefaultTreeCellRenderer {
    private final static long serialVersionUID = 1L;
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName=="foreground") {
            propertyName = "text";
        }
        super.firePropertyChange(propertyName, oldValue, newValue);
    }
}
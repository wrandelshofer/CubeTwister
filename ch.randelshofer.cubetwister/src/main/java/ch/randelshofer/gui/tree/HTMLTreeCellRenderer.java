/*
 * @(#)HTMLTreeCellRenderer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.tree;

import javax.swing.tree.DefaultTreeCellRenderer;
/**
 * A TreeCellRenderer designed for tree cells with HTML text.
 * <p>
 * This TreeCellRenderer implements the workaround suggested in Bug Id  4743195
 * of the JavaSoft Bug Parade.
 *
 * @author  Werner Randelshofer
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
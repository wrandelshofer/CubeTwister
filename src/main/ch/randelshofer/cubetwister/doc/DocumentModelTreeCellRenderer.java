/*
 * @(#)DocumentModelTreeCellRenderer.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 *
 * @author  werni
 */
public class DocumentModelTreeCellRenderer extends DefaultTreeCellRenderer {
    private final static long serialVersionUID = 1L;

    private boolean isSelectedAndFocused;

    /** Creates a new instance of DocumentModelTreeCellRenderer */
    public DocumentModelTreeCellRenderer() {
    }

    /**
     * Configures the renderer based on the passed in components.
     * The value is set from messaging the tree with
     * <code>convertValueToText</code>, which ultimately invokes
     * <code>toString</code> on <code>value</code>.
     * The foreground color is set based on the selection and the icon
     * is set based on on leaf and expanded.
     */
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
        boolean emphasized = false;
        if (value instanceof EntityModel) {
            EntityModel node = (EntityModel) value;

            if (node.isDefaultCube() ||
                    node.isDefaultNotation()) {
                emphasized = true;
            }
        }
        if (emphasized) {
            setFont(Fonts.getEmphasizedDialogFont());
        } else {
            setFont(Fonts.getDialogFont());
        }
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        return c;
    }
}

/*
 * @(#)DocumentModelTreeCellRenderer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.Fonts;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

/**
 *
 * @author Werner Randelshofer
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
            @Nonnull JTree tree,
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

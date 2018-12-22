/**
 * @(#)MutableTreeTransferHandler.java  1.1  2011-01-19
 * Copyright (c) 2010 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * A transfer handler for MutableJTree.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 2010-01-09 Created.
 */
public class MutableTreeTransferHandler extends TransferHandler {
    private final static long serialVersionUID = 1L;

    /** Returns a {@code TreeModel} or null if the JComponent does not have a tree model. */
    protected TreeModel getTreeModel(JComponent c) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            return tree.getModel();
        } else {
            return null;
        }
    }

    /** Returns the selection paths or null if the component does not have selection paths. */
    protected TreePath[] getSelectionPaths(JComponent c) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            return tree.getSelectionPaths();
        } else {
            return null;
        }
    }

    /** Returns the lead selection paths or null if the component does not have one. */
    protected TreePath getLeadSelectionPath(JComponent c) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            return tree.getLeadSelectionPath();
        } else {
            return null;
        }
    }

    /** Returns the cell renderer component or null if the component does not have a cell renderer. */
    protected Component getCellRendererComponent(JComponent c, Object value,
            boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            TreeCellRenderer r = tree.getCellRenderer();
            return r.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        } else {
            return null;
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        TreeModel m = getTreeModel(c);
        if (m != null) {
            if (m instanceof MutableTreeModel) {
                return COPY_OR_MOVE;
            } else {
                return COPY;
            }
        }
        return super.getSourceActions(c);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        TreeModel m = getTreeModel(c);
        if (m != null) {
            final TreePath[] paths = getSelectionPaths(c);
            final DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[paths.length];
            for (int i = 0; i < paths.length; i++) {
                nodes[i] = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
            }
            Transferable t;
            if (m instanceof MutableTreeModel) {
                MutableTreeModel model = (MutableTreeModel) m;
                t = model.exportTransferable(nodes);

            } else {
                t = super.createTransferable(c);
            }
            return new MutableTreeTransferable(c, paths, t);
        }
        return super.createTransferable(c);
    }

    @Override
    public Icon getVisualRepresentation(Transferable data) {
        if (data instanceof MutableTreeTransferable) {
            MutableTreeTransferable t = (MutableTreeTransferable) data;
            JComponent tree = t.getSource();
            TreeModel m = getTreeModel(tree);

            // Compute the size of the image
            int w = tree.getWidth();
            int h = tree.getHeight();

            if (tree.getParent() != null) {
                w = Math.min(w, tree.getParent().getWidth());
                h = Math.min(h, tree.getParent().getHeight());
            }

            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            g.setColor(tree.getBackground());
            g.fillRect(0, 0, w, h);

            int y = 0;
            for (TreePath i : t.getTransferedPaths()) {
                TreeNode node = (TreeNode) i.getLastPathComponent();
                Component c = getCellRendererComponent(
                        tree, i.getLastPathComponent(),
                        false, false, m.isLeaf(node),/*row*/ 0, false);
                //c.setBackground(transparent);
                Dimension p = c.getPreferredSize();
                c.setBounds(0, 0, w, p.height);
                g.setClip(0, 0, w, p.height);
                c.paint(g);
                y += p.height;
                //if (i < dragOriginItemIndex) yoffset += p.height;
                if (y > h) {
                    break;
                }
                g.translate(0, p.height);
            }
            g.dispose();
            if (y < h) {
                buf = buf.getSubimage(0, 0, w, y);
            }
            return new ImageIcon(buf);
        }

        return super.getVisualRepresentation(data);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        TreeModel m = getTreeModel(source);
        if (m != null) {
            JComponent list = source;
            if (m instanceof MutableTreeModel) {
                MutableTreeModel model = (MutableTreeModel) m;
                if (action == DnDConstants.ACTION_MOVE) {
                    if (data instanceof MutableTreeTransferable) {
                        MutableTreeTransferable t = (MutableTreeTransferable) data;
                        if (t.getSource() == source) {
                            for (TreePath i : t.getTransferedPaths()) {
                                if (model.isNodeRemovable((DefaultMutableTreeNode) i.getLastPathComponent())) {
                                    model.removeNodeFromParent((DefaultMutableTreeNode) i.getLastPathComponent());
                                }
                            }
                        }
                    }
                }
            }
        }
        super.exportDone(source, data, action);
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        TreeModel m = getTreeModel(comp);
        if (m != null) {
            if (m instanceof MutableTreeModel) {
                // FIXME - how do we know which action and which row?
                MutableTreeModel model = (MutableTreeModel) m;
                return model.isImportable(transferFlavors, DnDConstants.ACTION_COPY,
                        (DefaultMutableTreeNode) model.getRoot(), model.getChildCount(model.getRoot()));
            }
        }
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        TreeModel m = getTreeModel(comp);
        if (m != null) {
            if (m instanceof MutableTreeModel) {
                MutableTreeModel model = (MutableTreeModel) m;
                try {
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) (getLeadSelectionPath(comp) != null ? getLeadSelectionPath(comp).getLastPathComponent() : model.getRoot());

                    final List<TreePath> imported = model.importTransferable(t, DnDConstants.ACTION_COPY, parent, model.getChildCount(parent));
                    return imported.size() > 0;
                } catch (UnsupportedFlavorException ex) {
                    return false;
                } catch (IOException ex) {
                    return false;
                }
            }
        }
        return super.importData(comp, t);
    }
}

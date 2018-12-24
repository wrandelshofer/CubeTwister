/**
 * @(#)MutableListTransferHandler.java  1.0  Mar 21, 2008
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.list;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A transfer handler for JDnDList.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MutableListTransferHandler extends TransferHandler {
    private final static long serialVersionUID = 1L;

    @Override
    public int getSourceActions(JComponent c) {
        if (c instanceof JList) {
            JList list = (JList) c;
            if (list.getModel() instanceof MutableListModel) {
                return COPY_OR_MOVE;
            } else {
                return COPY;
            }
        }
        return super.getSourceActions(c);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList list = (JList) c;
            int[] indices = list.getSelectedIndices();
            Transferable t;
            if (list.getModel() instanceof MutableListModel) {
                MutableListModel model = (MutableListModel) list.getModel();
                t = model.exportTransferable(indices);

            } else {
                t = super.createTransferable(c);
            }
            return new MutableListTransferable(list, indices, t);
        }
        return super.createTransferable(c);
    }

    @Override
    public Icon getVisualRepresentation(Transferable data) {
        if (data instanceof MutableListTransferable) {
            MutableListTransferable t = (MutableListTransferable) data;
            JList list = t.getSource();
            ListModel m = list.getModel();

            // Compute the size of the image
            int w = list.getWidth();
            int h = list.getHeight();

            if (list.getParent() != null) {
                w = Math.min(w, list.getParent().getWidth());
                h = Math.min(h, list.getParent().getHeight());
            }

            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            g.setColor(list.getBackground());
            g.fillRect(0, 0, w, h);
            ListCellRenderer r = list.getCellRenderer();

            int y = 0;
            for (int i : t.getTransferedIndices()) {
                Component c = r.getListCellRendererComponent(
                        list, m.getElementAt(i),
                        i, /*isSelected*/ false, false);
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
        if (source instanceof JList) {
            JList list = (JList) source;
            if (list.getModel() instanceof MutableListModel) {
                MutableListModel model = (MutableListModel) list.getModel();
                if (action == DnDConstants.ACTION_MOVE) {
                    if (data instanceof MutableListTransferable) {
                        MutableListTransferable t = (MutableListTransferable) data;
                        if (t.getSource() == source) {
                            for (int i : t.getTransferedIndices()) {
                                model.remove(i);
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
        if (comp instanceof JList) {
            JList list = (JList) comp;
            if (list.getModel() instanceof MutableListModel) {
                // FIXME - how do we know which action and which row?
                MutableListModel model = (MutableListModel) list.getModel();
                return model.isImportable(transferFlavors, DnDConstants.ACTION_COPY,
                        model.getSize(), false);
            }
        }
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        if (comp instanceof JList) {
            JList list = (JList) comp;
            if (list.getModel() instanceof MutableListModel) {
                    // FIXME - how do we know which action and which row?
                    MutableListModel model = (MutableListModel) list.getModel();
                try {
                    int imported = model.importTransferable(t, DnDConstants.ACTION_COPY, model.getSize(), false);
                    return imported > 0;
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

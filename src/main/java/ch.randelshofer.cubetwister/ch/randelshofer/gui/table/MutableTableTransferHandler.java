/*
 * @(#)MutableTableTransferHandler.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.table;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
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
public class MutableTableTransferHandler extends TransferHandler {
    private final static long serialVersionUID = 1L;

    @Override
    public int getSourceActions(JComponent c) {
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            if (table.getModel() instanceof MutableTableModel) {
                return COPY_OR_MOVE;
            } else {
                return COPY;
            }
        }
        return super.getSourceActions(c);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            int[] indices = table.getSelectedRows();
            Transferable t;
            if (table.getModel() instanceof MutableTableModel) {
                MutableTableModel model = (MutableTableModel) table.getModel();
                t = model.exportRowTransferable(indices);

            } else {
                t = super.createTransferable(c);
            }
            return new MutableTableTransferable(table, indices, t);
        }
        return super.createTransferable(c);
    }

    @Override
    public Icon getVisualRepresentation(Transferable data) {
        if (data instanceof MutableTableTransferable) {
            MutableTableTransferable t = (MutableTableTransferable) data;
            JTable table = t.getSource();
            TableModel m = table.getModel();

            // Compute the size of the image
            int w = table.getWidth();
            int h = table.getHeight();

            if (table.getParent() != null) {
                w = Math.min(w, table.getParent().getWidth());
                h = Math.min(h, table.getParent().getHeight());
            }

            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = buf.createGraphics();
            g.setColor(table.getBackground());
            g.fillRect(0, 0, w, h);
            TableCellRenderer r = table.getCellRenderer(0,0);

            int y = 0;
            for (int i : t.getTransferedIndices()) {
                Component c = r.getTableCellRendererComponent(
                        table, m.getValueAt(i,0), false, false,
                        i, 0);
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
        if (source instanceof JTable) {
            JTable list = (JTable) source;
            if (list.getModel() instanceof MutableTableModel) {
                MutableTableModel model = (MutableTableModel) list.getModel();
                if (action == DnDConstants.ACTION_MOVE) {
                    if (data instanceof MutableTableTransferable) {
                        MutableTableTransferable t = (MutableTableTransferable) data;
                        if (t.getSource() == source) {
                            for (int i : t.getTransferedIndices()) {
                                model.removeRow(i);
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
        if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            if (table.getModel() instanceof MutableTableModel) {
                // FIXME - how do we know which action and which row?
                MutableTableModel model = (MutableTableModel) table.getModel();
                return model.isRowImportable(transferFlavors, DnDConstants.ACTION_COPY,
                        model.getRowCount(), false);
            }
        }
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        if (comp instanceof JTable) {
            JTable table = (JTable) comp;
            if (table.getModel() instanceof MutableTableModel) {
                    int row=table.getSelectionModel().getLeadSelectionIndex()+1;
                    MutableTableModel model = (MutableTableModel) table.getModel();
                try {
                    int imported = model.importRowTransferable(t, DnDConstants.ACTION_COPY, row, false);
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

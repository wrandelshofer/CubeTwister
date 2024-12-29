/*
 * @(#)MutableTableTransferable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.JTable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * The MutableTableTransferable is a proxy for the actual Transferable of a JTable,
 * plus a list of indexes of the transferred rows.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MutableTableTransferable implements Transferable {
    private JTable table;
    private Transferable target;
    private int[] indices;

    public MutableTableTransferable(JTable source, int[] indices, Transferable target) {
        this.table = source;
        this.indices = indices;
        this.target = target;
    }

    public JTable getSource() {
        return table;
    }
    public int[] getTransferedIndices() {
        return indices;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return target.getTransferDataFlavors();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return target.isDataFlavorSupported(flavor);
    }

    @Nonnull
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return target.getTransferData(flavor);
    }

}

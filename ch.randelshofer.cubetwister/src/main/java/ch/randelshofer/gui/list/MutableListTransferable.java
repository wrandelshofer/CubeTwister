/*
 * @(#)MutableListTransferable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.list;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.JList;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * The MutableListTransferable is a proxy for the actual Transferable of a JList,
 * plus a list of indexes of the transferred rows.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MutableListTransferable implements Transferable {
    private JList list;
    private Transferable target;
    private int[] indices;

    public MutableListTransferable(JList source, int[] indices, Transferable target) {
        this.list = source;
        this.indices = indices;
        this.target = target;
    }

    public JList getSource() {
        return list;
    }
    public int[] getTransferedIndices() {
        return indices;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return target.getTransferDataFlavors();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return target.isDataFlavorSupported(flavor);
    }

    @Nonnull
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return target.getTransferData(flavor);
    }

}

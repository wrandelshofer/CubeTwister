/*
 * @(#)FileListTransferable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import org.jhotdraw.annotation.Nonnull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
/**
 * Java File List Transferable.
 *
 * @author  Werner Randelshofer
 */
public class FileListTransferable
implements Transferable {
    private DataFlavor[] flavors;
    private Object data;

    /**
     * Creates new FileListTransferable
     */
    public FileListTransferable(List<File> data) {
        this.data = data;
        flavors = new DataFlavor[]{
                DataFlavor.javaFileListFlavor
        };
    }

    @Nonnull
    public Object getTransferData(@Nonnull DataFlavor dataFlavor)
            throws UnsupportedFlavorException, IOException {
        if (!dataFlavor.equals(flavors[0])) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        return data;
    }

    public boolean isDataFlavorSupported(@Nonnull DataFlavor dataFlavor) {
        return dataFlavor.equals(flavors[0]);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
}

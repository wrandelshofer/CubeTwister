/* @(#)JVMLocalObjectTransferable.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import org.jhotdraw.annotation.Nonnull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
/**
 *
 * @author Werner Randelshofer
 */
public class JVMLocalObjectTransferable
implements Transferable {
    private DataFlavor[] flavors;
    private Object data;

    /**
     * Creates new JVMLocalObjectTransferable
     */
    public JVMLocalObjectTransferable(@Nonnull Class<?> transferClass, Object data) {
        this.data = data;
        flavors = new DataFlavor[]{
                new DataFlavor(transferClass, "Object")
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

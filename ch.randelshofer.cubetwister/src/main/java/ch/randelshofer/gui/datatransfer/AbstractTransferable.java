/*
 * @(#)AbstractTransferable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Base class for transferable objects.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractTransferable implements Transferable {
    private DataFlavor[] flavors;

    /** Creates a new instance. */
    public AbstractTransferable(DataFlavor[] flavors) {
        this.flavors = flavors;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors.clone();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}

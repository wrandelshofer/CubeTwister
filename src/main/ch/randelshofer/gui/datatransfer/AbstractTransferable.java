/* @(#)AbstractTransferable.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.datatransfer;

import java.awt.datatransfer.*;
import java.io.*;

/**
 * Base class for transferable objects.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

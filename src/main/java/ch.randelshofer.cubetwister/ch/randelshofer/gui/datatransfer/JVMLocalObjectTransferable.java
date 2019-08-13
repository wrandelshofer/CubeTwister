/* @(#)JVMLocalObjectTransferable.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import java.awt.datatransfer.*;
import java.io.IOException;
/**
 *
 * @author Werner Randelshofer
 */
public class JVMLocalObjectTransferable
implements Transferable {
    private DataFlavor[] flavors;
    private Object data;
    
    /** Creates new JVMLocalObjectTransferable */
    public JVMLocalObjectTransferable(Class<?> transferClass, Object data) {
        this.data = data;
        flavors = new DataFlavor[] { 
            new DataFlavor(transferClass, "Object")
        };
    }

    public Object getTransferData(DataFlavor dataFlavor) 
    throws UnsupportedFlavorException, IOException {
        if (! dataFlavor.equals(flavors[0])) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        return data;
    }
    
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return dataFlavor.equals(flavors[0]);
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
}

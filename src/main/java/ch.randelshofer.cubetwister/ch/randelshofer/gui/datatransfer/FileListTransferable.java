/* @(#)FileListTransferable.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import java.awt.datatransfer.*;
import java.io.*;
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
    
    /** Creates new FileListTransferable */
    public FileListTransferable(List<File> data) {
        this.data = data;
        flavors = new DataFlavor[] { 
            DataFlavor.javaFileListFlavor
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

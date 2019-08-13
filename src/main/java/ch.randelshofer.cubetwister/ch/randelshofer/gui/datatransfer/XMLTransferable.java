/* @(#)XMLTransferable.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 * XMLTransferable.java
 */

package ch.randelshofer.gui.datatransfer;

import java.awt.datatransfer.*;
import java.io.*;
/**
 *
 * @author  Werner Randelshofer
 */
public class XMLTransferable implements java.awt.datatransfer.Transferable, java.awt.datatransfer.ClipboardOwner {
    private byte[] data;
    private DataFlavor flavor;
    
    /** Creates new XMLTransferable */
    public XMLTransferable(byte[] data, String mimeType, String humanPresentableName) {
        this.data = data;
        this.flavor = new DataFlavor(mimeType, humanPresentableName);
    }

    /**
     * Notifies this object that it is no longer the owner of
     * the contents of the clipboard.
     * @param clipboard the clipboard that is no longer owned
     * @param contents the contents which this owner had placed on the clipboard
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        
    }
    
    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException                if the data is no longer available
     *             in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is
     *             not supported.
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (this.flavor.equals(flavor)) {
            return new ByteArrayInputStream(data);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
    /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in.  The array should be ordered according to preference
     * for providing the data (from most richly descriptive to least descriptive).
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {flavor};
    }
    
    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating wjether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return this.flavor.equals(flavor);
    }

}

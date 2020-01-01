/* @(#)CharArrayReaderTransferable.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import org.jhotdraw.annotation.Nonnull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.CharArrayReader;
import java.io.IOException;
/**
 *
 * @author  Werner Randelshofer
 */
public class CharArrayReaderTransferable implements java.awt.datatransfer.Transferable {
    private char[] data;
    private DataFlavor flavor;

    /**
     * Creates a new instance of ArrayReaderTransferable
     */
    public CharArrayReaderTransferable(char[] data, @Nonnull String mimetype, String description) {
        this.data = data;

        if (mimetype.indexOf("; class=") == -1) {
            mimetype += "; class=java.io.Reader";
        }


        this.flavor = new DataFlavor(mimetype, description);
    }

    /**
     * Returns an object which represents the data to be transferred.  The class
     * of the object returned is defined by the representation class of the flavor.
     *
     * @param flavor the requested flavor for the data
     * @throws IOException                if the data is no longer available
     *                                    in the requested flavor.
     * @throws UnsupportedFlavorException if the requested data flavor is
     *                                    not supported.
     * @see DataFlavor#getRepresentationClass
     */
    @Nonnull
    public Object getTransferData(@Nonnull DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(this.flavor)) {
            return new CharArrayReader(data);
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
    @Nonnull
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{flavor};
    }

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     *
     * @param flavor the requested flavor for the data
     * @return boolean indicating wjether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(@Nonnull DataFlavor flavor) {
        return flavor.equals(this.flavor);
    }
    
}

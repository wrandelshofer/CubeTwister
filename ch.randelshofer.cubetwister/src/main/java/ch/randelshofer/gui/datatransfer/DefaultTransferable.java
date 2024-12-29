/*
 * @(#)DefaultTransferable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import org.jhotdraw.annotation.Nonnull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
/**
 * A Transferable which uses a char array or a byte array as its data source.
 *
 * @author  Werner Randelshofer
 */
public class DefaultTransferable implements Transferable {
    private byte[] data;
    private DataFlavor flavor;

    /**
     * Creates a new instance using a String as the data source.
     * The charset parameter of the mimetype is used to convert the chars
     * into bytes.
     * If no charset parameter is specified <code>;charset="UTF-8"</code> is
     * added and the data is encoded using UTF-8.
     */
    public DefaultTransferable(@Nonnull String data, @Nonnull String mimetype, String description) {
        this(data.toCharArray(), mimetype, description);
    }

    /**
     * Creates a new instance using a char array as the data source.
     * The charset parameter of the mimetype is used to convert the chars
     * into bytes.
     * If no charset parameter is specified <code>;charset="UTF-8"</code> is
     * added and the data is encoded using UTF-8.
     */
    public DefaultTransferable(@Nonnull char[] chars, @Nonnull String mimetype, String description) {
        try {
            MimeType mt = new MimeType(mimetype);
            String charset = mt.getParameter("charset");
            if (charset == null) {
                charset = "UTF-8";
                mt.setParameter("charset", charset);
            }
            this.data = new String(chars).getBytes(charset);
            this.flavor = new DataFlavor(mt.toString(), description);
        } catch (Exception e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
    }

    /**
     * Creates a new instance using a byte array as the data source.
     */
    public DefaultTransferable(byte[] data, @Nonnull String mimetype, String description) {
        this.data = data;
        this.flavor = new DataFlavor(mimetype, description);
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
    @Nonnull
    public Object getTransferData(@Nonnull DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(this.flavor)) {
            //return new CharArrayReader(data);
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
    @Nonnull
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {flavor};
    }

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * @param flavor the requested flavor for the data
     * @return boolean indicating wjether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(@Nonnull DataFlavor flavor) {
        return flavor.equals(this.flavor);
    }

}

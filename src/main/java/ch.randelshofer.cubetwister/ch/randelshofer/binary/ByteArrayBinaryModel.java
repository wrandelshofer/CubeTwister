/*
 * @(#)ByteArrayBinaryModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.binary;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Model for untyped binary data.
 *
 * @author Werner Randelshofer
 * @version  $Id: ByteArrayBinaryModel.java 331 2013-12-14 13:23:29Z werner $
 */
public class ByteArrayBinaryModel implements BinaryModel {
    // The data is stored in runs of 256 bytes. So we do not
    // need a contiguous area of memory.

    /** Table of elements. */
    @Nullable
    private ArrayList<byte[]> elemTable;
    /** Number of bytes in the model. */
    private long length;
    /** Size of an element. */
    private int elemSize = 1024;

    public ByteArrayBinaryModel() {
        elemTable = new ArrayList<byte[]>();
        length = 0;
    }

    public ByteArrayBinaryModel(@Nullable byte[] data) {
        elemTable = new ArrayList<byte[]>();
        if (data == null || data.length == 0) {
            length = 0;
        } else {
            elemTable.add(data);
            length = elemSize = data.length;
        }
    }

    public ByteArrayBinaryModel(@Nonnull InputStream in)
            throws IOException {
        this();

        //in = new BufferedInputStream(in);

        byte[] elem = new byte[elemSize];
        int elemLen = 0;
        while (true) {
            int readLen = in.read(elem, elemLen, elemSize - elemLen);
            if (readLen == -1) {
                elemTable.add(elem);
                length += elemLen;
                break;
            }
            elemLen += readLen;
            if (elemLen == elemSize) {
                elemTable.add(elem);
                length += elemSize;
                elem = new byte[elemSize];
                elemLen = 0;
            }
        }
    }

    public long getLength() {
        return length;
    }

    /**
    Gets a sequence of bytes and copies them into the supplied byte array.

    @param offset the starting offset >= 0
    @param len the number of bytes >= 0 && <= size - offset
    @param target the target array to copy into
    @exception ArrayIndexOutOfBoundsException  Thrown if the area covered by
    the arguments is not contained in the model.
     */
    @Override
    public int getBytes(long offset, int len, byte[] target) {
        int off = (int) offset;
        if (len + offset > length) {
            len = (int) (length - offset);
        }

        // Compute the index of the element
        int index = off / elemSize;

        // Get the element.
        byte[] elem = elemTable.get(index);

        // Count the number of bytes we transfer
        int count = 0;

        // Current index within the element
        int i = off % elemSize;

        // Copy until we are finished
        while (count < len) {
            if (i == elem.length) {
                elem = elemTable.get(++index);
                i = 0;
            }
            target[count++] = elem[i++];
        }
        return count;
    }

    @Override
    public void close() {
        elemTable=null;
        length=0;
    }
}

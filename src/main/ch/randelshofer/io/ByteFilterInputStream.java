/*
 * @(#)ByteFilterInputStream.java  1.0  April 23, 2004
 *
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */


package ch.randelshofer.io;

import java.io.*;
/**
 *
 * @author  werni
 */
public class ByteFilterInputStream extends FilterInputStream {    
    /** Creates a new instance of ByteFilterInputStream */
    public ByteFilterInputStream(InputStream in) {
        super(in);
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        int count = in.read(b, off, len);
        if (count > 0) {
            for (int i=off; i < off + count; i++) {
                if (b[i] == 0x0b) b[i] = 0x20;
            }
        }
        return count;
    }
}

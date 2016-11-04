/*
 * @(#)LFWriter.java  1.1  2004-04-17
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.io;

import java.io.*;

/**
 * A character-output stream that converts line terminators into a configurable
 * line separator sequence.mbers.  A line is considered to be terminated by
 * any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return
 * followed immediately by a linefeed.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.0.1 2004-02-14 Method write accidentaly suppressed a line feed
 * if it was the last character in the supplied data.
 * <br>1.0 2002-02-13 Created.
 */
public class LFWriter extends FilterWriter {
    /**
     * Line separator string.
     */
    private String lineSeparator = "\n";
    
    /** If the next character is a line feed, skip it */
    private boolean skipLF;
    
    /**
     * Create a new line-numbering writer.
     */
    public LFWriter(Writer out) {
        super(out);
        lineSeparator = System.getProperty("line.separator");
    }
    /**
     * Create a new line-numbering writer.
     */
    public LFWriter(Writer out, String lineSeparator) {
        super(out);
        this.lineSeparator = lineSeparator;
    }
    
    /**
     * Gets the line separator of the println() methods.
     */
    public String getLineSeparator() {
        return lineSeparator;
    }
    
    /**
     * Sets the line separator for the println() methods.
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }
    
    /**
     * Write a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
        switch (c) {
            case '\r':
                out.write(lineSeparator);
                skipLF = true;
                break;
            case '\n':
                if (!skipLF) out.write(lineSeparator);
                skipLF = false;
                break;
            default :
                out.write(c);
                skipLF = false;
                break;
        }
    }
    
    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        int end = off + len;
        for (int i=off; i < end; i++) {
            switch (cbuf[i]) {
                case '\r':
                    out.write(cbuf, off, i - off);
                    off = i + 1;
                    out.write(lineSeparator);
                    skipLF = true;
                    break;
                case '\n':
                    out.write(cbuf, off, i - off);
                    off = i + 1;
                    if (skipLF) {
                        skipLF = false;
                    } else {
                        out.write(lineSeparator);
                    }
                    break;
                default :
                    skipLF = false;
                    break;
            }
        }
        if (off < end) out.write(cbuf, off, end - off);
    }
    
    /**
     * Write a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }
}

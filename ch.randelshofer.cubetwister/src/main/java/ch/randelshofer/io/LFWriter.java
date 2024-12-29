/*
 * @(#)LFWriter.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.io;

import org.jhotdraw.annotation.Nonnull;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A character-output stream that converts line terminators into a configurable
 * line separator sequence.mbers.  A line is considered to be terminated by
 * any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return
 * followed immediately by a linefeed.
 *
 * @author  Werner Randelshofer
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
    public LFWriter(@Nonnull Writer out) {
        super(out);
        lineSeparator = System.getProperty("line.separator");
    }

    /**
     * Create a new line-numbering writer.
     */
    public LFWriter(@Nonnull Writer out, String lineSeparator) {
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
     * @param cbuf Buffer of characters to be written
     * @param off  Offset from which to start reading characters
     * @param len  Number of characters to be written
     * @throws IOException If an I/O error occurs
     */
    public void write(@Nonnull char cbuf[], int off, int len) throws IOException {
        int end = off + len;
        for (int i = off; i < end; i++) {
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
     * @exception IOException  If an I/O error occurs
     */
    public void write(@Nonnull String str, int off, int len) throws IOException {
        write(str.toCharArray(), off, len);
    }
}

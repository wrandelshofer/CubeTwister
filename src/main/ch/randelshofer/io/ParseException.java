/*
 * @(#)ParseException.java  1.0  2001-02-24
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.io;

import java.io.IOException;
/**
 * This exception is thrown by ScriptParser, when it
 * encounters an invalid token.
 *
 * @author Werner Randelshofer
 * @version 1.0 2001-02-24
 */
public class ParseException
extends IOException {
    private final static long serialVersionUID = 1L;
    /** Start position of the invalid token. */
    private int startpos;
    /** End position of the invalid token. */
    private int endpos;

    /**
     * Constructs a ParseException with the specified detail message.
     * Start and end position are set to 0.
     *
     * @param msg the detail message.
     */
    public ParseException(String msg) {
        this(msg, 0, 0);
    }
    /**
     * Constructs a ParseException with the specified detail message.
     *
     * @param msg the detail message.
     * @param startpos the start position of the invalid token.
     * @param endpos the end position of the invalid token.
     */
    public ParseException(String msg, int startpos, int endpos) {
        super(msg);
        this.startpos = startpos;
        this.endpos = endpos;
    }

    /**
     * Returns the start position of the invalid token.
     */
    public int getStartPosition() {
        return startpos;
    }

    /**
     * Returns the end position of the invalid token.
     */
    public int getEndPosition() {
        return endpos;
    }

}

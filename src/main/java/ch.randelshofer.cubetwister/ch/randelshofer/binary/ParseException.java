/*
 * @(#)ParseException.java  
 *
 * Copyright (c) 1999-2012 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.binary;

/**
 * Exception thrown by IFFParse.
 * 
 * @author Werner Randelshofer
 * @version  $Id: ParseException.java 328 2013-12-07 16:42:19Z werner $
 */
public class ParseException extends Exception {

    public static final long serialVersionUID = 1L;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

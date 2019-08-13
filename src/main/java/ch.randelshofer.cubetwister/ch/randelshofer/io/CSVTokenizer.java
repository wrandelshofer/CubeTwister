/* @(#)CSVTokenizer.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.io;

import java.io.*;
/**
 * Parses a comma separated values (CSV) stream into tokens.
 * <p>
 * EBNF rules for the CSV format:
 * <pre>
 * CSV = Record {RecordSeparator, Record}
 * 
 * RecordSeparator = linebreak
 * Record = Field {FieldSeparator, Field}
 * 
 * FieldSeparator = {whitespace} comma {whitespace}
 * Field = UnquotedField | DQuotedField
 * 
 * UnquotedField = (simplechar) {{simplechar|space}, (simplechar)}
 * 
 * DQuotedField = dquote (simplechar|stuffeddquote|linebreak|comma} dquote
 * 
 * 
 * simplechar = (* every character except specialchar *)
 * specialchar = linebreak | comma | dquote | whitespace | space
 * linebreak = lf | cr | cr, lf
 * comma = ','
 * dquote = '"'
 * stuffeddquote = '""'
 * lf = 0x0a
 * cr = 0x0d
 * space = ' '
 * whitespace = ' ' | tab
 * tab = 0x07
 * 
 * </pre>
 * <p>
 * Simple Example with unquoted fields:
 * <pre>
 * Jacques, Mayol, Rue St. Claire 8, Antibes
 * Enzo, Maiorca, Via Roma 2, Taormina
 * </pre>
 * Example with quoted fields:
 * <pre>
 * Trio, "Uno, due, tre!", Pop
 * Alice, "Did you go?
 * Did you stay?", Rock
 * The Pringles, "He said ""I like it""", Pop
 * </pre>
 *
 * @author  Werner Randelshofer
 */
public class CSVTokenizer {
    private Reader in;
    private int lineNumber = 0;
    
    private boolean pushedBack;
    /**
     * Delimiter character.
     */
    private char delimiterChar;
    
    /**
     * Quoting character.
     */
    private char quoteChar;
    
    /**
     * After a call to the <code>nextToken</code> method, this field
     * contains the type of the token just read. For a single character
     * token, its value is the single character, converted to an integer.
     * For a quoted string token (see , its value is the quote character.
     * Otherwise, its value is one of the following:
     * <ul>
     * <li><code>TT_VALUE</code> indicates that the token is a value.
     * <li><code>TT_EOL</code> indicates that the end of line has been read.
     * <li><code>TT_EOF</code> indicates that the end of the input stream
     *     has been reached.
     * </ul>
     * <p>
     * The initial value of this field is -4.
     *
     * @see     #nextToken()
     * @see     #TT_EOF
     * @see     #TT_EOL
     * @see     #TT_VALUE
     */
    public int ttype = TT_NOTHING;
    
    /**
     * A constant indicating that the end of the stream has been read.
     */
    public static final int TT_EOF = -1;
    
    /**
     * A constant indicating that the end of the line has been read.
     */
    public static final int TT_EOL = '\n';
    
    /**
     * A constant indicating that a delimiter token has been read.
     */
    public static final int TT_DELIMITER = -2;
    /**
     * A constant indicating that a word token has been read.
     */
    public static final int TT_VALUE = -3;
    
    /* A constant indicating that no token has been read, used for
     * initializing ttype.  FIXME This could be made public and
     * made available as the part of the API in a future release.
     */
    private static final int TT_NOTHING = -4;
    
    private char buf[] = new char[20];
    
    /** Creates a new instance. */
    public CSVTokenizer(Reader in) {
        this(in, ',', '"');
    }
    
    /**
     * If the current token is a value token, this field contains a
     * string giving the characters of the value token.
     * <p>
     * The current token is a value when the value of the
     * <code>ttype</code> field is <code>TT_VALUE</code>.
     * <p>
     * The initial value of this field is null.
     *
     * @see     #TT_VALUE
     * @see     #ttype
     */
    public static String value = null;
    
    
    /**
     * The next character to be considered by the nextToken method.  May also
     * be NEED_CHAR to indicate that a new character should be read, or SKIP_LF
     * to indicate that a new character should be read and, if it is a '\n'
     * character, it should be discarded and a second new character should be
     * read.
     */
    private int peekc = NEED_CHAR;
    
    private static final int NEED_CHAR = Integer.MAX_VALUE;
    private static final int SKIP_LF = Integer.MAX_VALUE - 1;
    
    /**
     * Creates a new instance.
     *
     * @param in reader from which to read.
     * @param delimiterChar The new delimiter character to use.
     * @param quoteChar The new character to use for quoting.
     * @throws IllegalArgumentException if one of the delimiters can not be used.
     */
    public CSVTokenizer(Reader in, char delimiterChar, char quoteChar) {
        
        this.in = in;
        this.delimiterChar = delimiterChar;
        this.quoteChar = quoteChar;
    }
    /**
     * Parses the next token from the input stream of this tokenizer.
     * The type of the next token is returned in the <code>ttype</code>
     * field. Additional information about the token may be in the
     * <code>nval</code> field or the <code>sval</code> field of this
     * tokenizer.
     * <p>
     * Typical clients of this
     * class first set up the syntax tables and then sit in a loop
     * calling nextToken to parse successive tokens until TT_EOF
     * is returned.
     *
     * @return     the value of the <code>ttype</code> field.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.StreamTokenizer#nval
     * @see        java.io.StreamTokenizer#sval
     * @see        java.io.StreamTokenizer#ttype
     */
    public int nextToken() throws IOException {
        if (pushedBack) {
            pushedBack = false;
            return ttype;
        }
        value = null;
        
        int c = peekc;
        if (c < 0)
            c = NEED_CHAR;
        if (c == SKIP_LF) {
            c = read();
            if (c < 0)
                return ttype = TT_EOF;
            if (c == '\n')
                c = NEED_CHAR;
        }
        if (c == NEED_CHAR) {
            c = read();
            if (c < 0)
                return ttype = TT_EOF;
        }
        ttype = c;		/* Just to be safe */
        
        /* Set peekc so that the next invocation of nextToken will read
         * another character unless peekc is reset in this invocation
         */
        peekc = NEED_CHAR;
        
        // Skip whitespace
        boolean whitespace = c != delimiterChar && Character.isWhitespace((char) c);
        while (whitespace) {
            if (c == '\r') {
                lineNumber++;
                peekc = SKIP_LF;
                return ttype = TT_EOL;
            } else {
                if (c == '\n') {
                    lineNumber++;
                    return ttype = TT_EOL;
                }
                c = read();
            }
            if (c < 0) {
                return ttype = TT_EOF;
            }
            whitespace = c != delimiterChar && Character.isWhitespace((char) c);
        }
        
        // Parse delimiter
        if (c == delimiterChar) {
            value = null;
            return ttype = TT_DELIMITER;
        }
        
        // Parse quoted value
        if (c == quoteChar) {
            int i = 0;
            while (true) {
                c = read();
                if (c == quoteChar) {
                    c = read();
                    if (c != quoteChar) break;
                } else if (c < 0) {
                    throw new IOException("Unexpected EOF in line "+(lineNumber+1)+" at quoted value \""+String.copyValueOf(buf, 0, i)+"\".");
                }

                if (i >= buf.length) {
                    char nb[] = new char[buf.length * 2];
                    System.arraycopy(buf, 0, nb, 0, buf.length);
                    buf = nb;
                }
                buf[i++] = (char) c;
            }
            peekc = c;
            value = String.copyValueOf(buf, 0, i);
            return ttype = TT_VALUE;
        }
        
        // Parse unquoted value
        int i = 0;
        int trailingWhitespaceCount = 0;
        do {
            if (i >= buf.length) {
                char nb[] = new char[buf.length * 2];
                System.arraycopy(buf, 0, nb, 0, buf.length);
                buf = nb;
            }
            buf[i++] = (char) c;
            if (c != delimiterChar &&Character.isWhitespace((char) c)) {
                trailingWhitespaceCount++;
            } else {
                trailingWhitespaceCount = 0;
            }
            c = read();
            if (c == quoteChar) {
                throw new IOException("Unexpected quote character in line "+(lineNumber+1)+" at unquoted value \""+String.copyValueOf(buf, 0, i)+"\".");
            }
        } while (c >= 0 && c != '\n' && c != '\r' && c != delimiterChar);
        peekc = c;
        value = String.copyValueOf(buf, 0, i - trailingWhitespaceCount);
        return ttype = TT_VALUE;
    }
    /**
     * Causes the next call to the <code>nextToken</code> method of this
     * tokenizer to return the current value in the <code>ttype</code>
     * field, and not to modify the value in the <code>value</code> field.
     *
     * @see     #nextToken()
     * @see     #value
     * @see     #ttype
     */
    public void pushBack() {
        if (ttype != TT_NOTHING)   /* No-op if nextToken() not called */
            pushedBack = true;
    }
    
    /** Read the next character */
    private int read() throws IOException {
        return in.read();
    }
    /**
     * Return the current line number.
     *
     * @return  the current line number of this stream tokenizer.
     */
    public int getLineNumber() {
        return lineNumber;
    }
}

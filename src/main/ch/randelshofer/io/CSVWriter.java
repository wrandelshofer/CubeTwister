/* @(#)CSVWriter.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.io;

import java.io.*;
/**
 * Write values into a comma separated value (CSV) stream.
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
 * Enzo, Maiorca, Via Greccio 2, Taormina
 * </pre>
 * Example with quoted fields:
 * <pre>
 * Alice, "Did you go?
 * Did you stay?", Rock
 * The Pringles, "He said ""I like it""", Pop
 * Trio, Uno, due, tre!, "Pop"
 * </pre>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.0  2004-04-18  Created.
 */
public class CSVWriter extends FilterWriter {
    /**
     * Delimiter character for values.
     */
    private char valueDelimiter;
    
    /**
     * Quoting character.
     */
    private char quoteChar;
    
    /**
     * Delimiter String for records.
     * This must be either cr, cr lf or lf.
     */
    private String recordDelimiter;
    
    private final static int NO_DELIMITER = 0;
    private final static int VALUE_DELIMITER = 1;
    private final static int RECORD_DELIMITER = 2;
    /**
     * Next delimiter to be written.
     */
    private int nextDelimiter = NO_DELIMITER;
    
    /** Creates a new instance. */
    public CSVWriter(Writer out) {
        this(out, ',', '"',"\n");
    }
    /**
     * Creates a new instance.
     *
     * @param out writer to which to print.
     * @param valueDelimiter The new delimiter character to use.
     * @param quoteChar The new character to use for quoting.
     * @param recordDelimiter The line break used for delimiting records. Must
     * be either cr, cr lf or lf.
     * @throws IllegalArgumentException if one of the delimiters can not be used.
     */
    public CSVWriter(Writer out, char valueDelimiter, char quoteChar, String recordDelimiter) {
        super(out);
        
        if (! "\n".equals(recordDelimiter) 
                && ! "\r".equals(recordDelimiter) 
                && ! "\r\n".equals(recordDelimiter)) {
            throw new IllegalArgumentException("Illegal record delimiter: \""+valueDelimiter+"\"");
        }
        
        
        this.valueDelimiter = valueDelimiter;
        this.quoteChar = quoteChar;
        this.recordDelimiter = recordDelimiter;
    }
    /**
     * Write a single int value and appends it as a new value to the
     * current record.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void writeInt(int i) throws IOException {
        write(Integer.toString(i));
    }
    
    /**
     * Writes a delimiter.
     */
    private void writeDelimiter() throws IOException {
        switch (nextDelimiter) {
            case NO_DELIMITER :
                break;
            case VALUE_DELIMITER :
                out.write(valueDelimiter);
                break;
            case RECORD_DELIMITER :
                out.write(recordDelimiter);
                break;
        }
    }
    
    /**
     * Write a single character value and appends it as a new value to the
     * current record.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
        writeDelimiter();
        nextDelimiter = VALUE_DELIMITER;
        
        if (c == '\n' || c == '\r' || c == valueDelimiter || Character.isWhitespace((char) c)) {
            out.write(quoteChar);
            out.write(c);
            out.write(quoteChar);
        } else if (c == quoteChar) {
            out.write(quoteChar);
            out.write(c);
            out.write(c);
            out.write(quoteChar);
        } else {
            out.write(c);
        }
    }
    
    /**
     * Write a portion of an array of characters and appends it as a new value
     * to the current record.
     *
     * @param  cbuf  Buffer of characters to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        writeDelimiter();
        nextDelimiter = VALUE_DELIMITER;
        
        if (len > 0) {
            boolean needsQuoting = Character.isWhitespace(cbuf[off])
            || Character.isWhitespace(cbuf[off + len - 1]);
            
            if (! needsQuoting) {
                for (int i=off, max=off+len; i < max; i++) {
                    char c = cbuf[i];
                    if (c == '\n' || c == '\r' || c == quoteChar || c == valueDelimiter) {
                        needsQuoting = true;
                        break;
                    }
                }
            }
            
            if (needsQuoting) {
                char[] qbuf = new char[len*2+2];
                int j=0;
                qbuf[j++] = quoteChar;
                for (int i=off, max=off+len; i < max; i++) {
                    char c = cbuf[i];
                    if (c == quoteChar) {
                        qbuf[j++] = quoteChar;
                        qbuf[j++] = quoteChar;
                    } else {
                        qbuf[j++] = c;
                    }
                }
                qbuf[j++] = quoteChar;
                out.write(qbuf, 0, j);
            } else {
                out.write(cbuf, off, len);
                out.write(valueDelimiter);
            }
        }
    }
    
    
    /**
     * Write a string.
     *
     * @param  str  String to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str) throws IOException {
        if (str == null) write((String) null, 0, 0);
        else write(str, 0, str.length());
    }
    
    /**
     * Write a portion of a string and appends it as a new value to the current
     * record.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start reading characters
     * @param  len  Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException {
        writeDelimiter();
        nextDelimiter = VALUE_DELIMITER;
        
        if (len > 0) {
            boolean needsQuoting = Character.isWhitespace(str.charAt(off))
            || Character.isWhitespace(str.charAt(off + len - 1));
            
            if (! needsQuoting) {
                for (int i=off, max=off+len; i < max; i++) {
                    char c = str.charAt(i);
                    if (c == '\n' || c == '\r' || c == quoteChar || c == valueDelimiter) {
                        needsQuoting = true;
                        break;
                    }
                }
            }
            
            if (needsQuoting) {
                char[] qbuf = new char[len*2+2];
                int j=0;
                qbuf[j++] = quoteChar;
                for (int i=off, max=off+len; i < max; i++) {
                    char c = str.charAt(i);
                    if (c == quoteChar) {
                        qbuf[j++] = quoteChar;
                        qbuf[j++] = quoteChar;
                    } else {
                        qbuf[j++] = c;
                    }
                }
                qbuf[j++] = quoteChar;
                out.write(qbuf, 0, j);
            } else {
                out.write(str, off, len);
            }
        }
    }
    
    /**
     * Finishes the current record by writing the record delimiter.
     */
    public void writeln() throws IOException {
        if (nextDelimiter == RECORD_DELIMITER) {
            out.write(recordDelimiter);
        }
        nextDelimiter = RECORD_DELIMITER;
    }
    /**
     * Writes a single record of comma separated values.
     * The values will be quoted if needed.  Quotes and
     * and other characters that need it will be escaped.
     *
     * @param values values to be outputted.
     * @throws IOException if an error occurs while writing.
     *
     * @since ostermillerutils 1.02.26
     */
    public void writeln(String[] values) throws IOException {
        for (int i=0; i < values.length; i++){
            write(values[i]);
        }
        writeln();
    }
}
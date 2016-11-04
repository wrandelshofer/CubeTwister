/*
 * @(#)CSVReader.java  1.0  April 18, 2004
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.io;

import java.io.*;
import java.util.*;
/**
 * Reads values from a comma separated (CSV) stream.
 * <p>
 * EBNF rules for the CSV format:
 * <pre>
 * CSV = Record {RecordSeparator, Record}
 * 
 * Record = Field {FieldSeparator, Field}
 * RecordSeparator = linebreak
 * 
 * Field = UnquotedField | DQuotedField
 * FieldSeparator = {whitespace} comma {whitespace}
 * 
 * UnquotedField = (simplechar) {{simplechar|space}, (simplechar)}
 * 
 * DQuotedField = dquote (simplechar|stuffeddquote|linebreak|comma} dquote
 * 
 * 
 * simplechar = (* every character except specialchar *)
 * specialchar = linebreak | comma | dquote | whitespace
 * linebreak = lf | cr | cr, lf
 * comma = ','
 * dquote = '"'
 * stuffeddquote = '""'
 * lf = 0x0a
 * cr = 0x0d
 * whitespace = space | tab
 * space = ' '
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
 * @version $Id$
 * <br>1.0  2004-04-18  Created.
 */
public class CSVReader {
    private Reader in;
    private CSVTokenizer tt;
    private ArrayList<String> record;
    
    /** Creates a new instance. */
    public CSVReader(Reader in, char delimiterChar, char quoteChar) {
        this.in = in;
        tt = new CSVTokenizer(in, delimiterChar, quoteChar);
        record = new ArrayList<String>();
    }
    
    /**
     * Returns null, if EOF has been reached.
     */
    public String[] readln() throws IOException {
        record.clear();
        
        if (tt.nextToken() == CSVTokenizer.TT_EOF) {
            return null;
        }
        tt.pushBack();
        
        while (tt.nextToken() != CSVTokenizer.TT_EOF && tt.ttype != CSVTokenizer.TT_EOL) {
            if (tt.ttype == CSVTokenizer.TT_DELIMITER) {
                record.add("");
            } else if (tt.ttype == CSVTokenizer.TT_VALUE) {
                record.add(tt.value);
                switch (tt.nextToken()) {
                case CSVTokenizer.TT_DELIMITER :
                    // nothing to do, we simply consume the delimiter
                    break;
                case CSVTokenizer.TT_EOL :
                case CSVTokenizer.TT_EOF :
                    tt.pushBack();
                    break;
                default :
                    throw new IOException("Unexpected token "+tt.ttype+" in line "+tt.getLineNumber());
                }
            }
        }
        
        return record.toArray(new String[record.size()]);
    }
    
    public void close() throws IOException {
        in.close();
    }
}

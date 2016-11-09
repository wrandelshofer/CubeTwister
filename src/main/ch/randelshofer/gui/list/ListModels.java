/* @(#)ListModels.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.list;

import ch.randelshofer.gui.datatransfer.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;
import javax.swing.ListModel;
/**
 * This class provides static utility operations for
 * <code>MutableListModel</code>'s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 2002-11-21 Reworked.
 * <br>0.1 2002-04-28 Created.
 */
public class ListModels {
    
    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private ListModels() {
    }
    
    /**
     * Creates a transferable in a number of default formats for a ListModel.
     *
     * @return A transferable for a list model.
     */
    public static Transferable createDefaultTransferable(ListModel model, int[] indices) {
        CompositeTransferable t = new CompositeTransferable();
        t.add(createLocalTransferable(model, indices, Object.class));
        t.add(createHTMLTransferable(model, indices));
        t.add(createPlainTransferable(model, indices));
        return t;
    }
    /**
     * Creates a transferable in text/html format from
     * a mutable list model.
     *
     * @return A transferable of type text/html
     */
    public static Transferable createHTMLTransferable(ListModel model, int[] indices) {
        CharArrayWriter w = new CharArrayWriter();
        try {
            w.write("<html><body><ol>");
            for (int i=0; i < indices.length; i++) {
                Object elem = model.getElementAt(indices[i]);
                w.write("<li>");
                writeHTMLEncoded(w, elem.toString());
                w.write("</li>");
            }
            w.write("</ol></body></html>");
            w.close();
        } catch (IOException e) {
            throw new InternalError(e.toString());
        }
        return new CharArrayReaderTransferable(w.toCharArray(), "text/html", "HTML");
    }
    
    private static void writeHTMLEncoded(Writer w, String str) throws IOException  {
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '&' :
                    w.write("&amp;");
                    break;
                case '<' :
                    w.write("&lt;");
                    break;
                case '>' :
                    w.write("&gt;");
                    break;
                default :
                    w.write(ch);
                    break;
            }
        }
    }
    
    /**
     * Creates a transferable in text/plain format from
     * a mutable list model.
     *
     * @return A transferable of type java.awt.datatransfer.StringSelection
     */
    public static Transferable createPlainTransferable(ListModel model, int[] indices) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < indices.length; i++) {
            if (i != 0) buf.append('\n');
            buf.append(model.getElementAt(indices[i]).toString());
        }
        return new StringSelection(buf.toString());
    }
    
    /**
     * Creates a local JVM transferable from
     * a mutable list model.
     *
     * @return A JVM local object transferable of type java.util.LinkedList if
     * indices.length &gt; 1. A JVM local object transferable of type
     * model.getElementAt(indices[0]).getClass() if indices.length = 1.
     */
    public static Transferable createLocalTransferable(ListModel model, int[] indices, Class<?> baseclass) {
        if (indices.length == 1) {
            return new JVMLocalObjectTransferable(baseclass, model.getElementAt(indices[0]));
        } else {
            LinkedList<Object> list = new LinkedList<Object>();
            for (int i = 0; i < indices.length; i++) {
                list.add(model.getElementAt(indices[i]));
            }
            return new JVMLocalObjectTransferable(List.class, list);
        }
    }
    
    /**
     * Creates a Java file list transferable from
     * a mutable list model.
     *
     * @return A Java filelist transferable.
     */
    public static Transferable createFileListTransferable(
            MutableListModel model, int[] indices) {
            LinkedList<File> list = new LinkedList<File>();
            for (int i = 0; i < indices.length; i++) {
                list.add((File)model.getElementAt(indices[i]));
            }
           return new FileListTransferable(list);
        }
    
    
    /**
     * Returns the contents of the transferable as
     * a list of strings. Where each string is determined
     * by reading a line of text from the transferable.
     *
     * @exception UnsupportedFlavorException
     * If the transferable does not support <code>DataFlavor.stringFlavor</code>
     */
    public static LinkedList<String> getStringList(Transferable t)
    throws UnsupportedFlavorException, IOException {
        LinkedList<String> list = new LinkedList<String>();
        StringTokenizer scanner = new StringTokenizer((String) t.getTransferData(DataFlavor.stringFlavor), "\n");
        while (scanner.hasMoreTokens()) {
            list.add(scanner.nextToken());
        }
        return list;
    }
    
    /**
     * Returns the contents of the transferable as
     * a list of strings. Where each string is determined
     * by reading a line of text from the transferable
     *
     * @exception UnsupportedFlavorException
     * If the transferable does not support 
     * <code>DataFlavor.getTextPlainUnicodeFlavor()</code>
     */
    public static LinkedList<String> getPlainList(Transferable t)
    throws UnsupportedFlavorException, IOException {
        LinkedList<String> list = new LinkedList<String>();
        BufferedReader in = new BufferedReader(DataFlavor.getTextPlainUnicodeFlavor().getReaderForText(t));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
        } finally {
            in.close();
        }
        return list;
    }
}

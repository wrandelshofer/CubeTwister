/*
 * @(#)ListModels.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.list;

import ch.randelshofer.gui.datatransfer.CharArrayReaderTransferable;
import ch.randelshofer.gui.datatransfer.CompositeTransferable;
import ch.randelshofer.gui.datatransfer.FileListTransferable;
import ch.randelshofer.gui.datatransfer.JVMLocalObjectTransferable;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.ListModel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
/**
 * This class provides static utility operations for
 * <code>MutableListModel</code>'s.
 *
 * @author Werner Randelshofer
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
    @Nonnull
    public static Transferable createDefaultTransferable(@Nonnull ListModel model, @Nonnull int[] indices) {
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
    @Nonnull
    public static Transferable createHTMLTransferable(@Nonnull ListModel model, @Nonnull int[] indices) {
        CharArrayWriter w = new CharArrayWriter();
        try {
            w.write("<html><body><ol>");
            for (int i = 0; i < indices.length; i++) {
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

    private static void writeHTMLEncoded(@Nonnull Writer w, @Nonnull String str) throws IOException {
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '&':
                    w.write("&amp;");
                    break;
                case '<':
                    w.write("&lt;");
                    break;
                case '>':
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
    @Nonnull
    public static Transferable createPlainTransferable(@Nonnull ListModel model, @Nonnull int[] indices) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < indices.length; i++) {
            if (i != 0) {
                buf.append('\n');
            }
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
    @Nonnull
    public static Transferable createLocalTransferable(@Nonnull ListModel model, @Nonnull int[] indices, Class<?> baseclass) {
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
    @Nonnull
    public static Transferable createFileListTransferable(
            @Nonnull MutableListModel model, @Nonnull int[] indices) {
        LinkedList<File> list = new LinkedList<File>();
        for (int i = 0; i < indices.length; i++) {
            list.add((File) model.getElementAt(indices[i]));
        }
        return new FileListTransferable(list);
    }


    /**
     * Returns the contents of the transferable as
     * a list of strings. Where each string is determined
     * by reading a line of text from the transferable.
     *
     * @throws UnsupportedFlavorException If the transferable does not support <code>DataFlavor.stringFlavor</code>
     */
    @Nonnull
    public static LinkedList<String> getStringList(@Nonnull Transferable t)
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
    @Nonnull
    public static LinkedList<String> getPlainList(@Nonnull Transferable t)
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

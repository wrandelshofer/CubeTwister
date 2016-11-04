/**
 * @(#)MutableTableTransferable.java  1.0  Mar 21, 2008
 *
 * Copyright (c) 2010 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.gui.table;

import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.JTable;

/**
 * The MutableTableTransferable is a proxy for the actual Transferable of a JTable,
 * plus a list of indexes of the transferred rows.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-01-09 Created.
 */
public class MutableTableTransferable implements Transferable {
    private JTable table;
    private Transferable target;
    private int[] indices;
    
    public MutableTableTransferable(JTable source, int[] indices, Transferable target) {
        this.table = source;
        this.indices = indices;
        this.target = target;
    }
    
    public JTable getSource() {
        return table;
    }
    public int[] getTransferedIndices() {
        return indices;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return target.getTransferDataFlavors();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return target.isDataFlavorSupported(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return target.getTransferData(flavor);
    }

}

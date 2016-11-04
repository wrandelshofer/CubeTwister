/**
 * @(#)MutableListTransferable.java  1.0  Mar 21, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.gui.list;

import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.JList;

/**
 * The MutableListTransferable is a proxy for the actual Transferable of a JList,
 * plus a list of indexes of the transferred rows.
 *
 * @author Werner Randelshofer
 * @version 1.0 Mar 21, 2008 Created.
 */
public class MutableListTransferable implements Transferable {
    private JList list;
    private Transferable target;
    private int[] indices;
    
    public MutableListTransferable(JList source, int[] indices, Transferable target) {
        this.list = source;
        this.indices = indices;
        this.target = target;
    }
    
    public JList getSource() {
        return list;
    }
    public int[] getTransferedIndices() {
        return indices;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return target.getTransferDataFlavors();
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return target.isDataFlavorSupported(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return target.getTransferData(flavor);
    }

}

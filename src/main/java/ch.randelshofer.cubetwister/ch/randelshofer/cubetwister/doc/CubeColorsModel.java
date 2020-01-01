/* @(#)CubeColorsModel.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.datatransfer.XMLTransferable;
import ch.randelshofer.xml.NanoXMLDOMInput;
import ch.randelshofer.xml.NanoXMLDOMOutput;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.tree.MutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
/**
 * Holds a collection of CubeColorModel as its children. CubeColorsModel is
 * a child of CubeModel.
 * 
 * @author Werner Randelshofer
 */
public class CubeColorsModel extends EntityModel {
        private final static long serialVersionUID = 1L;

    /** Creates a new instance. */
    public CubeColorsModel() {
    }

    /**
     * Creates a new EntityModel.
     */
    public CubeColorsModel(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Nonnull
    public Transferable exportTransferable(@Nonnull MutableTreeNode[] nodes) {
        NanoXMLDOMOutput out = new NanoXMLDOMOutput(new CubeMarkupDOMFactory());
        out.addElement("CubeMarkup_Colors");
        for (int i = 0; i < nodes.length; i++) {
            out.writeObject((CubeColorModel) nodes[i]);
        }
        out.closeElement();


        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {

            Writer writer = new OutputStreamWriter(buf, "UTF8");
            out.save(writer);
            writer.close();
        } catch (Exception e) {
            throw new InternalError();
        }

        XMLTransferable transfer = new XMLTransferable(buf.toByteArray(), "text/xml", "Cube Twister Markup");
        return transfer;
    }

    public int importTransferable(@Nonnull Transferable transfer, int action, MutableTreeNode parent, int index)
            throws UnsupportedFlavorException, IOException {
        DocumentModel document = getDocument();
        DataFlavor flavor = new DataFlavor("text/xml", "Cube Twister Markup");
        //Transferable transfer = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
        if (transfer.isDataFlavorSupported(flavor)) {
            NanoXMLDOMInput in = new NanoXMLDOMInput(new CubeMarkupDOMFactory(), (InputStream) transfer.getTransferData(flavor));
            in.openElement(0);
            if ("CubeMarkup_Colors".equals(in.getTagName())) {
                for (int i = 0, n = in.getElementCount(); i < n; i++) {
                    CubeColorModel colorModel = (CubeColorModel) in.readObject(i);
                    document.insertNodeInto(colorModel, this, index + i);
                }
                
            }
            return in.getElementCount();
        } else {
            return 0;
        }
    }
}

/*
 * @(#)ImageWellTransferable.java  1.0  January 7, 2006
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
/**
 * ImageWellTransferable.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ImageWellTransferable implements Transferable {
    //private ImageWellModel model;
    private Image image;
    private byte[] imageData;
    private DataFlavor imageDataFlavor;
    
    /**
     * Creates a new instance.
     */
    public ImageWellTransferable(ImageWellModel model) {
        // FIXME - We should copy the image data
        image = model.getImage();
        imageData = model.getBinaryImage();
        if (imageData != null) {
            try {
                ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(imageData));
                for (Iterator<ImageReader> i=ImageIO.getImageReaders(in); i.hasNext(); ) {
                    ImageReader reader = i.next();
                    imageDataFlavor = new DataFlavor("image/"+reader.getFormatName(),reader.getFormatName());
                    break;
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return image;
        } else if (flavor.equals(imageDataFlavor)) {
            return new ByteArrayInputStream(imageData);
        }
        throw new UnsupportedFlavorException(flavor);
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DataFlavor.imageFlavor, imageDataFlavor };
    }
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor) || flavor.equals(imageDataFlavor);
    }
    
}

/*
 * @(#)ImageWellTransferable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
/**
 * ImageWellTransferable.
 *
 * @author  Werner Randelshofer
 */
public class ImageWellTransferable implements Transferable {
    //private ImageWellModel model;
    private Image image;
    private byte[] imageData;
    private DataFlavor imageDataFlavor;

    /**
     * Creates a new instance.
     */
    public ImageWellTransferable(@Nonnull ImageWellModel model) {
        // FIXME - We should copy the image data
        image = model.getImage();
        imageData = model.getBinaryImage();
        if (imageData != null) {
            try {
                ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(imageData));
                for (Iterator<ImageReader> i = ImageIO.getImageReaders(in); i.hasNext(); ) {
                    ImageReader reader = i.next();
                    imageDataFlavor = new DataFlavor("image/" + reader.getFormatName(), reader.getFormatName());
                    break;
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nonnull
    public Object getTransferData(@Nonnull DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return image;
        } else if (flavor.equals(imageDataFlavor)) {
            return new ByteArrayInputStream(imageData);
        }
        throw new UnsupportedFlavorException(flavor);
    }

    @Nonnull
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DataFlavor.imageFlavor, imageDataFlavor };
    }

    public boolean isDataFlavorSupported(@Nonnull DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor) || flavor.equals(imageDataFlavor);
    }

}

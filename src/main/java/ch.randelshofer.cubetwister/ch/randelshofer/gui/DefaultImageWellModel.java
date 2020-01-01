/* @(#)DefaultImageWellModel.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import base64.Base64;
import ch.randelshofer.beans.AbstractStateModel;
import org.jhotdraw.annotation.Nullable;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * DefaultImageModel.
 *
 * @author  Werner Randelshofer
 */
public class DefaultImageWellModel extends AbstractStateModel implements ImageWellModel {

    /** Image. */
    @Nullable
    private Image image;
    /** Binary Image data, */
    @Nullable
    private byte[] binary;
    /** Base64 Image data. We may discard this data in favor of the binary data.
     * This is conversion from/to binary data is lossless, and binary data is
     * more memory efficient than Base64.
     */
    @Nullable
    private String base64;

    /**
     * Creates a new instance.
     */
    public DefaultImageWellModel() {
    }

    /**
     * Sets the image.
     */
    public void setImage(@Nullable Image newValue) {
        if (newValue == null) {
            new Throwable().printStackTrace();
        }
        this.image = newValue;
        binary = null;
        base64 = null;
        fireStateChanged();
    }

    /**
     * Sets the image data.
     */
    public void setBinaryImage(final byte[] data) {
        this.binary = data;
        image = null;
        base64 = null;
        fireStateChanged();
    }

    /**
     * Sets the image data.
     */
    public void setImage(final byte[] data, Image renderedImage) {
        this.binary = data;
        image = renderedImage;
        base64 = null;
        fireStateChanged();
    }

    public void setBase64Image(String base64) {
        this.base64 = base64;
        this.image = null;
        this.binary = null;
        fireStateChanged();
    }

    /**
     * Gets the image.
     */
    @Nullable
    public Image getImage() {
        if (image == null) {
            try {
                byte[] b = getBinaryImage();
                if (b == null) {
                    return null;
                }
                /*
                ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(b));
                for (Iterator i=ImageIO.getImageReaders(in); i.hasNext(); ) {
                ImageReader reader = (ImageReader) i.next();
                }*/
                image = ImageIO.read(new ByteArrayInputStream(b));
            } catch (Throwable e) {
                binary = null;
                e.printStackTrace();
            }
        }
        return image;
    }

    /**
     * Gets the image data.
     */
    @Nullable
    public byte[] getBinaryImage() {
        if (binary == null) {
            if (base64 != null) {
                binary = Base64.decode(base64);
                //base64 = null; // discard this data to save memory.
            } else if (image != null) {
                RenderedImage renderedImage = Images.toBufferedImage(image);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    ImageIO.write(renderedImage, "png", out);
                    return out.toByteArray();
                } catch (IOException e) {
                    image = null;
                    e.printStackTrace();
                }
            }
        }
        return binary;
    }

    @Nullable
    public String getBase64Image() {
        if (base64 == null) {
            base64 = Base64.encodeBytes(getBinaryImage());
        }
        return base64;
    }

    public boolean hasImage() {
        return image != null || binary != null || base64 != null;
    }
}

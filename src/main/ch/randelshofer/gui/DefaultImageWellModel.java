/* @(#)DefaultImageWellModel.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui;

import ch.randelshofer.util.*;
import ch.randelshofer.beans.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import base64.*;

/**
 * DefaultImageModel.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.1 2008-01-03 Removed unnecessary code in method getImage().
 * <br>1.0 January 6, 2006 Created.
 */
public class DefaultImageWellModel extends AbstractStateModel implements ImageWellModel {

    /** Image. */
    private Image image;
    /** Binary Image data, */
    private byte[] binary;
    /** Base64 Image data. We may discard this data in favor of the binary data.
     * This is conversion from/to binary data is lossless, and binary data is
     * more memory efficient than Base64.
     */
    private String base64;

    /**
     * Creates a new instance.
     */
    public DefaultImageWellModel() {
    }

    /**
     * Sets the image.
     */
    public void setImage(Image newValue) {
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

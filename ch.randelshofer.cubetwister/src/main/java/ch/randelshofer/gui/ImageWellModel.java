/*
 * @(#)ImageWellModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import javax.swing.event.ChangeListener;
import java.awt.Image;
/**
 * Model for JImageWell component.
 *
 * @author  Werner Randelshofer
 */
public interface ImageWellModel {
    /**
     * Sets the image.
     */
    public void setImage(Image newValue);
    /**
     * Sets the image.
     */
    public void setImage(byte[] binary, Image renderedImage);
    /**
     * Gets the image.
     */
    public Image getImage();
    /**
     * Alternative image setter.
     * Sets the image using binary encoded data.
     */
    public void setBinaryImage(byte[] binary);
    /**
     * Alternative image getter.
     */
    public byte[] getBinaryImage();
    /**
     * Alternative image setter.
     * Sets the image using base64 encoded data.
     */
    public void setBase64Image(String base64);
    /**
     * Alternative image getter.
     */
    public String getBase64Image();
    /**
     * Returns true the model holds an image, i.e. is not null.
     */
    public boolean hasImage();

    public void addChangeListener(ChangeListener l);
    public void removeChangeListener(ChangeListener l);

}

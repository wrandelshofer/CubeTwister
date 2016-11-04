/*
 * @(#)ImageWellModel.java  1.1  2010-01-01
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 * Model for JImageWell component.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.0 January 6, 2006 Created.
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

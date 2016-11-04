/*
 * @(#)LazyImageIcon.java  1.2 2011-02-02
 * Copyright (c) 2007 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import ch.randelshofer.quaqua.util.Images;
import java.awt.*;
import java.awt.image.*;
import java.net.URL;
import javax.swing.*;
import org.jhotdraw.gui.Worker;

/**
 * LazyImageIcon.
 *
 * @author Werner Randelshofer
 * @version 1.2 2011-02-02 Removes SwingWorker.
 * <br>1.1 2010-08-18 Adds xOffset and yOffset parameters.
 * <br>1.0 January 12, 2007 Created.
 */
public class LazyImageIcon extends ImageIcon {
    private final static long serialVersionUID = 1L;
    private BufferedImage bufferedImage;
    private Worker<BufferedImage> worker;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    
    /** Creates a new instance. */
    public LazyImageIcon(final Image image, int width, int height) {
            this(image, width, height,0,0);
            }
    public LazyImageIcon(final Image image, int width, int height, int xOffset, int yOffset) {
        this.width = width;
        this.height = height;
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        worker = new Worker<BufferedImage>() {
            @Override
            public BufferedImage construct() {
                return Images.toBufferedImage(image);
            }
        };
        worker.start();
    }
    public LazyImageIcon(final URL image, int width, int height) {
            this(image, width, height,0,0);

    }
    public LazyImageIcon(final URL image, int width, int height, int xOffset, int yOffset) {
        this.width = width;
        this.height = height;
        this.xOffset=xOffset;
        this.yOffset=yOffset;
        worker = new Worker<BufferedImage>() {
            @Override
            public BufferedImage construct() {
                return  Images.toBufferedImage(Toolkit.getDefaultToolkit().createImage(image));
            }
        };
        worker.start();
    }
    
    @Override
    public Image getImage() {
        if (bufferedImage == null) {
            bufferedImage = worker.getValue();
        }
        return bufferedImage;
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(getImage(), x+xOffset, y+yOffset, c);
    }
    
    @Override
    public int getIconWidth() {
        return width;
    }
    
    @Override
    public int getIconHeight() {
        return height;
    }
}

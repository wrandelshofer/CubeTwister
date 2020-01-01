/* @(#)LazyImageIcon.java
 * Copyright (c) 2007 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.gui.Worker;

import javax.swing.ImageIcon;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * LazyImageIcon.
 *
 * @author Werner Randelshofer
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
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        worker = new Worker<BufferedImage>() {
            @Override
            public BufferedImage construct() {
                return Images.toBufferedImage(Toolkit.getDefaultToolkit().createImage(image));
            }
        };
        worker.start();
    }

    public LazyImageIcon(@Nonnull final InputStream image, int width, int height, int xOffset, int yOffset) {
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        worker = new Worker<BufferedImage>() {
            @Override
            public BufferedImage construct() {
                try (image) {
                    return Images.toBufferedImage(Toolkit.getDefaultToolkit().createImage(image.readAllBytes()));
                } catch (IOException e) {
                    return null;
                }
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
    public void paintIcon(Component c, @Nonnull Graphics g, int x, int y) {
        g.drawImage(getImage(), x + xOffset, y + yOffset, c);
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

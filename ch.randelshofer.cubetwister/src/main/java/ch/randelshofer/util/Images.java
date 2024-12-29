/*
 * @(#)Images.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Image processing methods.
 *
 * @author  Werner Randelshofer, Karl von Randow
 */
public class Images {

    /**
     * Prevent instance creation.
     */
    private Images() {
    }

    public static Image createImage(@Nonnull InputStream resource) throws IOException {
        Image image = Toolkit.getDefaultToolkit().createImage(resource.readAllBytes());
        return image;
    }

    @Nullable
    public static Image createImage(@Nonnull String moduleName, @Nonnull String location) {
        try (InputStream resource = ModuleLayer.boot().findModule(moduleName).get().getResourceAsStream(location);) {
            return createImage(resource);
        } catch (IOException e) {
            System.err.println("Warning: Images.createImage no resource found for " + moduleName + " " + location);
            return null;
        }
    }

    public static Image createImage(@Nonnull Class<?> baseClass, @Nonnull String location) {
        URL url = baseClass.getResource(location);
        if (url == null) {
            throw new IllegalArgumentException("Resource not found for " + baseClass + " " + location);
        }
        return createImage(url);
    }

    public static Image createImage(@Nullable URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found for " + resource);
        }
        Image image = Toolkit.getDefaultToolkit().createImage(resource);
        return image;
    }

    @Nullable
    public static BufferedImage toBufferedImage(RenderedImage rImg) {
        BufferedImage image;
        if (rImg instanceof BufferedImage) {
            image = (BufferedImage) rImg;
        } else {
            Raster r = rImg.getData();
            WritableRaster wr = WritableRaster.createWritableRaster(
                    r.getSampleModel(), null);
            rImg.copyData(wr);
            image = new BufferedImage(
                    rImg.getColorModel(),
                    wr,
                    rImg.getColorModel().isAlphaPremultiplied(),
                    null
            );
        }
        return image;
    }

    @Nullable
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;

        if (System.getProperty("java.version").startsWith("1.4.1_")) {
            // Workaround for Java 1.4.1 on Mac OS X.
            // For this JVM, we always create an ARGB image to prevent a class
            // cast exception in
            // sun.awt.image.BufImgSurfaceData.createData(BufImgSurfaceData.java:434)
            // when we attempt to draw the buffered image.
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        } else {
            // Determine if the image has transparent pixels; for this method's
            // implementation, see e661 Determining If an Image Has Transparent Pixels
            boolean hasAlpha;
            try {
                hasAlpha = hasAlpha(image);
            } catch (IllegalAccessError e) {
                // If we can't determine this, we assume that we have an alpha,
                // in order not to loose data.
                hasAlpha = true;
            }


            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {
                // Determine the type of transparency of the new buffered image
                int transparency = Transparency.OPAQUE;
                if (hasAlpha) {
                    transparency = Transparency.TRANSLUCENT;
                }

                // Create the buffered image
                GraphicsDevice gs = ge.getDefaultScreenDevice();
                GraphicsConfiguration gc = gs.getDefaultConfiguration();
                bimage = gc.createCompatibleImage(
                        image.getWidth(null), image.getHeight(null), transparency);
            } catch (Exception e) {
                //} catch (HeadlessException e) {
                // The system does not have a screen
            }

            if (bimage == null) {
                // Create a buffered image using the default color model
                int type = BufferedImage.TYPE_INT_RGB;
                if (hasAlpha) {
                    type = BufferedImage.TYPE_INT_ARGB;
                }
                bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
            }
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;

        // My own implementation:
        /*
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        } else {
            BufferedImage bufImg;
            Frame f = new Frame();
            f.pack();
            MediaTracker t = new MediaTracker(f);
            t.addImage(image, 0);
            try { t.waitForAll(); } catch (InterruptedException e) {}

            // Workaround for Java 1.4.1 on Mac OS X.
            if (System.getProperty("java.version").startsWith("1.4.1_")) {
                bufImg = new BufferedImage(image.getWidth(f), image.getHeight(f), BufferedImage.TYPE_INT_ARGB);
            } else {
                bufImg = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.TRANSLUCENT);
            }
            Graphics2D imgGraphics = bufImg.createGraphics();
            imgGraphics.drawImage(image, 0, 0, f);
            imgGraphics.dispose();
            f.dispose();
            return bufImg;
        }*/
    }

    /**
     * This method returns true if the specified image has transparent pixels
     *
     * Code taken from the Java Developers Almanac 1.4
     * http://javaalmanac.com/egs/java.awt.image/HasAlpha.html
     */
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        // We must check for null here, because the pixel grabber
        // may not have been able to retrieve the color model.
        ColorModel cm = pg.getColorModel();
        return (cm != null) ? cm.hasAlpha() : false;
    }

    /**
     * Splits an image into count subimages.
     */
    @Nonnull
    public static BufferedImage[] split(Image image, int count, boolean isHorizontal) {
        BufferedImage src = Images.toBufferedImage(image);
        if (count == 1) {
            return new BufferedImage[] { src };
        }

        BufferedImage[] parts = new BufferedImage[count];
        for (int i=0; i < count; i++) {
            if (isHorizontal) {
                parts[i] = src.getSubimage(
                        src.getWidth() / count * i, 0,
                        src.getWidth() / count, src.getHeight()
                        );
            } else {
                parts[i] = src.getSubimage(
                        0, src.getHeight() / count * i,
                        src.getWidth(), src.getHeight() / count
                        );
            }
        }
        return parts;
    }
}

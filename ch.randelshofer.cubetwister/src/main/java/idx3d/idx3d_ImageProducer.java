// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | -----------------------------------------------------------------
// | idx3d is a 3d engine written in 100% pure Java (1.1 compatible)
// | and provides a fast and flexible API for software 3d rendering
// | on the Java platform.
// |
// | Feel free to use the idx3d API / classes / source code for
// | non-commercial purposes (of course on your own risk).
// | If you intend to use idx3d for commercial purposes, please
// | contact me with an e-mail [proxima@active.ch].
// |
// | Thanx & greetinx go to:
// | * Wilfred L. Guerin, 	for testing, bug report, and tons 
// |			of brilliant suggestions
// | * Sandy McArthur,	for reverse loops
// | * Dr. Douglas Lyons,	for mentioning idx3d1 in his book
// | * Hugo Elias,		for maintaining his great page
// | * the comp.graphics.algorithms people, 
// | 			for scientific concerns
// | * Tobias Hill,		for inspiration and awakening my
// |			interest in java gfx coding
// | * Kai Krause,		for inspiration and hope
// | * Incarom & Parisienne,	for keeping me awake during the 
// |			long coding nights
// | * Doris Langhard,	for being the sweetest girl on earth
// | * Etnica, Infinity Project, X-Dream and "Space Night"@BR3
// | 			for great sound while coding
// | and all coderz & scenerz out there (keep up the good work, ppl :)
// |
// | Peter Walser
// | proxima@active.ch
// | http://www2.active.ch/~proxima
// | "On the eigth day, God started debugging"
// | -----------------------------------------------------------------
package idx3d;

import org.jhotdraw.annotation.Nullable;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;

/**
 * Produces an image.
 *
 */
public class idx3d_ImageProducer implements ImageProducer {

    private ImageConsumer consumer;
    private int w, h;
    private ColorModel cm;
    @Nullable
    private int[] pixel;
    private int hints, done;
    @Nullable
    private BufferedImage image;

    // C O N S T R U C T O R
    public idx3d_ImageProducer(int w, int h, ColorModel cm, int pixel[]) {
        this.w = w;
        this.h = h;
        this.cm = cm;
        this.pixel = pixel;
        hints = ImageConsumer.TOPDOWNLEFTRIGHT
                | ImageConsumer.COMPLETESCANLINES
                | ImageConsumer.SINGLEPASS
                | ImageConsumer.SINGLEFRAME;
        //done=ImageConsumer.SINGLEFRAMEDONE;
        done = ImageConsumer.STATICIMAGEDONE;

    }

    @Nullable
    public BufferedImage getImage() {
        if (image == null && w > 0 && h > 0) {
            DataBufferInt db = new DataBufferInt(pixel, pixel.length);
            image = new BufferedImage(cm,
                    Raster.createWritableRaster(cm.createCompatibleSampleModel(w, h), db, new Point(0, 0)),
                    false, null);
        }
        return image;
    }

    //  P U B L I C   M E T H O D S
    public synchronized void addConsumer(ImageConsumer consumer) {
        this.consumer = consumer;
    }

    public final void startProduction(ImageConsumer imageconsumer) {
        if (consumer != imageconsumer) {
            consumer = imageconsumer;
            consumer.setDimensions(w, h);
            consumer.setProperties(null);
            consumer.setColorModel(cm);
            consumer.setHints(hints);
        }
        consumer.setPixels(0, 0, w, h, cm, pixel, 0, w);
        consumer.imageComplete(done);
    }

    public void update() {
        if (consumer != null) {
            startProduction(consumer);
        }
    }

    public final boolean isConsumer(ImageConsumer imageconsumer) {
        return consumer == imageconsumer;
    }

    public final void requestTopDownLeftRightResend(ImageConsumer imageconsumer) {
    }

    public final void removeConsumer(ImageConsumer imageconsumer) {
    }

    public void flush() {
        pixel = null;
    }
}

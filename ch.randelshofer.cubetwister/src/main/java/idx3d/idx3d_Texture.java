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

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.net.URL;

/**
 * Defines a texture.
 *
 */
public class idx3d_Texture {
    // F I E L D S
    
    public int width;
    public int height;
    public int bitWidth;
    public int bitHeight;
    @Nullable
    public int pixel[];

    @Nullable
    public String path=null;
    
    // C O N S T R U C T O R S
    
    public idx3d_Texture(int w, int h) {
        height=h;
        width=w;
        pixel=new int[w*h];
        cls();
    }
    
    public idx3d_Texture(int w, int h, int data[]) {
        height=h;
        width=w;
        pixel=data;
        //pixel=new int[width*height];
        //System.arraycopy(data,0,pixel,0,width*height);
    }

    public idx3d_Texture(@Nonnull Image img) {
        loadTexture(img);
    }

    public idx3d_Texture(@Nonnull URL docURL, String filename)
    // Call from Applet
    {
        int pos = 0;
        String temp = docURL.toString();
        while (temp.indexOf("/", pos) > 0) {
            pos = temp.indexOf("/", pos) + 1;
        }
        temp = temp.substring(0, pos) + filename;
        while (temp.indexOf("/", pos) > 0) {
            pos = temp.indexOf("/", pos) + 1;
        }
        String file = temp.substring(pos);
        String base = temp.substring(0, pos);
        
        try{
            loadTexture(Toolkit.getDefaultToolkit().getImage(new URL(base+file)));
        } catch (Exception e){System.err.println(e+"");}
    }

    public idx3d_Texture(@Nonnull String filename) {
        path = new java.io.File(filename).getName();
        loadTexture(Toolkit.getDefaultToolkit().getImage(filename));
    }
    
    
    // P U B L I C   M E T H O D S
    
    public void resize() {
        double log2inv = 1 / Math.log(2);
        int w = (int) Math.pow(2, bitWidth = (int) (Math.log(width) * log2inv));
        int h = (int) Math.pow(2, bitHeight = (int) (Math.log(height) * log2inv));
        resize(w, h);
    }

    public void resize(int w, int h) {
        setSize(w, h);
    }

    @Nonnull
    public idx3d_Texture put(@Nonnull idx3d_Texture newData)
    // assigns new data for the texture
    {
        System.arraycopy(newData.pixel, 0, pixel, 0, width * height);
        return this;
    }

    @Nonnull
    public idx3d_Texture mix(@Nonnull idx3d_Texture newData)
    // mixes the texture with another one
    {
        for (int i = width * height - 1; i >= 0; i--) {
            pixel[i] = idx3d_Color.mix(pixel[i], newData.pixel[i]);
        }
        return this;
    }

    @Nonnull
    public idx3d_Texture add(@Nonnull idx3d_Texture additive)
    // additive blends another texture with this
    {
        for (int i = width * height - 1; i >= 0; i--) {
            pixel[i] = idx3d_Color.add(pixel[i], additive.pixel[i]);
        }
        return this;
    }

    @Nonnull
    public idx3d_Texture sub(@Nonnull idx3d_Texture subtractive)
    // subtractive blends another texture with this
    {
        for (int i = width * height - 1; i >= 0; i--) {
            pixel[i] = idx3d_Color.sub(pixel[i], subtractive.pixel[i]);
        }
        return this;
    }

    @Nonnull
    public idx3d_Texture inv()
    // inverts the texture
    {
        for (int i = width * height - 1; i >= 0; i--) {
            pixel[i] = idx3d_Color.inv(pixel[i]);
        }
        return this;
    }

    @Nonnull
    public idx3d_Texture multiply(@Nonnull idx3d_Texture multiplicative)
    // inverts the texture
    {
        for (int i = width * height - 1; i >= 0; i--) {
            pixel[i] = idx3d_Color.multiply(pixel[i], multiplicative.pixel[i]);
        }
        return this;
    }


    /**
     * Clears the texture.
     */
    public void cls() {
        idx3d_Math.clearBuffer(pixel,0);
    }
    
    /** Builds the average of the channels. */
    @Nonnull
    public idx3d_Texture toAverage() {
        for (int i=width*height-1;i>=0;i--)
            pixel[i]=idx3d_Color.getAverage(pixel[i]);
        return this;
    }
    
    /** Converts this texture to gray. */
    @Nonnull
    public idx3d_Texture toGray() {
        for (int i = width*height-1; i>=0; i--)
            pixel[i]=idx3d_Color.getGray(pixel[i]);
        return this;
    }

    @Nonnull
    public idx3d_Texture valToGray() {
        int intensity;
        for (int i = width * height - 1; i >= 0; i--) {
            intensity = idx3d_Math.crop(pixel[i], 0, 255);
            pixel[i] = idx3d_Color.getColor(intensity, intensity, intensity);
        }

        return this;
    }

    @Nonnull
    public idx3d_Texture colorize(@Nonnull int[] pal) {
        int range = pal.length - 1;
        for (int i = width * height - 1; i >= 0; i--) {
            pixel[i] = pal[idx3d_Math.crop(pixel[i], 0, range)];
        }
        return this;
    }

    @Nonnull
    public static idx3d_Texture blendTopDown(@Nonnull idx3d_Texture top, @Nonnull idx3d_Texture down) {
        down.resize(top.width, top.height);
        idx3d_Texture t = new idx3d_Texture(top.width, top.height);
        int pos = 0;
        int alpha;
        for (int y = 0; y < top.height; y++) {
            alpha = 255 * y / (top.height - 1);
            for (int x = 0; x < top.width; x++) {
                t.pixel[pos] = idx3d_Color.transparency(down.pixel[pos], top.pixel[pos], alpha);
                pos++;
            }
        }
        return t;
    }
    
    // P R I V A T E   M E T H O D S


    /**
     * Grabs the pixels out of an image.
     */
    private void loadTexture(@Nonnull Image img) {
        Component component = new Panel();
        MediaTracker tracker = new MediaTracker(component);
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
        }

        width = img.getWidth(component);
        height = img.getHeight(component);
        // defensively free memory before allocating
        // a new array.
        pixel = null;
        pixel = new int[width*height];
        PixelGrabber pg = new PixelGrabber(img,0,0,width,height,pixel,0,width);
        boolean success = false;
        try {
            success = pg.grabPixels();
        } catch (InterruptedException e) {
        }
        if (success == false) {
            System.err.println("Sorry. idx3d_Testure couldn't grab pixels.");
            pixel = null;
            width = height = 0;
        }
    }
    
    /** Resizes the texture. */
    private void setSize(int w, int h) {
        if (width == w && height == h) return;
        
        int offset=w*h;
        int offset2;
        if (w*h!=0) {
            int newpixels[]=new int[w*h];
            for(int j=h-1;j>=0;j--) {
                offset-=w;
                offset2=(j*height/h)*width;
                for (int i=w-1;i>=0;i--)
                    newpixels[i+offset]=pixel[(i*width/w)+offset2];
            }
            width=w; height=h; pixel=newpixels;
        }
    }
    
    private boolean inrange(int a, int b, int c) {
        return (a>=b) & (a < c);
    }
    
    @Nonnull
    public idx3d_Texture getClone() {
        idx3d_Texture t=new idx3d_Texture(width,height);
        idx3d_Math.copyBuffer(pixel,t.pixel);
        return t;
    }
    
}
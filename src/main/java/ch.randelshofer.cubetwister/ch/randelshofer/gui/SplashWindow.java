/* @(#)Splash.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
/**
 * A Splash window.
 *  <p>
 * Usage: Main class is your application class. Create a startup class which
 * opens the splash window, invokes the main method of your Main class, and
 * disposes the splash window afterwards.
 * <pre>
 * public class StartupClass {
 *     public static void main(String[] args) {
 *         SplashWindow.splash(StartupClass.class.getResource("splash.gif"));
 *         Main.main(args);
 *         SplashWindow.disposeSplash();
 *     }
 * }
 * </pre>
 *
 * @author  Werner Randelshofer
 */
public class SplashWindow extends Window {
    private final static long serialVersionUID = 1L;
    /**
     * The current instance of the splash window.
     */
    private static SplashWindow instance;
    
    /**
     * The splash image is displayed on the splash window.
     */
    private Image image;
    
    /**
     * The text to display on the splash window.
     */
    private String text;
    
    /**
     * The location of the text.
     */
    private int x, y;
    
    /**
     * This attribute indicates whether the method
     * paint(Graphics) has been called at least once since the
     * construction of this window.<br>
     * This attribute is used to notify method splash(Image)
     * that the window has been drawn at least once
     * by the AWT event dispatcher thread.<br>
     * This attribute acts like a latch. Once set to true,
     * it will never be changed back to false again.
     *
     * @see #paint
     * @see #splash
     */
    private boolean paintCalled = false;
    
    /**
     * Creates a new instance.
     * @param parent the parent of the window.
     * @param image the splash image.
     */
    public SplashWindow(Frame parent, Image image) {
        super(parent);
        this.image = image;
        
        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}
        
        // Center the window on the screen
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
        (screenDim.width - imgWidth) / 2,
        (screenDim.height - imgHeight) / 2
        );
        
        // Users shall be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized(SplashWindow.this) {
                    SplashWindow.this.paintCalled = true;
                    SplashWindow.this.notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);
    }
    
    /**
     * Updates the display area of the window.
     */
    public void update(Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.
        paint(g);
    }
    /**
     * Paints the image on the window.
     */
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
        
        if (text != null) {
            FontMetrics fm = g.getFontMetrics();
            int ty = y + fm.getAscent();
            for (StringTokenizer tt = new StringTokenizer(text, "\n"); tt.hasMoreTokens(); ) {
                g.drawString(tt.nextToken(), x, ty);
                ty += fm.getHeight();
            }
        }
        
        // Notify method splash that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }
    
    /**
     * Open's a splash window using the specified image.
     * @param image The splash image.
     * @param text The text to display.
     * @param x The x coordinate of the text.
     * @param y The y coordinate of the text.
     */
    public static void splash(Image image, String text, int x, int y) {
        if (instance == null && image != null) {
            Frame f = new Frame();
            
            // Initiate the image loading process
            MediaTracker mt = new MediaTracker(f);
            mt.addImage(image,0);
            mt.checkID(0, true);
            
            
            // Create the splash image
            instance = new SplashWindow(f, image);
            
            instance.text = text;
            instance.x = x;
            instance.y = y;
            
            // Show the window.
            instance.setVisible(true);
            
            
            // Note: To make sure the user gets a chance to see the
            // splash window we wait until its paint method has been
            // called at least once by the AWT event dispatcher thread.
            if (! EventQueue.isDispatchThread()) {
                synchronized (instance) {
                    while (! instance.paintCalled) {
                        try { instance.wait(); } catch (InterruptedException e) {}
                    }
                }
            }
        }
    }
    /**
     * Open's a splash window using the specified image.
     * @param imageURL The url of the splash image.
     */
    public static void splash(URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL), null, 0, 0);
        }
    }
    /**
     * Open's a splash window using the specified image.
     * @param imageURL The url of the splash image.
     */
    public static void splash(URL imageURL, String text, int x, int y) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL), text, x, y);
        }
    }
    
    /**
     * Closes the splash window.
     */
    public static void disposeSplash() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }
}

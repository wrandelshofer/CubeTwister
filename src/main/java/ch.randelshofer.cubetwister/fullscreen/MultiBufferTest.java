/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

/**
 * This test takes a number up to 13 as an argument (assumes 2 by
 * default) and creates a multiple buffer strategy with the number of
 * buffers given.  This application enters full-screen mode, if available,
 * and flips back and forth between each buffer (each signified by a different
 * color).
 */
package fullscreen;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class MultiBufferTest {

    @Nonnull
    private static Color[] COLORS = new Color[] {
            Color.red, Color.blue, Color.green, Color.white, Color.black,
            Color.yellow, Color.gray, Color.cyan, Color.pink, Color.lightGray,
            Color.magenta, Color.orange, Color.darkGray };
    @Nonnull
    private static DisplayMode[] BEST_DISPLAY_MODES = new DisplayMode[] {
            new DisplayMode(640, 480, 32, 0),
            new DisplayMode(640, 480, 16, 0),
            new DisplayMode(640, 480, 8, 0)
    };
    
    Frame mainFrame;

    public MultiBufferTest(int numBuffers, @Nonnull GraphicsDevice device) {
        try {
            GraphicsConfiguration gc = device.getDefaultConfiguration();
            mainFrame = new Frame(gc);
            mainFrame.setUndecorated(true);
            mainFrame.setIgnoreRepaint(true);
            device.setFullScreenWindow(mainFrame);
            if (device.isDisplayChangeSupported()) {
                chooseBestDisplayMode(device);
            }
            Rectangle bounds = mainFrame.getBounds();
            mainFrame.createBufferStrategy(numBuffers);
            BufferStrategy bufferStrategy = mainFrame.getBufferStrategy();
            Random random = new Random();
            for (float lag = 2000.0f; lag > 0.00000006f; lag = lag / 1.33f) {
                for (int i = 0; i < numBuffers; i++) {
                    Graphics g = bufferStrategy.getDrawGraphics();
                    if (!bufferStrategy.contentsLost()) {
                        g.setColor(COLORS[i]);
                        g.fillRect(0,0,bounds.width, bounds.height);
                        g.setColor(Color.BLACK);
                        g.drawLine(
                                (int) (bounds.width * random.nextFloat()),
                                (int) (bounds.height * random.nextFloat()),
                                (int) (bounds.width * random.nextFloat()),
                                (int) (bounds.height * random.nextFloat()));
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Dialog", Font.PLAIN, 100));
                        FontMetrics fm = g.getFontMetrics();
                        String text = DateFormat.getTimeInstance().format(new Date());
                        LineMetrics lm = fm.getLineMetrics(text, g);
                        Rectangle2D textBounds = fm.getStringBounds(text, g);
                        g.drawString(text, (int) (bounds.width - textBounds.getWidth()) / 2, (int) (bounds.height - textBounds.getHeight()) / 2);
                        bufferStrategy.show();
                        g.dispose();
                    }
                    try {
                        Thread.sleep((int)lag);
                    } catch (InterruptedException e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            device.setFullScreenWindow(null);
        }
    }

    @Nullable
    private static DisplayMode getBestDisplayMode(@Nonnull GraphicsDevice device) {
        for (int x = 0; x < BEST_DISPLAY_MODES.length; x++) {
            DisplayMode[] modes = device.getDisplayModes();
            for (int i = 0; i < modes.length; i++) {
                if (modes[i].getWidth() == BEST_DISPLAY_MODES[x].getWidth()
                        && modes[i].getHeight() == BEST_DISPLAY_MODES[x].getHeight()
                        && modes[i].getBitDepth() == BEST_DISPLAY_MODES[x].getBitDepth()
                ) {
                    return BEST_DISPLAY_MODES[x];
                }
            }
        }
        return null;
    }

    public static void chooseBestDisplayMode(@Nonnull GraphicsDevice device) {
        DisplayMode best = getBestDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }

    public static void main(@Nullable String[] args) {
        try {
            int numBuffers = 2;
            if (args != null && args.length > 0) {
                numBuffers = Integer.parseInt(args[0]);
                if (numBuffers < 2 || numBuffers > COLORS.length) {
                    System.err.println("Must specify between 2 and "
                            + COLORS.length + " buffers");
                    System.exit(1);
                }
            }
            GraphicsEnvironment env = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            MultiBufferTest test = new MultiBufferTest(numBuffers, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
/*
 * @(#)BusyIcon.java  1.1.1  2010-11-06
 *
 * Copyright (c) 2010 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.gui.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.Timer;

/**
 * BusyIcon.
 *
 * @author Werner Randelshofer
 * @version 1.1.1 2010-11-06 Fixes unclosed stream when reading BusyIcon.png.
 * <br>1.1 2010-01-04 Only use a single timer.
 * <br>1.0 2010-01-01 Created.
 */
public class BusyIcon implements Icon {

    private static BusyIcon instance;
    private static BufferedImage image;
    private Timer timer;
    private HashSet<Component> timerComponents;

    /** Prevent instance creation. */
    private BusyIcon() {
    }

    public static BusyIcon getInstance() {
        if (instance == null) {
            instance = new BusyIcon();
            new Thread() {

                @Override
                public void run() {
                    try {
                        image = ImageIO.read(BusyIcon.class.getResource("/ch/randelshofer/gui/images/BusyIcon.png"));
                    } catch (IOException ex) {
                        System.err.println("ProgressIcon failed to load " + "/ch/randelshofer/gui/images/BusyIcon.png");
                        ex.printStackTrace();
                    }
                }
            }.start();
        }
        return instance;
    }

    @Override
    public void paintIcon(final Component c, Graphics g, final int x, final int y) {
        if (image != null) {
            // 10 frames @ in intervals of 60 milliseconds
            int i = (int) (System.currentTimeMillis() / 60 % 10);
            g.drawImage(image, x, y, x + 16, y + 16, 16 * i, 0, 16 * i + 16, 0 + 16, c);
            if (c != null) {
                startTimer(c);
            }
        }
    }

    private void startTimer(Component c) {
        if (timer == null) {
            timerComponents = new HashSet<Component>();
            timer = new Timer(30, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (timerComponents.isEmpty()) {
                        timer.stop();
                        timer = null;
                    } else {
                        for (Component c : timerComponents) {
                            c.repaint();
                        }
                        timerComponents.clear();
                    }
                }
            });
            timer.setRepeats(true);
            timer.start();
        }

        timerComponents.add(c);
    }

    @Override
    public int getIconWidth() {
        return 16;
    }

    @Override
    public int getIconHeight() {
        return 16;
    }
}

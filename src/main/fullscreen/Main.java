/**
 * @(#)Main.java  1.0  Apr 27, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package fullscreen;

import java.awt.event.*;
import javax.swing.*;

/**
 * Main.
 *
 * @author Werner Randelshofer
 * @version 1.0 Apr 27, 2008 Created.
 */
public class Main {
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JFrame f = new JFrame();
                f.getContentPane().addMouseListener(new MouseListener() {
                    private boolean isFullScreen = false;
                    
                    public void mouseClicked(MouseEvent e) {
                        if (isFullScreen) {
                            
                        } else {
                            
                        }
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                    
                });
            }
        });
    }

}

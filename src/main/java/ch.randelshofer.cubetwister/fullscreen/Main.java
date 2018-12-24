/* @(#)Main.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */

package fullscreen;

import java.awt.event.*;
import javax.swing.*;

/**
 * Main.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

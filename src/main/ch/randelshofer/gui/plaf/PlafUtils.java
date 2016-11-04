/*
 * @(#)PlafUtils.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.plaf;


import ch.randelshofer.gui.border.ImageBevelBorder;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
/**
 * PlafUtils.
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class PlafUtils 
implements PlafConstants {
    protected static ImageBevelBorder[][] bevelRenderer;
    /**
     * Convenience method for installing a component's default Border object on the 
     * specified component if either the border is currently null or already an instance 
     * of UIResource. 
     *
     *
     * @param c the target component for installing default border
     * @param defaultBorderName - the key specifying the default border     
     */
    static void installBevelBorder(JComponent c, String defaultBorderName) {
        initBevels();
        Object bevelProperty = c.getClientProperty(PROP_BEVEL);
        Border border;
        if (bevelProperty == WEST) border = new EmptyBorder(6,8,8,4);
        else if (bevelProperty == EAST) border = new EmptyBorder(6,4,8,8);
        else if (bevelProperty == NONE || bevelProperty == CENTER) border = new EmptyBorder(6,4,8,4);
        else border = new EmptyBorder(6,8,8,8);
        c.setBorder(border);
    }
    
    static void paintBevel(JComponent c, Graphics g, int x, int y, int width, int height, boolean enabled, boolean pressed, boolean selected) {
        initBevels();
        Object bevelProperty = c.getClientProperty(PROP_BEVEL);
        int type;
        if (bevelProperty == WEST) type = 1;
        else if (bevelProperty == EAST) type = 2;
        else if (bevelProperty == NONE || bevelProperty == CENTER) type = 3;
        else type = 0;
        
        int state = ((enabled) ? 0 : 1) | ((pressed & enabled) ? 2 : 0) | ((selected) ? 4 : 0);
        
        bevelRenderer[type][state].paintBorder(c, g, x, y, width, height);
    }
    
    private static void initBevels() {
        if (bevelRenderer == null) {
            String id = UIManager.getLookAndFeel().getID();
            String path;
            if ("Metal".equals(id)) {
                path = "images/Metal/";
            } else {
                path = "images/Mac/";
            }

            String[] names = {"Bevel", "BevelLeft", "BevelRight", "BevelNone"};
            Insets[] insets = {new Insets(8, 10, 16, 10), new Insets(8, 10, 16, 0), new Insets(8, 1, 16, 10), new Insets(8, 1, 16, 0)};


            bevelRenderer = new ImageBevelBorder[4][7];
            for (int i=0; i < 4; i++) {
                for (int j=0; j < 7; j++) {
                    if (j != 3) {
                        bevelRenderer[i][j] = new ImageBevelBorder(
                            Toolkit.getDefaultToolkit().createImage(
                                PlafUtils.class.getResource(path+names[i]+"."+j+".png")
                            ),
                            insets[i]
                        );
                    }
                }
            }
        }
    }

    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( Component c ) {
        /*if[JDK1.2]
        return c.getComponentOrientation().isLeftToRight();
        else[JDK1.2]*/
        return true;
        /*end[JDK1.2]*/
    }
    
}


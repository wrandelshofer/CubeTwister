/* @(#)Colors.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import java.awt.*;
/**
 * Colors.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class Colors {
    
    /** Prevent instance creation. */
    private Colors() {
    }

    /**
     * Blackens the specified color by casting a black shadow of the specified 
     * amount on the color.
     */
    public static Color shadow(Color c, int amount) {
       return (c == null) ? null :
           new Color(
        Math.max(0, c.getRed() - amount),
        Math.max(0, c.getGreen() - amount),
        Math.max(0, c.getBlue() - amount),
        c.getAlpha()
        );
    }
    
}

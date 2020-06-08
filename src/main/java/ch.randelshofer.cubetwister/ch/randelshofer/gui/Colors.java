/*
 * @(#)Colors.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nullable;

import java.awt.Color;
/**
 * Colors.
 *
 * @author  Werner Randelshofer
 */
public class Colors {

    /** Prevent instance creation. */
    private Colors() {
    }

    /**
     * Blackens the specified color by casting a black shadow of the specified
     * amount on the color.
     */
    @Nullable
    public static Color shadow(@Nullable Color c, int amount) {
        return (c == null) ? null :
                new Color(
                        Math.max(0, c.getRed() - amount),
                        Math.max(0, c.getGreen() - amount),
                        Math.max(0, c.getBlue() - amount),
                        c.getAlpha()
                );
    }

}

/*
 * FocusBorder.java
 *
 * Created on December 17, 2002, 7:33 AM
 */

package ch.randelshofer.gui.border;

import java.awt.*;
import javax.swing.border.Border;
/**
 *
 * @author  werni
 */
public class FocusBorder extends javax.swing.border.MatteBorder {
        private final static long serialVersionUID = 1L;

    /** Creates a focus border with the specified insets and color. */
    public FocusBorder(int top, int left, int bottom, int right, Color matteColor) {
        super(top, left, bottom, right, matteColor);
    }

   
    /**
     * Sets the matte color of the border.
     * If you set this value to null you must set the tile icon to a
     * non-null value.
     */
    public void setMatteColor(Color matteColor) {
        color = matteColor;
    }
}

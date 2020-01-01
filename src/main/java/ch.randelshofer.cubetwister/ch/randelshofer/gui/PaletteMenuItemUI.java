/* @(#)PaletteMenuItemUI.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;


import org.jhotdraw.annotation.Nonnull;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
/**
 * PaletteMenuItemUI.
 *
 * @author  Werner Randelshofer
 */
public class PaletteMenuItemUI extends javax.swing.plaf.basic.BasicMenuItemUI {
    /** Creates a new instance. */
    public PaletteMenuItemUI() {
    }

    protected void installDefaults() {
        super.installDefaults();
        defaultTextIconGap = 0;   // Should be from table
        //menuItem.setBorderPainted(false);
        //menuItem.setBorder(null);
        arrowIcon = null;
        checkIcon = null;
    }

    @Nonnull
    protected Dimension getPreferredMenuItemSize(JComponent c,
                                                 Icon checkIcon,
                                                 Icon arrowIcon,
                                                 int defaultTextIconGap) {
        JMenuItem b = (JMenuItem) c;
        Icon icon = b.getIcon();
        return new Dimension(icon.getIconWidth() + 2, icon.getIconHeight() + 2);
    }

    public void paint(@Nonnull Graphics g, JComponent c) {
        JMenuItem b = (JMenuItem) c;

        // Paint background
        paintBackground(g, b, selectionBackground);

        // Paint the icon
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Icon icon = b.getIcon();
        icon.paintIcon(b, g, 1, 1);
    }
}

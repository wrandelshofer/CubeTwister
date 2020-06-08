/*
 * @(#)PlacardButtonBorder.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.border;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * Quaqua14PlacardButtonBorder.
 *
 * @author  Werner Randelshofer
 */
public class PlacardButtonBorder implements Border {

    private final static Color[] defaultColors = {
        new Color(0xd8d8d8), // border top 1
        new Color(0x7d7d7d), // border top 2
        new Color(0x979797), // border left and right
        new Color(0x979797), // border bottom 1
        new Color(0xf5f5f5), // border bottom 2
        /*
        new Color(0xcacaca), // border gradient top
        new Color(0xb8b8b8), // border gradient bottom
         */
        new Color(0xfefefe), // inner border line top
        new Color(0xf3f3f3), // inner border line bottom
        new Color(0xfdfdfd), // shine box top
        new Color(0xf3f3f3), // shine box bottom
        new Color(0xe6e6e6), // shadow box
    };
    private final static Color[] selectedColors = {
        new Color(0xd8d8d8), // border top 1
        new Color(0x424242), // border top 2
        new Color(0x565656), // border left and right
        new Color(0x515151), // border bottom 1
        new Color(0xb9b9b9), // border bottom 2
        /*
        new Color(0x838383), // border gradient top
        new Color(0x737373), // border gradient bottom
         */
        new Color(0xa5a5a5), // inner border line top
        new Color(0x969696), // inner border line bottom
        new Color(0xa5a5a5), // shine box top
        new Color(0x9e9e9e), // shine box bottom
        new Color(0x969696), // shadow box
    };
    private final static Color[] disabledColors = {
        new Color(0xd8d8d8), // border top 1
        new Color(0x7d7d7d), // border top 2
        new Color(0x979797), // border left and right
        new Color(0x979797), // border bottom 1
        new Color(0xf5f5f5), // border bottom 2
        /*
        new Color(0xcacaca), // border gradient top
        new Color(0xb8b8b8), // border gradient bottom
         */
        new Color(0xfefefe), // inner border line top
        new Color(0xf3f3f3), // inner border line bottom
        new Color(0xfdfdfd), // shine box top
        new Color(0xf3f3f3), // shine box bottom
        new Color(0xe6e6e6), // shadow box
    };
    private final static Color[] disabledSelectedColors = {
        new Color(0xd8d8d8), // border top 1
        new Color(0x424242), // border top 2
        new Color(0x565656), // border left and right
        new Color(0x515151), // border bottom 1
        new Color(0xb9b9b9), // border bottom 2
        /*
        new Color(0x838383), // border gradient top
        new Color(0x737373), // border gradient bottom
         */
        new Color(0xa5a5a5), // inner border line top
        new Color(0x969696), // inner border line bottom
        new Color(0xa5a5a5), // shine box top
        new Color(0x9e9e9e), // shine box bottom
        new Color(0x969696), // shadow box
    };
    private int orientation;

    /** Creates a new instance of QuaquaSquareButtonBorder */
    public PlacardButtonBorder(int orientation) {
        this.orientation = orientation;
    }

    @Nonnull
    public Insets getBorderInsets(Component c) {
        if (c instanceof AbstractButton) {
            return new Insets(2, 7, 2, 7);
        } else {
            return new Insets(0, 0, 0, 0);
        }
    }

    public boolean isBorderOpaque() {
        return false;
    }

    /**
     * Creates the image to cache.  This returns a translucent image.
     *
     * @param c      Component painting to
     * @param w      Width of image to create
     * @param h      Height to image to create
     * @param config GraphicsConfiguration that will be
     *               rendered to, this may be null.
     */
    protected Image createImage(Component c, int w, int h,
                                @Nullable GraphicsConfiguration config) {
        if (config == null) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
        }
        return config.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
    }

    @Override
    public void paintBorder(Component c, @Nonnull Graphics gr, int x, int y, int width, int height) {
        if (height <= 0 || width <= 0) {
            return;
        }

        Color[] colors;
        if (c instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();

            if (button.isEnabled()) {
                colors = (model.isSelected() || model.isArmed() && model.isPressed()) ? selectedColors : defaultColors;
            } else {
                colors = (model.isSelected()) ? disabledSelectedColors : disabledColors;
            }
        } else {
            colors = (c.isEnabled()) ? defaultColors : disabledColors;
        }
        switch (orientation) {
            case SwingConstants.CENTER:
                paint(c, gr, x - 1, y, width + 2, height + 2, colors);
                break;
            case SwingConstants.LEFT:
                paint(c, gr, x, y, width + 1, height + 2, colors);
                break;
            case SwingConstants.RIGHT:
                paint(c, gr, x - 1, y, width + 1, height + 2, colors);
                break;
            default:
                paint(c, gr, x, y, width, height + 2, colors);
                break;

        }
    }

    protected void paint(Component c, @Nonnull Graphics gr, int x, int y, int width, int height, Color[] args) {
        // Cast Graphics to Graphics2D
        // Workaround for Java 1.4 and 1.4 on Mac OS X 10.4. We create a new
        // Graphics object instead of just casting the provided one. This is
        // because drawing texture paints appears to confuse the Graphics object.
        Graphics2D g = (Graphics2D) gr.create();
        g.translate(x, y - 1);
        Color[] colors = args;

        Paint oldPaint = g.getPaint();

        // Note: We draw the gradient paints first, because Apple's Java
        // 1.4.2_05 draws them 1 Pixel too wide on the left
        // draw inner border lines
        g.setPaint(new GradientPaint(0, 2, colors[5], 0, height - 3, colors[6]));
        g.drawLine(1, 2, 1, height - 3);
        g.drawLine(width - 2, 2, width - 2, height - 3);

        // draw shine box
        int sheight = (int) (height * 0.45);
        g.setPaint(new GradientPaint(0, 2, colors[7], 0, sheight, colors[8]));
        g.fillRect(2, 2, width - 4, sheight - 1);

        // draw border
        g.setColor(colors[0]);
        g.drawLine(0, 0, width - 1, 0);
        g.setColor(colors[1]);
        g.drawLine(0, 1, width - 1, 1);
        g.setColor(colors[2]);
        g.drawLine(0, 2, 0, height - 3);
        g.drawLine(width - 1, 2, width - 1, height - 3);
        g.setColor(colors[3]);
        g.drawLine(0, height - 2, width - 1, height - 2);
        g.setColor(colors[4]);
        g.drawLine(0, height - 1, width - 1, height - 1);

        // draw shadow box
        g.setColor(colors[9]);
        g.fillRect(2, sheight + 1, width - 4, height - sheight - 3);
        g.translate(-x, -y);

        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            g.setFont(b.getFont());
            Rectangle viewR = new Rectangle(0, 0, b.getWidth(), b.getHeight());
            Rectangle iconR = new Rectangle();
            Rectangle textR = new Rectangle();
            SwingUtilities.layoutCompoundLabel(b, g.getFontMetrics(),//
                    b.getText(), b.getIcon(),//
                    b.getVerticalAlignment(), b.getHorizontalAlignment(),//
                    b.getVerticalTextPosition(), b.getHorizontalTextPosition(),//
                    viewR, iconR, textR, 5);
            g.setColor(b.getForeground());
            if (b.getText() != null) {
                g.drawString(b.getText(), textR.x, textR.y + g.getFontMetrics().getAscent());
            }
            if (b.getIcon() != null) {
                b.getIcon().paintIcon(c, gr, iconR.x, iconR.y);
            }
        }

        g.setPaint(oldPaint);
        g.dispose();
    }
}

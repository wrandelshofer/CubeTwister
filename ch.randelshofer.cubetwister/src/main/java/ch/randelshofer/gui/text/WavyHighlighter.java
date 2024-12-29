/*
 * @(#)WavyHighlighter.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.text;

/**
 * Higlights text by underlining it with a wavy line.
 * <p>
 * Usage:
 * <pre>
 * JTextPane textPane = new JTextPane();
 * textPane.setText("one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\n");
 * JScrollPane scrollPane = new JScrollPane(textPane);
 *
 * //  Highlight some text
 *
 * WavyHighlighter cyan = new WavyHighlighter(Color.CYAN);
 *WavyHighlighter red = new WavyHighlighter(Color.RED);
 *
 * try {
 * textPane.getHighlighter().addHighlight(8, 14, cyan);
 * textPane.getHighlighter().addHighlight(19, 24, red);
 * } catch (BadLocationException ble) {
 * }
 *
 * JFrame frame = new JFrame();
 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 * frame.getContentPane().add(scrollPane);
 * frame.setSize(300, 200);
 * frame.setVisible(true);
 * </pre>
 * @author Werner Randelshofer
 */

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

public class WavyHighlighter extends DefaultHighlighter.DefaultHighlightPainter {

    public WavyHighlighter(Color color) {
        super(color);
    }

    @Nullable
    @Override
    public Shape paintLayer(@Nonnull Graphics g, int offs0, int offs1,
                            Shape bounds, @Nonnull JTextComponent c, @Nonnull View view) {
        Color color = getColor();

        if (color == null) {
            g.setColor(c.getSelectionColor());
        } else {
            g.setColor(color);
        }

        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            // Contained in view, can just use bounds.
            Rectangle r;
            if (bounds instanceof Rectangle) {
                r = (Rectangle) bounds;
            } else {
                r = bounds.getBounds();
            }

            //g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
            Shape clip = g.getClip();
            g.clipRect(r.x, r.y, r.width, r.height);
            for (int i = 0; i < r.width; i += 6) {
                g.drawArc(r.x + i, r.y + r.height - 3, 3, 3, 0, 180);
                g.drawArc(r.x + i+3, r.y + r.height - 3, 3, 3, 180, 181);
            }
            g.setClip(clip);
            return r;
        } else {
            // Should only render part of View.
            try {
                // --- determine locations ---
                Shape shape = view.modelToView(
                        offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                Rectangle r = (shape instanceof Rectangle)
                        ? (Rectangle) shape : shape.getBounds();
                //g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
                Shape clip = g.getClip();
                g.clipRect(r.x, r.y, r.width, r.height);
                for (int i = 0; i < r.width; i += 6) {
                    g.drawArc(r.x + i, r.y + r.height - 3, 3, 3, 0, 180);
                    g.drawArc(r.x + i+3, r.y + r.height - 3, 3, 3, 180, 181);
                }
                g.setClip(clip);
                return r;
            } catch (BadLocationException e) {
                // can't render
            }
        }

        // Only if exception
        return null;
    }

    public static void main(String[] args) {
        JTextPane textPane = new JTextPane();
        textPane.setText("one\ntwo\nthree\nfour\nfive\nsix\nseven\neight\n");
        JScrollPane scrollPane = new JScrollPane(textPane);

        //  Highlight some text

        WavyHighlighter cyan = new WavyHighlighter(Color.GREEN.darker());
        WavyHighlighter red = new WavyHighlighter(Color.RED);

        try {
            textPane.getHighlighter().addHighlight(8, 14, cyan);
            textPane.getHighlighter().addHighlight(19, 24, red);
        } catch (BadLocationException ble) {
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}
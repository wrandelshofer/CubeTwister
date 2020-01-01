/**
 * @(#)JMultilineLabel.java  2.0  December 25, 2007
 * Copyright (c) 1996 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * Displays multiple lines of text.
 *
 * @author Werner Randelshofer
 */
public class JMultilineLabel extends JComponent {
    private final static long serialVersionUID = 1L;
    private String text = "";
    @Nullable
    private String[] lines;
    @Nonnull
    private Insets insets = new Insets(2,3,3,3);
    private int selectionStart, selectionEnd;
    private Color selectionBackground = new Color(181,213,255);
    private Color borderColor = Color.black;
    private int minRows;
    
    /** Creates new form. */
    public JMultilineLabel() {
        initComponents();
        setBackground(Color.white);
        setForeground(Color.black);
    }
    
    public void setSelectionBackground(Color c) {
        selectionBackground = c;
        repaint();
    }
    
    public void setBorderColor(Color c) {
        borderColor = c;
        repaint();
    }
    public Color getBorderColor() {
        return borderColor;
    }
    
    /**
     * Converts the given place in the view coordinate system to the nearest
     * representative location in the model.
     */
    public int viewToModel(int x, int y) {
        FontMetrics fm = getFontMetrics(getFont());
        int line = (y - insets.top) / fm.getHeight();
        if (line < 0 || lines == null) {
            return 0;
        } else if (line >= lines.length) {
            return text.length();
        }
        
        int pos = 0;
        for (int i=0; i < line; i++) {
            pos += lines[i].length();
        }
        
        for (int i=1; i <= lines[line].length(); i++) {
            int width = fm.stringWidth(lines[line].substring(0, i));
            if (width + insets.left > x) {
                return pos + i - 1;
            }
        }
        return pos + lines[line].length();
    }
    
    
    public void setText(String text) {
        this.text = text;
        invalidate();
    }
    
    private void wrapText() {
        String t = text;
        if (t == null) {
            return;
        }
        
        int width = getSize().width - insets.left - insets.right;
        FontMetrics fm = getFontMetrics(getFont());
        
        ArrayList<String> linesVector = new ArrayList<String>();
        StringTokenizer tt = new StringTokenizer(t," \n", true);
        StringBuilder line = new StringBuilder();
        while (tt.hasMoreTokens()) {
            String token = tt.nextToken();
            if ("\n".equals(token)) {
                line.append(token);
                linesVector.add(line.toString());
                line.setLength(0);
            } else {
                if (fm.stringWidth(line + token) <= width) {
                    line.append(token);
                } else {
                    if (" ".equals(token)) {
                        line.append(token);
                        linesVector.add(line.toString());
                        line.setLength(0);
                    } else {
                        linesVector.add(line.toString());
                        line.setLength(0);
                        line.append(token);
                    }
                    
                }
            }
        }
        if (line.length() > 0) {
            linesVector.add(line.toString());
        }
        
        // Defensively copy the vector in the array 'newLines' before
        // assigning it to the variable 'lines'. This way we ensure,
        // that the variable lines is either null or has a fully
        // initialized array.
        String[] newLines = new String[linesVector.size()];
        lines = linesVector.toArray(newLines);
    }
    
    public String getText() {
        return text;
    }
    /**
     * Selects the text between the specified start and end positions.
     * <p>
     * This method sets the start and end positions of the
     * selected text, enforcing the restriction that the start position
     * must be greater than or equal to zero.  The end position must be
     * greater than or equal to the start position, and less than or
     * equal to the length of the text component's text.  The
     * character positions are indexed starting with zero.
     * The length of the selection is
     * <code>endPosition</code> - <code>startPosition</code>, so the
     * character at <code>endPosition</code> is not selected.
     * If the start and end positions of the selected text are equal,
     * all text is deselected.
     * <p>
     * If the caller supplies values that are inconsistent or out of
     * bounds, the method enforces these constraints silently, and
     * without failure. Specifically, if the start position or end
     * position is greater than the length of the text, it is reset to
     * equal the text length. If the start position is less than zero,
     * it is reset to zero, and if the end position is less than the
     * start position, it is reset to the start position.
     *
     * @param        selectionStart the zero-based index of the first
     * character to be selected
     * @param        selectionEnd the zero-based end position of the
     * text to be selected; the character at
     * <code>selectionEnd</code> is not selected
     * @see          java.awt.TextComponent#setSelectionStart
     * @see          java.awt.TextComponent#setSelectionEnd
     * @see          java.awt.TextComponent#selectAll
     */
    public synchronized void select(int selectionStart, int selectionEnd) {
        this.selectionStart = Math.min(text.length(), Math.max(0, selectionStart));
        this.selectionEnd = Math.min(text.length(), Math.max(selectionStart, selectionEnd));
        repaint();
    }
    
    public void invalidate() {
        lines = null;
        super.invalidate();
    }

    public void setInsets(@Nonnull Insets insets) {
        this.insets = (Insets) insets.clone();
        invalidate();
    }

    @Nonnull
    public Insets getInsets() {
        return (Insets) insets.clone();
    }

    @Nonnull
    public Dimension getPreferredSize() {
        Dimension size = new Dimension();
        Insets insets = getInsets();
        if (lines == null) {
            wrapText();
        }

        FontMetrics fm = getFontMetrics(getFont());
        for (int i=0; i < lines.length; i++) {
            size.width = Math.max(size.width, fm.stringWidth(lines[i]));
        }
        size.height = fm.getHeight() * Math.max(minRows, lines.length);

        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
        return size;
    }

    public void setMinRows(int rows) {
        minRows = rows;
        invalidate();
        }

    public void paintComponent(@Nonnull Graphics g) {
        Dimension size = getSize();

        // draw border
        g.setColor(borderColor);
        g.drawRect(0, -1, size.width - 1, size.height);

        if (text == null) {
            return;
        }
        if (lines == null) {
            invalidate();
            wrapText();
            Component p = this;
            while (p.getParent() != null && ! p.getParent().isValid()) {
                p = p.getParent();
            }
            p.validate();
            // We call repaint here, to make sure that we are redrawn in
            // case the validation results into the same size as we had before
            repaint();
            return;
        }
        
        // Defensively copy the lines array into a local variable
        // to prevent race conditions.
        String[] lines = this.lines;
        if (lines == null) {
            return;
        }
        
        Insets insets = getInsets();
        FontMetrics fm = getFontMetrics(getFont());
        
        // Draw Selection
        if (selectionEnd > selectionStart) {
            g.setColor(selectionBackground);
            int lineStart = 0;
            int y = insets.top;
            int height = fm.getHeight();
            for (int i=0; i < lines.length; i++) {
                int lineEnd = lineStart + lines[i].length();
                if (lineEnd >= selectionStart && lineStart <= selectionEnd) {
                    int offset = Math.max(0, selectionStart - lineStart);
                    int x = insets.left + fm.stringWidth(lines[i].substring(0, offset));
                    int width = fm.stringWidth(lines[i].substring(offset, Math.max(0, Math.min(lines[i].length(),selectionEnd - lineStart))));
                    g.fillRect(x, y, width, height);
                }
                lineStart = lineEnd;
                y += height;
            }
        }
        
        // Draw Text
            g.setColor(getForeground());
        int y = insets.top + fm.getAscent();

        for (int i=0; i < lines.length; i++) {
            String text = lines[i];
            if (text.length() > 0 && text.charAt(text.length() - 1) == '\n') {
                text = text.substring(0, text.length() - 1);
            }

            g.drawString(text, insets.left, y);
            y+=fm.getHeight();
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}

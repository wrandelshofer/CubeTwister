/* @(#)ColorTableCellEditor.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.table;

import ch.randelshofer.gui.Colors;
import ch.randelshofer.gui.PolygonIcon;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * ColorTableCellEditor..
 */
public class ColorTableCellEditor extends DefaultCellEditor {
    private final static long serialVersionUID = 1L;
    private PolygonIcon icon;
    @Nullable Color currentColor = null;
    
    public ColorTableCellEditor() {
        super(new JCheckBox()); //Unfortunately, the constructor
        //expects a check box, combo box,
        //or text field.
        
        icon = new PolygonIcon(
        new Polygon(
        new int[] {0, 24, 24, 0},
        new int[] {0, 0, 12, 12},
        4
        ),
        new Dimension(25, 13)
        );
        
        final JButton button = new JButton(icon);
        button.setBorderPainted(false);
        button.setMargin(new Insets(0,1,0,0));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        editorComponent = button;
        
        //Set up the dialog that the button brings up.
        final JColorChooser colorChooser = new JColorChooser();
        ActionListener okListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = colorChooser.getColor();
                if (c != null) {
                ColorTableCellEditor.this.currentColor = c;
                }
            }
        };
        final JDialog dialog = JColorChooser.createDialog(button,
        "Pick a Color",
        true,
        colorChooser,
        okListener,
        null); //XXXDoublecheck this is OK
        
        //Here's the code that brings up the dialog.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                colorChooser.setColor(ColorTableCellEditor.this.currentColor);
                //Without the following line, the dialog comes up
                //in the middle of the screen.
                dialog.setLocationRelativeTo(button);
                dialog.setVisible(true);
                
                //Must do this so that editing stops when appropriate.
                fireEditingStopped();
            }
        });
        setClickCountToStart(1); //This is usually 1 or 2.
    }

    /**
     * Creates new ColorTableCellEditor
     */
    public ColorTableCellEditor(@Nonnull JButton b) {
        super(new JCheckBox()); //Unfortunately, the constructor
        //expects a check box, combo box,
        //or text field.
        editorComponent = b;
        setClickCountToStart(1); //This is usually 1 or 2.

        //Must do this so that editing stops when appropriate.
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    @Nullable
    public Object getCellEditorValue() {
        return currentColor;
    }

    public Component getTableCellEditorComponent(@Nonnull JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {

        JButton button = (JButton) editorComponent;
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
	}

        
        currentColor = (Color) value;
        
        icon.setFillColor(currentColor);
        if (currentColor == null) {
            icon.setForeground(Color.black);
            button.setText("---");
        } else {
        icon.setForeground(Colors.shadow(currentColor, 38));
        button.setText(currentColor.getRed() + "r "+ currentColor.getGreen() + "g " + currentColor.getBlue()+"b");
        }
        return editorComponent;
    }
}

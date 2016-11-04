/*
 * ColorTableCellEditor.java
 *
 * Created on August 2, 2001, 5:00 PM
 */

package ch.randelshofer.gui.table;

import ch.randelshofer.gui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
/**
 * The editor button that brings up the dialog.
 * We extend DefaultCellEditor for convenience,
 * even though it mean we have to create a dummy
 * check box.  Another approach would be to copy
 * the implementation of TableCellEditor methods
 * from the source code for DefaultCellEditor.
 */
public class ColorTableCellEditor extends DefaultCellEditor {
    private final static long serialVersionUID = 1L;
    private PolygonIcon icon;
    Color currentColor = null;
    
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
    
    /** Creates new ColorTableCellEditor */
    public ColorTableCellEditor(JButton b) {
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
    
    public Object getCellEditorValue() {
        return currentColor;
    }
    
    public Component getTableCellEditorComponent(JTable table,
    Object value,
    boolean isSelected,
    int row,
    int column) {

        JButton button = (JButton) editorComponent; 
	if (isSelected) {
	   button.setForeground(table.getSelectionForeground());
	   button.setBackground(table.getSelectionBackground());
	}
	else {
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

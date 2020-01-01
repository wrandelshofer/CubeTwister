/* @(#)IntegerTextField.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import ch.randelshofer.gui.text.NumericDocument;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

/**
 * A text field which takes integer values. A <code>BoundedRangeModel</code>
 * is uses as the model for the integer value.
 * <p>
 * <p>
 * <b>User interface behaviour:</b>
 * <p>
 * Pressing the up cursor key increments the integer value. Pressing
 * cursor up when the maximum boundary is reached sets the integer
 * value to the minimum.
 * <br>Pressing the down cursor key decrements the integer value.
 * Pressing cursor down when the minimum boundary is reached sets the
 * integer value to the maximum.
 * <br>Characters, that are not digits, can not be entered into the
 * text field. Altough the text field can contain invalid values
 * during editing, method <code>getValue</code> is granted to always
 * return a value between the range boundaries.
 * <br>Pressing the enter key or attempting to move the cursor out
 * of the text field triggers a boundary check. If the value in the
 * text field is out of bounds, it is set to the minimum or maximum
 * value of the boundary.
 *
 */
public class IntegerTextField extends JTextField {
        private final static long serialVersionUID = 1L;

    /**
     * Creates a new IntegerTextField with a default bounded
     * range model.
     */
    public IntegerTextField() {
        this(new javax.swing.DefaultBoundedRangeModel());
    }

    /**
     * Creates a new IntegerTextField with the specified
     * bounded range model.
     */
    public IntegerTextField(@Nonnull javax.swing.BoundedRangeModel m) {
        super(
                new NumericDocument(m),
                String.valueOf(m.getValue()),
                Math.max(String.valueOf(m.getMinimum()).length(), String.valueOf(m.getMaximum()).length()) + 1
        );

        installKeyboardActions();
        addFocusListener(new FocusListener() {
                             public void focusGained(FocusEvent evt) {
                             }
            public void focusLost(FocusEvent evt) {
                    NumericDocument doc = (NumericDocument) getDocument();
                    javax.swing.BoundedRangeModel brm = doc.getBoundedRangeModel();
                    setText(String.valueOf(brm.getValue()));
            }
        }
        );
    }
    
    protected void installKeyboardActions() {
        InputMap keyMap = getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = getActionMap();
        if (keyMap != null && actionMap != null) {
            KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            KeyStroke upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
            KeyStroke downKey = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
            KeyStroke numUpKey = KeyStroke.getKeyStroke("KP_UP");
            KeyStroke numDownKey = KeyStroke.getKeyStroke("KP_DOWN");
            keyMap.put(enterKey, "validateValue");
            keyMap.put(upKey, "incrementValue");
            keyMap.put(downKey, "decrementValue");
            if (upKey != numUpKey) {
                keyMap.put(numUpKey, "incrementValue");
                keyMap.put(numDownKey, "decrementValue");
            }
            actionMap.put("validateValue",
            new AbstractAction() {
    private final static long serialVersionUID = 1L;
                public void actionPerformed(ActionEvent e) {
                    NumericDocument doc = (NumericDocument) getDocument();
                    javax.swing.BoundedRangeModel brm = doc.getBoundedRangeModel();
                    setText(String.valueOf(brm.getValue()));
                }
            }
            );
            actionMap.put("incrementValue", new ValueDelta(1));
            actionMap.put("decrementValue", new ValueDelta(-1));
        }
    }

    public void setBoundedRangeModel(@Nonnull BoundedRangeModel m) {
        ((NumericDocument) getDocument()).setBoundedRangeModel(m);
    }
    
    public javax.swing.BoundedRangeModel getBoundedRangeModel() {
        return ((NumericDocument) getDocument()).getBoundedRangeModel();
    }
    
    
    class ValueDelta extends AbstractAction {
        private final static long serialVersionUID = 1L;
    int delta;
        
        public ValueDelta(int delta) {
            this.delta = delta;
        }
        
        public void actionPerformed(ActionEvent e) {
            javax.swing.BoundedRangeModel brm = ((NumericDocument) getDocument()).getBoundedRangeModel();
            int min = brm.getMinimum();
            int max = brm.getMaximum();
            int value = brm.getValue();
            value += delta;
            
            if (value < min) {
                value = max;
            } else if (value > max) {
                value = min;
            }
            
            brm.setValue(value);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    /*
    private void initComponents() {//GEN-BEGIN:initComponents
        
        setLayout(new java.awt.BorderLayout());
        
    }//GEN-END:initComponents
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}




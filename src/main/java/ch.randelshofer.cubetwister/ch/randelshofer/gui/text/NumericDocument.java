/*
 * @(#)NumericDocument.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.text;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.Toolkit;
/**
 * NumericDocument.
 *
 * @author Werner Randelshofer.
 */
public class NumericDocument extends PlainDocument  implements ChangeListener {
    private final static long serialVersionUID = 1L;
    /**
     * This bounded range model is used to store the integer
     * value.
     */
    private javax.swing.BoundedRangeModel model;

    public NumericDocument(@Nonnull javax.swing.BoundedRangeModel m) {
        setBoundedRangeModel(m);
    }

    public int getIntegerValue() {
        try {
            return Math.min(model.getMaximum() - model.getExtent(),
                    Math.max(model.getMinimum(),
                    (getLength() == 0) ? 0 : Integer.valueOf(getText(0, getLength())).intValue())
                    );
        } catch (BadLocationException e) {
            throw new InternalError(e.toString());
        } catch (NumberFormatException e) {
            throw new InternalError(e.toString());
        }
    }

    public void setIntegerValue(int value) {
        try {
            String stringRepresentation = String.valueOf(value);
            if (! stringRepresentation.equals(toString())) {
                super.remove(0, getLength());
                super.insertString(0, String.valueOf(value), null);
            }
        } catch (BadLocationException e) {
            throw new InternalError(e.toString());
        }
    }


    @Override
    public void insertString(int offs, @Nonnull String str, AttributeSet a)
            throws BadLocationException {
        char[] source = str.toCharArray();
        char[] result = new char[source.length];
        int j = 0;

        for (int i = 0; i < result.length; i++) {
            if (Character.isDigit(source[i])) {
                result[j++] = source[i];
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        super.insertString(offs, new String(result, 0, j), a);
        model.setValue(getIntegerValue());
    }

    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        model.setValue(getIntegerValue());
    }

    public void setBoundedRangeModel(@Nonnull javax.swing.BoundedRangeModel m) {
        if (model != null) {
            model.removeChangeListener(this);
        }
        model = m;
        try {
            remove(0, getLength());
            insertString(0, String.valueOf(m.getValue()), null);
        } catch (BadLocationException e) {
            throw new InternalError(e.toString());
        }
        model.addChangeListener(this);
    }

    public javax.swing.BoundedRangeModel getBoundedRangeModel() {
        return model;
    }

    public void stateChanged(@Nonnull ChangeEvent evt) {
        if (evt.getSource() == model) {
            setIntegerValue(model.getValue());
        }
    }
}

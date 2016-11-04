/*
 * @(#)NumericDocument.java  1.0  June 3, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.gui.text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.beans.*;
/**
 * NumericDocument.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 3, 2006 Created.
 */
public class NumericDocument extends PlainDocument  implements ChangeListener {
    private final static long serialVersionUID = 1L;
    /**
     * This bounded range model is used to store the integer
     * value.
     */
    private javax.swing.BoundedRangeModel model;
    
    public NumericDocument(javax.swing.BoundedRangeModel m) {
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
    public void insertString(int offs, String str, AttributeSet a)
    throws BadLocationException {
        char[] source = str.toCharArray();
        char[] result = new char[source.length];
        int j = 0;
        
        for (int i=0; i < result.length; i++) {
            if (Character.isDigit(source[i]))
                result[j++] = source[i];
            else
                Toolkit.getDefaultToolkit().beep();
        }
        
        super.insertString(offs, new String(result, 0, j), a);
        model.setValue(getIntegerValue());
    }
    
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        model.setValue(getIntegerValue());
    }
    
    public void setBoundedRangeModel(javax.swing.BoundedRangeModel m) {
        if (model != null) model.removeChangeListener(this);
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
    
    public void stateChanged(ChangeEvent evt) {
        if (evt.getSource() == model) {
            setIntegerValue(model.getValue());
        }
    }
}

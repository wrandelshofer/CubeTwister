/*
 * @(#)DocumentAdapter.java  1.2  2008-12-25
 *
 * Copyright (c) 2001-2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.gui.event;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * An abstract adapter class for receiving window events.
 * The methods in this class are empty. 
 * This class exists as convenience for creating listener objects.  
 *
 * @author Werner Randelshofer
 * @version 1.2 2008-12-25 Ignore document events while setting the text of
 * the document.
 * <br>1.1 2006-04-17 Method setText(String) added.
 * <br>1.0 2001-07-24
 */
public class DocumentAdapter 
implements DocumentListener {
private JTextComponent textComponent;
    /**
     * This counter is increased while the document adapter is updating
     * itself. If the counter is zero, the document adapter is not updating.
     */
    private int isUpdating;

    /** Creates new DocumentAdapter */
    public DocumentAdapter(JTextComponent c) {
        textComponent = c;
        c.getDocument().addDocumentListener(this);
        
    }

    /**
     * Gives notification that a portion of the document has been 
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param evt the document event
     */
    public void removeUpdate(DocumentEvent evt) {
        if (isUpdating++ == 0) {
        documentChanged(evt);
        }
        isUpdating--;
    }
    
    /**
     * Gives notification that there was an insert into the document.  The 
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param evt the document event
     */
    public void insertUpdate(DocumentEvent evt) {
        if (isUpdating++ == 0) {
        documentChanged(evt);
        }
        isUpdating--;
    }
    
    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param evt the document event
     */
    public void changedUpdate(DocumentEvent evt) {
        if (isUpdating++ == 0) {
        documentChanged(evt);
        }
        isUpdating--;
    }
    
    public void documentChanged(DocumentEvent evt) {
    }
    public String getText(DocumentEvent evt) {
        Document doc = evt.getDocument();
        String txt;
        try {
            txt = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            txt = null;
        }
        return txt;
    }
    public void setText(String text) {
        if (isUpdating++ == 0) {
        textComponent.setText(text);
        }
        isUpdating--;
    }
}

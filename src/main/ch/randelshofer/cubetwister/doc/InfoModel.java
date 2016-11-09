/* @(#)InfoModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.beans.*;
import ch.randelshofer.geom3d.*;
import ch.randelshofer.rubik.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.undo.*;
import ch.randelshofer.gui.text.*;

import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.text.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

/**
 * Holds general information: a title, a description field, an author field
 * and a date field.
 * <p>
 * Assumes that the root node of the tree is a DefaultMutableTreeNode
 * and that its user object is a RootModel instance, which holds the
 * tree! This is used to ensure proper listener notifications for object 
 * insertion and removal.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * this class. 
 * <br>1.0 2001-10-05 Created
 */
public class InfoModel extends EntityModel {
    private final static long serialVersionUID = 1L;
    public static final String PROP_NAME = "Name";
    public static final String PROP_DESCRIPTION = "Description";
    public static final String PROP_AUTHOR = "Author";
    public static final String PROP_DATE = "Date";

    protected DocumentProxy name = new DocumentProxy();
    protected DocumentProxy description = new DocumentProxy();
    protected DocumentProxy author = new DocumentProxy();
    protected DocumentProxy date = new DocumentProxy();

    /** 
     * Creates a new InfoModel.
     * Sets the author to the System user.name property and the
     * date to the current time.
     */
    public InfoModel() {
        super(null, false);
        try {
            author.setText(System.getProperty("user.name"));
        } catch (SecurityException e) {}
        date.setText(DateFormat.getDateTimeInstance().format(new Date()));

        name.addUndoableEditListener(this);
        description.addUndoableEditListener(this);
        author.addUndoableEditListener(this);
        date.addUndoableEditListener(this);
    }

    public String getName() {
        return name.getText();
    }
    public Document getNameDocument() {
        return name;
    }

    public void setName(String value) {
        String oldValue = name.getText();
        name.setText(value);
        firePropertyChange (PROP_NAME, oldValue, value);
    }
    public void basicSetName(String value) {
        name.setText(value);
    }

    /**
     * Returns the description property.
     */
    public String getDescription() {
        return description.getText();
    }
    public DocumentProxy getDescriptionDocument() {
        return description;
    }

    /**
     * Sets the description property.
     */
    public void setDescription(String value) {
        String oldValue = description.getText();
        if (value != null && value.equals(oldValue))
            return;
        
        basicSetDescription(value);
        firePropertyChange(PROP_DESCRIPTION, oldValue, value);
    }
    public void basicSetDescription(String value) {
        description.setText(value);
    }

    /**
     * Returns the author property.
     */
    public String getAuthor() {
        return author.getText();
    }
    public DocumentProxy getAuthorDocument() {
        return author;
    }

    /**
     * Sets the author property.
     */
    public void setAuthor(String value) {
        String oldValue = author.getText();
        basicSetAuthor(value);
        firePropertyChange (PROP_AUTHOR, oldValue, value);
    }
    public void basicSetAuthor(String value) {
        author.setText(value);
    }

    /**
     * Returns the date property.
     */
    public String getDate() {
        return date.getText();
    }
    public DocumentProxy getDateDocument() {
        return date;
    }

    /**
     * Sets the date property.
     */
    public void setDate(String value) {
        String oldValue = date.getText();
        basicSetDate(value);
        firePropertyChange(PROP_DATE, oldValue, value);
    }
    public void basicSetDate(String value) {
        date.setText(value);
    }
    /**
     * An undoable edit happened
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        if (e.getSource() == name) {
            fireNodeChanged();
        }
        super.undoableEditHappened(e);
    }
    @Override public Object getUserObject() {
        return getName();
    }
    
    @Override public void setUserObject(Object obj) {
        setName((String) obj);
    }
    
    @Override public String toString() {
        return getName();
    }
    

     /**
     * Returns a shallow copy of this model.
     * All references to other models are maintained.
     * The new model has no parent or children and no property change listeners.
     */
    public InfoModel clone() {
       InfoModel that = (InfoModel) super.clone();

        that.name = (DocumentProxy) this.name.clone();
       that.description = (DocumentProxy) this.description.clone();
       that.author = (DocumentProxy) this.author.clone();
       that.date = (DocumentProxy) this.date.clone();

       that.name.addUndoableEditListener(that);
       that.description.addUndoableEditListener(that);
       that.author.addUndoableEditListener(that);
       that.date.addUndoableEditListener(that);

       return that;
    }
    
    public void dispose() {
        super.dispose();
        
        if (description != null) {
            description.removeUndoableEditListener(this);
            description = null;
        }
        if (author != null) {
            author.removeUndoableEditListener(this);
            author = null;
        }
        if (date != null) {
            date.removeUndoableEditListener(this);
            date = null;
        }
        if (name != null) {
            name.removeUndoableEditListener(this);
            name.setText(null);
            name = null;
        }
    }
}


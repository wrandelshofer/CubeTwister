/*
 * @(#)EntityModel.java  1.1  2006-09-24
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.cubetwister.doc.*;
import ch.randelshofer.beans.*;
import ch.randelshofer.geom3d.*;
import ch.randelshofer.rubik.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.undo.*;
import ch.randelshofer.gui.text.*;
import ch.randelshofer.gui.tree.TreeNodeImpl;

import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.text.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

/**
 * Holds an entity which can be managed by a DocumentModel.
 * <p>
 * Requires that the root node of the tree managed by DocumentModel is
 * an Entity Model and that its UserObject is the DocumentMdel.
 * This is used to ensure proper listener notifications for object
 * insertion and removal.
 *
 * @author  werni
 * @version 1.1 2006-09-24 Revised.
 * <br>1.0 2001-10-05
 */
public class EntityModel extends TreeNodeImpl<EntityModel> implements Cloneable, UndoableEditListener {
    private final static long serialVersionUID = 1L;
    public final static String PROP_CHILD_COUNT = "childCount";
    
    /**
     * Listener support.
     */
    private PropertyChangeSupport propertySupport;
    
    private boolean removable = true;
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (propertySupport == null) propertySupport = new PropertyChangeSupport(this);
        propertySupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (propertySupport != null) {
            propertySupport.removePropertyChangeListener(listener);
            if (! propertySupport.hasListeners(null)) propertySupport = null;
        }
    }
    
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (propertySupport != null)
            propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (propertySupport != null)
            propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertySupport != null) {
            propertySupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    protected boolean hasPropertyListeners() {
        return propertySupport != null && propertySupport.hasListeners(null);
    }
    
    
    
    /**
     * Creates a new EntityModel.
     */
    public EntityModel() {
        super(null, true);
    }
    /**
     * Creates a new EntityModel.
     */
    public EntityModel(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }
    
    /**
     * Returns the DocumentMdel which holds this model
     * or null if this EntityModel is not part of a document.
     * Assumes that the root node of the tree is
     * an instance of EntityModel and that
     * its user object is the DocumentModel.
     */
    public DocumentModel getDocument() {
        Object obj = ((DefaultMutableTreeNode) getRoot()).getUserObject();
        return (obj instanceof DocumentModel) ? (DocumentModel) obj : null;
    }
    
    /**
     * Notify all listeners that have registered interest
     * at the DocumentModel for notification on this event type.
     * The event instance is lazily created using the parameters
     * passed into the fire method.
     */
    protected void fireUndoableEditHappened(UndoableEdit edit) {
        DocumentModel root = getDocument();
        if (root != null) {
            root.fireUndoableEdit(edit);
        } else {
            edit.die();
        }
    }
    
    public void fireNodeStructureChanged() {
        DocumentModel p = getDocument();
        if (p != null) {
            p.nodeStructureChanged(this);
        }
    }
    public void fireNodeChanged() {
        DocumentModel p = getDocument();
        if (p != null) {
            p.nodeChanged(this);
        }
    }
    
    
    /**
     * Returns a shallow copy of this model.
     * All references to other entitys, are maintained.
     * The new model has no parent but it has all the
     * the children that are required to hold the properties
     * of the entity.
     * The new model has no PropertyChangeListener's.
     */
    @Override
    public EntityModel clone() {
        EntityModel that = super.clone();
        that.propertySupport = null;
        return that;
    }
    
    /**
     * Returns true if the node may be removed from its parent.
     */
    public boolean isRemovable() {
        return removable;
    }
    
    /**
     * Sets the 'removable' property.
     * @see #isRemovable()
     */
    public void setRemovable(boolean b) {
        removable = b;
    }
    
    public String toString() {
        return super.toString()+" ("+getChildCount()+")";
    }
    
        public void undoableEditHappened(UndoableEditEvent e) {
            fireUndoableEditHappened(e.getEdit());
        }
    
    public void dispose() {
        for (EntityModel child : getChildren()) {
            child.dispose();
        }
        removeAllChildren();
        propertySupport = null;
    }
    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
        firePropertyChange("childCount", getChildCount() - 1, getChildCount());
    }
    @Override
    public void remove(int childIndex) {
        super.remove(childIndex);
        firePropertyChange("childCount", getChildCount() + 1, getChildCount());
    }
    
    public boolean isDefaultCube() {
        return false;
    }
    public boolean isDefaultNotation() {
        return false;
    }
}

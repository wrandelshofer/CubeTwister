/*
 * @(#)UndoableIntEdit.java 1.0  2001-10-12
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.undo;

import javax.swing.undo.*;

/**
 * This is an abstract class for undoable int properties.
 *
 * @author  Werner Randelshofer
 * @version 1.0 2001-10-12
 */
public abstract class UndoableIntEdit extends javax.swing.undo.AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;
    protected Object source;
    protected String propertyName;
    protected int oldValue;
    protected int newValue;
    protected boolean isCoalesce;
    
    /** 
     * Creates new IntPropertyEdit of which consecutive edits are not coalesced.
     * @param source The Object to which the property belongs.
     * @param propertyName The name of the property.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    public UndoableIntEdit(Object source, String propertyName, int oldValue, int newValue) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.isCoalesce = false;
    }
    /** 
     * Creates new IntPropertyEdit 
     * @param source The Object to which the property belongs.
     * @param propertyName The name of the property.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     * @param coalesce Set to true, if consecutive edits shall be coalesced.
     */
    public UndoableIntEdit(Object source, String propertyName, int oldValue, int newValue, boolean coalesce) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.isCoalesce = coalesce;
    }

    /**
     * Re-apply the edit, assuming that it has been undone.
     */
    public void redo() {
        super.redo();
        revert(oldValue, newValue);
    }
    
    /**
     * Undo the edit that was made.
     */
    public void undo() {
        super.undo();
        revert(newValue, oldValue);
    }
    
    /**
     * The name to be displayed in the undo/redo menu.
     */
    public String getPresentationName() {
        return propertyName;
    }
    
    /**
     * This UndoableEdit should absorb anEdit if it can. Return true
     * if anEdit has been incoporated, false if it has not.
     *
     * <p>Typically the receiver is already in the queue of a
     * UndoManager (or other UndoableEditListener), and is being
     * given a chance to incorporate anEdit rather than letting it be
     * added to the queue in turn.</p>
     *
     * <p>If true is returned, from now on anEdit must return false from
     * canUndo() and canRedo(), and must throw the appropriate
     * exception on undo() or redo().</p>
     */
    public boolean addEdit(UndoableEdit anEdit) {
        if (isCoalesce && anEdit instanceof UndoableIntEdit) {
            UndoableIntEdit that = (UndoableIntEdit) anEdit;
            if (that.source == this.source 
            && that.propertyName.equals(this.propertyName)) {
                this.newValue = that.newValue;
                that.die();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Revert the property from the
     * oldValue to the newValue.
     */
    public abstract void revert(int oldValue, int newValue);
}

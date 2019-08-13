/* @(#)UndoableBooleanEdit.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.undo;

import javax.swing.undo.*;

/**
 * This is an abstract class for undoable int properties.
 * If the property is changed multiple times in sequence, then
 * these edit events are coalesced.
 *
 * @author  Werner Randelshofer
 */
public abstract class UndoableBooleanEdit extends javax.swing.undo.AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;
    protected Object source;
    protected String propertyName;
    protected boolean oldValue;
    protected boolean newValue;
    private boolean isSignificant;
    
    /** 
     * Creates new UndoableBooleanEdit 
     * @param source The Object to which the property belongs.
     * @param propertyName The name of the property.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    public UndoableBooleanEdit(Object source, String propertyName, boolean oldValue, boolean newValue) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.isSignificant = true;
    }
    /** 
     * Creates new UndoableBooleanEdit 
     * @param source The Object to which the property belongs.
     * @param propertyName The name of the property.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     * @param isSignificant The significance of the edit event.
     */
    public UndoableBooleanEdit(Object source, String propertyName, boolean oldValue, boolean newValue, boolean isSignificant) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.isSignificant = isSignificant;
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
        /*
        if (anEdit instanceof UndoableBooleanEdit) {
            UndoableBooleanEdit that = (UndoableBooleanEdit) anEdit;
            if (that.source == this.source 
            && that.propertyName.equals(this.propertyName)) {
                this.newValue = that.newValue;
                that.die();
                return true;
            }
        }
         */
        return false;
    }
    
    /**
     * Revert the property from the
     * oldValue to the newValue.
     */
    public abstract void revert(boolean oldValue, boolean newValue);
    

    /**
     * Returns false if this edit is insignificant 
     * - for example one that maintains the user's selection, 
     * but does not change any model state. This status can be 
     * used by an UndoableEditListener (like UndoManager) when 
     * deciding which UndoableEdits to present to the user as 
     * Undo/Redo options, and which to perform as side effects 
     * of undoing or redoing other events. 
     */
    public boolean isSignificant() {
        return isSignificant;
    }
}


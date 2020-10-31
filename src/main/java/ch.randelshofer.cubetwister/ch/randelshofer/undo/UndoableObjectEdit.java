/*
 * @(#)UndoableObjectEdit.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.undo;

import javax.swing.undo.UndoableEdit;

/**
 * This is an abstract class for undoable Object properties.
 *
 * @author Werner Randelshofer
 */
public abstract class UndoableObjectEdit extends javax.swing.undo.AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;
    protected Object source;
    protected String propertyName;
    protected Object oldValue;
    protected Object newValue;

    /**
     * Creates new IntPropertyEdit
     *
     * @param source       The Object to which the property belongs.
     * @param propertyName The name of the property.
     * @param oldValue     The old value of the property.
     * @param newValue     The new value of the property.
     */
    public UndoableObjectEdit(Object source, String propertyName, Object oldValue, Object newValue) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
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
        if (anEdit instanceof UndoableObjectEdit) {
            UndoableObjectEdit that = (UndoableObjectEdit) anEdit;
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
    public abstract void revert(Object oldValue, Object newValue);
}


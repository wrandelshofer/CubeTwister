/* @(#)UndoableTreeSelectionModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.tree;


import ch.randelshofer.undo.Undoable;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.StateEditable;
import javax.swing.undo.UndoableEdit;

/**
 * Supports UndoableEditListeners.
 *
 * @author  Werner Randelshofer
 */
public interface UndoableTreeSelectionModel
extends TreeSelectionModel,Undoable, StateEditable {
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
     public void fireUndoableEditEvent(UndoableEdit edit);
}

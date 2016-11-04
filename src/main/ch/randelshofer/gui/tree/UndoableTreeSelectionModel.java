/*
 * @(#)UndoableTreeSelectionModel.java  2.0  2011-01-22
 *
 * Copyright (c) 2001-2011 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
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
 * @version 2.0 2011-01-22 Turned class into an interface.
 * <br>1.0.1 2011-01-19 Removes unused imports.
 * <br>1.0 2001-10-09
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

/*
 * @(#)UndoRedoManager.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.undo;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Same as javax.swing.UndoManager but provides actions for undo and
 * redo operations.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.3 2003-03-16 Texts and accelerator keys are now read from a
 * resource bundle.
 * <br>1.1.2 2003-03-12 Actions use now KeyStroke objects instead of
 * String objects as accelerator keys.
 * <br>1.1.1 2002-05-10 Method addEdit is now smarter when
 * determining whether an edit is significant.
 * <br>1.1 2002-04-08 Method hasSignificantEdits/clearChanged added.
 * <br>1.0 2001-10-09
 */
public class UndoRedoManager extends UndoManager {
    private final static long serialVersionUID = 1L;
    /**
     * Listener support.
     */
    private EventListenerList listenerList;
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;
    /**
     * This flag is set to true when at
     * least one significant UndoableEdit
     * has been added to the manager since the
     * last call to discardAllEdits.
     */
    private boolean hasSignificantEdits = false;
    
    /**
     * This flag is set to true when an undo or redo
     * operation is in progress. The UndoRedoManager
     * ignores all incoming UndoableEdit events while
     * this flag is true.
     */
    private boolean undoOrRedoInProgress;
    
    /**
     * Sending this UndoableEdit event to the UndoRedoManager
     * disables the Undo and Redo functions of the manager.
     */
    public final static UndoableEdit DISCARD_ALL_EDITS = new AbstractUndoableEdit() {
    private final static long serialVersionUID = 1L;
        public boolean canUndo() {
            return false;
        }
        public boolean canRedo() {
            return false;
        }
    };
    
    /**
     * Undo Action for use in a menu bar.
     */
    private class UndoAction
    extends AbstractAction {
    private final static long serialVersionUID = 1L;
        public UndoAction() {
            super(labels.getTextProperty("edit.undo"));
            putValue(Action.ACCELERATOR_KEY, labels.getAcceleratorProperty("edit.undo"));
            setEnabled(false);
        }
        
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent evt) {
            try {
                undo();
            } catch (CannotUndoException e) {
                System.out.println("Cannot undo: "+e);
            }
        }
        
    }
    
    /**
     * Redo Action for use in a menu bar.
     */
    private class RedoAction
    extends AbstractAction {
    private final static long serialVersionUID = 1L;
        public RedoAction() {
            super(labels.getTextProperty("edit.redo"));
            putValue(Action.ACCELERATOR_KEY, labels.getAcceleratorProperty("edit.redo"));
            setEnabled(false);
        }
        
        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent evt) {
            try {
                redo();
            } catch (CannotRedoException e) {
                System.out.println("Cannot redo: "+e);
            }
        }
        
    }
    
    /** The undo action instance. */
    private UndoAction undoAction;
    /** The redo action instance. */
    private RedoAction redoAction;
    
    /** Creates new UndoRedoManager */
    public UndoRedoManager() {
        labels = ResourceBundleUtil.getBundle("org.jhotdraw.undo.Labels");
        undoAction = new UndoAction();
        redoAction = new RedoAction();
    }
    
    /**
     * Discards all edits.
     */
    public void discardAllEdits() {
        super.discardAllEdits();
        updateActions();
        if (hasSignificantEdits) {
        hasSignificantEdits = false;
        fireStateChanged();
        }
    }
    
    /**
     * Returns true if at least one significant UndoableEdit
     * has been added since the last call to discardAllEdits.
     */
    public boolean hasSignificantEdits() {
        return hasSignificantEdits;
    }
    
    /**
     * If inProgress, inserts anEdit at indexOfNextAdd, and removes
     * any old edits that were at indexOfNextAdd or later. The die
     * method is called on each edit that is removed is sent, in the
     * reverse of the order the edits were added. Updates
     * indexOfNextAdd.
     *
     * <p>If not inProgress, acts as a CompoundEdit</p>
     *
     * <p>Regardless of inProgress, if undoOrRedoInProgress,
     * calls die on each edit that is sent.</p>
     *
     *
     * @see CompoundEdit#end
     * @see CompoundEdit#addEdit
     */
    public boolean addEdit(UndoableEdit anEdit) {
        if (undoOrRedoInProgress) {
            anEdit.die();
            return true;
        }
        boolean success = super.addEdit(anEdit);
        updateActions();
        if (success && anEdit.isSignificant() && editToBeUndone() == anEdit) {
            if (! hasSignificantEdits) {
                hasSignificantEdits = true;
                fireStateChanged();
            }
        }
        return success;
    }
    /**
     * Gets the undo action for use as an Undo menu item.
     */
    public Action getUndoAction() {
        return undoAction;
    }
    
    /**
     * Gets the redo action for use as a Redo menu item.
     */
    public Action getRedoAction() {
        return redoAction;
    }
    
    /**
     * Updates the properties of the UndoAction
     * and of the RedoAction.
     */
    private void updateActions() {
        if (canUndo()) {
            undoAction.setEnabled(true);
            undoAction.putValue(Action.NAME, getUndoPresentationName());
        } else {
            undoAction.setEnabled(false);
            undoAction.putValue(Action.NAME, "Undo");
        }
        
        if (canRedo()) {
            redoAction.setEnabled(true);
            redoAction.putValue(Action.NAME, getRedoPresentationName());
        } else {
            redoAction.setEnabled(false);
            redoAction.putValue(Action.NAME, "Redo");
        }
    }
    
    /**
     * Undoes the last edit event.
     * The UndoRedoManager ignores all incoming UndoableEdit events,
     * while undo is in progress.
     */
    public void undo()
    throws CannotUndoException {
        undoOrRedoInProgress = true;
        try {
            super.undo();
        } finally {
            undoOrRedoInProgress = false;
            updateActions();
        }
    }
    
    /**
     * Redoes the last undone edit event.
     * The UndoRedoManager ignores all incoming UndoableEdit events,
     * while redo is in progress.
     */
    public void redo()
    throws CannotUndoException {
        undoOrRedoInProgress = true;
        try {
            super.redo();
        } finally {
            undoOrRedoInProgress = false;
            updateActions();
        }
    }
    
    /**
     * Undoes or redoes the last edit event.
     * The UndoRedoManager ignores all incoming UndoableEdit events,
     * while undo or redo is in progress.
     */
    public void undoOrRedo()
    throws CannotUndoException, CannotRedoException {
        undoOrRedoInProgress = true;
        try {
            super.undoOrRedo();
        } finally {
            undoOrRedoInProgress = false;
            updateActions();
        }
    }
    
    /**
     * Adds a change listener.
     * ChangeListener's get notified, when the state of hasSignificantEdits
     * changes.
     * @see #hasSignificantEdits
     */
    public void addChangeListener(ChangeListener l) {
        if (listenerList == null) listenerList = new EventListenerList();
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) {
        if (listenerList != null) {
            listenerList.remove(ChangeListener.class, l);
        }
    }
    
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    protected void fireStateChanged() {
        if (listenerList != null) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            ChangeEvent event = null;
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==ChangeListener.class) {
                    // Lazily create the event:
                    if (event == null)
                        event = new ChangeEvent(this);
                    ((ChangeListener)listeners[i+1]).stateChanged(event);
                }
            }
        }
    }
}

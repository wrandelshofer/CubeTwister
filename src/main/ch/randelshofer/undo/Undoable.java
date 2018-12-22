/* @(#)Undoable.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.undo;

import javax.swing.event.*;

/**
 * This interface is implemented by components, which support undo
 * and redo.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface Undoable {

    /**
     * Adds an UndoableEditListener.
     */
    public void addUndoableEditListener(UndoableEditListener l);
    
    /**
     * Removes an UndoableEditListener.
     */
    public void removeUndoableEditListener(UndoableEditListener l);
    
}


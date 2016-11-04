/*
 * @(#)UndoCoalescingBoundedRangeModel.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.undo;

import javax.swing.event.*;
import javax.swing.undo.*;

/**
 * UndoCoalescingBoundedRangeModel.
 * @author  werni
 */
public class UndoCoalescingBoundedRangeModel extends javax.swing.DefaultBoundedRangeModel {
    private final static long serialVersionUID = 1L;
    private CompositeEdit compositeEdit;
    protected EventListenerList listenerList = new EventListenerList();
    
    /** Creates a new instance of UndoCoalescingBoundedRangeModel */
    public UndoCoalescingBoundedRangeModel() {
    }
    public UndoCoalescingBoundedRangeModel(int value, int extent, int min, int max) {
        super(value, extent, min, max);
    }
    
    public void setValueIsAdjusting(boolean b) {
        //System.out.println("setValueIsAdjusting("+b+") "+this);
        if (b) {
            compositeEdit = new CompositeEdit("Slider");
            fireUndoableEditEvent(compositeEdit);

        } else {
            fireUndoableEditEvent(compositeEdit);
            compositeEdit = null;
        }
    }
    /**
     * Removes an UndoableEditListener.
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }
    
    /**
     * Adds an UndoableEditListener.
     */
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }
    
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    public void fireUndoableEditEvent(UndoableEdit edit) {
        UndoableEditEvent evt = null;
        
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==UndoableEditListener.class) {
                // Lazily create the event
                if (evt == null)
                    evt = new UndoableEditEvent(this, edit);
                ((UndoableEditListener)listeners[i+1]).undoableEditHappened(evt);
            }
        }
    }
}

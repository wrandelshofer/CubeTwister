/*
 * @(#)UndoCoalescingBoundedRangeModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.undo;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

/**
 * UndoCoalescingBoundedRangeModel.
 * @author Werner Randelshofer
 */
public class UndoCoalescingBoundedRangeModel extends javax.swing.DefaultBoundedRangeModel {
    private final static long serialVersionUID = 1L;
    @Nullable
    private CompositeEdit compositeEdit;
    @Nonnull
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

/*
 * @(#)DnDJList.java  3.0.1  2010-11-06
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui;

import ch.randelshofer.gui.list.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.*;

/**
 * A JList that supports drag and drop operations if its model
 * is an instance of MutableListModel.
 * <p>
 * The DnDJList supports Copy, Move and Link drag and drop actions.
 * The default is Copy and Move. To change the supported actions use
 * setSupportedDragActions and setSupportedDropActions.
 * <p>
 * FIXME: Implement Workarounds for Apple MRJ 1.3.1 Update 1 for Mac OS X 10.1.x
 * FIXME: Implement cloning.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>3.0 2008-03-21 Reimplemented with TransferHandler. Renamed from
 * DnDJList to JDnDList.
 * <br>2.2.1 2007-06-07 On dropActionChanged, we need always to call accept
 * drag.
 * <br>2.2 2006-10-24 Added timer for autoscroll.
 * <br>2.1.1 2006-01-07 Clear isUnderDrag variable when rejecting drop.
 * <br>2.1 2004-02-06 Moved all listener methods into the private inner
 * class EventHandler.
 * <br>2.0 2003-11-01 Revised.
 * <br>1.0 2002-12-17 Created.
 */
public class JDnDList extends MutableJList implements Autoscroll {
    private final static long serialVersionUID = 1L;

    /**
     * This inner class is used to prevent the API from being cluttered
     * by internal listeners.
     */
    private class EventHandler
            implements DropTargetListener, ListDataListener {
        // ===================================================================
        // Methods for drop target behaviour
        // ===================================================================

        /**
         * The drag operation has terminated
         * with a drop on this <code>DropTarget</code>.
         * This method is responsible for undertaking
         * the transfer of the data associated with the
         * gesture. The <code>DropTargetDropEvent</code>
         * provides a means to obtain a <code>Transferable</code>
         * object that represents the data object(s) to
         * be transfered.<P>
         * From this method, the <code>DropTargetListener</code>
         * shall accept or reject the drop via the
         * acceptDrop(int dropAction) or rejectDrop() methods of the
         * <code>DropTargetDropEvent</code> parameter.
         * <P>
         * Subsequent to acceptDrop(), but not before,
         * <code>DropTargetDropEvent</code>'s getTransferable()
         * method may be invoked, and data transfer may be
         * performed via the returned <code>Transferable</code>'s
         * getTransferData() method.
         * <P>
         * At the completion of a drop, an implementation
         * of this method is required to signal the success/failure
         * of the drop by passing an appropriate
         * <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
         * dropComplete(boolean success) method.
         * <P>
         * Note: The actual processing of the data transfer is not
         * required to finish before this method returns. It may be
         * deferred until later.
         * <P>
         * @param dtde the <code>DropTargetDropEvent</code>
         */
        @Override
        public void drop(DropTargetDropEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt drop dropAction(" + dndAction.get(new Integer(evt.getDropAction())));
            }

            int oldDropIndex = dropIndex;
            updateDropIndexAndDropAsChild(evt.getDropAction(), evt.getLocation(), evt.getCurrentDataFlavors());

            // Reject the drop if the action is ACTION_NONE
            // or if we don't have a suitable model
            int dropAction = evt.getDropAction();
            if (!isEnabled() || ((dropAction & getSupportedDropActions()) == DnDConstants.ACTION_NONE) || !(getModel() instanceof MutableListModel)) {
                if (VERBOSE) {
                    System.out.println("tgt drop dropAction(" + dropAction + "):rejectDrop because of drop action none or not-suitable list model");
                }
                evt.rejectDrop();
                // Erase the drop target state variables
                isUnderDrag = false;
                repaint();
                return;
            }

            MutableListModel m = (MutableListModel) getModel();

            // Reject the drop if we can't import the data at the current
            // insertion point
            if (!m.isImportable(evt.getCurrentDataFlavors(), dropAction, dropIndex, dropAsChild)) {
                if (VERBOSE) {
                    System.out.println("tgt drop dropAction(" + dropAction + "):rejectDrop because drop action not allowed at insertion point");
                }
                evt.rejectDrop();
                // Erase the drop target state variables
                isUnderDrag = false;
                repaint();
                return;
            }

            // Accept the drop
            if (VERBOSE) {
                System.out.println("tgt drop dropAction(" + dropAction + "):acceptDrop");
            }
            evt.acceptDrop(dropAction);

            // Remember the selected indices before we let the
            // model import the data
            int[] selectedIndices = getSelectedIndices();

            // import the data
            try {
                int count = m.importTransferable(evt.getTransferable(), dropAction, dropIndex, dropAsChild);

                // Restore the selected indices
                if (count > 0) {
                    ListSelectionModel sm = getSelectionModel();
                    sm.clearSelection();
                    for (int i = 0; i < selectedIndices.length; i++) {
                        int si = selectedIndices[i];
                        if (si >= dropIndex) {
                            si += count;
                        }
                        sm.addSelectionInterval(si, si);
                    }

                    scrollRectToVisible(getCellBounds(dropIndex, dropIndex + count - 1));
                }

                // Report success or failure
                evt.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                evt.dropComplete(false);
            }

            // Erase the drop target state variables
            isUnderDrag = false;
            dropIndex = -1;
            repaint();
        }

        /**
         * Called when a drag operation has
         * encountered the <code>DropTarget</code>.
         * <P>
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dragEnter(DropTargetDragEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt dragEnter action:" + dndAction.get(new Integer(evt.getDropAction())) + " " + evt.getLocation());
                DataFlavor[] flavors = evt.getCurrentDataFlavors();
                for (int i = 0; i < flavors.length; i++) {
                    System.out.println("              flavor:" + flavors[i]);
                }
            }

            // Reject the drag, if we are disabled or if we don't have a suitable model
            if (!isEnabled() || !(getModel() instanceof MutableListModel)) {
                dropIndex = -1;
                evt.rejectDrag();
                return;
            }

            updateDropIndexAndDropAsChild(evt.getDropAction(), evt.getLocation(), evt.getCurrentDataFlavors());
            isUnderDrag = true;
            repaint();
        }

        /**
         * Called when a drag operation is ongoing
         * on the <code>DropTarget</code>.
         * <P>
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dragOver(DropTargetDragEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt dragOver action:" + dndAction.get(new Integer(evt.getDropAction())) + " " + evt.getLocation());
            }

            // Determine over which element the mouse is hovering
            boolean oldDropAsChild = dropAsChild;
            int oldDropIndex = dropIndex;

            updateDropIndexAndDropAsChild(evt.getDropAction(), evt.getLocation(), evt.getCurrentDataFlavors());

            if (oldDropIndex != dropIndex || oldDropAsChild != dropAsChild) {
                repaintDropCursor(oldDropIndex, dropIndex);
            }
        }

        /**
         * The drag operation has departed
         * the <code>DropTarget</code> without dropping.
         * <P>
         * @param dte the <code>DropTargetEvent</code>
         */
        @Override
        public void dragExit(DropTargetEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt dragExit");
            }

            dropIndex = -1;
            isUnderDrag = false;
            repaint();
        }

        /**
         * Called if the user has modified
         * the current drop gesture.
         * <P>
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dropActionChanged(DropTargetDragEvent evt) {
            if (VERBOSE) {
                System.out.println("tgt drpActionChanged dropAction:" + dndAction.get(new Integer(evt.getDropAction())));
            }

            // Reject the drag, if we don't have a suitable model
            if (!(getModel() instanceof MutableListModel)) {
                evt.rejectDrag();
                return;
            }

            // Check if we can import the data at the current
            // insertion point
            int dropAction = evt.getDropAction();
            int oldDropIndex = dropIndex;
            updateDropIndexAndDropAsChild(dropAction, evt.getLocation(), evt.getCurrentDataFlavors());

            if (dropIndex == -1) {
                evt.acceptDrag(DnDConstants.ACTION_NONE);
            } else {
                evt.acceptDrag(dropAction);
            }

            if (oldDropIndex != dropIndex) {
                repaintDropCursor(oldDropIndex, dropIndex);
            }
        }

        /**
         * Sent when the contents of the list has changed in a way
         * that's too complex to characterize with the previous
         * methods.  Index0 and index1 bracket the change.
         *
         * @param e  a ListDataEvent encapuslating the event information
         */
        @Override
        public void contentsChanged(ListDataEvent e) {
        }

        /**
         * Sent after the indices in the index0,index1
         * interval have been inserted in the data model.
         * The new interval includes both index0 and index1.
         *
         * @param e  a ListDataEvent encapuslating the event information
         */
        @Override
        public void intervalAdded(ListDataEvent evt) {
            if (draggedIndices != null) {
                int index0 = evt.getIndex0();
                int count = evt.getIndex1() - index0 + 1;
                for (int i = 0; i < draggedIndices.length; i++) {
                    if (draggedIndices[i] >= index0) {
                        draggedIndices[i] += count;
                    }
                }
            }
        }

        /**
         * Sent after the indices in the index0,index1 interval
         * have been removed from the data model.  The interval
         * includes both index0 and index1.
         *
         * @param e  a ListDataEvent encapuslating the event information
         */
        @Override
        public void intervalRemoved(ListDataEvent evt) {
            if (draggedIndices != null) {
                int index0 = evt.getIndex0();
                int index1 = evt.getIndex1();
                int count = index1 - index0 + 1;
                for (int i = 0; i < draggedIndices.length; i++) {
                    if (index0 <= draggedIndices[i] && draggedIndices[i] <= index1) {
                        draggedIndices[i] = -1;
                    } else if (draggedIndices[i] > index1) {
                        draggedIndices[i] -= count;
                    }
                }
            }
        }
    }
    private EventHandler eventHandler = new EventHandler();

    private class ListTransferHandler extends TransferHandler {
    private final static long serialVersionUID = 1L;

        @Override
        public int getSourceActions(JComponent c) {
            if (getSelectedIndex() == -1) {
                return DnDConstants.ACTION_NONE;
            } else {
                if (getModel() instanceof MutableListModel) {
                    MutableListModel m = (MutableListModel) getModel();
                    int[] indices = getSelectedIndices();
                    boolean isRemovable = true;
                    for (int i = 0; i < indices.length; i++) {
                        if (!m.isRemovable(indices[i])) {
                            isRemovable = false;
                            break;
                        }
                    }
                    return (isRemovable) ? DnDConstants.ACTION_COPY_OR_MOVE : DnDConstants.ACTION_COPY;
                } else {
                    return DnDConstants.ACTION_COPY;
                }
            }
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (getSelectedIndex() == -1) {
                return null;
            } else {
                int[] indices = getSelectedIndices();
                if (getModel() instanceof MutableListModel) {
                    MutableListModel m = (MutableListModel) getModel();
                    return m.exportTransferable(indices);
                } else {
                    return ListModels.createDefaultTransferable(getModel(), indices);
                }
            }
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == DnDConstants.ACTION_MOVE) {
                MutableListModel m = (MutableListModel) getModel();
                int[] indices = getSelectedIndices();
                for (int i = indices.length - 1; i >= 0; i--) {
                    m.remove(indices[i]);
                }
            }
        }
    }
    private ListTransferHandler transferHandler = new ListTransferHandler();
    /**
     * Methods print diagnostic output to System.out, if true.
     */
    private static boolean VERBOSE = false;
    /**
     * The drag gesture recognizer is used to identify
     * a drag initiaging user gesture.
     */
    private DragGestureRecognizer dragGestureRecognizer;
    /**
     * The autoscroll border shows a highlight color when the
     * DnDJList has focus or when the user drags an item over it.
     */
    private Border autoscrollBorder = new MatteBorder(2, 2, 2, 2, UIManager.getColor("List.selectionBackground"));
    /**
     * Name of DragAction/DropAction. Used for diagnostic output only.
     */
    private final static HashMap<Integer,String> dndAction = new HashMap<Integer,String>();

    static {
        dndAction.put( DnDConstants.ACTION_NONE, "NONE");
        dndAction.put(DnDConstants.ACTION_COPY, "COPY");
        dndAction.put(DnDConstants.ACTION_MOVE, "MOVE");
        dndAction.put(DnDConstants.ACTION_COPY_OR_MOVE, "COPY OR MOVE");
        dndAction.put(DnDConstants.ACTION_LINK, "LINK");
        dndAction.put(DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK, "COPY OR MOVE OR LINK");
    }
    /**
     * Drop insertion index.
     * <p>
     * Values in the range <code>0 &lt;= dropInsertIndex &lt; getModel().getSize()</code>
     * specify potential insertion points for the the current drag and drop operation.
     * <p>
     * The Value is set to <code>-1</code> when the DnDJList is
     * not an active target of a drag and drop operation or when
     * the mouse hovers over an area of the list which is not suited
     * for dropping an object.
     * <p>
     * The drop insertion index is set when a drag enter operation is
     * detected, and updated during drag over. The drop index is erased
     * (set to -1), when a drag exit is detected or when the user drops
     * an object on the list.
     */
    private int dropIndex = -1;
    /**
     * dropAsChild is set to true, when the drag cursor hovers over
     * the bounds of a list cell.
     * <p>
     * The value true indicates, that the dragged object should
     * be added as a child of the list item indicated by dropIndex.
     * <p>
     * The value false indicates, that the dragged object should
     * be inserted at the dropIndex as a new member into the list.
     */
    private boolean dropAsChild = false;
    /**
     * Indices of the dragged ListModel elements. If a drag operation
     * ends with a DnDConstants.ACTION_MOVE, the indices are used
     * to determine which elements must be removed from the MutableListModel.
     * <p>
     * The indices are set when a drag gesture has been started by
     * this DnDJList and are kept up to date with ListModel changes
     * until the dragDropEnd event is received.
     */
    private int[] draggedIndices;
    /**
     * The margin for the autoscroll area.
     * Must be less than the height of a list item.
     */
    private final static int AUTOSCROLL_MARGIN = 8;
    /**
     * This attribute is set to true, when the component is
     * an active drag target.
     * The value is set to true, when a dragEnter event is
     * received and set to false when a dragExit or a
     * drop event is received.
     */
    private boolean isUnderDrag;
    /**
     * This attribute is set to true, when the component
     * draws a drop cursor when it is an active drop target.
     */
    private boolean paintsDropCursor = true;

    /**
     * Constructs a DnDJList with an empty DefaultMutableListModel.
     */
    public JDnDList() {
        this(new DefaultMutableListModel<Object>());

    }

    /**
     * Constructs a DnDJList with the specified MutableListModel.
     */
    public JDnDList(MutableListModel m) {
        super(m);
        JList list;
        initComponents();

        /*
        dragGestureRecognizer = DragSource.getDefaultDragSource()
        .createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this.eventHandler);
         */
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this.eventHandler);

        setTransferHandler(transferHandler);
        setDragEnabled(true);

        ActionMap am = getActionMap();
        am.put("delete", new AbstractAction() {
    private final static long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                delete();
            }
        });
        InputMap im = getInputMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
    }

    /**
     * Sets the model that represents the contents or "value" of the
     * list. The DnDJList supports drag and drop operations only when
     * the model is an instance of MutableListModel.
     */
    @Override
    public void setModel(ListModel m) {
        if (getModel() != null) {
            getModel().removeListDataListener(this.eventHandler);
        }
        super.setModel(m);
        if (getModel() != null) {
            m.addListDataListener(this.eventHandler);
        }
    }

    /**
     * This method sets the permitted source drag action(s)
     * for this DnDJList.
     * <p>
     * This is a bound property.
     *
     * @param actions the permitted source drag action(s).
     * The value is constructed by doing a logical OR of
     * the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public void setSupportedDragActions(int actions) {
        int oldValue = dragGestureRecognizer.getSourceActions();
        dragGestureRecognizer.setSourceActions(actions);
        firePropertyChange("supportedDragActions", oldValue, actions);
    }

    /**
     * This method returns an int representing the type of
     * drag action(s) this DnDJList supports.
     *
     * @return the currently permitted drag action(s)
     * The value is constructed by doing a logical OR of
     * the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public int getSupportedDragActions() {
        return (dragGestureRecognizer == null) ? DnDConstants.ACTION_NONE : dragGestureRecognizer.getSourceActions();
    }

    /**
     * This method sets the permitted target drop action(s)
     * for this DnDJList.
     *
     * @param actions the permitted target drop action(s).
     * The value is constructed by doing a logical OR of
     * the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public void setSupportedDropActions(int actions) {
        int oldValue = getDropTarget().getDefaultActions();
        getDropTarget().setDefaultActions(actions);
        firePropertyChange("supportedDropActions", oldValue, actions);
    }

    /**
     * This method returns an int representing the type of
     * drop action(s) this DnDJList supports.
     *
     * @return the currently permitted drop action(s)
     * The value is constructed by doing a logical OR of
     * the desired actions specified by the
     * DnDConstants.ACTION_... constants.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public int getSupportedDropActions() {
        return getDropTarget().getDefaultActions();
    }

    /**
     * Sets the enabled state of the DnDJList.
     * Also sets the drop target's active state.
     *
     * @see java.awt.dnd.DropTarget#setActive(boolean)
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        getDropTarget().setActive(b);
    }

    /**
     * Turns the drop cursor feature on or of.
     * <p>
     * The drop cursor indicates where an item
     * will be inserted when the user drops
     * an object at the current mouse location.
     * <p>
     * The drop cursor is shown only, when the DnDJList
     * is the target of the current drag and drop operation
     * and when it can accept the dragged object.
     */
    public void setPaintsDropCursor(boolean b) {
        boolean oldValue = paintsDropCursor;
        paintsDropCursor = b;
        firePropertyChange("paintsDropCursor", oldValue, b);
    }

    /**
     * Returns true if the DnDJList paints a
     * drop cursor when it is an active drop target.
     */
    public boolean getPaintsDropCursor() {
        return paintsDropCursor;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents

    /**
     * Updates the value of dropIndex and dropAsChild instance variables.
     */
    private void updateDropIndexAndDropAsChild(int dropAction, Point location, DataFlavor[] flavors) {
        MutableListModel m = (MutableListModel) getModel();

        dropIndex = locationToDropIndex(location);

        int childIndex = locationToDropAsChildIndex(location);
        dropAsChild = false;
        if (childIndex != -1 && m.isImportable(flavors, dropAction, dropIndex, true)) {
            // allow dropAsChild only, if the drop index is not contained in the array of dragged indices
            if (draggedIndices == null || Arrays.binarySearch(draggedIndices, childIndex) < 0) {
                dropAsChild = true;
                dropIndex = childIndex;
            }
        } else {
            if (!m.isImportable(flavors, dropAction, dropIndex, false)) {
                dropIndex = -1;
            }
        }
    }

    /**
     * Clears the selection - after calling this method isSelectionEmpty()
     * will return true.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @see ListSelectionModel#clearSelection
     * @see #isSelectionEmpty
     * @see #addListSelectionListener
     */
    @Override
    public void clearSelection() {
        // Allow changes of the selection only, if no drag and drop operation
        // is in progress.
        if (draggedIndices == null) {
            super.clearSelection();
        }
    }

    /**
     * Select the specified interval.  Both the anchor and lead indices are
     * included.  It's not neccessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @param anchor The first index to select
     * @param lead The last index to select
     * @see ListSelectionModel#setSelectionInterval
     * @see #addSelectionInterval
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    @Override
    public void setSelectionInterval(int anchor, int lead) {
        // Allow changes of the selection only, if no drag and drop operation
        // is in progress.
        if (draggedIndices == null) {
            super.setSelectionInterval(anchor, lead);
        }
    }

    /**
     * Set the selection to be the union of the specified interval with current
     * selection.  Both the anchor and lead indices are
     * included.  It's not neccessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @param anchor The first index to add to the selection
     * @param lead The last index to add to the selection
     * @see ListSelectionModel#addSelectionInterval
     * @see #setSelectionInterval
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    @Override
    public void addSelectionInterval(int anchor, int lead) {
        // Allow changes of the selection only, if no drag and drop operation
        // is in progress.
        if (draggedIndices == null) {
            super.addSelectionInterval(anchor, lead);
        }
    }

    /**
     * Set the selection to be the set difference of the specified interval
     * and the current selection.  Both the anchor and lead indices are
     * removed.  It's not neccessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @param index0 The first index to remove from the selection
     * @param index1 The last index to remove from the selection
     * @see ListSelectionModel#removeSelectionInterval
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     * @see #addListSelectionListener
     */
    @Override
    public void removeSelectionInterval(int index0, int index1) {
        // Allow changes of the selection only, if no drag and drop operation
        // is in progress.
        if (draggedIndices == null) {
            super.removeSelectionInterval(index0, index1);
        }
    }

    /**
     * Select a single cell.
     *
     * @param index The index of the one cell to select
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     * description: The index of the selected cell.
     */
    @Override
    public void setSelectedIndex(int index) {
        // Allow changes of the selection only, if no drag and drop operation
        // is in progress.
        if (draggedIndices == null) {
            super.setSelectedIndex(index);
        }
    }

    /**
     * Select a set of cells.
     *
     * @param indices The indices of the cells to select
     * @see ListSelectionModel#addSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    @Override
    public void setSelectedIndices(int[] indices) {
        // Allow changes of the selection only, if no drag and drop operation
        // is in progress.
        if (draggedIndices == null) {
            super.setSelectedIndices(indices);
        }
    }

    /**
     * Returns the viewportBounds cell bounds of a list item.
     * The viewportBounds cell bounds is the area which contains the text
     * and the icon of a list item.
     */
    private Rectangle getInnerCellBounds(int index) {
        Rectangle r = getCellBounds(index, index);

        Dimension d = getCellRenderer().getListCellRendererComponent(
                this, getModel().getElementAt(index), index, false, false).getPreferredSize();

        r.width = Math.min(r.width, d.width);
        r.height = Math.min(r.height, d.height);

        return r;
    }

    // ===================================================================
    // Methods for autoscroll behaviour
    // ===================================================================
    /**
     * This method returns the <code>Insets</code> describing
     * the autoscrolling region or border relative
     * to the geometry of the implementing Component.
     * <P>
     * This value is read once by the <code>DropTarget</code>
     * upon entry of the drag <code>Cursor</code>
     * into the associated <code>Component</code>.
     * <P>
     * @return the Insets
     */
    @Override
    public Insets getAutoscrollInsets() {
        // FIXME - We are returning here the whole are of the
        //    component.
        return new Insets(0, 0, getHeight(), getWidth());
    }

    /**
     * notify the <code>Component</code> to autoscroll
     * <P>
     * @param location A <code>Point</code> indicating the
     * location of the cursor that triggered this operation.
     */
    @Override
    public void autoscroll(Point location) {
        if (isUnderDrag) {

            Rectangle visibleRect = getVisibleRect();
            Rectangle noscrollRect = (Rectangle) visibleRect.clone();
            noscrollRect.grow(-AUTOSCROLL_MARGIN, -AUTOSCROLL_MARGIN);

            if (location.y < noscrollRect.y) {
                // scroll top downward
                int dy = getScrollableUnitIncrement(visibleRect, SwingConstants.VERTICAL, -1);
                if (dy != 0) {
                    Rectangle r = new Rectangle(visibleRect.x, visibleRect.y - dy, visibleRect.width, dy);
                    scrollRectToVisible(r);

                    // FIXME - We are altering here the point that is passed as input,
                    //         without this. Autoscroll does not work.
                    location.y -= dy;
                    dropIndex = locationToDropIndex(location);

                    // We have to repaint the first and the last visible line of the
                    // list, because we have painted our autoscroll border over it.
                    // FIXME - Optimize this call to repaint.
                    repaint();
                }
                return;
            } else if (location.y > noscrollRect.y + noscrollRect.height) {
                // scroll bottom upward
                int dy = getScrollableUnitIncrement(visibleRect, SwingConstants.VERTICAL, 1);
                if (dy != 0) {
                    Rectangle r = new Rectangle(visibleRect.x, visibleRect.y + visibleRect.height, visibleRect.width, dy);
                    scrollRectToVisible(r);

                    // FIXME - We are altering here the point that is passed as input,
                    //         without this. Autoscroll does not work.
                    location.y += dy;
                    dropIndex = locationToDropIndex(location);
                    // We have to repaint the first and the last visible line of the
                    // list, because we have painted our autoscroll border over it.
                    // FIXME - Optimize this call to repaint.
                    repaint();
                }
                return;
            }

            if (location.x < noscrollRect.x) {
                // scroll left side to the right
                int dx = getScrollableUnitIncrement(visibleRect, SwingConstants.HORIZONTAL, 1);
                if (dx != 0) {
                    Rectangle r = new Rectangle(visibleRect.x - dx, visibleRect.y, dx, visibleRect.height);
                    scrollRectToVisible(r);

                    // We have to repaint a portion at the left and at the right of
                    // the list.
                    // FIXME - Optimize this call to repaint.
                    repaint();
                }
            } else if (location.x > noscrollRect.x + noscrollRect.width) {
                // scroll right side to the left
                int dx = getScrollableUnitIncrement(visibleRect, SwingConstants.HORIZONTAL, -1);
                if (dx != 0) {
                    Rectangle r = new Rectangle(visibleRect.x + visibleRect.width, visibleRect.y, dx, visibleRect.height);
                    scrollRectToVisible(r);

                    // We have to repaint a portion at the left and at the right of
                    // the list.
                    // FIXME - Optimize this call to repaint.
                    repaint();
                }
            }
        }
    }

    /**
     * Convert a point in <code>JList</code> coordinates to the nearest
     * index in the list, where an object can be dropped.
     * Use ((MutableListModel) getModel()).canImport(...) to determine
     * if the model actually allows insertion of an object here.
     *
     * @param location the coordinates of the cell, relative to
     *			<code>JList</code>
     * @return the nearest index for dropping the object.
     */
    private int locationToDropIndex(Point location) {
        MutableListModel m = (MutableListModel) getModel();


        int index = locationToIndex(location);
        Rectangle cellBounds = getCellBounds(index, index);
        if (index == -1 || !cellBounds.contains(location)) {
            index = m.getSize();
        } else {
            if (location.y > cellBounds.y + cellBounds.height / 2) {
                index++;
            }
        }
        return index;
    }

    /**
     * Convert a point in <code>JList</code> coordinates to the
     * index in the list, where an object can be dropped as a child
     * of an element.
     * Use ((MutableListModel) getModel()).canImport(...) to determine
     * if the model actually allows insertion of an object here.
     *
     * @param location the coordinates of the cell, relative to
     *			<code>JList</code>
     * @return the nearest index for dropping the object or -1 if
     * the object can not be dropped as a child here.
     */
    private int locationToDropAsChildIndex(Point location) {
        MutableListModel m = (MutableListModel) getModel();

        int index = locationToIndex(location);
        if (index != -1) {
            Rectangle r = getInnerCellBounds(index);
            r.x++;
            r.y++;
            r.width -= 2;
            r.height -= 2;
            if (r.contains(location)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Paints the component and draws the drop cursor and the autoscroll
     * region if the DnDJList is an active drop target.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isUnderDrag) {
            paintAutoscrollRegion(g);
            if (dropIndex != -1 && (paintsDropCursor || dropAsChild)) {
                paintDropCursor(g);
            }
        }
    }

    /**
     * Paints a rectangle which gives the user a hint,
     * where the autoscroll region is.
     */
    protected void paintAutoscrollRegion(Graphics g) {
        Rectangle visibleRect = getVisibleRect();

        autoscrollBorder.paintBorder(
                this, g,
                visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
    }

    /**
     * Paints a cursor which gives the user a hint,
     * where the potential insertion point of the
     * current drag and drop operation is.
     * <p>
     * Note that you have to change method repaintInsertPoint
     * if you change the shape of the drop cursor.
     *
     *�@see
     */
    protected void paintDropCursor(Graphics g) {
        Rectangle r;
        if (dropAsChild) {
            int rgb = UIManager.getColor("TextField.caretForeground").getRGB();
            g.setColor(new Color((rgb & 0xfffff) | 100 << 24, true));
            r = getInnerCellBounds(dropIndex);
            g.fillRect(r.x, r.y, r.width - 1, r.height - 1);
        } else {
            int size = getModel().getSize();
            g.setColor(UIManager.getColor("TextField.caretForeground"));
            if (dropIndex == size) {
                if (size == 0) {
                    r = new Rectangle(0, 0, getWidth(), 0);
                } else {
                    r = getCellBounds(size - 1, size - 1);
                    r.y += r.height - 1;
                    r.height = 0;
                }
            } else {
                r = getCellBounds(dropIndex, dropIndex);
            }
            g.fillRect(r.x + 1, r.y, r.width - 2, 1);
        }
    }

    /**
     * Calls repaint for the graphics region where the old insertion
     * point was and where the new insertion point is.
     *
     * @param oldInsertionIndex index of the old insertion point.
     * <br>Range: 0 &lt;= value &lt;= getModel().getSize()
     * @param newInsertionIndex index of the new insertion point.
     * <br>Range: 0 &lt;= value &lt;= getModel().getSize()
     */
    protected void repaintDropCursor(int oldInsertionIndex, int newInsertionIndex) {
        int from = Math.min(oldInsertionIndex, newInsertionIndex);
        int to = Math.max(oldInsertionIndex, newInsertionIndex);

        if (to == -1) {
            return;
        }

        int size = getModel().getSize();
        if (size == 0) {
            repaint(new Rectangle(0, 0, getWidth(), 1));
            return;
        }

        if (from != to && from >= 0 && from < size) {
            Rectangle r = getCellBounds(from, from);
            if (r != null) {
                repaint(r);
            }
        }

        if (to >= 0 && to < size) {
            Rectangle r = getCellBounds(to, to);
            if (r != null) {
                repaint(r);
            }
        }

        if (to == size) {
            Rectangle r = getCellBounds(to - 1, to - 1);
            if (r != null) {
                repaint(r);
            }
        }
    }

    /**
     * Creates a drag image and computes the image offset as a side effect.
     *
     * @param dragOrigin The mouse location in component coordinates where
     *                   the drag gesture was recognized.
     * @param dragOriginItemIndex The index of the list item at the drag origin.
     * @param draggedIndices The indices of the list items to be dragged.
     * @param isSelected Indicates whether the dragged items are selected or not.
     * @param imageOffset The image offset. As a side effect, the x and y
     *                    coordinates of the argument will be set by this method.
     *                    The image offset is to be used along with the returned
     *                    image when invoking the start drag method!
     *
     * @return The drag image. To be used along with the imageOffset in a call
     *                    to start drag.
     */
    protected Image createDragImage(Point dragOrigin, int dragOriginItemIndex, int[] draggedIndices, boolean isSelected, Point imageOffset) {
        MutableListModel m = (MutableListModel) getModel();

        // Compute the size of the image
        int h = getHeight();
        int w = getWidth();

        if (getParent() != null) {
            h = Math.min(h, getParent().getHeight());
            w = Math.min(w, getParent().getWidth());
        }

        int y = 0;
        int yoffset = 0;

        // Paint the selected items on the image
            /*
        Image img = createImage(w, h);
        Graphics g = img.getGraphics();
         */
        Color transparent = new Color(0, 0, 0, 0);
        Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        //Image img = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1f));
        g.setColor(transparent);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        ListCellRenderer r = getCellRenderer();
        for (int i = 0; i < draggedIndices.length; i++) {
            Component c = r.getListCellRendererComponent(
                    JDnDList.this, m.getElementAt(draggedIndices[i]),
                    draggedIndices[i], /*isSelected*/ false, false);
            c.setBackground(transparent);
            Dimension p = c.getPreferredSize();
            c.setBounds(0, 0, w, p.height);
            g.setClip(0, 0, w, p.height);
            c.paint(g);
            y += p.height;
            if (draggedIndices[i] < dragOriginItemIndex) {
                yoffset += p.height;
            }
            if (y > h) {
                break;
            }
            g.translate(0, p.height);
        }
        g.dispose();

        // Optimize the size of the image
        if (img instanceof BufferedImage) {
            img = ((BufferedImage) img).getSubimage(0, 0, w, Math.min(y, h));
        }

        // Compute the image offset
        imageOffset.x = -dragOrigin.x;
        imageOffset.y = indexToLocation(dragOriginItemIndex).y - dragOrigin.y - yoffset;

        return img;
    }

    public static void main(String[] args) {
        new JDnDList();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

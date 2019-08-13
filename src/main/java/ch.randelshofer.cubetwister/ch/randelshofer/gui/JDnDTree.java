/* @(#)JDnDTree.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import ch.randelshofer.gui.tree.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * JDnDTree.
 *
 * @author  Werner Randelshofer
 */
public class JDnDTree extends MutableJTree implements Autoscroll {
    private final static long serialVersionUID = 1L;
    /**
     * This inner class is used to prevent the API from being cluttered
     * by internal listeners.
     */
    private class EventHandler
            implements DropTargetListener, TreeModelListener {
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
            if (VERBOSE) System.out.println("tgt drop dropAction("+dndAction.get(new Integer(evt.getDropAction())));
            
            TreePath oldDropPath = dropPath;
            updateDropIndexAndDropAsChild(evt.getDropAction(), evt.getLocation(), evt.getCurrentDataFlavors());
            
            // Reject the drop if the action is ACTION_NONE
            // or if we don't have a suitable model
            int dropAction = evt.getDropAction();
            if (! isEnabled()
            || ((dropAction & getSupportedDropActions()) == DnDConstants.ACTION_NONE)
            || ! (getModel() instanceof MutableTreeModel)) {
            if (VERBOSE) System.out.println("tgt drop dropAction("+dropAction+"):rejectDrop because of drop action none or not-suitable list model");
                evt.rejectDrop();
                // Erase the drop target state variables
                isUnderDrag = false;
                repaint();
                return;
            }
            
            MutableTreeModel m = (MutableTreeModel) getModel();
            
            // Reject the drop if we can't import the data at the current
            // insertion point
            boolean isImportable;
            if (dropAsChild) {
                DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
            isImportable = m.isImportable(evt.getCurrentDataFlavors(), dropAction, 
                    dropNode, dropNode.getChildCount());
            } else {
                DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
                if (dropNode == null) {
                    isImportable = false;
                } else if (dropNode.getParent() == null) {
            isImportable = m.isImportable(evt.getCurrentDataFlavors(), dropAction, 
                    dropNode, dropNode.getChildCount());
                } else {
            isImportable = m.isImportable(evt.getCurrentDataFlavors(), dropAction, 
                    (DefaultMutableTreeNode) dropNode.getParent(), dropNode.getParent().getIndex(dropNode));
                    
                }
                
            }
            
            if (! isImportable) {
            if (VERBOSE) System.out.println("tgt drop dropAction("+dropAction+"):rejectDrop because drop action not allowed at insertion point");
                evt.rejectDrop();
                // Erase the drop target state variables
                isUnderDrag = false;
                repaint();
                return;
            }
            
            // Accept the drop
            if (VERBOSE) System.out.println("tgt drop dropAction("+dropAction+"):acceptDrop");
            evt.acceptDrop(dropAction);
            
            // Remember the selected paths before we let the
            // model import the data
            TreePath[] selectedPaths = getSelectionPaths();
            
            // import the data
            try {
                int count;
            if (dropAsChild) {
                DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
                count = m.importTransferable(evt.getTransferable(), dropAction, dropNode, dropNode.getChildCount()).size();
            } else {
                DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) dropPath.getLastPathComponent();
                if (dropNode.getParent() == null) {
                count = m.importTransferable(evt.getTransferable(), dropAction, 
                    dropNode, dropNode.getChildCount()).size();
                } else {
                count = m.importTransferable(evt.getTransferable(), dropAction, 
                    (DefaultMutableTreeNode) dropNode.getParent(), dropNode.getParent().getIndex(dropNode)).size();
                }
            }
                
                // Restore the selected paths
                /*
                if (count > 0) {
                    TreeSelectionModel sm = getSelectionModel();
                    sm.clearSelection();
                    int dropIndex = getRowForPath(dropPath);
                    for(int i = 0; i < selectedPaths.length; i++) {
                        int si = getRowForPath(selectedPaths[i]);
                        if (si >= dropIndex) si += count;
                        sm.addSelectionPaht(selectedPaths[i]);
                    }
                    
                    scrollRectToVisible(getCellBounds(dropPath, dropPath + count - 1));
                    
                }*/
                
                // Report success or failure
                evt.dropComplete(true);
            } catch (Exception e) {
                e.printStackTrace();
                evt.dropComplete(false);
            }
            
            // Erase the drop target state variables
            isUnderDrag = false;
            dropPath = null;
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
                System.out.println("tgt dragEnter action:"+dndAction.get(new Integer(evt.getDropAction()))+" "+evt.getLocation());
                DataFlavor[] flavors = evt.getCurrentDataFlavors();
                for (int i=0; i < flavors.length; i++) {
                    System.out.println("              flavor:"+flavors[i]);
                }
            }
            
            // Reject the drag, if we are disabled or if we don't have a suitable model
            if (! isEnabled()
            || ! (getModel() instanceof MutableTreeModel)) {
                dropPath = null;
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
            if (VERBOSE) System.out.println("tgt dragOver action:"+dndAction.get(new Integer(evt.getDropAction()))+" "+evt.getLocation());
            
            // Determine over which element the mouse is hovering
            boolean oldDropAsChild = dropAsChild;
            TreePath oldDropPath = dropPath;
            
            updateDropIndexAndDropAsChild(evt.getDropAction(), evt.getLocation(), evt.getCurrentDataFlavors());
            
            if (oldDropPath != dropPath || oldDropAsChild != dropAsChild) {
                repaintDropCursor(oldDropPath, dropPath);
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
            if (VERBOSE) System.out.println("tgt dragExit");
            
            dropPath = null;
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
            if (VERBOSE) System.out.println("tgt drpActionChanged dropAction:"+dndAction.get(new Integer(evt.getDropAction())));
            
            // Reject the drag, if we don't have a suitable model
            if (! (getModel() instanceof MutableTreeModel)) {
                evt.rejectDrag();
                return;
            }
            
            // Check if we can import the data at the current
            // insertion point
            int dropAction = evt.getDropAction();
            TreePath oldDropIndex = dropPath;
            updateDropIndexAndDropAsChild(dropAction, evt.getLocation(), evt.getCurrentDataFlavors());
            
            if (dropPath == null) {
                evt.acceptDrag(DnDConstants.ACTION_NONE);
            } else {
                evt.acceptDrag(dropAction);
            }
            
            if (oldDropIndex != dropPath) {
                repaintDropCursor(oldDropIndex, dropPath);
            }
        }

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private EventHandler eventHandler = new EventHandler();
    
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
     * The margin for the autoscroll area.
     * Must be less than the height of a list item.
     */
    private final static int AUTOSCROLL_MARGIN = 8;
    
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
        dndAction.put(DnDConstants.ACTION_NONE, "NONE");
        dndAction.put(DnDConstants.ACTION_COPY, "COPY");
        dndAction.put(DnDConstants.ACTION_MOVE, "MOVE");
        dndAction.put(DnDConstants.ACTION_COPY_OR_MOVE, "COPY OR MOVE");
        dndAction.put(DnDConstants.ACTION_LINK, "LINK");
        dndAction.put(DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK, "COPY OR MOVE OR LINK");
    }
    
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
     * Paths of the dragged tree nodes. If a drag operation
     * ends with a DnDConstants.ACTION_MOVE, the paths are used
     * to determine which elements must be removed from the MutableTreeModel.
     * <p>
     * The paths are set when a drag gesture has been started by
     * this DnDJList and are kept up to date with TreeModel changes
     * until the dragDropEnd event is received.
     */
    private TreePath[] draggedPaths;
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
    private TreePath dropPath = null;
    
    /**
     * dropAsChild is set to true, when the drag cursor hovers over
     * the bounds of a list cell.
     * <p>
     * The value true indicates, that the dragged object should
     * be added as a child of the list item indicated by dropPath.
     * <p>
     * The value false indicates, that the dragged object should
     * be inserted at the dropPath as a new member into the list.
     */
    private boolean dropAsChild = false;
    
    
    /** Creates new form. */
    public JDnDTree() {
        this(new DefaultMutableTreeModel());
        
    }
    
    /**
     * Constructs a DnDJList with the specified MutableTreeModel.
     */
    public JDnDTree(MutableTreeModel m) {
        super(m);
        JTree tree;
        initComponents();


        /*
        dragGestureRecognizer = DragSource.getDefaultDragSource()
        .createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this.eventHandler);
         * */
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this.eventHandler);
       // setTransferHandler(transferHandler);
        setDragEnabled(true);
        /*
       ActionMap am = getActionMap();
       am.put("delete", new AbstractAction() {
           public void actionPerformed(ActionEvent evt) {
               delete();
           }
       });
       InputMap im = getInputMap();
       im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");
       im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
         */ 
    }
    /**
     * Sets the model that represents the contents or "value" of the
     * list. The DnDJList supports drag and drop operations only when
     * the model is an instance of MutableTreeModel.
     */
    @Override
    public void setModel(TreeModel m) {
        if (getModel() != null) {
        getModel().removeTreeModelListener(this.eventHandler);
}        
        super.setModel(m);
        if (getModel() != null) {
        m.addTreeModelListener(this.eventHandler);
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
    /**
     * Paints the component and draws the drop cursor and the autoscroll
     * region if the DnDJList is an active drop target.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isUnderDrag) {
            paintAutoscrollRegion(g);
            if (dropPath != null && (paintsDropCursor || dropAsChild)) {
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
            int childCount = treeModel.getChildCount(dropPath.getLastPathComponent());
            if (!isExpanded(dropPath) || childCount == 0) {
            int rgb = UIManager.getColor("TextField.caretForeground").getRGB();
            g.setColor(new Color((rgb & 0xfffff) | 100 << 24, true));
            r = getPathBounds(dropPath);
            g.fillRect(r.x, r.y, r.width - 1, r.height - 1);
            } else {
                TreePath drawPath = dropPath.pathByAddingChild(treeModel.getChild(dropPath.getLastPathComponent(), childCount - 1));
            g.setColor(UIManager.getColor("TextField.caretForeground"));
            r = getPathBounds(drawPath);
            g.fillRect(r.x + 1, r.y+r.height-1, r.width - 2, 1);
            }
        } else {
            //int size = getModel().getSize();
            TreeModel m = getModel();
            g.setColor(UIManager.getColor("TextField.caretForeground"));
            if (dropPath.getLastPathComponent() == m.getRoot()) {
                if (m.getChildCount(m.getRoot()) == 0) {
                    r = new Rectangle(0, 0, getWidth(), 0);
                } else {
                    r = getPathBounds(dropPath);
                    r.y += r.height - 1;
                    r.height = 0;
                }
            } else {
                r = getPathBounds(dropPath);
            }
            g.fillRect(r.x + 1, r.y, r.width - 2, 1);
        }
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
                    dropPath = locationToDropPath(location);
                    
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
                    dropPath = locationToDropPath(location);
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
     * Creates a drag image and computes the image offset as a side effect.
     *
     * @param dragOrigin The mouse location in component coordinates where
     *                   the drag gesture was recognized.
     * @param dragOriginPath The path of the node at the drag origin.
     * @param draggedPaths The paths of the nodes to be dragged.
     * @param isSelected Indicates whether the dragged items are selected or not.
     * @param imageOffset The image offset. As a side effect, the x and y
     *                    coordinates of the argument will be set by this method.
     *                    The image offset is to be used along with the returned
     *                    image when invoking the start drag method!
     *
     * @return The drag image. To be used along with the imageOffset in a call
     *                    to start drag.
     */
    protected Image createDragImage(Point dragOrigin, TreePath dragOriginPath, TreePath[] draggedPaths, boolean isSelected, Point imageOffset) {
        MutableTreeModel m = (MutableTreeModel) getModel();
        
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
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1f ));
        g.setColor(transparent);
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f ));
        int dragOriginItemIndex = getRowForPath(dragOriginPath);
        
        TreeCellRenderer r = getCellRenderer();
        for (int i=0; i < draggedPaths.length; i++) {
            MutableTreeNode node = (MutableTreeNode) draggedPaths[i].getLastPathComponent();
            int row = getRowForPath(draggedPaths[i]);
            Component c = r.getTreeCellRendererComponent(
                    JDnDTree.this, node,
                    /*isSelected*/false, /*isExpanded*/false, node.isLeaf(), 
                    getRowForPath(draggedPaths[i]), /*hasFocus*/false
                    
                    );
            c.setBackground(transparent);
            Dimension p = c.getPreferredSize();
            c.setBounds(0, 0, w, p.height);
            g.setClip(0, 0, w, p.height);
            c.paint(g);
            y += p.height;
            if (row < dragOriginItemIndex) yoffset += p.height;
            if (y > h) break;
            g.translate(0, p.height);
        }
        g.dispose();
        
        // Optimize the size of the image
        if (img instanceof BufferedImage) {
            img = ((BufferedImage) img).getSubimage(0, 0, w, Math.min(y, h));
        }
        
        // Compute the image offset
        imageOffset.x = -dragOrigin.x;
        imageOffset.y = getPathBounds(dragOriginPath).getLocation().y - dragOrigin.y - yoffset;
        
        return img;
    }
    /**
     * Convert a point in <code>JList</code> coordinates to the nearest
     * index in the list, where an object can be dropped.
     * Use ((MutableTreeModel) getModel()).canImport(...) to determine
     * if the model actually allows insertion of an object here.
     *
     * @param location the coordinates of the cell, relative to
     *			<code>JList</code>
     * @return the nearest index for dropping the object.
     */
    private TreePath locationToDropPath(Point location) {
        MutableTreeModel m = (MutableTreeModel) getModel();
        
        
        int index = getRowForLocation(location.x, location.y);
        TreePath path;
        Rectangle cellBounds = getRowBounds(index);
        if (index == -1 || ! cellBounds.contains(location)) {
            path = new TreePath(m.getRoot());
        } else {
            path = getPathForRow(index);
            if (location.y > cellBounds.y + cellBounds.height / 2) {
               if (treeModel.getIndexOfChild(path.getPathComponent(path.getPathCount()-2), path.getLastPathComponent()) == 
               treeModel.getChildCount(path.getPathComponent(path.getPathCount()-2)) - 1) {
            path = path.getParentPath();
                } else {
            path = getPathForRow(index+1);
                }
            }
        }
        return path;
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
    private TreePath locationToDropAsChildPath(Point location) {
        MutableTreeModel m = (MutableTreeModel) getModel();
        
        int index = getRowForLocation(location.x, location.y);
        if (index != -1) {
            Rectangle r = getRowBounds(index);
            r.x++;
            r.y++;
            r.width -= 2;
            r.height -= 2;
            if (r.contains(location)) {
                return getPathForRow(index);
            }
        }
        return null;
    }
    
    /**
     * Updates the value of dropPath and dropAsChild instance variables.
     */
    private void updateDropIndexAndDropAsChild(int dropAction, Point location, DataFlavor[] flavors) {
        MutableTreeModel m = (MutableTreeModel) getModel();
        
        dropPath = locationToDropPath(location);
        TreePath asChildPath = locationToDropAsChildPath(location);
        dropAsChild = false;
        DefaultMutableTreeNode dropNode = (dropPath == null) ? null : (DefaultMutableTreeNode) dropPath.getLastPathComponent();
        DefaultMutableTreeNode asChildNode = (asChildPath == null) ? null : (DefaultMutableTreeNode) asChildPath.getLastPathComponent();
        if (asChildPath != null && asChildNode != null && m.isImportable(flavors, dropAction, asChildNode,
                asChildNode.getChildCount())) {
            // allow dropAsChild only, if the drop index is not contained in the array of dragged paths
            boolean isDescendantOfDraggedPaths = false;
            if (draggedPaths != null) {
                for (TreePath p : draggedPaths) {
                    DefaultMutableTreeNode pn = (DefaultMutableTreeNode) p.getLastPathComponent();
                        if (pn.isNodeDescendant(dropNode)) {
                            isDescendantOfDraggedPaths = true;
                            break;
                        }
                }
            }
            if (! isDescendantOfDraggedPaths) {
                dropAsChild = true;
                dropPath = asChildPath;
            }

        } else if (dropPath != null && dropNode != null && m.isImportable(flavors, dropAction, dropNode,
                dropNode.getChildCount())) {
            ///...nothing to do...
            dropAsChild=true;
        } else {
            if (dropNode != null && dropNode.getParent() != null &&
                    ! m.isImportable(flavors, dropAction, (DefaultMutableTreeNode) dropNode.getParent(), dropNode.getParent().getIndex(dropNode))) {
                dropPath = null;
            }
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
    protected void repaintDropCursor(TreePath oldInsertionIndex, TreePath newInsertionIndex) {
        /*
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
        }*/
        repaint();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}

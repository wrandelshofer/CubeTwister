/* @(#)MutableTreeModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.tree;

import java.io.*;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.*;
import javax.swing.undo.UndoableEdit;

/**
 * Specifies the requirements for a mutable tree model.
 * <p>
 * The mutable list model adds support for the following operations
 * to the <code>javax.swing.tree.TreeModel</code> interface:
 * <ul>
 * <li>An Abstract factory for the creation of new nodes.</li>
 * <li>Insert and remove operations.</li>
 * <li>A setter operation for changing the value of a node.</li>
 * <li>Operations for retrieving <code>javax.swing.Action</code>'s
 *      for a group of elements</li>
 * <li>Operations for importing and exporting nodes from a
 * <code>java.awt.transfer.Transferable</code>.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>3.1 2009-12-22 Methods createNodeAt and importTransferable return
 * paths of inserted nodes. Use MutableTreeNode instead of MutableTreeNode.
 * Adds undo support.
 * <br>3.0 2004-05-16 Revised.
 * <br>2.3 2002-04-08 Interface streamlined. Support for Transferables
 * added.
 * <br>2.0.1 2002-04-07 Method getInsertableRowTypes added.
 * <br>2.0 2001-07-18
 */
public interface MutableTreeModel
        extends TreeModel {
    public final static String ENABLED_PROPERTY="enabled";

    // Abstract factory operations
    // ====================================================

    /**
     * Returns the types of children that may be created at this node.
     *
     * @param   parent   a node from the tree, obtained from this data source.
     * @return  an array of objects that specify a child type that may be
     *          added to the node. Returns an empty array for nodes
     *          that cannot have additional children.
     */
    public Object[] getCreatableNodeTypes(Object parent);

    /**
     * Returns the default type of children that can be created at
     * the specified node.
     *
     * @param   parent   a node from the tree, obtained from this data source.
     * @return  an Object that specifies the default child type that can be
     *          inserted at the insertion point. Returns null if no
     *          children can be inserted here.
     *          The value must be one of the types returned by operation
     *          getCreatableNodeTypes.
     */
    public Object getCreatableNodeType(Object parent);

    /**
     * Creates the specified element type at the specified position in this list
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     * <p>
     * This method may create a node at a different location.
     *
     * @param   type  the type of the new child to be created, obtained
     *                     from getCreatableChildren
     * @param   parent     a node from the tree, obtained from this data source.
     * @param   index      index of the child.
     *
     * @return The path pointing to the newly created node.
     *
     * @throws    IllegalArgumentException if the type is not contained in
     *            the array returned by getInsertableTypes(int).
     * @throws    IndexOutOfBoundsException if the index is out of range
     *		  (index &lt; 0 || index &gt; size()).
     */
    public TreePath createNodeAt(Object type, MutableTreeNode parent, int index)
            throws IllegalStateException;

    // Insert and remove operations
    // ====================================================
    /**
     * Returns whether a node may be added.
     *
     * @param   parent  a node from the tree, obtained from this data source.
     * @param   index the insertion index.
     */
    public boolean isNodeAddable(MutableTreeNode parent, int index);

    /**
     * Returns whether the specified node may be removed.
     *
     * @param   node   a node from the tree, obtained from this data source.
     */
    public boolean isNodeRemovable(MutableTreeNode node);

    /**
     * Message this to remove a child from its parent.
     *
     * @param   node   a node from the tree, obtained from this data source.
     * @exception   IllegalStateException if the node may not be removed.
     */
    public void removeNodeFromParent(MutableTreeNode node);

    // Editing operations.
    // ====================================================
    /**
     * Returns wether the specified node may be edited.
     *
     * @param   node   a node from the tree, obtained from this data source.
     */
    public boolean isNodeEditable(MutableTreeNode node);

    /**
     * Sets the value of an element at the given index.
     *
     * @param aValue - the new value
     * @param   node   a node from the tree, obtained from this data source.
     * @exception   IllegalStateException if the element is not editable.
     * /
    public void setValueAt(Object value, MutableTreeNode node)
    throws IllegalStateException;
     */
    // Operations for determining the actions for nodes
    // ====================================================
    /**
     * Gets actions for the specified nodes.
     *
     * @param   nodes   The nodes.
     */
    public Action[] getNodeActions(MutableTreeNode[] nodes);

    // Datatransfer operations
    // =======================
    /**
     * Creates a Transferable to use as the source for a data
     * transfer of the specified elements.
     * Returns the representation of the rows
     * to be transferred, or null if transfer is not possible.
     *
     * @param   nodes     The nodes.
     */
    public Transferable exportTransferable(MutableTreeNode[] nodes);

    /**
     * Indicates whether the model would accept an import of the
     * given set of data flavors prior to actually attempting
     * to import it.
     *
     * @param transferFlavors the data formats available
     * @param   action     the action, this is either COPY, MOVE or LINK.
     * @param   parent     a node from the tree, obtained from this data source.
     * @param   index    The insertion point.
     * @return true if the data can be inserted into the component,
     * false otherwise
     */
    public boolean isImportable(DataFlavor[] transferFlavors, int action, MutableTreeNode parent, int index);

    /**
     * Causes a transfer to the model from a clipboard or
     * a DND drop operation.
     *
     * @param   t        The transfer data.
     * @param   parent     a node from the tree, obtained from this data source.
     * @param   index    The insertion point.
     * @return The paths of the imported nodes.
     */
    public List<TreePath> importTransferable(Transferable t, int action, MutableTreeNode parent, int index)
            throws UnsupportedFlavorException, IOException;

    ///Undo support
    /**
     * Removes an UndoableEditListener.
     */
    public void removeUndoableEditListener(UndoableEditListener l);

    /**
     * Adds an UndoableEditListener.
     */
    public void addUndoableEditListener(UndoableEditListener l);

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    public void fireUndoableEdit(UndoableEdit edit);

    ///Property change support
    /**
     * Removes a PropertyChangeListener.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Adds a PropertyChangeListener.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);

}

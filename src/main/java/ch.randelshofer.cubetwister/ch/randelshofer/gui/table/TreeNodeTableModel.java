/* @(#)TreeNodeTableModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.table;

import ch.randelshofer.gui.tree.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
/**
 * Wraps a TreeNode of a MutableTreeModel into a MutableTableModel.
 *
 * @author  Werner Randelshofer
 */
public class TreeNodeTableModel
extends AbstractTableModel
implements MutableTableModel, TreeModelListener {
    private final static long serialVersionUID = 1L;
    protected MutableTreeModel treeModel;
    protected javax.swing.tree.DefaultMutableTreeNode treeNode;
    
    /** Creates new TreeNodeTableModel. */
    public TreeNodeTableModel() {
        treeNode = new javax.swing.tree.DefaultMutableTreeNode("Root", true);
        treeModel = new DefaultMutableTreeModel(treeNode);
    }
    /** Creates new TreeNodeTableModel. */
    public TreeNodeTableModel(MutableTreeModel m, javax.swing.tree.DefaultMutableTreeNode n) {
        treeModel = m;
        treeNode = n;
    }
    
    /** Sets the model. */
    public void setModel(MutableTreeModel treeModel, javax.swing.tree.DefaultMutableTreeNode treeNode) {
        if (this.treeModel != null) {
            this.treeModel.removeTreeModelListener(this);
            fireTableRowsDeleted(0, getRowCount());
        }
        
        this.treeModel = treeModel;
        this.treeNode = treeNode;
        
        if (treeModel != null) {
            treeModel.addTreeModelListener(this);
            fireTableRowsInserted(0, getRowCount());
        }
    }

    public javax.swing.tree.DefaultMutableTreeNode getRow(int row) {
        return (javax.swing.tree.DefaultMutableTreeNode) treeNode.getChildAt(row);
    }
    /**
     * Returns the value for the cell at <I>columnIndex</I> and <I>rowIndex</I>.
     *
     * @param	row	the row whose value is to be looked up
     * @param	column 	the column whose value is to be looked up
     * @return	the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int row, int column) {
        javax.swing.tree.DefaultMutableTreeNode child = (javax.swing.tree.DefaultMutableTreeNode) treeNode.getChildAt(row);
        return child.getUserObject();
    }
    
    /**
     * Returns the number of rows in the model. A
     * <B>JTable</B> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number or rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
        return (treeNode == null) ? 0 : treeNode.getChildCount();
    }
    
    /**
     * Returns the number of columns in the model. A
     * <B>JTable</B> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number or columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return 1;
    }
    
    /**
     * Gets actions for the indicated row.
     *
     * @param   rows   The rows.
     */
    @Override
    public Action[] getRowActions(int[] rows) {
        DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[rows.length];
        for (int i=0; i < rows.length; i++) {
            nodes[i] = (DefaultMutableTreeNode) treeNode.getChildAt(rows[i]);
        }
        return treeModel.getNodeActions(nodes);
    }
    
    /**
     * Invoke this to create a new row into the table.
     *
     * @param   row      index of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     */
    @Override
    public void createRow(int row, Object type) {
        treeModel.createNodeAt(type, treeNode, row);
    }
    
    /**
     * Returns true if the cell at <I>rowIndex</I> and <I>columnIndex</I>
     * is editable.  Otherwise, setValueAt() on the cell will not change
     * the value of that cell.
     *
     * @param	row	the row whose value is to be looked up
     * @param	column	the column whose value is to be looked up
     * @return	true if the cell is editable.
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return treeModel.isNodeEditable((DefaultMutableTreeNode) treeNode.getChildAt(row));
    }
    
    /**
     * Remove a row from the table.
     *
     * @param   row   index of the row.
     * @exception   IllegalStateException if the row may not be removed.
     */
    @Override
    public void removeRow(int row) {
        DefaultMutableTreeNode removed = (DefaultMutableTreeNode) treeNode.getChildAt(row);
        treeModel.removeNodeFromParent(removed);
        //return removed;
    }
    
    /**
     * Returns wether the specified row may be removed.
     *
     * @param   row   index of the row.
     */
    @Override
    public boolean isRowRemovable(int row) {
        return treeModel.isNodeRemovable((DefaultMutableTreeNode) treeNode.getChildAt(row));
    }
    
    /**
     * Invoked after the tree has drastically changed structure from a
     * given node down.  If the path returned by e.getPath() is of length
     * one and the first element does not identify the current root node
     * the first element should become the new root of the tree.
     *
     * <p>e.path() holds the path to the node.</p>
     * <p>e.childIndices() returns null.</p>
     */
    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        if (e.getTreePath().getLastPathComponent() == treeNode
        || e.getTreePath().getLastPathComponent() == treeNode.getParent()) {
            fireTableDataChanged();
        }
    }
    
    /**
     * <p>Invoked after nodes have been inserted into the tree.</p>
     *
     * <p>e.path() returns the parent of the new nodes
     * <p>e.childIndices() returns the indices of the new nodes in
     * ascending order.
     */
    @Override
    public void treeNodesInserted(TreeModelEvent e) {
        if (e.getTreePath().getLastPathComponent() == treeNode) {
            int[] indices = e.getChildIndices();
            //FIXME should analyse indices to make sure that
            // a contiguous segment of children has been inserted.
            // If the segment is discontiguous, then multiple events
            // must be fired.
            fireTableRowsInserted(indices[0], indices[indices.length - 1]);
        }
    }
    
    /**
     * <p>Invoked after nodes have been removed from the tree.  Note that
     * if a subtree is removed from the tree, this method may only be
     * invoked once for the root of the removed subtree, not once for
     * each individual set of siblings removed.</p>
     *
     * <p>e.path() returns the former parent of the deleted nodes.</p>
     *
     * <p>e.childIndices() returns the indices the nodes had before they were deleted in ascending order.</p>
     */
    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
        if (e.getTreePath().getLastPathComponent() == treeNode) {
            int[] indices = e.getChildIndices();
            //FIXME should analyse indices to make sure that
            // a contiguous segment of children has been updated.
            // If the segment is discontiguous, then multiple events
            // must be fired.
            fireTableRowsDeleted(indices[0], indices[indices.length - 1]);
        }
    }
    
    /**
     * <p>Invoked after a node (or a set of siblings) has changed in some
     * way. The node(s) have not changed locations in the tree or
     * altered their children arrays, but other attributes have
     * changed and may affect presentation. Example: the name of a
     * file has changed, but it is in the same location in the file
     * system.</p>
     * <p>To indicate the root has changed, childIndices and children
     * will be null. </p>
     *
     * <p>e.path() returns the path the parent of the changed node(s).</p>
     *
     * <p>e.childIndices() returns the index(es) of the changed node(s).</p>
     */
    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        if (e.getTreePath().getLastPathComponent() == treeNode) {
            int[] indices = e.getChildIndices();
            //FIXME should analyse indices to make sure that
            // a contiguous segment of children has been updated.
            // If the segment is discontiguous, then multiple events
            // must be fired.
            fireTableRowsUpdated(indices[0], indices[indices.length - 1]);
        }
    }
    
    /**
     * Returns the types of children that may be created at this row.
     *
     * @param   row   a row.
     * @return  an array of objects that specify a child type that may be
     *         added at this row.
     */
    @Override
    public Object[] getCreatableRowTypes(int row) {
        return treeModel.getCreatableNodeTypes(treeNode);
    }
    @Override
    public Object getCreatableRowType(int row) {
        return treeModel.getCreatableNodeType(treeNode);
    }
    
    // Datatransfer operations
    // =======================
    /**
     * Creates a Transferable to use as the source for a data
     * transfer of the specified elements.
     * Returns the representation of the rows
     * to be transferred, or null if transfer is not possible.
     *
     * @param   rows     Row indices.
     */
    @Override
    public Transferable exportRowTransferable(int[] rows) {
        DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[rows.length];
        for (int i=0; i < rows.length; i++) {
            nodes[i] = (DefaultMutableTreeNode) treeNode.getChildAt(rows[i]);
        }
        return treeModel.exportTransferable(nodes);
    }
    
    /**
     * Indicates whether the model would accept an import of the
     * given set of data flavors prior to actually attempting
     * to import it.
     *
     * @param   transferFlavors the data formats available
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   row    The insertion point. 0 &lt;= row &lt;= getRowCount()
     * @param   asChild  Indicates that the item is to be imported as a child
     *                   of the list item rather than as an individual element
     /                   of the list.
     *
     * @return true if the data can be imported.
     *
     * @see java.awt.dnd.DnDConstants
     */
    @Override
    public boolean isRowImportable(DataFlavor[] transferFlavors, int action, int row, boolean asChild) {
        return (asChild) 
        ? treeModel.isImportable(transferFlavors, action, (DefaultMutableTreeNode) treeNode.getChildAt(row), -1)
        : treeModel.isImportable(transferFlavors, action, treeNode, row);
    }
    
    /**
     * Causes a transfer to the model from a clipboard or
     * a DND drop operation.
     *
     * @param   t        The transfer data.
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   row    The insertion point. 0 &lt;= row &lt;= getRowCount()
     * @param   asChild  Indicates that the item is to be imported as a child
     *                   of the list item rather than as an individual element
     *                   of the list.
     *
     * @return The number of imported elements.
     */
    @Override
    public int importRowTransferable(Transferable t, int action, int row, boolean asChild)
    throws UnsupportedFlavorException, IOException {
        List<TreePath> importedPaths=
         (asChild) 
        ? treeModel.importTransferable(t, action, (DefaultMutableTreeNode) treeNode.getChildAt(row), -1)
        : treeModel.importTransferable(t, action, treeNode, row);
        return importedPaths.size();
    }
    
    @Override
    public boolean isRowAddable(int row) {
        return treeModel.isNodeAddable(treeNode, row);
    }
    
}

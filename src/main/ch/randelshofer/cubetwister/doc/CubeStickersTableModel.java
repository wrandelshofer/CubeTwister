/*
 * @(#)CubeStickersTableModel.java  2.0  2006-06-03
 *
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.cubetwister.doc.*;
import ch.randelshofer.gui.table.*;
import ch.randelshofer.gui.datatransfer.*;
import ch.randelshofer.util.*;
import java.awt.datatransfer.*;
import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Wraps the macros provided by CubeModel to make them
 * accessible by a MutableJTable.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2006-06-03 Reworked.
 * <br>1.0.1 2002-04-07 Method getInsertableRowTypes added.
 * <br>1.0 2001-07-19
 */
public class CubeStickersTableModel 
extends AbstractTableModel 
implements MutableTableModel, PropertyChangeListener, TreeModelListener {
    private final static long serialVersionUID = 1L;
    /**
     * The wrapped document.
     */
    private CubeModel model;
    /**
     * The Document.
     */
    private DocumentModel documentModel;

    private final String[] columnNames = {"cube.stickerColumn", "cube.visibleColumn",
    "cube.fillColorColumn"};
    
    /** 
     * Creates a new CubeColorsTableModel which wraps a
     * default CubeModel.
     */
    public CubeStickersTableModel() {
        this(null);
    }
    /** 
     * Creates a new CubeColorsTableModel which wraps the
     * provided CubeModel.
     */
    public CubeStickersTableModel(CubeModel n) {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ch.randelshofer.cubetwister.Labels");
        for (int i=0; i < columnNames.length; i++) {
            columnNames[i] = labels.getString(columnNames[i]);
        }
        setModel(n);
    }

    public void setModel(CubeModel value) {
        CubeModel oldValue = model;

        if (oldValue != null) {
            oldValue.removePropertyChangeListener(this);
            fireTableRowsDeleted(0, getRowCount() - 1);
            for (int i=getRowCount() - 1; i > -1; i--) {
                getRowObject(i).removePropertyChangeListener(this);
            }
        }
        if (documentModel != null) {
            documentModel.removeTreeModelListener(this);
            documentModel = null;
        }

        model = value;
        
        if (value != null) {
            value.addPropertyChangeListener(this);
            fireTableRowsInserted(0, getRowCount());
            for (int i=getRowCount() - 1; i > -1; i--) {
                getRowObject(i).addPropertyChangeListener(this);
            }
            documentModel = model.getDocument();
        }
        if (documentModel != null) {
            documentModel.addTreeModelListener(this);
        }
    }
    public CubeStickerModel getRowObject(int row) {
        return (CubeStickerModel) model.getStickers().getChildAt(row);
    }

    /**
     * Returns the value for the cell at <I>columnIndex</I> and <I>rowIndex</I>.
     *
     * @param	row             the row whose value is to be looked up
     * @param	column  	the column whose value is to be looked up
     * @return	the value Object at the specified cell
     */
    public Object getValueAt(int row, int column) {
        CubeStickerModel item = getRowObject(row);
        switch (column) {
            case 0 : return item.getName();
            case 1 : return new Boolean(item.isVisible());
            case 2 : return item.getFillColorModel();
        }
        return null; // should never happen
    }
    
    /**
     * Returns the name of the column at <i>columnIndex</i>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	columnIndex	the index of column
     * @return  the name of the column
     */
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    
    /**
     * Returns wether a row may be inserted.
     *
     * @param   row      index of the new row.
     */
    public boolean isRowInsertable(Object type, int row) {
        return false;
    }
    /**
     * Returns wether a row may be duplicated.
     *
     * @param   row      index of the new row.
     */
    public boolean isRowDuplicateable(int row) {
        return false;
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
    public int getRowCount() {
        return (model == null) ? 0 : model.getStickerCount();
    }
    
    /**
     * Invoke this to duplicate a row of the table.
     *
     * @param   row      index of the row.
     * @exception   IllegalStateException if the row may not be duplicated.
     */
    public void createRow(int row, Object type) {
        throw new IllegalStateException("cannot create row");
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
    public boolean isCellEditable(int row, int column) {
        return column > 0;
    }
    
    /**
     * Sets the value in the cell at <I>columnIndex</I> and <I>rowIndex</I> to 
     * <I>aValue</I> is the new value.
     *
     * @param	value		 the new value
     * @param	row	 the row whose value is to be changed
     * @param	column 	 the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(Object value, int row, int column) {
        CubeStickerModel item = getRowObject(row);
        switch (column) {
            case 0 : 
                //item.setName((String) value);
                break;
            case 1 : 
                item.setVisible(((Boolean) value).booleanValue());
                break;
            case 2 : 
                item.setFillColorModel((CubeColorModel) value);
                break;
            case 3 : 
 //               item.setOutlineColor((CubeColorModel) value);
                break;
        }
    }
    
    /**
     * Returns the most specific superclass for all the cell values 
     * in the column.  This is used by the JTable to set up a default 
     * renderer and editor for the column.
     *
     * @return the common ancestor class of the object values in the model.
     */
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 1 : 
                return Boolean.class;
            case 2 :
            case 3 :
                return CubeColorModel.class;
            default :
                return String.class;
        }
    }
    
    /**
     * Message this to remove a row from the table.
     *
     * @param   row   index of the row.
     * @exception   IllegalStateException if the row may not be removed.
     */
    public void removeRow(int row) {
        throw new IllegalStateException("cannot remove row");
    }
    
    /**
     * Returns wether the specified node may be removed.
     *
     * @param   row   index of the row.
     */
    public boolean isRowRemovable(int row) {
        return false;
    }
    
    /**
     * Returns the number of columns in the model. A
     * <B>JTable</B> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number or columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * Gets actions for the indicated rows.
     *
     * @param   rows   indexes of the rows.
     */
    public Action[] getRowActions(int[] rows) {
        return null;
    }
    
   public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source == model && evt.getPropertyName() == CubeModel.KIND_PROPERTY) {
            fireTableDataChanged();
        } else if (source instanceof CubeStickerModel) {
            int i = model.getStickers().getIndex((CubeStickerModel) source);
            if (i != -1) {
                fireTableRowsUpdated(i, i);
            }
        }
    }
    
    /**
     * <p>Invoked after the tree has drastically changed structure from a
     * given node down.  If the path returned by e.getPath() is of length
     * one and the first element does not identify the current root node
     * the first element should become the new root of the tree.<p>
     *
     * <p>evt.path() holds the path to the node.</p>
     * <p>evt.childIndices() returns null.</p>
     */
    public void treeStructureChanged(TreeModelEvent evt) {
        Object path[] = evt.getPath();
        if (path[path.length - 1] == model.getStickers() 
        || path[path.length - 1] == model) {
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
    public void treeNodesInserted(TreeModelEvent evt) {
        Object path[] = evt.getPath();
        if (path[path.length - 1] == model.getStickers()) {
            int[] indices = evt.getChildIndices();
            
            for (int i=0; i < indices.length; i++) {
                ((CubeStickerModel) model.getStickers().getChildAt(indices[i])).addPropertyChangeListener(this);
            }
            
            fireTableRowsInserted(indices[0], indices[indices.length - 1]);
        } else if (path[path.length - 1] == model) {
            fireTableDataChanged();
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
    public void treeNodesRemoved(TreeModelEvent evt) {
        Object path[] = evt.getPath();
        if (path[path.length - 1] == model.getStickers()) {
            int[] indices = evt.getChildIndices();
            fireTableRowsDeleted(indices[0], indices[indices.length - 1]);
        } else if (path[path.length - 1] == model) {
            fireTableDataChanged();
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
     * <p>evt.path() returns the path the parent of the changed node(s).</p>
     *
     * <p>evt.childIndices() returns the index(es) of the changed node(s).</p>
     */
    public void treeNodesChanged(TreeModelEvent evt) {
        Object path[] = evt.getPath();
        if (path[path.length - 1] == model.getStickers()) {
            int[] indices = evt.getChildIndices();
            fireTableRowsUpdated(indices[0], indices[indices.length - 1]);
        }
    }    
    /**
     * Returns an empty array since insertion of stickers is not allowed.
     */
    public Object[] getCreatableRowTypes(int row) {
        return new Object[] {};
    }
    public Object getCreatableRowType(int row) {
        return null;
    }
    
    public boolean isRowAddable(int row) {
        return false;
    }
    
    public int importRowTransferable(Transferable transfer, int action, int row, boolean asChild) {
        throw new IllegalStateException("cannot import row");
    }
    public boolean isRowImportable(DataFlavor[] flavors, int action, int row, boolean asChild) {
        return false;
    }
    public Transferable exportRowTransferable(int[] rows) {
        CompositeTransferable t = new CompositeTransferable();
        //t.add(TableModels.createLocalTransferable(this, rows));
        t.add(TableModels.createHTMLTransferable(this, rows));
        t.add(TableModels.createPlainTransferable(this, rows));
        return t;
    }
}

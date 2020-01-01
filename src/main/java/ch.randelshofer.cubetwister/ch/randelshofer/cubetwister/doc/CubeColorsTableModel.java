/* @(#)CubeColorsTableModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.table.TreeNodeTableModel;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.Action;
import javax.swing.event.TreeModelEvent;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

/**
 * Wraps a CubeColorsModel into the swing TableModel interface.
 *
 * @author Werner Randelshofer
 */
public class CubeColorsTableModel
        extends TreeNodeTableModel
        implements PropertyChangeListener {
    private final static long serialVersionUID = 1L;
    private final String[] columnNames = {"cube.colorNameColumn",
    "cube.colorColumn"};
    
    /** 
     * Creates a new CubeColorsTableModel which wraps a
     * default CubeModel.
     * XXX Broken
     */
    public CubeColorsTableModel() {
        super(null, null);
        init();
    }

    /**
     * Creates a new CubeColorsTableModel which wraps the
     * provided CubeModel.
     */
    public CubeColorsTableModel(@Nonnull CubeModel n) {
        super(n.getDocument(), n.getColors());
        init();
    }

    private void init() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));
        for (int i=0; i < columnNames.length; i++) {
            columnNames[i] = labels.getString(columnNames[i]);
        }
    }

    public void setModel(@Nullable CubeModel value) {
        if (treeNode != null && (treeNode.getParent() instanceof CubeModel)) {
            ((CubeModel) treeNode.getParent()).removePropertyChangeListener(this);
            for (int i = getRowCount() - 1; i > -1; i--) {
                getRowObject(i).removePropertyChangeListener(this);
            }
        }

        if (value == null) {
            setModel(null, null);
        } else {
            setModel(value.getDocument(), value.getColors());
        }

        if (value != null) {
            value.addPropertyChangeListener(this);
            for (int i=getRowCount() - 1; i > -1; i--) {
                getRowObject(i).addPropertyChangeListener(this);
            }
        }
    }

    @Nonnull
    public CubeColorModel getRowObject(int row) {
        return (CubeColorModel) getRow(row);
    }
    
    /**
     * Returns the value for the cell at <I>columnIndex</I> and <I>rowIndex</I>.
     *
     * @param	row	the row whose value is to be looked up
     * @param	column 	the column whose value is to be looked up
     * @return	the value Object at the specified cell
     */
    @Nullable
    public Object getValueAt(int row, int column) {
        CubeColorModel item = getRowObject(row);
        if (item == null) return null;

        switch (column) {
            case 0 : return item.getName();
            case 1 : return item.getColor();
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
     * Invoke this to insert a new row into the table.
     *
     * @param   row      index of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     */
    public void insertRow(int row) {
        CubeColorModel item = new CubeColorModel();
        item.setName("Color");
        
        ((DocumentModel) treeModel).insertNodeInto(item, treeNode, row);
    }
    /**
     * Invoke this to duplicate a row of the table.
     *
     * @param   row      index of the row.
     * @exception   IllegalStateException if the row may not be duplicated.
     */
    public void duplicateRow(int row) {
        CubeColorModel item = (CubeColorModel) ((CubeColorModel) treeNode.getChildAt(row)).clone();
        if (! item.getName().endsWith("Copy"))
            item.setName(item.getName()+" Copy");
        ((DocumentModel) treeModel).insertNodeInto(item, treeNode, row + 1);
    }
    
    /**
     * Sets the value in the cell at <I>columnIndex</I> and <I>rowIndex</I> to 
     * <I>aValue</I> is the new value.
     *
     * @param    aValue         the new value
     * @param    rowIndex     the row whose value is to be changed
     * @param    columnIndex     the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(@Nullable Object aValue, int rowIndex, int columnIndex) {
        CubeColorModel item = getRowObject(rowIndex);
        if (item == null) {
            return;
        }

        String str = (aValue == null) ? null : aValue.toString();
        switch (columnIndex) {
            case 0:
                item.setName(str);
                break;
            case 1:
                Object oldValue = item.getColor();
                item.setColor((Color) aValue);
                break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    /**
     * Returns the most specific superclass for all the cell values 
     * in the column.  This is used by the JTable to set up a default 
     * renderer and editor for the column.
     *
     * @return the common ancestor class of the object values in the model.
     */
    @Nonnull
    public Class<?> getColumnClass(int column) {
        return (column == 0) ? String.class : Color.class;
    }
    
    /**
     * Returns wether the specified node may be removed.
     *
     * @param   row   index of the row.
     */
    public boolean isRowRemovable(int row) {
        return getRowCount() > 1;
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
     * @param   rows   indices of the rows.
     */
    @Nullable
    public Action[] getRowActions(int[] rows) {
        return null;
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(@Nonnull PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source == treeNode.getParent() && evt.getPropertyName() == CubeModel.KIND_PROPERTY) {
            fireTableDataChanged();
        } else if (source instanceof CubeColorModel) {
            int i = treeNode.getIndex((CubeColorModel) source);
            if (i != -1) {
                fireTableRowsUpdated(i, i);
            }
        }
    }

    /**
     * <p>Invoked after nodes have been inserted into the tree.</p>
     *
     * <p>e.path() returns the parent of the new nodes
     * <p>e.childIndices() returns the indices of the new nodes in
     * ascending order.
     */
    public void treeNodesInserted(@Nonnull TreeModelEvent evt) {
        Object path[] = evt.getPath();
        if (path[path.length - 1] == treeNode) {
            int[] indices = evt.getChildIndices();

            for (int i = 0; i < indices.length; i++) {
                ((CubeColorModel) treeNode.getChildAt(indices[i])).addPropertyChangeListener(this);
            }

            fireTableRowsInserted(indices[0], indices[indices.length - 1]);
        } else if (path[path.length - 1] == treeNode.getParent()) {
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
    public void treeNodesRemoved(@Nonnull TreeModelEvent evt) {
        Object path[] = evt.getPath();
        if (path[path.length - 1] == treeNode) {
            int[] indices = evt.getChildIndices();
            fireTableRowsDeleted(indices[0], indices[indices.length - 1]);
        } else if (path[path.length - 1] == treeNode.getParent()) {
            fireTableDataChanged();
        }
    }

    @Nonnull
    public Object[] getCreatableRowTypes(int row) {
        return new Object[] { "Color" };
    }

    @Nonnull
    public Object getCreatableRowType(int row) {
        return "Color";
    }
    /**
     * Invoke this to insert a new row into the table.
     *
     * @param   row      index of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     */
    public void createRow(int row, Object type) {
        CubeColorModel item = new CubeColorModel();
        item.setName("unnamed Color");
        ((DocumentModel) treeModel).insertNodeInto(item, treeNode, row);
    }
}

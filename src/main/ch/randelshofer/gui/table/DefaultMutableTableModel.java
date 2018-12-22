/* @(#)DefaultMutableTableModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.table;

import ch.randelshofer.gui.datatransfer.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Default implementation of a MutableTableModel.
 *
 * FIXME: Should override more methods of the superclass.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>2.2 2001-09-22 Method getRowActions added.
 * <br>2.1 2001-07-27 Duplication of rows added.
 * <br>2.0 2001-07-18
 */
public class DefaultMutableTableModel
extends AbstractTableModel
implements MutableTableModel {
    private final static long serialVersionUID = 1L;
    /**
     * The <code>ArrayList</code> of <code>ArrayList</code> of
     * <code>Object</code> values.
     */
    protected ArrayList<ArrayList<Object>>    dataList;
    
    /** The <code>ArrayList</code> of column identifiers. */
    protected ArrayList<Object>    columnIdentifiers;
    
    /** The <code>ArrayList</code> of column classes. When
     * this array list is null, then all columns are reported
     * as of type Object.class.
     */
    protected ArrayList<Class<?>> columnClasses;
    
    /** The data flavour for JVM local object transferables. */
    private final static DataFlavor tableFlavor = new DataFlavor(Object[][].class, "Table");
    private final static DataFlavor rowFlavor = new DataFlavor(Object[].class, "Row");
    
    /**
     * Array with importable data flavors.
     */
    private final static DataFlavor[] importableFlavors = {
        tableFlavor,
        DataFlavor.stringFlavor,
        DataFlavor.getTextPlainUnicodeFlavor()
    };
    
    /**
     * The enabled state of the model.
     * By default this value is true.
     */
    private boolean enabled = true;
    
    /**
     * Constructs a DefaultMutableTableModel which has a table of
     * zero columns and zero rows.
     */
    public DefaultMutableTableModel() {
        this(0, 0);
    }
    
    /**
     * Creates an array list of the specified size and fills
     * it with <code>null</code> values.
     */
    private static ArrayList<ArrayList<Object>> createList(int size) {
        ArrayList<ArrayList<Object>> l = new ArrayList<ArrayList<Object>>(size);
        for (int i=0; i < size; i++) {
            l.add(null);
        }
        return l;
    }
    
    /**
     * Constructs a DefaultMutableTableModel with numRows and
     * numColumns of null object values.
     */
    public DefaultMutableTableModel(int rowCount, int columnCount) {
        this(new Object[rowCount][columnCount], new Object[columnCount]);
    }
    
    
    /**
     * Constructs a DefaultMutableTableModel and initializes the
     * table by passing dataList and columnNames to the setDataList()
     * method.
     */
    public DefaultMutableTableModel(Object[][] dataList, Object[] columnNames) {
        setDataVector(dataList, columnNames);
    }
    /**
     * Constructs a DefaultMutableTableModel and initializes the
     * table by passing dataList and columnNames to the setDataList()
     * method.
     */
    public DefaultMutableTableModel(Object[][] dataList, Object[] columnNames, Class<?>[] columnClasses) {
        setDataVector(dataList, columnNames);
        this.columnClasses = new ArrayList<Class<?>>(Arrays.asList(columnClasses));
    }
    
    /**
     * Constructs a DefaultMutableTableModel wit as many columns as there
     * are elements in columnNames and numRows of null object values.
     */
    public DefaultMutableTableModel(Object[] columnNames, int rowCount) {
        setDataVector(createList(rowCount), Arrays.asList(columnNames));
    }
    /**
     * Constructs a DefaultMutableTableModel with as many columns as there
     * are elements in columNames and numRows of null object values.
     */
    public DefaultMutableTableModel(List<Object> columnNames, int rowCount) {
        setDataVector(createList(rowCount), columnNames);
    }
    /**
     * Constructs a DefaultMutableTableModel and initializes the table
     * by passing dataList and columnNames to the setDataVector() method.
     */
    public DefaultMutableTableModel(ArrayList<ArrayList<Object>> dataList, List<Object> columnNames) {
        setDataVector(dataList, columnNames);
    }
    
    /**
     *  Replaces the current <code>dataVector</code> instance variable
     *  with the new Vector of rows, <code>dataVector</code>.
     *  <code>columnIdentifiers</code> are the names of the new
     *  columns.  The first name in <code>columnIdentifiers</code> is
     *  mapped to column 0 in <code>dataVector</code>. Each row in
     *  <code>dataVector</code> is adjusted to match the number of
     *  columns in <code>columnIdentifiers</code>
     *  either by truncating the <code>Vector</code> if it is too long,
     *  or adding <code>null</code> values if it is too short.
     *  <p>Note that passing in a <code>null</code> value for
     *  <code>dataVector</code> results in unspecified behavior,
     *  an possibly an exception.
     *
     * @param   dataVector         the new dataList vector
     * @param   columnIdentifiers     the names of the columns
     */
    public void setDataVector(List<ArrayList<Object>> dataVector, List<Object> columnIdentifiers) {
        this.dataList = (dataVector == null) ? new ArrayList<ArrayList<Object>>() : new ArrayList<ArrayList<Object>>(dataVector);
        this.columnIdentifiers = (columnIdentifiers == null) ? new ArrayList<Object>() : new ArrayList<Object>(columnIdentifiers);
        justifyRows(0, getRowCount());
        fireTableStructureChanged();
    }
    
    /**
     *  Replaces the value in the <code>dataVector</code> instance
     *  variable with the values in the array <code>dataVector</code>.
     *  The first index in the <code>Object[][]</code>
     *  array is the row index and the second is the column index.
     *  <code>columnIdentifiers</code> are the names of the new columns.
     *
     * @param dataVector		the new dataList vector
     * @param columnIdentifiers	the names of the columns
     */
    public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
        ArrayList<ArrayList<Object>> l = new ArrayList<ArrayList<Object>>(dataVector.length);
        for (int i=0; i < dataVector.length; i++) {
            l.add(new ArrayList<Object>(Arrays.asList(dataVector[i])));
        }
        setDataVector(l, new ArrayList<Object>(Arrays.asList(columnIdentifiers)));
    }
    
    //
    // Manipulating rows
    //
    /**
     * Removes elements from the end of the provided array list
     * if it has more elements than <code>size</code> elements,
     * or adds <code>null</code> elements at the end of the array list
     * if it has less than <code>size</code> elements.
     */
    private void setListSize(ArrayList<?> l, int size) {
        while (l.size() < size) {
            l.add(null);
        }
        while (l.size() > size) {
            l.remove(size - 1);
        }
    }
    
    /**
     * Adjusts the size of the row <code>ArrayList</code>'s if
     * to match <code>getRowCount()</code>.
     */
    private void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed
        // instead of the AbstractTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        setListSize(dataList, getRowCount());
        for (int i = from; i < to; i++) {
            if (dataList.get(i) == null) {
                dataList.set(i, new ArrayList<Object>(getColumnCount()));
            }
            setListSize((ArrayList) dataList.get(i), getColumnCount());
        }
    }
    
    /**
     * Invoke this to insert a new row into the table.
     *
     * @param  type         The type of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     */
    public void createRow(int row, Object type) {
        addRow(row, new Object[getColumnCount()]);
    }
    public Object[] getCreatableRowTypes(int row) {
        return new Object[] { getCreatableRowType(row) };
    }
    public Object getCreatableRowType(int row) {
        return "Row";
    }
    
    /**
     * Gets the enabled state of the model.
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Returns wether a row may be inserted.
     *
     * @param   row      row of the new row.
     * @return  Returns true for all rows. Returns false if the model is disabled.
     */
    public boolean isRowAddable(int row) {
        return enabled;
    }
    /**
     * Returns wether the specified node may be removed.
     *
     * @param   row   row of the row.
     * @return  Returns true for all rows. Returns false if the model is disabled.
     */
    public boolean isRowRemovable(int row) {
        return enabled;
    }
    /**
     * Message this to remove a row from the table.
     *
     * @param   row   row of the row.
     * @exception   IllegalStateException if the row may not be removed.
     */
    public void removeRow(int row) {
        if (! isRowRemovable(row)) {
            throw new IllegalStateException("Can't remove row.");
        }
        Object removed = dataList.remove(row);
        fireTableRowsDeleted(row, row);
        //return removed;
    }
    /**
     * Returns wether the specified row may be duplicated.
     *
     * @param   row   row of the row.
     * @return  Returns true for all rows. Returns false if the model is disabled.
     */
    public boolean isRowDuplicateable(int row) {
        return enabled;
    }
    
    /**
     * Sets the enabled state of the model.
     * By default the model is not enabled.
     */
    public void setEnabled(boolean b) {
        enabled = b;
    }
    /**
     * Returns an array of compound actions for the indicated rows.
     */
    public Action[] getRowActions(int[] rows) {
        return new Action[0];
    }
    
    
    /**
     * Creates a Transferable to use as the source for a dataList
     * transfer of the specified elements.
     * Returns the representation of the rows
     * to be transferred, or null if transfer is not possible.
     *
     * @param   rows     Row rows.
     */
    public Transferable exportRowTransferable(int[] rows) {
        CompositeTransferable t = new CompositeTransferable();
        t.add(TableModels.createLocalTransferable(this, rows));
        t.add(TableModels.createHTMLTransferable(this, rows));
        t.add(TableModels.createPlainTransferable(this, rows));
        return t;
    }
    
    /**
     * Indicates whether the model would accept an import of the
     * given set of dataList flavors prior to actually attempting
     * to import it.
     *
     * @param   transferFlavors the dataList formats available
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   row    The insertion point. 0 &lt;= index &lt;= getSize()
     * @param   asChild  Indicates that the item is to be imported as a child
     *                   of the list item rather than as an individual element
     * /                   of the list.
     *
     * @return true if the dataList can be imported.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public boolean isRowImportable(DataFlavor[] transferFlavors, int action, int row, boolean asChild) {
        if (action == DnDConstants.ACTION_LINK || asChild || ! isRowAddable(row)) return false;
        
        for (int i=0; i < transferFlavors.length; i++) {
            for (int j=0; j < importableFlavors.length; j++) {
                if (transferFlavors[i].equals(importableFlavors[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Causes a transfer to the model from a clipboard or
     * a DND drop operation.
     *
     * @param   t        The transfer dataList.
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   row    The insertion point. 0 &lt;= index &lt;= getSize()
     * @param   asChild  Indicates that the item is to be imported as a child
     *                   of the list item rather than as an individual element
     *                   of the list.
     *
     * @return The number of imported elements.
     */
    public int importRowTransferable(Transferable t, int action, int row, boolean asChild)
    throws UnsupportedFlavorException, IOException {
        if (! isRowImportable(t.getTransferDataFlavors(), action, row, asChild))
            throw new UnsupportedFlavorException(null);
        
        Object[][] transferData;
        try {
            if (t.isDataFlavorSupported(tableFlavor)) {
                transferData = (Object[][]) t.getTransferData(tableFlavor);
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                transferData = TableModels.getStringTable(t, getColumnCount());
            } else if (t.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
                transferData = TableModels.getPlainTable(t, getColumnCount());
            } else {
                throw new UnsupportedFlavorException(tableFlavor);
            }
            
            for (int i=0; i < transferData.length; i++) {
                dataList.add(row + i, new ArrayList<Object>(Arrays.asList(transferData[i])));
            }
            justifyRows(row, row + transferData.length - 1);
            fireTableRowsInserted(row, row + transferData.length);
            
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return transferData.length;
    }
    
    /**
     * Returns the number of columns in this data table.
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columnIdentifiers.size();
    }
    
    public int getRowCount() {
        return dataList.size();
    }
    
    /**
     * Returns an attribute value for the cell at <code>row</code>
     * and <code>column</code>.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return                  the value Object at the specified cell
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public Object getValueAt(int row, int column) {
        ArrayList<Object> rowList = dataList.get(row);
        return rowList.get(column);
    }
    /**
     * Sets the object value for the cell at <code>column</code> and
     * <code>row</code>.  <code>aValue</code> is the new value.  This method
     * will generate a <code>tableChanged</code> notification.
     *
     * @param   aValue          the new value; this can be null
     * @param   row             the row whose value is to be changed
     * @param   column          the column whose value is to be changed
     * @exception  ArrayIndexOutOfBoundsException  if an invalid row or
     *               column was given
     */
    public void setValueAt(Object aValue, int row, int column) {
        ArrayList<Object> rowList = dataList.get(row);
        rowList.set(column, aValue);
        fireTableCellUpdated(row, column);
    }
    
    /** Inserts an element at the specified index in the model.
     *
     * @param row index at which the specified element is to be inserted.
     * @param element element to be inserted.
     *
     * @throws  ClassCastException if the element is not compatible with
     *          one of the data flavors type returned by
     *          <code>getInsertableDataFlavors(int)</code>
     * @throws  IllegalStateException if isRowInsertable(int) returns false.
     * @throws  IndexOutOfBoundsException if the index is out of range
     *          (index &lt; 0 || index &gt; size()).
     *
     */
    public void addRow(int row, Object element) {
        if (! isRowAddable(row)) {
            throw new IllegalStateException("Element may not be added at row "+row);
        }
        dataList.add(row, new ArrayList<Object>(Arrays.asList((Object[]) element)));
        fireTableRowsInserted(row, row);
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        if (columnClasses == null) {
            return Object.class;
        } else {
            Class<?> c = (Class) columnClasses.get(column);
            return (c == null) ? Object.class : c;
        }
    }
    
    /**
     * Returns the column name.
     *
     * @return a name for this column using the string value of the
     * appropriate member in <code>columnIdentifiers</code>.
     * If <code>columnIdentifiers</code> does not have an entry 
     * for this index, returns the default
     * name provided by the superclass
     */
    public String getColumnName(int column) {
        Object id = null; 
	// This test is to cover the case when 
	// getColumnCount has been subclassed by mistake ... 
	if (column < columnIdentifiers.size()) {  
	    id = columnIdentifiers.get(column); 
	}
        return (id == null) ? super.getColumnName(column) 
                            : id.toString();
    }

/**
 * Returns true if the cell at <code>rowIndex</code> and
 * <code>columnIndex</code> is editable. Otherwise, 
 * <code>setValueAt</code> on the cell will not change
 * the value of that cell.
 * 
 * @param rowIndex - the row whose value to be queried
 * @param columnIndex - the column whose value to be queried 
 * @return true if the model is enabled
 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
    return isEnabled();
}
}

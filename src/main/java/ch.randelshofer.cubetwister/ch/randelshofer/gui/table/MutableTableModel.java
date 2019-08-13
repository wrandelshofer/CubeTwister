/* @(#)MutableTableModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.table;

import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.Action;
import javax.swing.table.TableModel;

/**
 * Specifies the requirements for a mutable table model.
 * <p>
 * The mutable table model adds the following operations
 * to the <code>javax.swing.table.TableModel</code> interface:
 * <ul>
 * <li>An Abstract factory for the creation of new elements.</li>
 * <li>Insert and remove operations.</li>
 * <li>Operations for retrieving <code>javax.swing.Action</code>'s
 * for a group of elements</li>
 * <li>A setter operation for changing the value of an element.</li>
 * <li>Operations for importing and exporting elements from a
 * <code>java.awt.transfer.Transferable</code>.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 */

public interface MutableTableModel
extends TableModel {
    // Abstract factory operations
    // ====================================================
    /**
     * Returns the types of elements that can be created at 
     * the specified index of the list.
     *
     * @param   row  The insertion point. 0 &lt;= index &lt;= getRowCount()
     * @return  an array of Object's that specify element types that can be
     *          inserted at the insertion point. Returns an empty array if no
     *          elements can be inserted here. Never returns null.
     *          This array must include the type returned by operation 
     *          getCreatableType.
     */
    public Object[] getCreatableRowTypes(int row);
    /**
     * Returns the default type of elements that can be created at 
     * the specified index of the list.
     *
     * @param   row  The insertion point. 0 &lt;= index &lt;= getRowCount()
     * @return  an Object that specifies the default element type that can be
     *          inserted at the insertion point. Returns null if no
     *          elements can be inserted here. 
     *          The value must be one of the types returned by operation 
     *          getCreatableTypes.
     */
    public Object getCreatableRowType(int row);
    /**
     * Creates the specified element type at the specified position in this list
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param row index at which the specified element is to be inserted.
     * @param type element type to be inserted.
     * 
     * @throws    IllegalArgumentException if the type is not contained in
     *            the array returned by getInsertableTypes(int).
     * @throws    IndexOutOfBoundsException if the index is out of range
     *		  (index &lt; 0 || index &gt; size()).
     */
    public void createRow(int row, Object type)
    throws IllegalStateException;

    
    // Insert and remove operations
    // ====================================================
    
    /**
     * Returns true if a row can be added.
     *
     * @param   row   index of the element. 0 &lt;= index &lt;= getSize()
     */
    public boolean isRowAddable(int row);
    
    /**
     * Returns true if the specified row can be removed.
     *
     * @param   row   index of the element. 0 &lt;= index &lt;= getSize()
     * @see #removeRow(int)
     */
    public boolean isRowRemovable(int row);
    
    /**
     * Removes an element from the model.
     *
     * @param   row  index of the element. 0 &lt;= index &lt;= getSize()
     * @exception   IllegalStateException if the element may not be removed.
     * @see #isRowRemovable(int)
     */
    public void removeRow(int row)
    throws IllegalStateException;
    
    // Editing operations.
    // ====================================================
    /**
     * Returns true if the specified cell may be edited.
     *
     * @param   row      index of the row.
     * @param   column   index of the column.
     */
    public boolean isCellEditable(int row, int column);
    
    // Operations for determining the actions for elements
    // ====================================================
    /**
     * Gets actions for the specified rows.
     *
     * @param   rows   The rows.
     */
    public Action[] getRowActions(int[] rows);
    
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
    public Transferable exportRowTransferable(int[] rows);
    
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
    public boolean isRowImportable(DataFlavor[] transferFlavors, int action, int row, boolean asChild);
    
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
    public int importRowTransferable(Transferable t, int action, int row, boolean asChild)
    throws UnsupportedFlavorException, IOException;
}
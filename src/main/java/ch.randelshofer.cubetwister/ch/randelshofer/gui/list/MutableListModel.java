/* @(#)MutableListModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.list;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;

/**
 * Specifies the requirements for a mutable list model.
 * <p>
 * The mutable list model adds suport for the following operations
 * to the <code>javax.swing.ListModel</code> interface:
 * <ul>
 * <li>An Abstract factory for the creation of new elements.</li>
 * <li>Insert and remove operations.</li>
 * <li>A setter operation for changing the value of an element.</li>
 * <li>Operations for retrieving <code>javax.swing.Action</code>'s
 *      for a group of elements</li>
 * <li>Operations for importing and exporting elements from a 
 * <code>java.awt.transfer.Transferable</code>.</li>
 * </ul>
 *
 *
 * @author  Werner Randelshofer
 */
public interface MutableListModel
extends ListModel {
    // Abstract factory operations
    // ====================================================
    /**
     * Returns the types of elements that can be created at 
     * the specified index of the list.
     *
     * @param   index  The insertion point. 0 &lt;= index &lt;= getSize()
     * @return  an array of Object's that specify element types that can be
     *          inserted at the insertion point. Returns an empty array if no
     *          elements can be inserted here. Never returns null.
     *          This array must include the type returned by operation 
     *          getCreatableType.
     */
    public Object[] getCreatableTypes(int index);
    /**
     * Returns the default type of elements that can be created at 
     * the specified index of the list.
     *
     * @param   index  The insertion point. 0 &lt;= index &lt;= getSize()
     * @return  an Object that specifies the default element type that can be
     *          inserted at the insertion point. Returns null if no
     *          elements can be inserted here. 
     *          The value must be one of the types returned by operation 
     *          getCreatableTypes.
     */
    public Object getCreatableType(int index);
    /**
     * Creates the specified element type at the specified position in this list
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param type element type to be inserted.
     * 
     * @throws    IllegalArgumentException if the type is not contained in
     *            the array returned by getInsertableTypes(int).
     * @throws    IndexOutOfBoundsException if the index is out of range
     *		  (index &lt; 0 || index &gt; size()).
     */
    public void create(int index, Object type);

    

    // Insert and remove operations
    // ====================================================
    
    /**
     * Returns true if an element can be added.
     *
     * @param   index   index of the element. 0 &lt;= index &lt;= getSize()
     * @see #remove(int)
     */
    public boolean isAddable(int index);
    /**
     * Returns true if the specified element can be removed.
     *
     * @param   index   index of the element. 0 &lt;= index &lt;= getSize()
     * @see #remove(int)
     */
    public boolean isRemovable(int index);
    
    /**
     * Removes an element from the model.
     *
     * @param   index   index of the element. 0 &lt;= index &lt;= getSize()
     * @return  The removed Object.
     * @exception   IllegalStateException if the element may not be removed.
     * @see #isRemovable(int)
     */
    public Object remove(int index)
    throws IllegalStateException;
    

    // Editing operations.
    // ====================================================
    /**
     * Returns true if the value of the specified element may be changed
     * using <code>setElementAt(Object,int)</code>.
     *
     * @param   index      index of the element. 0 &lt;= index &lt;= getSize()
     * @see #setElementAt(Object, int)
     */
    public boolean isEditable(int index);
    
    /**
     * Sets the value of an element at the given index.
     *
     * @param value - the new value
     * @param index - the index whose value is to be changed. 0 &lt;= index &lt; getSize()
     * @exception   IllegalStateException if the element is not editable.
     * @see #isEditable(int)
     */
    public void setElementAt(Object value, int index)
    throws IllegalStateException;
    
    // Operations for determining the actions for elements
    // ====================================================
    /**
     * Gets actions for the specified elements.
     *
     * @param   indices   The elements.
     */
    public Action[] getActions(int[] indices);
    
    // Datatransfer operations
    // =======================
    /**
     * Creates a Transferable to use as the source for a data
     * transfer of the specified elements.
     *
     * @param   indices     Element indices.
     * @return A Transferable representing the elements
     * to be transferred, or null if transfer is not possible.
     */
    public Transferable exportTransferable(int[] indices);
    
    /**
     * Indicates whether the model would accept an import of the
     * given set of data flavors prior to actually attempting
     * to import it.
     *
     * @param   transferFlavors the data formats available
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   index    The insertion point. 0 &lt;= index &lt;= getSize()
     * @param   asChild  True if the Transferable is dropped as a child of
     *                   the element.
     *
     * @return true if the data can be imported.
     *
     * @see java.awt.dnd.DnDConstants
     */
    public boolean isImportable(DataFlavor[] transferFlavors, int action, int index, boolean asChild);
    
    /**
     * Causes a transfer to the model from the specified transferable.
     *
     * @param   t        The transfer data.
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   index    The insertion point. 0 &lt;= index &lt;= getSize()
     * @param   asChild  True if the Transferable is dropped as a child of
     *                   the element.
     *
     * @return The number of imported elements.
     */
    public int importTransferable(Transferable t, int action, int index, boolean asChild)
    throws UnsupportedFlavorException, IOException;
}

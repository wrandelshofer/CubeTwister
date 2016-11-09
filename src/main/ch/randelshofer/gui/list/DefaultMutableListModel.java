/* @(#)DefaultMutableListModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.list;

import ch.randelshofer.gui.datatransfer.*;
import ch.randelshofer.util.*;

import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Default implementation of a MutableListModel.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * into a template.
 * <br>2.0 2003-11-01 Revised.
 * <br>1.0 2002-11-20 Created.
 */
public class DefaultMutableListModel<T>
        extends AbstractListModel
        implements MutableListModel {
    private final static long serialVersionUID = 1L;
    private ArrayList<T> list = new ArrayList<T>();
    
    private final static DataFlavor listFlavor = new DataFlavor(List.class, "List");
    private final static DataFlavor objectFlavor = new DataFlavor(Object.class, "Object");
    
    /**
     * ArrayList with importable data flavors.
     */
    private ArrayList<DataFlavor> importableFlavors = new ArrayList<DataFlavor>(
            Arrays.asList(new DataFlavor[] {
        listFlavor,
        objectFlavor,
        DataFlavor.stringFlavor,
        DataFlavor.getTextPlainUnicodeFlavor()
    }));
    
    
    /** Creates a new instance of DefaultMutableListModel */
    public DefaultMutableListModel() {
    }
    
    public DefaultMutableListModel(Collection<T> data) {
        list.addAll(data);
    }
    public DefaultMutableListModel(T[] data) {
        list.addAll(Arrays.asList(data));
    }
    
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
     */
    public Object[] getCreatableTypes(int index) {
        return new Object[] {"Element"};
    }
    /**
     * Returns the default type of elements that can be created at
     * the specified index of the list.
     *
     * @param   index  The insertion point. 0 &lt;= index &lt;= getSize()
     * @return  an Object that specifies the default element type that can be
     *          inserted at the insertion point. Returns null if no
     *          elements can be inserted here.
     */
    public Object getCreatableType(int index) {
        return "Element";
    }
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
    @SuppressWarnings("unchecked")
    @Override
    public void create(int index, Object type) {
        if (ArrayUtil.indexOf(getCreatableTypes(index), type) == -1) {
            throw new IllegalArgumentException("Illegal type:"+type);
        }
        try {
            add(index, (T) "Element");
        } catch (ClassCastException e) {
            add(index, null);
        }
    }
    
    // Insert and remove operations
    // ====================================================
    
    /**
     * Returns true if an element can be added at the specified index.
     *
     * @param   index   index of the element. 0 &lt;= index &lt;= getSize()
     * @see #add(int, Object)
     */
    public boolean isAddable(int index) {
        return true;
    }
    /**
     * Inserts the specified element at the specified position in this list
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their
     * indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     *
     * @throws    IllegalArgumentException if the element is not of an
     *            acceptable type returned by getInsertableTypes(int).
     * @throws    IndexOutOfBoundsException if the index is out of range
     *		  (index &lt; 0 || index &gt; size()).
     */
    public void add(int index, T element) {
        list.add(index, element);
        fireIntervalAdded(this, index, index);
    }
    
    /**
     * Adds the element to the end of the list.
     *
     * @throws  IllegalStateException if isElementInsertableAt(getSize()) returns false.
     * @param element the element.
     */
    public void add(T element) {
        add(list.size(), element);
    }
    
    /**
     * Returns true if the specified element may be removed.
     *
     * @param   index   index of the element. 0 &lt;= index &lt;= getSize()
     * @see #remove(int)
     */
    public boolean isRemovable(int index) {
        return true;
    }
    
    /**
     * Removes an element from the model.
     *
     * @param   index   index of the element. 0 &lt;= index &lt;= getSize()
     * @return  The removed Object.
     * @exception   IllegalStateException if the element may not be removed.
     * @see #isRemovable(int)
     */
    public Object remove(int index) {
        if (! isRemovable(index)) throw new IllegalStateException();
        Object removed = list.remove(index);
        fireIntervalRemoved(this, index, index);
        return removed;
    }
    
    // Editing operations.
    // ====================================================
    /**
     * Returns true if the value of the specified element may be changed
     * using <code>setElementAt(Object,int)</code>.
     *
     * @param   index      index of the element. 0 &lt;= index &lt;= getSize()
     * @see #setElementAt(Object, int)
     */
    public boolean isEditable(int index) {
        return true;
    }
    
    /**
     * Returns the value at the specified index.
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    public T getElementAt(int index) {
        return list.get(index);
    }
    
    /**
     * Sets the value of an element at the given index.
     *
     * @param value - the new value
     * @param index - the index whose value is to be changed. 0 &lt;= index &lt; getSize()
     * @exception   IllegalStateException if the element is not editable.
     * @see #isEditable(int)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setElementAt(Object value, int index) {
        if (! isEditable(index)) throw new IllegalStateException();
        list.set(index, (T) value);
        fireContentsChanged(value, index, index);
    }
    /**
     * Sets the value of an element at the given index.
     *
     * @param index - the index whose value is to be changed. 0 &lt;= index &lt; getSize()
     * @param value - the new value
     * @exception   IllegalStateException if the element is not editable.
     * @see #isEditable(int)
     */
    public void set(int index, T value) {
        if (! isEditable(index)) throw new IllegalStateException();
        list.set(index, value);
        fireContentsChanged(value, index, index);
    }
    
    
    /**
     * Gets actions for the specified elements.
     *
     * @param   indices   The elements.
     */
    public Action[] getActions(int[] indices) {
        return new Action[0];
    }
    
    
    /**
     * Returns the length of the list.
     * @return the length of the list
     */
    public int getSize() {
        return list.size();
    }
    
    /**
     * Creates a Transferable to use as the source for a data
     * transfer of the specified elements.
     * Returns the representation of the elements
     * to be transferred, or null if transfer is not possible.
     *
     * @param   indices     Element indices.
     */
    public Transferable exportTransferable(int[] indices) {
        CompositeTransferable t = new CompositeTransferable();
        t.add(ListModels.createLocalTransferable(this, indices, Object.class));
        t.add(ListModels.createHTMLTransferable(this, indices));
        t.add(ListModels.createPlainTransferable(this, indices));
        return t;
    }
    
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
    public boolean isImportable(DataFlavor[] transferFlavors, int action, int index, boolean asChild) {
        if (
                asChild
                || ! isAddable(index)) {
            return false;
        }
        
        for (int i=0; i < transferFlavors.length; i++) {
            for (DataFlavor importable : importableFlavors) {
                if (transferFlavors[i].match(importable)) {
                    return true;
                }
            }
        }
        return false;
    }
    
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
    @SuppressWarnings("unchecked")
    public int importTransferable(Transferable t, int action, int index, boolean asChild)
    throws UnsupportedFlavorException, IOException {
        if (asChild || ! isAddable(index)) {
            return 0;
        }
        
        int count = 0;
        List<T> l = null;
        
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            l = (List<T>) t.getTransferData(DataFlavor.javaFileListFlavor);
        } else if (t.isDataFlavorSupported(listFlavor)) {
            l = (List<T>) t.getTransferData(listFlavor);
        } else if (t.isDataFlavorSupported(objectFlavor)) {
            l = new ArrayList<T>(1);
            l.add((T) t.getTransferData(objectFlavor));
        } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            l = (List<T>) ListModels.getStringList(t);
        } else if (t.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
            l = (List<T>) ListModels.getPlainList(t);
        } else {
            throw new UnsupportedFlavorException(listFlavor);
        }
        
        return addAll(index, l);
    }
    
    public int addAll(int index, List<T> l) {
        list.addAll(index, l);
        fireIntervalAdded(this, index, index + l.size() - 1);
        return l.size();
    }
    
    
    /*
     * Returns an iterator over the elements in this collection.
     *
     * @return an <tt>Iterator</tt> over the elements in this collection
     */
    public Iterator<T> iterator() {
        return list.iterator();
    }
    
    /**
     * Sets the importable data flavors for this list model.
     * @param newValue A list which contains the data flavors.
     */
    public void setImportableFlavors(List<DataFlavor> newValue) {
        importableFlavors = new ArrayList<DataFlavor>(newValue);
    }
    /**
     * Gets the importable data flavors for this list model.
     * @return An immutable list which contains the data flavors.
     */
    public List<DataFlavor> getImportableFlavors() {
        return Collections.unmodifiableList(importableFlavors);
    }
}

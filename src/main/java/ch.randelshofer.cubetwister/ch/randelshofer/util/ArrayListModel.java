/* @(#)ArrayListModel.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A ListModel backed by an ArrayList.
 *
 * @author Werner Randelshofer
 * @param <E>
 */
public class ArrayListModel<E> extends javax.swing.AbstractListModel
        implements List<E>/*, RandomAccess*/ {
    private final static long serialVersionUID = 1L;

    private ArrayList<E> delegate;

    /**
     * Creates a new instance of ArrayListModel
     */
    public ArrayListModel() {
        delegate = new ArrayList<E>();
    }

    @Override
    public E getElementAt(int index) {
        return delegate.get(index);
    }

    @Override
    public int getSize() {
        return delegate.size();
    }

    @Override
    public boolean add(E o) {
        int index = delegate.size();
        delegate.add(o);
        fireIntervalAdded(this, index, index);
        return true;
    }

    @Override
    public void add(int index, E element) {
        delegate.add(index, element);
        fireIntervalAdded(this, index, index);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends E> c) {
        if (c.size() > 0) {
            int index = delegate.size();
            delegate.addAll(c);
            fireIntervalAdded(this, index, index + c.size() - 1);

            return true;
        } else {
            return false;
        }

    }

    public boolean addAll(E[] c) {
        return addAll(Arrays.asList(c));
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends E> c) {
        if (delegate.addAll(index, c)) {
            fireIntervalAdded(this, index, index + c.size() - 1);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        int index1 = delegate.size() - 1;
        delegate.clear();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection c) {
        return delegate.containsAll(c);
    }

    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Nonnull
    @Override
    public ListIterator<E> listIterator() {
        return delegate.listIterator();
    }

    @Nonnull
    @Override
    public ListIterator<E> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        int index = delegate.indexOf(o);
        if (index != -1) {
            delegate.remove(index);
            fireIntervalRemoved(this, index, index);
            return true;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        E removed = delegate.remove(index);
        fireIntervalRemoved(this, index, index);
        return removed;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        boolean hasRemoved = false;
        Iterator i = c.iterator();
        while (i.hasNext()) {
            hasRemoved = remove(i.next()) || hasRemoved;
        }
        return hasRemoved;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean hasChanged;
        ArrayList<E> temp = delegate;
        delegate = new ArrayList<E>();
        fireIntervalRemoved(this, 0, temp.size() - 1);
        hasChanged = temp.retainAll(c);
        delegate = temp;
        fireIntervalAdded(this, 0, delegate.size() - 1);
        return hasChanged;
    }

    @Override
    public E set(int index, E element) {
        E result = delegate.set(index, element);
        fireContentsChanged(this, index, index);
        return result;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Nonnull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Nonnull
    public <T> T[] toArray(@Nonnull T[] a) {
        return delegate.toArray(a);
    }

    /**
     * Returns a string that displays and identifies this object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        return delegate.toString();
    }
}

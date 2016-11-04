/*
 * @(#)ReverseVectorEnumeration.java  0.0  2000-01-02
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * ReverseListIteerator.
 *
 * @author Werner Randelshofer.
 * @version 0.0 2000-01-02 Draft.
 */
public class ReverseListIterator<T>
        implements Iterator<T> {

    private List<T> list;
    private int index;

    public ReverseListIterator(List<T> list) {
        this.list = list;
        index = list.size() - 1;
    }

    @Override
    public boolean hasNext() {
        return index >= 0;
    }

    @Override
    public T next() {
        return list.get(index--);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}

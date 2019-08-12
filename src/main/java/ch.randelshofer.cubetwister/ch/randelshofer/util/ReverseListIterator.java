/* @(#)ReverseVectorEnumeration.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import java.util.Iterator;
import java.util.List;

/**
 * ReverseListIteerator.
 *
 * @author Werner Randelshofer.
 * @version $Id$
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

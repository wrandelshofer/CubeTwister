/*
 * @(#)EnumerationIterator.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

import java.util.Enumeration;

/**
 * Wraps an Enumeration with the Iterator interface.
 *
 * @author Werni Randelshofer
 */
public class EnumerationIterator<T> implements java.util.Iterator<T> {
    private Enumeration<T> enumer;

    /** Creates new EnumIterator */
    public EnumerationIterator(Enumeration<T> e) {
        enumer = e;
    }

    @Override
    public boolean hasNext() {
        return enumer.hasMoreElements();
    }

    @Override
    public T next() {
        return enumer.nextElement();
    }

    /**
     * Throws always UnsupportedOperationException.
     * @exception UnsupportedOperationException
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}

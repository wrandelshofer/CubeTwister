/*
 * @(#)EnumerationIterator.java  1.2  2010-01-03
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.util;
import java.util.*;

/**
 * Wraps an Enumeration with the Iterator interface.
 *
 * @author  Werni Randelshofer
 * @version $Id$
 * <br>1.0 2001-10-08
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

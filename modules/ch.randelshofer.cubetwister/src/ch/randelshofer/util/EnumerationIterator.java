/* @(#)EnumerationIterator.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
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

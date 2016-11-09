/* @(#)SequenceIterator.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** 
 * This class Encapsulates two Enumerations.
 *
 * @author Werner Randelshofer
 * @version $Id$
*/
public class SequenceIterator<T> 
implements Iterator<T> {
    /** The first enumeration. */
    private Iterator<T> first;
    /** The second enumeration. */
    private Iterator<T> second;

    /** 
     * @param first The first enumeration.
     * @param second The second enumeration.
     */
    public SequenceIterator(Iterator<T> first, Iterator<T> second) {
        this.first = first;
        this.second = second;
    }

    /** 
     * Tests if this enumeration contains next element.
     * @return  <code>true</code> if this enumeration contains it
     *           <code>false</code> otherwise.
     */ 
    @Override
    public boolean hasNext() {
        return first.hasNext() || second.hasNext();
    }

    /** 
     * Returns the next element of this enumeration.
     * @return     the next element of this enumeration.
     * @exception  NoSuchElementException  if no more elements exist.
     */
    @Override
    public synchronized T next() {
        if (first.hasNext()) {
            return first.next();
        } else {
            return second.next();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}

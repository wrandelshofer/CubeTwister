/* @(#)SequenceIterator.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class Encapsulates a sequence of iterators.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SequenceIterator<T>
        implements Iterator<T> {
    /**
     * The current iterator.
     */
    private Iterator<T> current;
    /**
     * The remaining iterators.
     */
    private Iterator<Iterator<T>> iterators;

    public SequenceIterator(Iterator<T> first, Iterator<T> second) {
        this(List.of(first, second).iterator());
    }

    public SequenceIterator(Iterable<Iterator<T>> iterators) {
        this(iterators.iterator());
    }
    public SequenceIterator(Iterator<Iterator<T>> iterators) {
        this.current = Collections.emptyIterator();
        this.iterators = iterators;
    }

    private boolean move() {
        if (!current.hasNext()) {
            while (iterators.hasNext()) {
                current = iterators.next();
                if (current.hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        return current.hasNext()|| move();
    }

    @Override
    public T next() {
        move();
        return current.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}

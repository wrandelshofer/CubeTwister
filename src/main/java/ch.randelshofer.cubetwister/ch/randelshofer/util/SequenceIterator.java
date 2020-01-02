/* @(#)SequenceIterator.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class Encapsulates a sequence of iterators.
 *
 * @author Werner Randelshofer
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

    public SequenceIterator(@Nonnull Iterator<T> first, @Nonnull Iterator<T> second) {
        this(List.of(first, second).iterator());
    }

    public SequenceIterator(@Nonnull Iterable<Iterator<T>> iterators) {
        this(iterators.iterator());
    }

    public SequenceIterator(Iterator<Iterator<T>> iterators) {
        this.current = Collections.emptyIterator();
        this.iterators = iterators;
    }

    /**
     * Moves to a non-empty iterator if the current has no more elements.
     *
     * @return true if are on a non-empty iterator or have moved to a non-empty
     * iterator, false if we have not found a non-empty iterator.
     */
    private boolean move() {
        if (current.hasNext()) {
            return true;
        } else {
            while (iterators.hasNext()) {
                current = iterators.next();
                if (current.hasNext()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean hasNext() {
        return current.hasNext() || move();
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

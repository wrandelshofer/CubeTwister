/*
 * @(#)ReverseListIterator.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;

import java.util.Iterator;
import java.util.List;

/**
 * ReverseListIteerator.
 *
 * @author Werner Randelshofer.
 */
public class ReverseListIterator<T>
        implements Iterator<T> {

    private List<T> list;
    private int index;

    public ReverseListIterator(@Nonnull List<T> list) {
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

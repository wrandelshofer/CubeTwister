/*
 * @(#)IteratorEnumerator.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

/*
 * @(#)IteratorEnumerator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package ch.randelshofer.util;


import org.jhotdraw.annotation.Nonnull;

import java.util.Iterator;

/**
 * Enumerator wrapper for Iterator.
 *
 * @author Werner Randelshofer
 */
public class IteratorEnumerator<E> implements Enumerator<E> {
    @Nonnull
    private final Iterator<? extends E> iterator;

    private E current;

    public IteratorEnumerator(final @Nonnull Iterator<? extends E> iterator) {
        this.iterator = iterator;
    }


    @Override
    public boolean moveNext() {
        if (iterator.hasNext()) {
            current = iterator.next();
            return true;
        }
        return false;
    }

    @Override
    public E current() {
        return current;
    }
}

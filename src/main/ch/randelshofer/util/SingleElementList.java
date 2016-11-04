/**
 * @(#)SingleElementList.java  1.0  Jan 2, 2008
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.util;

import java.util.*;

/**
 * SingleElementList.
 *
 * @author Werner Randelshofer
 * @version 1.0 Jan 2, 2008 Created.
 */
public class SingleElementList<T> extends AbstractList<T> {
    private final T item;

    public SingleElementList(T item) {
        this.item = item;
    }
    
    @Override
    public T get(int index) {
        if (index!=0)throw new ArrayIndexOutOfBoundsException(index);
        return item;
    }

    @Override
    public int size() {
        return 1;
    }

}

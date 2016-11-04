/**
 * @(#)RepeatedList.java  2.0  2012-02-08
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.util;

import java.util.*;

/**
 * RepeatedList.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 Jan 2, 2008 Created.
 */
public class RepeatedList<T> extends AbstractList<T> {
    private List<T> list;
    private int repeatCount;
    
    public RepeatedList(List<T> list, int repeatCount) {
        this.list = list;
        this.repeatCount = repeatCount;
    }

    @Override
    public int size() {
        return list.size() * repeatCount;
    }

    @Override
    public T get(int index) {
        return list.get(index % list.size());
    }
}

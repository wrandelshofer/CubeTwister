/**
 * @(#)ListOfLists.java  2.0  2012-02-08
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.util;

import java.util.*;

/**
 * ListOfLists.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.0 Jan 2, 2008 Created.
 */
public class ListOfLists<T> extends AbstractList<T> {

    private ArrayList<List<T>> lists;
    private int size;

    public ListOfLists() {
        lists = new ArrayList<List<T>>();
    }

    public void addList(List<T> l) {
        lists.add(l);
        invalidate();
    }

    public void invalidate() {
        size = -1;
    }

    @Override
    public int size() {
        if (size == -1) {
            size = 0;
            for (int i = 0; i < lists.size(); i++) {
                List<T> l = lists.get(i);
                size += l.size();
            }
        }
        return size;
    }
    @Override
    public T get(int index) {
        int offset = 0;
        for (int i = 0; i < lists.size(); i++) {
            List<T> l = lists.get(i);
            if (offset <= index && index < l.size() + offset) {
                return l.get(index - offset);
            }
            offset += l.size();
        }
        throw new IllegalArgumentException(index+" illegal index for size="+size());
    }
}

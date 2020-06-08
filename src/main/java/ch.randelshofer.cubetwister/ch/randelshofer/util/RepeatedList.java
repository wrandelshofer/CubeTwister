/*
 * @(#)RepeatedList.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util;

import java.util.AbstractList;
import java.util.List;

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

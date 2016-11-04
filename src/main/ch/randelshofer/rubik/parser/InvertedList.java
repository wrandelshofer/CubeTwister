/**
 * @(#)InvertedList.java  1.0  Jan 2, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.rubik.parser;

import java.util.AbstractList;
import java.util.List;

/**
 * InvertedList.
 *
 * @author Werner Randelshofer
 * @version 1.0 Jan 2, 2008 Created.
 */
public class InvertedList extends AbstractList<Node> {
    private List<Node> list;
    
    public InvertedList(List<Node> list) {
        this.list = list;
    }

    public Node get(int index) {
        Node node = list.get(list.size() - 1 - index);
        node = node.clone();
        node.inverse();
        return node;
    }

    public int size() {
        return list.size();
    }

}

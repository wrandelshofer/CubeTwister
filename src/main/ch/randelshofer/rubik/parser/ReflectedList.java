/**
 * @(#)ReflectedList.java  1.1  2009-01-22
 *
 * Copyright (c) 2008-2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.rubik.parser;

import java.util.AbstractList;
import java.util.List;

/**
 * ReflectedList.
 *
 * @author Werner Randelshofer
 * @version 1.1 2009-01-22 Fixed Node.reflect() method.
 * <br>1.0 Jan 2, 2008 Created.
 */
public class ReflectedList extends AbstractList<Node> {
    private final List<Node> list;
    
    public ReflectedList(List<Node> list) {
        this.list = list;
    }

    @Override
    public Node get(int index) {
        Node node = list.get(index);
        node = node.clone();
//System.out.println("REFLECTEDLIST ....... "+node);
        node.reflect();
//System.out.println("REFLECTEDLIST reflect "+node);
        return node;
    }

    public int size() {
        return list.size();
    }

}

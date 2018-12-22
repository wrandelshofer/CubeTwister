/**
 * @(#)RotatedList.java 1.0 Jan 2, 2008
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import java.util.AbstractList;
import java.util.List;

/**
 * RotatedList.
 *
 * @author Werner Randelshofer  @version $Id$
 */
public class RotatedList extends AbstractList<Node> {

    private List<Node> list;
    private List<Node> rotators;

    public RotatedList(List<Node> list, List<Node> rotators) {
        this.list = list;
        this.rotators = rotators;
    }

    @Override
    public Node get(int index) {
        Node node = list.get(index);
        node = node.clone();
        for (int j = rotators.size() - 1; j > -1; j--) {
            MoveNode rotate = (MoveNode) rotators.get(j);
            node.transform(rotate, true);
        }
        return node;
    }

    @Override
    public int size() {
        return list.size();
    }

}

/*
 * @(#)XMLPreorderIterator.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.xml;

import nanoxml.XMLElement;
import org.jhotdraw.annotation.Nonnull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;

/**
 * XMLPreorderIterator.
 *
 * @author Werner Randelshofer
 */
public class XMLPreorderIterator implements Iterator<XMLElement> {
    private final Deque<Iterator<XMLElement>> stack;

    /**
     * Creates a new instance.
     */
    public XMLPreorderIterator(XMLElement rootNode) {
        var v = new ArrayList<XMLElement>(1);
        v.add(rootNode);
        stack = new ArrayDeque<>();
        stack.add(v.iterator());
    }

    public boolean hasNext() {
        return (!stack.isEmpty() &&
                stack.getLast().hasNext());
    }

    @Nonnull
    public XMLElement next() {
        var iter = stack.getLast();
        var node = iter.next();
        var children = node.getChildren().iterator();
        if (!iter.hasNext()) {
            stack.removeLast();
        }
        if (children.hasNext()) {
            stack.add(children);
        }
        return node;
	}

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

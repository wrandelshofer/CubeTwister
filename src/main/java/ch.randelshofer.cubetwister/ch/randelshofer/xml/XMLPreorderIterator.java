/* @(#)XMLPreorderIterator.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.xml;

import nanoxml.XMLElement;
import org.jhotdraw.annotation.Nonnull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Vector;

/**
 * XMLPreorderIterator.
 * 
 * @author Werner Randelshofer
 */
public class XMLPreorderIterator implements Iterator {
	private Deque<Iterator> stack;
    
    /** Creates a new instance. */
    public XMLPreorderIterator(XMLElement rootNode) {
	    Vector<XMLElement> v = new Vector<XMLElement>(1);
        v.addElement(rootNode);
        stack = new ArrayDeque<Iterator>();
        stack.add(v.iterator());
    }

    public boolean hasNext() {
        return (!stack.isEmpty() &&
                stack.getLast().hasNext());
    }

    @Nonnull
    public Object next() {
        Iterator enu = stack.getLast();
        XMLElement node = (XMLElement) enu.next();
        Iterator children = node.getChildren().iterator();

        if (!enu.hasNext()) {
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

/* @(#)RepetitionNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.util.EmptyIterator;
import ch.randelshofer.util.RepeatedList;
import ch.randelshofer.util.ReverseListIterator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * RepetitionNode.
 *
 * @author werni
 * @version $Id$
 * result is an illegal move.
 * <br>5.1 2010-02-27 Special treatment of a single MoveNode child in
 * method toResolvedList.
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.0.2 2003-06-20 WriteTokens should catch IOException when
 * attempting to write brackets in a notation that does not support
 * repetitions.
 * <br>1.0.1 2002-12-30 Bug in inner class ResolvedEnumeration fixed.
 * <br>1.0 2002-07-24
 */
public class RepetitionNode extends Node {
    private final static long serialVersionUID = 1L;

    int repeatCount = 1;

    /**
     * Creates new RepetitionNode
     */
    public RepetitionNode(int layerCount) {
        this(layerCount, -1, -1);
    }

    public RepetitionNode(int layerCount, int startpos, int endpos) {
        super(Symbol.REPETITION, layerCount, startpos, endpos);
    }

    public void setRepeatCount(int r) {
        repeatCount = r;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    @Override
    public void applyTo(Cube cube, boolean inverse) {
        for (int i = 0; i < repeatCount; i++) {
            super.applyTo(cube, inverse);
        }
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        return new ResolvedIterator(this, inverse, repeatCount);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Iterator<Node> reversedChildIterator() {
        return (children == null)
                ? EmptyIterator.getInstance()
                : new ReverseListIterator(children);
    }

    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String, MacroNode> macroMap)
            throws IOException {
        Syntax pos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.REPETITION) : null;
        if (pos == null) {
            for (int i = 0; i < repeatCount; i++) {
                if (i > 0) {
                    w.write(' ');
                }
                super.writeTokens(w, p, macroMap);
            }
        } else if (pos == Syntax.PREFIX) {
            p.writeToken(w, Symbol.REPETITION_BEGIN);
            w.write(Integer.toString(repeatCount));
            p.writeToken(w, Symbol.REPETITION_END);
            super.writeTokens(w, p, macroMap);
        } else if (pos == Syntax.SUFFIX) {
            super.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.REPETITION_BEGIN);
            w.write(Integer.toString(repeatCount));
            p.writeToken(w, Symbol.REPETITION_END);
        }
    }

    private static class ResolvedIterator
            implements Iterator<Node> {

        private final RepetitionNode root;
        private Iterator<Node> children;
        private Iterator<Node> subtree;
        private boolean inverse;
        private int repeatCount;

        public ResolvedIterator(RepetitionNode root, boolean inverse, int repeat) {
            this.root = root;
            this.inverse = inverse;
            this.repeatCount = (root.getChildCount() == 0) ? 0 : repeat;
            children = (inverse) ? root.reversedChildIterator() : root.childIterator();
            subtree = Collections.emptyIterator();
        }

        @Override
        public boolean hasNext() {
            // Assuming that each children element has at least one element
            return subtree.hasNext() || children.hasNext() || repeatCount > 1;
        }

        @Override
        public Node next() {
            Node retval;

            if (subtree.hasNext()) {
                retval = subtree.next();
            } else if (children.hasNext()) {
                subtree = children.next().resolvedIterator(inverse);
                retval = subtree.next();
            } else if (this.repeatCount > 1) {
                this.repeatCount--;
                children = (inverse) ? root.reversedChildIterator() : root.childIterator();
                subtree = children.next().resolvedIterator(inverse);
                retval = subtree.next();
            } else {
                throw new NoSuchElementException();
            }
            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("RepetitionNode[");
        b.append(repeatCount).append('*');

        for (Node n : getChildren()) {
            b.append(n.toString());
        }
        b.append("]");
        return b.toString();
    }
}

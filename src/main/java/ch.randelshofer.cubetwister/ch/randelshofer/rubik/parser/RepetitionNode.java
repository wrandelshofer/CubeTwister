/* @(#)RepetitionNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.util.EmptyIterator;
import ch.randelshofer.util.ReverseListIterator;
import org.jhotdraw.annotation.Nonnull;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A RepetitionNode holds one child A and a repeat count.
 * The side effect of a RepetitionNode on a cube is
 * repeat count times A.
 *
 * @author Werner Randelshofer
 */
public class RepetitionNode extends Node {
    private final static long serialVersionUID = 1L;

    int repeatCount = 1;

    /**
     * Creates new RepetitionNode
     */
    public RepetitionNode() {
        this(-1, -1);
    }

    public RepetitionNode(int startpos, int endpos) {
        super(startpos, endpos);
    }

    public void setRepeatCount(int r) {
        repeatCount = r;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Nonnull
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        return new ResolvedIterator(this, inverse, repeatCount);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Iterator<Node> reversedChildIterator() {
        return (children == null)
                ? EmptyIterator.getInstance()
                : new ReverseListIterator(children);
    }

    @Override
    public void writeTokens(Writer w, @Nonnull Notation p, Map<String, MacroNode> macroMap)
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

        @Nonnull
        private final RepetitionNode root;
        private Iterator<Node> children;
        private Iterator<Node> subtree;
        private boolean inverse;
        private int repeatCount;

        public ResolvedIterator(@Nonnull RepetitionNode root, boolean inverse, int repeat) {
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

    @Nonnull
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getStartPosition());
        b.append("..");
        b.append(getEndPosition());
        b.append(getClass().getSimpleName());
        b.append("{");
        b.append(' ');
        b.append(repeatCount);
        b.append(",");
        for (Node n : getChildren()) {
            b.append(' ');
            b.append(n.toString());
        }
        b.append(' ');
        b.append("}");
        return b.toString();
    }
}

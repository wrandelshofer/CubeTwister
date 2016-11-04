/*
 * @(#)RepetitionNode.java  5.1.1  2012-02-08
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.util.*;
import java.util.*;
import java.io.*;

/**
 * RepetitionNode.
 *
 * @author  werni
 * @version 5.1.1 Repetition node must not resolve to a single MoveNode if
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

    /** Creates new RepetitionNode */
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
     * Gets the face turn count of the subtree starting
     * at this node.
     */
    @Override
    public int getFaceTurnCount() {
        //return super.getFaceTurnCount() * repeatCount;
        int count = 0;
        for (Node n : toResolvedList()) {
            count += n.getFaceTurnCount();
        }
        return count;
    }

    /**
     * Gets the layer turn count of the subtree starting
     * at this node.
     */
    @Override
    public int getLayerTurnCount() {
        // return super.getLayerTurnCount() * repeatCount;
        int count = 0;
        for (Node n : toResolvedList()) {
            count += n.getLayerTurnCount();
        }
        return count;
    }

    /**
     * Gets the block turn count of the subtree starting
     * at this node.
     */
    @Override
    public int getBlockTurnCount() {
        // return super.getBlockTurnCount() * repeatCount;
        int count = 0;
        for (Node n : toResolvedList()) {
            count += n.getBlockTurnCount();
        }
        return count;
    }

    /**
     * Gets the quarter turn count of the subtree starting
     * at this node.
     */
    @Override
    public int getQuarterTurnCount() {
        ///return super.getQuarterTurnCount() * repeatCount;
        int count = 0;
        for (Node n : toResolvedList()) {
            count += n.getQuarterTurnCount();
        }
        return count;
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
    @SuppressWarnings({"rawtypes","unchecked"})
    public Iterator<Node> reversedChildIterator() {
        return (children == null)
                ?  EmptyIterator.getInstance()
                :  new ReverseListIterator(children);
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

        protected RepetitionNode root;
        protected Iterator<Node> children;
        protected Iterator<Node> subtree;
        boolean inverse;
        int repeatCount;

        public ResolvedIterator(RepetitionNode rootNode, boolean inverse, int repeat) {
            root = rootNode;
            this.inverse = inverse;
            this.repeatCount = (rootNode.getChildCount() == 0) ? 0 : repeat;
            children = (inverse) ? root.reversedChildIterator() : root.childIterator();
            subtree = new SingletonIterator<Node>(root.clone());
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
    public List<Node> toResolvedList() {
        if (getChildCount() == 1 && (getChildAt(0) instanceof MoveNode) //
                /*&& Math.abs(((MoveNode) getChildAt(0)).getAngle() * repeatCount) <= 2*/) {
            // Our only child is a +/-90° MoveNode:
            //      We add a +/-180° MoveNode to the list which includes the
            //      start and end position of the repetitoro.
            MoveNode moveNode = (MoveNode) ((MoveNode) getChildAt(0)).clone();
            moveNode.setAngle((repeatCount * moveNode.getAngle())%4);
            moveNode.setStartPosition(Math.min(getStartPosition(), moveNode.getStartPosition()));
            moveNode.setEndPosition(Math.max(getEndPosition(), moveNode.getEndPosition()));
            LinkedList<Node> l = new LinkedList<Node>();
            l.add(moveNode);
            return l;
        } else {
            // We have more than one child or our only child is not a MoveNode,
            //  or it is already a double turn node:
            return new RepeatedList<Node>(super.toResolvedList(), repeatCount);
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

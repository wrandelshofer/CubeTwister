/*
 * @(#)InversionNode.java  5.1  2010-02-27
 *
 * Copyright (c) 2001-2010 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import java.util.*;
import java.io.*;

/**
 * An InversionNode holds one child A.
 * The side effect of an inversion node is A'.
 *
 * @author Werner Randelshofer
 * @version 5.1 2010-02-27 Special treatment of a single MoveNode child in
 * method toResolvedList.
 * end position of a single child Move node.
 * <br>5.0.1 2007-07-19 Implemented method applyTo.
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.1 20004-03-28 Two nested inversions cancel each other out.
 * <br>1.0 2001-07-25
 * @see ScriptParser
 */
public class InversionNode extends Node {
    private final static long serialVersionUID = 1L;

    public InversionNode(int layerCount) {
        this(layerCount, -1, -1);
    }

    public InversionNode(int layerCount, int startpos, int endpos) {
        super(Symbol.INVERSION, layerCount, startpos, endpos);
    }

    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    @Override
    public void applyTo(Cube cube, boolean inverse) {
        super.applyTo(cube, !inverse);
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        /*
        if (getChildCount() == 1) {
        // If we have only one child, it looks better, if
        // the selection includes the invertor
        Node child = (Node) getChildAt(0).clone();
        child.setStartPosition(this.getStartPosition());
        child.setEndPosition(this.getEndPosition());
        child.inverse();
        Vector v = new Vector();
        v.addElement(child);
        return v.elements();
        } else {
        return super.resolvedEnumeration(!inverse);
        }*/
        return super.resolvedIterator(!inverse);
    }

    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String, MacroNode> macroMap)
            throws IOException {
        if (getChildCount() == 1 && (getChildAt(0) instanceof InversionNode)) {
            // Short cut: If two inversions are nested, they cancel each other out.
            // We print the children of the inner inversion without having to invert
            // them.
            InversionNode nestedInversion = (InversionNode) getChildAt(0);
            for (Enumeration<Node> enumer = nestedInversion.children(); enumer.hasMoreElements();) {
                ((SequenceNode) enumer.nextElement()).writeTokens(w, p, macroMap);
                if (enumer.hasMoreElements()) {
                    p.writeToken(w, Symbol.DELIMITER);
                    w.write(' ');
                }
            }



        } else if (getChildCount() == 1 && (getChildAt(0) instanceof MoveNode)) {
            // Short cut: If the inversion is a parent of a single move node, we
            // print the inverse of the move node.
            InversionNode inverted = (InversionNode) cloneSubtree();
            for (Iterator<Node> i = inverted.reversedChildIterator(); i.hasNext();) {
                SequenceNode node = (SequenceNode) i.next();
                node.inverse();
                node.writeTokens(w, p, macroMap);
            }

        } else {
            // No short cut possible: Print the inversion.
            Syntax invertorPos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.INVERSION) : null;

            if (invertorPos == null) {
                InversionNode inverted = (InversionNode) cloneSubtree();
                for (Iterator<Node> i = inverted.reversedChildIterator();i.hasNext();) {
                    SequenceNode node = (SequenceNode) i.next();
                    node.inverse();
                    node.writeTokens(w, p, macroMap);
                }
            } else if (invertorPos == Syntax.PREFIX) {
                p.writeToken(w, Symbol.INVERTOR);
                super.writeTokens(w, p, macroMap);

            } else if (invertorPos == Syntax.SUFFIX) {
                super.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.INVERTOR);
            } else {
                throw new InternalError("Syntax not implemented " + invertorPos);
            }
        }
    }

    @Override
    public List<Node> toResolvedList() {
        if (getChildCount() == 1 && (getChildAt(0) instanceof MoveNode)) {
            // Our only child is a MoveNode:
            //      We add an inverted MoveNode to the list which includes the
            //      start and end position of the invertor.
            MoveNode moveNode = (MoveNode) ((MoveNode) getChildAt(0)).clone();
            moveNode.setAngle(-moveNode.getAngle());
            moveNode.setStartPosition(Math.min(getStartPosition(), moveNode.getStartPosition()));
            moveNode.setEndPosition(Math.max(getEndPosition(), moveNode.getEndPosition()));
            LinkedList<Node> l = new LinkedList<Node>();
            l.add(moveNode);
            return l;
        } else {
            // We have more than one child or our only child is not a MoveNode,
            return new InvertedList(super.toResolvedList());
        }
    }
}

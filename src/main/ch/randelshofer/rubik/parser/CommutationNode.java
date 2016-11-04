/*
 * @(#)CommutationNode.java  5.1  2010-11-11
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.util.*;
import ch.randelshofer.gui.tree.*;
import java.io.*;
import java.util.*;
/**
 * A CommutationNode holds a commutator A and a single child B.
 * The side effect of a commutation node is A B A' B'.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.0 2001-07-25
 * @see ScriptParser
 */
public class CommutationNode extends Node {
    private final static long serialVersionUID = 1L;
    private Node commutator;
    
    public CommutationNode(int layerCount) {
        this(layerCount, new SequenceNode(layerCount), new SequenceNode(layerCount), -1, -1);
    }
    public CommutationNode(int layerCount, int startpos, int endpos) {
        this(layerCount, new SequenceNode(layerCount), new SequenceNode(layerCount), startpos, endpos);
    }
    public CommutationNode(int layerCount, Node commutator, Node commutated, int startpos, int endpos) {
        super(Symbol.COMMUTATION, layerCount, startpos, endpos);
        commutator.removeFromParent();
        commutator.setParent(this);
        this.commutator = commutator;
        add(commutated);
    }
    
    public Node getCommutator() {
        return commutator;
    }
    public void setCommutator(Node newValue) {
        commutator = newValue;
    }
    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    @Override
    public void applyTo(Cube cube, boolean inverse) {
        if (inverse) {
            /*
            super.applyTo(cube, true);
            commutator.applyTo(cube, true);
            super.applyTo(cube, false);
            commutator.applyTo(cube, false);
             */
            super.applyTo(cube, false);
            commutator.applyTo(cube, false);
            super.applyTo(cube, true);
            commutator.applyTo(cube, true);
        } else {
            commutator.applyTo(cube, false);
            super.applyTo(cube, false);
            commutator.applyTo(cube, true);
            super.applyTo(cube, true);
        }
    }
    /** Overwrite start and end positions of this node and
     * the subtree starting at this node.
     */
    public void overwritePositions(int sp, int ep) {
        super.overwritePositions(sp, ep);
        if (commutator != null) {
            for (Enumeration<Node> i = commutator.children(); i.hasMoreElements(); ) {
                Node child = i.nextElement();
                child.overwritePositions(sp, ep);
            }
        }
    }
    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void inverse() {
        Node helper = commutator;
        commutator = new SequenceNode(layerCount);
        
        while (getChildCount() > 0) {
            Node n = getChildAt(0);
            commutator.add(n);
        }
        
        while (helper.getChildCount() > 0) {
            Node n = helper.getChildAt(0);
            add(n);
        }
    }
    /**
     * Reflect the subtree starting at this node.
     */
    @Override
    public void reflect() {
        commutator.reflect();
        super.reflect();
    }
    /**
     * Transformes the subtree starting at this node by
     * the given ScriptParser.symbol constant.
     * Does nothing if the transformation can not be done.
     */
    public void transform(int axis, int layerMask, int angle) {
        commutator.transform(axis, layerMask, angle);
        super.transform(axis, layerMask, angle);
    }
    /**
     * Returns a deep clone of the subtree starting at this node.
     */
    public Node cloneSubtree() {
        CommutationNode that = (CommutationNode) super.cloneSubtree();
        that.commutator = this.commutator.cloneSubtree();
        return that;
    }
    /**
     * Gets the layer turn count of the subtree starting
     * at this node.
     */
    public int getLayerTurnCount() {
        return (super.getLayerTurnCount() + commutator.getLayerTurnCount()) * 2;
    }
    /**
     * Gets the block turn count of the subtree starting
     * at this node.
     */
    public int getBlockTurnCount() {
        return (super.getBlockTurnCount() + commutator.getBlockTurnCount()) * 2;
    }
    /**
     * Gets the face turn count of the subtree starting
     * at this node.
     */
    public int getFaceTurnCount() {
        return (super.getFaceTurnCount() + commutator.getFaceTurnCount()) * 2;
    }
    /**
     * Gets the quarter turn count of the subtree starting
     * at this node.
     */
    public int getQuarterTurnCount() {
        return (super.getQuarterTurnCount() + commutator.getQuarterTurnCount()) * 2;
    }
    
    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (inverse) {
            return new SequenceIterator<Node>(
                    super.resolvedIterator(false),
                    new SequenceIterator<Node>(
                    commutator.resolvedIterator(false),
                    new SequenceIterator<Node>(
                    super.resolvedIterator(true),
                    commutator.resolvedIterator(true)
                    )
                    )
                    );
        } else {
            return new SequenceIterator<Node>(
                    commutator.resolvedIterator(false),
                    new SequenceIterator<Node>(
                    super.resolvedIterator(false),
                    new SequenceIterator<Node>(
                    commutator.resolvedIterator(true),
                    super.resolvedIterator(true)
                    )
                    )
                    );
        }
    }
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        Syntax pos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.COMMUTATION) : null;
        if (pos == null) {
            // Write the commutation as (A B A' B').
            if (p.isSupported(Symbol.GROUPING)) p.writeToken(w, Symbol.GROUPING_BEGIN);
            commutator.writeTokens(w, p, macroMap);
            w.write(' ');
            p.writeToken(w, Symbol.NOP);
            w.write(' ');
            writeChildTokens(w, p, macroMap);
            w.write(' ');
            p.writeToken(w, Symbol.NOP);
            w.write(' ');
            Node inv =  commutator.cloneSubtree();
            inv.inverse();
            inv.writeTokens(w, p, macroMap);
            w.write(' ');
            p.writeToken(w, Symbol.NOP);
            w.write(' ');
            inv = new SequenceNode(layerCount);
            Enumeration<Node> enumer = children();
            while (enumer.hasMoreElements()) {
                //inv.add((Node) enumer.nextElement());
                inv.add( enumer.nextElement().cloneSubtree());
            }
            inv.inverse();
            inv.writeTokens(w, p, macroMap);
            if (p.isSupported(Symbol.GROUPING)) p.writeToken(w, Symbol.GROUPING_END);
        } else if (pos == Syntax.PREFIX) {
            if (commutator.getChildCount() != 0) {
                p.writeToken(w, Symbol.COMMUTATION_BEGIN);
                commutator.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.COMMUTATION_END);
            }
            if (getParent() instanceof StatementNode
                    && getChildCount() == 1
                    && ((getChildAt(0) instanceof GroupingNode)
                    || (getChildAt(0) instanceof GroupingNode)
                    || (getChildAt(0) instanceof PermutationNode)
                    || (getChildAt(0) instanceof MoveNode)
                    )
                    ) {
                super.writeTokens(w, p, macroMap);
            } else {
                p.writeToken(w, Symbol.GROUPING_BEGIN);
                super.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.GROUPING_END);
            }
        } else if (pos == Syntax.SUFFIX) {
            if (getParent() instanceof StatementNode
                    && getChildCount() == 1
                    && ((getChildAt(0) instanceof GroupingNode)
                    || (getChildAt(0) instanceof GroupingNode)
                    || (getChildAt(0) instanceof PermutationNode)
                    || (getChildAt(0) instanceof MoveNode)
                    )
                    ) {
                super.writeTokens(w, p, macroMap);
            } else {
                p.writeToken(w, Symbol.GROUPING_BEGIN);
                super.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.GROUPING_END);
            }
            if (commutator.getChildCount() != 0) {
                p.writeToken(w, Symbol.COMMUTATION_BEGIN);
                commutator.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.COMMUTATION_END);
            }
        } else if (pos == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.COMMUTATION_BEGIN);
            commutator.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.COMMUTATION_DELIMITER);
            super.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.COMMUTATION_END);
        }
    }
    /**
     * Writes only the child token(s) represented by the subtree starting
     * at this node, that is, the commutator is ommitted.
     * The syntax and the string representations
     * of the tokens are provided by the parser.
     *
     * @param w   This is where the tokens are written to.
     * @param p   The parser which provides the tokens.
     * @param macroMap Macros with identifiers of which
     *             macroMap.containsKey(String) returns true
     *             are preserved.
     */
    private void writeChildTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        Cube cube = Cubes.create(layerCount);
        applyTo(cube, false);
        String macroName = p.getEquivalentMacro(cube, macroMap);
        if (macroName != null) {
            w.write(macroName);
        } else {
            Enumeration<Node> enumer = children();
            while (enumer.hasMoreElements()) {
                enumer.nextElement().writeTokens(w, p, macroMap);
                if (enumer.hasMoreElements()) {
                    p.writeToken(w, Symbol.DELIMITER);
                    w.write(' ');
                }
            }
        }
    }
    public List<Node> toResolvedList() {
        ListOfLists<Node> list = new ListOfLists<Node>();
        @SuppressWarnings("unchecked")
        List<Node> a = (commutator == null) ? Collections.EMPTY_LIST : commutator.toResolvedList();
        List<Node> b = super.toResolvedList();
        
        list.addList(a);
        list.addList(b);
        list.addList(new InvertedList(a));
        list.addList(new InvertedList(b));
        return list;
    }
}

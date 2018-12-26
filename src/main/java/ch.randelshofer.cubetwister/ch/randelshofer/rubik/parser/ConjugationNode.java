/* @(#)ConjugationNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.util.ListOfLists;
import ch.randelshofer.util.ReverseListIterator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
/**
 * A ConjugationNode holds a conjugator A and a single child B.
 * The side effect of a conjugation node is A B A'.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.1 2002-08-12 Fixed a problem where conjugator (X Y)
 * with child B was resolved into X Y B X' Y' instead of X Y B Y' X'.
 * <br>1.0 2001-07-25
 * @see ScriptParser
 */
public class ConjugationNode extends Node {
    private final static long serialVersionUID = 1L;
    private Node conjugator;
    
    public ConjugationNode(int layerCount) {
        this(layerCount,-1,-1);
    }
    
    public ConjugationNode(int layerCount, int startpos, int endpos) {
        super(Symbol.CONJUGATION, layerCount, startpos, endpos);
        conjugator = new SequenceNode(layerCount);
        conjugator.setParent(this);
    }
    public ConjugationNode(int layerCount, Node conjugator, Node conjugate, int startpos, int endpos) {
        super(Symbol.CONJUGATION, layerCount, startpos, endpos);
        conjugator.removeFromParent();
        conjugator.setParent(this);
        this.conjugator = conjugator;
        add(conjugate);
    }
    /** Overwrite start and end positions of this node and
     * the subtree starting at this node.
     */
    public void overwritePositions(int sp, int ep) {
        super.overwritePositions(sp, ep);
        if (conjugator != null) {
            for (Node child : conjugator.getChildren()) {
                child.overwritePositions(sp, ep);
            }
        }
    }
    
    public Node getConjugator() {
        return conjugator;
    }
    public void setConjugator(Node newValue) {
        conjugator = newValue;
    }
    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    public void applyTo(Cube cube, boolean inverse) {
            conjugator.applyTo(cube, false);
            super.applyTo(cube, inverse);
            conjugator.applyTo(cube, true);
    }
    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void inverse() {
        //conjugator.inverse(); / Conjugator does not have to be inverted
        super.inverse();
    }
    /**
     * Reflect the subtree starting at this node.
     */
    @Override
    public void reflect() {
        conjugator.reflect();
        super.reflect();
    }
    /**
     * Transformes the subtree starting at this node by
     * the given ScriptParser.symbol constant.
     * Does nothing if the transformation can not be done.
     */
    public void transform(int axis, int layerMask, int angle) {
        conjugator.transform(axis, layerMask, angle);
        super.transform(axis, layerMask, angle);
    }
    /**
     * Returns a deep clone of the subtree starting at this node.
     */
    public Node cloneSubtree() {
        ConjugationNode that = (ConjugationNode) super.cloneSubtree();
        that.conjugator = this.conjugator.cloneSubtree();
        return that;
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        ArrayList<Node> rotators = new ArrayList<Node>();
        ArrayList<Node> twisters = new ArrayList<Node>();
        
        // Extract rotators at the beginning of the conjugator
        // if any.
        // Conjugator does not have to be inversed
        for (Node node : conjugator.resolvedIterable(false)) {
            if (node instanceof MoveNode) {
                MoveNode transform = (MoveNode) node;
                if (twisters.size() == 0 && transform.isRotation()) {
                    rotators.add(transform);
                } else {
                    twisters.add(transform);
                }
            } else if (node instanceof PermutationNode) {
                twisters.add(node);
            }
        }
        // Rotate the twist operators of the conjugator.
        if (rotators.size() > 0 && twisters.size() > 0) {
            //for (int i = 0; i < twisters.size(); i++) {
            for (int i = twisters.size() - 1; i > -1; i--) {
                Node rotated = twisters.get(i).clone();
                //for (int j = 0; j < rotators.size(); j++) {
                for (int j = rotators.size() - 1; j > -1; j--) {
                    MoveNode rotate = (MoveNode) rotators.get(j);
                    rotated.transform(rotate, true);
                }
                twisters.set(i, rotated);
            }
        }
        
        // Create a vector holding the resolved operations
        // It starts with the conjugation operators.
        ArrayList<Node> resolved = new ArrayList<Node>();
        resolved.addAll(twisters);
        
        // Now we rotate the children and addElement them to
        // the resolved vector.
        {
            Iterator<Node> i = super.resolvedIterator(inverse);
            i.next(); // skip this node
            while (i.hasNext()) {
                Node rotated = i.next().clone();
                for (int j = rotators.size() - 1; j > -1; j--) {
                    MoveNode rotator = (MoveNode) rotators.get(j);
                    rotated.transform(rotator, true);
                }
                resolved.add(rotated);
            }
        }
        
        // And finally we append the inverse of the
        // conjugator twist operators to the resolved vector.
        for (int i = twisters.size() - 1; i >= 0; i--) {
            Node twist = twisters.get(i).clone();
            twist.inverse();
            resolved.add(twist);
        }
        return resolved.iterator();
    }
    
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        Syntax pos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.CONJUGATION) : null;
        if (pos == null) {
            // Extract rotators at the beginning of the conjugator
            // if any.
            // Break the loop, if other symbols than rotators are encountered.
            boolean isPureRotation = true;
            Vector<Node> rotators = new Vector<Node>();
            Iterator<Node> i = conjugator.resolvedIterator(false);
            while (i.hasNext()) {
                Node node = i.next();
                if (node instanceof MoveNode) {
                    MoveNode transform = (MoveNode) node;
                    if (transform.isRotation()) {
                        rotators.addElement(transform);
                    } else {
                        isPureRotation = false;
                        break;
                    }
                } else if (node instanceof PermutationNode) {
                    isPureRotation = false;
                    break;
                }
            }
            
            // If the conjugator consists of rotators only it can be
            // replaced by the rotated version of the children only
            // if none of the macros must be preserved.
            if (isPureRotation) {
                for (Node node: preorderIterable()) {
                    if (node instanceof MacroNode
                            && macroMap.containsKey(((MacroNode) node).getIdentifier())) {
                        isPureRotation = false;
                        break;
                    }
                }
            }
            
            if (isPureRotation) {
                // If the conjugator consists of rotators only it can be
                // written as rotated children.
                ConjugationNode rotated = (ConjugationNode) cloneSubtree();
                rotated.conjugator.removeAllChildren();
                
                i = new ReverseListIterator<Node>(rotators);
                while (i.hasNext()) {
                    MoveNode rotator = (MoveNode) i.next();
                    rotated.transform(rotator, true);
                }
                i = rotated.childIterator();
                while (i.hasNext()) {
                    i.next().writeTokens(w, p, macroMap);
                    if (i.hasNext()) {
                        p.writeToken(w, Symbol.DELIMITER);
                        w.write(' ');
                    }
                }
            } else {
                // Write the commutation as (A B A').
                if (p.isSupported(Symbol.GROUPING)) p.writeToken(w, Symbol.GROUPING_BEGIN);
                conjugator.writeTokens(w, p, macroMap);
                w.write(' ');
                super.writeTokens(w, p, macroMap);
                w.write(' ');
                Node inverseCommutator = conjugator.cloneSubtree();
                inverseCommutator.inverse();
                inverseCommutator.writeTokens(w, p, macroMap);
                if (p.isSupported(Symbol.GROUPING)) p.writeToken(w, Symbol.GROUPING_END);
            }
        } else if (pos == Syntax.PREFIX) {
            if (conjugator.getChildCount() != 0) {
                p.writeToken(w, Symbol.CONJUGATION_BEGIN);
                conjugator.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.CONJUGATION_END);
            }
            super.writeTokens(w, p, macroMap);
        } else if (pos == Syntax.SUFFIX) {
            super.writeTokens(w, p, macroMap);
            if (conjugator.getChildCount() != 0) {
                p.writeToken(w, Symbol.CONJUGATION_BEGIN);
                conjugator.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.CONJUGATION_END);
            }
        } else if (pos == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.CONJUGATION_BEGIN);
            conjugator.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.CONJUGATION_DELIMITER);
            super.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.CONJUGATION_END);
        }
    }
    public List<Node> toResolvedList() {
        ListOfLists<Node> list = new ListOfLists<Node>();
        @SuppressWarnings("unchecked")
        List<Node> a = (conjugator == null) ? Collections.EMPTY_LIST : conjugator.toResolvedList();
        List<Node> b = super.toResolvedList();
        
        list.addList(a);
        list.addList(b);
        list.addList(new InvertedList(a));
        return list;
    }
}

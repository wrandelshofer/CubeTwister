/*
 * @(#)RotationNode.java  5.0  2005-01-31
 *
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.util.*;
import ch.randelshofer.gui.tree.*;
import java.io.*;
import java.util.*;

/**
 * A RotationNode holds a rotator A and a single child B. The side effect of a
 * rotation node to a Cube is A' B A.
 *
 * @author Werner Randelshofer
 * @version 5.0 2005-01-31 Reworked.
 * <br>1.0 2002-02-24 Created.
 */
public class RotationNode extends SequenceNode {

    private final static long serialVersionUID = 1L;

    private Node rotator;

    public RotationNode(int layerCount) {
        this(layerCount, 0, 0);
    }

    public RotationNode(int layerCount, int startpos, int endpos) {
        super(layerCount, startpos, endpos);
        rotator = new SequenceNode(layerCount);
        rotator.setParent(this);
    }

    public RotationNode(int layerCount, Node rotator, Node rotatee, int startpos, int endpos) {
        super(layerCount, startpos, endpos);
        rotator.removeFromParent();
        rotator.setParent(this);
        this.rotator = rotator;
        add(rotatee);
    }

    public Node getRotator() {
        return rotator;
    }

    public void setRotator(Node newValue) {
        rotator = newValue;
    }

    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    public void applyTo(Cube cube, boolean inverse) {
        rotator.applyTo(cube, true);
        super.applyTo(cube, inverse);
        rotator.applyTo(cube, false);
    }

    /**
     * Overwrite start and end positions of this node and the subtree starting
     * at this node.
     */
    public void overwritePositions(int sp, int ep) {
        super.overwritePositions(sp, ep);
        if (rotator != null) {
            for (Enumeration<Node> i = rotator.children(); i.hasMoreElements();) {
                SequenceNode child = (SequenceNode) i.nextElement();
                child.overwritePositions(sp, ep);
            }
        }
    }

    /**
     * Inverses the subtree starting at this node.
     */
    public void inverse() {
        //rotator.inverse(); / Rotator does not have to be inverted
        super.inverse();
    }

    /**
     * Reflect the subtree starting at this node.
     */
    @Override
    public void reflect() {
        rotator.reflect();
        super.reflect();
    }

    /**
     * Transformes the subtree starting at this node by the given
     * ScriptParser.symbol constant. Does nothing if the transformation can not
     * be done.
     */
    public void transform(int axis, int layerMask, int angle) {
        rotator.transform(axis, layerMask, angle);
        super.transform(axis, layerMask, angle);
    }

    /**
     * Returns a deep clone of the subtree starting at this node.
     */
    public Node cloneSubtree() {
        RotationNode that = (RotationNode) super.cloneSubtree();
        that.rotator = this.rotator.cloneSubtree();
        return that;
    }

    /**
     * Gets the layer turn count of the subtree starting at this node.
     */
    public int getLayerTurnCount() {
        return super.getLayerTurnCount() + rotator.getLayerTurnCount() * 2;
    }

    /**
     * Gets the block turn count of the subtree starting at this node.
     */
    public int getBlockTurnCount() {
        return super.getBlockTurnCount() + rotator.getBlockTurnCount() * 2;
    }

    /**
     * Gets the face turn count of the subtree starting at this node.
     */
    public int getFaceTurnCount() {
        return super.getFaceTurnCount() + rotator.getFaceTurnCount() * 2;
    }

    /**
     * Gets the quarter turn count of the subtree starting at this node.
     */
    public int getQuarterTurnCount() {
        return super.getQuarterTurnCount() + rotator.getQuarterTurnCount() * 2;
    }

    /**
     * Enumerate this symbol and all of its children. Special operators (i. e.
     * repeat and inverse) are resolved before the children are returned.
     */
    public Iterator<Node> resolvedIterator(boolean inverse) {
        ArrayList<Node> rotators = new ArrayList<Node>();
        ArrayList<Node> twisters = new ArrayList<Node>();

        // Extract rotators at the end of the rotator
        // if any.
        Iterator<Node> enumeration = rotator.resolvedIterator(false);
        while (enumeration.hasNext()) {
            Node node = enumeration.next();
            if (node instanceof MoveNode) {
                MoveNode transform = (MoveNode) node;
                if (transform.isRotation()) {
                    rotators.add(transform);
                } else {
                    twisters.addAll(rotators);
                    rotators.clear();
                    twisters.add(transform);
                }
            } else if (node instanceof PermutationNode) {
                twisters.add(node);
            }
        }
        // Rotate the twist operators of the rotator.
        if (rotators.size() > 0 && twisters.size() > 0) {
            //for (int i = 0; i < twisters.size(); i++) {
            for (int i = twisters.size() - 1; i > -1; i--) {
                SequenceNode rotated = (SequenceNode) ((SequenceNode) twisters.get(i)).clone();
                //for (int j = 0; j < rotators.size(); j++) {
                for (int j = rotators.size() - 1; j > -1; j--) {
                    MoveNode rotate = (MoveNode) rotators.get(j);
                    rotated.transform(rotate, true);
                }
                twisters.set(i, rotated);
            }
        }

        // Create a vector holding the resolved operations
        // It starts with the inverse of the rotation operators.
        ArrayList<Node> resolved = new ArrayList<Node>();
        // And finally we append the inverse of the
        // rotator twist operators to the resolved vector.
        for (int i = twisters.size() - 1; i >= 0; i--) {
            Node twist = twisters.get(i);
            twist.inverse();
            resolved.add(twist);
        }

        // Now we rotate the children and addElement them to
        // the resolved vector.
        enumeration = super.resolvedIterator(inverse);
        enumeration.next(); // skip this node
        while (enumeration.hasNext()) {
            Node rotated = enumeration.next().clone();
            for (int j = 0; j < rotators.size(); j++) {
                MoveNode rotator_ = (MoveNode) rotators.get(j);
                rotated.transform((MoveNode) rotator, false);
            }
            resolved.add(rotated);
        }

        // And finally we append the rotator twist operators to the resolved vector.
        resolved.addAll(twisters);

        return resolved.iterator();
    }

    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String, MacroNode> macroMap)
            throws IOException {
        Syntax pos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.ROTATION) : null;
        if (pos == null) {
            // Extract rotators at the beginning of the rotator
            // if any.
            // Break the loop, if other symbols than rotators are encountered.
            boolean isPureRotation = true;
            Vector<Node> rotators = new Vector<Node>();
            for (Iterator<Node> i = rotator.resolvedIterator(false);i.hasNext();) {
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

            // If the rotator consists of rotators only it can be
            // replaced by the rotated version of the children only
            // if none of the macros must be preserved.
            if (isPureRotation) {
                for (Node node : preorderIterable()) {
                    if (node instanceof MacroNode
                            && macroMap.containsKey(((MacroNode) node).getIdentifier())) {
                        isPureRotation = false;
                        break;
                    }
                }
            }

            if (isPureRotation) {
                // If the rotator consists of rotators only it can be
                // written as rotated children.
                RotationNode rotated = (RotationNode) cloneSubtree();
                rotated.rotator.removeAllChildren();

for (Iterator<Node> i= rotators.iterator();i.hasNext();) {
                    MoveNode rotator_ = (MoveNode) i.next();
                    rotated.transform(rotator_, false);
                }
                for (Iterator<Node> i = rotated.childIterator();i.hasNext();) {
                    ((SequenceNode) i.next()).writeTokens(w, p, macroMap);
                    if (i.hasNext()) {
                        p.writeToken(w, Symbol.DELIMITER);
                        w.write(' ');
                    }
                }
            } else {
                // Write the rotation as (A' B A).
                if (p.isSupported(Symbol.GROUPING)) {
                    p.writeToken(w, Symbol.GROUPING_BEGIN);
                }
                Node inverseRotator = rotator.cloneSubtree();
                inverseRotator.inverse();
                inverseRotator.writeTokens(w, p, macroMap);
                w.write(' ');
                super.writeTokens(w, p, macroMap);
                w.write(' ');
                rotator.writeTokens(w, p, macroMap);
                if (p.isSupported(Symbol.GROUPING)) {
                    p.writeToken(w, Symbol.GROUPING_END);
                }
            }
        } else if (pos == Syntax.PREFIX) {
            if (rotator.getChildCount() != 0) {
                p.writeToken(w, Symbol.ROTATION_BEGIN);
                rotator.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.ROTATION_END);
            }
            super.writeTokens(w, p, macroMap);
        } else if (pos == Syntax.SUFFIX) {
            super.writeTokens(w, p, macroMap);
            if (rotator.getChildCount() != 0) {
                p.writeToken(w, Symbol.ROTATION_BEGIN);
                rotator.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.ROTATION_END);
            }
        } else if (pos == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.ROTATION_BEGIN);
            rotator.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.ROTATION_DELIMITER);
            super.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.ROTATION_END);
        }
    }

    public List<Node> toResolvedList() {
        boolean isPureRotation = true;
        Vector<Node> rotators = new Vector<Node>();
        for (Iterator<Node> i = rotator.resolvedIterator(false);i.hasNext();) {
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
        if (isPureRotation) {
            @SuppressWarnings("unchecked")
            List<Node> list = new RotatedList((children == null) ? Collections.EMPTY_LIST : children, rotators);
            return list;
        } else {
            ListOfLists<Node> list = new ListOfLists<Node>();
            @SuppressWarnings("unchecked")
            List<Node> a = (rotator == null) ? Collections.EMPTY_LIST : rotator.toResolvedList();
            @SuppressWarnings("unchecked")
            List<Node> b = (children == null) ? Collections.EMPTY_LIST : children;

            list.addList(new InvertedList(a));
            list.addList(b);
            list.addList(a);
            return list;
        }
    }
}

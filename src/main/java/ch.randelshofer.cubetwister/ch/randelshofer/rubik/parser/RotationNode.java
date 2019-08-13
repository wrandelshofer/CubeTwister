/* @(#)RotationNode.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * A RotationNode holds a rotator A and a single child B. The side effect of a
 * rotation node to a Cube is A' B A.
 *
 * @author Werner Randelshofer
 */
public class RotationNode extends BinaryNode {

    private final static long serialVersionUID = 1L;

    public RotationNode() {
        this(0, 0);
    }

    public RotationNode(int startpos, int endpos) {
        super(startpos, endpos);
        operand1 = new SequenceNode();
        operand1.setParent(this);
    }

    public RotationNode(Node operand1, Node rotatee, int startpos, int endpos) {
        super(startpos, endpos);
        operand1.removeFromParent();
        operand1.setParent(this);
        this.operand1 = operand1;
        add(rotatee);
    }

     /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube    A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    public void applyTo(Cube cube, boolean inverse) {
        operand1.applyTo(cube, true);
        super.applyTo(cube, inverse);
        operand1.applyTo(cube, false);
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
        operand1.reflect();
        super.reflect();
    }

    /**
     * Transformes the subtree starting at this node by the given
     * ScriptParser.symbol constant. Does nothing if the transformation can not
     * be done.
     */
    public void transform(int axis, int layerMask, int angle) {
        operand1.transform(axis, layerMask, angle);
        super.transform(axis, layerMask, angle);
    }

    /**
     * Returns a deep clone of the subtree starting at this node.
     */
    public Node cloneSubtree() {
        RotationNode that = (RotationNode) super.cloneSubtree();
        that.operand1 = this.operand1.cloneSubtree();
        return that;
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
        Iterator<Node> enumeration = operand1.resolvedIterator(false);
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
                rotated.transform((MoveNode) operand1, false);
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
            for (Iterator<Node> i = operand1.resolvedIterator(false); i.hasNext(); ) {
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
                rotated.operand1.removeAllChildren();

                for (Iterator<Node> i = rotators.iterator(); i.hasNext(); ) {
                    MoveNode rotator_ = (MoveNode) i.next();
                    rotated.transform(rotator_, false);
                }
                for (Iterator<Node> i = rotated.childIterator(); i.hasNext(); ) {
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
                Node inverseRotator = operand1.cloneSubtree();
                inverseRotator.inverse();
                inverseRotator.writeTokens(w, p, macroMap);
                w.write(' ');
                super.writeTokens(w, p, macroMap);
                w.write(' ');
                operand1.writeTokens(w, p, macroMap);
                if (p.isSupported(Symbol.GROUPING)) {
                    p.writeToken(w, Symbol.GROUPING_END);
                }
            }
        } else if (pos == Syntax.PREFIX) {
            if (operand1.getChildCount() != 0) {
                p.writeToken(w, Symbol.ROTATION_BEGIN);
                operand1.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.ROTATION_END);
            }
            super.writeTokens(w, p, macroMap);
        } else if (pos == Syntax.SUFFIX) {
            super.writeTokens(w, p, macroMap);
            if (operand1.getChildCount() != 0) {
                p.writeToken(w, Symbol.ROTATION_BEGIN);
                operand1.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.ROTATION_END);
            }
        } else if (pos == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.ROTATION_BEGIN);
            operand1.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.ROTATION_OPERATOR);
            super.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.ROTATION_END);
        }
    }
}

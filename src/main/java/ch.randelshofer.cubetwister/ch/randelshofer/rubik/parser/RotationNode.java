/* @(#)RotationNode.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.notation.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
    }

    @Override
    protected Symbol getSymbol() {
        return Symbol.ROTATION;
    }

    public RotationNode(Node operand1, Node rotatee, int startpos, int endpos) {
        super(startpos, endpos);
        operand1.removeFromParent();
        operand1.setParent(this);
        add(rotatee);
    }

     /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube    A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    public void applyTo(Cube cube, boolean inverse) {
        if (getChildCount() == 2) {
            getChildAt(0).applyTo(cube, true);
            getChildAt(1).applyTo(cube, false);
            getChildAt(0).applyTo(cube, false);
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
     * Enumerate this symbol and all of its children. Special operators (i. e.
     * repeat and inverse) are resolved before the children are returned.
     */
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (getChildCount() != 2) {
            return Collections.emptyIterator();
        }
        Node operand1 = getChildAt(0);
        Node operand2 = getChildAt(1);

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
        enumeration = operand2.resolvedIterator(inverse);
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
}

/* @(#)CommutationNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.util.SequenceIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A CommutationNode holds a child A and a child B.
 * The side effect of a commutation node is A B A' B'.
 *
 * @author Werner Randelshofer
 */
public class CommutationNode extends BinaryNode {
    private final static long serialVersionUID = 1L;

    public CommutationNode() {
        super(-1, -1);
    }

    @Override
    protected Symbol getSymbol() {
        return Symbol.COMMUTATION;
    }

    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube    A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    @Override
    public void applyTo(Cube cube, boolean inverse) {
        if (getChildCount() != 2) {
            return;
        }
        final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);
        if (inverse) {
            operand2.applyTo(cube, false);
            operand1.applyTo(cube, false);
            operand2.applyTo(cube, true);
            operand1.applyTo(cube, true);
        } else {
            operand1.applyTo(cube, false);
            operand2.applyTo(cube, false);
            operand1.applyTo(cube, true);
            operand2.applyTo(cube, true);
        }
    }

    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void inverse() {
        if (getChildCount() != 2) {
            return;
        }
        final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);
        removeAllChildren();
        add(operand2);
        add(operand1);
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (getChildCount() != 2) {
            return Collections.emptyIterator();
        }
        final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);
        return new SequenceIterator<Node>(
                List.of(
                        operand1.resolvedIterator(inverse),
                        operand2.resolvedIterator(inverse),
                        operand1.resolvedIterator(!inverse),
                        operand2.resolvedIterator(!inverse)
                )
        );
    }
}

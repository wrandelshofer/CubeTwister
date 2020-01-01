/* @(#)ConjugationNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.util.SequenceIterator;
import org.jhotdraw.annotation.Nonnull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A ConjugationNode holds a child A and a child B.
 * The side effect of a conjugation node is A B A'.
 *
 * @author Werner Randelshofer
 */
public class ConjugationNode extends BinaryNode {
    private final static long serialVersionUID = 1L;

    public ConjugationNode() {
        super(-1, -1);
    }

    @Nonnull
    @Override
    protected Symbol getSymbol() {
        return Symbol.CONJUGATION;
    }


    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube    A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    public void applyTo(Cube cube, boolean inverse) {
        if (getChildCount() != 2) {
            return;
        }
        final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);
        operand1.applyTo(cube, false);
        operand2.applyTo(cube, inverse);
        operand1.applyTo(cube, true);
    }

    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void inverse() {
        if (getChildCount() != 2) {
            return;
        }
        //final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);
        //operand1.inverse(); // Conjugator does not have to be inverted
        operand2.inverse();
    }

    @Nonnull
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (getChildCount() != 2) {
            return Collections.emptyIterator();
        }
        final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);

        return new SequenceIterator<>(
                List.of(
                        operand1.resolvedIterator(inverse),
                        operand2.resolvedIterator(inverse),
                        operand1.resolvedIterator(!inverse)
                )
        );
    }
}

/* @(#)ConjugationNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

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

    @Nonnull
    @Override
    protected Symbol getSymbol() {
        return Symbol.CONJUGATION;
    }

    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void invert() {
        if (getChildCount() != 2) {
            return;
        }
        //The conjugator does not have to be inverted.
        var b = getChildAt(1);
        b.invert();
    }

    @Nonnull
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (getChildCount() != 2) {
            return Collections.emptyIterator();
        }
        var a = getChildAt(0);
        var b = getChildAt(1);

        return new SequenceIterator<>(
                List.of(
                        a.resolvedIterator(false),
                        b.resolvedIterator(inverse),
                        a.resolvedIterator(true)
                )
        );
    }
}

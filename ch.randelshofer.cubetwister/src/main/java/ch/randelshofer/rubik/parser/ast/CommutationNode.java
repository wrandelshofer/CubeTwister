/*
 * @(#)CommutationNode.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser.ast;

import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.util.SequenceIterator;
import org.jhotdraw.annotation.Nonnull;

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


    @Nonnull
    @Override
    protected Symbol getSymbol() {
        return Symbol.COMMUTATION;
    }

    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void invert() {
        if (getChildCount() != 2) {
            return;
        }
        var a = getChildAt(0);
        var b = getChildAt(1);
        removeAllChildren();
        add(b);
        add(a);
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Nonnull
    public Iterator<Node> resolvedIterator(boolean inverse) {
        if (getChildCount() != 2) {
            return Collections.emptyIterator();
        }
        final Node a = getChildAt(0);
        final Node b = getChildAt(1);
        return inverse
                ? new SequenceIterator<>(
                List.of(
                        b.resolvedIterator(false),
                        a.resolvedIterator(false),
                        b.resolvedIterator(true),
                        a.resolvedIterator(true)
                )
        )
                : new SequenceIterator<>(
                List.of(
                        a.resolvedIterator(false),
                        b.resolvedIterator(false),
                        a.resolvedIterator(true),
                        b.resolvedIterator(true)
                )
        );
    }
}

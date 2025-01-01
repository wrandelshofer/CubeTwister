/*
 * @(#)RotationNode.java
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
 * A RotationNode holds a child A and a child B. The side effect of a
 * rotation node to a Cube is A' B A.
 *
 * @author Werner Randelshofer
 */
public class RotationNode extends BinaryNode {

    private final static long serialVersionUID = 1L;

    @Nonnull
    @Override
    protected Symbol getSymbol() {
        return Symbol.ROTATION;
    }

    /**
     * Inverses the subtree starting at this node.
     */
    @Override
    public void invert() {
        if (getChildCount() != 2) {
            return;
        }
        //The rotator does not have to be inverted.
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
                        a.resolvedIterator(true),
                        b.resolvedIterator(inverse),
                        a.resolvedIterator(false)
                )
        );
    }
}

/* @(#)InversionNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.jhotdraw.annotation.Nonnull;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * An InversionNode holds one child A.
 * The side effect of an inversion node is A'.
 *
 * @author Werner Randelshofer
 */
public class InversionNode extends UnaryNode {
    private final static long serialVersionUID = 1L;

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        return super.resolvedIterator(!inverse);
    }

    @Override
    public void writeTokens(Writer w, @Nonnull Notation p, Map<String, MacroNode> macroMap)
            throws IOException {
        if (getChildCount() == 1 && (getChildAt(0) instanceof InversionNode)) {
            // Short cut: If two inversions are nested, they cancel each other out.
            // We print the children of the inner inversion without having to invert
            // them.
            InversionNode nestedInversion = (InversionNode) getChildAt(0);
            for (Iterator<Node> enumer = nestedInversion.getChildren().iterator(); enumer.hasNext(); ) {
                ((SequenceNode) enumer.next()).writeTokens(w, p, macroMap);
                if (enumer.hasNext()) {
                    p.writeToken(w, Symbol.DELIMITER);
                    w.write(' ');
                }
            }



        } else if (getChildCount() == 1 && (getChildAt(0) instanceof MoveNode)) {
            // Short cut: If the inversion is a parent of a single move node, we
            // print the inverse of the move node.
            InversionNode inverted = (InversionNode) cloneSubtree();
            for (Iterator<Node> i = inverted.reversedChildIterator(); i.hasNext();) {
                SequenceNode node = (SequenceNode) i.next();
                node.invert();
                node.writeTokens(w, p, macroMap);
            }

        } else {
            // No short cut possible: Print the inversion.
            Syntax invertorPos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.INVERSION) : null;

            if (invertorPos == null) {
                InversionNode inverted = (InversionNode) cloneSubtree();
                for (Iterator<Node> i = inverted.reversedChildIterator();i.hasNext();) {
                    SequenceNode node = (SequenceNode) i.next();
                    node.invert();
                    node.writeTokens(w, p, macroMap);
                }
            } else if (invertorPos == Syntax.PREFIX) {
                p.writeToken(w, Symbol.INVERSION_OPERATOR);
                super.writeTokens(w, p, macroMap);

            } else if (invertorPos == Syntax.SUFFIX) {
                super.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.INVERSION_OPERATOR);
            } else {
                throw new InternalError("Syntax not implemented " + invertorPos);
            }
        }
    }
}

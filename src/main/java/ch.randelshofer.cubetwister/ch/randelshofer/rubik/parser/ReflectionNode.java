/* @(#)ReflectionNode.java
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
 * A reflection node holds one child A.
 * The side effect of a reflection node on a cube is
 * the reflection of A.
 *
 * @author Werner Randelshofer
 */
public class ReflectionNode extends UnaryNode {
    private final static long serialVersionUID = 1L;

    @Override
    public void writeTokens(Writer w, @Nonnull Notation p, Map<String, MacroNode> macroMap)
            throws IOException {
        // Short cut: If two reflections are nested, they cancel each other out.
        // We print the children of the inner reflection without having to
        // reflect them.
        if (getChildCount() == 1 && (getChildAt(0) instanceof ReflectionNode)) {
            ReflectionNode nested = (ReflectionNode) getChildAt(0);
            Iterator<Node> i = nested.getChildren().iterator();
            while (i.hasNext()) {
                i.next().writeTokens(w, p, macroMap);
                if (i.hasNext()) {
                    p.writeToken(w, Symbol.DELIMITER);
                    w.write(' ');
                }
            }

        } else {
            // No short cut possible: Print the reflection.
            Syntax reflectorPos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.REFLECTION) : null;

            if (reflectorPos == null) {
                ReflectionNode reflected = (ReflectionNode) cloneSubtree();
                for (Node node1 : reflected.getChildren()) {
                    SequenceNode node = (SequenceNode) node1;
                    node.reflect();
                    node.writeTokens(w, p, macroMap);
                }

            } else if (reflectorPos == Syntax.PREFIX) {
                p.writeToken(w, Symbol.REFLECTION_OPERATOR);
                super.writeTokens(w, p, macroMap);

            } else if (reflectorPos == Syntax.SUFFIX) {
                super.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.REFLECTION_OPERATOR);
            }
        }
    }

    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Nonnull
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        return new ReflectedIterator(super.resolvedIterator(inverse));
    }

    private static class ReflectedIterator
            implements Iterator<Node> {
        protected Iterator<Node> inner;

        public ReflectedIterator(Iterator<Node> inner) {
            this.inner = inner;
        }

        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public Node next() {
            Node elem = inner.next();
            if (elem instanceof MoveNode) {
                MoveNode t = (MoveNode) elem;
                MoveNode reflectedT = (MoveNode) t.clone();
                reflectedT.reflect();
                return reflectedT;
            } else {
                return elem;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}

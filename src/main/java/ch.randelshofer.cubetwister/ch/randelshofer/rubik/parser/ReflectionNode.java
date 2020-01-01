/* @(#)ReflectionNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;

import java.io.IOException;
import java.io.PrintWriter;
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

    public ReflectionNode() {
        this(-1, -1);
    }
    
    public ReflectionNode(int startpos, int endpos) {
        super(startpos, endpos);
    }
    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    @Override
    public void applyTo(Cube cube, boolean inverse) {
        for (Iterator<Node> i=resolvedIterator(inverse); i.hasNext(); ) {
            Node child = i.next();
            child.applyTo(cube, inverse);
        }
    }
    
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        // Short cut: If two reflections are nested, they cancel each other out.
        // We print the children of the inner reflection without having to
        // reflect them.
        if (getChildCount() == 1 && (getChildAt(0) instanceof ReflectionNode)) {
            ReflectionNode nestedInversion = (ReflectionNode) getChildAt(0);
            Iterator<Node> enumer = nestedInversion.getChildren().iterator();
            while (enumer.hasNext()) {
                enumer.next().writeTokens(w, p, macroMap);
                if (enumer.hasNext()) {
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

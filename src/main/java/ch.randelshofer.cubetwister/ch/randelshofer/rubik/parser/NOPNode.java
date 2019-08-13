/* @(#)NOPNode.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * A No Operation Node holds no children.
 * A NOP node has no side effect if applied to a cube.
 *
 * @author Werner Randelshofer
 */
public class NOPNode extends Node {
    private final static long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public NOPNode() {
        this(-1, -1);
    }

    /**
     * Creates a new instance.
     */
    public NOPNode(int startpos, int endpos) {
        super(startpos, endpos);
        setAllowsChildren(false);
    }

    @Override
    public Iterator<Node> resolvedIterator(boolean isInverse) {
        return Collections.emptyIterator();
        //return new SingletonIterator<Node>(this);
    }

    /**
     * Returns a string representation of this node using the specified notation.
     */
    @Override
    public void writeTokens(PrintWriter w, Notation notation, Map<String, MacroNode> macroMap)
            throws IOException {
        if (notation.isSupported(Symbol.NOP)) {
            w.append(notation.getToken(Symbol.NOP));
        }
    }
}

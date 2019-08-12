/* @(#)StatementNode.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
/**
 * A StatementNode is a structuring unit. It holds one child A.
 * The side effect of a statement node to a Cube is A.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.0 2001-07-25
 * @see ScriptParser
 */
public class StatementNode extends Node {
    private final static long serialVersionUID = 1L;
    public StatementNode(int layerCount) {
        this(layerCount, -1,-1);
    }

    public StatementNode(int layerCount, int startpos, int endpos) {
        super(Symbol.STATEMENT, startpos, endpos);
    }
    /**
     * Writes the token(s) represented by the subtree starting
     * at this node. The syntax and the string representations 
     * of the tokens are provided by the parser.
     *
     * @param w   This is where the tokens are written to.
     * @param n   The notation which provides the tokens.
     * @param macroMap Macros with identifiers of which
     *             macroMap.containsKey(String) returns true
     *             are preserved.
     */
    @Override
    public void writeTokens(PrintWriter w, Notation n, Map<String,MacroNode> macroMap)
    throws IOException {
        Iterator<Node> i = getChildren().iterator();
        while (i.hasNext()) {
            i.next().writeTokens(w, n, macroMap);
            if (i.hasNext()) {
                n.writeToken(w, Symbol.DELIMITER);
                w.write(' ');
            }
        }
    }
}

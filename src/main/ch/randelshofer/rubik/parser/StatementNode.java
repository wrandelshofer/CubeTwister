/*
 * @(#)StatementNode.java  6.0  2005-01-31
 *
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.util.*;
import java.util.*;
import java.io.*;
/**
 * A StatementNode is a structuring unit. It holds one child A.
 * The side effect of a statement node to a Cube is A.
 *
 * @author Werner Randelshofer
 * @version 6.0 2005-12-24 Reworked.
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
        super(Symbol.STATEMENT, layerCount, startpos, endpos);
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
    @Override
    public List<Node> toResolvedList() {
        if (children == null) {
            return Collections.emptyList();
        } else {
            ListOfLists<Node> list = new ListOfLists<Node>();
            for (int i=0; i < getChildCount(); i++) {
                list.addList(getChildAt(i).toResolvedList());
            }
            return list;
        }
    }
     
    @Override
        public String toString() {
        StringBuilder b= new StringBuilder( "StatementNode[");
        for (Node n:getChildren()) {
            b.append(n.toString());
        }
        b.append("]");
        return b.toString();
    }
}

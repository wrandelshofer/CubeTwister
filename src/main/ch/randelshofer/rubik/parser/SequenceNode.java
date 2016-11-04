/*
 * @(#)SequenceNode.java  6.0  2009-01-24
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

/**
 * A SequenceNode holds a sequence of statements as its children A.
 * The side effect of a script node to a Cube is A.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.1 2004-02-25 Method overwritePositions added.
 * <br>1.0.1 2002-05-17 NPE in enumerateChildrenReversed fixed.
 * <br>1.0 2001-07-25
 * @see ScriptParser
 */
public class SequenceNode extends Node {
        private final static long serialVersionUID = 1L;

    /**
     * Creates a script node with start position = 0
     * and end position = 0.
     */
    public SequenceNode(int layerCount) {
        this(layerCount, -1, -1);
    }
    /**
     * Creates a script node which represents a symbol
     * at the indicated position in the source code.
     *
     * @param startpos The start position of the symbol.
     * @param endpos The end position of the symbol.
     */
    public SequenceNode(int layerCount, int startpos, int endpos) {
        super(Symbol.SEQUENCE, layerCount, startpos, endpos);
        setAllowsChildren(true);
    }
    
    @Override
        public String toString() {
        StringBuilder b= new StringBuilder( "SequenceNode[");
        for (Node n:getChildren()) {
            b.append(n.toString());
        }
        b.append("]");
        return b.toString();
    }


}

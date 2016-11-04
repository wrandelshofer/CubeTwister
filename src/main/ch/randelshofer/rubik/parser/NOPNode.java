/*
 * @(#)NOPNode.java  1.1  2009-07-30
 *
 * Copyright (c) 2005-2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * A No Operation Node holds no children.
 * A NOP node has no side effect if applied to a cube.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2009-07-30 Added writeTokens method.
 * <br>1.0  01 February 2005  Created.
 */
public class NOPNode extends Node {
    private final static long serialVersionUID = 1L;

    /** Creates a new instance. */
    public NOPNode(int layerCount) {
        this(layerCount, -1, -1);
    }

    /** Creates a new instance. */
    public NOPNode(int layerCount, int startpos, int endpos) {
        super(Symbol.NOP, layerCount, startpos, endpos);
        setAllowsChildren(false);
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

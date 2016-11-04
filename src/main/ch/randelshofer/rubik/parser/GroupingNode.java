/*
 * @(#)GroupingNode.java  5.0  2005-01-31
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.util.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * A GroupingNode holds a sequence of children A.
 * The resolved Enumeration of a grouping node is A.
 *
 * @author  werni
 * @version $Id$
 * <br>2.1 2004-03-25 A grouping which has a grouping as its
 * only child does not need to be printed.
 * <br>2.0 2003-01-01 Renamed from SequenceNode to GroupingNode.
 * <br>1.0 2001-07-24
 */
public class GroupingNode extends Node {
        private final static long serialVersionUID = 1L;

    /** Creates new SequenceNode */
    public GroupingNode(int layerCount) {
        this(layerCount, -1,-1);
    }
    public GroupingNode(int layerCount, int startpos, int endpos) {
        super(Symbol.GROUPING, layerCount, startpos, endpos);
    }
    
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        if (getChildCount() <= 1) {
            // Short cut: If a grouping has only one or no
            // child, it does not need to be printed.
            Node child = getChildAt(0);
            child.writeTokens(w, p, macroMap);
        } else {
            // FIXME - Add support for more cube types
            Cube cube = Cubes.create(layerCount);
            applyTo(cube, false);
            String macroName = p.getEquivalentMacro(cube, macroMap);
            if (macroName != null) {
                w.write(macroName);
            } else {
                if (p.isSupported(Symbol.GROUPING)) {
                    p.writeToken(w, Symbol.GROUPING_BEGIN);
                    super.writeTokens(w, p, macroMap);
                    p.writeToken(w, Symbol.GROUPING_END);
                } else {
                    super.writeTokens(w, p, macroMap);
                }
            }
        }
    }
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("GroupingNode[");
        for (Node n : getChildren()) {
            b.append(n.toString());
        }
        b.append("]");
        return b.toString();
    }
}

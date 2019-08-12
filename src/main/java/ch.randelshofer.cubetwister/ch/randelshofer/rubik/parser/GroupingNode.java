/* @(#)GroupingNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
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
    public GroupingNode() {
        this(-1,-1);
    }
    public GroupingNode(int startpos, int endpos) {
        super(Symbol.GROUPING, startpos, endpos);
    }
    
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
                if (p.isSupported(Symbol.GROUPING)) {
                    p.writeToken(w, Symbol.GROUPING_BEGIN);
                    super.writeTokens(w, p, macroMap);
                    p.writeToken(w, Symbol.GROUPING_END);
                } else {
                    super.writeTokens(w, p, macroMap);
                }

    }
}

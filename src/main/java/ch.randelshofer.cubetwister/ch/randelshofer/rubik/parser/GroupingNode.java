/* @(#)GroupingNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import org.jhotdraw.annotation.Nonnull;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * A GroupingNode holds a sequence of children A.
 * The resolved Enumeration of a grouping node is A.
 *
 * @author Werner Randelshofer
 */
public class GroupingNode extends UnaryNode {
    private final static long serialVersionUID = 1L;

    @Override
    public void writeTokens(Writer w, @Nonnull Notation p, Map<String, MacroNode> macroMap)
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

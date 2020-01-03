/* @(#)MacroNode.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.notation.Notation;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
/**
 * A MacroNode holds a macro identifier and an unparsed script String. 
 * The MacroNode can be expanded to hold the macro script as its child A.
 * The side effect of a macro node is A.
 *
 * @author Werner Randelshofer
 */
public class MacroNode extends Node {
    private final static long serialVersionUID = 1L;
    /**
     * Holds the identifier of the script macro.
     */
    @Nullable
    private String identifier;
    /**
     * Holds the source of the script macro.
     */
    private String script;

    /** Creates new MacroNode */
    public MacroNode(String identifier, String script) {
        this(identifier,script,-1,-1);
    }
    public MacroNode(String identifier, String script, int startpos, int endpos) {
        super(startpos, endpos);
        this.identifier = identifier;
        this.script = script;
        setAllowsChildren(true);
    }

    @Nullable
    public String getIdentifier() {
        return identifier;
    }

    public String getScript() {
        return script;
    }

    public void expand(@Nonnull ScriptParser parser)
            throws IOException {
        // Don't expand if already expanded
        if (getChildCount() > 0) {
            return;
        }

        // Check if macro is recursive
        DefaultMutableTreeNode ancestor = (DefaultMutableTreeNode) getParent();
        while (ancestor != null) {
            if (ancestor instanceof MacroNode
                    && ((MacroNode) ancestor).identifier.equals(identifier)) {
                throw new ParseException("Macro: Illegal Recursion", getStartPosition(), getEndPosition());
            }
            ancestor = (DefaultMutableTreeNode) ancestor.getParent();
        }
        
        // Expand the macro
        removeAllChildren();
        add(parser.parse(script));

        // Overwrite start and end positions of children
        int sp = getStartPosition();
        int ep = getEndPosition();
        for (Node node : preorderIterable()) {
            node.setStartPosition(sp);
            node.setEndPosition(ep);
        }
    }

    @Override
    public void writeTokens(Writer w, Notation p, @Nonnull Map<String, MacroNode> macroMap)
            throws IOException {
        if (macroMap.containsKey(identifier)) {
            w.write(identifier);
        }
    }
}

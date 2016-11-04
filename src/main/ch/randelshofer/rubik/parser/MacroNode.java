/*
 * @(#)MacroNode.java  6.0  2007-11-15
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.io.*;
import java.io.*;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 * A MacroNode holds a macro identifier and an unparsed script String. 
 * The MacroNode can be expanded to hold the macro script as its child A.
 * The side effect of a macro node is A.
 *
 * @author  werni
 * @version $Id$
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.0.1 2002-02-25 During expansion the expanded nodes did not
 * always get the right start and end positions.
 * <br>1.0 2001-07-22 Created.
 */
public class MacroNode extends Node {
    private final static long serialVersionUID = 1L;
    /**
     * Holds the identifier of the script macro.
     */
    private String identifier;
    /**
     * Holds the source of the script macro.
     */
    private String script;
    /**
     * Holds the parser for the script macro.
     */
    private ScriptParser parser;
    
    /** Creates new MacroNode */
    public MacroNode(int layerCount, String identifier, String script, int startpos, int endpos) {
        super(Symbol.MACRO, layerCount, startpos, endpos);
        this.identifier = identifier;
        this.script = script;
        setAllowsChildren(true);
    }
    
    /**
     * Transformes the subtree starting at this node by
     * the given ScriptParserAWT.symbol constant.
     * Does nothing if the transformation can not be done.
     */
    @Override
    public void transform(int axis, int layerMask, int angle) {
        identifier = null; // macro must be expanded.
        super.transform(axis, layerMask, angle);
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void expand(ScriptParser parser)
    throws IOException {
        // Don't expand if already expanded
        if (getChildCount() > 0) return;
        
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
        int sp = getStartPosition();
        int ep = getEndPosition();
        parser.parse(new StringReader(script), this);
        
        // Overwrite start and end positions
        overwritePositions(sp, ep);
    }
    
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        // FIXME - Implement macro expansion
        if (false && macroMap.containsKey(identifier)) { 
            w.write(identifier);
        } else {
            // XXX - Add support for more cube types
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
}

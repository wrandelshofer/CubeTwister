/*
 * @(#)ReflectionNode.java  5.1  2009-01-22
 *
 * Copyright (c) 2001-2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import java.io.*;
import java.util.*;
/**
 * Represents a node of a parsed script.
 *
 * @author Werner Randelshofer
 * @version 5.1 2009-01-22 Override applyTo method. Fixed reflect() method.
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.1 2004-03-28 Two nested reflections cancel each other out.
 * <br>1.0 2001-07-25
 * @see ScriptParser
 */
public class ReflectionNode extends Node {
        private final static long serialVersionUID = 1L;

    public ReflectionNode(int layerCount) {
        this(layerCount, -1, -1);
    }
    
    public ReflectionNode(int layerCount, int startpos, int endpos) {
        super(Symbol.REFLECTION, layerCount, startpos, endpos);
    }
    /**
     * Applies the symbol represented by this node to the cube.
     *
     * @param cube A cube to be transformed by this symbol.
     * @param inverse If true, the transform will be done in inverse order.
     */
    @Override
    public void applyTo(Cube cube, boolean inverse) {
        for (Iterator<Node> i=resolvedIterator(inverse); i.hasNext(); ) {
            Node child = i.next();
            child.applyTo(cube, inverse);
        }
    }
    
    @Override
    public void writeTokens(PrintWriter w, Notation p, Map<String,MacroNode> macroMap)
    throws IOException {
        // Short cut: If two reflections are nested, they cancel each other out.
        // We print the children of the inner reflection without having to
        // reflect them.
        if (getChildCount() == 1 && (getChildAt(0) instanceof ReflectionNode)) {
            ReflectionNode nestedInversion = (ReflectionNode) getChildAt(0);
            Enumeration<Node> enumer = nestedInversion.children();
            while (enumer.hasMoreElements()) {
                enumer.nextElement().writeTokens(w, p, macroMap);
                if (enumer.hasMoreElements()) {
                    p.writeToken(w, Symbol.DELIMITER);
                    w.write(' ');
                }
            }
            
        } else {
            // No short cut possible: Print the reflection.
            Syntax reflectorPos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(Symbol.REFLECTION) : null;
            
            if (reflectorPos == null) {
                    ReflectionNode reflected = (ReflectionNode) cloneSubtree();
                    Enumeration<Node> enumer = reflected.children();
                    while (enumer.hasMoreElements()) {
                        SequenceNode node = (SequenceNode) enumer.nextElement();
                        node.reflect();
                        node.writeTokens(w, p, macroMap);
                    }
                    
                } else if (reflectorPos == Syntax.PREFIX) { 
                    p.writeToken(w, Symbol.REFLECTOR);
                    super.writeTokens(w, p, macroMap);
                    
                } else if (reflectorPos == Syntax.SUFFIX) {
                    super.writeTokens(w, p, macroMap);
                    p.writeToken(w, Symbol.REFLECTOR);
            }
        }
    }
    /**
     * Enumerate this symbol and all of its children.
     * Special operators (i. e. repeat and inverse) are
     * resolved before the children are returned.
     */
    @Override
    public Iterator<Node> resolvedIterator(boolean inverse) {
        return new ReflectedIterator(layerCount, super.resolvedIterator(inverse));
    }
    private static class ReflectedIterator
    implements Iterator<Node> {
        protected Iterator<Node> inner;
        private final int layerCount;
        
        public ReflectedIterator(int layerCount, Iterator<Node> inner) {
            this.layerCount = layerCount;
            this.inner = inner;
        }
        
        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }
        
        @Override
        public Node next() {
            Node elem = inner.next();
            if (elem instanceof MoveNode) {
                MoveNode t = (MoveNode) elem;
                MoveNode reflectedT = (MoveNode) t.clone();
                reflectedT.reflect();
                return reflectedT;
            } else {
                return elem;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
    }
    @Override
    public List<Node> toResolvedList() {
        return new ReflectedList(super.toResolvedList());
    }
}

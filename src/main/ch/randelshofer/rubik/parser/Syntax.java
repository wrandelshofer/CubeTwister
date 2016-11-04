/*
 * @(#)Syntax.java  10.0  2013-12-15
 *
 * Copyright (c) 2004-2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.rubik.parser;

import java.util.*;

/**
 * Typesafe enum of Syntaxes for the Parser.
 *
 * @author  Werner Randelshofer
 * @version 10.0 2013-12-15 Converted from class to enum.
 * <br>9.0 2009-01-22 Reworked for ScriptParser 9.0.
 * <br>5.0 2005-01-31 Reworked.
 * <br>1.0  November 14, 2004  Created.
 */
public enum Syntax {
    /**
     * Binary prefix syntax: The affix is placed between begin and end before
     * the root.
     * <pre>
     * Binary Prefix ::= Begin , Affix , End , Root ;
     * </pre>
     *
     * Unary prefix syntax: The affix is placed before the root.
     * <pre>
     * Unary Prefix ::= Affix , Root ;
     * </pre>
     */
    PREFIX("prefix"),
    /**
     * Binary suffix syntax: The affix is placed between begin and end after
     * the root.
     * <pre>
     * Binary Suffix ::= Root , Begin , Affix , End ;
     * </pre>
     *
     * Unary suffix syntax: The affix is placed after the root.
     * <pre>
     * Suffix ::= Root, Affix ;
     * </pre>
     */
    SUFFIX("suffix"),
    /**
     * Circumfix syntax: The root is placed between begin and end.
     * <pre>
     * Circumfix ::= Begin , Root , End ;
     * </pre>
     */
    CIRCUMFIX("circumfix"),
    /**
     * Pre-Circumfix syntax: The affix is placed before the root.
     * Begin, delimiter and end tokens are placed around them.
     * <pre>
     * Precircumfix ::= Begin , Affix , Delimiter , Root , End ;
     * </pre>
     */
    PRECIRCUMFIX("precircumfix"),
    /**
     * Post-Circumfix syntax: The affix is placed after the root.
     * Begin, delimiter and end tokens are placed around them.
     * <pre>
     * Postcircumfix ::= Begin , Root , Delimiter , Affix , End ;
     * </pre>
     */
    POSTCIRCUMFIX("postcircumfix"),
    /**
     * Binary Pre-Infix syntax: The affix is placed between pre-root and post-root.
     * <pre>
     * Infix ::= Pre-Root , Affix, Post-Root;
     * </pre>
     */
    PREINFIX("preinfix"),
    /**
     * Binary Post-Infix syntax: The affix is placed between post-root and pre-root.
     * <pre>
     * Infix ::= Post-Root , Affix , Pre-Root;
     * </pre>
     */
    POSTINFIX("postinfix");
    
    
    /**
     * Name of the symbol.
     */
    private final String name;
    
    private static HashMap<String,Syntax> syntaxValueSet;
    
    private Syntax(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static Map<String,Syntax> getSyntaxValueSet() {
        if (syntaxValueSet == null) {
            syntaxValueSet = new HashMap<String,Syntax>();
            for (Syntax s:values()) {
                syntaxValueSet.put(s.name, s);
            }
        }
        return Collections.unmodifiableMap(syntaxValueSet);
    }
}

/* @(#)Syntax.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.notation;

import org.jhotdraw.annotation.Nonnull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Typesafe enum of Syntaxes for the Parser.
 *
 * @author  Werner Randelshofer
 */
public enum Syntax {
    /**
     * A primary expression: A single literal or name that stands for itself
     * or can be used as a building block of other expressions.
     */
    PRIMARY ("primary"),

    /**
     * Unary prefix expression. The operator consists
     * of a single token that is placed before the operand.
     * <pre>
     * Prefix ::= Operator, Operand ;
     * </pre>
     *
     */
    PREFIX("prefix"),
    /**
     * Unary suffix expression.  The operator consists
     * of a single token that is placed after the operand.
     * <pre>
     * Suffix ::= Operand, Operator  ;
     * </pre>
     */
    SUFFIX("suffix"),
    /**
     * Unary circumfix expression: The operator consists
     * of two tokens (begin, end) that are placed
     * around the operand.
     * <pre>
     * Unary Circumfix ::= Begin , Operand1 , End ;
     * </pre>
     */
    CIRCUMFIX("circumfix"),
    /**
     * Binary Pre-Circumfix expression:  The operator consists
     * of three tokens (begin, delimiter, end) that are placed
     * around operand 2 and operand 1.
     * <pre>
     * Binary Precircumfix ::= Begin , Operand2 , Delimiter , Operand1 , End ;
     * </pre>
     */
    PRECIRCUMFIX("precircumfix"),
    /**
     * Binary Post-Circumfix expression:  The operator consists
     * of three tokens (begin, delimiter, end) that are placed
     * around the operands 1 and 2.
     * <pre>
     * Binary Postcircumfix ::= Begin , Operand1 , Delimiter , Operand2 , End ;
     * </pre>
     */
    POSTCIRCUMFIX("postcircumfix"),
    /**
     * Binary Pre-Infix expression: The operator consists of a single token
     * that is placed between operand 2 and 1.
     * <pre>
     * Preinfix ::= Operand2 , Operator, Operand1;
     * </pre>
     */
    PREINFIX("preinfix"),
    /**
     * Binary Post-Infix expression: The operator consists of a single token
     * that is placed between operand 1 and 2.
     * <pre>
     * Postinfix ::= Operand1 , Operator, Operand2;
     * </pre>
     */
    POSTINFIX("postinfix"),
    ;

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

    @Nonnull
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

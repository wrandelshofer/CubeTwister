/*
 * @(#)BinaryNode.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser.ast;

import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import org.jhotdraw.annotation.Nonnull;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * A binary node must have two children.
 * The first child is the first operand,
 * the second child is the second operand of the binary node.
 */
public abstract class BinaryNode extends Node {
    private final static long serialVersionUID = 1L;

    protected abstract Symbol getSymbol();

    @Override
    public void writeTokens(Writer w, @Nonnull ScriptNotation p, Map<String, MacroNode> macroMap)
            throws IOException {
        if (getChildCount() != 2) {
            return;
        }
        final Node operand1 = getChildAt(0);
        final Node operand2 = getChildAt(1);

        Syntax pos = (p.isSupported(Symbol.GROUPING)) ? p.getSyntax(getSymbol()) : null;
        if (pos == null) {
            // Write the commutation as (A B A' B').
            if (p.isSupported(Symbol.GROUPING)) {
                p.writeToken(w, Symbol.GROUPING_BEGIN);
            }
            operand1.writeTokens(w, p, macroMap);
            w.write(' ');
            p.writeToken(w, Symbol.NOP);
            w.write(' ');
            operand2.writeTokens(w, p, macroMap);
            w.write(' ');
            p.writeToken(w, Symbol.NOP);
            w.write(' ');
            Node inv = operand1.cloneSubtree();
            inv.invert();
            inv.writeTokens(w, p, macroMap);
            w.write(' ');
            p.writeToken(w, Symbol.NOP);
            w.write(' ');
            inv = new SequenceNode();
            Iterator<Node> enumer = getChildren().iterator();
            while (enumer.hasNext()) {
                //inv.add((Node) enumer.nextElement());
                inv.add(enumer.next().cloneSubtree());
            }
            inv.invert();
            inv.writeTokens(w, p, macroMap);
            if (p.isSupported(Symbol.GROUPING)) {
                p.writeToken(w, Symbol.GROUPING_END);
            }
        } else if (pos == Syntax.PREFIX) {
            if (operand1.getChildCount() != 0) {
                p.writeToken(w, Symbol.COMMUTATION_BEGIN);
                operand1.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.COMMUTATION_END);
            }
            if (getChildCount() == 1) {
                operand2.writeTokens(w, p, macroMap);
            } else {
                p.writeToken(w, Symbol.GROUPING_BEGIN);
            }
            operand2.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.GROUPING_END);

        } else if (pos == Syntax.SUFFIX) {
            if (getChildCount() == 1) {
                operand2.writeTokens(w, p, macroMap);
            } else {
                p.writeToken(w, Symbol.GROUPING_BEGIN);
                operand2.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.GROUPING_END);
            }
            if (operand1.getChildCount() != 0) {
                p.writeToken(w, Symbol.COMMUTATION_BEGIN);
                operand1.writeTokens(w, p, macroMap);
                p.writeToken(w, Symbol.COMMUTATION_END);
            }
        } else if (pos == Syntax.PRECIRCUMFIX) {
            p.writeToken(w, Symbol.COMMUTATION_BEGIN);
            operand1.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.COMMUTATION_DELIMITER);
            operand2.writeTokens(w, p, macroMap);
            p.writeToken(w, Symbol.COMMUTATION_END);
        }
    }

}

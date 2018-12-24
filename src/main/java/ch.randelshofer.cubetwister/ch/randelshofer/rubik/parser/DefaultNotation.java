/* @(#)DefaultNotation.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DefaultNotation supports Superset ENG.
 *
 * FIXME - This class is incomplete. It currently only works with a 3x3 Rubiks Cube.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>0.3 2009-04-11 Added constructor with layerCount parameter.
 * <br>0.2 2008-11-24 Don't print "Null", if a symbol is not supported
 * by this notation.
 * <br>0.1 September 18, 2006 Experimental.
 */
public class DefaultNotation implements Notation {

    private HashMap<Symbol, String> symbolToTokenMap = new HashMap<Symbol, String>();
    private HashMap<String, Set<Symbol>> tokenToSymbolMap = new HashMap<>();
    private HashMap<Move, String> moveToTokenMap = new HashMap<Move, String>();
    private HashMap<String, Move> tokenToMoveMap = new HashMap<String, Move>();
    private HashMap<Symbol, Syntax> symbolToSyntaxMap = new HashMap<Symbol, Syntax>();
    private int layerCount;

    /** Creates a new instance. */
    public DefaultNotation() {
        this(3);
    }

    public DefaultNotation(int layerCount) {
        this.layerCount = layerCount;

        addToken(Symbol.NOP, "·");
        addToken(Symbol.NOP, ".");
        addToken(Symbol.FACE_R, "r");
        addToken(Symbol.FACE_U, "u");
        addToken(Symbol.FACE_F, "f");
        addToken(Symbol.FACE_L, "l");
        addToken(Symbol.FACE_D, "d");
        addToken(Symbol.FACE_B, "b");
        addToken(Symbol.PERMUTATION_PLUS, "+");
        addToken(Symbol.PERMUTATION_MINUS, "-");
        addToken(Symbol.PERMUTATION_PLUSPLUS, "++");
        addToken(Symbol.PERMUTATION_BEGIN, "(");
        addToken(Symbol.PERMUTATION_END, ")");
        addToken(Symbol.PERMUTATION_DELIMITER, ",");
        //addToken(Symbol.DELIMITER ,"");
        //addToken(Symbol.INVERSION_BEGIN ,"(");
        //addToken(Symbol.INVERSION_END ,")");
        //addToken(Symbol.INVERSION_DELIMITER ,"");
        addToken(Symbol.INVERTOR, "'");
        addToken(Symbol.INVERTOR, "-");
        //addToken(Symbol.REFLECTION_BEGIN ,"(");
        //addToken(Symbol.REFLECTION_END ,")");
        //addToken(Symbol.REFLECTION_DELIMITER ,"");
        addToken(Symbol.REFLECTOR, "*");
        addToken(Symbol.GROUPING_BEGIN, "(");
        addToken(Symbol.GROUPING_END, ")");
        //addToken(Symbol.REPETITION_BEGIN ,"");
        //addToken(Symbol.REPETITION_END ,"");
        //addToken(Symbol.REPETITION_DELIMITER ,"");
        addToken(Symbol.COMMUTATION_BEGIN, "[");
        addToken(Symbol.COMMUTATION_END, "]");
        addToken(Symbol.COMMUTATION_DELIMITER, ",");
        addToken(Symbol.CONJUGATION_BEGIN, "<");
        addToken(Symbol.CONJUGATION_END, ">");
        //addToken(Symbol.CONJUGATION_DELIMITER ,":");
        addToken(Symbol.ROTATION_BEGIN, "<");
        addToken(Symbol.ROTATION_END, ">'");
        //addToken(Symbol.ROTATION_DELIMITER ,"::");
        // addToken(Symbol.MACRO ,"");
        addToken(Symbol.MULTILINE_COMMENT_BEGIN, "/*");
        addToken(Symbol.MULTILINE_COMMENT_END, "*/");
        addToken(Symbol.SINGLELINE_COMMENT_BEGIN, "//");

        // Layer masks
        int inner = 1;
        int middle = 1<<(layerCount/2);
        int outer = 1<<(layerCount-1);
        int all = inner | middle | outer;

        addMove(new Move(0, outer, 1), "R");
        addMove(new Move(1, outer, 1), "U");
        addMove(new Move(2, outer, 1), "F");
        addMove(new Move(0, inner, -1), "L");
        addMove(new Move(1, inner, -1), "D");
        addMove(new Move(2, inner, -1), "B");

        addMove(new Move(0, middle, 1), "MR");
        addMove(new Move(1, middle, 1), "MU");
        addMove(new Move(2, middle, 1), "MF");
        addMove(new Move(0, middle, -1), "ML");
        addMove(new Move(1, middle, -1), "MD");
        addMove(new Move(2, middle, -1), "MB");

        addMove(new Move(0, outer|middle, 1), "TR");
        addMove(new Move(1, outer|middle, 1), "TU");
        addMove(new Move(2, outer|middle, 1), "TF");
        addMove(new Move(0, inner|middle, -1), "TL");
        addMove(new Move(1, inner|middle, -1), "TD");
        addMove(new Move(2, inner|middle, -1), "TB");
        
        addMove(new Move(0, outer|inner, 1), "SR");
        addMove(new Move(1, outer|inner, 1), "SU");
        addMove(new Move(2, outer|inner, 1), "SF");
        addMove(new Move(0, inner|outer, -1), "SL");
        addMove(new Move(1, inner|outer, -1), "SD");
        addMove(new Move(2, inner|outer, -1), "SB");
        
        addMove(new Move(0, all, 1), "CR");
        addMove(new Move(1, all, 1), "CU");
        addMove(new Move(2, all, 1), "CF");
        addMove(new Move(0, all, -1), "CL");
        addMove(new Move(1, all, -1), "CD");
        addMove(new Move(2, all, -1), "CB");

        symbolToSyntaxMap.put(Symbol.COMMUTATION, Syntax.PRECIRCUMFIX);
        symbolToSyntaxMap.put(Symbol.CONJUGATION, Syntax.PREFIX);
        symbolToSyntaxMap.put(Symbol.ROTATION, Syntax.PREFIX);
        symbolToSyntaxMap.put(Symbol.GROUPING, Syntax.CIRCUMFIX);
        symbolToSyntaxMap.put(Symbol.PERMUTATION, Syntax.PRECIRCUMFIX);
        symbolToSyntaxMap.put(Symbol.REPETITION, Syntax.SUFFIX);
        symbolToSyntaxMap.put(Symbol.REFLECTION, Syntax.SUFFIX);
        symbolToSyntaxMap.put(Symbol.INVERSION, Syntax.SUFFIX);
    }

    private void addToken(Symbol symbol, String token) {
        // Add to symbolToTokenMap
        if (!symbolToTokenMap.containsKey(symbol)) {
            symbolToTokenMap.put(symbol, token);
        }

        // Add to tokenToSymbolMap
        Set<Symbol> symbols = tokenToSymbolMap.compute(token,(key,value)->value==null?new LinkedHashSet<Symbol>():value);
        symbols.add(symbol);
        if (Symbol.PERMUTATION.isSubSymbol(symbol)) {
            symbols.add(Symbol.PERMUTATION);
        }
    }

    private void addMove(Move move, String token) {
        moveToTokenMap.put(move, token);
        tokenToMoveMap.put(token, move);

        Set<Symbol> symbols = tokenToSymbolMap.compute(token,(k,v)->v==null?new LinkedHashSet<Symbol>():v);
        symbols.add(Symbol.MOVE);
    }

    public int getLayerCount() {
        return layerCount;
    }

    public String getEquivalentMacro(Cube cube, Map<String,MacroNode> localMacros) {
        return null;
    }

    public void writeToken(PrintWriter w, Symbol symbol) throws IOException {
        String token = symbolToTokenMap.get(symbol);
        if (token != null) {
            w.print(token);
        }
    }

    public void writeToken(PrintWriter w, int axis, int layerMask, int angle) throws IOException {
        w.print(new Move(axis, layerMask, angle));
    }

    public boolean isSupported(Symbol s) {
        return symbolToTokenMap.containsKey(s) || symbolToSyntaxMap.containsKey(s);
    }

    public Syntax getSyntax(Symbol s) {
        return symbolToSyntaxMap.get(s);
    }

    public boolean isToken(String token) {
        return tokenToSymbolMap.containsKey(token);
    }

    public boolean isTokenFor(String token, Symbol symbol) {
        Set<Symbol> list = tokenToSymbolMap.get(token);
        return list != null && list.contains(symbol);
    }

    public String getToken(Symbol s) {
        return symbolToTokenMap.get(s);
    }

    public Symbol getSymbolFor(String token, Symbol compositeSymbol) {
        Set<Symbol> list = tokenToSymbolMap.get(token);
        if (list != null) {
            for (Symbol s : list) {
                if (compositeSymbol.isSubSymbol(s)) {
                    return s;
                }
            }
        }
        return null;
    }

    public void configureMoveFromToken(MoveNode move, String moveToken) {
        Move t = tokenToMoveMap.get(moveToken);
        if (t != null) {
            move.setAngle(t.getAngle());
            move.setAxis(t.getAxis());
            move.setLayerMask(t.getLayerMask());
        }
    }

    @Override
    public String getToken(Move s) {
        return moveToTokenMap.get(s);
    }

    @Override
    public List<MacroNode> getMacros() {
        // FIXME - Implement me
        return Collections.emptyList();
    }
    /*
    public static void main(String[] args) {
    ScriptParser p = new ScriptParser(new DefaultNotation());
    try {
    System.out.println( p.parse("(ur,lu,dl,rd)") );
    } catch (IOException ex) {
    ex.printStackTrace();
    //Logger.getLogger(DefaultNotation.class.getName()).log(Level.SEVERE, null, ex);
    }
    }*/
}

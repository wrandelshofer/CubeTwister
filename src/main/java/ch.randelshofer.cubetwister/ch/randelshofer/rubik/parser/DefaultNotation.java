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
 * DefaultNotation supports Superset ENG for the 3x3 cube.
 *
 * @author Werner Randelshofer
 * @version $Id$
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

        for (int i=1;i<=2;i++) {
            String suffix=i==1?"":"2";

            addMove(new Move(0, outer, 1*i), "R"+suffix);
            addMove(new Move(1, outer, 1*i), "U"+suffix);
            addMove(new Move(2, outer, 1*i), "F"+suffix);
            addMove(new Move(0, inner, -1*i), "L"+suffix);
            addMove(new Move(1, inner, -1*i), "D"+suffix);
            addMove(new Move(2, inner, -1*i), "B"+suffix);

            addMove(new Move(0, middle, 1*i), "MR"+suffix);
            addMove(new Move(1, middle, 1*i), "MU"+suffix);
            addMove(new Move(2, middle, 1*i), "MF"+suffix);
            addMove(new Move(0, middle, -1*i), "ML"+suffix);
            addMove(new Move(1, middle, -1*i), "MD"+suffix);
            addMove(new Move(2, middle, -1*i), "MB"+suffix);

            addMove(new Move(0, outer | middle, 1*i), "TR"+suffix);
            addMove(new Move(1, outer | middle, 1*i), "TU"+suffix);
            addMove(new Move(2, outer | middle, 1*i), "TF"+suffix);
            addMove(new Move(0, inner | middle, -1*i), "TL"+suffix);
            addMove(new Move(1, inner | middle, -1*i), "TD"+suffix);
            addMove(new Move(2, inner | middle, -1*i), "TB"+suffix);

            addMove(new Move(0, outer | inner, 1*i), "SR"+suffix);
            addMove(new Move(1, outer | inner, 1*i), "SU"+suffix);
            addMove(new Move(2, outer | inner, 1*i), "SF"+suffix);
            addMove(new Move(0, inner | outer, -1*i), "SL"+suffix);
            addMove(new Move(1, inner | outer, -1*i), "SD"+suffix);
            addMove(new Move(2, inner | outer, -1*i), "SB"+suffix);

            addMove(new Move(0, all, 1*i), "CR"+suffix);
            addMove(new Move(1, all, 1*i), "CU"+suffix);
            addMove(new Move(2, all, 1*i), "CF"+suffix);
            addMove(new Move(0, all, -1*i), "CL"+suffix);
            addMove(new Move(1, all, -1*i), "CD"+suffix);
            addMove(new Move(2, all, -1*i), "CB"+suffix);
        }

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

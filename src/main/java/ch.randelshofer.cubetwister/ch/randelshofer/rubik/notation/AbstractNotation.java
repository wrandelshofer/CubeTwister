/* @(#)AbstractNotation.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.parser.MacroNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractNotation implements Notation {
    private final HashMap<Symbol, List<String>> symbolToTokensMap = new HashMap<>();
    private final HashMap<String, List<Symbol>> tokenToSymbolsMap = new HashMap<>();
    private final HashMap<Move, List<String>> moveToTokensMap = new HashMap<>();
    private final HashMap<String, Move> tokenToMoveMap = new HashMap<>();
    private final HashMap<Symbol, Syntax> symbolToSyntaxMap = new HashMap<>();
    private final List<MacroNode> macros = new ArrayList<>();
    private int layerCount;

    protected void setLayerCount(int value) {
        this.layerCount = value;
    }

    @Override
    public int getLayerCount() {
        return layerCount;
    }

    @Override
    public String getEquivalentMacro(Cube cube, Map<String, MacroNode> localMacros) {
        // FIXME implement me
        return null;
    }

    @Override
    public List<MacroNode> getMacros() {
        return macros;
    }

    @Override
    public void writeToken(PrintWriter w, Symbol symbol) throws IOException {
        List<String> tokens = symbolToTokensMap.getOrDefault(symbol, Collections.emptyList());
        if (!tokens.isEmpty()) {
            w.print(tokens.get(0));
        }
    }

    @Override
    public void writeMoveToken(PrintWriter w, int axis, int layerMask, int angle) throws IOException {
        Move move = new Move(layerCount, axis, layerMask, angle);
        List<String> tokens = moveToTokensMap.getOrDefault(move, Collections.emptyList());
        if (!tokens.isEmpty()) {
            w.print(tokens.get(0));
        }
    }

    @Override
    public boolean isSupported(Symbol s) {
        return symbolToSyntaxMap.containsKey(s)
                || symbolToTokensMap.containsKey(s);
    }

    @Override
    public Syntax getSyntax(Symbol s) {
        return symbolToSyntaxMap.get(s.getCompositeSymbol());
    }

    @Override
    public boolean isToken(String token) {
        return tokenToSymbolsMap.containsKey(token);
    }

    @Override
    public boolean isTokenFor(String token, Symbol symbol) {
        return tokenToSymbolsMap.getOrDefault(token, Collections.emptyList()).contains(symbol);
    }

    @Override
    public String getToken(Symbol s) {
        List<String> tokens = symbolToTokensMap.getOrDefault(s, Collections.emptyList());
        return tokens.isEmpty() ? null : tokens.get(0);
    }

    @Override
    public String getToken(Move s) {
        List<String> tokens = moveToTokensMap.getOrDefault(s, Collections.emptyList());
        return tokens.isEmpty() ? null : tokens.get(0);
    }

    @Override
    public Symbol getSymbolFor(String token, Symbol compositeSymbol) {
        List<Symbol> symbols = tokenToSymbolsMap.getOrDefault(token, Collections.emptyList());
        for (Symbol s : symbols) {
            if (compositeSymbol.isSubSymbol(s)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public Move getMoveFromToken(String moveToken) {
        Move move = tokenToMoveMap.get(moveToken);
        if (move == null) {
            throw new IllegalArgumentException("Not a move token. token:" + moveToken);
        }
        return move;
    }

    @Override
    public Collection<String> getTokens() {
        return tokenToSymbolsMap.keySet();
    }

    @Override
    public List<Symbol> getSymbolsFor(String token) {
        return tokenToSymbolsMap.get(token);
    }


    protected void addToken(Symbol symbol, String token) {
        symbolToTokensMap.computeIfAbsent(symbol, k -> new ArrayList<>()).add(token);
        tokenToSymbolsMap.computeIfAbsent(token, k -> new ArrayList<>()).add(symbol);
    }

    protected void addMove(Move move, String token) {
        addToken(Symbol.MOVE, token);
        moveToTokensMap.computeIfAbsent(move, k -> new ArrayList<>()).add(token);
        tokenToMoveMap.put(token, move);
    }

    protected void putSyntax(Symbol symbol, Syntax syntax) {
        symbolToSyntaxMap.put(symbol, syntax);
    }


}

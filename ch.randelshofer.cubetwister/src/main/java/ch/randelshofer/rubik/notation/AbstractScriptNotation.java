/*
 * @(#)AbstractNotation.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.parser.ast.MacroNode;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractScriptNotation implements ScriptNotation {
    private final HashMap<Symbol, List<String>> symbolToTokensMap = new HashMap<>();
    private final HashMap<String, List<Symbol>> tokenToSymbolsMap = new HashMap<>();
    private final HashMap<Move, List<String>> moveToTokensMap = new HashMap<>();
    private final HashMap<String, Move> tokenToMoveMap = new HashMap<>();
    private final HashMap<Symbol, Syntax> symbolToSyntaxMap = new HashMap<>();
    private final Map<String, String> macros = new HashMap<>();
    private int layerCount;

    public void putMacro(String identifier, String code) {
        macros.put(identifier, code);
        tokenToSymbolsMap.computeIfAbsent(identifier, k -> new ArrayList<>()).add(Symbol.MACRO);
    }

    public void removeToken(Symbol symbol, String token) {
        symbolToTokensMap.computeIfAbsent(symbol, k -> new ArrayList<>()).remove(token);
        tokenToSymbolsMap.computeIfAbsent(token, k -> new ArrayList<>()).remove(symbol);
    }

    protected void setLayerCount(int value) {
        this.layerCount = value;
    }

    @Override
    public int getLayerCount() {
        return layerCount;
    }

    @Nullable
    @Override
    public String getEquivalentMacro(Cube cube, Map<String, MacroNode> localMacros) {
        // FIXME implement me
        return null;
    }

    @Nonnull
    @Override
    public Map<String, String> getAllMacros() {
        return macros;
    }

    @Override
    public void writeToken(Writer w, Symbol symbol) throws IOException {
        List<String> tokens = symbolToTokensMap.getOrDefault(symbol, Collections.emptyList());
        if (!tokens.isEmpty()) {
            w.write(tokens.get(0));
        }
    }

    @Override
    public void writeMoveToken(@Nonnull PrintWriter w, int axis, int layerMask, int angle) throws IOException {
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
    public boolean isMoveSupported(Move key) {
        return moveToTokensMap.containsKey(key);
    }

    @Nonnull
    @Override
    public Syntax getSyntax(@Nonnull Symbol s) {
        return symbolToSyntaxMap.getOrDefault(s.getCompositeSymbol(), Syntax.PRIMARY);
    }

    @Nullable
    @Override
    public String getToken(Symbol symbol) {
        List<String> tokens = symbolToTokensMap.getOrDefault(symbol, Collections.emptyList());
        return tokens.isEmpty() ? null : tokens.getFirst();
    }

    @Override
    public List<String> getAllTokens(Symbol key) {
        return symbolToTokensMap.getOrDefault(key, Collections.emptyList());
    }

    @Nullable
    @Override
    public String getMoveToken(Move s) {
        List<String> tokens = moveToTokensMap.getOrDefault(s, Collections.emptyList());
        return tokens.isEmpty() ? null : tokens.getFirst();
    }

    @Nullable
    @Override
    public List<String> getAllMoveTokens(Move s) {
        return moveToTokensMap.getOrDefault(s, Collections.emptyList());

    }

    @Override
    public @Nullable Move getMoveFromToken(String moveToken) {
        return tokenToMoveMap.get(moveToken);
    }

    @Nonnull
    @Override
    public Collection<String> getTokens() {
        return tokenToSymbolsMap.keySet();
    }

    @Override
    public Set<Move> getAllMoveSymbols() {
        return moveToTokensMap.keySet();
    }

    @Override
    public List<Symbol> getSymbols(String token) {
        return tokenToSymbolsMap.getOrDefault(token, Collections.emptyList());
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

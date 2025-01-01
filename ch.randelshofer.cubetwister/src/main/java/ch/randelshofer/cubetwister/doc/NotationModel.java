/*
 * @(#)NotationModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.rubik.parser.ast.MacroNode;
import ch.randelshofer.rubik.parser.ast.MoveNode;
import ch.randelshofer.undo.UndoableObjectEdit;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * NotationModel.
 *
 * @author Werner Randelshofer.
 */
public class NotationModel extends InfoModel implements ScriptNotation {
    @Serial
    private final static long serialVersionUID = 1L;
    public static final String PROP_STATEMENT_TOKEN = "StatementToken";
    public static final String PROP_TWIST_TOKEN = "TwistToken";
    public static final String PROP_SYNTAX = "Syntax";
    public static final String PROP_SYMBOL_SUPPORTED = "SymbolSupported";
    /**
     * Key = Move
     * Value = String
     */
    @Nonnull
    private SequencedMap<Move, String> moveToTokenMap = new LinkedHashMap<Move, String>();
    /**
     * Key = Symbol
     * Value = String
     */
    @Nonnull
    private SequencedMap<Symbol, String> symbolToTokenMap = new LinkedHashMap<Symbol, String>();
    /**
     * A set of supported symbols.
     */
    @Nonnull
    private SequencedSet<Symbol> supportedSymbols = new LinkedHashSet<Symbol>();
    /**
     * A set of supported twists.
     */
    @Nonnull
    private SequencedSet<Move> supportedTwists = new LinkedHashSet<Move>();
    /**
     * We create the token to symbol map lazily.
     * We set this variable to null, each time a token or a twist token of the notation changes.
     */
    @Nullable
    private SequencedMap<String, HashSet<Symbol>> tokenToSymbolMap = null;
    /**
     * We create the token to twist map lazily.
     * We set this variable to null, each time a token or a twist token of the notation changes.
     */
    @Nullable
    private SequencedMap<String, Move> tokenToTwistMap = null;
    /**
     * A map with symbol syntaxes.
     * Key = Symbol
     * Value = Syntax
     */
    @Nonnull
    private SequencedMap<Symbol, Syntax> symbolToSyntaxMap = new LinkedHashMap<Symbol, Syntax>();
    private final static int MACRO_INDEX = 0;
    /**
     * Number of layers supported by this notation.
     */
    private int layerCount = 3;
    private final static int[][] usefulLayers = {
            // 2x2 cube
            {0b1, 0b11},// Tier Moves
            // 3x3 cube
            {0b1, 0b11, 0b111,// Tier Moves
                    0b010, // Nth-Layer Moves width 1,
                    0b101 // Slice-Moves width 1
            },
            // 4x4 cube
            {0b1, 0b11, 0b111, 0b1111,// Tier Moves
                    0b0010, // Nth-Layer Moves width 1,
                    0b0110, // Nth-Layer Moves width 2,
                    0b1101, // Slice Moves width 1
                    0b1001 // Slice Moves width 2
            },
            // 5x5 cube
            {0b1, 0b11, 0b111, 0b1111, 0b11111,// Tier Moves
                    0b00010, 0b00100, // Nth-Layer Moves width 1,
                    0b00110, // Nth-Layer Moves width 2,
                    0b01110, // Nth-Layer Moves width 3,
                    0b11101, 0b11011, // Slice-Moves width 1
                    0b11001, // Slice Moves width 2
                    0b10001 // Slice Moves width 3
            },
            // 6x6 cube
            {1, 3, 7, 15, 31, 63,// Tier Moves
                    2, 4, // Nth-Layer Moves width 1,
                    6, 12, // Nth-Layer Moves width 2,
                    14, // Nth-Layer Moves width 3,
                    30, // Nth-Layer Moves width 4
                    61, 59, // Slice-Moves width 1
                    57, 51,// Slice Moves width 2
                    49, // Slice Moves width 3
                    33 // Slice Moves width 4
            },
            // 7x7 cube
            {1, 1 + 2, 7, 15, 31, 63, 127, // Tier Movies
                    2, 4, 8, // Nth-Layer Moves width 1
                    6, 12, // Nth-Layer Moves width 2,
                    14, 28, // Nth-Layer Moves width 3,
                    30, // Nth-Layer Moves width 4
                    62, // Nth-Layer Moves width 5
                    125, 123, 119, // Slice Moves width 1
                    121, 115, // Slice Moves width 2
                    113, 99, // Slice Moves width 3
                    97, // Slice Moves width 4
                    65 // Slice Moves width 5
            },};

    /**
     * Returns an array of 'useful' layerMask values for cubes with the specified
     * layer count.
     * This layerMask does not include mirrored layers.
     */
    public static int[] getUsefulLayers(int layerCount) {
        return usefulLayers[layerCount - 2];
    }

    /**
     * Returns a reversed (mirrored) layerMask for cubes with the specified
     * layer count.
     *
     * @param layerMask
     * @param layerCount
     * @return reversed layer mask.
     */
    public static int reverseLayerMask(int layerMask, int layerCount) {
        int reverse = 0;
        for (int i = 0; i < layerCount; i++) {
            reverse = (reverse << 1) | (layerMask & 1);
            layerMask >>= 1;
        }
        return reverse;
    }

    /**
     * Creates new CubeNotation
     */
    public NotationModel() {
        setAllowsChildren(true);

        setSyntax(Symbol.GROUPING, Syntax.CIRCUMFIX);

        add(new EntityModel("Macros", true)); // Macros
    }

    /**
     * /**
     * Returns the number of layers supported by this notation.
     */
    @Override
    public int getLayerCount() {
        return layerCount;
    }

    /**
     * Returns the number of layers supported by this notation.
     */
    public void setLayerCount(int newValue) {
        int oldValue = layerCount;
        layerCount = newValue;
        firePropertyChange("layerCount", oldValue, newValue);

        invalidateTwists();
    }

    @Nonnull
    @Override
    public Map<String, String> getAllMacros() {
        Map<String, String> macros = new LinkedHashMap<>();
        EntityModel macroModels = getMacroModels();
        for (int i = 0, n = macroModels.getChildCount(); i < n; i++) {
            MacroModel mm = (MacroModel) macroModels.getChildAt(i);
            StringTokenizer st = new StringTokenizer(mm.getIdentifier());
            while (st.hasMoreTokens()) {
                macros.put(st.nextToken(), mm.getScript());
            }
        }
        return macros;
    }

    @Nonnull
    public EntityModel getMacroModels() {
        return getChildAt(MACRO_INDEX);
    }

    public boolean isMoveSupported() {
        // Always return true;
        return true;
        //return supportedTwistTokensSet.size() > 0;
    }

    public boolean isMoveSupported(Move key) {
        return supportedTwists.contains(key);
    }

    public void basicSetMoveSupported(Move key, boolean newValue) {
        if (newValue) {
            supportedTwists.add(key);
        } else {
            supportedTwists.remove(key);
        }
    }

    public void setMoveSupported(Move key, boolean newValue) {
        boolean oldValue = supportedTwists.contains(key);
        if (oldValue != newValue) {
            basicSetMoveSupported(key, newValue);
            firePropertyChange("twistSupported", oldValue, newValue);
            // XXX - Implement undo redo support
        }
    }

    public void basicSetSupported(Symbol key, boolean newValue) {
        if (newValue) {
            supportedSymbols.add(key);
        } else {
            supportedSymbols.remove(key);
        }
    }

    public void setSupported(Symbol key, boolean newValue) {
        boolean oldValue = supportedSymbols.contains(key);
        if (oldValue != newValue) {
            basicSetSupported(key, newValue);
            firePropertyChange(PROP_SYMBOL_SUPPORTED, oldValue, newValue);
            // XXX - Implement undo redo support
        }
    }

    @Nonnull
    public Set<Move> getAllMoveSymbols() {
        return moveToTokenMap.keySet();
    }

    @Nullable
    @Override
    public String getMoveToken(Move s) {
        String tokens = moveToTokenMap.get(s);
        if (tokens == null || tokens.length() == 0) {
            return null;
        } else {
            int p = tokens.indexOf(' ');
            if (p != -1) {
                return tokens.substring(0, p);
            } else {
                return tokens;
            }
        }
    }

    public List<String> getAllMoveTokens(Move s) {
        String tokens = moveToTokenMap.get(s);
        return tokens == null ? Collections.emptyList() : Arrays.asList(tokens.split(" +"));
    }

    public String getAllMoveTokensAsString(Move key) {
        return moveToTokenMap.get(key);
    }

    public void setAllMoveTokens(final Move key, @Nullable String newValue) {
        String oldValue = moveToTokenMap.get(key);
        basicSetMoveToken(key, newValue);
        if (oldValue != newValue &&
                (oldValue != null && newValue != null && !oldValue.equals(newValue))) {
            firePropertyChange(PROP_TWIST_TOKEN, oldValue, newValue);

            fireUndoableEditHappened(
                    new UndoableObjectEdit(this, "Twist Token", oldValue, newValue) {
                        private final static long serialVersionUID = 1L;

                        public void revert(Object a, Object b) {
                            moveToTokenMap.put(key, (String) b);
                            firePropertyChange(PROP_TWIST_TOKEN, a, b);
                        }
                    });
        }
    }

    public void basicSetMoveToken(final Move key, String newValue) {
        invalidateTokenMaps();
        moveToTokenMap.put(key, newValue);
    }

    public void basicSetToken(final Symbol key, String newValue) {
        invalidateTokenMaps();
        symbolToTokenMap.put(key, newValue);
    }

    public void setAllTokens(final Symbol key, String newValue) {
        invalidateTokenMaps();
        String oldValue = symbolToTokenMap.get(key);
        basicSetToken(key, newValue);
        if (oldValue != null && !oldValue.equals(newValue)) {
            firePropertyChange(PROP_STATEMENT_TOKEN, oldValue, newValue);

            // FIXME - Undo/Redo must be handled by the view and not by the model!!
            fireUndoableEditHappened(
                    new UndoableObjectEdit(this, "Statement Token", oldValue, newValue) {
                        private final static long serialVersionUID = 1L;

                        public void revert(Object a, Object b) {
                            symbolToTokenMap.put(key, (String) b);
                            basicSetToken(key, (String) b);
                            firePropertyChange(PROP_STATEMENT_TOKEN, a, b);
                        }
                    });
        }
    }
    /*
    public ScriptParser getParser(Collection localMacros) {
    return new ScriptParser(this, localMacros);
    }*/

    @Nonnull
    public ScriptParser getParser(@Nonnull List<MacroNode> localMacros) {
        return new ScriptParser(this, localMacros);
    }

    /**
     * Returns true if the node may be removed from its parent.
     */
    @Override
    public boolean isRemovable() {
        return super.isRemovable();
    /* Only return true, if notation is not the default
    if (getDocument() == null || getDocument().getDefaultNotation(getLayerCount()) != this) {
    return super.isRemovable();
    } else {
    return false;
    }*/
    }

    /**
     * Returns a macro which performs the same transformation as the cube
     * parameter. Returns null if no macro is available.
     *
     * @param cube        A transformed cube.
     * @param localMacros A Map with local macros.
     */
    @Nullable
    @Override
    public String getEquivalentMacro(Cube cube, Map<String, MacroNode> localMacros) {
        // XXX - Implement me
        return null;
    }

    /**
     * Writes a token for the specified symbol to the print writer.
     *
     * @throws IOException If the symbol is not supported by the notation,
     *                     and if no alternative symbols could be found.
     */
    @Override
    public void writeToken(Writer w, Symbol symbol) throws IOException {
        String str = symbolToTokenMap.get(symbol);
        if (str == null) {
            throw new IOException("No token for " + symbol);
        }
        String[] tokens = str.split("\\w+", 1);
        w.write(tokens[0]);
    }

    /**
     * Writes a token for the specified transformation to the print writer.
     */
    @Override
    public void writeMoveToken(PrintWriter w, int axis, int layerMask, int angle)
            throws IOException {
        // XXX - Implement me
    }

    /**
     * Returns true, if this notation supports the specified symbol.
     */
    @Override
    public boolean isSupported(Symbol s) {
        return supportedSymbols.contains(s);
    }

    /**
     * Returns the syntax for the specified symbol.
     * Note: This makes only sense for composite symbols.
     */
    @Nonnull
    @Override
    public Syntax getSyntax(Symbol s) {
        return symbolToSyntaxMap.getOrDefault(s.getCompositeSymbol(), Syntax.PRIMARY);
    }

    public void basicSetSyntax(Symbol s, Syntax newValue) {
        symbolToSyntaxMap.put(s, newValue);
    }

    /**
     * Sets the syntax for the specified symbol.
     * Note: This makes only sense for composite symbols.
     */
    public void setSyntax(Symbol s, Syntax newValue) {
        Syntax oldValue = symbolToSyntaxMap.get(s);
        basicSetSyntax(s, newValue);
        firePropertyChange("Syntax", oldValue, newValue);
    }

    /**
     * Returns a token for the specified symbol.
     * If the symbol has more than one token, the first token is returned.
     * <p>
     * Returns null, if symbol is not supported.
     */
    @Nullable
    public String getToken(Symbol key) {
        String str = symbolToTokenMap.get(key);
        return (str == null || str.trim().length() == 0) ? null : str.split(" ")[0];
    }

    /**
     * Returns all token for the specified symbol.
     * <p>
     * Returns the token regardless whether the symbol is supported or not.
     * Returns null if the token is not defined.
     */
    public String getAllTokensAsString(Symbol key) {
        String str = symbolToTokenMap.get(key);
        return str;
    }

    @Override
    public List<String> getAllTokens(Symbol key) {
        String str = getAllTokensAsString(key);
        return str == null ? List.of() : Arrays.asList(str.split("\\s+"));
    }

    @Override
    public @Nullable Move getMoveFromToken(String moveToken) {
        return getTokenToTwistMap().get(moveToken);
    }

    @Nonnull
    @Override
    public Collection<String> getTokens() {
        return getTokenToSymbolMap().keySet();
    }

    @Nonnull
    @Override
    public List<Symbol> getSymbols(String token) {
        return new ArrayList<>(tokenToSymbolMap.get(token));
    }

    /**
     * Configures a MoveNode from the specified twist token.
     */
    public void configureMoveFromToken(@Nonnull MoveNode twist, String moveToken) {
        Move tw = getTokenToTwistMap().get(moveToken);
        if (tw != null) {
            twist.setAxis(tw.getAxis());
            twist.setAngle(tw.getAngle());
            twist.setLayerMask(tw.getLayerMask());
        } else {
            throw new IllegalArgumentException("unknown twist token " + moveToken);
        /*
        twist.setAxis(0);
        twist.setAngle(0);
        twist.setLayerMask(0);
         */
        }
    }

    @Nonnull
    public NotationModel clone() {
        NotationModel that = (NotationModel) super.clone();

        EntityModel macros = new EntityModel("Macros", true);
        for (EntityModel child : this.getChildAt(MACRO_INDEX).getChildren()) {
            macros.add((MacroModel) child.clone());
        }
        that.add(macros);

        that.moveToTokenMap = new LinkedHashMap<Move, String>(this.moveToTokenMap);
        that.symbolToTokenMap = new LinkedHashMap<Symbol, String>(this.symbolToTokenMap);
        that.supportedSymbols = new LinkedHashSet<Symbol>(this.supportedSymbols);
        that.supportedTwists = new LinkedHashSet<Move>(this.supportedTwists);
        that.invalidateTokenMaps();
        return that;
    }


    @Nullable
    private Map<String, Move> getTokenToTwistMap() {
        validateTokenMaps();
        return tokenToTwistMap;

    }

    @Nullable
    private Map<String, HashSet<Symbol>> getTokenToSymbolMap() {
        validateTokenMaps();
        return tokenToSymbolMap;
    }

    private void invalidateTokenMaps() {
        tokenToSymbolMap = null;
        tokenToTwistMap = null;
    }

    private void validateTokenMaps() {
        if (tokenToSymbolMap == null) {
            tokenToSymbolMap = new LinkedHashMap<String, HashSet<Symbol>>();
            for (Map.Entry<Symbol, String> entry : symbolToTokenMap.entrySet()) {
                if (entry.getValue() != null) {
                    for (StringTokenizer tt = new StringTokenizer(entry.getValue()); tt.hasMoreTokens(); ) {
                        String token = tt.nextToken();
                        HashSet<Symbol> symbols = tokenToSymbolMap.get(token);
                        if (symbols == null) {
                            symbols = new HashSet<Symbol>();
                            tokenToSymbolMap.put(token, symbols);
                        }
                        symbols.add(entry.getKey());
                    }
                }
            }
            tokenToTwistMap = new LinkedHashMap<String, Move>();
            int validMask = (1 << layerCount) - 1;
            for (Map.Entry<Move, String> entry : moveToTokenMap.entrySet()) {
                if (entry.getValue() != null &&
                        (entry.getKey().getLayerMask() & validMask) == entry.getKey().getLayerMask()) {
                    for (StringTokenizer tt = new StringTokenizer(entry.getValue()); tt.hasMoreTokens(); ) {
                        String token = tt.nextToken();
                        HashSet<Symbol> symbols = tokenToSymbolMap.get(token);
                        if (symbols == null) {
                            symbols = new HashSet<Symbol>();
                            tokenToSymbolMap.put(token, symbols);
                        }
                        symbols.add(Symbol.MOVE);
                        tokenToTwistMap.put(token, entry.getKey());
                    }
                }
            }
        }
    }

    private void invalidateTwists() {
        invalidateTokenMaps();

        moveToTokenMap.clear();
        supportedTwists.clear();
    }

    public boolean isDefaultNotation() {
        return getDocument().getDefaultNotation(getLayerCount()) == this;
    }
}


/* @(#)CubeNotation.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.parser.MacroNode;
import ch.randelshofer.rubik.parser.MoveNode;
import ch.randelshofer.rubik.parser.ScriptParser;
import ch.randelshofer.undo.UndoableObjectEdit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * NotationModel.
 *
 * @author  Werner Randelshofer.
 */
public class NotationModel extends InfoModel implements Notation {
    private final static long serialVersionUID = 1L;
    public static final String PROP_STATEMENT_TOKEN = "StatementToken";
    public static final String PROP_TWIST_TOKEN = "TwistToken";
    public static final String PROP_SYNTAX = "Syntax";
    public static final String PROP_SYMBOL_SUPPORTED = "SymbolSupported";
    /**
     * Key = Move
     * Value = String
     */
    private HashMap<Move, String> twistToTokenMap = new HashMap<Move, String>();
    /**
     * Key = Symbol
     * Value = String
     */
    private HashMap<Symbol, String> symbolToTokenMap = new HashMap<Symbol, String>();
    /**
     * A set of supported symbols.
     */
    private HashSet<Symbol> supportedSymbols = new HashSet<Symbol>();
    /**
     * A set of supported twists.
     */
    private HashSet<Move> supportedTwists = new HashSet<Move>();
    /**
     * We create the token to symbol map lazily.
     * We set this variable to null, each time a token or a twist token of the notation changes.
     */
    private HashMap<String, HashSet<Symbol>> tokenToSymbolMap = null;
    /**
     * We create the token to twist map lazily.
     * We set this variable to null, each time a token or a twist token of the notation changes.
     */
    private HashMap<String, Move> tokenToTwistMap = null;
    /**
     * A map with symbol syntaxes.
     * Key = Symbol
     * Value = Syntax
     */
    private HashMap<Symbol, Syntax> symbolToSyntaxMap = new HashMap<Symbol, Syntax>();
    private final static int MACRO_INDEX = 0;
    /**
     * Number of layers supported by this notation.
     */
    private int layerCount = 3;
    private final static int[][] usefulLayers = {
        // 2x2 cube
        {1, 3},
        // 3x3 cube
        {1, 3, 7, 2, 5},
        // 4x4 cube
        {1, 3, 7, 15, 2, 6, 9},
        // 5x5 cube
        {1, 3, 7, 15, 31, 2, 4, 6, 14, 29, 27, 25, 17},
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
        {1, 3, 7, 15, 31, 63, 127, // Tier Movies
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

    /** Returns an array of 'useful' layerMask values for cubes with the specified
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

    /** Creates new CubeNotation */
    public NotationModel() {
        setAllowsChildren(true);
        //     tokens = new String[ScriptParser.TOKEN_COUNT];
        add(new EntityModel("Macros", true)); // Macros
    }

    /**
    
    /**
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

        validateTwists();
    }

    @Override
    public Map<String, String> getMacros() {
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

    public EntityModel getMacroModels() {
        return getChildAt(MACRO_INDEX);
    }

    public boolean isTwistSupported() {
        // Always return true;
        return true;
    //return supportedTwistTokensSet.size() > 0;
    }

    public boolean isTwistSupported(Move key) {
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

    public Set<Move> getAllMoveSymbols() {
        return twistToTokenMap.keySet();
    }

    public String getMoveToken(Move key) {
        return twistToTokenMap.get(key);
    }

    public String getAllTwistTokens(Move key) {
        return twistToTokenMap.get(key);
    }

    public void setMoveToken(final Move key, String newValue) {
        String oldValue = twistToTokenMap.get(key);
        basicSetMoveToken(key, newValue);
        if (oldValue != newValue &&
                (oldValue != null && newValue != null && !oldValue.equals(newValue))) {
            firePropertyChange(PROP_TWIST_TOKEN, oldValue, newValue);

            // FIXME - Undo/Redo must be handled by the view and not by the model!!
            fireUndoableEditHappened(
                    new UndoableObjectEdit(this, "Twist Token", oldValue, newValue) {
    private final static long serialVersionUID = 1L;

                        public void revert(Object a, Object b) {
                            twistToTokenMap.put(key, (String) b);
                            firePropertyChange(PROP_TWIST_TOKEN, a, b);
                        }
                    });
        }
    }

    public void basicSetMoveToken(final Move key, String newValue) {
        invalidateTokenMaps();
        twistToTokenMap.put(key, newValue);
    }

    public void basicSetToken(final Symbol key, String newValue) {
        invalidateTokenMaps();
        symbolToTokenMap.put(key, newValue);
    }

    public void setToken(final Symbol key, String newValue) {
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

    public ScriptParser getParser(List<MacroNode> localMacros) {
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
     * @param cube A transformed cube.
     * @param localMacros A Map with local macros.
     */
    @Override
    public String getEquivalentMacro(Cube cube, Map<String,MacroNode> localMacros) {
        // XXX - Implement me
        return null;
    }

    /**
     * Writes a token for the specified symbol to the print writer.
     *
     * @exception IOException If the symbol is not supported by the notation,
     * and if no alternative symbols could be found.
     */
    @Override
    public void writeToken(PrintWriter w, Symbol symbol) throws IOException {
        String str=symbolToTokenMap.get(symbol);
        if (str==null) {
            throw new IOException("No token for "+symbol);
        }
        String[] tokens=str.split("\\w+",1);
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
    @Override
    public Syntax getSyntax(Symbol s) {
        return symbolToSyntaxMap.getOrDefault(s, Syntax.PRIMARY);
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
     *
     * Returns null, if symbol is not supported.
     */
    public String getToken(Symbol key) {
        String str = symbolToTokenMap.get(key);
        return (str == null || str.trim().length() == 0) ? null : str.split(" ")[0];
    }

    /**
     * Returns all token for the specified symbol.
     *
     * Returns the token regardless whether the symbol is supported or not.
     * Returns null if the token is not defined.
     */
    public String getAllTokens(Symbol key) {
        String str = symbolToTokenMap.get(key);
        return str;
    }

    @Override
    public Move getMoveFromToken(String moveToken) {
        return getTokenToTwistMap().get(moveToken);
    }

    @Override
    public Collection<String> getTokens() {
       return getTokenToSymbolMap().keySet();
    }

    @Override
    public List<Symbol> getSymbolsFor(String token) {
       return new ArrayList<>(tokenToSymbolMap.get(token));
    }

    /**
     * Configures a MoveNode from the specified twist token.
     */
    public void configureMoveFromToken(MoveNode twist, String moveToken) {
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

    public NotationModel clone() {
        NotationModel that = (NotationModel) super.clone();

        EntityModel macros = new EntityModel("Macros", true);
        for (EntityModel child : this.getChildAt(MACRO_INDEX).getChildren()) {
            macros.add((MacroModel) child.clone());
        }
        that.add(macros);

        that.twistToTokenMap = new HashMap<Move, String>(this.twistToTokenMap);
        that.symbolToTokenMap = new HashMap<Symbol, String>(this.symbolToTokenMap);
        that.supportedSymbols = new HashSet<Symbol>(this.supportedSymbols);
        that.supportedTwists = new HashSet<Move>(this.supportedTwists);
        that.invalidateTokenMaps();
        return that;
    }

    @Override
    public String getToken(Move s) {
        String tokens = twistToTokenMap.get(s);
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

    private Map<String, Move> getTokenToTwistMap() {
        validateTokenMaps();
        return tokenToTwistMap;

    }

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
            tokenToSymbolMap = new HashMap<String, HashSet<Symbol>>();
            for (Map.Entry<Symbol, String> entry : symbolToTokenMap.entrySet()) {
                if (entry.getValue() != null) {
                    for (StringTokenizer tt = new StringTokenizer(entry.getValue()); tt.hasMoreTokens();) {
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
            tokenToTwistMap = new HashMap<String, Move>();
            int validMask = (1 << layerCount) - 1;
            for (Map.Entry<Move, String> entry : twistToTokenMap.entrySet()) {
                if (entry.getValue() != null &&
                        (entry.getKey().getLayerMask() & validMask) == entry.getKey().getLayerMask()) {
                    for (StringTokenizer tt = new StringTokenizer(entry.getValue()); tt.hasMoreTokens();) {
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

    private void validateTwists() {
        invalidateTokenMaps();

        twistToTokenMap.clear();
        supportedTwists.clear();
    }

    public boolean isDefaultNotation() {
        return getDocument().getDefaultNotation(getLayerCount()) == this;
    }
}


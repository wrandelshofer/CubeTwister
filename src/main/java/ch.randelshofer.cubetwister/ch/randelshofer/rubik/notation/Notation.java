/* @(#)Notation.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.notation;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.parser.MacroNode;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * Defines the syntax and tokens of a rubik's cube script.
 *
 * @author  Werner Randelshofer
 */
public interface Notation {
    /**
     * Returns the number of layers supported by this notation.
     */
    int getLayerCount();
    
    /**
     * Returns a macro which performs the same transformation as the cube 
     * parameter. Returns null if no macro is available.
     *
     * @param cube A transformed cube.
     * @param localMacros A Map with local macros.
     * @return equivalent macro or null
     */
    String getEquivalentMacro(Cube cube, Map<String, MacroNode> localMacros);

    /**
     * Returns the macros defined by this notation.
     * 
     * @return macros.
     */
    Map<String, String> getMacros();

    default String getMacro(String identifier) {
        return getMacros().get(identifier);
    }

    String getName();

    /**
     * Writes a token for the specified symbol to the writer.
     *
     * @throws IOException If the symbol is not supported by the notation,
     *                     and if no alternative symbols could be found.
     */
    void writeToken(Writer w, Symbol symbol) throws IOException;
    
    /**
     * Writes a token for the specified transformation to the print writer.
     */
    void writeMoveToken(PrintWriter w, int axis, int layerMask, int angle) throws IOException;

    /**
     * Returns true, if this notation supports the specified symbol.
     */
    boolean isSupported(Symbol s);
    
    /**
     * Returns the syntax for the specified symbol.
     */
    @Nonnull
    Syntax getSyntax(Symbol s);

    /**
     * Returns a token for the specified symbol.
     * If the symbol has more than one token, the first token is returned.
     *
     * Returns null, if symbol is not supported.
     */
    String getToken(Symbol s);
    /**
     * Returns a token for the specified move.
     * If the move has more than one token, the first token is returned.
     *
     * Returns null, if move is not supported.
     */
    String getMoveToken(Move s);

    /**
     * Returns the a move from the given move token.
     * @return a move
     */
    Move getMoveFromToken(String moveToken);

    /**
     * Gets all tokens defined for this notation.
     *
     * @return the tokens.
     */
    Collection<String> getTokens();

    /**
     * Given a (potentially ambiguous) token returns all symbols for
     * that token.
     *
     * @param token a token
     * @return the symbols for the token
     */
    List<Symbol> getSymbols(String token);

    /**
     * Given a (potentially ambiguous) token and a composite symbol
     * that parser is currently parsing, returns the symbol for
     * that token.
     *
     * @param token           a token
     * @param compositeSymbol the composite symbol being parsed
     * @return the symbol for the token in this composite symbol
     */
    @Nullable
    default Symbol getSymbolInCompositeSymbol(String token, @Nonnull Symbol compositeSymbol) {
        for (Symbol s : getSymbols(token)) {
            if (compositeSymbol.isSubSymbol(s)) {
                return s;
            }
        }
        return null;
    }
}

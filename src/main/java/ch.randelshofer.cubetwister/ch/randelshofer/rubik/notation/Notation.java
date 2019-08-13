/* @(#)Notation.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.notation;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.rubik.parser.MacroNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
    List<MacroNode> getMacros();

    default String getMacro(String identifier) {
        for (MacroNode macro : getMacros()) {
            if (identifier.equals(macro.getIdentifier())) return macro.getScript();
        }
        return null;
    }

    String getName();

    /**
     * Writes a token for the specified symbol to the print writer.
     *
     * @exception IOException If the symbol is not supported by the notation, 
     * and if no alternative symbols could be found.
     */
    void writeToken(PrintWriter w, Symbol symbol) throws IOException;
    
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
    String getToken(Move s);

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

    List<Symbol> getSymbolsFor(String token);

    default Symbol getSymbolFor(String token, Symbol compositeSymbol) {
        for (Symbol s : getSymbolsFor(token)) {
            if (compositeSymbol.isSubSymbol(s)) {
                return s;
            }
        }
        return null;
    }
}

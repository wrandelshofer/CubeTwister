/* @(#)Notation.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import java.util.*;
import java.io.*;
/**
 * Notation describes the tokens and syntax used by the Parser.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>4.0 2008-01-03 Migrated from JDK 1.1 to 1.4.
 * <br>3.0 2007-06-16 Renamed "twist" to "move". 
 * <br>2.0 2005-01-31 Reworked.
 * <br>1.0  31 January 2005  Created.
 */
public interface Notation {
    /**
     * Returns the number of layers supported by this notation.
     */
    public int getLayerCount();
    
    /**
     * Returns a macro which performs the same transformation as the cube 
     * parameter. Returns null if no macro is available.
     *
     * @param cube A transformed cube.
     * @param localMacros A Map with local macros.
     * @return equivalent macro or null
     */
    public String getEquivalentMacro(Cube cube, Map<String,MacroNode> localMacros);

    /**
     * Returns the macros defined by this notation.
     * 
     * @return macros.
     */
    public List<MacroNode> getMacros();
    
    /**
     * Writes a token for the specified symbol to the print writer.
     *
     * @exception IOException If the symbol is not supported by the notation, 
     * and if no alternative symbols could be found.
     */
    public void writeToken(PrintWriter w, Symbol symbol) throws IOException;
    
    /**
     * Writes a token for the specified transformation to the print writer.
     */
    public void writeToken(PrintWriter w, int axis, int layerMask, int angle) throws IOException;
    
    /**
     * Returns true, if this notation supports the specified symbol.
     */
    public boolean isSupported(Symbol s);
    
    /**
     * Returns the syntax for the specified symbol.
     * Note: This makes only sense for composite symbols.
     */
    public Syntax getSyntax(Symbol s);

    /**
     * Returns true, if the specified String is a token of this notation.
     */
    public boolean isToken(String token);
    
    /**
     * Returns true, if the specified String is a token for the specified symbol.
     */
    public boolean isTokenFor(String token, Symbol symbol);
    
    /**
     * Returns a token for the specified symbol.
     * If the symbol has more than one token, the first token is returned.
     *
     * Returns null, if symbol is not supported.
     */
    public String getToken(Symbol s);
    /**
     * Returns a token for the specified move.
     * If the move has more than one token, the first token is returned.
     *
     * Returns null, if move is not supported.
     */
    public String getToken(Move s);
    
    /**
     * Returns a symbol for the specified token.
     * The compositeSymbol must be specified do disambiguate tokens.
     * If the compositeSymbol is null, then the token must be unambiguous. 
     *
     * Returns null, if the token is not a token for the specified compositeSymbol.
     */
    public Symbol getSymbolFor(String token, Symbol compositeSymbol);
    
    /**
     * Configures a MoveNode from the specified move token.
     */
    public void configureMoveFromToken(MoveNode move, String twistToken);
}

/* @(#)CubeMarkupNotation.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.Cube;
import ch.randelshofer.xml.XMLPreorderIterator;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * CubeMarkupNotation reads its notation definition from a CubeMarkup XML 3 file.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>3.2 2009-01-04 Disable permutation support if tokens are missing.
 * <br>3.1 2009-01-03 Added "getName" method.
 * <br>3.0.1 2008-08-25 Notation may support up to 32 layers instead of
 * only 5. 
 * <br>3.0 2008-01-03 Migrated from JDK 1.1 to 1.4. 
 * <br>2.1 2007-09-10 Updated method isTokenFor.
 * <br>1.0  05 February 2005  Created.
 */
public class CubeMarkupNotation implements Notation {

    private static class SymbolInfo {

        Symbol symbol;
        boolean isSupported;
        Syntax syntax;
        String[] tokens;

        public SymbolInfo(Symbol symbol, boolean isSupported, Syntax syntax, String[] tokens) {
            this.symbol = symbol;
            this.isSupported = isSupported;
            this.syntax = syntax;
            this.tokens = tokens;
        }

        public SymbolInfo(Symbol symbol, boolean isSupported, Syntax syntax) {
            this(symbol, isSupported, syntax, null);
        }
    }
    /**
     * This map holds all the symbol infos.
     *
     * Key: Symbol
     * Value: SymbolInfo
     */
    private HashMap<Symbol,SymbolInfo> symbolToInfoMap;
    /**
     * This map is used for parsing a script. Or more precisely for
     * associating tokens to symbols.
     * Tokens can be ambiguous. Therefore a token can have multiple symbols.
     *
     * Key: String
     * Value: ArrayList<Symbol>
     */
    private HashMap<String,ArrayList<Symbol>> tokenToSymbolMap;
    /**
     * This map is used for associating global macro identifiers of the notation
     * to macro scripts.
     *
     * Key: String
     * Value: String
     */
    private HashMap<String,String> identifierToMacroMap;
    /**
     * This map is used for pretty printing a MoveNode (aka a parsed twist).
     *
     * Key: Move
     * Value: String
     */
    private HashMap<Move,String[]> moveToTokenMap;
    /**
     * This map is used for associationg a move token to a MoveNode.
     *
     * Key: String
     * Value: Move
     */
    private HashMap<String,Move> tokenToMoveMap;
    /**
     * Name of the notation.
     */
    private String name;
    /**
     * Description of the notation.
     */
    private String description;
    /**
     * The cube for which this notation shall be used.
     */
    private Cube cube;
    private int layerCount = 3;

    /** Creates a new instance. */
    public CubeMarkupNotation(Cube cube) {
        this.cube = cube;
        symbolToInfoMap = new HashMap<Symbol,SymbolInfo>();
        identifierToMacroMap = new HashMap<String,String>();
    }

    /**
     * Configures this CubeMarkupNotation by the default Notation
     * found in the XMLElement.
     * The CubeMarkup 4 DTD must be used.
     */
    public void readXML(XMLElement doc) throws XMLParseException {
        // Find the first notation element.
        XMLElement elem = null;
        XMLPreorderIterator i = new XMLPreorderIterator(doc);
        boolean found = false;
        Search:
        while (i.hasNext()) {
            elem = (XMLElement) i.next();
            if ("Notation".equals(elem.getName()) 
                    && "true".equals(elem.getAttribute("default", "false"))) {
                found = true;
                break Search;
            }
        }
        if (found) {
            readNotationXMLElement(elem);
        } else {
            throw new XMLParseException("Notation", "No notation found.");
        }
    }

    /**
     * Configures this CubeMarkupNotation by the first Notation element
     * found in the XMLElement which has the specified notation name.
     *
     * The CubeMarkup 4 DTD must be used.
     */
    public void readXML(XMLElement doc, String notationName) throws XMLParseException {
        // Find the first notation element.
        XMLElement elem = null;
        XMLPreorderIterator i = new XMLPreorderIterator(doc);
        boolean found = false;
        Search:
        while (i.hasNext()) {
            elem = (XMLElement) i.next();
            if ("Notation".equals(elem.getName())) {
                for (Iterator j = elem.getChildren().iterator(); j.hasNext();) {
                    XMLElement notationElem = (XMLElement) j.next();
                    if ("Name".equals(notationElem.getName())) {
                        if (notationElem.getContent().equals(notationName)) {
                            found = true;
                            break Search;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (found) {
            readNotationXMLElement(elem);
        } else {
            throw new XMLParseException("Notation", "No notation found with name '" + notationName + "'");
        }
    }

    /**
     * Configures this CubeMarkupNotation using the specified Notation element
     *
     * The CubeMarkup 4 DTD must be used.
     */
    private void readNotationXMLElement(XMLElement notationElem) throws XMLParseException {
        // Name
        name = notationElem.getStringAttribute("name");

        // Initialize syntax value set
        HashMap<String,Syntax> syntaxValueSet = new HashMap<String,Syntax>();
        syntaxValueSet.put("precircumfix", Syntax.PRECIRCUMFIX);
        syntaxValueSet.put("postcircumfix", Syntax.POSTCIRCUMFIX);
        syntaxValueSet.put("prefix", Syntax.PREFIX);
        syntaxValueSet.put("suffix", Syntax.SUFFIX);

        // Axis value set
        HashMap<String,Integer> axisValueSet = new HashMap<String,Integer>();
        axisValueSet.put("x", 0);
        axisValueSet.put("y", 1);
        axisValueSet.put("z", 2);

        // Angle value set
        HashMap<String,Integer> angleValueSet = new HashMap<String,Integer>();
        angleValueSet.put("-180", -2);
        angleValueSet.put("-90", -1);
        angleValueSet.put("90", 1);
        angleValueSet.put("180", 2);

        // Initialize symbol to info map
        symbolToInfoMap = new HashMap<Symbol,SymbolInfo>();
        HashMap<String,Symbol> symbolValueSet = new HashMap<String,Symbol>();
        for (Symbol symbol : Symbol.values()) {
            symbolToInfoMap.put(symbol, new SymbolInfo(symbol, true, null));
            symbolValueSet.put(symbol.getName(), symbol);
            if (symbol.getAlternativeName() != null) {
                symbolValueSet.put(symbol.getAlternativeName(), symbol);
            }
        }
        symbolToInfoMap.put(Symbol.COMMUTATION, new SymbolInfo(Symbol.COMMUTATION, true, Syntax.PREFIX));
        symbolToInfoMap.put(Symbol.CONJUGATION, new SymbolInfo(Symbol.CONJUGATION, true, Syntax.PREFIX));
        symbolToInfoMap.put(Symbol.INVERSION, new SymbolInfo(Symbol.INVERSION, true, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.PERMUTATION, new SymbolInfo(Symbol.PERMUTATION, true, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.REFLECTION, new SymbolInfo(Symbol.REFLECTION, true, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.REPETITION, new SymbolInfo(Symbol.REPETITION, true, Syntax.SUFFIX));

        // Initialize move to token map
        moveToTokenMap = new HashMap<Move,String[]>();

        // Read layer count
        layerCount = notationElem.getIntAttribute("layerCount", 2, 32, 3);

        // Read notation constructs
        for (Iterator i = notationElem.getChildren().iterator(); i.hasNext();) {
            XMLElement elem = (XMLElement) i.next();
            String name = elem.getName();
            if ("Statement".equals(name)) {
                Symbol sym = elem.getAttribute("symbol", symbolValueSet, "move", false);
                SymbolInfo info = symbolToInfoMap.get(sym);
                info.isSupported = elem.getBooleanAttribute("enabled", true);
                info.syntax = elem.getAttribute("syntax", syntaxValueSet, "suffix", false);
                for (Iterator i2 = elem.getChildren().iterator(); i2.hasNext();) {
                    XMLElement elem2 = (XMLElement) i2.next();
                    String name2 = elem2.getName();
                    if ("Token".equals(name2)) {
                        Symbol sym2 = elem2.getAttribute("symbol", symbolValueSet, "move", false);
                        if (sym2 == Symbol.MOVE) {
                            Move ts = new Move(
                                    (elem2.getAttribute("axis", axisValueSet, "x", false)).intValue(),
                                    Move.toLayerMask(elem2.getAttribute("layerList")),
                                    (elem2.getAttribute("angle", angleValueSet, "90", false)).intValue());
                            String tokenList = elem2.getContent();
                            StringTokenizer tt = new StringTokenizer(tokenList);
                            String[] tokens = new String[tt.countTokens()];
                            for (int ti = 0; ti < tokens.length; ti++) {
                                tokens[ti] = tt.nextToken();
                            }
                            moveToTokenMap.put(ts, tokens);
                        } else {
                            SymbolInfo info2 = symbolToInfoMap.get(sym2);
                            info2.isSupported = elem2.getBooleanAttribute("enabled", true);
                            info2.syntax = elem2.getAttribute("syntax", syntaxValueSet, "suffix", false);
                            String tokenList = elem2.getContent();
                            StringTokenizer tt = new StringTokenizer(tokenList);
                            String[] tokens = new String[tt.countTokens()];
                            for (int ti = 0; ti < tokens.length; ti++) {
                                tokens[ti] = tt.nextToken();
                            }
                            info2.tokens = tokens;
                        }
                    }
                }
            } else if ("Macro".equals(name)) {
                String macro = elem.getContent();
                for (StringTokenizer tt = new StringTokenizer(elem.getContent()); tt.hasMoreTokens();) {
                    String identifier = tt.nextToken();
                    identifierToMacroMap.put(identifier, macro);
                }
            }
        }

        // Fill token to symbol map
        tokenToSymbolMap = new HashMap<String,ArrayList<Symbol>>();
        for (Iterator i = symbolToInfoMap.keySet().iterator(); i.hasNext();) {
            Symbol symbol = (Symbol) i.next();
            SymbolInfo info = symbolToInfoMap.get(symbol);
            String[] tokens = info.tokens;
            if (tokens != null) {
                for (int ti = 0; ti < tokens.length; ti++) {
                    ArrayList<Symbol> v;
                    v = tokenToSymbolMap.get(tokens[ti]);
                    if (v == null) {
                        v = new ArrayList<Symbol>();
                        tokenToSymbolMap.put(tokens[ti], v);
                    }
                    v.add(symbol);
                }
            }
        }
        // Fill token to move map
        tokenToMoveMap = new HashMap<String,Move>();
        for (Iterator i = moveToTokenMap.keySet().iterator(); i.hasNext();) {
            Move moveSymbol = (Move) i.next();
            String[] tokens = moveToTokenMap.get(moveSymbol);
            for (int ti = 0; ti < tokens.length; ti++) {
                tokenToMoveMap.put(tokens[ti], moveSymbol);
            }
        }

        // Disable permutation support, if tokens are missing
        if (getToken(Symbol.FACE_R) == null) {
            symbolToInfoMap.put(Symbol.PERMUTATION, new SymbolInfo(Symbol.PERMUTATION, false, Syntax.PRECIRCUMFIX));
        }
        //System.out.println(this.toVerboseString());
    }

    public String toVerboseString() {
        StringBuilder buf = new StringBuilder();

        buf.append("Permutation=");
        buf.append(isSupported(Symbol.PERMUTATION));
        buf.append("\n  Syntax=");
        buf.append(getSyntax(Symbol.PERMUTATION));
        buf.append("\n  Faces=");
        buf.append(getToken(Symbol.FACE_R));
        buf.append(",");
        buf.append(getToken(Symbol.FACE_U));
        buf.append(",");
        buf.append(getToken(Symbol.FACE_F));
        buf.append(",");
        buf.append(getToken(Symbol.FACE_L));
        buf.append(",");
        buf.append(getToken(Symbol.FACE_D));
        buf.append(",");
        buf.append(getToken(Symbol.FACE_B));
        buf.append("\n  Plus=");
        buf.append(getToken(Symbol.PERMUTATION_PLUS));
        buf.append("  Minus=");
        buf.append(getToken(Symbol.PERMUTATION_MINUS));
        buf.append("  PlusPlus=");
        buf.append(getToken(Symbol.PERMUTATION_PLUSPLUS));
        buf.append("\n  Begin=");
        buf.append(getToken(Symbol.PERMUTATION_BEGIN));
        buf.append("  End=");
        buf.append(getToken(Symbol.PERMUTATION_END));
        return buf.toString();
    }

    @Override
    public void configureMoveFromToken(MoveNode twist, String twistToken) {
        Move twistSymbol = tokenToMoveMap.get(twistToken);
        twist.setAngle(twistSymbol.getAngle());
        twist.setAxis(twistSymbol.getAxis());
        twist.setLayerMask(twistSymbol.getLayerMask());
    }

    public String getName() {
        return name;
    }

    @Override
    public String getEquivalentMacro(Cube cube, Map localMacros) {
        return null;
    }

    @Override
    public Symbol getSymbolFor(String token, Symbol compositeSymbol) {
        if (compositeSymbol == Symbol.MOVE) {
            return (tokenToMoveMap.containsKey(token)) ? Symbol.MOVE : null;
        } else {
            ArrayList<Symbol> symbols =  tokenToSymbolMap.get(token);
            if (symbols != null) {
                for (Symbol symbol : symbols) {
                    if (compositeSymbol.isSubSymbol(symbol)) {
                        return symbol;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Syntax getSyntax(Symbol s) {
        SymbolInfo info = symbolToInfoMap.get(s);
        return (info == null) ? null : info.syntax;
    }

    @Override
    public String getToken(Symbol s) {
        SymbolInfo info = symbolToInfoMap.get(s);
        return (info == null || info.tokens == null) ? null : info.tokens[0];
    }

    @Override
    public boolean isSupported(Symbol s) {
        SymbolInfo info = symbolToInfoMap.get(s);
        return info == null || info.isSupported;
    }

    @Override
    public boolean isToken(String token) {
        return tokenToMoveMap != null && tokenToMoveMap.containsKey(token)
                || tokenToSymbolMap != null && tokenToSymbolMap.containsKey(token);
    }
    /*
    public boolean isTokenFor(String token, Symbol symbol) {
    if (token == null) return false;

    if (symbol == Symbol.MOVE) {
    return tokenToMoveMap.containsKey(token);
    } else {
    Vector symbols = (Vector) tokenToSymbolMap.get(token);
    return symbols != null && symbols.contains(symbol);
    }
    }*/

    /**
     * Returns true, if the specified String is a token for the specified symbol.
     */
    @Override
    public boolean isTokenFor(String token, Symbol symbol) {
        boolean result;
        if (symbol == Symbol.MOVE) {
            result = tokenToMoveMap.containsKey(token);
        } else {
            ArrayList<Symbol> symbols = tokenToSymbolMap.get(token);
            if (symbols == null) {
                result = false;
            } else {
                result = symbols.contains(symbol);
                if (!result) {
                    for (Symbol ss : symbols) {
                        if (symbol.isSubSymbol(ss)) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void writeToken(PrintWriter w, Symbol symbol) throws IOException {
        w.write(getToken(symbol));
    }

    public void writeToken(PrintStream w, Symbol symbol) {
        String token = getToken(symbol);
        w.print((token == null) ? "null" : token);
    }

    @Override
    public void writeToken(PrintWriter w, int axis, int layerMask, int angle) throws IOException {
        w.write(moveToTokenMap.get(new MoveNode(getLayerCount(), axis, layerMask, angle, -1, -1))[0]);
    }

    public void dumpNotation() {
        System.out.println("name:" + name);
        System.out.println("description:" + name);
        Symbol[] s = Symbol.SEQUENCE.getSubSymbols();
        for (int i = 0; i < s.length; i++) {
            if (i != 0) {
                if (i % 10 == 0) {
                    System.out.println();
                } else {
                    System.out.print(" ,");
                }
            }
            System.out.print(s[i] + ":");
            if (s[i] == null) {
                System.out.println("null symbol for " + i);
            } else {
                writeToken(System.out, s[i]);
            }
        }
        System.out.println();
        for (Iterator i = moveToTokenMap.keySet().iterator(); i.hasNext();) {
            Object key = i.next();
            System.out.println(key + ":" + moveToTokenMap.get(key));
        }
    }

    @Override
    public int getLayerCount() {
        return layerCount;
    }

    @Override
    public String getToken(Move s) {
        return moveToTokenMap.get(s)[0];
    }

    @Override
    public List<MacroNode> getMacros() {
        // FIXME - Implement me
        return Collections.emptyList();
    }
}

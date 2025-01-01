/*
 * @(#)CubeMarkupNotation.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.notation;

import ch.randelshofer.cubetwister.doc.Template;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.xml.XMLPreorderIterator;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * CubeMarkupNotation reads its notation definition from a CubeMarkup XML 3 file.
 *
 * @author Werner Randelshofer
 */
public class CubeMarkupNotation implements ScriptNotation {

    private static class SymbolInfo {

        Symbol symbol;
        boolean isSupported;
        Syntax syntax;
        List<String> tokens;

        public SymbolInfo(Symbol symbol, boolean isSupported, Syntax syntax, @Nullable List<String> tokens) {
            this.symbol = symbol;
            this.isSupported = isSupported;
            this.syntax = syntax;
            this.tokens = tokens == null ? new ArrayList<>() : tokens;
        }

        public SymbolInfo(Symbol symbol, boolean isSupported, Syntax syntax) {
            this(symbol, isSupported, syntax, null);
        }
    }

    /**
     * This map holds all the symbol infos.
     * <p>
     * Key: Symbol
     * Value: SymbolInfo
     */
    private SequencedMap<Symbol, SymbolInfo> symbolToInfoMap;
    /**
     * This map is used for parsing a script. Or more precisely for
     * associating tokens to symbols.
     * Tokens can be ambiguous. Therefore a token can have multiple symbols.
     * <p>
     * Key: String
     * Value: ArrayList<Symbol>
     */
    private SequencedMap<String, ArrayList<Symbol>> tokenToSymbolsMap;
    /**
     * This map is used for associating global macro identifiers of the notation
     * to macro scripts.
     * <p>
     * Key: String
     * Value: String
     */
    private SequencedMap<String, String> identifierToMacroMap;
    /**
     * This map is used for pretty printing a MoveNode (aka a parsed twist).
     * <p>
     * Key: Move
     * Value: String
     */
    private SequencedMap<Move, String[]> moveToTokenMap;
    /**
     * This map is used for associationg a move token to a MoveNode.
     * <p>
     * Key: String
     * Value: Move
     */
    private SequencedMap<String, Move> tokenToMoveMap;
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

    /**
     * Creates a new instance.
     */
    public CubeMarkupNotation() {
        this(new RubiksCube());
    }

    /**
     * Creates a new instance.
     */
    public CubeMarkupNotation(Cube cube) {
        this.cube = cube;
        symbolToInfoMap = new LinkedHashMap<>();
        identifierToMacroMap = new LinkedHashMap<>();
    }

    /**
     * Configures this CubeMarkupNotation by the default Notation
     * found in the URL.
     * The CubeMarkup 4 DTD must be used.
     */
    public void readXML(URL url) throws IOException {
        try (InputStreamReader r = new InputStreamReader(Template.getTemplate().openStream(), StandardCharsets.UTF_8)) {
            XMLElement root = new XMLElement(new LinkedHashMap<>(), true, false);
            root.parseFromReader(r);
            readXML(root);
        }
    }

    /**
     * Configures this CubeMarkupNotation by the specified Notation
     * found in the URL.
     * The CubeMarkup 4 DTD must be used.
     */
    public void readXML(URL url, String notationName) throws IOException {
        try (InputStreamReader r = new InputStreamReader(Template.getTemplate().openStream(), StandardCharsets.UTF_8)) {
            XMLElement root = new XMLElement(new HashMap<>(), true, false);
            root.parseFromReader(r);
            readXML(root, notationName);
        }
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
     * <p>
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
                for (XMLElement notationElem : elem.getChildren()) {
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
     * <p>
     * The CubeMarkup 4 DTD must be used.
     */
    private void readNotationXMLElement(@Nonnull XMLElement notationElem) throws XMLParseException {
        // Name
        ArrayList<XMLElement> names = notationElem.getChildren("Name");
        name = names.isEmpty() ? null : names.getFirst().getContent();

        // Initialize syntax value set
        SequencedMap<String, Syntax> syntaxValueSet = new LinkedHashMap<>();
        syntaxValueSet.put("precircumfix", Syntax.PRECIRCUMFIX);
        syntaxValueSet.put("postcircumfix", Syntax.POSTCIRCUMFIX);
        syntaxValueSet.put("postinfix", Syntax.POSTINFIX);
        syntaxValueSet.put("preinfix", Syntax.PREINFIX);
        syntaxValueSet.put("prefix", Syntax.PREFIX);
        syntaxValueSet.put("suffix", Syntax.SUFFIX);
        syntaxValueSet.put("circumfix", Syntax.CIRCUMFIX);
        syntaxValueSet.put("primary", Syntax.PRIMARY);

        // Axis value set
        SequencedMap<String, Integer> axisValueSet = new LinkedHashMap<>();
        axisValueSet.put("x", 0);
        axisValueSet.put("y", 1);
        axisValueSet.put("z", 2);

        // Angle value set
        SequencedMap<String, Integer> angleValueSet = new LinkedHashMap<>();
        angleValueSet.put("-180", -2);
        angleValueSet.put("-90", -1);
        angleValueSet.put("90", 1);
        angleValueSet.put("180", 2);

        // Initialize symbol to info map
        symbolToInfoMap = new LinkedHashMap<>();
        SequencedMap<String, Symbol> symbolValueSet = new LinkedHashMap<>();
        for (Symbol symbol : Symbol.values()) {
            symbolToInfoMap.put(symbol, new SymbolInfo(symbol, false, null));
            symbolValueSet.put(symbol.getName(), symbol);
            if (symbol.getAlternativeName() != null) {
                symbolValueSet.put(symbol.getAlternativeName(), symbol);
            }
        }
        symbolToInfoMap.put(Symbol.MOVE, new SymbolInfo(Symbol.MOVE, true, Syntax.PRIMARY));
        symbolToInfoMap.put(Symbol.COMMUTATION, new SymbolInfo(Symbol.COMMUTATION, false, Syntax.PREFIX));
        symbolToInfoMap.put(Symbol.CONJUGATION, new SymbolInfo(Symbol.CONJUGATION, false, Syntax.PREFIX));
        symbolToInfoMap.put(Symbol.INVERSION, new SymbolInfo(Symbol.INVERSION, false, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.PERMUTATION, new SymbolInfo(Symbol.PERMUTATION, false, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.REFLECTION, new SymbolInfo(Symbol.REFLECTION, false, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.REPETITION, new SymbolInfo(Symbol.REPETITION, false, Syntax.SUFFIX));
        symbolToInfoMap.put(Symbol.GROUPING, new SymbolInfo(Symbol.GROUPING, false, Syntax.CIRCUMFIX));
        /*
        for (Symbol s : List.of(Symbol.CONJUGATION_BEGIN,
                Symbol.COMMUTATION_BEGIN,
                Symbol.ROTATION_BEGIN,
                Symbol.PERMUTATION_BEGIN,
                Symbol.INVERSION_BEGIN,
                Symbol.REFLECTION_BEGIN,
                Symbol.GROUPING_BEGIN,
                Symbol.MULTILINE_COMMENT_BEGIN,
                Symbol.SINGLELINE_COMMENT_BEGIN)) {
            symbolToInfoMap.put(s, new SymbolInfo(s, false, Syntax.PREFIX));
        }
        for (Symbol s : List.of(Symbol.CONJUGATION_END,
                Symbol.COMMUTATION_END,
                Symbol.PERMUTATION_END,
                Symbol.ROTATION_END,
                Symbol.INVERSION_END,
                Symbol.REFLECTION_END,
                Symbol.GROUPING_END,
                Symbol.MULTILINE_COMMENT_END)) {
            symbolToInfoMap.put(s, new SymbolInfo(s, false, Syntax.SUFFIX));
        }
        for (Symbol s : List.of(Symbol.CONJUGATION_OPERATOR,
                Symbol.COMMUTATION_OPERATOR,
                Symbol.ROTATION_OPERATOR,
                Symbol.INVERSION_OPERATOR,
                Symbol.REFLECTION_OPERATOR,
                Symbol.REPETITION_OPERATOR)) {
            symbolToInfoMap.put(s, new SymbolInfo(s, false, Syntax.PREINFIX));
        }*/

        // Initialize move to token map
        moveToTokenMap = new LinkedHashMap<>();

        // Read layer count
        layerCount = notationElem.getIntAttribute("layerCount", 2, 32, 3);

        // Read notation constructs
        for (Iterator i = notationElem.getChildren().iterator(); i.hasNext(); ) {
            XMLElement elem = (XMLElement) i.next();
            String name = elem.getName();
            if ("Statement".equals(name)) {
                Symbol sym = elem.getAttribute("symbol", symbolValueSet, "move", false);
                SymbolInfo info = symbolToInfoMap.get(sym);
                info.isSupported = elem.getBooleanAttribute("enabled", true);
                if (info.syntax == null || elem.getAttribute("syntax") != null) {
                    info.syntax = elem.getAttribute("syntax", syntaxValueSet, "suffix", false);
                }
                for (Iterator i2 = elem.getChildren().iterator(); i2.hasNext(); ) {
                    XMLElement elem2 = (XMLElement) i2.next();
                    String name2 = elem2.getName();
                    if ("Token".equals(name2)) {
                        Symbol sym2 = elem2.getAttribute("symbol", symbolValueSet, "move", false);
                        if (sym2 == Symbol.MOVE) {
                            Move ts;
                            try {
                                ts = new Move(
                                        layerCount, (elem2.getAttribute("axis", axisValueSet, "x", false)).intValue(),
                                        Move.toLayerMask(elem2.getAttribute("layerList")),
                                        (elem2.getAttribute("angle", angleValueSet, "90", false)).intValue());
                            } catch (IllegalArgumentException e) {
                                throw new XMLParseException(elem2.getName(), "Illegal move " + elem2.toString());
                            }
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
                            if (elem2.getAttribute("syntax") != null) {
                                info2.syntax = elem2.getAttribute("syntax", syntaxValueSet, "suffix", false);
                            }
                            String tokenList = elem2.getContent();
                            StringTokenizer tt = new StringTokenizer(tokenList);
                            List<String> tokens = new ArrayList<>();
                            for (int ti = 0, tc = tt.countTokens(); ti < tc; ti++) {
                                tokens.add(tt.nextToken());
                            }
                            info2.tokens = tokens;
                        }
                    }
                }
            } else if ("Macro".equals(name)) {
                String macro = elem.getContent();
                for (StringTokenizer tt = new StringTokenizer(elem.getContent()); tt.hasMoreTokens(); ) {
                    String identifier = tt.nextToken();
                    identifierToMacroMap.put(identifier, macro);
                }
            }
        }

        // Fill token to symbol map
        tokenToSymbolsMap = new LinkedHashMap<>();
        for (Symbol symbol : symbolToInfoMap.keySet()) {
            SymbolInfo info = symbolToInfoMap.get(symbol);
            List<String> tokens = info.tokens;
            if (tokens != null) {
                for (String token : tokens) {
                    tokenToSymbolsMap.computeIfAbsent(token, k -> new ArrayList<>())
                            .add(symbol);
                }
            }
        }
        // Fill token to move map
        tokenToMoveMap = new LinkedHashMap<>();
        for (Move moveSymbol : moveToTokenMap.keySet()) {
            String[] tokens = moveToTokenMap.get(moveSymbol);
            for (String token : tokens) {
                tokenToMoveMap.put(token, moveSymbol);
            }
        }

        // Disable permutation support, if tokens are missing
        if (getToken(Symbol.FACE_R) == null) {
            symbolToInfoMap.put(Symbol.PERMUTATION, new SymbolInfo(Symbol.PERMUTATION, false, Syntax.PRECIRCUMFIX));
        }
        //System.out.println(this.toVerboseString());
    }


    @Override
    public @Nullable Move getMoveFromToken(String moveToken) {
        Move move = tokenToMoveMap.get(moveToken);
        if (move == null) {
            throw new IllegalArgumentException("Not a move token. token:" + moveToken);
        }
        return move;
    }

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public String getEquivalentMacro(Cube cube, Map localMacros) {
        return null;
    }

    @Nonnull
    @Override
    public Syntax getSyntax(Symbol symbol) {
        var info = this.symbolToInfoMap.get(symbol.getCompositeSymbol());
        return info != null ? info.syntax : Syntax.PRIMARY;

    }

    @Nullable
    @Override
    public String getToken(Symbol s) {
        SymbolInfo info = symbolToInfoMap.get(s);
        return (info == null || info.tokens == null || info.tokens.isEmpty()) ? null : info.tokens.get(0);
    }

    @Override
    public List<String> getAllTokens(Symbol key) {
        SymbolInfo info = symbolToInfoMap.get(key);
        return (info == null || info.tokens == null) ? List.of() : info.tokens;
    }

    @Override
    public boolean isSupported(Symbol s) {
        SymbolInfo info = symbolToInfoMap.get(s);
        return info == null || info.isSupported;
    }

    @Override
    public boolean isMoveSupported(Move key) {
        return false;
    }

    @Override
    public void writeToken(Writer w, Symbol symbol) throws IOException {
        w.write(getToken(symbol));
    }

    public void writeToken(@Nonnull PrintStream w, Symbol symbol) {
        String token = getToken(symbol);
        w.print((token == null) ? "null" : token);
    }

    @Override
    public void writeMoveToken(@Nonnull PrintWriter w, int axis, int layerMask, int angle) throws IOException {
        w.write(moveToTokenMap.get(new Move(getLayerCount(), axis, layerMask, angle))[0]);
    }

    public void dumpNotation() {
        System.out.println("name:" + name);
        System.out.println("description:" + name);
        Set<Symbol> ss = Symbol.SEQUENCE.getSubSymbols();
        int i = 0;
        for (Symbol s : ss) {
            if (i != 0) {
                if (i % 10 == 0) {
                    System.out.println();
                } else {
                    System.out.print(" ,");
                }
            }
            System.out.print(s + ":");
            if (s == null) {
                System.out.println("null symbol for " + i);
            } else {
                writeToken(System.out, s);
            }
            i++;
        }
        System.out.println();
        for (Move key : moveToTokenMap.keySet()) {
            System.out.println(key + ":" + Arrays.toString(moveToTokenMap.get(key)));
        }
    }

    @Override
    public int getLayerCount() {
        return layerCount;
    }

    @Override
    public String getMoveToken(Move s) {
        String[] strings = moveToTokenMap.get(s);
        return strings != null && strings.length > 0 ? strings[0] : null;
    }

    @Override
    public List<String> getAllMoveTokens(Move s) {
        String[] strings = moveToTokenMap.get(s);
        return strings != null ? Arrays.asList(strings) : Collections.emptyList();
    }

    @Override
    public Set<Move> getAllMoveSymbols() {
        return moveToTokenMap.keySet();
    }

    @Nonnull
    @Override
    public Map<String, String> getAllMacros() {
        // FIXME - Implement me
        return Collections.emptyMap();
    }

    /**
     * Gets all tokens defined for this notation.
     *
     * @return the tokens.
     */
    @Nonnull
    public Collection<String> getTokens() {
        Set<String> tokens = new LinkedHashSet<>();
        for (Map.Entry<String, ArrayList<Symbol>> entry : tokenToSymbolsMap.entrySet()) {
            boolean enabled = false;
            for (Symbol symbol : entry.getValue()) {
                SymbolInfo info = symbolToInfoMap.get(symbol);
                if (info != null && info.isSupported) {
                    enabled = true;
                    break;
                }
            }
            if (enabled) {
                tokens.add(entry.getKey());
            }
        }
        tokens.addAll(tokenToMoveMap.keySet());

        return tokens;
    }

    @Nonnull
    public List<Symbol> getSymbols(String token) {
        ArrayList<Symbol> symbols = new ArrayList<>();
        ArrayList<Symbol> emptyList = new ArrayList<>();
        for (Symbol symbol : tokenToSymbolsMap.getOrDefault(token, emptyList)) {
            SymbolInfo info = symbolToInfoMap.get(symbol);
            if (info != null && info.isSupported) {
                symbols.add(symbol);
            }
        }
        if (tokenToMoveMap.containsKey(token)) {
            symbols.add(Symbol.MOVE);
        }

        return symbols;
    }
}

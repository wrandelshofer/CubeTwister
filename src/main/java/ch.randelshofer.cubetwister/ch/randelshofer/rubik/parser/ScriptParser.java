package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parser for rubik's cube scripts. The tokens and syntax-rules used by the
 * parser are read from a Notation object.<p>
 * <p>
 * The parser supports the EBNF ISO/IEC 14977 productions shown below.
 * The {@link Syntax}  of the productions is read from a {@link Notation} object.
 * The notation allows to specify the tokens for Expression productions which may
 * be ambiguous up until the last token of the Expression production has been parsed.
 * This parser takes the first solution that it could find using a backtracking
 * algorithm.
 * <pre>
 * Script         = { Statement } ;
 *
 * Statement        = Primary
 *                  | Prefix
 *                  | Circumfix
 *                  | Precircumfix
 *                  | Postcircumfix
 *                  | Preinfix
 *                  | Postinfix
 *                  | Repetition
 *                  | Permutation
 *                  ;
 *
 * Primary          = Keyword ;
 * Prefix           = Operator , Statement ;
 * Suffix           = Statement , Operator ;
 * Circumfix        = Begin, { Statement } , End ;
 * Preinfix         = Statement, Operator, Statement ;
 * Postinfix        = Statement, Operator, Statement ;
 * Precircumfix     = Begin, { Statement } , Delimiter, { Statement }  , End ;
 * Postcircumfix    = Begin, { Statement } , Delimiter, { Statement }  , End ;
 *
 * Unary            = Prefix
 *                  | Suffix
 *                  | Circumfix
 *                  ;
 *
 * Binary           = Preinfix
 *                  | Postinfix
 *                  | Precircumfix
 *                  | Postcircumfix
 *                  ;
 *
 * Move             = Primary ;
 * NOP              = Primary ;
 * Macro            = Primary ;
 * Commutation      = Binary ;
 * Conjugation      = Binary ;
 * Grouping         = Circumfix ;
 * Inversion        = Unary ;
 *
 * Repetition       = PrefixRepetition
 *                  | SuffixRepetition
 *                  | PreinfixRepetition
 *                  | PostinfixRepetition
 *                  | PrecircumfixRepetition
 *                  | PostcircumfixRepetition
 *                  ;
 *
 * PrefixRepetition        = Number , Statement ;
 * SuffixRepetition        = Statement , Number ;
 * PreinfixRepetition      = Number, Operator, Statement ;
 * PostinfixRepetition     = Statement, Operator, Number ;
 * PrecircumfixRepetition  = Begin, Number, Delimiter, { Statement }  , End ;
 * PostcircumfixRepetition = Begin, { Statement } , Delimiter, Number , End ;
 *
 * Permutation   = PrecircumfixPermutation
 *               | PrefixPermutation
 *               | PostcircumfixPermutation
 *               | PostfixPermutation
 *               ;
 *
 * PrecircumfixPermutation   = Begin, [ PermSign ], { PrefixPermItem} , End ;
 * PrefixPermutation         = [ PermSign ], Begin, { PrefixPermItem} , End ;
 * PostcircumfixPermutation  = Begin, { SuffixPermItem} , [ PermSign ], End ;
 * PostfixPermutation        = Begin, { SuffixPermItem} , End , [ PermSign ] ;
 * PermSign       = Plus | PlusPlus | Minus ;
 * PrefixPermItem = [PermSign] , Face{1,3} , [ Number ] ;
 * SuffixPermItem = Face{1,3} , [ Number ] , [PermSign] ;
 * Face           = FaceR | FaceU | FaceF | FaceD | FaceL | FaceB;
 *
 * Plus      = Keyword ;
 * PlusPlus  = Keyword ;
 * PlusMinus = Keyword ;
 * FaceR = Keyword;
 * FaceU = Keyword;
 * FaceF = Keyword;
 * FaceD = Keyword;
 * FaceL = Keyword;
 * FaceB = Keyword;
 *
 * Kewyord  = Char , { Char } ;
 * Char     = 'A'..'Z' | 'a'..'z' | '0'..'9' | '.'..'$' ;
 * </pre>
 *
 * @author Werner Randelshofer
 */

public class ScriptParser {
    private Notation notation;
    private Map<String, MacroNode> localMacros;

    public ScriptParser(Notation notation) {
        this(notation, Collections.emptyList());
    }

    public ScriptParser(Notation notation, List<MacroNode> localMacros) {
        this.notation = notation;
        this.localMacros = localMacros.stream().collect(
                Collectors.toMap(MacroNode::getIdentifier, Function.identity()));
    }

    public Notation getNotation() {
        return notation;
    }

    public ScriptNode parse(String input) throws ParseException {
        Tokenizer tt = createTokenizer(notation);
        tt.setInput(input);
        return parseScript(tt);
    }

    private ScriptNode parseScript(Tokenizer tt) throws ParseException {
        ScriptNode script = new ScriptNode();
        script.setStartPosition(tt.getStartPosition());
        while (tt.nextToken() != Tokenizer.TT_EOF) {
            tt.pushBack();
            parseStatement(tt, script);
        }
        script.setEndPosition(tt.getEndPosition());
        return script;
    }

    private void parseStatement(Tokenizer tt, Node parent) throws ParseException {
        switch (tt.nextToken()) {
            case Tokenizer.TT_NUMBER:
                tt.pushBack();
                parseRepetition(tt, parent);
                break;
            case Tokenizer.TT_KEYWORD:
                tt.pushBack();
                parseOtherStatementBacktrack(tt, parent);
                break;
            default:
                throw createException(tt, "Statement: Keyword or Number expected.");
        }
    }

    private void parseOtherStatementBacktrack(Tokenizer tt, Node parent) throws ParseException {
        if (tt.nextToken() != Tokenizer.TT_KEYWORD) {
            throw createException(tt, "Statement: Keyword expected.");
        }

        String token = tt.getStringValue();
        ParseException e = null;
        List<Node> children = new ArrayList<>(parent.getChildren());
        for (Symbol symbol : this.getSymbolsFor(token)) {
            Syntax syntax = getSyntax(symbol);

            Tokenizer clone = tt.clone();
            try {
                ScriptNode p = new ScriptNode();
                p.addAll(children);
                parseOtherStatement(clone, p, token, symbol, syntax);
                tt.setTo(clone);
                parent.addAll(new ArrayList<>(p.getChildren()));
                e = null;
                break;
            } catch (ParseException pe) {
                if (e == null || e.getEndPosition() < pe.getEndPosition()) {
                    e = pe;
                }
            }
        }
        if (e != null) {
            throw e;
        }
    }

    private Syntax getSyntax(Symbol symbol) {
        Syntax s = notation.getSyntax(symbol);
        return s == null ? Syntax.PRIMARY : s;
    }

    private void parseOtherStatement(Tokenizer tt, Node parent, String token, Symbol symbol, Syntax syntax) throws ParseException {
        Symbol c = symbol.getCompositeSymbol();
        if (c == Symbol.PERMUTATION) {
            tt.pushBack();
            parsePermutation(tt, parent, token, symbol);
            return;
        }

        switch (syntax) {
            case PRIMARY:
                parsePrimary(tt, parent, token, symbol);
                break;
            case PREFIX:
                parsePrefix(tt, parent, token, symbol);
                break;
            case SUFFIX:
                parseSuffix(tt, parent, token, symbol);
                break;
            case CIRCUMFIX:
                parseCircumfix(tt, parent, token, symbol);
                break;
            case PRECIRCUMFIX:
                parsePrecircumfix(tt, parent, token, symbol);
                break;
            case POSTCIRCUMFIX:
                parsePostcircumfix(tt, parent, token, symbol);
                break;
            case PREINFIX:
                parsePreinfix(tt, parent, token, symbol);
                break;
            case POSTINFIX:
                parsePostinfix(tt, parent, token, symbol);
                break;
        }
    }

    private boolean isBegin(Symbol symbol) {
        switch (symbol) {
            case CONJUGATION_BEGIN:
            case COMMUTATION_BEGIN:
            case PERMUTATION_BEGIN:
            case INVERSION_BEGIN:
            case REFLECTION_BEGIN:
            case GROUPING_BEGIN:
            case MULTILINE_COMMENT_BEGIN:
            case SINGLELINE_COMMENT_BEGIN:
                return true;
            default:
                return false;
        }
    }

    private boolean isDelimiter(Symbol symbol) {
        switch (symbol) {
            case CONJUGATION_DELIMITER:
            case COMMUTATION_DELIMITER:
                return true;
            default:
                return false;
        }
    }

    private boolean isEnd(Symbol symbol) {
        switch (symbol) {
            case CONJUGATION_END:
            case COMMUTATION_END:
            case PERMUTATION_END:
            case INVERSION_END:
            case REFLECTION_END:
            case GROUPING_END:
            case MULTILINE_COMMENT_END:
                return true;
            default:
                return false;
        }
    }

    private void parseCircumfix(Tokenizer tt, Node parent, String begin, Symbol symbol) throws ParseException {
        int startPos = tt.getStartPosition();
        Node operand = parseCircumfixOperand(tt, parent, begin, symbol);
        Node compositeNode = createCompositeNode(tt, symbol, operand, null);
        compositeNode.setStartPosition(startPos);
        compositeNode.setEndPosition(tt.getEndPosition());
        parent.add(compositeNode);
    }

    private Node parseCircumfixOperand(Tokenizer tt, Node parent, String begin, Symbol symbol) throws ParseException {
        List<Node> nodes = parseCircumfixOperands(tt, parent, begin, symbol);
        if (nodes.size() != 1) {
            throw createException(tt, "Circumfix: Exactly one operand expected.");
        }
        return nodes.get(0);
    }

    private List<Node> parseCircumfixOperands(Tokenizer tt, Node parent, String begin, Symbol symbol) throws ParseException {
        if (!isBegin(symbol)) {
            throw createException(tt, "Circumfix: Begin expected.");
        }
        Symbol compositeSymbol = symbol.getCompositeSymbol();
        List<Node> operands = new ArrayList<>();
        ScriptNode operand = new ScriptNode();
        operand.setStartPosition(tt.getEndPosition());
        operands.add(operand);
        Loop:
        while (true) {
            switch (tt.nextToken()) {
                case Tokenizer.TT_EOF:
                    throw createException(tt, "Circumfix: End expected.");
                case Tokenizer.TT_KEYWORD:
                    String maybeSeparatorOrEnd = tt.getStringValue();
                    for (Symbol symbol1 : this.getSymbolsFor(maybeSeparatorOrEnd)) {
                        if (symbol1.getCompositeSymbol().equals(compositeSymbol)) {
                            if (isDelimiter(symbol1)) {
                                operand.setEndPosition(tt.getStartPosition());
                                operand = new ScriptNode();
                                operand.setStartPosition(tt.getEndPosition());
                                operands.add(operand);
                                continue Loop;
                            } else if (isEnd(symbol1)) {
                                break Loop;
                            }
                        }
                    }
                    tt.pushBack();
                    parseStatement(tt, operand);
                    break;
                default:
                    throw createException(tt, "Circumfix: Keyword or End expected.");
            }
        }
        operand.setEndPosition(tt.getStartPosition());
        return operands;
    }

    private List<Symbol> getSymbolsFor(String token) {
        List<Symbol> notationSymbols = notation.getSymbolsFor(token);
        if (localMacros.containsKey(token)) {
            List<Symbol> symbols = new ArrayList<>();
            symbols.addAll(notationSymbols);
            symbols.add(Symbol.MACRO);
            return symbols;
        }
        return notationSymbols;
    }

    private Symbol getSymbolFor(String token, Symbol compositeSymbol) {
        for (Symbol s : getSymbolsFor(token)) {
            if (compositeSymbol.isSubSymbol(s)) {
                return s;
            }
        }
        return null;

    }

    private void parsePrecircumfix(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {
        int start = tt.getStartPosition();
        List<Node> operands = parseCircumfixOperands(tt, parent, token, symbol);
        if (operands.size() != 2) {
            throw createException(tt, "Precircumfix: Two operands expected.");
        }
        int end = tt.getEndPosition();
        Node node = createCompositeNode(tt, symbol, operands.get(0), operands.get(1));
        node.setStartPosition(start);
        node.setEndPosition(end);
        parent.add(node);
    }

    private void parsePostcircumfix(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {
        int start = tt.getStartPosition();
        List<Node> operands = parseCircumfixOperands(tt, parent, token, symbol);
        if (operands.size() != 2) {
            throw createException(tt, "Precircumfix: Two operands expected.");
        }
        int end = tt.getEndPosition();
        Node node = createCompositeNode(tt, symbol, operands.get(1), operands.get(0));
        node.setStartPosition(start);
        node.setEndPosition(end);
        parent.add(node);
    }

    private void parsePreinfix(Tokenizer tt, Node parent, String value, Symbol symbol) throws ParseException {
        if (parent.getChildCount() == 0) {
            throw createException(tt, "Preinfix: Operand expected.");
        }
        Node sibling = parent.getChildAt(parent.getChildCount() - 1);
        ScriptNode operand1 = new ScriptNode();
        operand1.add(sibling);
        operand1.setStartPosition(sibling.getStartPosition());
        operand1.setEndPosition(sibling.getEndPosition());
        ScriptNode operand2 = new ScriptNode();
        parseStatement(tt, operand2);
        Node node = createCompositeNode(tt, symbol, operand1, operand2);
        node.setStartPosition(sibling.getStartPosition());
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    private void parsePostinfix(Tokenizer tt, Node parent, String value, Symbol symbol) throws ParseException {
        if (parent.getChildCount() == 0) {
            throw createException(tt, "Preinfix: Operand expected.");
        }
        Node sibling = parent.getChildAt(parent.getChildCount() - 1);
        ScriptNode operand2 = new ScriptNode();
        operand2.add(sibling);
        ScriptNode operand1 = new ScriptNode();
        Node node;
        if (symbol.getCompositeSymbol() == Symbol.REPETITION) {
            if (tt.nextToken() != Tokenizer.TT_NUMBER) {
                throw new ParseException("Repetition: Repetition count expected.", tt.getStartPosition(), tt.getEndPosition());
            }
            node = createCompositeNode(tt, symbol, operand2, operand1);
            ((RepetitionNode) node).setRepeatCount(tt.getNumericValue());
        } else {
            operand1.setStartPosition(tt.getStartPosition());
            parseStatement(tt, operand1);
            operand1.setEndPosition(tt.getEndPosition());
            node = createCompositeNode(tt, symbol, operand1, operand2);
        }
        node.setStartPosition(sibling.getStartPosition());
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    private void parsePrimary(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {
        Node child;
        switch (symbol) {
            case NOP:
                child = new NOPNode(tt.getStartPosition(), tt.getEndPosition());
                break;
            case MOVE:
                Move move = notation.getMoveFromToken(token);
                child = new MoveNode(move.getLayerCount(), move.getAxis(), move.getLayerMask(), move.getAngle(),
                        tt.getStartPosition(), tt.getEndPosition());
                break;
            case MACRO:
                // Expand macro
                try {
                    String macro = notation.getMacro(token);
                    ScriptNode macroScript = parse(macro);
                    MacroNode macroNode = new MacroNode(null, macro, tt.getStartPosition(), tt.getEndPosition());
                    macroNode.add(macroScript);
                    child = macroNode;
                } catch (ParseException e) {
                    throw new ParseException("Error in macro \"" + token + "\":" + e.getMessage()
                            + " at " + e.getStartPosition() + ".." + e.getEndPosition(),
                            tt.getStartPosition(), tt.getEndPosition());
                }
                break;
            default:
                throw createException(tt, "Primary Expression: " + symbol + " cannot be used as a primary expression.");
        }
        parent.add(child);
    }

    private void parsePrefix(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {
        Node operand1 = null;
        Node operand2 = null;
        int startPosition = tt.getStartPosition();
        if (isBegin(symbol)) {
            operand1 = parseCircumfixOperand(tt, operand2, token, symbol);
            operand2 = new ScriptNode();
            parseStatement(tt, operand2);
        } else {
            operand1 = new ScriptNode();
            parseStatement(tt, operand1);
        }
        Node node = createCompositeNode(tt, symbol, operand1, operand2);
        node.setStartPosition(startPosition);
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    private void parseSuffix(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {
        if (parent.getChildCount() < 1) {
            throw new ParseException("Suffix: No sibling for suffix.", tt.getStartPosition(), tt.getEndPosition());
        }

        Node sibling = parent.getChildAt(parent.getChildCount() - 1);
        int startPosition = sibling.getStartPosition();
        Node operand1 = null;
        Node operand2 = null;
        if (isBegin(symbol)) {
            operand1 = parseCircumfixOperand(tt, new ScriptNode(), token, symbol);
            operand2 = new ScriptNode();
            operand2.add(sibling);
        } else {
            operand1 = new ScriptNode();
            operand1.add(sibling);
        }

        Node node = createCompositeNode(tt, symbol, operand1, operand2);
        node.setStartPosition(startPosition);
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    private void parseRepetition(Tokenizer tt, Node parent) throws ParseException {
        if (tt.nextToken() != Tokenizer.TT_NUMBER) {
            throw new ParseException("Repetition: Number expected.", tt.getStartPosition(), tt.getEndPosition());
        }
        int start = tt.getStartPosition();
        int repeatCount = tt.getNumericValue();
        Syntax syntax = getSyntax(Symbol.REPETITION);
        ScriptNode operand = new ScriptNode();
        switch (syntax) {
            case PREFIX:
                parseStatement(tt, operand);
                break;
            case SUFFIX: {
                if (parent.getChildCount() < 1) {
                    throw createException(tt, "Repetition: Sibling missing.");
                }
                Node sibling = parent.getChildAt(parent.getChildCount() - 1);
                start = sibling.getStartPosition();
                operand.add(sibling);
                break;
            }
            case PREINFIX:
                nextTokenWithSymbolNonnull(tt, Symbol.REPETITION_OPERATOR, "Repetition");
                parseStatement(tt, operand);
                break;
            case POSTINFIX:
                // Note: Postinfix syntax is handled by parsePostinfix.
                // We only get here, if the operator is missing!
                throw createException(tt, "Repetition: Operator expected.");
            case CIRCUMFIX:
            case PRECIRCUMFIX:
            case POSTCIRCUMFIX: {
                throw new ParseException("Repetition: Illegal syntax: " + syntax, tt.getStartPosition(), tt.getEndPosition());
            }
        }
        RepetitionNode repetitionNode = new RepetitionNode();
        repetitionNode.addAll(new ArrayList<>(operand.getChildren()));
        repetitionNode.setRepeatCount(repeatCount);
        repetitionNode.setStartPosition(start);
        repetitionNode.setEndPosition(tt.getEndPosition());
        parent.add(repetitionNode);
    }

    private String nextTokenWithSymbolNonnull(Tokenizer tt, Symbol symbol0, String production) throws ParseException {
        tt.nextToken();
        String value = tt.getStringValue();
        if (tt.getTokenType() != Tokenizer.TT_KEYWORD) {
            throw new ParseException(production + ": Unexpected token: " + value, tt.getStartPosition(), tt.getEndPosition());
        }
        List<Symbol> symbols = this.getSymbolsFor(value);
        if (!symbols.contains(symbol0)) {
            throw new ParseException(production + ": Unexpected keyword: " + value, tt.getStartPosition(), tt.getEndPosition());
        }
        return value;
    }

    private Node createCompositeNode(Tokenizer tt, Symbol operator, Node operand1, Node operand2) throws ParseException {
        Node node;
        switch (operator.getCompositeSymbol()) {
            case GROUPING:
                node = new GroupingNode();
                node.addAll(new ArrayList<>(operand1.getChildren()));
                break;
            case INVERSION:
                node = new InversionNode();
                node.addAll(new ArrayList<>(operand1.getChildren()));
                break;
            case REFLECTION:
                node = new ReflectionNode();
                node.addAll(new ArrayList<>(operand1.getChildren()));
                break;
            case REPETITION:
                node = new RepetitionNode();
                node.addAll(new ArrayList<>(operand1.getChildren()));
                break;
            case ROTATION:
                node = new RotationNode();
                node.addAll(new ArrayList<>(operand1.getChildren()));
                break;
            case COMMUTATION:
                if (operand1 == null || operand2 == null) {
                    throw createException(tt, "Commutation: Two operands expected.");
                }
                CommutationNode commutation = new CommutationNode();
                node = commutation;
                commutation.setCommutator(operand1);
                node.addAll(new ArrayList<>(operand2.getChildren()));
                break;
            case CONJUGATION:
                if (operand1 == null || operand2 == null) {
                    throw createException(tt, "Conjugation: Two operands expected.");
                }
                ConjugationNode conjugation = new ConjugationNode();
                node = conjugation;
                conjugation.setConjugator(operand1);
                node.addAll(new ArrayList<>(operand2.getChildren()));
                break;
            default:
                throw new AssertionError("Composite. Unexpected Symbol: " + operator);
        }
        return node;
    }

    private void parsePermutation(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {

        PermutationNode permutation = new PermutationNode(tt.getStartPosition(), tt.getStartPosition());

        Symbol sign = null;
        Syntax syntax = getSyntax(Symbol.PERMUTATION);
        if (syntax == Syntax.PREFIX) {
            sign = this.parsePermutationSign(tt);
        }
        if (tt.nextToken() != Tokenizer.TT_KEYWORD ||
                this.getSymbolFor(tt.getStringValue(), Symbol.PERMUTATION) != Symbol.PERMUTATION_BEGIN) {
            throw createException(tt, "Permutation: Begin expected.");
        }
        if (syntax == Syntax.PRECIRCUMFIX) {
            sign = this.parsePermutationSign(tt);
        }

        PermutationCycle:
        while (true) {
            switch (tt.nextToken()) {
                case Tokenizer.TT_EOF:
                    throw createException(tt, "Permutation: Unexpected EOF.");
                case Tokenizer.TT_KEYWORD:
                    Symbol sym = this.getSymbolFor(tt.getStringValue(), Symbol.PERMUTATION);
                    if (sym == Symbol.PERMUTATION_END) {
                        break PermutationCycle;
                    } else if (sym == null) {
                        throw createException(tt, "Permutation: Illegal symbol.");
                    } else if (sym == Symbol.PERMUTATION_DELIMITER) {
                        // consume
                    } else {
                        tt.pushBack();
                        parsePermutationItem(tt, permutation, syntax);
                    }
                    break;

            }
        }

        if (syntax == Syntax.SUFFIX) {
            sign = this.parsePermutationSign(tt);
        }
        if (syntax != Syntax.POSTCIRCUMFIX) {
            // postcircumfix is read in parsePermutationItem.
            permutation.setSign(sign);
        }
        permutation.setEndPosition(tt.getEndPosition());
        parent.add(permutation);
    }

    /**
     * Parses a permutation sign and returns null or one of the three sign
     * symbols.
     */
    private Symbol parsePermutationSign(Tokenizer t) {
        if (t.nextToken() == Tokenizer.TT_KEYWORD) {
            List<Symbol> symbols = this.getSymbolsFor(t.getStringValue());
            if (this.containsType(symbols, Symbol.PERMUTATION_PLUS)
                    || this.containsType(symbols, Symbol.PERMUTATION_PLUSPLUS)
                    || this.containsType(symbols, Symbol.PERMUTATION_MINUS)) {
                return symbols.get(0);
            }
        }
        t.pushBack();
        return null;
    }

    private Node parsePermutationItem(Tokenizer t, PermutationNode parent, Syntax syntax) throws ParseException {

        int startpos = t.getStartPosition();
        Symbol sign = null;
        int leadingSignStartPos = -1, leadingSignEndPos = -1;
        String partName = "";

        // Evaluate [sign]
        if (syntax == Syntax.PRECIRCUMFIX
                || syntax == Syntax.PREFIX) {
            leadingSignStartPos = t.getStartPosition();
            leadingSignEndPos = t.getEndPosition();
            sign = this.parsePermutationSign(t);
        }
        // Evaluate PermFace [PermFace] [PermFace]
        List<Symbol> faceSymbols = new ArrayList<>(3);

        while (faceSymbols.size() < 3) {
            if (t.nextToken() != Tokenizer.TT_KEYWORD) {
                throw createException(t, "PermutationItem: Face token expected.");
            }
            Symbol symbol = this.getSymbolFor(t.getStringValue(), Symbol.PERMUTATION);
            if (symbol != null && isFaceSymbol(symbol)) {
                partName = partName + t.getStringValue();
                faceSymbols.add(symbol);
            } else {
                t.pushBack();
                break;
            }
        }
        int type = faceSymbols.size();
        if (type == 0) {
            throw createException(t, "PermutationItem: Face expected.");
        }

        if (notation.getLayerCount() < 3 && type < 3) {
            throw new ParseException("PermutationItem: The 2x2 cube does not have a \"" + partName.toString() + "\" part.", startpos, t.getEndPosition());
        }

        // Evaluate [Integer]
        int partNumber = 0;
        if (t.nextToken() == Tokenizer.TT_NUMBER) {
            partNumber = t.getNumericValue();
        } else {
            t.pushBack();
        }
        switch (type) {
            case 3:
                if (partNumber != 0) {
                    throw new ParseException("PermutationItem: Invalid corner part number: " + partNumber, t.getStartPosition(), t.getEndPosition());
                }
                break;
            case 2:
                switch (notation.getLayerCount()) {
                    case 4:
                        if (partNumber < 1 || partNumber > 2) {
                            throw new ParseException("PermutationItem: Invalid edge part number for 4x4 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        partNumber -= 1;
                        break;
                    case 5:
                        if (partNumber < 0 || partNumber > 2) {
                            throw new ParseException("PermutationItem: Invalid edge part number for 5x5 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        break;
                    case 6:
                        if (partNumber < 1 || partNumber > 4) {
                            throw new ParseException("PermutationItem: Invalid edge part number for 6x6 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        partNumber -= 1;
                        break;
                    case 7:
                        if (partNumber < 0 || partNumber > 4) {
                            throw new ParseException("PermutationItem: Invalid edge part number for 7x7 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        break;
                    default:
                        if (partNumber != 0) {
                            throw new ParseException("PermutationItem: Invalid edge part number for 3x3 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        break;
                }
                break;
            case 1:
                switch (notation.getLayerCount()) {
                    case 4:
                        if (partNumber < 1 || partNumber > 4) {
                            throw new ParseException("PermutationItem: Invalid side part number for 4x4 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        partNumber -= 1;
                        break;
                    case 5:
                        if (partNumber < 0 || partNumber > 8) {
                            throw new ParseException("PermutationItem: Invalid side part number for 5x5 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        break;
                    case 6:
                        if (partNumber < 1 || partNumber > 16) {
                            throw new ParseException("PermutationItem: Invalid side part number for 6x6 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        partNumber -= 1;
                        break;
                    case 7:
                        if (partNumber < 0 || partNumber > 24) {
                            throw new ParseException("PermutationItem: Invalid side part number for 7x7 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        break;
                    default:
                        if (partNumber != 0) {
                            throw new ParseException("PermutationItem: Invalid side part number for 3x3 cube: " + partNumber, t.getStartPosition(), t.getEndPosition());
                        }
                        break;
                }
                break;
        }
        // The Integer token is now done.

        // Evaluate [sign]
        if ((syntax == Syntax.POSTCIRCUMFIX || syntax == Syntax.SUFFIX)) {
            sign = parsePermutationSign(t);
        }

        parent.addPermItem(type, sign, faceSymbols.toArray(new Symbol[faceSymbols.size()]), partNumber, notation.getLayerCount());
        return parent;
    }

    private boolean isFaceSymbol(Symbol symbol) {
        switch (symbol) {
            case PERMUTATION_FACE_R:
            case PERMUTATION_FACE_U:
            case PERMUTATION_FACE_F:
            case PEMRUTATION_FACE_L:
            case PERMUTATION_FACE_D:
            case PERMUTATION_FACE_B:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the first intersecting symbol of symbols with types.
     *
     * @param symbols array of symbols
     * @param types   type desired type
     * @return the first symbol that is of the desired type or null
     */
    private Symbol getFirstIntersectingType(List<Symbol> symbols, Set<Symbol> types) {
        for (Symbol s : symbols) {
            for (Symbol type : types) {
                if (s == type) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if the array contains a symbol of the specified symbol type
     *
     * @param symbols array of symbols
     * @param type    compound symbol
     * @return true if array contains a symbol of the specified type
     */
    private boolean containsType(List<Symbol> symbols, Symbol type) {
        for (int i = 0; i < symbols.size(); i++) {
            Symbol s = symbols.get(i);
            if (type.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private ParseException createException(Tokenizer tt, String msg) {
        return new ParseException(msg + " Found \"" + tt.getStringValue() + "\".", tt.getStartPosition(), tt.getEndPosition());
    }

    private Tokenizer createTokenizer(Notation notation) {
        Tokenizer tt = new Tokenizer();
        tt.addNumbers();
        tt.skipWhitespace();

        for (String token : notation.getTokens()) {
            tt.addKeyword(token);
        }
        for (Map.Entry<String, MacroNode> e : localMacros.entrySet()) {
            tt.addKeyword(e.getKey());
        }

        String mbegin = notation.getToken(Symbol.MULTILINE_COMMENT_BEGIN);
        String mend = notation.getToken(Symbol.MULTILINE_COMMENT_END);
        if (mbegin != null && mend != null && !mbegin.isEmpty() && !mend.isEmpty()) {
            tt.addComment(mbegin, mend);
        }
        String sbegin = notation.getToken(Symbol.SINGLELINE_COMMENT_BEGIN);
        if (sbegin != null && !sbegin.isEmpty()) {
            tt.addComment(sbegin, "\n");
        }

        return tt;
    }
}

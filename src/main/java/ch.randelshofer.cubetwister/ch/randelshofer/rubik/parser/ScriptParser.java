package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
 * Script           = Sequence ;
 *
 * Sequence         = { Statement } ;
 *
 * Statement        = Primary
 *                  | Prefix
 *                  | Suffix
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
 * Circumfix        = Begin, Sequence , End ;
 * Preinfix         = Statement, Operator, Statement ;
 * Postinfix        = Statement, Operator, Statement ;
 * Precircumfix     = Begin, Sequence , Delimiter, Sequence , End ;
 * Postcircumfix    = Begin, Sequence , Delimiter, Sequence  , End ;
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
 * PrecircumfixRepetition  = Begin, Number, Delimiter, Script  , End ;
 * PostcircumfixRepetition = Begin, Script , Delimiter, Number , End ;
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

    private Node createBinaryNode(Tokenizer tt, BinaryNode binary, Node operand1, Node operand2) throws ParseException {
        if (operand1 == null || operand2 == null) {
            throw createException(tt, "Binary: Two operands expected.");
        }
        binary.add(operand1);
        binary.add(operand2);
        return binary;
    }

    private Node createCompositeNode(Tokenizer tt, Symbol operation, Node operand1, Node operand2) throws ParseException {
        Node node;
        switch (operation.getCompositeSymbol()) {
            case GROUPING:
                node = createUnaryNode(tt, new GroupingNode(), operand1, operand2);
                break;
            case INVERSION:
                node = createUnaryNode(tt, new InversionNode(), operand1, operand2);
                break;
            case REFLECTION:
                node = createUnaryNode(tt, new ReflectionNode(), operand1, operand2);
                break;
            case REPETITION:
                node = createRepetitionNode(tt, operand1, operand2);
                break;
            case ROTATION:
                node = createBinaryNode(tt, new RotationNode(), operand1, operand2);
                break;
            case COMMUTATION:
                node = createBinaryNode(tt, new CommutationNode(), operand1, operand2);
                break;
            case CONJUGATION:
                node = createBinaryNode(tt, new ConjugationNode(), operand1, operand2);
                break;
            default:
                throw new AssertionError("Composite. Unexpected operation: " + operation);
        }
        return node;
    }

    private ParseException createException(Tokenizer tt, String msg) {
        return new ParseException(msg + " Found \"" + tt.getStringValue() + "\".", tt.getStartPosition(), tt.getEndPosition());
    }

    private Node createRepetitionNode(Tokenizer tt, Node operand1, Node operand2) throws ParseException {
        if (operand1 == null || operand2 != null) {
            throw createException(tt, "Repetition: One operand expected.");
        }
        Node n = new RepetitionNode();
        n.add(operand1);
        return n;
    }

    private Tokenizer createTokenizer(Notation notation) {
        var tt = new Tokenizer();
        tt.addNumbers();
        tt.skipWhitespace();

        for (var token : notation.getTokens()) {
            tt.addKeyword(token);
        }
        for (var identifier : localMacros.keySet()) {
            tt.addKeyword(identifier);
        }

        var mbegin = notation.getToken(Symbol.MULTILINE_COMMENT_BEGIN);
        var mend = notation.getToken(Symbol.MULTILINE_COMMENT_END);
        if (mbegin != null && mend != null && !mbegin.isEmpty() && !mend.isEmpty()) {
            tt.addComment(mbegin, mend);
        }
        var sbegin = notation.getToken(Symbol.SINGLELINE_COMMENT_BEGIN);
        if (sbegin != null && !sbegin.isEmpty()) {
            tt.addComment(sbegin, "\n");
        }

        return tt;
    }

    private Node createUnaryNode(Tokenizer tt, UnaryNode unary, Node operand1, Node operand2) throws ParseException {
        if (operand1 == null || operand2 != null) {
            throw createException(tt, "Unary: One operand expected.");
        }
        if (operand1 instanceof SequenceNode) {
            unary.addAll(new ArrayList<>(operand1.getChildren()));
        } else {
            unary.add(operand1);
        }
        return unary;
    }

    public Notation getNotation() {
        return notation;
    }

    public Node parse(String input) throws ParseException {
        var tt = this.createTokenizer(this.notation);
        tt.setInput(input);
        return parseScript(tt);
    }

    private void parseCircumfix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        var startPos = tt.getStartPosition();
        var operand1 = parseCircumfixOperand(tt, symbol);
        var compositeNode = createCompositeNode(tt, symbol, operand1, null);
        compositeNode.setStartPosition(startPos);
        compositeNode.setEndPosition(tt.getEndPosition());
        parent.add(compositeNode);
    }

    private Node parseCircumfixOperand(Tokenizer tt, Symbol symbol) throws ParseException {
        var nodes = parseCircumfixOperands(tt, symbol);
        if (nodes.size() != 1) {
            throw createException(tt, "Circumfix: Exactly one operand expected.");
        }
        return nodes.get(0);
    }

    private List<Node> parseCircumfixOperands(Tokenizer tt, Symbol symbol) throws ParseException {
        if (!Symbol.isBegin(symbol)) {
            throw createException(tt, "Circumfix: Begin expected.");
        }
        var compositeSymbol = symbol.getCompositeSymbol();
        List<Node> operands = new ArrayList<>();
        var operand = new SequenceNode();
        operand.setStartPosition(tt.getEndPosition());
        operands.add(operand);
        Loop:
        while (true) {
            switch (tt.nextToken()) {
                case Tokenizer.TT_NUMBER:
                    tt.pushBack();
                    parseStatement(tt, operand);
                    break;
                case Tokenizer.TT_KEYWORD:
                    var maybeSeparatorOrEnd = tt.getStringValue();
                    for (var symbol1 : this.notation.getSymbolsFor(maybeSeparatorOrEnd)) {
                        if (symbol1.getCompositeSymbol().equals(compositeSymbol)) {
                            if (Symbol.isDelimiter(symbol1)) {
                                operand.setEndPosition(tt.getStartPosition());
                                operand = new SequenceNode();
                                operand.setStartPosition(tt.getEndPosition());
                                operands.add(operand);
                                continue Loop;
                            } else if (Symbol.isEnd(symbol1)) {
                                break Loop;
                            }
                        }
                    }
                    tt.pushBack();
                    parseStatement(tt, operand);
                    break;
                default:
                    throw createException(tt, "Circumfix: Number, Keyword or End expected.");
            }
        }
        operand.setEndPosition(tt.getStartPosition());
        return operands;
    }

    /**
     * Progressively parses a statement with a syntax that is known not to
     * be of type {@link Syntax#SUFFIX}.
     * <p>
     * This method tries out to parse the given token with the given syntax.
     * <p>
     * On success, this method (or a method called from it) either adds a
     * new child to the parent or replaces the last child.
     *
     * @param tt the tokenizer
     * @param parent the parent of the statement
     * @param token the current token
     * @param symbol the symbol that we want to try out
     * @throws ParseException on parse failure
     */
    private void parseNonSuffix(Tokenizer tt, Node parent, String token, Symbol symbol) throws ParseException {
        var c = symbol.getCompositeSymbol();
        if (c == Symbol.PERMUTATION) {
            tt.pushBack();
            parsePermutation(tt, parent);
            return;
        }

        var syntax = notation.getSyntax(symbol);
        switch (syntax) {
            case PRIMARY:
                parsePrimary(tt, parent, token, symbol);
                break;
            case PREFIX:
                parsePrefix(tt, parent, symbol);
                break;
            case CIRCUMFIX:
                parseCircumfix(tt, parent, symbol);
                break;
            case PRECIRCUMFIX:
                parsePrecircumfix(tt, parent, symbol);
                break;
            case POSTCIRCUMFIX:
                parsePostcircumfix(tt, parent, symbol);
                break;
            case PREINFIX:
                parsePreinfix(tt, parent, symbol);
                break;
            case POSTINFIX:
                parsePostinfix(tt, parent, symbol);
                break;
            default:
                throw createException(tt, "Unexpected Syntax: " + syntax);
        }
    }

    /**
     * Progressively parses a statement with a syntax that is known not to
     * be of type {@link Syntax#SUFFIX}.
     * <p>
     * This method tries out all symbols that could work for the next token
     * of the tokenizer. If a symbol does not work out, the method backtracks
     * and tries the next symbol.
     * <p>
     * On success, this method (or a method called from it) either adds a
     * new child to the parent or replaces the last child.
     *
     * @param tt     the tokenizer
     * @param parent the parent of the statement
     * @throws ParseException
     */
    private void parseNonSuffixOrBacktrack(Tokenizer tt, Node parent) throws ParseException {
        if (tt.nextToken() != Tokenizer.TT_KEYWORD) {
            throw createException(tt, "Statement: Keyword expected.");
        }

        // Backtracking algorithm: try out each possible symbol for the given token.
        ParseException e = null;
        List<Node> savedChildren = new ArrayList<>(parent.getChildren());
        var savedTokenizer = new Tokenizer();
        savedTokenizer.setTo(tt);
        var token = tt.getStringValue();
        for (var symbol : this.notation.getSymbolsFor(token)) {
            try {
                parseNonSuffix(tt, parent, token, symbol);
                // Parse was successful
                return;
            } catch (ParseException pe) {
                // Parse failed: backtrack and try with another symbol.
                tt.setTo(savedTokenizer);
                parent.removeAllChildren();
                parent.addAll(savedChildren);
                if (e == null || e.getEndPosition() < pe.getEndPosition()) {
                    e = pe;
                }
            }
        }
        throw (e != null) ? e : createException(tt, "Statement: Illegal token.");
    }

    private void parsePermutation(Tokenizer tt, Node parent) throws ParseException {
        var permutation = new PermutationNode(tt.getStartPosition(), tt.getStartPosition());

        Symbol sign = null;
        var syntax = notation.getSyntax(Symbol.PERMUTATION);
        if (syntax == Syntax.PREFIX) {
            sign = this.parsePermutationSign(tt);
        }
        if (tt.nextToken() != Tokenizer.TT_KEYWORD ||
                this.notation.getSymbolFor(tt.getStringValue(), Symbol.PERMUTATION) != Symbol.PERMUTATION_BEGIN) {
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
                    var sym = this.notation.getSymbolFor(tt.getStringValue(), Symbol.PERMUTATION);
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

    private List<Symbol> parsePermutationFaces(Tokenizer t) throws ParseException {
        List<Symbol> faceSymbols = new ArrayList<>(3);
        while (true) {
            if (t.nextToken() == Tokenizer.TT_KEYWORD) {
                var symbol = this.notation.getSymbolFor(t.getStringValue(), Symbol.PERMUTATION);
                if (symbol != null && Symbol.isFaceSymbol(symbol)) {
                    faceSymbols.add(symbol);
                    continue;
                }
            }
            break;
        }
        t.pushBack();

        var type = faceSymbols.size();
        if (type == 0) {
            throw createException(t, "PermutationItem: Face expected.");
        }
        if (notation.getLayerCount() < 3 && type < 3) {
            throw createException(t, "PermutationItem: The 2x2 cube only has corner parts.");
        }

        return faceSymbols;
    }

    private void parsePermutationItem(Tokenizer t, PermutationNode parent, Syntax syntax) throws ParseException {
        Symbol sign = null;

        if (syntax == Syntax.PRECIRCUMFIX || syntax == Syntax.PREFIX) {
            sign = this.parsePermutationSign(t);
        }

        var layerCount = notation.getLayerCount();
        var faceSymbols = parsePermutationFaces(t);
        var partNumber = parsePermutationPartNumber(t, layerCount, faceSymbols.size());

        if ((syntax == Syntax.POSTCIRCUMFIX || syntax == Syntax.SUFFIX)) {
            sign = parsePermutationSign(t);
        }

        parent.addPermItem(faceSymbols.size(), sign, faceSymbols, partNumber, layerCount);
    }

    private int parsePermutationPartNumber(Tokenizer t, int layerCount, int type) throws ParseException {
        var partNumber = 0;
        if (t.nextToken() == Tokenizer.TT_NUMBER) {
            partNumber = t.getNumericValue();
        } else {
            t.pushBack();
        }
        switch (type) {
            case 3:
                if (partNumber != 0) {
                    throw createException(t, "PermutationItem: Invalid corner part number: " + partNumber);
                }
                break;
            case 2: {
                boolean valid;
                switch (layerCount) {
                    case 4:
                        valid = 1 <= partNumber && partNumber <= 2;
                        break;
                    case 5:
                        valid = 0 <= partNumber && partNumber <= 2;
                        break;
                    case 6:
                        valid = 1 <= partNumber && partNumber <= 4;
                        break;
                    case 7:
                        valid = 0 <= partNumber && partNumber <= 4;
                        break;
                    default:
                        valid = partNumber == 0;
                        break;
                }
                if (!valid) {
                    throw createException(t, "PermutationItem: Invalid edge part number: " + partNumber);
                }
                switch (layerCount) {
                    case 4:
                    case 6:
                        partNumber -= 1;
                        break;
                }
                break;
            }
            case 1: {
                boolean valid;
                switch (layerCount) {
                    case 4:
                        valid = 1 <= partNumber && partNumber <= 4;
                        break;
                    case 5:
                        valid = 0 <= partNumber && partNumber <= 8;
                        break;
                    case 6:
                        valid = 1 <= partNumber && partNumber <= 16;
                        break;
                    case 7:
                        valid = 0 <= partNumber && partNumber <= 24;
                        break;
                    default:
                        valid = partNumber == 0;
                        break;
                }
                if (!valid) {
                    throw createException(t, "PermutationItem: Invalid side part number: " + partNumber);
                }
                switch (layerCount) {
                    case 4:
                    case 6:
                        partNumber -= 1;
                        break;
                }
                break;
            }
        }
        return partNumber;
    }

    /**
     * Parses a permutation sign and returns null or one of the three sign
     * symbols.
     */
    private Symbol parsePermutationSign(Tokenizer t) {
        if (t.nextToken() == Tokenizer.TT_KEYWORD) {
            var symbol = this.notation.getSymbolFor(t.getStringValue(), Symbol.PERMUTATION);
            if (symbol != null && Symbol.isPermutationSign(symbol)) {
                return symbol;
            }
        }
        t.pushBack();
        return null;
    }

    private void parsePostcircumfix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        var start = tt.getStartPosition();
        var operands = parseCircumfixOperands(tt, symbol);
        if (operands.size() != 2) {
            throw createException(tt, "Postcircumfix: Two operands expected.");
        }
        var end = tt.getEndPosition();
        var node = createCompositeNode(tt, symbol, operands.get(1), operands.get(0));
        node.setStartPosition(start);
        node.setEndPosition(end);
        parent.add(node);
    }

    /**
     * Replaces the last child of parent with a post-infix expression.
     *
     * @param tt     the tokenizer
     * @param parent the parent
     * @param symbol the symbol with post-infix syntax
     * @throws ParseException on parsing failure
     */
    private void parsePostinfix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        if (parent.getChildCount() == 0) {
            throw createException(tt, "Postinfix: Operand expected.");
        }
        var operand2 = parent.getChildAt(parent.getChildCount() - 1);
        Node node;
        if (symbol.getCompositeSymbol() == Symbol.REPETITION) {
            if (tt.nextToken() != Tokenizer.TT_NUMBER) {
                throw new ParseException("Repetition: Repetition count expected.", tt.getStartPosition(), tt.getEndPosition());
            }
            node = createCompositeNode(tt, symbol, operand2, null);
            ((RepetitionNode) node).setRepeatCount(tt.getNumericValue());
        } else {
            Node tempParent = new SequenceNode();
            parseStatement(tt, tempParent);
            var operand1 = tempParent.getChildAt(0);
            node = createCompositeNode(tt, symbol, operand1, operand2);
        }
        node.setStartPosition(operand2.getStartPosition());
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    private void parsePrecircumfix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        var start = tt.getStartPosition();
        var operands = parseCircumfixOperands(tt, symbol);
        if (operands.size() != 2) {
            throw createException(tt, "Precircumfix: Two operands expected.");
        }
        var end = tt.getEndPosition();
        var node = createCompositeNode(tt, symbol, operands.get(0), operands.get(1));
        node.setStartPosition(start);
        node.setEndPosition(end);
        parent.add(node);
    }

    private void parsePrefix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        var startPosition = tt.getStartPosition();
        Node node;
        if (Symbol.isBegin(symbol)) {
            var operand1 = parseCircumfixOperand(tt, symbol);
            Node tempParent = new SequenceNode();
            parseStatement(tt, tempParent);
            var operand2 = tempParent.getChildAt(0);
            node = createCompositeNode(tt, symbol, operand1, operand2);
        } else if (Symbol.isOperator(symbol)) {
            Node operand1 = new SequenceNode();
            parseStatement(tt, operand1);
            node = createCompositeNode(tt, symbol, operand1, null);
        } else {
            throw createException(tt, "Prefix: Begin or Operator expected.");
        }
        node.setStartPosition(startPosition);
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    /**
     * Replaces the last child of parent with a pre-infix expression.
     *
     * @param tt     the tokenizer
     * @param parent the parent
     * @param symbol the symbol with pre-infix syntax
     * @throws ParseException on parsing failure
     */
    private void parsePreinfix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        if (parent.getChildCount() == 0) {
            throw createException(tt, "Preinfix: Operand expected.");
        }
        var operand1 = parent.getChildAt(parent.getChildCount() - 1);
        Node tempParent = new SequenceNode();
        parseStatement(tt, tempParent);
        var operand2 = tempParent.getChildAt(0);
        var node = createCompositeNode(tt, symbol, operand1, operand2);
        node.setStartPosition(operand1.getStartPosition());
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
                var move = notation.getMoveFromToken(token);
                child = new MoveNode(move.getLayerCount(), move.getAxis(), move.getLayerMask(), move.getAngle(),
                        tt.getStartPosition(), tt.getEndPosition());
                break;
            case MACRO:
                // Expand macro
                try {
                    var macro = notation.getMacro(token);
                    var macroScript = parse(macro);
                    var macroNode = new MacroNode(null, token, tt.getStartPosition(), tt.getEndPosition());
                    for (Node node : macroScript.preorderIterable()) {
                        node.setStartPosition(tt.getStartPosition());
                        node.setEndPosition(tt.getEndPosition());
                    }

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

    /**
     * Adds a child to the parent or (if the repetition has suffix syntax)
     * replaces the last child of the parent.
     *
     * @param tt     the tokenizer
     * @param parent the parent
     * @throws ParseException on parsing failure
     */
    private void parseRepetition(Tokenizer tt, Node parent) throws ParseException {
        if (tt.nextToken() != Tokenizer.TT_NUMBER) {
            throw new ParseException("Repetition: Number expected.", tt.getStartPosition(), tt.getEndPosition());
        }
        var start = tt.getStartPosition();
        int repeatCount = tt.getNumericValue();
        var syntax = notation.getSyntax(Symbol.REPETITION);
        var operand = new SequenceNode();
        switch (syntax) {
            case PREFIX:
                parseStatement(tt, operand);
                break;
            case SUFFIX: {
                if (parent.getChildCount() < 1) {
                    throw createException(tt, "Repetition: Operand missing.");
                }
                var sibling = parent.getChildAt(parent.getChildCount() - 1);
                start = sibling.getStartPosition();
                operand.add(sibling);
                break;
            }
            case PREINFIX:
                if (tt.nextToken() != Tokenizer.TT_KEYWORD
                        || !this.notation.getSymbolsFor(tt.getStringValue()).contains(Symbol.REPETITION_OPERATOR)) {
                    throw createException(tt, "Repetition: Operator expected.");
                }
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
        var repetitionNode = new RepetitionNode();
        repetitionNode.addAll(new ArrayList<>(operand.getChildren()));
        repetitionNode.setRepeatCount(repeatCount);
        repetitionNode.setStartPosition(start);
        repetitionNode.setEndPosition(tt.getEndPosition());
        parent.add(repetitionNode);
    }

    private SequenceNode parseScript(Tokenizer tt) throws ParseException {
        var script = new SequenceNode();
        script.setStartPosition(tt.getStartPosition());
        while (tt.nextToken() != Tokenizer.TT_EOF) {
            tt.pushBack();
            parseStatement(tt, script);
        }
        script.setEndPosition(tt.getEndPosition());
        return script;
    }

    /**
     * Progressively parses a statement.
     * <p>
     * This method either adds a new child to the parent or replaces the last
     * child.
     *
     * @param tt     the tokenizer
     * @param parent the parent of the statement
     * @throws ParseException
     */
    private void parseStatement(Tokenizer tt, Node parent) throws ParseException {
        switch (tt.nextToken()) {
            case Tokenizer.TT_NUMBER:
                tt.pushBack();
                parseRepetition(tt, parent);
                break;
            case Tokenizer.TT_KEYWORD:
                tt.pushBack();
                parseNonSuffixOrBacktrack(tt, parent);
                break;
            default:
                throw createException(tt, "Statement: Keyword or Number expected.");
        }

        // We parse suffix expressions here, so that they have precedence over
        // other expressions.
        parseSuffixes(tt, parent);
    }

    /**
     * Replaces the last child of parent with a suffix expression.
     *
     * @param tt     the tokenizer
     * @param parent the parent
     * @param symbol the symbol with suffix syntax
     * @throws ParseException on parsing failure
     */
    private void parseSuffix(Tokenizer tt, Node parent, Symbol symbol) throws ParseException {
        if (parent.getChildCount() < 1) {
            throw new ParseException("Suffix: No sibling for suffix.", tt.getStartPosition(), tt.getEndPosition());
        }
        var sibling = parent.getChildAt(parent.getChildCount() - 1);
        var startPosition = sibling.getStartPosition();
        Node node;
        if (Symbol.isBegin(symbol)) {
            var operand1 = parseCircumfixOperand(tt, symbol);
            node = createCompositeNode(tt, symbol, operand1, sibling);
        } else if (Symbol.isOperator(symbol)) {
            node = createCompositeNode(tt, symbol, sibling, null);
        } else {
            throw createException(tt, "Suffix: Begin or Operator expected.");
        }
        node.setStartPosition(startPosition);
        node.setEndPosition(tt.getEndPosition());
        parent.add(node);
    }

    /**
     * Tries to replace the last child of the parent with a suffix expression(s).
     * <p>
     * This method replaces the last child of the parent, each time a
     * suffix has been parsed succesfully.
     *
     * @param tt     the tokenizer
     * @param parent the parent of the statement
     */
    private void parseSuffixes(Tokenizer tt, Node parent) {
        var savedTT = new Tokenizer();
        savedTT.setTo(tt);
        Outer:
        while (true) {
            if (tt.nextToken() == Tokenizer.TT_KEYWORD) {
                var token = tt.getStringValue();

                // Backtracking algorithm: try out each possible symbol for the given token.
                for (var symbol : this.notation.getSymbolsFor(token)) {
                    if (symbol.getCompositeSymbol() != Symbol.PERMUTATION
                            && notation.getSyntax(symbol) == Syntax.SUFFIX) {

                        try {
                            parseSuffix(tt, parent, symbol);
                            // Success: parse next suffix.
                            savedTT.setTo(tt);
                            continue Outer;
                        } catch (ParseException e) {
                            // Failure: backtrack and try another symbol.
                            tt.setTo(savedTT);
                        }

                    }
                }
            } else if (tt.getTokenType() == Tokenizer.TT_NUMBER
                    && notation.getSyntax(Symbol.REPETITION) == Syntax.SUFFIX) {
                try {
                    tt.pushBack();
                    parseRepetition(tt, parent);
                    savedTT.setTo(tt);
                    continue;
                } catch (ParseException e) {
                    // Failure: try with another symbol.
                    tt.setTo(savedTT);
                }
            }
            // We failed with all symbols that we tried out.
            break;
        }
        tt.setTo(savedTT);
    }
}

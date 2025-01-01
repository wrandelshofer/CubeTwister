/*
 * @(#)ScriptParser.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.notation.ScriptNotation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.parser.ast.BinaryNode;
import ch.randelshofer.rubik.parser.ast.CommutationNode;
import ch.randelshofer.rubik.parser.ast.ConjugationNode;
import ch.randelshofer.rubik.parser.ast.GroupingNode;
import ch.randelshofer.rubik.parser.ast.InversionNode;
import ch.randelshofer.rubik.parser.ast.MacroNode;
import ch.randelshofer.rubik.parser.ast.MoveNode;
import ch.randelshofer.rubik.parser.ast.NOPNode;
import ch.randelshofer.rubik.parser.ast.Node;
import ch.randelshofer.rubik.parser.ast.PermutationCycleNode;
import ch.randelshofer.rubik.parser.ast.ReflectionNode;
import ch.randelshofer.rubik.parser.ast.RepetitionNode;
import ch.randelshofer.rubik.parser.ast.RotationNode;
import ch.randelshofer.rubik.parser.ast.SequenceNode;
import ch.randelshofer.rubik.parser.ast.UnaryNode;
import ch.randelshofer.rubik.tokenizer.Tokenizer;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parser for Rubik's Cube scripts. The tokens and syntax-rules used by the
 * parser are read from a Notation object.<p>
 * <p>
 * The parser supports the EBNF ISO/IEC 14977 productions shown below.
 * The {@link Syntax}  of the productions is read from a {@link ScriptNotation} object.
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
 * Prefix           = UnaryPrefix | BinaryPrefix ;
 * Suffix           = UnarySuffix | BinarySuffix ;
 * Precircumfix     = BinaryPrecircumfix ;
 * Postcircumfix    = BinaryPostcircumfix ;
 * Preinfix         = BinaryPreinfix ;
 * Postinfix        = BinaryPostinfix ;
 *
 * UnaryPrefix      = Operator , Statement ;
 * UnarySuffix      = Statement , Operator ;
 * UnaryCircumfix   = Begin, Sequence , End ;
 * Unary            = UnaryPrefix
 *                  | UnarySuffix
 *                  | UnaryCircumfix
 *                  ;
 *
 * BinaryPrefix        = Begin, Sequence, End, Statement;
 * BinarySuffix        = Statement, Begin, Sequence, End;
 * BinaryPrecircumfix  = Begin, Sequence , Delimiter, Sequence , End ;
 * BinaryPostcircumfix = Begin, Sequence , Delimiter, Sequence  , End ;
 * BinaryPreinfix      = Statement, Operator, Statement
 * BinaryPostinfix     = Statement, Operator, Statement
 * Binary           = BinaryPrefix
 *                  | BinarySuffix
 *                  | BinaryPreinfix
 *                  | BinaryPostinfix
 *                  | BinaryPrecircumfix
 *                  | BinaryPostcircumfix
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
 * PrecircumfixPermutation   = Begin, [ [ PermSign ], PrefixPermItem { PermDelim , PrefixPermItem } ] , End ;
 * PrefixPermutation         = [ PermSign ], Begin, [ PrefixPermItem { PermDelim , PrefixPermItem } ] , End ;
 * PostcircumfixPermutation  = Begin, [ SuffixPermItem { PermDelim , SuffixPermItem } ] , [ PermSign ], End ;
 * PostfixPermutation        = Begin, [ SuffixPermItem { PermDelim , SuffixPermItem } ] , End , [ PermSign ] ;
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
    private ScriptNotation notation;
    private Map<String, MacroNode> localMacros;

    public ScriptParser(ScriptNotation notation) {
        this(notation, Collections.emptyList());
    }

    public ScriptParser(ScriptNotation notation, @Nonnull List<MacroNode> localMacros) {
        this.notation = notation;
        this.localMacros = localMacros.stream().collect(
                Collectors.toMap(MacroNode::getIdentifier, Function.identity()));
    }

    @Nonnull
    private Node createBinaryNode(@Nonnull Tokenizer tt, @Nonnull BinaryNode binary, @Nullable Node operand1, @Nullable Node operand2) throws ParseException {
        if (operand1 == null || operand2 == null) {
            throw createException(tt, "Binary: Two operands expected.");
        }
        binary.add(operand1);
        binary.add(operand2);
        return binary;
    }

    private Node createCompositeNode(@Nonnull Tokenizer tt, @Nonnull Symbol operation, Node operand1, Node operand2) throws ParseException {
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

    @Nonnull
    private ParseException createException(@Nonnull Tokenizer tt, String msg) {
        return new ParseException(msg + " Found \"" + tt.getStringValue() + "\".", tt.getStartPosition(), tt.getEndPosition());
    }

    @Nonnull
    private Node createRepetitionNode(@Nonnull Tokenizer tt, @Nullable Node operand1, @Nullable Node operand2) throws ParseException {
        if (operand1 == null || operand2 != null) {
            throw createException(tt, "Repetition: One operand expected.");
        }
        Node n = new RepetitionNode();
        n.add(operand1);
        return n;
    }

    @Nonnull
    private Tokenizer createTokenizer(@Nonnull ScriptNotation notation) {
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

    @Nonnull
    private Node createUnaryNode(@Nonnull Tokenizer tt, @Nonnull UnaryNode unary, @Nullable Node operand1, @Nullable Node operand2) throws ParseException {
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

    public ScriptNotation getNotation() {
        return notation;
    }

    @Nonnull
    public Node parse(@Nonnull String input) throws ParseException {
        Objects.requireNonNull(input, "input");
        var tt = this.createTokenizer(this.notation);
        tt.setInput(input);
        return parseScript(tt);
    }

    private void parseCircumfix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
        var startPos = tt.getStartPosition();
        var operand1 = parseCircumfixOperand(tt, symbol);
        var compositeNode = createCompositeNode(tt, symbol, operand1, null);
        compositeNode.setStartPosition(startPos);
        compositeNode.setEndPosition(tt.getEndPosition());
        parent.add(compositeNode);
    }

    private Node parseCircumfixOperand(@Nonnull Tokenizer tt, @Nonnull Symbol symbol) throws ParseException {
        var nodes = parseCircumfixOperands(tt, symbol);
        if (nodes.size() != 1) {
            throw createException(tt, "Circumfix: Exactly one operand expected.");
        }
        return nodes.get(0);
    }

    @Nonnull
    private List<Node> parseCircumfixOperands(@Nonnull Tokenizer tt, @Nonnull Symbol symbol) throws ParseException {
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
                for (var symbol1 : this.notation.getSymbols(maybeSeparatorOrEnd)) {
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
    private void parseNonSuffix(@Nonnull Tokenizer tt, @Nonnull Node parent, String token, @Nonnull Symbol symbol) throws ParseException {
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
    private void parseNonSuffixOrBacktrack(@Nonnull Tokenizer tt, @Nonnull Node parent) throws ParseException {
        if (tt.nextToken() != Tokenizer.TT_KEYWORD) {
            throw createException(tt, "Statement: Keyword expected.");
        }

        // Backtracking algorithm: try out each possible symbol for the given token.
        ParseException e = null;
        List<Node> savedChildren = new ArrayList<>(parent.getChildren());
        var savedTokenizer = new Tokenizer();
        savedTokenizer.setTo(tt);
        var token = tt.getStringValue();
        for (var symbol : this.notation.getSymbols(token)) {
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

    private void parsePermutation(@Nonnull Tokenizer tt, @Nonnull Node parent) throws ParseException {
        var permutation = new PermutationCycleNode(tt.getStartPosition(), tt.getStartPosition());

        Symbol sign = null;
        var syntax = notation.getSyntax(Symbol.PERMUTATION);
        if (syntax == Syntax.PREFIX) {
            sign = this.parsePermutationSign(tt);
        }
        if (tt.nextToken() != Tokenizer.TT_KEYWORD ||
                this.notation.getSymbolInCompositeSymbol(tt.getStringValue(), Symbol.PERMUTATION) != Symbol.PERMUTATION_BEGIN) {
            throw createException(tt, "Permutation: Begin expected.");
        }
        if (syntax == Syntax.PRECIRCUMFIX) {
            sign = this.parsePermutationSign(tt);
        }

        PermutationCycle:
        while (true) {
            switch (tt.nextToken()) {
            case Tokenizer.TT_KEYWORD:
                var sym = this.notation.getSymbolInCompositeSymbol(tt.getStringValue(), Symbol.PERMUTATION);
                if (sym == Symbol.PERMUTATION_END) {
                    break PermutationCycle;
                } else if (sym == null) {
                    throw createException(tt, "Permutation: PermutationItem expected.");
                } else if (sym == Symbol.PERMUTATION_DELIMITER) {
                    // consume
                } else {
                    tt.pushBack();
                    parsePermutationItem(tt, permutation, syntax);
                }
                break;
            default:
                throw createException(tt, "Permutation: PermutationItem expected.");

            }
        }

        if (syntax == Syntax.SUFFIX) {
            sign = this.parsePermutationSign(tt);
        }
        if (syntax != Syntax.POSTCIRCUMFIX) {
            // postcircumfix is read in parsePermutationItem.
            permutation.setSignSymbol(sign);
        }
        permutation.setEndPosition(tt.getEndPosition());
        parent.add(permutation);
    }

    @Nonnull
    private List<Symbol> parsePermutationFaces(@Nonnull Tokenizer t) throws ParseException {
        List<Symbol> faceSymbols = new ArrayList<>(3);
        while (true) {
            if (t.nextToken() == Tokenizer.TT_KEYWORD) {
                var symbol = this.notation.getSymbolInCompositeSymbol(t.getStringValue(), Symbol.PERMUTATION);
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

    private void parsePermutationItem(@Nonnull Tokenizer t, @Nonnull PermutationCycleNode parent, Syntax syntax) throws ParseException {
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

    private int parsePermutationPartNumber(@Nonnull Tokenizer t, int layerCount, int type) throws ParseException {
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
    @Nullable
    private Symbol parsePermutationSign(@Nonnull Tokenizer t) {
        if (t.nextToken() == Tokenizer.TT_KEYWORD) {
            var symbol = this.notation.getSymbolInCompositeSymbol(t.getStringValue(), Symbol.PERMUTATION);
            if (symbol != null && Symbol.isPermutationSign(symbol)) {
                return symbol;
            }
        }
        t.pushBack();
        return null;
    }

    private void parsePostcircumfix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
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
    private void parsePostinfix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
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

    private void parsePrecircumfix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
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

    /**
     * Adds a Prefix expression to parent,
     * if we encountered a BEGIN symbol we parse a BinaryPrefix,
     * if we encountered an OPERATOR symbol we parse a UnaryPrefix.
     * <p>
     * Parses a BinaryPrefix or a UnaryPrefix depending on whether we have encountered
     * a BEGIN symbol or an OPERATOR symbol.
     */
    private void parsePrefix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
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
    private void parsePreinfix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
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

    private void parsePrimary(@Nonnull Tokenizer tt, @Nonnull Node parent, String token, @Nonnull Symbol symbol) throws ParseException {
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
    private void parseRepetition(@Nonnull Tokenizer tt, @Nonnull Node parent) throws ParseException {
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
                    || !this.notation.getSymbols(tt.getStringValue()).contains(Symbol.REPETITION_OPERATOR)) {
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

    @Nonnull
    private SequenceNode parseScript(@Nonnull Tokenizer tt) throws ParseException {
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
    private void parseStatement(@Nonnull Tokenizer tt, @Nonnull Node parent) throws ParseException {
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
     * Replaces the last child of parent with a Suffix expression,
     * if we encountered a BEGIN symbol we parse a BinarySuffix,
     * if we encountered an OPERATOR symbol we parse a UnarySuffix.
     *
     * @param tt     the tokenizer
     * @param parent the parent
     * @param symbol the symbol with suffix syntax
     * @throws ParseException on parsing failure
     */
    private void parseSuffix(@Nonnull Tokenizer tt, @Nonnull Node parent, @Nonnull Symbol symbol) throws ParseException {
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
     * suffix has been parsed successfully.
     *
     * @param tt     the tokenizer
     * @param parent the parent of the statement
     */
    private void parseSuffixes(@Nonnull Tokenizer tt, @Nonnull Node parent) {
        var savedTT = new Tokenizer();
        savedTT.setTo(tt);
        Outer:
        while (true) {
            if (tt.nextToken() == Tokenizer.TT_KEYWORD) {
                var token = tt.getStringValue();

                // Backtracking algorithm: try out each possible symbol for the given token.
                for (var symbol : this.notation.getSymbols(token)) {
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

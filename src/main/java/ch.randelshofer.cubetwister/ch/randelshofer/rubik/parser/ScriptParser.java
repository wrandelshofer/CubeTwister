/* @(#)ScriptParser.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.notation.Move;
import ch.randelshofer.rubik.notation.Notation;
import ch.randelshofer.rubik.notation.Symbol;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.rubik.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Parser for rubik's cube scripts. The tokens and syntax-rules used by the
 * parser are read from a Notation object.<p>
 * <p>
 * The parser supports the EBNF ISO/IEC 14977 productions shown below.
 * Alternative syntax-rules specifiable by the Notation object are indicated in
 * square brackets. The syntax-rules are given here for readability. The
 * syntax-rules actually implemented by the parser are more complex, because
 * those must be LALR1 (left-aligned left recursive with a lookahead of 1) to be
 * parseable. The rules used by the implementation are given in the comments of
 * the methods.
 *
 * <pre>
 * Sequence         = { Expression } ;
 *
 * Expression       = Construct [Operator Expression] ;
 *
 * Operator         = Commutator | Conjugator | Rotator ;
 *
 * Construct        = { Prefix }, Statement, {Suffix} ;
 *
 * Statement        = CompoundStatement | Move | NOP | Macro ;
 *
 * CompoundStatement= Grouping |
 *                    Inversion | Reflection |
 *                    Commutation | Conjugation | Rotation |
 *                    Permutation;
 *
 * Affix            = Construct ;
 * Root             = Construct ;
 *
 * Grouping         = GroupingBegin, Sequence, GroupingEnd ;
 * GroupingBegin    = Word ;
 * GroupingEnd      = Word ;
 *
 * Inversion        = Inversion-prefix | Inversion-suffix |
 *                    Inversion-circumfix ;
 *
 * Inversion-prefix    = [InversionBegin], Invertor, [InversionEnd], Root ;
 * Inversion-suffix    = Root, [InversionBegin], Invertor, [InversionEnd] ;
 * Inversion-circumfix = InversionBegin, Root, InversionEnd ;
 * Invertor         = Word ;
 * InversionBegin   = Word ;
 * InversionEnd     = Word ;
 *
 * Reflection       = Reflection-prefix | Reflection-suffix |
 *                    Reflection-circumfix ;
 *
 * Reflection-prefix = [ReflectionBegin], Reflector, [ReflectionEnd], Root ;
 * Reflection-suffix = Root, [ReflectionBegin], Reflector, [ReflectionEnd] ;
 * Reflection-circumfix = ReflectionBegin, Sequence, ReflectionEnd;
 * Reflector        = Word ;
 * ReflectionBegin  = Word ;
 * ReflectionEnd    = Word ;
 *
 * Commutation      = Commutation-prefix | Commutation-suffix |
 *                    Commutation-precircumfix | Commutation-postcircumfix |
 *                    Commutation-preinfix | Commutation-postinfix ;
 *
 * Commutation-prefix        = CommutationBegin, Affix, CommutationEnd, Root ;
 * Commutation-suffix        = Root, CommutationBegin, Affix, CommutationEnd ;
 * Commutation-precircumfix  = CommutationBegin, Affix, CommutationDelim, Sequence, CommutationEnd ;
 * Commutation-postcircumfix = CommutationBegin, Sequence CommutationDelim, Affix, CommutationEnd ;
 * Commutation-preinfix      = Affix, CommutationDelim, Root ;
 * Commutation-postinfix     = Root, CommutationDelim, Affix ;
 * CommutationBegin = Word ;
 * CommutationEnd   = Word ;
 * CommutationDelim = Word ;
 *
 * Conjugation      = Conjugation-prefix | Conjugation-suffix |
 *                    Conjugation-precircumfix | Conjugation-postcircumfix |
 *                    Conjugation-preinfix | Conjugation-postinfix ;
 *
 * Conjugation-prefix        = ConjugationBegin, Affix, ConjugationEnd, Root ;
 * Conjugation-suffix        = Root, ConjugationBegin, Affix, ConjugationEnd ;
 * Conjugation-precircumfix  = ConjugationBegin, Affix, ConjugationDelim, Root, ConjugationEnd ;
 * Conjugation-postcircumfix = ConjugationBegin, Root, ConjugationDelim, Affix, ConjugationEnd ;
 * Conjugation-preinfix      = Affix, ConjugationDelim, Root ;
 * Conjugation-postinfix     = Root, ConjugationDelim, Affix ;
 * ConjugationBegin = Word ;
 * ConjugationEnd   = Word ;
 * ConjugationDelim = Word ;
 *
 * Rotation         = Rotation-prefix | Rotation-suffix |
 *                    Rotation-precircumfix | Rotation-postcircumfix |
 *                    Rotation-preinfix | Rotation-postinfix ;
 *
 * Rotation-prefix = RotationBegin, Affix, RotationEnd, Root ;
 * Rotation-suffix = Root, RotationBegin, Affix, RotationEnd ;
 * Rotation-precircumfix  = RotationBegin, Affix, RotationDelim, Sequence, RotationEnd ;
 * Rotation-postcircumfix = RotationBegin, Sequence, RotationDelim, Affix, RotationEnd ;
 * Rotation-preinfix      = Affix, RotationDelim, Root ;
 * Rotation-postinfix     = Root, RotationDelim, Affix ;
 * Rotator          = Script ;
 * RotationBegin    = Word ;
 * RotationEnd      = Word ;
 * RotationDelim    = Word ;
 *
 * Permutation      = Permutation-prefix | Permutation-suffix |
 *                    Permutation-precircumfix | Permutation-postcircumfix ;
 *
 * Permutation-prefix        = [PermSign], PermBegin, { SidePerm | EdgePerm | CornerPerm }, PermEnd ;
 * Permutation-suffix        = PermBegin, { SidePerm | EdgePerm | CornerPerm }, PermEnd, [PermSign] ;
 * Permutation-precircumfix  = PermBegin, [PermSign], { SidePerm | EdgePerm | CornerPerm }, PermEnd ;
 * Permutation-postcircumfix = PermBegin, { SidePerm | EdgePerm | CornerPerm }, [PermSign], PermEnd ;
 * SidePerm-prefix           = [PermSign], Face, [Integer];
 * SidePerm-suffix           = Face, [Integer], [PermSign] ;
 * SidePerm-precircumfix     = Face, [Integer], [PermSign] ;
 * SidePerm-postcircumfix    = [PermSign], Face, [Integer] ;
 * EdgePerm         = Face, Face, [Integer];
 * CornerPerm       = Face, Face, Face;
 * Face             = Word;
 *
 * Move             = Word ;
 *
 * NOP              = Word ;
 *
 * Macro            = Word ;
 *
 * Word             = { letter | digit } ;
 * Integer          = { digit } ;
 * </pre>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ScriptParser {

    private static boolean VERBOSE = false;
    private final static int UNKNOWN_MASK = 0;
    private final static int GROUPING_MASK = 1;
    private final static int CONJUGATION_MASK = 2;
    private final static int COMMUTATION_MASK = 4;
    private final static int ROTATION_MASK = 8;
    private final static int PERMUTATION_MASK = 16;
    private final static int INVERSION_MASK = 32;
    private final static int REFLECTION_MASK = 64;
    /**
     * key = String value = MacroNode
     */
    private HashMap<String, MacroNode> macros = new HashMap<String, MacroNode>();
    /**
     * The notation describing the tokens and the syntax of the scripts that can
     * be parsed.
     */
    private Notation notation;

    /**
     * Creates a new ScriptParser.
     *
     * @param notation The notation describing the tokens and the syntax of the
     * scripts that can be parsed.
     */
    public ScriptParser(Notation notation) {
        this(notation, null);
    }

    /**
     * Creates a ScriptParser for the specified notation and with the specified
     * local macros.
     *
     * @param notation The notation describing the tokens and the syntax of the
     * scripts that can be parsed.
     * @param localMacros Local Macros Entry.key = macro identifier, Entry.value
     * = macro script.
     */
    public ScriptParser(Notation notation, List<MacroNode> localMacros) {
        if (notation == null) {
            throw new IllegalArgumentException("Notation must not be null");
        }
        this.notation = notation;
        if (localMacros != null) {
            for (MacroNode macro : localMacros) {
                macros.put(macro.getIdentifier(), macro);
            }
        }
        // global macros override local macros
        for (MacroNode macro : notation.getMacros()) {
            macros.put(macro.getIdentifier(), macro);
        }

    }

    public Notation getNotation() {
        return notation;
    }
    private Tokenizer createTokenizer(String input) {
            Tokenizer tt = new Tokenizer();
            tt.skipWhitespace();
            tt.addNumbers();
            Collection<String> tokenToSymbolMap = this.notation.getTokens();
            for (String i : tokenToSymbolMap) {
                tt.addKeyword(i);
            }
            tt.setInput(input);
            return tt;
    }

    /**
     * Parses the specified string.
     * @param str string
     * @return Node the parsed abstract syntax tree node
     * @throws ParseException if the parsing fails.
     */
    public Node parse(String str) throws ParseException {
        Tokenizer tt = this.createTokenizer(str);
        ScriptNode root = new ScriptNode();
        int guard = str.length();
        while (tt.nextToken() != Tokenizer.TT_EOF) {
            tt.pushBack();
            this.parseExpression(tt, root);
            guard = guard - 1;
            if (guard < 0) {
                throw new ParseException("Too many iterations! " + tt.getTokenType() , tt.getStartPosition(),tt.getEndPosition());
            }
        }
        return root;
    }

    /**
     * Returns true if the array contains a symbol of the specified symbol type
     * @param symbols array of symbols
     * @param type compound symbol
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

    /**
     * Returns true if the array contains a symbol of the specified symbol type
     * @param symbols array of symbols
     * @param types type desired type
     * @return true if array contains a symbol of the specified type
     */
    boolean intersectsTypes(List<Symbol> symbols, Set<Symbol> types) {
        for (Symbol s : symbols) {
            if (types.contains(s)){
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the first intersecting symbol of symbols with types.
     * @param symbols array of symbols
     * @param types type desired type
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
     * Returns true if the array contains at least one symbol, and
     * only symbols of the specified symbol type.
     *
     * @param {type} array of symbols
     * @param {type} type desired type
     * @return true if array contains a symbol of the specified type
     */
    boolean isType(List<Symbol> symbols, Symbol type) {
        for (Symbol s : symbols) {
            if (s != type) {
                return false;
            }
        }
        return !symbols.isEmpty();
    }

    /**
     * Parses a Statement.
     *
     * @param {
    Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed statement
     * @throws ParseException parse exception
     */
    Node parseStatement(Tokenizer t, Node parent) throws ParseException {
        // Fetch the next token.
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            throw new ParseException("Statement: \"" + t.getStringValue() + "\" is a " + t.getTokenType() + " but not a keyword.", t.getStartPosition(), t.getEndPosition());
        }

        int startPos = t.getStartPosition();
        List<Symbol> candidates = notation.getSymbolsFor(t.getStringValue());
        // Evaluate: Macro
        if (candidates.isEmpty()) {
            throw new ParseException("Statement: Unknown statement " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
        }

        // Is it a Macro?
        if (this.containsType(candidates, Symbol.MACRO)) {
            t.pushBack();
            return this.parseMacro(t, parent);
        }

        // Is it a Move?
        if (this.containsType(candidates, Symbol.MOVE)) {
            t.pushBack();
            return this.parseMove(t, parent);
        }

        // Is it a NOP?
        if (this.containsType(candidates, Symbol.NOP)) {
            t.pushBack();
            return this.parseNOP(t, parent);
        }


        // Is it a Permutation token? Parse a permutation.
        if ((notation.getSyntax(Symbol.PERMUTATION)== Syntax.PREFIX
                ||notation.getSyntax(Symbol.PERMUTATION)== Syntax.PRECIRCUMFIX)
                &&  this.intersectsTypes(candidates,Symbol.PERMUTATION_SIGNS.getSubSymbols())) {
            int startpos = t.getStartPosition();
            List<Symbol> sign=candidates;
            t.nextToken();
            candidates = notation.getSymbolsFor(t.getStringValue());
            if (!this.containsType(candidates, Symbol.PERMUTATION_BEGIN)) {
                throw new ParseException(
                        "Permutation: Unexpected token - expected permutation begin.", t.getStartPosition(), t.getEndPosition());
            }

            PermutationNode pnode = this.parsePermutation(t, parent, startpos, sign.get(0));
            return pnode;
        }
        // Okay, it's not a move and not a permutation sign.
        // Since we allow for some ambiguity of the
        // tokens used by the grouping, conjugation, commutation and permutation
        // statement it gets a little bit complicated here.
        // Create a bit mask with a bit for each expected statement.
       int expressionMask =
               ((this.containsType(candidates, Symbol.GROUPING_BEGIN)) ? GROUPING_MASK : UNKNOWN_MASK) | //
                ((notation.getSyntax(Symbol.CONJUGATION) == Syntax.PRECIRCUMFIX && this.containsType(candidates, Symbol.CONJUGATION_BEGIN)) ? CONJUGATION_MASK : UNKNOWN_MASK) | //
                ((notation.getSyntax(Symbol.COMMUTATION) == Syntax.PRECIRCUMFIX && this.containsType(candidates, Symbol.COMMUTATION_BEGIN)) ? COMMUTATION_MASK : UNKNOWN_MASK) | //
                ((notation.getSyntax(Symbol.ROTATION) == Syntax.PRECIRCUMFIX && this.containsType(candidates, Symbol.ROTATION_BEGIN)) ? ROTATION_MASK : UNKNOWN_MASK) | //
                ((notation.getSyntax(Symbol.INVERSION) == Syntax.CIRCUMFIX && this.containsType(candidates, Symbol.INVERSION_BEGIN)) ? INVERSION_MASK : UNKNOWN_MASK) | //
                ((notation.getSyntax(Symbol.REFLECTION) == Syntax.CIRCUMFIX && this.containsType(candidates, Symbol.REFLECTION_BEGIN)) ? REFLECTION_MASK : UNKNOWN_MASK) | //
                ((notation.isSupported(Symbol.PERMUTATION) && this.containsType(candidates, Symbol.PERMUTATION_BEGIN)) ? PERMUTATION_MASK : UNKNOWN_MASK);
        // Is it a Permutation Begin token without any ambiguity?
        if (expressionMask == PERMUTATION_MASK) {
            return this.parsePermutation(t, parent, startPos, null);
        }

        // Is it an ambiguous permutation begin token?
        if ((expressionMask & PERMUTATION_MASK) == PERMUTATION_MASK) {
             startPos = t.getStartPosition();
            // Look ahead
            if (t.nextToken() != Tokenizer.TT_KEYWORD) {
                throw new ParseException("Statement: keyword expected.", t.getStartPosition(), t.getEndPosition());
            }
            candidates = notation.getSymbolsFor( t.getStringValue());
            t.pushBack();
            if (candidates != null && this.intersectsTypes(candidates, Symbol.PERMUTATION.getSubSymbols())) {
                return this.parsePermutation(t, parent, startPos, null);
            } else {
                return this.parseCompoundStatement(t, parent, startPos, expressionMask ^ PERMUTATION_MASK);
            }
        }

        // Is it one of the other Begin tokens?
        if (expressionMask != UNKNOWN_MASK) {
            return this.parseCompoundStatement(t, parent, startPos, expressionMask);
        }

        throw new ParseException("Statement: illegal Statement " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
    }

    /** Parses the remainder of a permutation statement after its PERMUTATION_BEGIN token has been consumed.
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @param {int} startPos the start position of the PERMUTATION_BEGIN begin token
     * @returns {unresolved} the parsed permutation
     */
    PermutationNode parsePermutation(Tokenizer t, Node parent, int startPos, Symbol sign) throws ParseException {

        PermutationNode permutation = new PermutationNode(startPos, startPos);
        parent.add(permutation);

        if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.PRECIRCUMFIX) {
            sign = this.parsePermutationSign(t, parent);
        }

        ThePermutation:
        while (true) {
            switch (t.nextToken()) {
                case Tokenizer.TT_KEYWORD:

                    // Evaluate PermEnd
                    List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
                    if (this.containsType(symbols, Symbol.PERMUTATION_END)) {
                        permutation.setEndPosition(t.getEndPosition());
                        break ThePermutation;

                    } else {
                        t.pushBack();
                        this.parsePermutationItem(t, permutation);
                        if (t.nextToken() == Tokenizer.TT_KEYWORD) {
                            symbols = notation.getSymbolsFor(t.getStringValue());
                            if (this.containsType(symbols, Symbol.PERMUTATION_DELIMITER)) {

                            } else if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.POSTCIRCUMFIX
                                    && (this.containsType(symbols, Symbol.PERMUTATION_PLUS)
                                    || this.containsType(symbols, Symbol.PERMUTATION_MINUS)
                                    || this.containsType(symbols, Symbol.PERMUTATION_PLUSPLUS))) {
                                t.pushBack();
                                sign = this.parsePermutationSign(t, parent);
                                if (t.nextToken() != Tokenizer.TT_WORD) {
                                    throw new ParseException(
                                            "Permutation: End expected.", t.getStartPosition(), t.getEndPosition());
                                }
                                // FIXME check if current token is PERMUATION END
                                if (this.containsType(symbols,Symbol.PERMUTATION_END)) {
                                    permutation.setEndPosition(t.getEndPosition());
                                    break ThePermutation;
                                } else {
                                    throw new ParseException(
                                            "Permutation: End expected.", t.getStartPosition(), t.getEndPosition());
                                }
                            } else {
                                t.pushBack();
                            }
                        } else {
                            t.pushBack();
                        }
                    }
                    break;
                case Tokenizer.TT_EOF:
                    throw new ParseException(
                            "Permutation: End missing.", t.getStartPosition(), t.getEndPosition());
                default:
                    throw new ParseException(
                            "Permutation: Internal error. "+t.getTokenType(), t.getStartPosition(), t.getEndPosition());
            }
        }

        if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.SUFFIX) {
            sign = this.parsePermutationSign(t, parent);
        }

        if (sign != null) {
            switch (permutation.getType()) {
                case 1:
                    break;
                case 2:
                    if (sign == Symbol.PERMUTATION_PLUSPLUS
                            || sign == Symbol.PERMUTATION_MINUS) {
                        throw new ParseException(
                                "Permutation: Illegal sign.", t.getStartPosition(), t.getEndPosition());
                    }
                    break;
                case 3:
                    if (sign == Symbol.PERMUTATION_PLUSPLUS) {
                        throw new ParseException(
                                "Permutation: Illegal sign.", t.getStartPosition(), t.getEndPosition());
                    }
                    break;
            }
            permutation.setPermutationSign(sign);
            permutation.setEndPosition(t.getEndPosition());
        }

        return permutation;
    }

    /**
     * Parses a permutation sign and returns null or one of the three sign
     * symbols.
     */
    Symbol parsePermutationSign(Tokenizer t,  Node parent)  {

        Symbol sign = null;
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            t.pushBack();
            sign = null;
        } else {
            List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
            if (this.containsType(symbols, Symbol.PERMUTATION_PLUS)
                    || this.containsType(symbols, Symbol.PERMUTATION_PLUSPLUS)
                    || this.containsType(symbols, Symbol.PERMUTATION_MINUS)) {
                sign = symbols.get(0);
            } else {
                sign = null;
                t.pushBack();
            }
        }
        return sign;
    }

    /**
     * Parses a permutation item.
     *
     * @param {Tokenizer} t
     * @param {PermutationNode} parent
     */
   Node parsePermutationItem(Tokenizer t, PermutationNode parent) throws ParseException {

    int startpos = t.getStartPosition();
        Symbol sign = null;
        int leadingSignStartPos = -1, leadingSignEndPos = -1;
        String partName = "";

        // Evaluate [sign]
        Syntax syntax = notation.getSyntax(Symbol.PERMUTATION);
        if (syntax == Syntax.PRECIRCUMFIX
                || syntax == Syntax.PREFIX
                || syntax == Syntax.POSTCIRCUMFIX) {
            leadingSignStartPos = t.getStartPosition();
            leadingSignEndPos = t.getEndPosition();
            sign = this.parsePermutationSign(t, parent);
        }
        // Evaluate PermFace [PermFace] [PermFace]
        List<Symbol> faceSymbols = new ArrayList<>(3);
        int type = 0;

        while (type < 3) {
            if (t.nextToken() != Tokenizer.TT_KEYWORD) {
                throw new ParseException("PermutationItem: Face token missing.", t.getStartPosition(), t.getEndPosition());
            }
            List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
            if (!this.containsType(symbols, Symbol.PERMUTATION)) {
                t.pushBack();
                break;
            }
            Symbol symbol = this.getFirstIntersectingType(symbols, Symbol.PERMUTATION_FACES.getSubSymbols());
            if (symbol != null) {
                partName = partName + t.getStringValue();
                faceSymbols.add(symbol);
                type++;
            } else {
                t.pushBack();
                break;
            }
        }

        if (notation.getLayerCount() < 3 && type < 3) {
            throw new ParseException("PermutationItem: The 2x2 cube does not have a \"" + partName.toString() + "\" part.", startpos, t.getEndPosition());
        }

        if (type != 1 && sign != null && (syntax == Syntax.SUFFIX)) {
            throw new ParseException("PermutationItem: Unexpected sign", leadingSignStartPos, leadingSignEndPos);
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
        if (syntax == Syntax.SUFFIX && type == PermutationNode.SIDE_PERMUTATION) {
            sign = parsePermutationSign(t, parent);
        }

            parent.addPermItem(type, sign, faceSymbols.toArray(new Symbol[faceSymbols.size()]), partNumber, notation.getLayerCount());
        return parent;
    }

    /** Parses a compound statement after its BEGIN token has been consumed.
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @param {int} startPos the start position of the XXX_BEGIN begin token
     * @param {int} beginTypeMask the mask indicating which XXX_BEGIN token was consumed
     * @returns {unresolved} the parsed compound statement
     */
   Node parseCompoundStatement(Tokenizer t, Node parent, int startPos, int beginTypeMask) throws ParseException{
        ScriptNode seq1 = new ScriptNode();
        seq1.setStartPosition(startPos);
        parent.add(seq1);
        ScriptNode seq2 = null;
        Node grouping = seq1;
        // The final type mask reflects the final type that we have determined
        // after parsing all of the grouping.
        int finalTypeMask = beginTypeMask & (GROUPING_MASK | CONJUGATION_MASK | COMMUTATION_MASK | ROTATION_MASK | REFLECTION_MASK | INVERSION_MASK);
        // Evaluate: {Statement} , (GROUPING_END | COMMUTATION_END | CONJUGATION_END | ROTATION_END) ;
        int guard = t.getInputLength();
        TheGrouping:
        while (true) {
            guard = guard - 1;
            if (guard < 0) {
                throw new Error("too many iterations");
            }

            switch (t.nextToken()) {
                case Tokenizer.TT_KEYWORD:
                    List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
                    // Look ahead the nextElement token.

                    int endTypeMask
                            = ((this.containsType(symbols, Symbol.GROUPING_END)) ? GROUPING_MASK : UNKNOWN_MASK) | //
                            ((notation.getSyntax(Symbol.CONJUGATION) == Syntax.PRECIRCUMFIX && this.containsType(symbols, Symbol.CONJUGATION_END)) ? CONJUGATION_MASK : UNKNOWN_MASK) | //
                            ((notation.getSyntax(Symbol.COMMUTATION) == Syntax.PRECIRCUMFIX && this.containsType(symbols, Symbol.COMMUTATION_END)) ? COMMUTATION_MASK : UNKNOWN_MASK) | //
                            ((notation.getSyntax(Symbol.INVERSION) == Syntax.CIRCUMFIX && this.containsType(symbols, Symbol.INVERSION_END)) ? INVERSION_MASK : UNKNOWN_MASK) | //
                            ((notation.getSyntax(Symbol.REFLECTION) == Syntax.CIRCUMFIX && this.containsType(symbols, Symbol.REFLECTION_END)) ? REFLECTION_MASK : UNKNOWN_MASK) | //
                            ((notation.getSyntax(Symbol.ROTATION) == Syntax.PRECIRCUMFIX && this.containsType(symbols, Symbol.ROTATION_END)) ? ROTATION_MASK : UNKNOWN_MASK);
                    int delimiterTypeMask
                            = ((notation.getSyntax(Symbol.CONJUGATION) == Syntax.PRECIRCUMFIX && this.containsType(symbols, Symbol.CONJUGATION_DELIMITER)) ? CONJUGATION_MASK : 0)
                            | ((notation.getSyntax(Symbol.COMMUTATION) == Syntax.PRECIRCUMFIX && this.containsType(symbols, Symbol.COMMUTATION_DELIMITER)) ? COMMUTATION_MASK : 0)
                            | ((notation.getSyntax(Symbol.ROTATION) == Syntax.PRECIRCUMFIX && this.containsType(symbols, Symbol.ROTATION_OPERATOR)) ? ROTATION_MASK : 0);

                    if (endTypeMask != 0) {
                        finalTypeMask &= endTypeMask;
                        grouping.setEndPosition(t.getEndPosition());
                        break TheGrouping;
                    } else if (delimiterTypeMask != 0) {
                        finalTypeMask &= delimiterTypeMask;
                        if (finalTypeMask == 0) {
                            throw new ParseException("Grouping: illegal delimiter:" + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
                        }
                        if (seq2 == null) {
                            seq1.setEndPosition(t.getStartPosition());
                            seq2 = new ScriptNode();
                            seq2.setStartPosition(t.getEndPosition());
                            parent.add(seq2);
                            grouping = seq2;
                        } else {
                            throw new ParseException("Grouping: Delimiter must occur only once", t.getStartPosition(), t.getEndPosition());
                        }

                    } else {
                        t.pushBack();
                        this.parseExpression(t, grouping);
                    }
                    break;
                case Tokenizer.TT_EOF:
                    throw new ParseException("Grouping: End missing.", t.getStartPosition(), t.getEndPosition());
                default:
                    throw new ParseException("Grouping: Internal error.", t.getStartPosition(), t.getEndPosition());
            }
        }

        seq1.removeFromParent();
        if (seq2 == null) {
            // There is no second sequence.
            // The compound statement can only be a grouping.
            finalTypeMask &= GROUPING_MASK;
        } else {
            // There is a second sequence. Remove it from its parent, because we
            // will integrate it into the compound statement.
            seq2.removeFromParent();
            // The compound statement can not be a grouping.
            finalTypeMask &= ~GROUPING_MASK;
        }

        switch (finalTypeMask) {
            case GROUPING_MASK:
                    grouping = new GroupingNode(startPos, t.getEndPosition());
                    for (int i = seq1.getChildCount() - 1; i >= 0; i--) {
                        grouping.add(seq1.getChildAt(0));
                    }
                break;
            case INVERSION_MASK:
                    grouping = new InversionNode(startPos, t.getEndPosition());
                    for (int i = seq1.getChildCount() - 1; i >= 0; i--) {
                        grouping.add(seq1.getChildAt(0));
                    }
                break;
            case REFLECTION_MASK:
                    grouping = new ReflectionNode(startPos, t.getEndPosition());
                    for (int i = seq1.getChildCount() - 1; i >= 0; i--) {
                        grouping.add(seq1.getChildAt(0));
                    }
                break;
            case CONJUGATION_MASK:
                    grouping = new ConjugationNode(seq1, seq2, startPos, t.getEndPosition());
                break;
            case COMMUTATION_MASK:
                    grouping = new CommutationNode(seq1, seq2, startPos, t.getEndPosition());
                break;
            case ROTATION_MASK:
                    grouping = new RotationNode(seq1, seq2, startPos, t.getEndPosition());
                break;
            default:
                String ambiguous = "";
                if ((finalTypeMask & GROUPING_MASK) != 0) {
                    ambiguous += ("Grouping");
                }
                if ((finalTypeMask & INVERSION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous += (" or ");
                    }
                    ambiguous += ("Inversion");
                }
                if ((finalTypeMask & REFLECTION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous += (" or ");
                    }
                    ambiguous += ("Reflection");
                }
                if ((finalTypeMask & CONJUGATION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous += (" or ");
                    }
                    ambiguous+=("Conjugation");
                }
                if ((finalTypeMask & COMMUTATION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous += (" or ");
                    }
                    ambiguous += ("Commutation");
                }
                if ((finalTypeMask & ROTATION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous += (" or ");
                    }
                    ambiguous += ("Rotation");
                }
                throw new ParseException("Compound Statement: Ambiguous compound statement, possibilities are " + ambiguous + ".", startPos, t.getEndPosition());
        }

        parent.add(grouping);
        return grouping;
    }
    /** Parses an expression.
     */
   private Node parseExpression(Tokenizer t, Node parent)throws ParseException {
        Node expression = this.parseConstruct(t, parent);
        int ttype = t.nextToken();
        if (ttype == Tokenizer.TT_KEYWORD) {
            List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
            if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.PREINFIX
                    && this.containsType(symbols, Symbol.COMMUTATION_DELIMITER)) {
                Node exp2 = this.parseExpression(t, parent);
                expression = new CommutationNode(expression, exp2, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.PREINFIX
                    && this.containsType(symbols, Symbol.CONJUGATION_DELIMITER)) {
                Node exp2 = this.parseExpression(t, parent);
                expression = new ConjugationNode(expression, exp2, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.ROTATION) == Syntax.PREINFIX
                    && this.containsType(symbols, Symbol.ROTATION_OPERATOR)) {
                Node exp2 = parseExpression(t, parent);
                expression = new RotationNode(expression, exp2, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.POSTINFIX
                    && this.containsType(symbols, Symbol.COMMUTATION_DELIMITER)) {
                Node exp2 = parseExpression(t, parent);
                expression = new CommutationNode(exp2, expression, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.POSTINFIX
                    && this.containsType(symbols, Symbol.CONJUGATION_DELIMITER)) {
                Node exp2 = parseExpression(t, parent);
                expression = new ConjugationNode(exp2, expression, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.ROTATION) == Syntax.POSTINFIX
                    && this.containsType(symbols, Symbol.ROTATION_OPERATOR)) {
                Node exp2 = parseExpression(t, parent);
                expression = new RotationNode(exp2, expression, expression.getStartPosition(), exp2.getEndPosition());
            } else {
                t.pushBack();
            }
        } else {
            t.pushBack();
        }

        parent.add(expression);
        return expression;
    }
    /** Parses a construct
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed macro
     */
   private Node parseConstruct(Tokenizer t, Node parent) throws ParseException {
        Node statement = null;
        int ttype = t.nextToken();
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        if (ttype == Tokenizer.TT_KEYWORD
                && this.containsType(symbols, Symbol.DELIMITER)) {
            // Evaluate: StmtDelimiter
            // -----------------------

            // We discard StmtDelimiter's
            statement = null;
        } else {
            statement = new StatementNode(notation.getLayerCount());
            parent.add(statement);
            statement.setStartPosition(t.getStartPosition());
            t.pushBack();
            // Evaluate: {Prefix}
            Node prefix = statement;
            Node lastPrefix = statement;
            int guard = t.getInputLength();
            while ((prefix = this.parsePrefix(t, prefix)) != null) {

                guard = guard - 1;
                if (guard < 0) {
                    throw new Error("too many iterations");
                }
                lastPrefix = prefix;
            }

            // Evaluate: Statement
            Node innerStatement = this.parseStatement(t, lastPrefix);
            statement.setEndPosition(innerStatement.getEndPosition());
            // Evaluate: Suffix
            Node child = statement.getChildAt(0);
            Node suffix = statement;
            guard = t.getInputLength();
            while ((suffix = this.parseSuffix(t, statement)) != null) {
                guard = guard - 1;
                if (guard < 0) {
                    throw new Error("too many iterations");
                }
                suffix.add(child);
                child = suffix;
                statement.setEndPosition(suffix.getEndPosition());
            }
        }
        return statement;
    }
    /** Parses a prefix.
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed macro
     */
    private Node parsePrefix(Tokenizer t, Node parent) throws ParseException{
        int ttype = t.nextToken();
        if (ttype == Tokenizer.TT_EOF) {
            return null;
        }
        Integer numericToken = null;
        if (ttype == Tokenizer.TT_NUMBER) {
            t.pushBack();
            // If the token is numeric, we have encountered
            // a repetition prefix.
            if (notation.getSyntax(Symbol.REPETITION) == Syntax.PREFIX) {
                return this.parseRepetitor(t, parent);
            } else {
                return null;
            }
        }
        // the prefix must be a keyword, or it is not a prefix at all
        if (ttype != Tokenizer.TT_KEYWORD) {
            t.pushBack();
            return null;
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        // We push back, because we do just decisions in this production
        t.pushBack();
        // Is it a commutator?
        if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.PREFIX
                && this.containsType(symbols, Symbol.COMMUTATION_BEGIN)) {
            return this.parseExpressionAffix(t, parent);
        }

        // Is it a conjugator?
        if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.PREFIX
                && this.containsType(symbols, Symbol.CONJUGATION_BEGIN)) {
            return this.parseExpressionAffix(t, parent);
        }

        // Is it a rotator?
        if (notation.getSyntax(Symbol.ROTATION) == Syntax.PREFIX
                && this.containsType(symbols, Symbol.ROTATION_BEGIN)) {
            return this.parseExpressionAffix(t, parent);
        }

        // Is it an Inversion?
        if (notation.getSyntax(Symbol.INVERSION) == Syntax.PREFIX
                && this.containsType(symbols, Symbol.INVERSION_OPERATOR)) {
            return this.parseInvertor(t, parent);
        }

        // Is it a repetition?
        if (notation.getSyntax(Symbol.REPETITION) == Syntax.PREFIX
                && this.containsType(symbols, Symbol.REPETITION_BEGIN)) {
            return this.parseRepetitor(t, parent);
        }

        // Is it a reflection?
        if (notation.getSyntax(Symbol.REFLECTION) == Syntax.PREFIX
                && this.containsType(symbols,  Symbol.REFLECTION_OPERATOR)) {
            return this.parseReflector(t, parent);
        }

        // Or is it no prefix at all?
        return null;
    }
    /** Parses a suffix.
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed macro
     */
    private Node parseSuffix(Tokenizer t, Node parent)throws ParseException {
        int ttype = t.nextToken();
        if (ttype == Tokenizer.TT_EOF) {
            return null;
        }
        Integer numericToken = null;
        if (ttype == Tokenizer.TT_NUMBER) {
            t.pushBack();
            // If the token is numeric, we have encountered
            // a repetition prefix.
            if (notation.getSyntax(Symbol.REPETITION) == Syntax.SUFFIX) {
                return this.parseRepetitor(t, parent);
            } else {
                return null;
            }
        }
        // the prefix must be a keyword, or it is not a prefix at all
        if (ttype != Tokenizer.TT_KEYWORD) {
            t.pushBack();
            return null;
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        // We push back, because we do just decisions in this production
        t.pushBack();
        // Is it a commutator?
        if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.SUFFIX
                && this.containsType(symbols, Symbol.COMMUTATION_BEGIN)) {
            return this.parseExpressionAffix(t, parent);
        }

        // Is it a conjugator?
        if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.SUFFIX
                && this.containsType(symbols, Symbol.CONJUGATION_BEGIN)) {
            return this.parseExpressionAffix(t, parent);
        }

        // Is it a rotator?
        if (notation.getSyntax(Symbol.ROTATION) == Syntax.SUFFIX
                && this.containsType(symbols, Symbol.ROTATION_BEGIN)) {
            return this.parseExpressionAffix(t, parent);
        }

        // Is it an Inversion?
        if (notation.getSyntax(Symbol.INVERSION) == Syntax.SUFFIX
                && this.containsType(symbols, Symbol.INVERSION_OPERATOR)) {
            return this.parseInvertor(t, parent);
        }

        // Is it a repetition?
        if (notation.getSyntax(Symbol.REPETITION) == Syntax.SUFFIX
                && this.containsType(symbols, Symbol.REPETITION_BEGIN)) {
            return this.parseRepetitor(t, parent);
        }

        // Is it a reflection?
        if (notation.getSyntax(Symbol.REFLECTION) == Syntax.SUFFIX
                && this.containsType(symbols, Symbol.REFLECTION_OPERATOR)) {
            return this.parseReflector(t, parent);
        }

        // Or is it no suffix at all?
        return null;
    }
    /** Parses a macro.
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed macro
     */
    private Node parseMacro(Tokenizer t, Node parent) throws ParseException{
        throw new ParseException("Macro: Not implemented " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
    }
    /** Parses a repetitor
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed repetitor
     */
    private Node parseRepetitor(Tokenizer t, Node parent) throws ParseException {
        // Only parse if supported
        if (!notation.isSupported(Symbol.REPETITION)) {
            return null;
        }

      RepetitionNode repetition = new RepetitionNode();
        parent.add(repetition);
        repetition.setStartPosition(t.getStartPosition());
        // Evaluate [RptrBegin] token.
        // ---------------------------
        // Only word tokens are legit.
        // Fetch the next token.
        if (t.nextToken() != Tokenizer.TT_KEYWORD
                && t.getTokenType() != Tokenizer.TT_NUMBER) {
            throw new ParseException("Repetitor: illegal begin.", t.getStartPosition(), t.getEndPosition());
        }

        // Is it a [RptrBegin] token? Consume it.
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        if (symbols != null && this.isType(symbols, Symbol.REPETITION_BEGIN)) {
            //consume
        } else {
            t.pushBack();
        }
        // The [RptrBegin] token is now done.

        // Evaluate Integer token.
        // ---------------------------
        // Only number tokens are legit.
        if (t.nextToken() != Tokenizer.TT_NUMBER) {
            throw new ParseException("Repetitor: Repeat count missing.", t.getStartPosition(), t.getEndPosition());
        }
        int intValue = t.getNumericValue();
        if (intValue < 1) {
            throw new ParseException("Repetitor: illegal repeat count " + intValue, t.getStartPosition(), t.getEndPosition());
        }
        repetition.setRepeatCount(intValue);
        repetition.setEndPosition(t.getEndPosition());
        
        // The Integer token is now done.
        
        // Evaluate [RptrEnd] token.
        // ---------------------------
        // Only keyword tokens are of interest.
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
           t.pushBack();
            return repetition;
        }

        // Is it a [RptrEnd] token? Consume it.
        symbols = notation.getSymbolsFor(t.getStringValue());
        if (this.isType(symbols, Symbol.REPETITION_END)) {
            //consume
        } else {
            t.pushBack();
        }
        return repetition;
    }

    /** Parses an invertor
     *
     * @param {    Tokenizer} t
     * @param {Node} parent
     * @returns the parsed node
     */
    private Node parseInvertor(Tokenizer t, Node parent) throws ParseException {
    InversionNode inversion = new InversionNode();
        parent.add(inversion);
        inversion.setStartPosition(t.getStartPosition());
        // Fetch the next token.
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            throw new ParseException("Invertor: illegal begin.", t.getStartPosition(), t.getEndPosition());
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        if (this.containsType(symbols, Symbol.INVERSION_OPERATOR)) {
            inversion.setEndPosition(t.getEndPosition());
            return inversion;
        }

        // Or else?
        throw new ParseException("Invertor: illegal invertor " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
    }

    /** Parses a reflector
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed node
     */
    private Node parseReflector(Tokenizer t, Node parent) throws ParseException {
      ReflectionNode reflection = new ReflectionNode();
        parent.add(reflection);
        reflection.setStartPosition(t.getStartPosition());
        // Fetch the next token.
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            throw new ParseException("Reflector: illegal begin.", t.getStartPosition(), t.getEndPosition());
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        if (this.containsType(symbols, Symbol.REFLECTION_OPERATOR)) {
            reflection.setEndPosition(t.getEndPosition());
            return reflection;
        }

        // Or else?
        throw new ParseException("Reflector: illegal reflection " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
    }

    /**
     * Parses an affix which consists of an expression surrounded by a begin
     * token and an end token. Either the begin or the end token is mandatory.
     */
    private Node parseExpressionAffix(Tokenizer t, Node parent) throws ParseException {
    int startPosition = t.getStartPosition();

        // Fetch the next token.
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            throw new ParseException("Affix: Invalid begin.", t.getStartPosition(), t.getEndPosition());
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());

        // Parse the BEGIN token and collect all potential end nodes
    List<Symbol> endSymbols = new ArrayList<>();
        if (this.containsType(symbols, Symbol.CONJUGATION_BEGIN)
                && (notation.getSyntax(Symbol.CONJUGATION) == Syntax.PREFIX
                || notation.getSyntax(Symbol.CONJUGATION) == Syntax.SUFFIX)) {
            endSymbols.add(Symbol.CONJUGATION_END);
        }
        if (this.containsType(symbols, Symbol.COMMUTATION_BEGIN)
                && (notation.getSyntax(Symbol.COMMUTATION) == Syntax.PREFIX
                || notation.getSyntax(Symbol.COMMUTATION) == Syntax.SUFFIX)) {
            endSymbols.add(Symbol.COMMUTATION_END);
        }
        if (this.containsType(symbols, Symbol.ROTATION_BEGIN)
                && (notation.getSyntax(Symbol.ROTATION) == Syntax.PREFIX
                || notation.getSyntax(Symbol.ROTATION) == Syntax.SUFFIX)) {
            endSymbols.add(Symbol.ROTATION_END);
        }
        if (endSymbols.size() == 0) {
            // Or else?
            throw new ParseException("Affix: Invalid begin " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
        }

        // Is it a CngrBegin Statement {Statement} CngrEnd thingy?
      ScriptNode operator = new ScriptNode();
        Symbol endSymbol = null;
        Loop:
        do {
            this.parseExpression(t, operator);
            if (t.nextToken() != Tokenizer.TT_KEYWORD) {
                throw new ParseException("Affix: Statement missing.", t.getStartPosition(), t.getEndPosition());
            }
            symbols = notation.getSymbolsFor(t.getStringValue());
            for (Symbol s : endSymbols) {
                endSymbol = s;
                if (this.containsType(symbols, endSymbol)) {
                    break Loop;
                }
            }
            t.pushBack();
        } while (symbols != null);
        //t.nextToken();

        Node affix = null;
        if (endSymbol == Symbol.CONJUGATION_END) {
            ConjugationNode cNode = new ConjugationNode();
            cNode.setConjugator(operator);
            affix = cNode;
        } else if (endSymbol == Symbol.COMMUTATION_END) {
            CommutationNode cNode = new CommutationNode();
            cNode.setCommutator(operator);
            affix = cNode;
        } else if (endSymbol == Symbol.ROTATION_END) {
            RotationNode cNode = new RotationNode();
            cNode.setRotator(operator);
            affix = cNode;
        } else {
            throw new ParseException("Affix: Invalid end symbol " + t.getStringValue(), t.getStartPosition(), t.getEndPosition());
        }
        affix.setStartPosition(startPosition);
        affix.setEndPosition(t.getEndPosition());
        parent.add(affix);
        return affix;
    }

    /** Parses a NOP.
     *
     * @param {Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed NOP
     */
    private Node parseNOP(Tokenizer t, Node parent) throws ParseException {
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            throw new ParseException("NOP: \"" + t.getStringValue() + "\" is a " + t.getTokenType() + " but not a keyword.", t.getStartPosition(), t.getEndPosition());
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        if (!this.containsType(symbols, Symbol.NOP)) {
            throw new ParseException("Move: \"" + t.getStringValue() + "\" is not a NOP", t.getStartPosition(), t.getEndPosition());
        }

        Node nop = new NOPNode(t.getStartPosition(), t.getEndPosition());
        parent.add(nop);
        return nop;
    }

    /**
     * Parses a move.
     *
     * @param { Tokenizer} t
     * @param {Node} parent
     * @returns {unresolved} the parsed move
     */
    private Node parseMove(Tokenizer t, Node parent) throws ParseException {
        if (t.nextToken() != Tokenizer.TT_KEYWORD) {
            throw new ParseException("Move: \"" + t.getStringValue() + "\" is a " + t.getTokenType() + " but not a keyword.", t.getStartPosition(), t.getEndPosition());
        }
        List<Symbol> symbols = notation.getSymbolsFor(t.getStringValue());
        Symbol symbol = null;
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i) == Symbol.MOVE) {
                symbol = symbols.get(i);
                break;
            }
        }
        if (symbol == null) {
            throw new ParseException("Move: \"" + t.getStringValue() + "\" is not a Move", t.getStartPosition(), t.getEndPosition());
        }

        Move mv = notation.getMoveFromToken(t.getStringValue());
        Node move = new MoveNode(mv.getLayerCount(),mv.getAxis(),mv.getLayerMask(),mv.getAngle(),t.getStartPosition(),t.getEndPosition());
        parent.add(move);
        return move;
    }

}

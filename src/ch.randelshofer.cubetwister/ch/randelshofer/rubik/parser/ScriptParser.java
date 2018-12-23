/* @(#)ScriptParser.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.io.ParseException;
import ch.randelshofer.io.StreamPosTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class ScriptParser extends Object {

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

    /**
     * Extracts the token at the beginning of the String.
     *
     * This method tries to assign as many characters as possible to the token.
     *
     * @param string A string which contains one ore more concatenated tokens.
     * @return Returns the token at the beginning of the string, or null, if the
     * string does not start with a known token.
     */
    private String fetchGreedy(String string) {
        if (notation.isToken(string) || macros.containsKey(string)) {
            return string;
        } else if (string.length() > 1) {
            return fetchGreedy(string.substring(0, string.length() - 1));
        }
        return null;
    }

    /**
     * Extracts the first numeric token out of a grouping of concatenated
     * tokens. This method is greedy. It tries to assign as many characters as
     * possible to the numeric token.
     *
     * @param string A string which contains one token or several concatenated
     * tokens.
     * @return Returns the first token or null, if the string does not start
     * with a known token.
     */
    private String fetchGreedyNumber(String string) {
        try {
            Integer.parseInt(string);
            return string;
        } catch (NumberFormatException e) {
            if (string.length() > 1) {
                return fetchGreedyNumber(string.substring(0, string.length() - 1));
            } else {
                return null;
            }
        }
    }

    /**
     * Parses a Script.
     * <pre>
     * Script = {Statement} ;
     * </pre>.
     */
    public SequenceNode parse(String s)
            throws IOException {
        return parse(new StringReader(s), null);
    }

    /**
     * Parses a script.
     * <pre>
     * Script = {Statement} ;
     * </pre>.
     */
    public SequenceNode parse(Reader r)
            throws IOException {
        return parse(r, null);
    }

    /**
     * Parses a script.
     * <pre>
     * Script = {Statement} ;
     * </pre>.
     */
    public SequenceNode parse(Reader r, Node parent)
            throws IOException {
        if (VERBOSE) {
            System.out.println("BEGIN PARSE");
        }

        // Configure the tokenizer
        // -----------------------
        StreamPosTokenizer t = new StreamPosTokenizer(r);
        t.resetSyntax();
        t.wordChars('\u0021', '\uffff');
        t.whitespaceChars('\u0000', '\u0020');
        t.eolIsSignificant(false);

        if (notation.isSupported(Symbol.COMMENT)
                && notation.getToken(Symbol.MULTILINE_COMMENT_BEGIN) != null
                && notation.getToken(Symbol.MULTILINE_COMMENT_END) != null) {
            t.slashStarComments(true);
            t.setSlashStarTokens(
                    notation.getToken(Symbol.MULTILINE_COMMENT_BEGIN),
                    notation.getToken(Symbol.MULTILINE_COMMENT_END));
        }
        if (notation.isSupported(Symbol.COMMENT)
                && notation.getToken(Symbol.SINGLELINE_COMMENT_BEGIN) != null) {
            t.slashSlashComments(true);
            t.setSlashSlashToken(notation.getToken(Symbol.SINGLELINE_COMMENT_BEGIN));
        }

        SequenceNode script = new SequenceNode(notation.getLayerCount());
        if (parent != null) {
            parent.add(script);
        }
        script.setStartPosition(0);

        // Evaluate: {Expression}
        // ----------------------
        while (t.nextToken() != StreamPosTokenizer.TT_EOF) {
            t.pushBack();
            parseExpression(t, script);
        }

        script.setEndPosition(t.getEndPosition());
        if (VERBOSE) {
            System.out.println("END PARSE");
            System.out.println("script:" + script);
            System.out.println("resolved:" + script.toResolvedList());
        }
        return script;
    }

    private void printVerbose(StreamPosTokenizer t, String msg, Node parent)
            throws IOException {
        int i = parent.getLevel();
        StringBuilder buf = new StringBuilder();
        while (i-- > 0) {
            buf.append('.');
        }
        buf.append(msg);
        buf.append(' ');
        buf.append(t.sval);
        System.out.println(buf.toString());
    }

    /**
     * Parses an Expression.
     */
    private Node parseExpression(StreamPosTokenizer t, Node parent) throws IOException {
        Node expression = parseConstruct(t, parent);

        String token;
        int ttype = t.nextToken();
        if (ttype == StreamPosTokenizer.TT_WORD) {
            if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.PREINFIX
                    && //
                    notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.COMMUTATION_DELIMITER)) {
                t.consumeGreedy(token);
                Node exp2 = parseExpression(t, parent);
                expression = new CommutationNode(notation.getLayerCount(), expression, exp2, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.PREINFIX
                    && //
                    notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.CONJUGATION_DELIMITER)) {
                t.consumeGreedy(token);
                Node exp2 = parseExpression(t, parent);
                expression = new ConjugationNode(notation.getLayerCount(), expression, exp2, expression.getStartPosition(), exp2.getEndPosition());

            } else if (notation.getSyntax(Symbol.ROTATION) == Syntax.PREINFIX
                    && //
                    notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.ROTATION_DELIMITER)) {
                t.consumeGreedy(token);
                Node exp2 = parseExpression(t, parent);
                expression = new RotationNode(notation.getLayerCount(), expression, exp2, expression.getStartPosition(), exp2.getEndPosition());

            } else if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.POSTINFIX
                    && //
                    notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.COMMUTATION_DELIMITER)) {
                t.consumeGreedy(token);
                Node exp2 = parseExpression(t, parent);
                expression = new CommutationNode(notation.getLayerCount(), exp2, expression, expression.getStartPosition(), exp2.getEndPosition());
            } else if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.POSTINFIX
                    && //
                    notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.CONJUGATION_DELIMITER)) {
                t.consumeGreedy(token);
                Node exp2 = parseExpression(t, parent);
                expression = new ConjugationNode(notation.getLayerCount(), exp2, expression, expression.getStartPosition(), exp2.getEndPosition());

            } else if (notation.getSyntax(Symbol.ROTATION) == Syntax.POSTINFIX
                    && //
                    notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.ROTATION_DELIMITER)) {
                t.consumeGreedy(token);
                Node exp2 = parseExpression(t, parent);
                expression = new RotationNode(notation.getLayerCount(), exp2, expression, expression.getStartPosition(), exp2.getEndPosition());
            } else {
                t.pushBack();
            }
        } else {
            t.pushBack();
        }

        parent.add(expression);
        return expression;
    }

    /**
     * Parses a Construct.
     */
    private StatementNode parseConstruct(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "construct", parent);
        }

        String token;
        Symbol symbol;
        StatementNode statement;

        if (t.nextToken() == StreamPosTokenizer.TT_WORD
                && //
                notation.isTokenFor(token = fetchGreedy(t.sval), Symbol.DELIMITER)) {
            // Evaluate: StmtDelimiter
            // -----------------------
            t.consumeGreedy(token);

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
            while ((prefix = parsePrefix(t, prefix)) != null) {
                lastPrefix = prefix;
            }

            // Evaluate: Statement
            Node innerStatement = parseStatement(t, lastPrefix);
            statement.setEndPosition(innerStatement.getEndPosition());

            // Evaluate: {Suffix}
            Node child = statement.getChildAt(0);
            Node suffix = statement;
            while ((suffix = parseSuffix(t, statement)) != null) {
                suffix.add(child);
                child = suffix;
                statement.setEndPosition(suffix.getEndPosition());
            }
        }
        return statement;
    }

    /**
     * Parses a Prefix.
     */
    private Node parsePrefix(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "prefix", parent);
        }

        String token;
        String numericToken = null;

        // Fetch the next token
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            t.pushBack();
            return null;
        }
        if ((token = fetchGreedy(t.sval)) == null) {
            numericToken = fetchGreedyNumber(t.sval);
        }
        // We push back, because we do only decisions in this production
        t.pushBack();

        // If the symbol is neither a symbol or an integer,
        // then it can't match any of the prefix productions.
        if (token == null && numericToken == null) {
            return null;
        }

        // If the token is numeric, we have encountered
        // a repetition prefix.
        if (numericToken != null) {
            if (notation.getSyntax(Symbol.REPETITION) == Syntax.PREFIX) {
                return parseRepetitor(t, parent);
            } else {
                return null;
            }
        }

        // Is it a commutator?
        if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.PREFIX
                && notation.isTokenFor(token, Symbol.COMMUTATION_BEGIN)) {
            return parseExpressionAffix(t, parent);
        }

        // Is it a conjugator?
        if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.PREFIX
                && notation.isTokenFor(token, Symbol.CONJUGATION_BEGIN)) {
            return parseExpressionAffix(t, parent);
        }

        // Is it a rotator?
        if (notation.getSyntax(Symbol.ROTATION) == Syntax.PREFIX
                && notation.isTokenFor(token, Symbol.ROTATION_BEGIN)) {
            return parseExpressionAffix(t, parent);
        }

        // Is it an Inversion?
        if (notation.getSyntax(Symbol.INVERSION) == Syntax.PREFIX
                && notation.isTokenFor(token, Symbol.INVERTOR)) {
            return parseInvertor(t, parent);
        }

        // Is it a repetition?
        if (notation.getSyntax(Symbol.REPETITION) == Syntax.PREFIX
                && notation.isTokenFor(token, Symbol.REPETITION_BEGIN)) {
            return parseRepetitor(t, parent);
        }

        // Is it a reflection?
        if (notation.getSyntax(Symbol.REFLECTION) == Syntax.PREFIX
                && notation.isTokenFor(token, Symbol.REFLECTOR)) {
            return parseReflector(t, parent);
        }

        // Or is it no prefix at all?
        return null;
    }

    /**
     * Parses a Suffix.
     */
    private Node parseSuffix(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "suffix", parent);
        }

        String token;
        String numericToken = null;

        // Fetch the next token.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            t.pushBack();
            return null;
        }
        token = fetchGreedy(t.sval);
        if (token == null) {
            numericToken = fetchGreedyNumber(t.sval);
        }

        // We push back, because we do just decisions in this production
        t.pushBack();

        // If the token is neither alphanumeric nor numeric
        // then it can't match any of the suffix productions.
        if (token == null && numericToken == null) {
            return null;
        }

        // If the token is numeric, we have encountered
        // a repetition suffix.
        if (token == null) {
            if (notation.getSyntax(Symbol.REPETITION) == Syntax.SUFFIX) {
                return parseRepetitor(t, parent);
            } else {
                return null;
            }
        }

        // Is it a commutator?
        if (notation.getSyntax(Symbol.COMMUTATION) == Syntax.SUFFIX
                && notation.isTokenFor(token, Symbol.COMMUTATION_BEGIN)) {
            return parseExpressionAffix(t, parent);
        }

        // Is it a conjugator?
        if (notation.getSyntax(Symbol.CONJUGATION) == Syntax.SUFFIX
                && notation.isTokenFor(token, Symbol.CONJUGATION_BEGIN)) {
            return parseExpressionAffix(t, parent);
        }

        // Is it a rotator?
        if (notation.getSyntax(Symbol.ROTATION) == Syntax.SUFFIX
                && notation.isTokenFor(token, Symbol.ROTATION_BEGIN)) {
            return parseExpressionAffix(t, parent);
        }

        // Is it an Inversion?
        if (notation.getSyntax(Symbol.INVERSION) == Syntax.SUFFIX
                && notation.isTokenFor(token, Symbol.INVERTOR)) {
            return parseInvertor(t, parent);
        }

        // Is it a repetition?
        if (notation.getSyntax(Symbol.REPETITION) == Syntax.SUFFIX
                && notation.isTokenFor(token, Symbol.REPETITION_BEGIN)) {
            return parseRepetitor(t, parent);
        }

        // Is it a reflection?
        if (notation.getSyntax(Symbol.REFLECTION) == Syntax.SUFFIX
                && notation.isTokenFor(token, Symbol.REFLECTOR)) {
            return parseReflector(t, parent);
        }

        // Or is it no prefix at all?
        return null;
    }

    /**
     * Parses an affix which consists of an expression surrounded by a begin
     * token and an end token. Either the begin or the end token is mandatory.
     */
    private Node parseExpressionAffix(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "affix", parent);
        }

        String token;

        //ConjugationNode conjugation = new ConjugationNode();
        //parent.add(conjugation);
        //conjugation.setStartPosition(t.getStartPosition());
        int startPosition = t.getStartPosition();

        // Fetch the next token.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException("Affix: Invalid begin.", t.getStartPosition(), t.getEndPosition());
        }
        if ((token = fetchGreedy(t.sval)) == null) {
            throw new ParseException("Affix: Invalid begin " + t.sval, t.getStartPosition(), t.getEndPosition());
        }

        // Parse the BEGIN token and collect all potential end nodes
        ArrayList<Symbol> endSymbols = new ArrayList<Symbol>();
        if (notation.isTokenFor(token, Symbol.CONJUGATION_BEGIN)
                && (notation.getSyntax(Symbol.CONJUGATION) == Syntax.PREFIX
                || notation.getSyntax(Symbol.CONJUGATION) == Syntax.SUFFIX)) {
            endSymbols.add(Symbol.CONJUGATION_END);
        }
        if (notation.isTokenFor(token, Symbol.COMMUTATION_BEGIN)
                && (notation.getSyntax(Symbol.COMMUTATION) == Syntax.PREFIX
                || notation.getSyntax(Symbol.COMMUTATION) == Syntax.SUFFIX)) {
            endSymbols.add(Symbol.COMMUTATION_END);
        }
        if (notation.isTokenFor(token, Symbol.ROTATION_BEGIN)
                && (notation.getSyntax(Symbol.ROTATION) == Syntax.PREFIX
                || notation.getSyntax(Symbol.ROTATION) == Syntax.SUFFIX)) {
            endSymbols.add(Symbol.ROTATION_END);
        }
        if (endSymbols.isEmpty()) {
            // Or else?
            throw new ParseException("Affix: Invalid begin " + t.sval, t.getStartPosition(), t.getEndPosition());
        }
        t.consumeGreedy(token);

        // Is it a CngrBegin Statement {Statement} CngrEnd thingy?
        Node operator = new SequenceNode(notation.getLayerCount());
        Symbol endSymbol = null;
        Loop:
        do {
            parseExpression(t, operator);
            if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
                throw new ParseException("Affix: Statement missing.", t.getStartPosition(), t.getEndPosition());
            }
            token = fetchGreedy(t.sval);
            for (int i = 0; i < endSymbols.size(); i++) {
                endSymbol = endSymbols.get(i);
                if (notation.isTokenFor(token, endSymbol)) {
                    t.consumeGreedy(token);
                    break Loop;
                }
            }
            t.pushBack();
        } while (token != null);
        //t.nextToken();

        Node affix = null;
        if (endSymbol == Symbol.CONJUGATION_END) {
            ConjugationNode cNode = new ConjugationNode(notation.getLayerCount());
            cNode.setConjugator(operator);
            affix = cNode;
        } else if (endSymbol == Symbol.COMMUTATION_END) {
            CommutationNode cNode = new CommutationNode(notation.getLayerCount());
            cNode.setCommutator(operator);
            affix = cNode;
        } else if (endSymbol == Symbol.ROTATION_END) {
            RotationNode cNode = new RotationNode(notation.getLayerCount());
            cNode.setRotator(operator);
            affix = cNode;
        } else {
            throw new ParseException("Affix: Invalid end symbol " + t.sval, t.getStartPosition(), t.getEndPosition());
        }
        affix.setStartPosition(startPosition);
        affix.setEndPosition(t.getStartPosition() + token.length() - 1);
        if (VERBOSE) {
            printVerbose(t, "end " + affix + "=>" + token, parent);
        }
        parent.add(affix);
        return affix;

    }

    /**
     * Parses an invertor.
     */
    private Node parseInvertor(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "invertor", parent);
        }

        String token;

        InversionNode inversion = new InversionNode(notation.getLayerCount());
        parent.add(inversion);
        inversion.setStartPosition(t.getStartPosition());

        // Fetch the next token.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException("Invertor: Invalid begin.", t.getStartPosition(), t.getEndPosition());
        }
        if ((token = fetchGreedy(t.sval)) == null) {
            throw new ParseException("Invertor: Invalid begin " + t.sval, t.getStartPosition(), t.getEndPosition());
        }

        if (token != null && notation.isTokenFor(token, Symbol.INVERTOR)) {
            inversion.setEndPosition(t.getStartPosition() + token.length() - 1);
            t.consumeGreedy(token);
            return inversion;
        }

        // Or else?
        throw new ParseException("Invertor: Invalid invertor " + t.sval, t.getStartPosition(), t.getEndPosition());
    }

    /**
     * Parses a repetitor.
     */
    private Node parseRepetitor(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "repetitor", parent);
        }
        // Only parse if supported
        if (!notation.isSupported(Symbol.REPETITION)) {
            return null;
        }

        String token;
        String numericToken;
        int intValue;

        RepetitionNode repetition = new RepetitionNode(notation.getLayerCount());
        parent.add(repetition);
        repetition.setStartPosition(t.getStartPosition());

        // Evaluate [RptrBegin] token.
        // ---------------------------
        // Only word tokens are legit.
        // Fetch the next token.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException("Repetitor: Invalid begin.", t.getStartPosition(), t.getEndPosition());
        }

        // Is it a [RptrBegin] token? Consume it.
        token = fetchGreedy(t.sval);
        if (token != null && notation.isTokenFor(token, Symbol.REPETITION_BEGIN)) {
            t.consumeGreedy(token);
        } else {
            t.pushBack();
        }
        // The [RptrBegin] token is now done.

        // Evaluate Integer token.
        // ---------------------------
        // Only word tokens are legit.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException("Repetitor: Repeat count missing.", t.getStartPosition(), t.getEndPosition());
        }

        // Lets be greedy.
        if ((numericToken = fetchGreedyNumber(t.sval)) == null) {
            throw new ParseException("Repetitor: Invalid repeat count " + t.sval, t.getStartPosition(), t.getEndPosition());
        }
        try {
            intValue = Integer.parseInt(numericToken);
        } catch (NumberFormatException e) {
            throw new ParseException("Repetitor: Internal Error " + e.getMessage(), t.getStartPosition(), t.getEndPosition());
        }
        if (intValue < 1) {
            throw new ParseException("Repetitor: Invalid repeat count " + intValue, t.getStartPosition(), t.getEndPosition());
        }
        repetition.setRepeatCount(intValue);
        repetition.setEndPosition(t.getStartPosition() + numericToken.length() - 1);
        t.consumeGreedy(numericToken);
        // The Integer token is now done.

        // Evaluate [RptrEnd] token.
        // ---------------------------
        // Only word tokens are of interest.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            t.pushBack();
            return repetition;
        }

        // Lets be greedy.
        token = fetchGreedy(t.sval);

        if (token == null) {
            t.pushBack();
            return repetition;
        }

        // Is it a [RptrEnd] token? Consume it.
        if (notation.isTokenFor(token, Symbol.REPETITION_END)) {
            t.consumeGreedy(token);
        } else {
            t.pushBack();
        }
        return repetition;
    }

    /**
     * Parses a reflector.
     */
    private Node parseReflector(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "reflector", parent);
        }
        String token;

        ReflectionNode reflection = new ReflectionNode(notation.getLayerCount());
        parent.add(reflection);
        reflection.setStartPosition(t.getStartPosition());

        // Fetch the next token.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException("Reflector: Invalid begin.", t.getStartPosition(), t.getEndPosition());
        }
        token = fetchGreedy(t.sval);

        if (token != null && notation.isTokenFor(token, Symbol.REFLECTOR)) {
            reflection.setEndPosition(t.getStartPosition() + token.length() - 1);
            t.consumeGreedy(token);
            return reflection;
        }

        // Or else?
        throw new ParseException("Reflector: Invalid reflector " + t.sval, t.getStartPosition(), t.getEndPosition());
    }

    /**
     * Parses a Statement.
     */
    private Node parseStatement(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "statement", parent);
        }
        String token;
        String numericToken;
        int intValue;

        // Fetch the next token.
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException("Statement: Invalid begin.", t.getStartPosition(), t.getEndPosition());
        }
        token = fetchGreedy(t.sval);

        // Evaluate: Macro
        if (token == null) {
            throw new ParseException("Statement: Unknown statement " + t.sval, t.getStartPosition(), t.getEndPosition());
        } else if (macros.get(token) != null) {
            t.pushBack();
            return parseMacro(t, parent);
        }

        // Is it a Move token? Parse it.
        if (notation.isTokenFor(token, Symbol.MOVE)) {
            t.pushBack();
            return parseMove(t, parent);

            // Is it a NOP token? Parse it.
        } else if (notation.isTokenFor(token, Symbol.NOP)) {
            t.consumeGreedy(token);
            NOPNode nop = new NOPNode(notation.getLayerCount(), t.getStartPosition(), t.getEndPosition());
            parent.add(nop);
            return nop;

            // Is it a Permutation sign token? Parse a permutation.
        } else if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.PREFIX
                && (notation.isTokenFor(token, Symbol.PERMUTATION_PLUS) 
                || notation.isTokenFor(token, Symbol.PERMUTATION_MINUS) 
                || notation.isTokenFor(token, Symbol.PERMUTATION_PLUSPLUS))) {
            int startpos = t.getStartPosition();
            t.pushBack();
            Symbol sign = parsePermutationSign(t, parent);
            if (sign != null) {
                if (t.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new ParseException(
                            "Permutation: Unexpected token - expected a word.", t.getStartPosition(), t.getEndPosition());
                }
                token = fetchGreedy(t.sval);
                if (!notation.isTokenFor(token, Symbol.PERMUTATION_BEGIN)) {
                    throw new ParseException(
                            "Permutation: Unexpected token - expected permutation begin.", t.getStartPosition(), t.getEndPosition());
                }
                t.consumeGreedy(token);

                PermutationNode pnode = (PermutationNode) parsePermutation(t, parent, startpos, sign);
                return pnode;
            }
        }

        // Okay, it's not a move and not a permutation sign.
        // Since we allow for some ambiguity of the
        // tokens used by the grouping, conjugation, commutation and permutation
        // statement it gets a little bit complicated here.
        // Create a bit mask with a bit for each expected statement.
        int expressionMask
                = ((notation.isTokenFor(token, Symbol.GROUPING_BEGIN)) ? GROUPING_MASK : UNKNOWN_MASK)
                | //
                ((notation.getSyntax(Symbol.CONJUGATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.CONJUGATION_BEGIN)) ? CONJUGATION_MASK : UNKNOWN_MASK)
                | //
                ((notation.getSyntax(Symbol.COMMUTATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.COMMUTATION_BEGIN)) ? COMMUTATION_MASK : UNKNOWN_MASK)
                | //
                ((notation.getSyntax(Symbol.ROTATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.ROTATION_BEGIN)) ? ROTATION_MASK : UNKNOWN_MASK)
                | //
                ((notation.getSyntax(Symbol.INVERSION) == Syntax.CIRCUMFIX && notation.isTokenFor(token, Symbol.INVERSION_BEGIN)) ? INVERSION_MASK : UNKNOWN_MASK)
                | //
                ((notation.getSyntax(Symbol.REFLECTION) == Syntax.CIRCUMFIX && notation.isTokenFor(token, Symbol.REFLECTION_BEGIN)) ? REFLECTION_MASK : UNKNOWN_MASK)
                | //
                ((notation.isSupported(Symbol.PERMUTATION) && notation.isTokenFor(token, Symbol.PERMUTATION_BEGIN)) ? PERMUTATION_MASK : UNKNOWN_MASK);

        // Is it a Permutation Begin token without any ambiguity?
        if (expressionMask == PERMUTATION_MASK) {
            int p = t.getStartPosition();
            t.consumeGreedy(token);
            return parsePermutation(t, parent, p, null);

            // Is it an ambiguous permutation begin token?
        } else if ((expressionMask & PERMUTATION_MASK) == PERMUTATION_MASK) {
            int p = t.getStartPosition();
            t.consumeGreedy(token);

            // Look ahead
            if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
                throw new ParseException("Statement: Word missing.", t.getStartPosition(), t.getEndPosition());
            }
            // Lets be greedy.
            token = fetchGreedy(t.sval);
            t.pushBack();
            if (token != null
                    && notation.isTokenFor(token, Symbol.PERMUTATION)
                    && !notation.isTokenFor(token, Symbol.GROUPING_BEGIN)) {
                return parsePermutation(t, parent, p, null);
            } else {
                return parseCompoundStatement(t, parent, p, expressionMask);
            }

            // Is it one of the other Begin tokens?
        } else if (expressionMask != UNKNOWN_MASK) {
            int p = t.getStartPosition();
            t.consumeGreedy(token);
            return parseCompoundStatement(t, parent, p, expressionMask);
        }

        throw new ParseException("Statement: Invalid Statement " + t.sval, t.getStartPosition(), t.getEndPosition());
    }

    /**
     * Parse a compound statement.
     */
    private Node parseCompoundStatement(StreamPosTokenizer t, Node parent, int startPos, int beginTypeMask)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "compount statement", parent);
        }
        Node seq1 = new SequenceNode(notation.getLayerCount());
        seq1.setStartPosition(startPos);
        parent.add(seq1);
        Node seq2 = null;
        Node grouping = seq1;

        // The final type mask reflects the final type that we have determined
        // after parsing all of the grouping.
        int finalTypeMask = beginTypeMask & (GROUPING_MASK | CONJUGATION_MASK | COMMUTATION_MASK | ROTATION_MASK | REFLECTION_MASK | INVERSION_MASK);

        // Evaluate: {Statement} , (GROUPING_END | COMMUTATION_END | CONJUGATION_END | ROTATION_END) ;
        TheGrouping:
        while (true) {
            switch (t.nextToken()) {
                case StreamPosTokenizer.TT_WORD:
                    // Look ahead the nextElement token.
                    String token = fetchGreedy(t.sval);
                    if (token == null) {
                        t.pushBack();
                        break TheGrouping;
                    }

                    int endTypeMask
                            = ((notation.isTokenFor(token, Symbol.GROUPING_END)) ? GROUPING_MASK : UNKNOWN_MASK)
                            | //
                            ((notation.getSyntax(Symbol.CONJUGATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.CONJUGATION_END)) ? CONJUGATION_MASK : UNKNOWN_MASK)
                            | //
                            ((notation.getSyntax(Symbol.COMMUTATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.COMMUTATION_END)) ? COMMUTATION_MASK : UNKNOWN_MASK)
                            | //
                            ((notation.getSyntax(Symbol.INVERSION) == Syntax.CIRCUMFIX && notation.isTokenFor(token, Symbol.INVERSION_END)) ? INVERSION_MASK : UNKNOWN_MASK)
                            | //
                            ((notation.getSyntax(Symbol.REFLECTION) == Syntax.CIRCUMFIX && notation.isTokenFor(token, Symbol.REFLECTION_END)) ? REFLECTION_MASK : UNKNOWN_MASK)
                            | //
                            ((notation.getSyntax(Symbol.ROTATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.ROTATION_END)) ? ROTATION_MASK : UNKNOWN_MASK);
                    int delimiterTypeMask
                            = ((notation.getSyntax(Symbol.CONJUGATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.CONJUGATION_DELIMITER)) ? CONJUGATION_MASK : 0) 
                            | ((notation.getSyntax(Symbol.COMMUTATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.COMMUTATION_DELIMITER)) ? COMMUTATION_MASK : 0)
                            | ((notation.getSyntax(Symbol.ROTATION) == Syntax.PRECIRCUMFIX && notation.isTokenFor(token, Symbol.ROTATION_DELIMITER)) ? ROTATION_MASK : 0);

                    if (endTypeMask != 0) {
                        finalTypeMask &= endTypeMask;
                        grouping.setEndPosition(t.getStartPosition() + token.length() - 1);
                        t.consumeGreedy(token);

                        break TheGrouping;

                    } else if (delimiterTypeMask != 0) {
                        finalTypeMask &= delimiterTypeMask;
                        if (finalTypeMask == 0) {
                            throw new ParseException("Grouping: Invalid delimiter.", t.getStartPosition(), t.getEndPosition());
                        }
                        if (seq2 == null) {
                            seq1.setEndPosition(t.getStartPosition());
                            seq2 = new SequenceNode(notation.getLayerCount());
                            seq2.setStartPosition(t.getEndPosition());
                            parent.add(seq2);
                            grouping = seq2;
                        } else {
                            throw new ParseException("Grouping: Delimiter must occur only once", t.getStartPosition(), t.getEndPosition());
                        }
                        t.consumeGreedy(token);

                    } else {
                        t.pushBack();
                        parseExpression(t, grouping);
                    }
                    break;
                case StreamPosTokenizer.TT_EOF:
                    throw new ParseException(
                            "Grouping: End missing.", t.getStartPosition(), t.getEndPosition());
                default:
                    throw new ParseException(
                            "Grouping: Internal error.", t.getStartPosition(), t.getEndPosition());
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
            finalTypeMask &= -1 ^ GROUPING_MASK;
        }

        switch (finalTypeMask) {
            case GROUPING_MASK:
                if (seq2 != null) {
                    throw new ParseException(
                            "Grouping: Invalid Grouping.", startPos, t.getEndPosition());
                } else {
                    grouping = new GroupingNode(notation.getLayerCount(), startPos, t.getEndPosition());
                    while (seq1.getChildCount() > 0) {
                        grouping.add(seq1.getChildAt(0));
                    }
                }
                break;

            case INVERSION_MASK:
                if (seq2 != null) {
                    throw new ParseException(
                            "Inversion: Invalid Inversion.", startPos, t.getEndPosition());
                } else {
                    grouping = new InversionNode(notation.getLayerCount(), startPos, t.getEndPosition());
                    while (seq1.getChildCount() > 0) {
                        grouping.add(seq1.getChildAt(0));
                    }
                }
                break;

            case REFLECTION_MASK:
                if (seq2 != null) {
                    throw new ParseException(
                            "Reflection: Invalid Reflection.", startPos, t.getEndPosition());
                } else {
                    grouping = new ReflectionNode(notation.getLayerCount(), startPos, t.getEndPosition());
                    while (seq1.getChildCount() > 0) {
                        grouping.add(seq1.getChildAt(0));
                    }
                }
                break;

            case CONJUGATION_MASK:
                if (seq2 == null) {
                    throw new ParseException(
                            "Conjugation: Conjugate missing.", startPos, t.getEndPosition());
                } else {
                    grouping = new ConjugationNode(notation.getLayerCount(), seq1, seq2, startPos, t.getEndPosition());
                }
                break;

            case COMMUTATION_MASK:
                if (seq2 == null) {
                    if (seq1.getChildCount() == 2 && seq1.getSymbol() == Symbol.SEQUENCE) {
                        grouping = new CommutationNode(notation.getLayerCount(), seq1.getChildAt(0), seq1.getChildAt(1), startPos, t.getEndPosition());
                    } else {
                        throw new ParseException(
                                "Commutation: Commutee missing.", startPos, t.getEndPosition());
                    }
                } else {
                    grouping = new CommutationNode(notation.getLayerCount(), seq1, seq2, startPos, t.getEndPosition());
                }
                break;

            case ROTATION_MASK:
                if (seq2 == null) {
                    throw new ParseException(
                            "Rotation: Rotatee missing.", startPos, t.getEndPosition());
                } else {
                    grouping = new RotationNode(notation.getLayerCount(), seq1, seq2, startPos, t.getEndPosition());
                }
                break;

            default:
                StringBuilder ambiguous = new StringBuilder();
                if ((finalTypeMask & GROUPING_MASK) != 0) {
                    ambiguous.append("Grouping");
                }
                if ((finalTypeMask & INVERSION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous.append(" or ");
                    }
                    ambiguous.append("Inversion");
                }
                if ((finalTypeMask & REFLECTION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous.append(" or ");
                    }
                    ambiguous.append("Reflection");
                }
                if ((finalTypeMask & CONJUGATION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous.append(" or ");
                    }
                    ambiguous.append("Conjugation");
                }
                if ((finalTypeMask & COMMUTATION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous.append(" or ");
                    }
                    ambiguous.append("Commutation");
                }
                if ((finalTypeMask & ROTATION_MASK) != 0) {
                    if (ambiguous.length() != 0) {
                        ambiguous.append(" or ");
                    }
                    ambiguous.append("Rotation");
                }
                throw new ParseException(
                        "Compound Statement: Ambiguous compound statement, possibilities are " + ambiguous + ".", startPos, t.getEndPosition());
        }

        parent.add(grouping);
        return grouping;
    }

    /**
     * Parses a permutation.
     */
    private Node parsePermutation(StreamPosTokenizer t, Node parent, int startPos, Symbol sign)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "permutation", parent);
        }

        PermutationNode permutation = new PermutationNode(notation.getLayerCount());
        parent.add(permutation);
        permutation.setStartPosition(startPos);

        if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.PRECIRCUMFIX) {
            sign = parsePermutationSign(t, parent);
        }

        ThePermutation:
        while (true) {
            switch (t.nextToken()) {
                case StreamPosTokenizer.TT_WORD:

                    // Evaluate PermEnd
                    String token = fetchGreedy(t.sval);
                    if (notation.isTokenFor(token, Symbol.PERMUTATION_END)) {
                        permutation.setEndPosition(t.getStartPosition() + token.length() - 1);
                        t.consumeGreedy(token);
                        break ThePermutation;

                    } else {
                        t.pushBack();
                        parsePermutationItem(t, permutation);
                        if (t.nextToken() == StreamPosTokenizer.TT_WORD) {
                            token = fetchGreedy(t.sval);
                            if (notation.isTokenFor(token, Symbol.PERMUTATION_DELIMITER)) {
                                t.consumeGreedy(token);

                            } else if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.POSTCIRCUMFIX 
                                    && (notation.isTokenFor(token, Symbol.PERMUTATION_PLUS) 
                                    || notation.isTokenFor(token, Symbol.PERMUTATION_MINUS) 
                                    || notation.isTokenFor(token, Symbol.PERMUTATION_PLUSPLUS))) {
                                t.pushBack();
                                sign = parsePermutationSign(t, parent);
                                if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
                                    throw new ParseException(
                                            "Permutation: End expected.", t.getStartPosition(), t.getEndPosition());
                                }
                                token = fetchGreedy(t.sval);
                                if (notation.isTokenFor(token, Symbol.PERMUTATION_END)) {
                                    permutation.setEndPosition(t.getStartPosition() + token.length() - 1);
                                    t.consumeGreedy(token);
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
                case StreamPosTokenizer.TT_EOF:
                    throw new ParseException(
                            "Permutation: End missing.", t.getStartPosition(), t.getEndPosition());
                default:
                    throw new ParseException(
                            "Permutation: Internal error.", t.getStartPosition(), t.getEndPosition());
            }
        }

        if (notation.getSyntax(Symbol.PERMUTATION) == Syntax.SUFFIX) {
            sign = parsePermutationSign(t, parent);
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
     * Parses a permutation item.
     */
    private void parsePermutationItem(StreamPosTokenizer t, PermutationNode parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "permutationItem", parent);
        }
        int startpos = t.getStartPosition();
        Symbol sign = null;
        String token;
        Symbol symbol;
        int leadingSignStartPos = -1, leadingSignEndPos = -1;

        // Evaluate [sign]
        Syntax syntax = notation.getSyntax(Symbol.PERMUTATION);
        if (syntax == Syntax.PRECIRCUMFIX
                || syntax == Syntax.PREFIX
                || syntax == Syntax.POSTCIRCUMFIX) {
            leadingSignStartPos = t.getStartPosition();
            leadingSignEndPos = t.getEndPosition();
            sign = parsePermutationSign(t, parent);
        }
        // Evaluate PermFace [PermFace] [PermFace]
        Symbol[] faceSymbols = new Symbol[3];
        int type = 0;

        StringBuilder partName = new StringBuilder();
        while (type < 3) {
            if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
                throw new ParseException("PermutationItem: Face token missing.", t.getStartPosition(), t.getEndPosition());
            }
            token = fetchGreedy(t.sval);
            symbol = notation.getSymbolFor(token, Symbol.PERMUTATION);
            if (symbol == null) {
                t.pushBack();
                break;
            }
            if (Symbol.FACE_R.compareTo(symbol) <= 0
                    && symbol.compareTo(Symbol.FACE_B) <= 0) {
                if (VERBOSE) {
                    printVerbose(t, "permutationItem Face:" + token, parent);
                }
                partName.append(token);
                faceSymbols[type++] = symbol;
                t.consumeGreedy(token);
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
        if (t.nextToken() == StreamPosTokenizer.TT_WORD
                && (token = fetchGreedyNumber(t.sval)) != null) {
            if (type == 3) {
                throw new ParseException("PermutationItem: Corner parts must not have a number " + partNumber, t.getStartPosition(), t.getEndPosition());
            }

            try {
                partNumber = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new ParseException("PermutationItem: Internal Error " + e.getMessage(), t.getStartPosition(), t.getEndPosition());
            }
            t.consumeGreedy(token);
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

        try {
            parent.addPermItem(type, sign, faceSymbols, partNumber, notation.getLayerCount());
        } catch (IllegalArgumentException e) {
            ParseException pe = new ParseException(e.getMessage(), startpos, t.getEndPosition());
            pe.initCause(e);
            throw pe;
        }
    }

    /**
     * Parses a permutation sign and returns null or one of the three sign
     * symbols.
     */
    private Symbol parsePermutationSign(StreamPosTokenizer t, Node parent) throws ParseException, IOException {
        if (VERBOSE) {
            printVerbose(t, "permutationItem Sign", parent);
        }
        Symbol sign;
        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            t.pushBack();
            sign = null;
        } else {
            String token = fetchGreedy(t.sval);
            Symbol symbol = notation.getSymbolFor(token, Symbol.PERMUTATION);
            if (symbol == Symbol.PERMUTATION_PLUS 
                    || symbol == Symbol.PERMUTATION_PLUSPLUS
                    || symbol == Symbol.PERMUTATION_MINUS) {
                sign = symbol;
                t.consumeGreedy(token);
            } else {
                sign = null;
                t.pushBack();
            }
        }
        return sign;
    }

    /**
     * Parses a move.
     */
    private Node parseMove(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "transformation", parent);
        }
        MoveNode move = new MoveNode(notation.getLayerCount());
        parent.add(move);

        if (t.nextToken() != StreamPosTokenizer.TT_WORD) {
            throw new ParseException(
                    "Move: Symbol missing.", t.getStartPosition(), t.getEndPosition());
        }
        move.setStartPosition(t.getStartPosition());
        String token = fetchGreedy(t.sval);
        Symbol symbol = notation.getSymbolFor(token, Symbol.MOVE);

        if (Symbol.MOVE == symbol) {
            notation.configureMoveFromToken(move, token);
            move.setEndPosition(t.getStartPosition() + token.length() - 1);
            t.consumeGreedy(token);
        } else {
            throw new ParseException(
                    "Move: Invalid token " + t.sval, t.getStartPosition(), t.getEndPosition());
        }
        return move;
    }

    /**
     * Parses a macro.
     */
    private Node parseMacro(StreamPosTokenizer t, Node parent)
            throws IOException {
        if (VERBOSE) {
            printVerbose(t, "macro", parent);
        }
        switch (t.nextToken()) {
            case StreamPosTokenizer.TT_WORD:
                String token = fetchGreedy(t.sval);
                MacroNode macro = macros.get(token);
                if (macro != null) {
                    MacroNode node;
                    node = (MacroNode) macro.cloneSubtree();
                    for (Node child : node.preorderIterable()) {
                        child.setStartPosition(t.getStartPosition());
                        child.setEndPosition(t.getStartPosition() + token.length() - 1);
                    }
                    parent.add(node);
                    try {
                        node.expand(this);
                    } catch (IOException e) {
                        if (e instanceof ParseException) {
                            ParseException pe = (ParseException) e;
                            throw new ParseException(
                                    "Macro '" + token + "': " + e.getMessage() + " @" + pe.getStartPosition() + ".." + pe.getEndPosition(), t.getStartPosition(), t.getStartPosition() + token.length() - 1);
                        } else {
                            throw new ParseException(
                                    "Macro '" + token + "': " + e.getMessage(), t.getStartPosition(), t.getStartPosition() + token.length() - 1);
                        }
                    }

                    t.consumeGreedy(token);
                    return node;
                } else {
                    throw new ParseException(
                            "Macro: Unexpected or unknown Symbol.",
                            t.getStartPosition(),
                            t.getStartPosition() + token.length() - 1);
                }

            //break;
            case StreamPosTokenizer.TT_EOF:
                throw new ParseException(
                        "Macro: Symbol missing.", t.getStartPosition(), t.getEndPosition());
            default:
                throw new ParseException(
                        "Macro: Internal error.", t.getStartPosition(), t.getEndPosition());
        }
    }
}

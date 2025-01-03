/*
 * @(#)TokenizerTest.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import ch.randelshofer.rubik.notation.DefaultScriptNotation;
import org.jhotdraw.annotation.Nonnull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TokenizerTest {

    @Nonnull
    @TestFactory
    public List<DynamicTest> testTokenizerWithDefaultNotation() {
        DefaultScriptNotation defaultNotation = new DefaultScriptNotation();
        Tokenizer tt = new Tokenizer();
        tt.skipWhitespace();
        for (String token : defaultNotation.getTokens()) {
            tt.addKeyword(token);
        }

        return Arrays.asList(
                dynamicTest("1", () -> doTokenizer(tt, "<CU CF>'(R)", "0..1:KEY:<, 1..3:KEY:CU, 4..6:KEY:CF, 6..8:KEY:>', 8..9:KEY:(, 9..10:KEY:R, 10..11:KEY:)"))
        );

    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> testTokenizerSetTo() {
        DefaultScriptNotation defaultNotation = new DefaultScriptNotation();
        Tokenizer tt = new Tokenizer();
        tt.skipWhitespace();
        for (String token : defaultNotation.getTokens()) {
            tt.addKeyword(token);
        }

        return Arrays.asList(
                dynamicTest("1", () -> doTokenizerSetTo(tt, "<CU CF>'(R)", "0..1:KEY:<, 1..3:KEY:CU, 4..6:KEY:CF, 6..8:KEY:>', 8..9:KEY:(, 9..10:KEY:R, 10..11:KEY:)"))
        );

    }

    @TestFactory
    public List<DynamicTest> shouldParseEntireString() {
        Tokenizer tt = new Tokenizer();
        return Arrays.asList(
                dynamicTest("1", () -> doTokenizer(tt, "lorem ipsum", "0..11:WORD:lorem ipsum"))
        );
    }

    @TestFactory
    public List<DynamicTest> shouldSkipWhitespace() {
        Tokenizer tt = new Tokenizer();
        tt.skipWhitespace();
        return Arrays.asList(
                dynamicTest("1", () -> doTokenizer(tt, "lorem ipsum", "0..5:WORD:lorem, 6..11:WORD:ipsum"))
        );
    }

    @TestFactory
    public List<DynamicTest> shouldSkipWhitespaceAndParseNumbers() {
        Tokenizer tt = new Tokenizer();
        tt.skipWhitespace();
        tt.addNumbers();
        return Arrays.asList(
                dynamicTest("1", () -> doTokenizer(tt, "1 2", "0..1:NUM:1, 2..3:NUM:2")),
                dynamicTest("2", () -> doTokenizer(tt, "1lorem 2ipsum", "0..1:NUM:1, 1..6:WORD:lorem, 7..8:NUM:2, 8..13:WORD:ipsum")),
                dynamicTest("3", () -> doTokenizer(tt, "lorem1 ipsum2", "0..5:WORD:lorem, 5..6:NUM:1, 7..12:WORD:ipsum, 12..13:NUM:2")),
                dynamicTest("4", () -> doTokenizer(tt, "16 21", "0..2:NUM:16, 3..5:NUM:21")),
                dynamicTest("5", () -> doTokenizer(tt, "-16", "0..1:WORD:-, 1..3:NUM:16"))
        );
    }

    @TestFactory
    public List<DynamicTest> shouldSkipWhitespaceAndParseNumbersAndParseKeywords() {
        Tokenizer tt = new Tokenizer();
        tt.skipWhitespace();
        tt.addNumbers();
        tt.addKeyword("tom");
        tt.addKeyword("tomato");
        tt.addKeyword("two2");
        tt.addKeyword("3three");

        return Arrays.asList(
                dynamicTest("1", () -> doTokenizer(tt, "tom", "0..3:KEY:tom")),
                dynamicTest("2", () -> doTokenizer(tt, "toma", "0..3:KEY:tom, 3..4:WORD:a")),
                dynamicTest("3", () -> doTokenizer(tt, "tomato", "0..6:KEY:tomato")),
                dynamicTest("4", () -> doTokenizer(tt, "tomatoto", "0..6:KEY:tomato, 6..8:WORD:to")),
                dynamicTest("5", () -> doTokenizer(tt, "tomatotom", "0..6:KEY:tomato, 6..9:KEY:tom")),
                dynamicTest("6", () -> doTokenizer(tt, "to14matotom", "0..2:WORD:to, 2..4:NUM:14, 4..11:WORD:matotom")),
                dynamicTest("7", () -> doTokenizer(tt, "tomato tom", "0..6:KEY:tomato, 7..10:KEY:tom")),
                dynamicTest("8", () -> doTokenizer(tt, "tom ato tom", "0..3:KEY:tom, 4..7:WORD:ato, 8..11:KEY:tom")),
                dynamicTest("9", () -> doTokenizer(tt, "two2 3three", "0..4:KEY:two2, 5..11:KEY:3three")),
                dynamicTest("10", () -> doTokenizer(tt, "two24 63three", "0..4:KEY:two2, 4..5:NUM:4, 6..8:NUM:63, 8..13:WORD:three")),
                dynamicTest("11", () -> doTokenizer(tt, "two24 6 3three", "0..4:KEY:two2, 4..5:NUM:4, 6..7:NUM:6, 8..14:KEY:3three")),
                dynamicTest("12", () -> doTokenizer(tt, "two24 63thre", "0..4:KEY:two2, 4..5:NUM:4, 6..8:NUM:63, 8..12:WORD:thre"))
        );
    }

    @Nonnull
    @TestFactory
    public List<DynamicTest> shouldSkipWhitespaceAndParseNumbersAndParseKeywordsAndParseComments() {
        Tokenizer tt = new Tokenizer();
        tt.skipWhitespace();
        tt.addNumbers();
        tt.addKeyword("tom");
        tt.addKeyword("tomato");
        tt.addKeyword("two2");
        tt.addKeyword("3three");
        tt.addComment("/*", "*/");
        tt.addComment("//", "\n");


        return Arrays.asList(
                dynamicTest("comment.21 - on, but unable to separate from word", () -> doTokenizer(tt, "lorem/* comment */ipsum", "0..7:WORD:lorem/*, 8..15:WORD:comment, 16..23:WORD:*/ipsum")),
                dynamicTest("comment.22 - on, but, unable to separate from word", () -> doTokenizer(tt, "lorem// comment\nipsum", "0..7:WORD:lorem//, 8..15:WORD:comment, 16..21:WORD:ipsum")),
                dynamicTest("comment.23 - on", () -> doTokenizer(tt, "lorem /* comment */ipsum", "0..5:WORD:lorem, 19..24:WORD:ipsum")),
                dynamicTest("comment.24 - on", () -> doTokenizer(tt, "lorem // comment\nipsum", "0..5:WORD:lorem, 17..22:WORD:ipsum")),
                dynamicTest("comment.25 - on", () -> doTokenizer(tt, "tom/* comment */tom", "0..3:KEY:tom, 16..19:KEY:tom")),
                dynamicTest("comment.26 - on", () -> doTokenizer(tt, "tom// comment\ntom", "0..3:KEY:tom, 14..17:KEY:tom"))
        );
    }

    /**
     * Utility function for testing a {@link Tokenizer} instance.
     */
    private void doTokenizer(@Nonnull Tokenizer instance, String input, String expected) {
        doNextToken(instance, input, expected);
        doPushBack(instance, input, expected);
    }

    private void doTokenizerSetTo(@Nonnull Tokenizer instance, String input, String expected) {
        Tokenizer tt = instance;
        tt.setInput(input);
        StringBuffer buf = new StringBuffer();
        while (tt.nextToken() != Tokenizer.TT_EOF) {
            Tokenizer copy = new Tokenizer();
            copy.setTo(tt);
            tt = copy;
            appendToken(tt, buf);
        }
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    /**
     * Tokenizes the input. Calls nextToken() for each token.
     *
     * @param instance the tokenizer
     * @param input    the input
     * @param expected the expected output
     */
    private void doNextToken(@Nonnull Tokenizer instance, String input, String expected) {
        instance.setInput(input);
        StringBuffer buf = new StringBuffer();
        while (instance.nextToken() != Tokenizer.TT_EOF) {
            appendToken(instance, buf);
        }
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    /**
     * Tokenizes the input. Calls nextToken(),pushBack(),nextToken() for each token.
     *
     * @param instance the tokenizer
     * @param input    the input
     * @param expected the expected output
     */
    private void doPushBack(@Nonnull Tokenizer instance, String input, String expected) {
        instance.setInput(input);
        StringBuffer buf = new StringBuffer();
        while (instance.nextToken() != Tokenizer.TT_EOF) {
            instance.pushBack();
            instance.nextToken();
            appendToken(instance, buf);
        }
        String actual = buf.toString();
        assertEquals(expected, actual);
    }

    /**
     * Appends a string description of the current token in the tokenizer to
     * the given buffer.
     *
     * @param instance the tokenizer
     * @param buf      the buffer
     */
    private void appendToken(@Nonnull Tokenizer instance, @Nonnull StringBuffer buf) {
        if (!buf.isEmpty()) {
            buf.append(", ");
        }
        buf.append(instance.getStartPosition());
        buf.append("..");
        buf.append(instance.getEndPosition());
        buf.append(':');
        switch (instance.getTokenType()) {
            case Tokenizer.TT_NUMBER:
                buf.append("NUM");
                buf.append(':');
                buf.append(instance.getNumericValue());
                break;
            case Tokenizer.TT_KEYWORD:
                buf.append("KEY");
                buf.append(':');
                buf.append(instance.getStringValue());
                break;
            case Tokenizer.TT_WORD:
                buf.append("WORD");
                buf.append(':');
                buf.append(instance.getStringValue());
                break;
            default:
                throw new AssertionError("Unexpected token type: " + instance.getTokenType());
        }
    }
}
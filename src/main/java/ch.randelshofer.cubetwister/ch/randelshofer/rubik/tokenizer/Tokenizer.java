/*
 * @(#)Tokenizer.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A greedy tokenizer.
 * <p>
 * By default this tokenizer parses the entire input sequence as a single word.
 * You can activate skipping of whitespaces by adding whitespace tokens using {@link #addSkip}.
 * You can activate tokenization of positive integer numbers, by invoking {@link #addNumbers}.
 * You can activate tokenization of keywords, by adding keyword tokens using {@link #addKeyword}.
 * You can activate tokenization of comments, by adding comment tokens using {@link #addComment}.
 * <p>
 * Note that keyword parsing is greedy.
 * <p>
 * The tokenizer supports backtracking. That is, it can be
 * set to the state of another tokenizer. See method {@link #setTo}.
 */
public class Tokenizer {
    public final static int TT_WORD = -2;
    public final static int TT_EOF = -1;

    // the following token types can be activated on demand
    public final static int TT_KEYWORD = -4;
    public final static int TT_NUMBER = -5;

    // the following token types are used internally
    private final static int TT_DIGIT = -11;
    private final static int TT_SKIP = -13;


    @Nonnull
    private String input = "";
    private int pos = 0;
    private boolean pushedBack = false;
    private int ttype = TT_EOF;
    private int tstart = 0;
    private int tend = 0;
    @Nullable
    private String sval = null;
    @Nullable
    private Integer nval = null;

    @Nonnull
    private CharSeqNode keywordTree = new CharSeqNode();
    /**
     * Map<Character,TType> maps char to ttype or to null
     */
    private Map<Character, Integer> lookup = new HashMap<>();


    public Tokenizer() {
    }

    /**
     * Adds a comment token.
     * <p>
     * To add a single line comment, use:
     * <pre>
     *     addComment("//","\n");
     * </pre>
     * <p>
     * To add a multi line comment, use:
     * <pre>
     *     addComment("/*", "* /");
     * </pre>
     */
    public void addComment(@Nonnull String start, @Nonnull String end) {
        CharSeqNode.addStartEndSequence(keywordTree, start, end);
    }

    /**
     * Adds a digit character.
     */
    private void addDigit(char ch) {
        this.lookup.put(ch, TT_DIGIT);
    }

    /**
     * Adds a keyword.
     *
     * @param keyword the keyword token
     */
    public void addKeyword(@Nonnull String keyword) {
        CharSeqNode.addCharacterSequence(keywordTree, keyword);
    }


    /**
     * Defines the tokens needed for parsing non-negative integers.
     */
    public void addNumbers() {
        this.addDigit('0');
        this.addDigit('1');
        this.addDigit('2');
        this.addDigit('3');
        this.addDigit('4');
        this.addDigit('5');
        this.addDigit('6');
        this.addDigit('7');
        this.addDigit('8');
        this.addDigit('9');
    }

    /**
     * Adds a character that the tokenizer should skip.
     */
    private void addSkip(char ch) {
        this.lookup.put(ch, TT_SKIP);
    }

    /**
     * Returns the end position of the current token.
     */
    public int getEndPosition() {
        return this.tend;
    }

    /**
     * Returns the current token numeric value.
     *
     * @return Integer value or null
     */
    @Nullable
    public Integer getNumericValue() {
        return this.nval;
    }

    /**
     * Returns the start position of the current token.
     */
    public int getStartPosition() {
        return this.tstart;
    }

    /**
     * Returns the current token string value.
     *
     * @return String value or null
     */
    @Nullable
    public String getStringValue() {
        return this.sval;
    }

    /**
     * Returns the current token type.
     *
     * @return token type
     */
    public int getTokenType() {
        return this.ttype;
    }

    private <K, V> V getOrDefault(@Nonnull Map<K, V> map, K key, V defaultValue) {
        V value = map.get(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Parses the next token.
     *
     * @return [Object] ttype
     */
    public int nextToken() {
        loop:
        while (true) {
            if (this.pushedBack) {
                this.pushedBack = false;
                return this.ttype;
            }

            int start = this.pos;
            int ch = this.read();

            // try to skip characters
            while (ch != TT_EOF && this.getOrDefault(this.lookup, (char) ch, TT_WORD) == TT_SKIP) {
                ch = this.read();
                start += 1;
            }

            // try to tokenize a keyword or a comment
            CharSeqNode node = this.keywordTree;
            CharSeqNode foundNode = null;
            int end = start;
            while (ch != TT_EOF && node != null && node.getChild((char) ch) != null) {
                node = node.getChild((char) ch);
                if (node != null && node.getCharseq() != null) {
                    foundNode = node;
                    end = this.pos;
                }
                ch = this.read();
            }
            if (foundNode != null) {
                String commentEnd = foundNode.getEndseq();
                if (commentEnd != null) {
                    seekTo(commentEnd);
                    continue loop;
                }

                this.setPosition(end);
                this.ttype = TT_KEYWORD;
                this.tstart = start;
                this.tend = end;
                this.sval = foundNode.getCharseq();
                return this.ttype;
            }
            this.setPosition(start);
            ch = this.read();

            // try to tokenize a number
            if (ch != TT_EOF && this.getOrDefault(this.lookup, (char) ch, TT_WORD) == TT_DIGIT) {
                while (ch != TT_EOF && this.getOrDefault(this.lookup, (char) ch, TT_WORD) == TT_DIGIT) {
                    ch = this.read();
                }
                if (ch != TT_EOF) {
                    this.unread();
                }
                this.ttype = TT_NUMBER;
                this.tstart = start;
                this.tend = this.pos;
                this.sval = this.input.subSequence(start, this.pos).toString();
                this.nval = Integer.parseInt(this.sval);
                return this.ttype;
            }

            // try to tokenize a word
            if (ch != TT_EOF && this.getOrDefault(this.lookup, (char) ch, TT_WORD) == TT_WORD) {
                while (ch != TT_EOF && this.getOrDefault(this.lookup, (char) ch, TT_WORD) == TT_WORD) {
                    ch = this.read();
                }
                if (ch != TT_EOF) {
                    this.unread();
                }
                this.ttype = TT_WORD;
                this.tstart = start;
                this.tend = this.pos;
                this.sval = this.input.subSequence(start, this.pos).toString();
                return this.ttype;
            }

            this.ttype = ch; // special character
            this.sval = ch == TT_EOF ? "<EOF>" : String.valueOf((char) ch);
            return this.ttype;
        }
    }

    /**
     * Causes the next call to the {@code nextToken} method of this
     * tokenizer to return the current value.
     */
    public void pushBack() {
        this.pushedBack = true;
    }

    /**
     * Reads the next character from input.
     *
     * @return the next character or null in case of EOF
     */
    private int read() {
        if (this.pos < this.input.length()) {
            int ch = this.input.charAt(this.pos);
            this.pos = this.pos + 1;
            return ch;
        } else {
            this.pos = this.input.length();
            return TT_EOF;
        }
    }

    private void seekTo(@Nonnull String str) {
        int i = this.input.indexOf(str, this.pos);
        pos = (i == -1) ? this.input.length() : i + str.length();
    }

    /**
     * Sets the input for the tokenizer.
     *
     * @param input the input String;
     */
    public void setInput(@Nonnull String input) {
        this.input = input;
        this.pos = 0;
        this.pushedBack = false;
        this.ttype = TT_EOF;
        this.tstart = 0;
        this.tend = 0;
        this.sval = null;
    }

    /**
     * Sets the input position.
     */
    private void setPosition(int newValue) {
        this.pos = newValue;
    }

    /**
     * Sets this tokenizer to the state of that tokenizer
     * <p>
     * This should only be used for backtracking.
     * <p>
     * Note that both tokenizer share the same tokenizer
     * settings (e.g. added keywords, added comments, ...)
     * after this call.
     *
     * @param that another tokenizer
     */
    public void setTo(@Nonnull Tokenizer that) {
        this.input = that.input;
        this.pos = that.pos;
        this.pushedBack = that.pushedBack;
        this.lookup = that.lookup;
        this.ttype = that.ttype;
        this.tstart = that.tstart;
        this.tend = that.tend;
        this.sval = that.sval;
        this.nval = that.nval;
        this.keywordTree = that.keywordTree;
    }

    /**
     * Adds whitespace characters to the list of characters that the tokenizer
     * is supposed to skip.
     */
    public void skipWhitespace() {
        this.addSkip(' ');
        this.addSkip('\f');// FORM FEED
        this.addSkip('\n');// LINE FEED
        this.addSkip('\r');// CARRIAGE RETURN
        this.addSkip('\t');// CHARACTER TABULATION
        this.addSkip('\u000b');// LINE TABULATION
        this.addSkip('\u00a0');// NO-BREAK SPACE
        this.addSkip('\u2028');// LINE SEPARATOR
        this.addSkip('\u2029');// PARAGRAPH SEPARATOR
    }

    /**
     * Unreads the last character from input.
     */
    private void unread() {
        if (this.pos > 0) {
            this.pos = this.pos - 1;
        }
    }
}
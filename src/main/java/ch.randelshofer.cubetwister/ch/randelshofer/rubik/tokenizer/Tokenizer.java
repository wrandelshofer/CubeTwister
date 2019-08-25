/* @(#)Tokenizer.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * A greedy tokenizer.
 * <p>
 * By default this tokenizer parses the entire input sequence as a single word.
 * You can activate skipping of whitespaces by invoking addWhitespaceTokens().
 * You can activate tokenization of positive integer numbers, by invoking addDigitTokens().
 * You can activate tokenization of keywords, by adding keyword lookup.
 * <p>
 * Note that word parsing is greedy. If a word contains a comment-start token, then
 * the tokenizer will add the characters of the comment-start token to the word.
 * <p>
 * The tokenizer supports backtracking. That is, it can be
 * set to the state of another tokenizer. See method {@link #setTo}.
 */
public class Tokenizer {
    public final static int TT_WORD = -2;
    public final static int TT_EOF = -1;

    // the following ttypes can be activated on demand
    public final static int TT_KEYWORD = -4;
    public final static int TT_NUMBER = -5;

    // the following ttypes are used internally
    private final static int TT_DIGIT = -11;
    private final static int TT_SPECIAL = -12;
    private final static int TT_SKIP = -13;


    /**
     * Map<Character,TType> maps char to ttype or to null
     */
    private Map<Character, Integer> lookup = new HashMap<>();

    private String input = "";

    private int pos = 0;
    private boolean pushedBack = false;
    private int ttype = TT_EOF;
    private int tstart = 0;
    private int tend = 0;
    private String sval = null;
    private Integer nval = null;
    private KeywordTree keywordTree = new KeywordTree();

    public Tokenizer() {
    }

    /**
     * Defines a comment begin and end token.
     */
    public void addComment(String start, String end) {
        KeywordTree node = addKeywordRecursively(start);
        node.setKeyword(start);
        node.setCommentEnd(end);
    }

    /**
     * Adds a digit character.
     */
    private void addDigit(char ch) {
        this.lookup.put(ch, TT_DIGIT);
    }

    /**
     * Defines a keyword token.
     *
     * @param token the keyword token
     */
    public void addKeyword(String token) {
        KeywordTree node = addKeywordRecursively(token);
        node.setKeyword(token);
    }

    private KeywordTree addKeywordRecursively(String token) {
        KeywordTree node = this.keywordTree;
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            KeywordTree child = node.getChild(ch);
            if (child == null) {
                child = new KeywordTree();
                node.putChild(ch, child);
            }
            node = child;
        }
        return node;
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
     * Adds a skip character.
     */
    private void addSkip(char ch) {
        this.lookup.put(ch, TT_SKIP);
    }

    /**
     * Adds a special character.
     */
    private void addSpecial(char ch) {
        this.lookup.put(ch, TT_SPECIAL);
    }

    /**
     * Returns the end position of the current token.
     */
    public int getEndPosition() {
        return this.tend;
    }

    public int getInputLength() {
        return this.input.length();
    }

    /**
     * Returns the current token numeric value.
     *
     * @return Integer value or null
     */
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
            while (ch != TT_EOF && this.lookup.getOrDefault((char) ch, TT_WORD) == TT_SKIP) {
                ch = this.read();
                start += 1;
            }

            // try to tokenize a keyword or a comment
            KeywordTree node = this.keywordTree;
            KeywordTree foundNode = null;
            int end = start;
            while (ch != TT_EOF && node.getChild((char) ch) != null) {
                node = node.getChild((char) ch);
                if (node.getKeyword() != null) {
                    foundNode = node;
                    end = this.pos;
                }
                ch = this.read();
            }
            if (foundNode != null) {
                String commentEnd = foundNode.getCommentEnd();
                if (commentEnd != null) {
                    seekTo(commentEnd);
                    continue loop;
                }

                this.setPosition(end);
                this.ttype = TT_KEYWORD;
                this.tstart = start;
                this.tend = end;
                this.sval = foundNode.getKeyword();
                return this.ttype;
            }
            this.setPosition(start);
            ch = this.read();

            // try to tokenize a number
            if (ch != TT_EOF && this.lookup.getOrDefault((char) ch, TT_WORD) == TT_DIGIT) {
                while (ch != TT_EOF && this.lookup.getOrDefault((char) ch, TT_WORD) == TT_DIGIT) {
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
            if (ch != TT_EOF && this.lookup.getOrDefault((char) ch, TT_WORD) == TT_WORD) {
                while (ch != TT_EOF && this.lookup.getOrDefault((char) ch, TT_WORD) == TT_WORD) {
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

    private void seekTo(String str) {
        int i = this.input.indexOf(str, this.pos);
        pos = (i == -1) ? this.input.length() : i + str.length();
    }

    /**
     * Sets the input for the tokenizer.
     *
     * @param input the input String;
     */
    public void setInput(String input) {
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
    public void setTo(Tokenizer that) {
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
     * Defines the lookup needed for skipping whitespace.
     */
    public void skipWhitespace() {
        this.addSkip(' ');
        this.addSkip('\f');
        this.addSkip('\n');
        this.addSkip('\r');
        this.addSkip('\t');
        this.addSkip('\u00a0');
        this.addSkip('\u2028');
        this.addSkip('\u2029');
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


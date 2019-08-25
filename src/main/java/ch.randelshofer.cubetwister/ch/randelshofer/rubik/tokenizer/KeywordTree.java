/* @(#)KeywordTree.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A node of a keyword tree.
 * <p>
 * Example tree structure, for the keywords "ab", and "abcd".
 * <pre>
 * ''.KeywordTree(null)
 * ''.'a'.KeywordTree(null)
 * ''.'a'.'b'.KeywordTree("ab")
 * ''.'a'.'b'.'c'.KeywordTree(null)
 * ''.'a'.'b'.'c'.'d'.KeywordTree("abcd")
 * </pre>
 */
class KeywordTree {
    private String keyword;
    private String commentEnd;
    private final Map<Character,KeywordTree> children=new LinkedHashMap<>();

    KeywordTree() {
    }

    KeywordTree getChild(char ch) {
        return this.children.get(ch);
    }

    void putChild(char ch, KeywordTree child) {
        this.children.put(ch, child);
    }

    void setKeyword(String value) {
         this.keyword=value;
    }

    String getKeyword() {
        return this.keyword;
    }

    void setCommentEnd(String value) {
        this.commentEnd = value;
    }

    String getCommentEnd() {
        return this.commentEnd;
    }
}

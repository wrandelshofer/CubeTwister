/* @(#)KeywordNode.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A node of a keyword tree.
 * <p>
 * Example tree structure, for the keywords "ab", and "abcd".
 * <pre>
 * ''.KeywordNode{keyword=null}
 * ''.'a'.KeywordNode{keyword=null}
 * ''.'a'.'b'.KeywordNode{keyword=n"ab"}
 * ''.'a'.'b'.'c'.KeywordNode{keyword=null}
 * ''.'a'.'b'.'c'.'d'.KeywordNode{keyword="abcd"}
 * </pre>
 */
class KeywordNode {
    /**
     * The keyword.
     * This value is non-null if the node represents a keyword.
     * The value is null if the node is an intermediate node in the tree.
     */
    @Nullable
    private String keyword;
    /**
     * The character sequence that ends a comment.
     * This value is non-null if the node represents a keyword that starts
     * a comment.
     */
    @Nullable
    private String commentEnd;

    /**
     * The children map. The key of the map is the character that leads
     * from this tree node down to the next.
     */
    @Nonnull
    private final Map<Character, KeywordNode> children = new LinkedHashMap<>();

    KeywordNode() {
    }

    @Nullable
    KeywordNode getChild(char ch) {
        return this.children.get(ch);
    }

    void putChild(char ch, @Nonnull KeywordNode child) {
        this.children.put(ch, child);
    }

    void setKeyword(@Nonnull String value) {
        this.keyword = value;
    }

    @Nullable
    String getKeyword() {
        return this.keyword;
    }

    void setCommentEnd(@Nonnull String value) {
        this.commentEnd = value;
    }

    @Nullable
    String getCommentEnd() {
        return this.commentEnd;
    }
}

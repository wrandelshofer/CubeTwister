/*
 * @(#)KeywordNode.java
 * Copyright © 2025 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import ch.randelshofer.util.BreadthFirstIterator;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A node of keyword tree.
 * <p>
 * A node contains two fields: {@code startSeq} and {@code endSeq}.
 * <p>
 * Example tree structure, for the character sequences "ab", and "abcd".
 * <pre>
 * ''.KeywordNode{startseq=null}
 * ''.'a'.KeywordNode{startseq=null}
 * ''.'a'.'b'.KeywordNode{startseq="ab"}
 * ''.'a'.'b'.'c'.KeywordNode{startseq=null}
 * ''.'a'.'b'.'c'.'d'.KeywordNode{startseq="abcd"}
 * </pre>
 * If the field {@code endseq} is non-null, then the node represents
 * a character sequence that starts with {@code startseq} and ends at the
 * first occurrence of {@code endseq}. This is equivalent to the regular
 * expression {@code startseq.*?endseq }. This is useful for parsing
 * comments. Below is an example for a line comment of the form
 * {@code // foo \n}.
 * <pre>
 * ''.KeywordNode{startseq=null}
 * ''.'/'.KeywordNode{startseq=null}
 * ''.'/'.'/'.KeywordNode{startseq="//",endseq="\n"}
 * </pre>
 */
public class KeywordNode {
    /**
     * The character sequence that starts the keyword.
     * This value is non-null if the node represents a keyword.
     * The value is null if the node is an intermediate node in the tree.
     */
    @Nullable
    private String startSeq;
    /**
     * The character sequence that ends the keyword.
     * This value is non-null if the node represents a character sequence
     * that starts with the {@code startseq} sequence and ends at the first
     * occurrence of the {@code endseq} sequence.
     */
    @Nullable
    private String endSeq;

    /**
     * The children map. The key of the map is the character that leads
     * from this tree node down to the next.
     */
    @Nonnull
    private final Map<Character, KeywordNode> children = new LinkedHashMap<>();

    public KeywordNode() {
    }

    @Nullable
    public KeywordNode getChild(char ch) {
        return this.children.get(ch);
    }

    void putChild(char ch, @Nonnull KeywordNode child) {
        this.children.put(ch, child);
    }

    void setStartSeq(@Nonnull String value) {
        this.startSeq = value;
    }

    @Nullable
    public String getStartSeq() {
        return this.startSeq;
    }

    public void setEndSeq(@Nonnull String value) {
        this.endSeq = value;
    }

    @Nullable
    public String getEndSeq() {
        return this.endSeq;
    }


    /**
     * Adds a startseq.
     *
     * @param root    the root of the startseq tree
     * @param startseq the new startseq
     * @return the KeywordNode that contains the last character of the startseq
     */
    public static KeywordNode addCharacterSequence(@Nonnull KeywordNode root, @Nonnull String startseq) {
        var node = root;
        for (int i = 0; i < startseq.length(); i++) {
            char ch = startseq.charAt(i);
            var child = node.getChild(ch);
            if (child == null) {
                child = new KeywordNode();
                node.putChild(ch, child);
            }
            node = child;
        }
        node.setStartSeq(startseq);
        return node;
    }

    /**
     * Adds a character sequence which is defined by a start character sequence
     * and an end character sequence.
     *
     * @param root     the root of the startseq tree
     * @param startseq the start sequence
     * @param endseq   the end sequence
     * @return the KeywordNode that contains the last character of the startseq
     */
    public static KeywordNode addStartEndSequence(@Nonnull KeywordNode root, @Nonnull String startseq, @Nonnull String endseq) {
        KeywordNode charSeqNode = addCharacterSequence(root, startseq);
        charSeqNode.endSeq = endseq;
        return charSeqNode;
    }

    public Collection<KeywordNode> getChildren() {
        return children.values();
    }

    @Nonnull
    public List<String> getCharacterSequences() {
        List<String> list = new ArrayList<>();
        for (KeywordNode node : BreadthFirstIterator.iterable(n -> n.children.values(), this)) {
            if (node.startSeq != null && node.endSeq == null) {
                list.add(node.startSeq);
            }
        }
        return list;
    }

    @Nonnull
    public List<Map.Entry<String, String>> getStartEndSequences() {
        List<Map.Entry<String, String>> list = new ArrayList<>();
        for (KeywordNode node : BreadthFirstIterator.iterable(n -> n.children.values(), this)) {
            if (node.startSeq != null && node.endSeq != null) {
                list.add(new AbstractMap.SimpleImmutableEntry<>(node.startSeq, node.endSeq));
            }
        }
        return list;
    }
}

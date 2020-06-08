/*
 * @(#)CharSeqNode.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
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
 * A node of a character sequences tree.
 * <p>
 * A node contains two fields: {@code charseq} and {@code endseq}.
 * <p>
 * Example tree structure, for the character sequences "ab", and "abcd".
 * <pre>
 * ''.CharSeqNode{charseq=null}
 * ''.'a'.CharSeqNode{charseq=null}
 * ''.'a'.'b'.CharSeqNode{charseq="ab"}
 * ''.'a'.'b'.'c'.CharSeqNode{charseq=null}
 * ''.'a'.'b'.'c'.'d'.CharSeqNode{charseq="abcd"}
 * </pre>
 * If the field {@code endseq} is non-null, then the node represents
 * a character sequence that starts with {@code charseq} and ends at the
 * first occurrence of {@code endseq}. This is equivalent to the regular
 * expression {@code startseq.*?endseq }. This is useful for parsing
 * comments. Below is an example for a line comment of the form
 * {@code // foo \n}.
 * <pre>
 * ''.CharSeqNode{charseq=null}
 * ''.'/'.CharSeqNode{charseq=null}
 * ''.'/'.'/'.CharSeqNode{charseq="//",endseq="\n"}
 * </pre>
 */
public class CharSeqNode {
    /**
     * The character sequence.
     * This value is non-null if the node represents a character sequence.
     * The value is null if the node is an intermediate node in the tree.
     */
    @Nullable
    private String charseq;
    /**
     * The character sequence that ends the character sequence.
     * This value is non-null if the node represents a character sequence
     * that starts with the {@code charseq} sequence and and ends at the first
     * occurrence ofthe {@code endseq} sequence.
     */
    @Nullable
    private String endseq;

    /**
     * The children map. The key of the map is the character that leads
     * from this tree node down to the next.
     */
    @Nonnull
    private final Map<Character, CharSeqNode> children = new LinkedHashMap<>();

    public CharSeqNode() {
    }

    @Nullable
    public CharSeqNode getChild(char ch) {
        return this.children.get(ch);
    }

    void putChild(char ch, @Nonnull CharSeqNode child) {
        this.children.put(ch, child);
    }

    void setCharseq(@Nonnull String value) {
        this.charseq = value;
    }

    @Nullable
    public String getCharseq() {
        return this.charseq;
    }

    public void setEndseq(@Nonnull String value) {
        this.endseq = value;
    }

    @Nullable
    public String getEndseq() {
        return this.endseq;
    }


    /**
     * Adds a charseq.
     *
     * @param root    the root of the charseq tree
     * @param charseq the new charseq
     * @return the KeywordNode that contains the last character of the charseq
     */
    public static CharSeqNode addCharacterSequence(@Nonnull CharSeqNode root, @Nonnull String charseq) {
        var node = root;
        for (int i = 0; i < charseq.length(); i++) {
            char ch = charseq.charAt(i);
            var child = node.getChild(ch);
            if (child == null) {
                child = new CharSeqNode();
                node.putChild(ch, child);
            }
            node = child;
        }
        node.setCharseq(charseq);
        return node;
    }

    /**
     * Adds a character sequence which is defined by a start character sequence
     * and an end character sequence.
     *
     * @param root     the root of the charseq tree
     * @param startseq the start sequence
     * @param endseq   the end sequence
     * @return the KeywordNode that contains the last character of the charseq
     */
    public static CharSeqNode addStartEndSequence(@Nonnull CharSeqNode root, @Nonnull String startseq, @Nonnull String endseq) {
        CharSeqNode charSeqNode = addCharacterSequence(root, startseq);
        charSeqNode.endseq = endseq;
        return charSeqNode;
    }

    public Collection<CharSeqNode> getChildren() {
        return children.values();
    }

    @Nonnull
    public List<String> getCharacterSequences() {
        List<String> list = new ArrayList<>();
        for (CharSeqNode node : BreadthFirstIterator.iterable(n -> n.children.values(), this)) {
            if (node.charseq != null && node.endseq == null) {
                list.add(node.charseq);
            }
        }
        return list;
    }

    @Nonnull
    public List<Map.Entry<String, String>> getStartEndSequences() {
        List<Map.Entry<String, String>> list = new ArrayList<>();
        for (CharSeqNode node : BreadthFirstIterator.iterable(n -> n.children.values(), this)) {
            if (node.charseq != null && node.endseq != null) {
                list.add(new AbstractMap.SimpleImmutableEntry<>(node.charseq, node.endseq));
            }
        }
        return list;
    }
}

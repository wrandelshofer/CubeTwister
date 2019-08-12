/* @(#)KeywordTree.java
 * Copyright (c) 2018 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.tokenizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    private String commendEnd;
    private final Map<Character,KeywordTree> children=new LinkedHashMap<>();
    /**
     * Constructos a new instance.
     *
     * @param keyword a keyword
     */
     KeywordTree(String keyword) {
        this.keyword = keyword;
    }

     KeywordTree getChild(char ch) {
        return children.get(ch);
    }
     void putChild(char ch, KeywordTree child) {
        children.put(ch,child);
    }

    public void setKeyword(String value) {
         this.keyword=value;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setCommentEnd(String value) {
        this.commendEnd=value;
    }

    public String getCommentEnd() {
        return commendEnd;
    }
}

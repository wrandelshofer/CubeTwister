/* @(#)CommentNode.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

/**
 * A CommentNode holds descriptive text.
 * CommentNode's have no side effects on a Cube.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class CommentNode extends Node {
        private final static long serialVersionUID = 1L;

    /** Creates a new instance. */
    public CommentNode(int layerCount) {
        this(layerCount, -1,-1);
    }
    /** Creates a new instance. */
    public CommentNode(int layerCount, int startpos, int endpos) {
        super(Symbol.COMMENT, layerCount, startpos, endpos);
        setAllowsChildren(false);
    }
}

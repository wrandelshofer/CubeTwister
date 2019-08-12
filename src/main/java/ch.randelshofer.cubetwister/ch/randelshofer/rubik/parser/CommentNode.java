/* @(#)CommentNode.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.notation.Symbol;

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
    public CommentNode() {
        this(-1,-1);
    }
    /** Creates a new instance. */
    public CommentNode(int startpos, int endpos) {
        super(Symbol.COMMENT, startpos, endpos);
        setAllowsChildren(false);
    }
}

/* @(#)TreeModels.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.tree;

import ch.randelshofer.gui.datatransfer.*;
import java.awt.datatransfer.*;
import java.util.*;
import javax.swing.tree.*;
import java.io.*;

/**
 * TreeModels.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.0 November 1, 2003 Created.
 */
public class TreeModels {

    /** Prevent instance creation. */
    private TreeModels() {
    }
    /**
     * Creates a transferable in a number of default formats for a ListModel.
     *
     * @return A transferable for a list model.
     */
    public static Transferable createDefaultTransferable(TreeModel model, MutableTreeNode[] nodes) {
        CompositeTransferable t = new CompositeTransferable();
        t.add(createLocalTransferable(model, nodes, Object.class));
        t.add(createHTMLTransferable(model, nodes));
        t.add(createPlainTransferable(model, nodes));
        return t;
    }

    /**
     * Creates a transferable in text/html format from
     * a mutable tree model.
     *
     * @return A transferable of type text/html
     */
    public static Transferable createHTMLTransferable(TreeModel model, MutableTreeNode[] nodes) {
        try {
            CharArrayWriter w = new CharArrayWriter();
            w.write("<html><body><ul>");
            for (int i = 0; i < nodes.length; i++) {
                writeHTML(w, nodes[i]);
            }
            w.write("</ul></body></html>");
            w.close();
            return new DefaultTransferable(w.toCharArray(), "text/html", "HTML");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeHTML(CharArrayWriter w, MutableTreeNode node) throws IOException {
        w.write("<li>");
        writeHTMLEncoded(w, node.toString());
        if (!node.isLeaf()) {
            for (int i = 0,  n = node.getChildCount(); i < n; i++) {
                w.write("<ul>");
                writeHTML(w, (MutableTreeNode) node.getChildAt(i));
                w.write("</ul>");
            }
        }
        w.write("</li>");
    }

    private static void writeHTMLEncoded(Writer w, String str) throws IOException {
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '&':
                    w.write("&amp;");
                    break;
                case '<':
                    w.write("&lt;");
                    break;
                case '>':
                    w.write("&gt;");
                    break;
                default:
                    w.write(ch);
                    break;
            }
        }
    }

    /**
     * Creates a transferable in text/plain format from
     * a mutable tree model.
     *
     * @return A transferable of type java.awt.datatransfer.StringSelection
     */
    public static Transferable createPlainTransferable(TreeModel model, MutableTreeNode[] nodes) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            Object elem = ((DefaultMutableTreeNode) nodes[i]).getUserObject();

            if (i != 0) {
                buf.append('\n');
            }
            buf.append(elem.toString());
        }
        return new StringSelection(buf.toString());
    }

    /**
     * Creates a local JVM transferable from
     * a mutable tree model.
     *
     * @return A JVM local object transferable of type java.util.LinkedList if
     * nodes.length &gt; 1. A JVM local object transferable of type
     * model.getElementAt(nodes[0]).getClass() if nodes.length = 1.
     */
    public static Transferable createLocalTransferable(TreeModel model, MutableTreeNode[] nodes, Class<?> baseclass) {
        LinkedList<Object> l = new LinkedList<Object>();
        for (int i = 0; i < nodes.length; i++) {
            Object elem = ((DefaultMutableTreeNode) nodes[i]).getUserObject();
            l.add(nodes[i]);
        }
        return new JVMLocalObjectTransferable(List.class, l);
    }

    /**
     * Removes all descendants from a node array.
     * <p>
     * A node is removed from the array when it is a descendant from
     * another node in the array.
     */
    public static MutableTreeNode[] removeDescendantsFromNodeArray(MutableTreeNode[] nodes) {
        int i, j;
        TreePath[] paths = new TreePath[nodes.length];
        for (i = 0; i < nodes.length; i++) {
            paths[i] = new TreePath(getPathToRoot(nodes[i]));
        }

        int removeCount = 0;
        for (i = 0; i < paths.length; i++) {
            for (j = 0; j < paths.length; j++) {
                if (i != j && paths[j] != null) {
                    if (paths[j].isDescendant(paths[i])) {
                        paths[i] = null;
                        removeCount++;
                        break;
                    }
                }
            }
        }

        MutableTreeNode[] result = new MutableTreeNode[nodes.length - removeCount];
        j = 0;
        for (i = 0; i < paths.length; i++) {
            if (paths[i] != null) {
                result[j++] = nodes[i];
            }
        }
        return result;
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode the TreeNode to get the path for
     * @return an array of TreeNodes giving the path from the root to the
     *        specified node. 
     */
    public static TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node 
     */
    public static TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[] retNodes;
        // This method recurses, traversing towards the root in order
        // size the array. On the way back, it fills in the nodes,
        // starting from the root and working back to the original node.

        /* Check for null, in case someone passed in a null node, or
        they passed in an element that isn't rooted at root. */
        if (aNode == null) {
            if (depth == 0) {
                return null;
            } else {
                retNodes = new TreeNode[depth];
            }
        } else {
            depth++;
            if (aNode.getParent() == null) {
                retNodes = new TreeNode[depth];
            } else {
                retNodes = getPathToRoot(aNode.getParent(), depth);
            }
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
}

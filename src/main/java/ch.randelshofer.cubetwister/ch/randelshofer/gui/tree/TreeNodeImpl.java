/* @(#)MutableTreeNodeEx.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.tree;

import ch.randelshofer.util.EnumerationIterator;
import org.jhotdraw.annotation.Nonnull;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Extends {@code DefaultMutableTreeNode} with type safe collections.
 * This class only allows nodes of the specified type T.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TreeNodeImpl<T extends TreeNodeImpl<T>> extends DefaultMutableTreeNode {
    private final static long serialVersionUID = 1L;

    public TreeNodeImpl(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public TreeNodeImpl(Object userObject) {
        super(userObject);
    }

    public TreeNodeImpl() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(MutableTreeNode newChild) {
        super.add((T) newChild);
    }


    public Iterable<T> breadthFirstIterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                @SuppressWarnings("unchecked") Enumeration<T> treeNodeEnumeration = (Enumeration<T>) (Enumeration<?>) breadthFirstEnumeration();
                return new EnumerationIterator<T>(treeNodeEnumeration);
            }

        };
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public List<T> getChildren() {
        if (children == null) {
            children = new Vector<>();
        }
        @SuppressWarnings("unchecked") List<T> treeNodeList = (List<T>) (List<?>) children;
        return Collections.unmodifiableList(treeNodeList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T clone() {
        return (T) super.clone();
    }

    public Iterable<T> depthFirstIterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                @SuppressWarnings("unchecked") Enumeration<T> treeNodeEnumeration = (Enumeration<T>) (Enumeration<?>) depthFirstEnumeration();
                return new EnumerationIterator<T>(treeNodeEnumeration);
            }

        };
    }

    @Override
    public boolean getAllowsChildren() {
        return super.getAllowsChildren();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getChildAfter(TreeNode aChild) {
        return (T) super.getChildAfter(aChild);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getChildAt(int index) {
        return (T) super.getChildAt(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getChildBefore(TreeNode aChild) {
        return (T) super.getChildBefore(aChild);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getFirstChild() {
        return (T) super.getFirstChild();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getFirstLeaf() {
        return (T) super.getFirstLeaf();
    }

    @Override
    public int getIndex(TreeNode aChild) {
        return super.getIndex(aChild);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getLastChild() {
        return (T) super.getLastChild();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getLastLeaf() {
        return (T) super.getLastLeaf();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNextLeaf() {
        return (T) super.getNextLeaf();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNextNode() {
        return (T) super.getNextNode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNextSibling() {
        return (T) super.getNextSibling();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getParent() {
        return (T) super.getParent();
    }

    @Override
    public TreeNode[] getPath() {
        return getPathToRootEx(this, 0);
    }

    protected TreeNode[] getPathToRootEx(TreeNodeImpl<T> aNode, int depth) {
        TreeNode[] retNodes;

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
            retNodes = getPathToRootEx(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getPreviousLeaf() {
        return (T) super.getPreviousLeaf();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getPreviousNode() {
        return (T) super.getPreviousNode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getPreviousSibling() {
        return (T) super.getPreviousSibling();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getRoot() {
        return (T) super.getRoot();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSharedAncestor(DefaultMutableTreeNode aNode) {
        return (T) super.getSharedAncestor(aNode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert((T) newChild, childIndex);
    }

    public Iterable<T> postorderIterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                @SuppressWarnings("unchecked")
                Enumeration<T> treeNodeEnumeration = (Enumeration<T>) (Enumeration<?>) postorderEnumeration();
                return new EnumerationIterator<T>(treeNodeEnumeration);
            }

        };
    }

    public Iterable<T> preorderIterable() {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                @SuppressWarnings("unchecked")
                Enumeration<T> treeNodeEnumeration = (Enumeration<T>) (Enumeration<?>) preorderEnumeration();
                return new EnumerationIterator<T>(treeNodeEnumeration);

            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setParent(MutableTreeNode newParent) {
        super.setParent((T) newParent);
    }
}

/* @(#)MutableTreeNodeEx.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.tree;

import ch.randelshofer.util.EnumerationIterator;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Extends {@code DefaultMutableTreeNode} with type safe collections.
 * This class only allows nodes of the specified type T.
 *
 * @author Werner Randelshofer
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


    @Nonnull
    public Iterable<T> breadthFirstIterable() {
        return new Iterable<T>() {

            @Nonnull
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

    @Nonnull
    public Iterable<T> depthFirstIterable() {
        return new Iterable<T>() {

            @Nonnull
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

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getChildAfter(@Nonnull TreeNode aChild) {
        return (T) super.getChildAfter(aChild);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getChildAt(int index) {
        return (T) super.getChildAt(index);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getChildBefore(@Nonnull TreeNode aChild) {
        return (T) super.getChildBefore(aChild);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getFirstChild() {
        return (T) super.getFirstChild();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getFirstLeaf() {
        return (T) super.getFirstLeaf();
    }

    @Override
    public int getIndex(@Nonnull TreeNode aChild) {
        return super.getIndex(aChild);
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getLastChild() {
        return (T) super.getLastChild();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getLastLeaf() {
        return (T) super.getLastLeaf();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getNextLeaf() {
        return (T) super.getNextLeaf();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getNextNode() {
        return (T) super.getNextNode();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getNextSibling() {
        return (T) super.getNextSibling();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getParent() {
        return (T) super.getParent();
    }

    @Nullable
    @Override
    public TreeNode[] getPath() {
        return getPathToRootEx(this, 0);
    }

    @Nullable
    protected TreeNode[] getPathToRootEx(@Nullable TreeNodeImpl<T> aNode, int depth) {
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

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getPreviousLeaf() {
        return (T) super.getPreviousLeaf();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getPreviousNode() {
        return (T) super.getPreviousNode();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getPreviousSibling() {
        return (T) super.getPreviousSibling();
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T getRoot() {
        return (T) super.getRoot();
    }

    @Nonnull
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

    @Nonnull
    public Iterable<T> postorderIterable() {
        return new Iterable<T>() {

            @Nonnull
            @Override
            public Iterator<T> iterator() {
                @SuppressWarnings("unchecked")
                Enumeration<T> treeNodeEnumeration = (Enumeration<T>) (Enumeration<?>) postorderEnumeration();
                return new EnumerationIterator<T>(treeNodeEnumeration);
            }

        };
    }

    @Nonnull
    public Iterable<T> preorderIterable() {
        return new Iterable<T>() {

            @Nonnull
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

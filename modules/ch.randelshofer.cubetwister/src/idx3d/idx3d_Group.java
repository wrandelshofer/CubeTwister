package idx3d;

/* @(#)idx3d_Group.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */
import java.util.*;

/**
 * The idx3d_Group node object is a general-purpose grouping node. Group nodes
 * have exactly one parent and an arbitrary number of children. Operations on
 * idx3d_Group node objects include adding, removing and enumerating the
 * children of the idx3d_Group node. The subclasses of idx3d_Group node add
 * additional semantics.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class idx3d_Group extends idx3d_Node {

    private ArrayList<idx3d_Node> children = new ArrayList<idx3d_Node>();

    /**
     * Creates a new instance.
     */
    public idx3d_Group() {
    }

    /**
     * Returns a count of the number of children.
     */
    public final int getChildCount() {
        return children.size();
    }

    /**
     * Returns the child at the specified index.
     */
    public final idx3d_Node getChild(int index) {
        return children.get(index);
    }

    /**
     * Replace the child at the specified index.
     */
    public final void setChild(idx3d_Node child, int index) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        children.set(index, child);
        child.parent = this;
    }

    /**
     * Insert a new child before the specified index.
     */
    public final void insertChild(idx3d_Node child, int index) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        children.add(index, child);
        child.parent = this;
    }

    /**
     * Remove the child at the specified index.
     */
    public final void removeChild(int index) {
        idx3d_Node child = getChild(index);
        children.remove(index);
        child.parent = null;
    }

    /**
     * Returns the specified child.
     */
    public final void removeChild(idx3d_Node child) {
        if (child.parent == this) {
            children.remove(child);
            child.parent = null;
        }
    }

    /**
     * Return an enumeration of all children.
     */
    @Override
    public final Iterable<idx3d_Node> children() {
        return children;
    }

    /**
     * Remove all children.
     */
    public void removeAllChildren() {
        for (int i = 0, n = children.size(); i < n; i++) {
            idx3d_Node child = children.get(i);
            child.parent = null;
        }
        children.clear();
    }

    /**
     * Add a new child as the last child in the group.
     */
    public final void addChild(idx3d_Node child) {
        if (child.parent != null) {
            child.parent.removeChild(child);
        }
        children.add(child);
        child.parent = this;
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in preorder. The first node returned by the enumeration's
     * <code>nextElement()</code> method is this node.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @return	an enumeration for traversing the tree in preorder
     */
    public Iterable<idx3d_Node> preorderIterator() {
        return new Iterable<idx3d_Node>() {

            @Override
            public Iterator<idx3d_Node> iterator() {
                return new PreorderIterator(idx3d_Group.this);
            }
        };
    }

    /**
     * Validates this node and all its children.
     */
    @Override
    public void validate() {
        if (!isValid()) {
            super.validate();
            for (int i = 0, n = children.size(); i < n; i++) {
                idx3d_Node child = children.get(i);
                child.validate();

            }
        }
    }

    private final static class PreorderIterator implements Iterator<idx3d_Node> {

        protected Deque<Iterator<idx3d_Node>> stack;

        public PreorderIterator(idx3d_Node rootNode) {
            super();
            ArrayList<idx3d_Node> v = new ArrayList<idx3d_Node>(1);
            v.add(rootNode);	// PENDING: don't really need a vector
            stack = new ArrayDeque<Iterator<idx3d_Node>>();
            stack.push(v.iterator());
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty()
                    && stack.peek().hasNext();
        }

        @Override
        public idx3d_Node next() {
            Iterator<idx3d_Node> enumer = stack.peek();
            idx3d_Node node = enumer.next();
            Iterator<idx3d_Node> children = node.children().iterator();

            if (!enumer.hasNext()) {
                stack.pop();
            }
            if (children.hasNext()) {
                stack.push(children);
            }
            return node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }  // End of class PreorderEnumeration
}

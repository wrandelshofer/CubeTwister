/*
 * @(#)DefaultMutableTreeModel.java  3.1  2011-01-22
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui.tree;

import ch.randelshofer.gui.datatransfer.*;
import ch.randelshofer.gui.list.ListModels;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.*;
import java.util.*;
import java.io.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;
import org.jhotdraw.beans.WeakPropertyChangeListener;

/**
 * A simple mutable tree model.
 *
 * FIXME: Should override more methods in the superclass.
 *
 * @author Werner Randelshofer
 * @version 3.1 2011-01-22 Methods insertAllInto and importTransferable return the tree paths
 * of the inserted nodes. Adds undo support. Adds method insertNodesInto.
 * <br>3.0.1 2011-01-20 Method removeNodeFromParent was not properly overriden.
 * <br>3.0 2008-03-21 Rewrote default implementation of importTransferable.
 * <br>2.3 2002-04-08 Support for transferables added.
 * <br>2.0 2001-07-18
 */
public class DefaultMutableTreeModel
        extends DefaultTreeModel
        implements MutableTreeModel {
    private final static long serialVersionUID = 1L;

    protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    protected Object[] childTypes = new Object[]{"Leaf", "Folder"};
    private boolean enabled = true;
    private boolean editable = true;
    public final static DataFlavor listFlavor = new DataFlavor(List.class, "List");
    public final static DataFlavor objectFlavor = new DataFlavor(Object.class, "Local VM Object");
    /**
     * Supported Flavors for data import.
     *
     * Note: Implementation of method importData depends on
     * the contents of this array.
     */
    private final static DataFlavor[] supportedFlavors = {
        listFlavor,
        objectFlavor,
        DataFlavor.getTextPlainUnicodeFlavor()
    };

    /**
     * Constructs a new DefaultMutableTreeModel using a
     * DefaultMutableTreeNode as root of the tree.
     */
    public DefaultMutableTreeModel() {
        this(new DefaultMutableTreeNode());
    }

    /**
     * Constructs a new DefaultMutableTreeModel using the given tree node
     * as the root of the tree.
     */
    public DefaultMutableTreeModel(TreeNode root) {
        super(root, true);
        this.root = root;
    }

    /**
     * Invoke this to insert a new child at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create the
     * appropriate event.
     *
     * @param   type       the type of the new child to be created.
     * @param   parent     a node from the tree, obtained from this data source.
     * @param   index      index of the child.
     * @exception   IllegalStateException if the parent node does not allow children.
     */
    @Override
    public TreePath createNodeAt(Object type, MutableTreeNode parent, int index) throws IllegalStateException {
        // determine if the new node may be inserted
        int i;
        Object[] allowedTypes = getCreatableNodeTypes(parent);
        for (i = 0; i < allowedTypes.length; i++) {
            if (type.equals(allowedTypes[i])) {
                break;
            }
        }
        if (i == allowedTypes.length) {
            throw new IllegalStateException("Can't insert node.");
        }

        // insert the node
        DefaultMutableTreeNode newChild = new DefaultMutableTreeNode("unnamed");
        newChild.setAllowsChildren("Folder".equals(type));
        insertNodeInto(newChild, parent, index);

        return new TreePath(newChild.getPath());
    }

    @Override
    public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index) {
        if (!isNodeAddable((DefaultMutableTreeNode) parent, index)) {
            throw new IllegalStateException("Cannot insert node");
        }
        super.insertNodeInto(newChild, parent, index);
    }

    /**
     * Returns the type of children that may be created at
     * this node.
     */
    @Override
    public Object[] getCreatableNodeTypes(Object node) {
        return (!isEnabled() || isLeaf(node)) ? new Object[]{} : childTypes;
    }

    /**
     * Returns the type of children that may be created at
     * this node.
     */
    @Override
    public Object getCreatableNodeType(Object node) {
        return (!isEnabled() || isLeaf(node)) ? null : childTypes[0];
    }

    /**
     * Gets the editable state of the model.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Gets the enabled state of the model.
     */
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isNodeAddable(MutableTreeNode parent, int index) {
        return isEnabled() && isEditable();
    }

    /**
     * Returns wether the specified node may be removed.
     *
     * @param   node   a node from the tree, obtained from this data source.
     * @return  Returns true for all nodes except for the root.
     *          Returns false if the model is disabled.
     */
    @Override
    public boolean isNodeRemovable(MutableTreeNode node) {
        return isEnabled() && isEditable() && getRoot() != node;
    }

    /**
     * Returns wether the specified node may be renamed.
     *
     * @param   node   a node from the tree, obtained from this data source.
     * @return  Returns true for all nodes except.
     *          Returns false if the model is disabled.
     */
    @Override
    public boolean isNodeEditable(MutableTreeNode node) {
        return isEnabled() && isEditable();
    }

    /**
     * Removes a child from its parent.
     *
     * @param   node   a node from the tree, obtained from this data source.
     */
    @Override
    public void removeNodeFromParent(MutableTreeNode node) {
        // determine if the node may be removed
        if (!isNodeRemovable(node)) {
            throw new IllegalStateException("Can't remove node.");
        }

        super.removeNodeFromParent(node);
    }

    /**
     * Sets the editable state of the model.
     *
     * A disabled tree model returns Object[]{} for getAllowedChildTypes(Object),
     * and false for isRemoveAllowed()
     *
     */
    public void setEditable(boolean b) {
        editable = b;
    }

    /**
     * Sets the enabled state of the model.
     *
     * A disabled tree model returns Object[]{} for getAllowedChildTypes(Object),
     * and false for isRemoveAllowed()
     */
    public void setEnabled(boolean newValue) {
        boolean oldValue = enabled;
        enabled = newValue;
        firePropertyChange(ENABLED_PROPERTY, oldValue, newValue);
    }

    /**
     * Sets the node types to be returned by getInsertableNodeTypes.
     */
    protected void setInsertableNodeTypes(Object[] childTypes) {
        this.childTypes = childTypes;
    }

    /**
     * Sets the root to <code>root</code>. This will throw an
     * IllegalArgumentException if <code>root</code> is null.
     */
    @Override
    public void setRoot(TreeNode aRoot) {
        if (aRoot == null) {
            throw new IllegalArgumentException("Root must not be null.");
        }
        this.root = aRoot;
        nodeStructureChanged(this.root);
    }

    /**
     * Indicates whether the model would accept an import of the
     * given set of data flavors prior to actually attempting
     * to import it.
     *
     * @return true if the data can be inserted into the component,
     * false otherwise
     */
    @Override
    public boolean isImportable(DataFlavor[] transferFlavors, int action, MutableTreeNode parent, int index) {
        if (isLeaf(parent)) {
            return false;
        }

        for (int i = 0; i < transferFlavors.length; i++) {
            if (transferFlavors[i].isMimeTypeEqual("text/plain")) {
                return true;
            }
            for (int j = 0; j < supportedFlavors.length; j++) {
                if (transferFlavors[i].equals(supportedFlavors[j])) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Transferable exportTransferable(MutableTreeNode[] nodes) {
        nodes = removeDescendantsFromNodeArray(nodes);
        CompositeTransferable t = new CompositeTransferable();
        t.add(TreeModels.createLocalTransferable(this, nodes, Object.class));
        t.add(TreeModels.createHTMLTransferable(this, nodes));
        t.add(TreeModels.createPlainTransferable(this, nodes));

        boolean hasOnlyLeafNodes = true;
        for (MutableTreeNode n : nodes) {
            if (!n.isLeaf()) {
                hasOnlyLeafNodes = false;
                break;
            }
        }

        if (hasOnlyLeafNodes) {
            LinkedList<Object> l = new LinkedList<Object>();
            for (int i = 0; i < nodes.length; i++) {
                Object elem = ((DefaultMutableTreeNode) nodes[i]).getUserObject();
                l.add(elem);
            }
            t.add(new JVMLocalObjectTransferable(List.class, l));
        }
        return t;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TreePath> importTransferable(Transferable t, int action, MutableTreeNode parent, int index)
            throws UnsupportedFlavorException, IOException {
        if (!isImportable(t.getTransferDataFlavors(), action, parent, index)) {
            return Collections.emptyList();
        }

        @SuppressWarnings("rawtypes")
        List l = null;
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
        } else if (t.isDataFlavorSupported(listFlavor)) {
            l = (List) t.getTransferData(listFlavor);
        } else if (t.isDataFlavorSupported(objectFlavor)) {
            l = new ArrayList<Object>(1);
            l.add( t.getTransferData(objectFlavor));
        } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            l = ListModels.getStringList(t);
        } else if (t.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
            l = ListModels.getPlainList(t);
        } else {
            throw new UnsupportedFlavorException(listFlavor);
        }

        if (l != null) {
            return insertAllInto(l, parent, index);
        }

        return Collections.emptyList();
    }

    public List<TreePath> insertAllInto(List<Object> l, MutableTreeNode parent, int index) {
        if (index == -1) {
            index = parent.getChildCount();
        }

        LinkedList<TreePath> insertedPaths = new LinkedList<TreePath>();
        TreePath parentPath = new TreePath(((DefaultMutableTreeNode)parent).getPath());
        for (Object userObject : l) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);
            insertNodeInto(node, parent, index++);
            insertedPaths.add(parentPath.pathByAddingChild(node));
        }
        return insertedPaths;
    }

    public List<TreePath> insertNodesInto(List<? extends DefaultMutableTreeNode> newChilds, DefaultMutableTreeNode parent, int index) {
        if (index == -1) {
            index = parent.getChildCount();
        }

        LinkedList<TreePath> insertedPaths = new LinkedList<TreePath>();
        TreePath parentPath = new TreePath(parent.getPath());
        int[] indices = new int[newChilds.size()];
        int i = 0;
        for (DefaultMutableTreeNode node : newChilds) {
            parent.insert(node, index);
            indices[i++] = index++;
            insertedPaths.add(parentPath.pathByAddingChild(node));
        }
        fireTreeNodesInserted(this, parent.getPath(), indices, newChilds.toArray(new DefaultMutableTreeNode[newChilds.size()]));
        return insertedPaths;
    }

    /**
     * Gets actions for the indicated nodes.
     *
     * @param   nodes   The nodes.
     */
    @Override
    public Action[] getNodeActions(MutableTreeNode[] nodes) {
        return new Action[0];
    }

    /**
     * Removes all descendants from a node array.
     * <p>
     * A node is removed from the array when it is a descendant from
     * another node in the array.
     */
    private MutableTreeNode[] removeDescendantsFromNodeArray(MutableTreeNode[] nodes) {
        int i, j;
        TreePath[] paths = new TreePath[nodes.length];
        for (i = 0; i < nodes.length; i++) {
            paths[i] = new TreePath(getPathToRoot((TreeNode) nodes[i]));
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
     * Removes an UndoableEditListener.
     */
    @Override
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }

    /**
     * Adds an UndoableEditListener.
     */
    @Override
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }

    /** Adds a {@code PropertyChangeListener} which can optionally be wrapped
     * into a {@code WeakPropertyChangeListener}.
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /** Removes a {@code PropertyChangeListener}. If the listener was added
     * wrapped into a {@code WeakPropertyChangeListener}, the
     * {@code WeakPropertyChangeListener} is removed.
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        // Removes a property change listener from our list.
        // We need a somewhat complex procedure here in case a listener
        // has been registered using addPropertyChangeListener(new WeakPropertyChangeListener(listener));
        for (PropertyChangeListener l : propertySupport.getPropertyChangeListeners()) {
            if (l == listener) {
                propertySupport.removePropertyChangeListener(l);
                break;
            }
            if (l instanceof WeakPropertyChangeListener) {
                WeakPropertyChangeListener wl = (WeakPropertyChangeListener) l;
                PropertyChangeListener target = wl.getTarget();
                if (target == listener) {
                    propertySupport.removePropertyChangeListener(l);
                    break;
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    @Override
    public void fireUndoableEdit(UndoableEdit edit) {
        UndoableEditEvent evt = null;

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == UndoableEditListener.class) {
                // Lazily create the event
                if (evt == null) {
                    evt = new UndoableEditEvent(this, edit);
                }
                ((UndoableEditListener) listeners[i + 1]).undoableEditHappened(evt);
            }
        }
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}

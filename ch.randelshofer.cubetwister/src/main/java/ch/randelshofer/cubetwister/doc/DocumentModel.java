/*
 * @(#)DocumentModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.datatransfer.XMLTransferable;
import ch.randelshofer.gui.tree.MutableTreeModel;
import ch.randelshofer.gui.tree.TreeNodeImpl;
import ch.randelshofer.rubik.notation.Syntax;
import ch.randelshofer.undo.CompositeEdit;
import ch.randelshofer.undo.Undoable;
import ch.randelshofer.undo.UndoableObjectEdit;
import ch.randelshofer.util.ConcurrentDispatcher;
import ch.randelshofer.util.SequentialDispatcher;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds a CubeTwister document.
 * <p>
 * A document is a tree of EntityModels.
 * <p>
 * DocumentModels must be self-contained, that is, the
 * the EntityModels in this DocumentModel must not have
 * a reference to an EntityModel in another DocumentModel.
 *
 * @author Werner Randelshofer
 */
public class DocumentModel extends DefaultTreeModel
        implements MutableTreeModel, Undoable {
    private final static long serialVersionUID = 1L;
    private final static ConcurrentDispatcher threadPool = new ConcurrentDispatcher(Thread.MIN_PRIORITY, 1/*Runtime.getRuntime().availableProcessors()*/);
    public final static int CUBE_INDEX = 0;
    public final static int NOTATION_INDEX = 1;
    public final static int SCRIPT_INDEX = 2;
    public final static int TEXT_INDEX = 3;
    private final static String[] NODE_TYPES = {
            "Cube", "Notation", "Script", "Note"
    };
    final static HashMap<String, Syntax> syntaxValueSet = new HashMap<String, Syntax>();

    static {
        syntaxValueSet.put("PREFIX", Syntax.PREFIX);
        syntaxValueSet.put("SUFFIX", Syntax.SUFFIX);
        syntaxValueSet.put("HEADER", Syntax.PRECIRCUMFIX);
        syntaxValueSet.put("TAIL", Syntax.POSTCIRCUMFIX);
    }

    final static HashMap<String, Integer> axisValueSet = new HashMap<String, Integer>();

    static {
        axisValueSet.put("x", 0);
        axisValueSet.put("y", 1);
        axisValueSet.put("z", 2);
    }

    final static HashMap<String, Boolean> scriptTypeValueSet = new HashMap<String, Boolean>();

    static {
        scriptTypeValueSet.put("generator", true);
        scriptTypeValueSet.put("solver", false);
    }

    @Nullable
    private CubeModel defaultCube;
    /**
     * Key = LayerCount, Value = NotationModel.
     */
    @Nullable
    private HashMap<Integer, NotationModel> defaultNotation = new HashMap<Integer, NotationModel>();
    public static final String PROP_DEFAULT_CUBE = "DefaultCube";
    public static final String PROP_DEFAULT_NOTATION = "DefaultNotation";
    @Nullable
    protected PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

    /**
     * The dispatcher is used for lengthy background operations.
     *
     * @see #dispatch(Runnable)
     */
    @Nullable
    private SequentialDispatcher dispatcher;

    /**
     * Creates a new DocumentModel.
     */
    public DocumentModel() {
        super(new EntityModel(null, true));

        EntityModel node = getRoot();
        node.setUserObject(this);
        node.setRemovable(false);
        setAsksAllowsChildren(true);

        insertNodeInto(node = new EntityModel("Cubes", true), getRoot(), CUBE_INDEX);
        node.setRemovable(false);
        insertNodeInto(node = new EntityModel("Notations", true), getRoot(), NOTATION_INDEX);
        node.setRemovable(false);
        insertNodeInto(node = new EntityModel("Scripts", true), getRoot(), SCRIPT_INDEX);
        node.setRemovable(false);
        insertNodeInto(node = new EntityModel("Notes", true), getRoot(), TEXT_INDEX);
        node.setRemovable(false);
    }

    public void setDispatcher(SequentialDispatcher d) {
        dispatcher = d;
    }

    @Nonnull
    @Override
    public EntityModel getRoot() {
        return (EntityModel) super.getRoot();
    }

    /**
     * Dispatches a runnable on the worker thread of the document model.
     * <p>
     * Note that the worker thread executes the runnables sequentially.
     * <p>
     * Use this method to dispatch tasks which block the user interface
     * until the task has finished. i.e. Loading and Saving a document from
     * a file.
     */
    public void dispatch(Runnable runner) {
        if (dispatcher == null) {
            dispatcher = new SequentialDispatcher();
        }
        dispatcher.dispatch(runner);
    }

    /**
     * Dispatches a runnable on the global thread pool of the virtual machine.
     * The size of the thread pool is determined by the number of available
     * processors for the virtual machine.
     * <p>
     * Note that the worker thread executes the runnables concurrently.
     * <p>
     * Use this method to dispatch tasks which do not block the user interface
     * until the task has finished. i.e. Solving a scrambled cube.
     */
    public void dispatchSolver(@Nonnull Runnable runner) {
        threadPool.dispatch(runner);
    }

    @Nonnull
    public EntityModel getScripts() {
        return (EntityModel) root.getChildAt(SCRIPT_INDEX);
    }

    @Nonnull
    public EntityModel getTexts() {
        return (EntityModel) root.getChildAt(TEXT_INDEX);
    }

    @Nonnull
    public EntityModel getNotations() {
        return (EntityModel) root.getChildAt(NOTATION_INDEX);
    }

    @Nonnull
    public EntityModel getCubes() {
        return (EntityModel) root.getChildAt(CUBE_INDEX);
    }

    /**
     * Gets the default cube. The default cube is used
     * by the scripts unless they override it with their
     * own definition.
     */
    @Nullable
    public CubeModel getDefaultCube() {
        return defaultCube;
    }

    /**
     * Sets the default cube. The default cube is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public void setDefaultCube(CubeModel newValue) {
        CubeModel oldValue = defaultCube;
        defaultCube = newValue;
        propertySupport.firePropertyChange(PROP_DEFAULT_CUBE, oldValue, newValue);

        nodeChanged(oldValue);
        nodeChanged(newValue);

        UndoableEdit edit = new UndoableObjectEdit(this, "Default", oldValue, newValue) {
            private final static long serialVersionUID = 1L;

            @Override
            public void revert(Object a, Object b) {
                defaultCube = (CubeModel) b;
                propertySupport.firePropertyChange(PROP_DEFAULT_CUBE, a, b);
            }
        };
        fireUndoableEdit(edit);
    }

    /**
     * Gets the default notation. The default notation is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public NotationModel getDefaultNotation(int layerCount) {
        return defaultNotation.get(layerCount);
    }

    /**
     * Sets the default notation. The default notation is used
     * by the scripts unless they override it with their
     * own definition.
     */
    public void setDefaultNotation(@Nonnull NotationModel value) {
        NotationModel oldValue = defaultNotation.get(value.getLayerCount());

        // make sure that the notation is only represented once as the default
        defaultNotation.values().remove(value);
        defaultNotation.put(value.getLayerCount(), value);
        propertySupport.firePropertyChange(PROP_DEFAULT_NOTATION, oldValue, value);

        nodeChanged(oldValue);
        nodeChanged(value);

        UndoableEdit edit = new UndoableObjectEdit(this, "Default", oldValue, value) {
            private final static long serialVersionUID = 1L;

            @Override
            public void revert(Object a, @Nonnull Object b) {
                defaultNotation.put(((NotationModel) b).getLayerCount(), (NotationModel) b);
                propertySupport.firePropertyChange(PROP_DEFAULT_NOTATION, a, b);
            }
        };
        fireUndoableEdit(edit);
    }

    /**
     * Writes the indicated entities of the DocumentModel into the
     * output stream.
     * For peak performance, the output stream should be buffered.
     */
    public void writeXML(@Nonnull OutputStream out, Object[] entities)
            throws IOException {
        new DocumentModelXmlWriter().writeXml(this, out, entities);
    }



    @Nonnull
    @Override
    public EntityModel getChild(@Nonnull Object parent, int index) {
        return (EntityModel) super.getChild(parent, index);
    }

    /**
     * Writes the contents of the DocumentModel into the output stream.
     * For peak performance, the output stream should be buffered.
     */
    public void writeXML(@Nonnull PrintWriter out) throws IOException {
        new DocumentModelXmlWriter().writeXml(this, out);
    }

    /**
     * Adds the contents of the input stream to the DocumentModel.
     * For peak performance, the input stream should be buffered.
     */
    public void addSerializedNode(@Nonnull InputStream in)
            throws IOException {
        insertSerializedNodeInto(in, (TreeNodeImpl) root, root.getChildCount());
    }

    /**
     * Adds the contents of the input stream to the DocumentModel.
     * For peak performance, the input stream should be buffered.
     */
    public void insertSerializedNodeInto(@Nonnull InputStream in, @Nonnull MutableTreeNode parent, int index)
            throws IOException {
        Reader r = new InputStreamReader(in, "UTF8");
        insertSerializedNodeInto(r, parent, index);
    }

    /**
     * Adds the contents of the input stream to the DocumentModel.
     * For peak performance, the input stream should be buffered.
     */
    public void insertSerializedNodeInto(Reader r, @Nonnull MutableTreeNode parent, int index)
            throws IOException {
        //in = new ByteFilterInputStream(in);
        try {
            // The DOM XMLElement.
            long start = System.currentTimeMillis();
            XMLElement doc = new XMLElement(null, false, false);
            //.newInstance().newDocumentBuilder().parse(in);
            doc.parseFromReader(r);
            long end1 = System.currentTimeMillis();
            new DocumentModelXmlReader().insertXMLNodeInto(this, doc, parent, index);
            long end2 = System.currentTimeMillis();
            //System.out.println("DocumentModel.addSerializedNode buildDOM=" + (end1 - start) + " parseDOM=" + (end2 - end1));

        } catch (XMLParseException e) {
            throw new IOException(e);
        }

    }

    public void addXMLNode(@Nonnull XMLElement doc)
            throws IOException {
        new DocumentModelXmlReader().insertXMLNodeInto(this, doc, (TreeNodeImpl) root, root.getChildCount());
    }


    /**
     * Invoked this to add newChild to parents children.
     * This will then message nodesWereInserted to create the appropriate event.
     * This is the preferred way to add children as it will create the appropriate event.
     */
    public void addTo(EntityModel newChild, @Nonnull TreeNode parent) {
        insertNodeInto(newChild, (MutableTreeNode) parent, parent.getChildCount());
    }

    /**
     * Invoked this to add newChild to parents children.
     * This will then message nodesWereInserted to create the appropriate event.
     * This is the preferred way to add children as it will create the appropriate event.
     */
    @Override
    public void insertNodeInto(final MutableTreeNode newChild, final MutableTreeNode parent, final int index) {
        CompositeEdit ce = new CompositeEdit();
        fireUndoableEdit(ce);

        fireUndoableEdit(
                new AbstractUndoableEdit() {
                    private final static long serialVersionUID = 1L;

                    @Override
                    public void redo() {
                        super.redo();
                        insertNodeIntoQuiet((TreeNodeImpl) newChild, (TreeNodeImpl) parent, index);
                    }

                    @Nonnull
                    @Override
                    public String getPresentationName() {
                        return "Insert";
                    }
                });

        insertNodeIntoQuiet((TreeNodeImpl) newChild, (TreeNodeImpl) parent, index);

        fireUndoableEdit(
                new AbstractUndoableEdit() {
                    private final static long serialVersionUID = 1L;

                    @Override
                    public void undo() {
                        super.undo();
                        removeNodeFromParentQuiet((TreeNodeImpl) newChild);
                    }

                    @Nonnull
                    @Override
                    public String getPresentationName() {
                        return "Insert";
                    }
                });
        fireUndoableEdit(ce);
    }

    protected void insertNodeIntoQuiet(MutableTreeNode newChild, TreeNode parent, int index) {
        super.insertNodeInto(newChild, (MutableTreeNode) parent, index);
    }

    protected void removeNodeFromParentQuiet(@Nonnull MutableTreeNode node) {
        super.removeNodeFromParent(node);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Returns wether the specified node may be removed.
     *
     * @param node a node from the tree, obtained from this data source.
     */
    @Override
    public boolean isNodeRemovable(@Nonnull MutableTreeNode node) {
        // Direct children of the root node are
        // the Cubes, Notations, Scripts and the Text
        // folder. It does not make sense to rename
        // these nodes.
        //return node != root && ((TreeNode) node).getParent() != root;
        return ((EntityModel) node).isRemovable();
    }

    /**
     * Message this to remove node from its parent.
     *
     * @throws IllegalStateException if the node is not removable.
     */
    @Override
    public void removeNodeFromParent(@Nonnull final MutableTreeNode node) {
        if (isNodeRemovable(node)) {
            CompositeEdit ce = new CompositeEdit();
            fireUndoableEdit(ce);

            fireUndoableEdit(
                    new AbstractUndoableEdit() {
                        private final static long serialVersionUID = 1L;

                        @Override
                        public void redo() {
                            super.redo();
                            removeNodeFromParentQuiet(node);
                        }

                        @Nonnull
                        @Override
                        public String getPresentationName() {
                            return "Remove";
                        }
                    });

            final TreeNode parent = node.getParent();
            final int index = parent.getIndex(node);

            removeNodeFromParentQuiet(node);

            fireUndoableEdit(
                    new AbstractUndoableEdit() {
                        private final static long serialVersionUID = 1L;

                        @Override
                        public void undo() {
                            super.undo();
                            insertNodeIntoQuiet(node, parent, index);
                        }

                        @Nonnull
                        @Override
                        public String getPresentationName() {
                            return "Remove";
                        }
                    });
            fireUndoableEdit(ce);
        } else {
            throw new IllegalStateException("org.w3c.dom.Node can not be removed.");
        }
    }

    /**
     * Invoke this to insert a new child at location index in parents children.
     * This will then message nodesWereInserted to create the appropriate
     * event. This is the preferred way to add children as it will create the
     * appropriate event.
     *
     * @param newNodeType the type of the new child to be created, obtained
     *                    from getCreatableChildren
     * @param parent      a node from the tree, obtained from this data source.
     * @param index       index of the child.
     * @throws IllegalStateException if the parent node does not allow children.
     */
    @Nonnull
    public TreePath createNodeAt(Object newNodeType, @Nonnull MutableTreeNode parent, int index) throws IllegalStateException {
        // We create the nodes where we want them to be created!
        EntityModel realParent = null;
        int realIndex;
        EntityModel newChild = null;
        int type;

        for (type = 0; type < NODE_TYPES.length; type++) {
            if (newNodeType == NODE_TYPES[type]) {
                realParent = (EntityModel) root.getChildAt(type);
                break;
            }
        }
        realIndex = (parent == realParent) ? index : //
                (realParent == parent.getParent()) ? realParent.getIndex(parent) + 1 ://
                        realParent.getChildCount();
        switch (type) {
            case CUBE_INDEX:
                newChild = new CubeModel();
                ((CubeModel) newChild).setName("unnamed Cube");
                break;
            case NOTATION_INDEX:
                newChild = new NotationModel();
                ((NotationModel) newChild).setName("unnamed Notation");
                break;
            case SCRIPT_INDEX:
                newChild = new ScriptModel();
                ((ScriptModel) newChild).setName("unnamed Script");
                break;
            case TEXT_INDEX:
                newChild = new TextModel();
                ((TextModel) newChild).setName("unnamed Note");
                break;
        }

        insertNodeInto(newChild, realParent, realIndex);

        LinkedList<Object> path = new LinkedList<Object>();
        for (EntityModel n = newChild; n != null; n = n.getParent()) {
            path.addFirst(n);
        }
        return new TreePath(path.toArray());
    }

    /**
     * Returns wether the specified node may be renamed.
     *
     * @param node a node from the tree, obtained from this data source.
     */
    public boolean isNodeEditable(@Nonnull MutableTreeNode node) {
        // Direct children of the root node are
        // the Cubes, Notations, Scripts and the Text
        // folder. It does not make sense to rename
        // these nodes.
        return node != root && node.getParent() != root;
    }

    /**
     * Returns the types of children that may be created at this node.
     *
     * @param parent a node from the tree, obtained from this data source.
     * @return an array of objects that specify a child type that may be
     * added to the node. Returns an empty array for nodes
     * that cannot have additional children.
     */
    @Nonnull
    @Override
    public Object[] getCreatableNodeTypes(Object parent) {
        return NODE_TYPES;
    }

    @Nullable
    @Override
    public Object getCreatableNodeType(Object parent) {
        if (isLeaf(parent)) {
            return null;
        }

        EntityModel node = (EntityModel) parent;
        TreeNode[] path = node.getPath();
        if (path.length <= 1) {
            return "Script";
        }
        if (path[1] == getCubes()) {
            return "Cube";
        } else if (path[1] == getNotations()) {
            return "Notation";
        } else if (path[1] == getScripts()) {
            return "Script";
        } else if (path[1] == getTexts()) {
            return "Note";
        } else {
            return null;
        }
    }

    /**
     * Returns wether the specified node may be inserted.
     *
     * @param parent a node from the tree, obtained from this data source.
     */
    public boolean isNodeAddable(MutableTreeNode parent, int index) {
        return true;
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof InfoModel;
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

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
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

    /**
     * Copies the indicated nodes to the clipboard.
     *
     * @param nodes The nodes to be copied.
     * @throws IllegalStateException if the nodes can not be removed.
     */
    @Nonnull
    public Transferable exportTransferable(MutableTreeNode[] nodes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            writeXML(out, nodes);
            out.close();
        } catch (IOException e) {
            InternalError error = new InternalError(e.getMessage());
            error.initCause(e);
            throw error;
        }
        XMLTransferable transfer = new XMLTransferable(out.toByteArray(), "text/xml", "Cube Twister Markup");
        return transfer;
    }

    @Nonnull
    @Override
    public List<TreePath> importTransferable(@Nonnull Transferable transfer, int action, @Nonnull MutableTreeNode parent, int index)
            throws UnsupportedFlavorException, IOException {
        LinkedList<TreePath> importedPaths = new LinkedList<TreePath>();
        DataFlavor flavor = new DataFlavor("text/xml", "Cube Twister Markup");
        //Transferable transfer = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
        if (transfer.isDataFlavorSupported(flavor)) {
            InputStream in = null;
            try {
                in = (InputStream) transfer.getTransferData(flavor);
                insertSerializedNodeInto(in, parent, index);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            importedPaths.add(new TreePath(((TreeNodeImpl) parent).getPath()).pathByAddingChild(getChild(parent, index)));
            return importedPaths;
        } else if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            Reader in = null;
            try {
                in = new StringReader((String) transfer.getTransferData(DataFlavor.stringFlavor));
                insertSerializedNodeInto(in, parent, index);
            } finally {
                if (in != null) {
                    in.close();
                }
            }

            importedPaths.add(new TreePath(((TreeNodeImpl) parent).getPath()).pathByAddingChild(getChild(parent, index)));
            return importedPaths;
        } else {
            Toolkit.getDefaultToolkit().beep();
            return importedPaths;
        }
    }

    public boolean isImportable(DataFlavor[] flavors, int action, MutableTreeNode parent, int index) {
        return parent == getCubes() || parent == getNotations() || parent == getScripts() || parent == getTexts();
    }

    /**
     * Returns true, if no background processes are working on the
     * model.
     */
    public boolean isCloseable() {
        for (EntityModel node : getScripts().getChildren()) {
            ScriptModel item = (ScriptModel) node;
            if (item.getProgressView() != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Shuts down all background processes.
     */
    public void close() {
        for (EntityModel node : getScripts().getChildren()) {
            ScriptModel item = (ScriptModel) node;
            ProgressObserver p = item.getProgressView();
            if (p != null) {
                p.cancel();
            }
        }
    }

    @Nonnull
    public Action[] getNodeActions(@Nonnull final MutableTreeNode[] nodes) {
        LinkedList<Action> actions = new LinkedList<Action>();
        boolean allNodesAreScripts = true;
        for (int i = 0; i < nodes.length; i++) {
            if (!(nodes[i] instanceof ScriptModel)) {
                allNodesAreScripts = false;
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    public void dispose() {
        TreeNode[] children = new TreeNode[root.getChildCount()];
        int[] childIndices = new int[root.getChildCount()];
        for (int i = 0; i < children.length; i++) {
            EntityModel entity = (EntityModel) root.getChildAt(i);
            entity.dispose();
            children[i] = root.getChildAt(i);
            childIndices[i] = i;
        }
        fireTreeStructureChanged(this, new Object[]{root}, childIndices, children);
        defaultCube = null;
        defaultNotation = null;
        propertySupport = null;
        listenerList = null;
        dispatcher = null;

        ((EntityModel) root).setUserObject(null);
        root = null;
    }
    /*
    public void finalize() {
    System.out.println("DocumentModel.finalize " + this);
    }*/
}

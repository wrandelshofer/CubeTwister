/*
 * @(#)MutableJTree.java  3.3  2011-01-22
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui;

import ch.randelshofer.gui.tree.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.awt.event.*;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * A JTree that uses a MutableTreeModel. Users can add and remove elements
 * using a popup menu. MutableJTree also supports the standard clipboard
 * operations cut, copy and paste.
 *
 * @author  Werner Randelshofer
 * @version 3.3 2011-01-22 Use DefaultMutableTreeNode instead of MutableTreeNode.
 * Displays node actions.
 * <br>3.2.1 2011-01-20 Method delete() did not remove all descendants.
 * <br>3.2 2010-01-09 Implemented duplicate() method.
 * Adapted for JHotDraw 7.3 rev604.
 * <br>3.1 2009-12-21 MutableTreeModel.createNodeAt-method returns path to
 * created node.
 * <br>3.0 2008-03-19 Streamlined naming of cut/copy/paste methods with
 * JTextComponent. 
 * <br>2.4 2007-01-12 Place popup menu nicely by the button. 
 * <br>2.3.1 2006-01-04 Specifying Quaqua "tableHeader" button style for popup
 * button.
 * <br>2.3  2004-07-03  Reworked due to API changes in MutableTreeModel.
 * <br>2.2.1 2004-02-03 Fixed a problem in method unconfigureEnclosingScrollPane.
 * <br>2.2 2003-06-20 Add actions from MutableTreeModel to popup menu.
 * <br>2.1 2002-12-21 Popup button added.
 * <br>2.0.2 2002-11-20 Undo support added.
 * <br>2.0.1 2001-10-13
 */
public class MutableJTree extends JTree
        implements EditableComponent {
    private final static long serialVersionUID = 1L;
    /**
     * This inner class is used to prevent the API from being cluttered
     * by internal listeners.
     */
    private class EventHandler implements ClipboardOwner {

        /**
         * Notifies this object that it is no longer the owner of the contents
         * of the clipboard.
         */
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    }
    private EventHandler eventHandler = new EventHandler();
    /**
     * Holds locale specific resources.
     */
    private ResourceBundleUtil labels;
    /**
     * Listener for popup mouse events.
     */
    private MouseAdapter popupListener;
    /**
     * Popup button at the top right corner
     * of the enclosing scroll pane.
     */
    private JButton popupButton;

    /**
     * Constructs a MutableJTree with an empty DefaultMutableTreeModel.
     */
    public MutableJTree() {
        super(new DefaultMutableTreeModel());
        init();
    }

    /**
     * Constructs a MutableJTree with the specified MutableTreeModel.
     */
    public MutableJTree(MutableTreeModel m) {
        super(m);
        init();
    }

    /**
     * This method is called from the constructor to initialize the Object.
     */
    private void init() {
        initComponents();

        // The popup button will be placed on the top right corner
        // of the parent JScrollPane when the MutableJList is
        // added to a JScrollPane.
        popupButton = new JButton();
        popupButton.setIcon(Icons.get(Icons.ACTIONS_TABLE_HEADER));
        popupButton.putClientProperty("Quaqua.Button.style", "tableHeader");
        popupButton.addMouseListener(
                new MouseAdapter() {

            @Override
                    public void mousePressed(MouseEvent evt) {
                        if (isEnabled()) {
                            showPopup(evt, true);
                        }
                    }
                });

        // The popup listener provides an alternative way for
        // opening the popup menu.
        popupListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                if (isEnabled() && evt.isPopupTrigger()) {
                    showPopup(evt, false);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (isEnabled() && evt.isPopupTrigger()) {
                    showPopup(evt, false);
                }
            }
        };
        addMouseListener(popupListener);

        // All locale specific and LAF specific
        // labels are read from a resource bundle.
        initLabels(Locale.getDefault());

        // Transfer handler
        setTransferHandler(new MutableTreeTransferHandler());
    }

    /**
     * Initializes the labels in a locale specific and
     * look-and-feel (LAF) specific way.
     */
    private void initLabels(Locale locale) {
        // remove previously installed key strokes
        KeyStroke keyStroke;
        if (labels != null) {
            if (null != (keyStroke = labels.getKeyStroke("edit.new.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("edit.duplicate.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("edit.cut.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("edit.copy.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("edit.paste.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("edit.delete.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
        }

        // get the locale and LAF specific resources
        labels = ResourceBundleUtil.getBundle(
                "ch.randelshofer.gui.Labels", locale);

        // install key strokes
        if (labels != null) {
            if (null != (keyStroke = labels.getKeyStroke("edit.new.accelerator"))) {
                registerKeyboardAction(
                        new ActionListener() {

                    @Override
                            public void actionPerformed(ActionEvent evt) {
                                if (isEnabled()) {
                                    create();
                                }
                            }
                        },
                        keyStroke,
                        WHEN_FOCUSED);
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.duplicate.accelerator"))) {
                registerKeyboardAction(new DuplicateAction(this),
                        keyStroke,
                        WHEN_FOCUSED);
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.cut.accelerator"))) {
                registerKeyboardAction(new CutAction(this),
                        keyStroke,
                        WHEN_FOCUSED);
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.copy.accelerator"))) {
                registerKeyboardAction(new CopyAction(this),
                        keyStroke,
                        WHEN_FOCUSED);
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.paste.accelerator"))) {
                registerKeyboardAction(new PasteAction(this),
                        keyStroke,
                        WHEN_FOCUSED);
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.delete.accelerator"))) {
                registerKeyboardAction(new DeleteAction(this),
                        keyStroke,
                        WHEN_FOCUSED);
            }
        }
    }

    /**
     * Creates the popup menu. The contents of the popup menu
     * is determined by the current selection.
     *
     * @return The popup menu.
     */
    protected JPopupMenu createPopup() {
        final TreePath[] selectedPaths = getSelectionPaths();
        final MutableTreeModel model = (MutableTreeModel) getModel();
        final TreePath leadSelectionPath = (selectedPaths == null || selectedPaths.length == 0) ? new TreePath(model.getRoot()) : getSelectionModel().getLeadSelectionPath();
        final DefaultMutableTreeNode leadNode = (DefaultMutableTreeNode) leadSelectionPath.getLastPathComponent();
        final JPopupMenu popup = new JPopupMenu();

        JMenuItem item;
        boolean b;

        // New
        Object[] types = model.getCreatableNodeTypes(leadNode);
        Object defaultType = model.getCreatableNodeType(leadNode);
        for (int i = 0; i < types.length; i++) {
            final Object newChildType = types[i];
            item = new JMenuItem(labels.getFormatted("edit.new.text", new Object[]{newChildType.toString()}));
            if (types[i] == defaultType) {
                item.setMnemonic(labels.getMnemonic("edit.new.mnemonic"));
                if (labels.getKeyStroke("edit.new.accelerator") != null) {
                    item.setAccelerator(labels.getKeyStroke("edit.new.accelerator"));
                }
            }

            item.addActionListener(
                    new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            MutableTreeModel m = (MutableTreeModel) getModel();
                            TreePath createdPath = m.createNodeAt(
                                    newChildType,
                                    leadNode,
                                    m.getChildCount(leadNode));
                            if (createdPath != null) {
                                expandPath(createdPath);
                                setSelectionPath(createdPath);
                                scrollPathToVisible(createdPath);
                            }
                        }
                    });
            popup.add(item);
        }

        if (popup.getComponentCount() > 0) {
            popup.addSeparator();
        }
        // Node Actions
        DefaultMutableTreeNode[] selectedNodes=new DefaultMutableTreeNode[selectedPaths==null?0:selectedPaths.length];
        for (int i=0;i<selectedNodes.length;++i) {
            selectedNodes[i]=(DefaultMutableTreeNode)selectedPaths[i].getLastPathComponent();
        }
        Action[] nodeActions=model.getNodeActions(selectedNodes);
        for (Action a:nodeActions) {
            popup.add(a);
        }
        if (nodeActions.length>0) {
            popup.addSeparator();
        }

        // Cut
        item = new JMenuItem(labels.getString("edit.cut.text"));
        item.setMnemonic(labels.getMnemonic("edit.cut.mnemonic"));
        if (labels.getKeyStroke("edit.cut.accelerator") != null) {
            item.setAccelerator(labels.getKeyStroke("edit.cut.accelerator"));
        }
        boolean enabled = true;
        if (selectedPaths != null) {
            for (int i = 0; i < selectedPaths.length; i++) {
                if (!model.isNodeRemovable((DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent())) {
                    enabled = false;
                    break;
                }
            }
        }
        item.setEnabled(enabled && model.isNodeRemovable(leadNode));
        item.addActionListener(new CutAction(this));
        popup.add(item);

        // Copy
        item = new JMenuItem(labels.getString("edit.copy.text"));
        item.setMnemonic(labels.getMnemonic("edit.copy.mnemonic"));
        if (labels.getKeyStroke("edit.copy.accelerator") != null) {
            item.setAccelerator(labels.getKeyStroke("edit.copy.accelerator"));
        }
        //item.setEnabled(leadNode != null);
        item.setEnabled(getSelectionCount() > 0);
        item.addActionListener(new CopyAction(this));
        popup.add(item);

        // Paste
        item = new JMenuItem(labels.getString("edit.paste.text"));
        item.setMnemonic(labels.getMnemonic("edit.paste.mnemonic"));
        if (labels.getKeyStroke("edit.paste.accelerator") != null) {
            item.setAccelerator(labels.getKeyStroke("edit.paste.accelerator"));
        }
        item.setEnabled(
                model.getCreatableNodeTypes(leadNode).length > 0
                || leadNode.getParent() != null && model.getCreatableNodeTypes(leadNode.getParent()).length > 0);
        item.addActionListener(new PasteAction(this));
        popup.add(item);

        // Duplicate
        item = new JMenuItem(labels.getString("edit.duplicate.text"));
        item.setMnemonic(labels.getMnemonic("edit.duplicate.mnemonic"));
        if (labels.getKeyStroke("edit.duplicate.accelerator") != null) {
            item.setAccelerator(labels.getKeyStroke("edit.duplicate.accelerator"));
        }
        item.setEnabled(
                model.getCreatableNodeTypes(leadNode).length > 0
                || leadNode.getParent() != null && model.getCreatableNodeTypes(leadNode.getParent()).length > 0);
        item.addActionListener(new DuplicateAction(this));
        popup.add(item);

        // Add the "Delete" menu item.
        item = new JMenuItem(labels.getString("edit.delete.text"));
        item.setMnemonic(labels.getMnemonic("edit.delete.mnemonic"));
        if (labels.getKeyStroke("edit.delete.accelerator") != null) {
            item.setAccelerator(labels.getKeyStroke("edit.delete.accelerator"));
        }
        enabled = true;
        if (selectedPaths != null) {
            for (int i = 0; i < selectedPaths.length; i++) {
                if (!model.isNodeRemovable((DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent())) {
                    enabled = false;
                    break;
                }
            }
        }
        item.setEnabled(enabled && model.isNodeRemovable(leadNode));
        item.addActionListener(new DeleteAction(this));
        popup.add(item);

        popup.addSeparator();

        // add the "Select All" menu item
        item = new JMenuItem(labels.getString("edit.selectAll.text"));
        item.setMnemonic(labels.getMnemonic("edit.selectAll.mnemonic"));
        if (labels.getKeyStroke("edit.selectAll.accelerator") != null) {
            item.setAccelerator(labels.getKeyStroke("edit.selectAll.accelerator"));
        }
        item.setIcon(labels.getSmallIconProperty("edit.selectAll", getClass()));
        item.addActionListener(new SelectAllAction(this));
        item.setEnabled(true);
        popup.add(item);
        // Actions

        return popup;

    }

    protected void showPopup(MouseEvent evt, boolean isTriggeredByButton) {
        Component c = evt.getComponent();
        JPopupMenu popupMenu = createPopup();
        if (isTriggeredByButton && c.getGraphicsConfiguration() != null) {
            // If the popup menu is triggered by a button, it is
            // presented at one of the borders of the button.
            Point cScreenLocation = c.getLocationOnScreen();
            Rectangle cBounds = c.getBounds();
            Rectangle screenBounds = c.getGraphicsConfiguration().getBounds();
            Dimension pDimension = popupMenu.getPreferredSize();

            if (screenBounds.contains(cScreenLocation.x, cScreenLocation.y + cBounds.height, pDimension.width, pDimension.height)) {
                popupMenu.show(c, 0, cBounds.height);
            } else if (screenBounds.contains(cScreenLocation.x + cBounds.width - pDimension.width, cScreenLocation.y + cBounds.height, pDimension.width, pDimension.height)) {
                popupMenu.show(c, cBounds.width - pDimension.width, cBounds.height);
            } else if (screenBounds.contains(cScreenLocation.x, cScreenLocation.y - pDimension.height, pDimension.width, pDimension.height)) {
                popupMenu.show(c, 0, -pDimension.height);
            } else {
                popupMenu.show(c, cBounds.width - pDimension.width, -pDimension.height);
            }
        } else {
            // If the popup menu is triggered by a mouse event, it is
            // presented at the location where the mouse event happened
            popupMenu.show(c, evt.getX(), evt.getY());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents

    /**
     * Inserts a new row after the lead selection row,
     * if the model allows it.
     */
    public void create() {
        MutableTreeModel model = (MutableTreeModel) getModel();
        TreePath path = getSelectionModel().getLeadSelectionPath();
        if (path == null) {
            path = new TreePath(model.getRoot());
        }
        int index = model.getChildCount(path.getLastPathComponent());
        do {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (model.getCreatableNodeType(node) != null) {
                if (model.isNodeAddable(node, model.getChildCount(node))) {
                    TreePath createdPath = model.createNodeAt(
                            model.getCreatableNodeType(node),
                            (DefaultMutableTreeNode) path.getLastPathComponent(), index);
                    if (createdPath != null) {
                        expandPath(createdPath);
                        setSelectionPath(createdPath);
                        scrollPathToVisible(createdPath);
                    }
                }
                break;
            }
            if (path.getPathCount() > 1) {
                index = node.getParent().getIndex(node) + 1;
            } else {
                index = 0;
            }
            path = path.getParentPath();
        } while (path.getPathCount() > 0);
    }

    /** Deletes the component at (or after) the caret position.
     */
    @Override
    public void delete() {
        if (isEditing()) {
            getCellEditor().stopCellEditing();
        }

        //if (isEnabled() && isEditable() && getSelectionCount() > 0) {
        if (isEnabled() && getSelectionCount() > 0) {
            MutableTreeModel model = (MutableTreeModel) getModel();
            TreePath[] paths = getSelectionPaths();
            if (paths == null) {
                getToolkit().beep();
                return;
            }

            int i;
            int j;

            // remove root from list of selected paths if root is not visible
            if (!isRootVisible()) {
                Object root = model.getRoot();
                for (i = 0; i < paths.length; i++) {
                    if (paths[i].getLastPathComponent() == root) {
                        removeSelectionPath(paths[i]);
                        paths = getSelectionPaths();
                        break;
                    }
                }
            }

            if (paths == null) {
                getToolkit().beep();
                return;
            }

            // remove descendants from list of selected nodes
            for (i = paths.length - 1; i >= 0; i--) {
                for (j = i - 1; j >= 0; j--) {
                    if (paths[j] != null) {
                        if (paths[i].isDescendant(paths[j])) {
                            paths[j] = null;
                        }
                        if (paths[j].isDescendant(paths[i])) {
                            paths[i] = null;
                            break;
                        }
                    }
                }
            }

            // check if all nodes may be removed
            int deletableCount = 0;
            int nonDeletableCount = 0;
            for (i = 0; i < paths.length; i++) {
                if (paths[i] != null) {
                    if (!model.isNodeRemovable((DefaultMutableTreeNode) paths[i].getLastPathComponent())) {
                        nonDeletableCount++;
                    } else {
                        deletableCount++;
                    }
                }
            }
            if (nonDeletableCount > 0) {
                getToolkit().beep();
                /*
                JOptionPane.showMessageDialog(
                this,
                labels.getString("nodeNotRemovableInfo"),
                labels.getString("nodeNotRemovableInfoTitle"),
                JOptionPane.INFORMATION_MESSAGE
                );*/
                requestFocus();
            } else {/*
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                this,
                labels.getString("removeNodeQuestion"),
                labels.getString("removeNodeQuestionTitle"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
                )) {*/
                for (i = 0; i < paths.length; i++) {
                    if (paths[i] != null) {
                        model.removeNodeFromParent((DefaultMutableTreeNode) paths[i].getLastPathComponent());
                    }
                }
                //}
                requestFocus();
            }
        }
    }

    /** Duplicates the selected region.
     */
    @Override
    public void duplicate() {
        final TreePath[] selectedPaths = getSelectionPaths();
        TreePath leadSelection = getLeadSelectionPath();
        if (selectedPaths == null) {
            getToolkit().beep();
            return;
        }
        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[selectedPaths.length];
        Object[] selectedNodes = new Object[selectedPaths.length];
        for (int i = 0; i < selectedPaths.length; i++) {
            selectedNodes[i] = selectedPaths[i].getLastPathComponent();
            nodes[i] = (DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent();
        }

        MutableTreeModel m = (MutableTreeModel) getModel();
        Transferable t = m.exportTransferable(nodes);
        try {
            m.importTransferable(t, DnDConstants.ACTION_COPY, (DefaultMutableTreeNode) leadSelection.getLastPathComponent(), m.getIndexOfChild(leadSelection.getParentPath().getLastPathComponent(), leadSelection.getLastPathComponent()));
        } catch (UnsupportedFlavorException ex) {
            // Duplicate should never fail
            ex.printStackTrace();
        } catch (IOException ex) {
            // Duplicate should never fail
            ex.printStackTrace();
        }

    }

    /**
     * Calls the configureEnclosingScrollPane method.
     *
     * @see #configureEnclosingScrollPane()
     */
    @Override
    public void addNotify() {
        super.addNotify();
        configureEnclosingScrollPane();
    }

    /**
     * If this <code>MutableJList</code> is the <code>viewportView</code> of an
     * enclosing <code>JScrollPane</code> (the usual situation), configure this
     * scroll pane by, amongst other things, installing the lists's popup-menu
     * button at the top right corner of the scroll pane. When a <code>
     * MutableJList</code> is added to a <code>JScrollPane</code> in the usual
     * way, using <code>new JScrollPane(myTable)</code>, <code>addNotify</code>
     * is called in the <code>MutableJList</code> (when the table is added to
     * the viewport). <code>MutableJList</code>'s <code>addNotify</code> method
     * in turn calls this method, which is protected so that this default
     * installation procedure can be overridden by a subclass.
     *
     * @see #addNotify()
     */
    protected void configureEnclosingScrollPane() {
        //super.configureEnclosingScrollPane();

        Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport != null && viewport.getView() == this) {
                    // Install the mouse listener for the popup menu
                    viewport.addMouseListener(popupListener);

                    // Install a ScrollPaneLayout2 layout manager to ensure
                    // that the popup button we are going to add next is
                    // shown properly.
                    ScrollPaneLayout2 spl = new ScrollPaneLayout2();
                    scrollPane.setLayout(spl);
                    spl.syncWithScrollPane(scrollPane);

                    // Install the popup button at the top right corner
                    // of the JScrollPane
                    if (popupButton.getParent() == null) {
                        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, popupButton);
                    }
                }
            }
        }
    }

    /**
     * Calls the unconfigureEnclosingScrollPane method.
     *
     * @see #unconfigureEnclosingScrollPane()
     */
    @Override
    public void removeNotify() {
        unconfigureEnclosingScrollPane();
        super.removeNotify();
    }

    /** Reverses the effect of <code>configureEnclosingScrollPane</code> by
     * removing the button at the top right corner of the <code>JScrollPane</code>.
     * <code>MutableJTable</code>'s <code>removeNotify</code> method
     * calls this method, which is protected so that this default uninstallation
     * procedure can be overridden by a subclass.
     *
     * @see #removeNotify()
     */
    protected void unconfigureEnclosingScrollPane() {
        Container p = getParent();
        if (p != null && p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp != null && gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport != null && viewport.getView() == this) {
                    // Remove the previously installed mouse listener for the popup menu
                    viewport.removeMouseListener(popupListener);

                    // Remove the previously installed ScrollPaneLayout2
                    // layout manager.
                    ScrollPaneLayout spl = new ScrollPaneLayout();
                    scrollPane.setLayout(spl);
                    spl.syncWithScrollPane(scrollPane);

                    // Remove the popup button from the top right corner
                    // of the JScrollPane
                    try {
                        // I would like to set the corner to null. Unfortunately
                        // we are called from removeNotify. Removing a component
                        // during removeNotify leads to a NPE.
                        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JPanel());
                        //scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, null);
                    } catch (NullPointerException e) {
                        // This try/catch block is a workaround for
                        // bug 4247092 which is present in JDK 1.3.1 and prior
                        // versions
                    }
                }
            }
        }
    }

    /**
     * Sets the enabled state of the component.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        popupButton.setEnabled(b);
    }

    public JButton getPopupButton() {
        return popupButton;
    }

    @Override
    public void selectAll() {
        setSelectionInterval(0, getRowCount() - 1);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

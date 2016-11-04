/*
 * @(#)JExplorer.java 1.1  2011-02-02
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui;

import ch.randelshofer.gui.border.PlacardButtonBorder;
import ch.randelshofer.gui.tree.*;
import ch.randelshofer.undo.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.tree.*;
import javax.swing.undo.*;

/**
 * This panel acts like an Explorer Window as commonly used on Windows.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2011-02-02 Adds support for info panel. Replaces Dispatcher
 * by Executor.
 * <br>1.0.1 2011-01-22 UndoableTreeSelectionModel was renamed to DefaultUndoableTreeSelectionModel.
 * <br>1.0 2001-10-05
 */
public class JExplorer extends javax.swing.JPanel {
    private final static long serialVersionUID = 1L;

    /**
     * The viewer supplies components for rendering the
     * selected item(s) of the tree.
     */
    private Viewer viewer = new DefaultViewer();
    /**
     * This variable holds the Object or Object[]-array
     * of the objects being viewed currently.
     */
    private Object viewedObject;
    /**
     * This variable holds the Component which displays
     * the current viewedObject.
     */
    private Component view;
    /**
     * Undo Manager for undo/redo support.
     */
    private UndoManager undo;
    /**
     * Dispatcher for background processing.
     */
    private ExecutorService dispatcher;
    /** The info panel is displayed in a split pane below the tree.
     * If infoPanel is null, no split pane is used.
     */
    private JComponent infoPanel;
    /** This split pane is non-null only, if infoPanel is non-null.
     */
    private JSplitPane infoSplitPane;

    /** Creates new form JExplorer */
    public JExplorer() {
        initComponents();

        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
            //leftPane.setOpaque(true);
            tree.putClientProperty("Quaqua.Tree.style", "sourceList");
            addButton.setIcon(Icons.get(Icons.EDIT_ADD_PLACARD));
            addButton.putClientProperty("Quaqua.Button.style", "gradient");
            addButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, 0));
            addButton.setFocusable(false);

            JButton actionsButton = tree.getPopupButton();
            toolBar.add(actionsButton);
            actionsButton.setVisible(true);
            actionsButton.setIcon(Icons.get(Icons.ACTIONS_PLACARD));
            actionsButton.setText(null);
            actionsButton.putClientProperty("Quaqua.Button.style", "gradient");
            actionsButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, 0));
            actionsButton.setFocusable(false);

            toolBar.putClientProperty("Quaqua.Border.insets", new Insets(0, 0, 0, 0));
            toolBar.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, -1));
            toolBar.putClientProperty("Quaqua.ToolBar.style", "gradient");

            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            splitPane.setDividerSize(1);
            splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
        } else if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            tree.setBackground(new Color(0xd7e4f2));
            addButton.setUI((ButtonUI) BasicButtonUI.createUI(addButton));
            addButton.setBorder(new PlacardButtonBorder(-1));
            addButton.setIcon(Icons.get(Icons.EDIT_ADD_PLACARD));
            addButton.setFocusable(false);
            JButton actionsButton = tree.getPopupButton();
            toolBar.add(actionsButton);
            actionsButton.setUI((ButtonUI) BasicButtonUI.createUI(actionsButton));
            actionsButton.setIcon(Icons.get(Icons.ACTIONS_PLACARD));
            actionsButton.setVisible(true);
            actionsButton.setText(null);
            actionsButton.setFocusable(false);
            actionsButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            toolBar.setBorder(new PlacardButtonBorder(SwingConstants.LEFT));
            toolBar.setMinimumSize(new Dimension(10, 23));
            splitPane.setDividerSize(1);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            splitPane.setForeground(new Color(0xa5a5a5));
        } else {
            tree.setBackground(new Color(0xd7e4f2));
            toolBar.setVisible(false);
        }
        tree.setSelectionModel(new DefaultUndoableTreeSelectionModel());
        tree.addTreeSelectionListener(
                new TreeSelectionListener() {

                    @Override
                    public void valueChanged(TreeSelectionEvent evt) {
                        treeSelectionChanged(evt);
                    }
                });
        updateAddButton();
    }

    /**
     * Sets the tree model.
     */
    public void setTreeModel(TreeModel m) {
        tree.setModel(m);
        updateAddButton();
    }

    /**
     * Expands all tree nodes, up to the
     * specified depth.
     *
     * @param depthLimit The depth limit.
     */
    public void expandAll(int depthLimit) {
        expandAll(depthLimit, Integer.MAX_VALUE);
    }

    /**
     * Expands all tree nodes, up to the
     * specified depth and maximal number
     * of children.
     *
     * @param depthLimit The depth limit.
     * @param childLimit The child count limit;
     */
    public void expandAll(int depthLimit, int childLimit) {
        TreeModel treeModel = getTreeModel();

        depthLimit += 2;
        for (int i = 0; i < tree.getRowCount(); i++) {
            TreePath path = tree.getPathForRow(i);
            if (path.getPathCount() < depthLimit) {
                Object node = path.getLastPathComponent();
                if (!treeModel.isLeaf(node) && treeModel.getChildCount(node) < childLimit) {
                    tree.expandRow(i);
                }
            }
        }
    }

    /**
     * Gets the tree model.
     */
    public TreeModel getTreeModel() {
        return tree.getModel();
    }

    /**
     * Gets the tree component.
     */
    public MutableJTree getTree() {
        return tree;
    }

    public void dispatch(Runnable r) {
        if (dispatcher == null) {
            dispatcher = Executors.newSingleThreadExecutor();
        }
        dispatcher.execute(r);
    }

    public void setUndoManager(UndoManager value) {
        if (undo != null) {
            ((UndoableTreeSelectionModel) tree.getSelectionModel()).removeUndoableEditListener(undo);
        }
        undo = value;
        if (undo != null) {
            ((UndoableTreeSelectionModel) tree.getSelectionModel()).addUndoableEditListener(undo);
        }
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        tree.setEnabled(b);
        scrollPane.setEnabled(b);
        addButton.setEnabled(b);
        if (view != null) {
            view.setEnabled(b);
        }
    }

    /**
     * Sets the viewer.
     * The viewer renders the currently selected
     * TreeNode or Array of TreeNodes in the
     * right pane of the explorer.
     */
    public void setViewer(Viewer v) {
        viewer = v;
    }

    /**
     * Gets the viewer.
     */
    public Viewer getViewer() {
        return viewer;
    }

    /** Sets the info panel. The info panel is displayed in a split pane under
     * the sidebar tree. If the info panel is set to null, the sidebar tree
     * is displayed without a split pane.
     */
    public void setInfoPanel(JComponent newValue) {
        if (infoPanel != null) {
            leftPane.remove(infoSplitPane);
            leftPane.add(scrollPane, BorderLayout.CENTER);
            infoSplitPane = null;
            scrollPane.revalidate();
        }
        infoPanel = newValue;
        if (infoPanel != null) {
            infoSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            leftPane.remove(scrollPane);
            infoSplitPane.setLeftComponent(scrollPane);
            infoSplitPane.setRightComponent(infoPanel);
            infoSplitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
            leftPane.add(infoSplitPane, BorderLayout.CENTER);
            infoPanel.revalidate();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        rightPane = new javax.swing.JPanel();
        leftPane = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tree = new ch.randelshofer.gui.JDnDTree();
        toolBar = new javax.swing.JToolBar();
        addButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        splitPane.setBorder(null);
        splitPane.setDividerLocation(196);

        rightPane.setMinimumSize(new java.awt.Dimension(0, 0));
        rightPane.setPreferredSize(new java.awt.Dimension(400, 400));
        rightPane.setLayout(new java.awt.BorderLayout());
        splitPane.setRightComponent(rightPane);

        leftPane.setLayout(new java.awt.BorderLayout());

        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollPane.setPreferredSize(new java.awt.Dimension(200, 400));

        tree.setEditable(true);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        scrollPane.setViewportView(tree);

        leftPane.add(scrollPane, java.awt.BorderLayout.CENTER);

        toolBar.setFloatable(false);

        addButton.setFocusable(false);
        addButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNode(evt);
            }
        });
        toolBar.add(addButton);

        leftPane.add(toolBar, java.awt.BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPane);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addNode(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNode
        tree.create();
    }//GEN-LAST:event_addNode

    protected void treeSelectionChanged(TreeSelectionEvent evt) {
        Component newView = null;

        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null || paths.length == 0) {
            newView = null;

        } else if (paths.length == 1) {
            Object value = paths[0].getLastPathComponent();
            viewedObject = value;
            newView = viewer.getComponent(JExplorer.this, viewedObject);
        } else {
            Object[] values = new Object[paths.length];
            for (int i = 0; i < paths.length; i++) {
                values[i] = paths[i].getLastPathComponent();
            }
            viewedObject = values;
            newView = viewer.getComponent(JExplorer.this, viewedObject);
        }

        if (newView == null) {
            newView = new JPanel();
        }
        if (newView != view) {
            if ((undo != null)
                    && (view instanceof Undoable)) {
                ((Undoable) view).removeUndoableEditListener(undo);
            }

            view = newView;
            rightPane.removeAll();
            rightPane.add(view);
            view.invalidate();
            rightPane.validate();
            rightPane.repaint();
            view.setEnabled(isEnabled());
        }

        if ((undo != null)
                && (view instanceof Undoable)) {
            ((Undoable) view).addUndoableEditListener(undo);
        }
        updateAddButton();
    }

    private void updateAddButton() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            path = new TreePath(tree.getModel().getRoot());
        }
        boolean enabled = false;
        if (tree.getModel() instanceof MutableTreeModel) {
            MutableTreeModel m = (MutableTreeModel) tree.getModel();
            enabled = null != m.getCreatableNodeType(m.isLeaf(path.getLastPathComponent()) ? path.getPathComponent(path.getPathCount() - 2) : path.getLastPathComponent());
        }
        addButton.setEnabled(enabled);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel leftPane;
    private javax.swing.JPanel rightPane;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JToolBar toolBar;
    private ch.randelshofer.gui.JDnDTree tree;
    // End of variables declaration//GEN-END:variables
}

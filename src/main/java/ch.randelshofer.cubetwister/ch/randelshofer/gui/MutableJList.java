/* @(#)MutableJList.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import ch.randelshofer.gui.list.DefaultMutableListModel;
import ch.randelshofer.gui.list.MutableListModel;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneLayout;
import java.awt.Container;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * A JList that uses a MutableTableModel. Users can add and remove elements
 * using a popup menu. MutableJList also supports the standard clipboard
 * operations cut, copy and paste.
 *
 * @author Werner Randelshofer
 */
public class MutableJList
extends JList
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
        public void lostOwnership(Clipboard clipboard, Transferable contents) {
            
        }
    }

    @Nonnull
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
     * Constructs a MutableJList with an empty DefaultMutableListModel.
     */
    public MutableJList() {
        super(new DefaultMutableListModel<Object>());
        init();
    }
    
    /**
     * Constructs a MutableJList with the specified MutableListModel.
     */
    public MutableJList(@Nonnull MutableListModel m) {
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
            public void mousePressed(MouseEvent evt) {
                if (isEnabled())
                    createPopup().show(popupButton, 0, popupButton.getHeight());
            }
        }
        );
        
        // The popup listener provides an alternative way for
        // opening the popup menu.
        popupListener = new MouseAdapter() {
            public void mousePressed(@Nonnull MouseEvent evt) {
                if (isEnabled() && evt.isPopupTrigger()) {
                    createPopup().show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            public void mouseReleased(@Nonnull MouseEvent evt) {
                if (isEnabled() && evt.isPopupTrigger()) {
                    createPopup().show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        };
        addMouseListener(popupListener);

        // All locale specific and LAF specific
        // labels are read from a resource bundle.
        initLabels(Locale.getDefault());
    }

    /**
     * Initializes the labels in a locale specific and
     * look-and-feel (LAF) specific way.
     */
    private void initLabels(@Nonnull Locale locale) {
        // remove previously installed key strokes
        KeyStroke keyStroke;
        if (labels != null) {
            if (null != (keyStroke = labels.getKeyStroke("edit.new.accelerator"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("editDuplicateAcc"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("editCutAcc"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("editCopyAcc"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("editPasteAcc"))) {
                unregisterKeyboardAction(keyStroke);
            }
            if (null != (keyStroke = labels.getKeyStroke("editDeleteAcc"))) {
                unregisterKeyboardAction(keyStroke);
            }
        }
        
        // get the locale and LAF specific resources
        labels =new ResourceBundleUtil(ResourceBundle.getBundle(
        "ch.randelshofer.gui.Labels", locale
        ));
        
        // install key strokes
        if (labels != null) {
            if (null != (keyStroke = labels.getKeyStroke("edit.new.accelerator"))) {
                registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) create();
                    }
                },
                keyStroke,
                WHEN_FOCUSED
                );
            }
            
            if (null != (keyStroke = labels.getKeyStroke("edit.duplicate.accelerator"))) {
                registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) duplicate();
                    }
                },
                keyStroke,
                WHEN_FOCUSED
                );
            }
            
            if (null != (keyStroke = labels.getKeyStroke("edit.cut.accelerator"))) {
                registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) cut();
                    }
                },
                keyStroke,
                WHEN_FOCUSED
                );
            }
            
            if (null != (keyStroke = labels.getKeyStroke("edit.copy.accelerator"))) {
                registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) copy();
                    }
                },
                keyStroke,
                WHEN_FOCUSED
                );
            }
            
            if (null != (keyStroke = labels.getKeyStroke("edit.paste.accelerator"))) {
                registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) paste();
                    }
                },
                keyStroke,
                WHEN_FOCUSED
                );
            }
            
            if (null != (keyStroke = labels.getKeyStroke("edit.delete.accelerator"))) {
                registerKeyboardAction(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (isEnabled()) delete();
                    }
                },
                keyStroke,
                WHEN_FOCUSED
                );
            }
        }
    }
    /**
     * Creates the popup menu. The contents of the popup menu
     * is determined by the current selection.
     *
     * @return The popup menu.
     */
    @Nonnull
    protected JPopupMenu createPopup() {
        final int[] selectedRows = getSelectedIndices();
        int leadSelectionRow = (selectedRows.length == 0) ? -1 : getSelectionModel().getLeadSelectionIndex();
        
        final MutableListModel model = (MutableListModel) getModel();
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem item;
        boolean b;
        
        // add the "New Row" menu item.
        final int newRow = (leadSelectionRow == -1) ? model.getSize() : leadSelectionRow + 1;
        Object[] types = model.getCreatableTypes(newRow);
        Object defaultType = model.getCreatableType(newRow);
        for (int i = 0; i < types.length; i++) {
            final Object newRowType = types[i];
            item = new JMenuItem(labels.getFormatted("edit.new.text", new Object[] {newRowType}));
            if (newRowType.equals(defaultType)) {
                item.setMnemonic(labels.getMnemonic("edit.new.mnemonic"));
                if (labels.getKeyStroke("edit.new.accelerator") != null)
                    item.setAccelerator(labels.getKeyStroke("edit.new.accelerator"));
            }
            item.setIcon(labels.getSmallIconProperty("edit.new", getClass()));
            item.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    model.create(newRow, newRowType);
                }
            }
            );
            item.setEnabled(model.isAddable(newRow));
            popup.add(item);
        }
        
        
        // add the "Cut" menu item.
        item = new JMenuItem(labels.getString("edit.cut.text"));
        item.setMnemonic(labels.getMnemonic("edit.cut.mnemonic"));
        if (labels.getKeyStroke("edit.cut.acc") != null)
            item.setAccelerator(labels.getKeyStroke("edit.cut.accelerator"));
        item.setIcon(labels.getSmallIconProperty("edit.cut", getClass()));
        item.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                cut();
            }
        }
        );
        b = selectedRows.length > 0;
        for (int i = 0; i < selectedRows.length; i++) {
            if (! model.isRemovable(selectedRows[i])) {
                b = false;
                break;
            }
        }
        item.setEnabled(b);
        popup.add(item);
        
        
        // add the "Copy" menu item.
        item = new JMenuItem(labels.getString("edit.copy.text"));
        item.setMnemonic(labels.getMnemonic("edit.copy.mnemonic"));
        if (labels.getKeyStroke("edit.copy.accelerator") != null)
            item.setAccelerator(labels.getKeyStroke("edit.copy.accelerator"));
        item.setIcon(labels.getSmallIconProperty("edit.copy", getClass()));
        item.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                copy();
            }
        }
        );
        item.setEnabled(selectedRows.length > 0);
        popup.add(item);
        
        
        // add the "Paste" menu item.
        item = new JMenuItem(labels.getString("edit.pasteMenu"));
        item.setMnemonic(labels.getMnemonic("edit.paste.mnemonic"));
        if (labels.getKeyStroke("edit.paste.accelerator") != null)
            item.setAccelerator(labels.getKeyStroke("edit.paste.accelerator"));
        item.setIcon(labels.getSmallIconProperty("edit.paste", getClass()));
        item.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                paste();
            }
        }
        );
        b = true;
        for (int i = 0; i < selectedRows.length; i++) {
            if (! model.isRemovable(selectedRows[i])) {
                b = false;
                break;
            }
        }
        item.setEnabled(b || selectedRows.length == 0);
        popup.add(item);
        
        
        if (leadSelectionRow != -1) {
            // Add the duplicate row menu item
            item = new JMenuItem(labels.getString("edit.duplicate.menu"));
            item.setMnemonic(labels.getMnemonic("edit.duplicate.mnemonic"));
            if (labels.getKeyStroke("edit.duplicate.accelerator") != null)
                item.setAccelerator(labels.getKeyStroke("edit.duplicate.accelerator"));
            item.setIcon(labels.getSmallIconProperty("edit.duplicate", getClass()));
            boolean allowed = true;
            for (int i=0; i < selectedRows.length; i++) {
                if (! model.isRemovable(i) || ! model.isAddable(i)) {
                    allowed = false;
                    break;
                }
            }
            item.setEnabled(allowed);
            item.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    //if (isEditing())
                    //    getCellEditor().stopCellEditing();
                    Transferable t = model.exportTransferable(selectedRows);
                    try {
                        model.importTransferable(t, DnDConstants.ACTION_COPY, (selectedRows.length == 0) ? -1 : selectedRows[0], false);
                    } catch (Exception e) {
                        throw new InternalError(e.getMessage()); // should never happen
                    }
                }
            }
            );
            popup.add(item);
            
            
            // add the "Delete" menu item.
            item = new JMenuItem(labels.getString("edit.delete.menu"));
            item.setMnemonic(labels.getMnemonic("edit.delete.mnem"));
            if (labels.getKeyStroke("edit.delete.accelerator") != null)
                item.setAccelerator(labels.getKeyStroke("edit.delete.accelerator"));
            item.setIcon(labels.getSmallIconProperty("edit.delete", getClass()));
            allowed = true;
            for (int i=0; i < selectedRows.length; i++) {
                if (! model.isRemovable(i)) {
                    allowed = false;
                    break;
                }
            }
            item.setEnabled(allowed);
            item.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    //if (isEditing())
                    //    getCellEditor().stopCellEditing();
                    for (int i=0; i < selectedRows.length; i++) {
                        model.remove(selectedRows[i] - i);
                    }
                }
            }
            );
            popup.add(item);
        }
        
        // add the "Select All" menu item
        item = new JMenuItem(labels.getString("edit.selectAll.text"));
        item.setMnemonic(labels.getMnemonic("edit.selectAll.mnemonic"));
        if (labels.getKeyStroke("edit.selectAll.accelerator") != null)
            item.setAccelerator(labels.getKeyStroke("edit.selectAll.accelerator"));
        item.setIcon(labels.getSmallIconProperty("edit.selectAll", getClass()));
        item.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setSelectionInterval(0, getModel().getSize());
            }
        }
        );
        item.setEnabled(true);
        popup.add(item);
        
        
        if (leadSelectionRow != -1) {
            // Add actions provided by the MutableTableModel
            HashMap<String,JMenu> menus = new HashMap<String,JMenu>();
            Action[] actions = model.getActions(selectedRows);
            if (actions != null) {
                for (int j = 0; j < actions.length; j++) {
                    String menuName = (String) actions[j].getValue("Menu");
                    if (menuName != null) {
                        if (menus.get(menuName) == null) {
                            JMenu m = new JMenu(menuName);
                            popup.add(m);
                            menus.put(menuName, m);
                        }
                        menus.get(actions[j].getValue("Menu")).add(actions[j]);
                    } else {
                        popup.add(actions[j]);
                    }
                }
            }
        }
        
        return popup;
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
    protected void create() {
        MutableListModel model = (MutableListModel) getModel();
        int row = getSelectionModel().getLeadSelectionIndex() + 1;
        if (row == 0 || row > model.getSize()) {
            row = model.getSize();
        }
        if (model.getCreatableTypes(row).length == 1) {
            if (model.isAddable(row)) {
                model.create(row, model.getCreatableTypes(row)[0]);
                getSelectionModel().setSelectionInterval(row, row);
            }
        }
    }
    /** Cuts the selected region and place its contents into the system clipboard.
     */
    public void cut() {
        final int[] selectedRows = getSelectedIndices();
        MutableListModel m = (MutableListModel) getModel();
        for (int i=0; i < selectedRows.length; i++) {
            if (! m.isRemovable(selectedRows[i])) {
                getToolkit().beep();
                return;
            }
        }
        
        getToolkit().getSystemClipboard().setContents(
        m.exportTransferable(selectedRows),
        eventHandler
        );
        
        for (int i=selectedRows.length - 1; i > -1; i--) {
            m.remove(selectedRows[i]);
        }
    }
    
    /**
     * Copies the selected region and place its contents into
     * the system clipboard.
     */
    public void copy() {
        final int[] selectedRows = getSelectedIndices();
        MutableListModel m = (MutableListModel) getModel();
        getToolkit().getSystemClipboard().setContents(
        m.exportTransferable(selectedRows),
        eventHandler
        );
    }
    
    /** Pastes the contents of the system clipboard at the caret position.
     */
    public void paste() {
        int row = getSelectionModel().getLeadSelectionIndex() + 1;
        MutableListModel m = (MutableListModel) getModel();
        if (row == 0 || row > m.getSize()) {
            row = m.getSize();
        }
        
        if (m.isAddable(row)) {
            try {
                m.importTransferable(
                getToolkit().getSystemClipboard().getContents(this),
                DnDConstants.ACTION_COPY,
                row,
                false
                );
            } catch (Exception e) {
                getToolkit().beep();
            }
        } else {
            getToolkit().beep();
        }
    }
    /** Deletes the component at (or after) the caret position.
     */
    public void delete() {
        MutableListModel model = (MutableListModel) getModel();
        int rows[] = getSelectedIndices();
        int i;
        for (i = 0; i < rows.length; i++) {
            if (! model.isRemovable(rows[i])) {
                break;
            }
        }
        if (i == rows.length) {
            for (i = 0; i < rows.length; i++) {
                model.remove(rows[i] - i);
            }
        }
    }
    
    
    /** Duplicates the selected region.
     */
    public void duplicate() {
        int[] selectedRows = getSelectedIndices();
        if (selectedRows.length > 0) {
            MutableListModel m = (MutableListModel) getModel();
            
            int row = getSelectionModel().getLeadSelectionIndex() + 1;
            if (m.isAddable(row)) {
                try {
                    m.importTransferable(
                    m.exportTransferable(selectedRows),
                    DnDConstants.ACTION_COPY,
                    row,
                    false
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    getToolkit().beep();
                }
            } else {
                getToolkit().beep();
            }
        }
    }
    
    /**
     * Calls the configureEnclosingScrollPane method.
     *
     * @see #configureEnclosingScrollPane()
     */
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
                JScrollPane scrollPane = (JScrollPane)gp;
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
                    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, popupButton);
                }
            }
        }
    }
    
    /**
     * Calls the unconfigureEnclosingScrollPane method.
     *
     * @see #unconfigureEnclosingScrollPane()
     */
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
                JScrollPane scrollPane = (JScrollPane)gp;
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
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        popupButton.setEnabled(b);
    }

    public void selectAll() {
        if (getModel().getSize() > 0) {
        getSelectionModel().setSelectionInterval(0, getModel().getSize() - 1);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}

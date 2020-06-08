/*
 * @(#)MutableJTable.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui;

import ch.randelshofer.gui.table.DefaultMutableTableModel;
import ch.randelshofer.gui.table.MutableTableModel;
import ch.randelshofer.gui.table.MutableTableTransferHandler;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import java.awt.Container;
import java.awt.Font;
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
 * A JTable that uses a MutableTableModel. Users can add and remove rows
 * using a popup menu. MutableJTree also supports the standard clipboard
 * operations cut, copy and paste.
 *
 * @author Werner Randelshofer
 */
public class MutableJTable
        extends JTable
        implements EditableComponent {
    private final static long serialVersionUID = 1L;
    //private boolean isStriped;
    //private Color alternateColor = new Color(237, 243, 254);


    private final static boolean VERBOSE = true;
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
     * Creates a MutableJTable with a sample model.
     */
    public MutableJTable() {
        super(new DefaultMutableTableModel());
        init();
    }
    /**
     * Creates a MutableJTable with the provided model.
     */
    public MutableJTable(MutableTableModel model) {
        super(model);
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

        setTransferHandler(new MutableTableTransferHandler());
    }

    public JButton getPopupButton() {
        return popupButton;
    }

    /**
     * Initializes the labels in a locale specific way.
     */
    private void initLabels(@Nonnull Locale locale) {
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
        labels = new ResourceBundleUtil(ResourceBundle.getBundle(
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
                registerKeyboardAction(new CutAction(this),
                        keyStroke,
                        WHEN_FOCUSED
                        );
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.copy.accelerator"))) {
                registerKeyboardAction(new CopyAction(this),
                        keyStroke,
                        WHEN_FOCUSED
                        );
            }

            if (null != (keyStroke = labels.getKeyStroke("edit.paste.accelerator"))) {
                registerKeyboardAction(new PasteAction(this),
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
     * Configures the enclosing scroll pane.
     */
    protected void configureEnclosingScrollPane() {
        super.configureEnclosingScrollPane();

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
                    // Install a mouse listener for the popup menu
                    viewport.addMouseListener(popupListener);

                    // Install a popup button at the top right corner
                    // of the JScrollPane
                    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, popupButton);
                }
            }
        }
    }
    /**
     * This method creates a new popup menu.
     *
     * @return The popup menu.
     */
    @Nonnull
    protected JPopupMenu createPopup() {
        final int[] selectedRows = getSelectedRows();
        int leadSelectionRow = (selectedRows.length == 0) ? -1 : getSelectionModel().getLeadSelectionIndex();


        final MutableTableModel model = (MutableTableModel) getModel();
        JPopupMenu popup = new JPopupMenu();
        JMenuItem item;

        // add the "New Row" menu item.
        final int newRow = (leadSelectionRow == -1) ? getRowCount() : leadSelectionRow + 1;
        Object[] types = model.getCreatableRowTypes(newRow);
        for (int i = 0; i < types.length; i++) {
            final Object newRowType = types[i];
            item = new JMenuItem(labels.getFormatted("edit.new.text", new Object[] {newRowType}));
            item.setMnemonic(labels.getMnemonic("edit.new.mnemonic"));
            if (labels.getKeyStroke("edit.new.accelerator") != null)
                item.setAccelerator(labels.getKeyStroke("edit.new.accelerator"));
            item.setIcon(labels.getSmallIconProperty("edit.newIcon", getClass()));
            item.addActionListener(
                    new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    if (isEditing())
                        getCellEditor().stopCellEditing();
                    model.createRow(newRow, newRowType);
                }
            }
            );
            //item.setEnabled(model.isRowAddable(newRow));
            popup.add(item);
        }
        if (popup.getComponentCount() > 0) popup.addSeparator();

        // add the "Cut" menu item.
        item = new JMenuItem(labels.getString("edit.cut.text"));
        item.setMnemonic(labels.getMnemonic("edit.cut.mnemonic"));
        if (labels.getKeyStroke("edit.cut.accelerator") != null)
            item.setAccelerator(labels.getKeyStroke("edit.cut.accelerator"));
        item.setIcon(labels.getSmallIconProperty("edit.cutIcon", getClass()));
        item.addActionListener(new CutAction(this)
        );
        boolean b = selectedRows.length > 0;
        for (int i = 0; i < selectedRows.length; i++) {
            if (! model.isRowRemovable(selectedRows[i])) {
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
        item.setIcon(labels.getSmallIconProperty("edit.copyIcon", getClass()));
        item.addActionListener(new CopyAction(this));
        item.setEnabled(selectedRows.length > 0);
        popup.add(item);


        // add the "Paste" menu item.
        item = new JMenuItem(labels.getString("edit.paste.text"));
        item.setMnemonic(labels.getMnemonic("edit.paste.mnemonic"));
        if (labels.getKeyStroke("edit.paste.accelerator") != null)
            item.setAccelerator(labels.getKeyStroke("edit.paste.accelerator"));
        item.setIcon(labels.getSmallIconProperty("edit.pasteIcon", getClass()));
        item.addActionListener(new PasteAction(this));

        // Enable paste, if the lead selection row allows insertion
        b = model.isRowAddable(leadSelectionRow);
        // Disable paste, if at least one of the selected rows is not removeable
        for (int i = 0; i < selectedRows.length; i++) {
            if (! model.isRowRemovable(selectedRows[i])) {
                b = false;
                break;
            }
        }
        // Enable paste, if at least one cell is editable
        if (! b) {
            for (int i = 0; i < selectedRows.length; i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if (! model.isCellEditable(i, j)) {
                        b = true;
                        break;
                    }
                }
            }
        }
        item.setEnabled(b);
        popup.add(item);


        if (leadSelectionRow != -1) {
            // Add the duplicate row menu item
            item = new JMenuItem(labels.getString("edit.duplicate.text"));
            item.setMnemonic(labels.getMnemonic("edit.duplicate.mnemonic"));
            if (labels.getKeyStroke("edit.duplicate.accelerator") != null)
                item.setAccelerator(labels.getKeyStroke("edit.duplicate.accelerator"));
            item.setIcon(labels.getSmallIconProperty("edit.duplicateIcon", getClass()));
            boolean allowed = true;
            for (int i=0; i < selectedRows.length; i++) {
                if (! model.isRowRemovable(i) || ! model.isRowAddable(i)) {
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
                    Transferable t = model.exportRowTransferable(selectedRows);
                    try {
                        model.importRowTransferable(t, DnDConstants.ACTION_COPY, (selectedRows.length == 0) ? -1 : selectedRows[0], false);
                    } catch (Exception e) {
                        throw new InternalError(e.getMessage()); // should never happen
                    }
                }
            }
            );
            popup.add(item);


            // add the "Delete" menu item.
            item = new JMenuItem(labels.getString("edit.delete.text"));
            item.setMnemonic(labels.getMnemonic("edit.delete.mnemonic"));
            if (labels.getKeyStroke("edit.delete.accelerator") != null)
                item.setAccelerator(labels.getKeyStroke("edit.delete.accelerator"));
            item.setIcon(labels.getSmallIconProperty("edit.deleteIcon", getClass()));
            allowed = true;
            for (int i=0; i < selectedRows.length; i++) {
                if (! model.isRowRemovable(i)) {
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
                        model.removeRow(selectedRows[i] - i);
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
        item.setIcon(labels.getSmallIconProperty("edit.selectAllIcon", getClass()));
        item.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (getModel().getRowCount() > 0) {
                    setRowSelectionInterval(0, getModel().getRowCount() - 1);
                }
            }
        }
        );
        item.setEnabled(true);
        popup.add(item);


        if (leadSelectionRow != -1) {
            // Add actions provided by the MutableTableModel
            HashMap<String,JMenu> menus = new HashMap<String,JMenu>();
            Action[] actions = model.getRowActions(selectedRows);
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

    /**
     * Unconfigures the enclosing scroll pane.
     */
    protected void unconfigureEnclosingScrollPane() {
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
                    // Remove the previously installed mouse listener for the popup menu
                    viewport.removeMouseListener(popupListener);

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

        super.unconfigureEnclosingScrollPane();
    }

    /**
     * Sets the locale of this component.
     *
     * @param l The locale to become this component's locale.
     */
    public void setLocale(@Nonnull Locale l) {
        super.setLocale(l);
        initLabels(l);
    }

    public void updateUI() {
        super.updateUI();
        if ("MacOS".equals(UIManager.getLookAndFeel().getName())) {
            getTableHeader().setFont(new Font("Lucida Grande", Font.PLAIN, 11));
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
    protected void create() {
        if (isEditing())
            getCellEditor().stopCellEditing();
        MutableTableModel model = (MutableTableModel) getModel();
        int row = getSelectionModel().getLeadSelectionIndex() + 1;
        if (row == 0 || row > model.getRowCount()) {
            row = model.getRowCount();
        }
        if (model.getCreatableRowType(row) != null) {
            if (model.isRowAddable(row)) {
                Object rowType = model.getCreatableRowType(row);
                model.createRow(row, rowType);
                getSelectionModel().setSelectionInterval(row, row);
            }
        }
    }
    /** Deletes the component at (or after) the caret position.
     */
    @Override
    public void delete() {
        if (isEditing())
            getCellEditor().stopCellEditing();
        MutableTableModel model = (MutableTableModel) getModel();
        int rows[] = getSelectedRows();
        int i;
        for (i = 0; i < rows.length; i++) {
            if (! model.isRowRemovable(rows[i])) {
                break;
            }
        }
        if (i == rows.length) {
            for (i = 0; i < rows.length; i++) {
                model.removeRow(rows[i] - i);
            }
        }
    }

    /** Duplicates the selected region.
     */
    @Override
    public void duplicate() {
        int[] selectedRows = getSelectedRows();
        if (selectedRows.length > 0) {
            MutableTableModel m = (MutableTableModel) getModel();

            int row = getSelectionModel().getLeadSelectionIndex() + 1;
            if (m.isRowAddable(row)) {
                try {
                    m.importRowTransferable(
                            m.exportRowTransferable(selectedRows),
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
     * Sets the enabled state of the component.
     */
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        popupButton.setEnabled(b);
    }

    @Override
    public boolean isSelectionEmpty() {
        return getSelectionModel().isSelectionEmpty();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

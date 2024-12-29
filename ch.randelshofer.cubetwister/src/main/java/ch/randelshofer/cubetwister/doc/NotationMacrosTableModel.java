/*
 * @(#)NotationMacrosTableModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.datatransfer.CompositeTransferable;
import ch.randelshofer.gui.table.TableModels;
import ch.randelshofer.gui.table.TreeNodeTableModel;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.Normalizer;
/**
 * Wraps the macros provided by NotationModel to make them
 * accessible by a MutableJTable.
 *
 * @author  Werner Randelshofer
 */
public class NotationMacrosTableModel
        extends TreeNodeTableModel
        implements PropertyChangeListener {
    private final static long serialVersionUID = 1L;
    private final static String[] columnNames = {"Identifiers", "Script", "Description"};
    /** The data flavour for JVM local object transferables. */
    private final static DataFlavor tableFlavor = new DataFlavor(Object[][].class, "Table");
    private final static DataFlavor rowFlavor = new DataFlavor(Object[].class, "Row");
    /**
     * Array with importable data flavors.
     */
    private final static DataFlavor[] importableFlavors = {
        tableFlavor,
        DataFlavor.stringFlavor,
        DataFlavor.getTextPlainUnicodeFlavor()
    };
    @Nullable
    private NotationModel model;

    public void setModel(@Nullable NotationModel m) {
        if (treeNode != null && treeNode.getParent() != null) {
            ((EntityModel) treeNode.getParent()).removePropertyChangeListener(this);
        }

        this.model = m;

        if (m == null) {
            setModel(null, null);
        } else {
            setModel(m.getDocument(), m.getMacroModels());
            m.addPropertyChangeListener(this);
        }
    }

    @Nullable
    public NotationModel getModel() {
        return model;
    }

    @Override
    public void propertyChange(@Nonnull PropertyChangeEvent evt) {
        if (evt.getSource() == treeNode && evt.getPropertyName() == ScriptModel.NOTATION_PROPERTY) {
            fireTableDataChanged();
        }
    }

    /**
     * Invoke this to insert a new row into the table.
     *
     * @param   row      index of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     */
    public void insertRow(Object type, int row) {
        MacroModel item = new MacroModel();
        item.setIdentifier("unnamed Macro");
        ((DocumentModel) treeModel).insertNodeInto(item, treeNode, row);
    }
    /**
     * Returns the value for the cell at <I>columnIndex</I> and <I>rowIndex</I>.
     *
     * @param	row	the row whose value is to be looked up
     * @param	column 	the column whose value is to be looked up
     * @return	the value Object at the specified cell
     */
    @Nullable
    @Override
    public Object getValueAt(int row, int column) {
        MacroModel item = (MacroModel) treeNode.getChildAt(row);

        switch (column) {
            case 0 : return item.getIdentifier();
            case 1 : return item.getScript();
            case 2 : return item.getDescription();
        }
        return null; // should never happen
    }

    /**
     * Returns the name of the column at <i>columnIndex</i>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	columnIndex	the index of column
     * @return  the name of the column
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
    /**
     * Invoke this to insert a new row into the table.
     *
     * @param   index      index of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     * /
     * public void insertRow(Object type, int row) {
     * MacroModel item = new MacroModel();
     * item.setIdentifier("Macro");
     * ((DocumentModel) treeModel).insertNodeInto(item, treeNode, row);
     * }*/

    /**
     * Returns true if the cell at <I>rowIndex</I> and <I>columnIndex</I>
     * is editable.  Otherwise, setValueAt() on the cell will not change
     * the value of that cell.
     *
     * @param	row	the row whose value is to be looked up
     * @param	column	the column whose value is to be looked up
     * @return	true if the cell is editable.
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /**
     * Sets the value in the cell at <I>columnIndex</I> and <I>rowIndex</I> to
     * <I>aValue</I> is the new value.
     *
     * @param    aValue         the new value
     * @param    row     the row whose value is to be changed
     * @param    column     the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    @Override
    public void setValueAt(@Nullable Object aValue, int row, int column) {
        MacroModel item = (MacroModel) treeNode.getChildAt(row);

        String str = (aValue == null) ? null : aValue.toString();
        switch (column) {
            case 0:
                item.setIdentifier(Normalizer.normalize(str, Normalizer.Form.NFC));
                break;
            case 1:
                item.setScript(Normalizer.normalize(str, Normalizer.Form.NFC));
                break;
            case 2:
                item.setDescription(str);
                break;
        }
    }

    /**
     * Returns the number of columns in the model. A
     * <B>JTable</B> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number or columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Nonnull
    @Override
    public Object[] getCreatableRowTypes(int row) {
        return new Object[] { "Macro" };
    }

    @Nonnull
    @Override
    public Object getCreatableRowType(int row) {
        return "Macro";
    }
    /**
     * Invoke this to insert a new row into the table.
     *
     * @param   row      index of the new row.
     * @exception   IllegalStateException if the row may not be inserted.
     */
    @Override
    public void createRow(int row, Object type) {
        MacroModel item = new MacroModel();
        item.setIdentifier("unnamed");
        ((DocumentModel) treeModel).insertNodeInto(item, treeNode, row);
    }

    // Datatransfer operations
    // =======================

    /**
     * Creates a Transferable to use as the source for a data
     * transfer of the specified elements.
     * Returns the representation of the rows
     * to be transferred, or null if transfer is not possible.
     *
     * @param rows Row indices.
     */
    @Nonnull
    @Override
    public Transferable exportRowTransferable(@Nonnull int[] rows) {
        CompositeTransferable t = new CompositeTransferable();
        t.add(TableModels.createLocalTransferable(this, rows));
        t.add(TableModels.createHTMLTransferable(this, rows));
        t.add(TableModels.createPlainTransferable(this, rows));
        return t;
    }

    /**
     * Causes a transfer to the model from a clipboard or
     * a DND drop operation.
     *
     * @param   t        The transfer dataList.
     * @param   action    The action DnDConstants.ACTION_COPY, .ACTION_MOVE or
     *                  .ACTION_LINK.
     * @param   row    The insertion point. 0 &lt;= index &lt;= getSize()
     * @param   asChild  Indicates that the item is to be imported as a child
     *                   of the list item rather than as an individual element
     *                   of the list.
     *
     * @return The number of imported elements.
     */
    @Override
    public int importRowTransferable(@Nonnull Transferable t, int action, int row, boolean asChild)
            throws UnsupportedFlavorException, IOException {
        if (!isRowImportable(t.getTransferDataFlavors(), action, row, asChild)) {
            throw new UnsupportedFlavorException(null);
        }

        Object[][] transferData;
        try {
            if (t.isDataFlavorSupported(tableFlavor)) {
                transferData = (Object[][]) t.getTransferData(tableFlavor);
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                transferData = TableModels.getStringTable(t, getColumnCount());
            } else if (t.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
                transferData = TableModels.getPlainTable(t, getColumnCount());
            } else {
                throw new UnsupportedFlavorException(tableFlavor);
            }

            DefaultMutableTreeNode macros = model.getMacroModels();
            for (int i=0; i < transferData.length; i++) {
                MacroModel item = new MacroModel();
                macros.insert(item,row++);
                for (int j=0; j < transferData[i].length; j++) {
                    String value;
                    if (transferData[i][j] instanceof String) {
                        value = (String) transferData[i][j];
                    } else {
                        value = null;
                    }
                    if (value != null) {
                    switch (j) {
                        case 0 : item.setIdentifier(value); break;
                        case 1 : item.setScript(value); break;
                        case 2 : item.setDescription(value); break;
                    }
                    }
                }


            }
            //justifyRows(row, row + transferData.length - 1);
            fireTableRowsInserted(row, row + transferData.length);

        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return transferData.length;
    }

    @Override
    public boolean isRowImportable(@Nonnull DataFlavor[] transferFlavors, int action, int row, boolean asChild) {
        if (action == DnDConstants.ACTION_LINK || asChild || !isRowAddable(row)) {
            return false;
        }

        for (int i = 0; i < transferFlavors.length; i++) {
            for (int j = 0; j < importableFlavors.length; j++) {
                if (transferFlavors[i].equals(importableFlavors[j])) {
                    return true;
                }
            }
        }
        return false;
    }
}

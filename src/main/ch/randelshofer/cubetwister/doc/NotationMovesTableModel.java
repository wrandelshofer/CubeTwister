/*
 * @(#)NotationMovesTableModel.java  1.1.2  2010-04-05
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.datatransfer.*;
import ch.randelshofer.gui.table.*;
import ch.randelshofer.rubik.parser.Move;
import ch.randelshofer.util.*;
import java.awt.datatransfer.*;
import java.awt.dnd.DnDConstants;
import java.beans.*;
import java.io.IOException;
import java.text.Normalizer;
import javax.swing.*;
import javax.swing.table.*;
/**
 * NotationMovesTableModel.
 * 
 * @author Werner Randelshofer.
 * @version 1.1.2 2010-04-05 Removed dependencies to IBM Unicode normalizer.
 * <br>1.1.1 2010-02-27 Fixed row change event in method importRowTransferable.
 * <br>1.1 2009-01-24 Normalize tokens to Unicode NFKC.
 * <br>1.0 April 15, 2006 Created.
 */
public class NotationMovesTableModel extends AbstractTableModel
        implements MutableTableModel, PropertyChangeListener {
    private final static long serialVersionUID = 1L;
    private static String[] columnNames;
    private static Class<?>[] columnClasses;
    private NotationModel model;
    
    /**
     * [][0] = Boolean "Supported"
     * [][1] = Layers String representation
     * [][2] = Angle String representation
     * [][3..8] = Move.
     */
    private Object[][] backboneRows;
    
    private int rowCount;
    
    
    /** Creates a new instance. */
    public NotationMovesTableModel() {
        if (columnNames == null) {
            columnNames = new String[] {
                "Supported","Layers","Angle","R","U","F","L","D","B"
            };
            columnClasses = new Class<?>[] {
                Boolean.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class, String.class
            };
        }
        setModel(null);
    }
    
    public void setModel(NotationModel newValue) {
        if (model != null) {
            model.removePropertyChangeListener(this);
        }
        model = newValue;
        if (model != null) {
            model.addPropertyChangeListener(this);
            updateRows();
        } else {
            backboneRows = new Object[0][0];
        }
            fireTableDataChanged();
    }
    
    private void updateRows() {
        int layerCount = model.getLayerCount();
        backboneRows = new Object[NotationModel.getUsefulLayers(layerCount).length * 4][9];
        rowCount = 0;
        for (int i=0, n = NotationModel.getUsefulLayers(layerCount).length; i < n; i++) {
            int layerMask = NotationModel.getUsefulLayers(layerCount)[i];
            backboneRows[rowCount][0] = Boolean.FALSE;
            String layers = "000000"+Integer.toBinaryString(layerMask);
            layers = layers.substring(layers.length() - layerCount);
            int reverseLayerMask = Integer.valueOf(Strings.reverse(layers), 2).intValue();
            layers = Strings.translate(layers, "01", "\u25cb\u25cf"); // unicode white circle and unicode black circle
            
            if (layerMask == reverseLayerMask) {
                for (int j=0; j < 2; j++) {
                    backboneRows[rowCount][1] = layers;
                    backboneRows[rowCount][2] = (j == 0) ? "+90°" : "+180°";
                    int angle = (j == 0) ? 1 : 2;
                    backboneRows[rowCount][3] = new Move(0, reverseLayerMask, angle);
                    backboneRows[rowCount][4] = new Move(1, reverseLayerMask, angle);
                    backboneRows[rowCount][5] = new Move(2, reverseLayerMask, angle);
                    backboneRows[rowCount][6] = new Move(0, layerMask, -angle);
                    backboneRows[rowCount][7] = new Move(1, layerMask, -angle);
                    backboneRows[rowCount][8] = new Move(2, layerMask, -angle);
                    rowCount++;
                }
            } else {
                for (int j=0; j < 4; j++) {
                    backboneRows[rowCount][1] = layers;
                    int angle;
                    switch (j) {
                        case 0 :
                        default :
                            backboneRows[rowCount][2] = "+90°";
                            angle = 1;
                            break;
                        case 1 :
                            backboneRows[rowCount][2] = "-90°";
                            angle = -1;
                            break;
                        case 2 :
                            backboneRows[rowCount][2] = "+180°";
                            angle = 2;
                            break;
                        case 3 :
                            backboneRows[rowCount][2] = "-180°";
                            angle = -2;
                            break;
                    }
                    backboneRows[rowCount][3] = new Move(0, reverseLayerMask, angle);
                    backboneRows[rowCount][4] = new Move(1, reverseLayerMask, angle);
                    backboneRows[rowCount][5] = new Move(2, reverseLayerMask, angle);
                    backboneRows[rowCount][6] = new Move(0, layerMask, -angle);
                    backboneRows[rowCount][7] = new Move(1, layerMask, -angle);
                    backboneRows[rowCount][8] = new Move(2, layerMask, -angle);
                    rowCount++;
                }
            }
        }
    }
    
    
    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return Boolean.valueOf(model.isTwistSupported((Move) backboneRows[row][3]));
        } else if (column == 1 || column == 2) {
            return backboneRows[row][column];
        } else {
            String tt = model.getMoveToken((Move) backboneRows[row][column]);
            return (tt == null) ? "" : tt;
        }
    }
    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == 0) {
            boolean b = false;
            if (value instanceof Boolean) {
                b = ((Boolean) value).booleanValue();
            } else if (value instanceof String) {
                b = "true".equals(value);
            }
            
            for (int i=3; i < 9; i++) {
                model.setMoveSupported((Move) backboneRows[row][i], b);
            }
        } else if (column > 2) {
            model.setMoveToken((Move) backboneRows[row][column],//
                    (value == null) ? null : Normalizer.normalize((String) value, Normalizer.Form.NFC)//
                    );
        }
    }
    
    @Override
    public int getRowCount() {
        return rowCount;
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return columnClasses[column];
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 || columnIndex > 2;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if ("layerCount".equals(name)) {
            updateRows();
            fireTableDataChanged();
        } else if ("twistSupported".equals(name)) {
            for (int i=0, n = getRowCount(); i < n; i++) {
                fireTableCellUpdated(i, 0);
            }
        } else if ("twistToken".equals(name)) {
            for (int i=0, n = getRowCount(); i < n; i++) {
                for (int j=3; j < 9; j++) {
                    fireTableCellUpdated(i, j);
                }
            }
        }
    }
    
    @Override
    public boolean isRowImportable(DataFlavor[] transferFlavors, int action, int row, boolean asChild) {
        if (action == DnDConstants.ACTION_LINK || asChild /*|| ! isRowAddable(row)*/) return false;
        
        for (int i=0; i < transferFlavors.length; i++) {
            for (int j=0; j < importableFlavors.length; j++) {
                if (transferFlavors[i].equals(importableFlavors[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void removeRow(int row) throws IllegalStateException {
        throw new IllegalStateException();
    }
    
    @Override
    public boolean isRowRemovable(int row) {
        return false;
    }
    
    @Override
    public boolean isRowAddable(int row) {
        return false;
    }
    
    @Override
    public Action[] getRowActions(int[] rows) {
        return new Action[0];
    }
    
    @Override
    public Object[] getCreatableRowTypes(int row) {
        return new Object[0];
    }
    
    @Override
    public Object getCreatableRowType(int row) {
        return null;
    }
    
    @Override
    public Transferable exportRowTransferable(int[] rows) {
        CompositeTransferable t = new CompositeTransferable();
        t.add(TableModels.createLocalTransferable(this, rows));
        t.add(TableModels.createHTMLTransferable(this, rows));
        t.add(TableModels.createPlainTransferable(this, rows));
        return t;
    }
    
    @Override
    public void createRow(int row, Object type) throws IllegalStateException {
        throw new IllegalStateException();
    }
    
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
    
    @Override
    public int importRowTransferable(Transferable t, int action, int row, boolean asChild) throws UnsupportedFlavorException, IOException {
        if (! isRowImportable(t.getTransferDataFlavors(), action, row, asChild))
            throw new UnsupportedFlavorException(null);
        
        Object[][] transferData;
        int rowCount = 0;
        try {
            if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                transferData = TableModels.getStringTable(t, getColumnCount());
            } else if (t.isDataFlavorSupported(DataFlavor.getTextPlainUnicodeFlavor())) {
                transferData = TableModels.getPlainTable(t, getColumnCount());
            } else {
                throw new UnsupportedFlavorException(null);
            }
            
            int overRow = row-1; // Overwrite the selected row! Don't append after it.
            for (int i=0; i < transferData.length; i++) {
                for (int j=0; j < transferData[i].length; j++) {
                    setValueAt(transferData[i][j], overRow + i, j);
                }
                rowCount++;
                if (row + rowCount > getRowCount()) {
                    break;
                }
            }
            fireTableRowsUpdated(overRow, overRow+rowCount);
            
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return rowCount;
    }
}

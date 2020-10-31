/*
 * @(#)ColumnMappingTableModel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.rubik.impexp.csv;

import org.jhotdraw.annotation.Nonnull;

import java.util.ArrayList;
/**
 * ColumnMappingTableModel.
 * @author Werner Randelshofer
 */
public class ColumnMappingTableModel extends javax.swing.table.AbstractTableModel {
    private final static long serialVersionUID = 1L;
    /**
     * The column names shown by the JTable.
     */
    private final static String[] columnNames = {
        "Data", "Mapping"
    };


    /**
     * An instance of this class specifies a column available from the import file.
     * and a mapping of that column to an index specified by TableImpExp.supportedImportColumns.
     */
    public static class Entry {
        /**
         * The title of a column.
         */
        public String columnTitle;
        /**
         * The index mapping the column of the import file to
         * an index in the array TableImpExp.supportedImportColumns. The value -1
         * indicates that this columns should be ignored.
         */
        public int columnMapping;

        public Entry(String columnTitle, int mapping) {
            this.columnTitle = columnTitle;
            this.columnMapping = mapping;
        }
    }
    /**
     * An array list holding the entries.
     */
    private ArrayList<Entry> data;

    /** Creates a new instance of TableImportTableModel */
    public ColumnMappingTableModel() {
        data = new ArrayList<Entry>();
    }

    public void setImportDataColumnTitles(@Nonnull String[] columnTitles) {
        clear();
        for (int i = 0; i < columnTitles.length; i++) {
            data.add(new Entry(
                    columnTitles[i],
                    (i < CSVImporter.supportedColumns.length) ? i : -1
            ));
        }
        fireTableRowsInserted(0, data.size() - 1);
    }

    public int getColumnCount() {
        return columnNames.length;
    }
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getRowCount() {
        return data.size();

    }

    @Nonnull
    public Class getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0 : return String.class;
            case 1 : return Integer.class;
            default : throw new ArrayIndexOutOfBoundsException();
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Entry entry = data.get(rowIndex);
        switch (columnIndex) {
            case 0 : return entry.columnTitle;
            case 1 : return new Integer(entry.columnMapping);
            default : throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void setValueAt(@Nonnull Object value, int rowIndex, int columnIndex) {
        Entry entry = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                throw new IllegalStateException();
            case 1:
                int newMapping = ((Integer) value).intValue();
                ;
                entry.columnMapping = newMapping;
                fireTableCellUpdated(rowIndex, columnIndex);
                if (newMapping != -1) {
                    for (int i = 0; i < data.size(); i++) {
                        if (i != rowIndex
                        && data.get(i).columnMapping == newMapping) {
                            setValueAt(new Integer(-1), i, columnIndex);
                        }
                    }
                }
                break;
            default : throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void clear() {
        if (data.size() > 0) {
            int lastIndex = data.size() - 1;
            data.clear();
            fireTableRowsDeleted(0, lastIndex);
        }
    }
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return columnIndex == 1;
    }

    /**
     * item[i] of this array contains the index of the data column that
     * contains the data for the data element with that index.
     * The value -1 in item[i] specifies that no data is available.
     */
    @Nonnull
    public int[] getColumnMapping() {
        int[] mapping = new int[CSVImporter.supportedColumns.length];
        for (int i=0; i < mapping.length; i++) {
            mapping[i] = -1;
        }

        for (int i=0; i < data.size(); i++) {
            Entry entry = data.get(i);
            if (entry.columnMapping != -1) {
                mapping[entry.columnMapping] = i;
            }
        }
        return mapping;
    }

    @Nonnull
    public static int[] getDefaultColumnMapping() {
        int[] mapping = new int[CSVImporter.supportedColumns.length];
        for (int i=0; i < mapping.length; i++) {
            mapping[i] = i;
        }
        return mapping;
    }
}

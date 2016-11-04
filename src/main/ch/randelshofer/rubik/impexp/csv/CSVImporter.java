/*
 * @(#)CSVImporter.java  1.0  April 12, 2004
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.rubik.impexp.csv;

import ch.randelshofer.cubetwister.doc.*;
import ch.randelshofer.gui.*;
import ch.randelshofer.util.*;
import ch.randelshofer.io.*;
import ch.randelshofer.rubik.impexp.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * CSVImporter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CSVImporter extends JPanel implements Importer {
    private final static long serialVersionUID = 1L;

    private ColumnMappingTableModel columnMapping;
    private File previewFile;
    private DocumentModel documentModel;
    /**
     * The columns of the import data that can be imported by this importer into
     * CubeTwister.
     */
    public final static String[] supportedColumns = {
        "Name",
        "Notation",
        "Script",
        "Macros",
        "Notes",
        "Author",
        "Date",};
    /**
     * The indices of the elements in the array <code>supportedColumns</code>.
     */
    public final static int NAME_INDEX = 0,
            NOTATION_INDEX = 1,
            SCRIPT_INDEX = 2,
            MACROS_INDEX = 3,
            DESCRIPTION_INDEX = 4,
            AUTHOR_INDEX = 5,
            DATE_INDEX = 6;

    private char delimiterChar;

    /**
     * Creates new form.
     */
    public CSVImporter(char delimiterChar) {
        this.delimiterChar = delimiterChar;

        initComponents();
        Font f = Fonts.getSmallDialogFont();
        table.setFont(f);

        table.setModel(columnMapping = new ColumnMappingTableModel());
        columnMapping.setImportDataColumnTitles(supportedColumns);
        //Set up the renderer and the editor for the column mapping cells.
        //----------------------------------------------------------------
        table.getColumnModel().getColumn(1).setCellRenderer(new ColumnMappingCellRenderer());

        // We fill the combo box model with Integer objects. Each Integer objects
        // is an index in the supportedColumns array.
        Object[] items = new Object[supportedColumns.length + 1];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Integer(i - 1);
        }
        JComboBox comboBox = new JComboBox(new DefaultComboBoxModel(items));
        comboBox.setRenderer(new ColumnMappingCellRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBox));
    }

    private void updateView() {
        if (previewFile != null && previewFile.isFile()) {
            try {
                columnMapping.setImportDataColumnTitles(readColumnTitles(previewFile));
            } catch (IOException e) {
                //inputListModel.add(e);
                columnMapping.clear();
            }
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    private String[] readColumnTitles(File f) throws IOException {
        if (f == null) {
            return new String[0];
        }

        String[] columnTitles;
        CSVReader in = null;
        try {
            in = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(f), "ISO-8859-1")), delimiterChar, '"');
            columnTitles = in.readln();
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return columnTitles;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        columnsLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setViewportView(table);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        columnsLabel.setText("Column mapping:");
        add(columnsLabel, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents

    @Override
    public ArrayList<ScriptModel> importFile(File file, ProgressObserver p) throws IOException {
        int[] columnIndices = columnMapping.getColumnMapping();

        CSVReader in = null;
        try {/*
             BoundedRangeInputStream bris;
             bris = new BoundedRangeInputStream(file, true);
             p.setModel(bris);
             in = new CSVReader(new InputStreamReader(bris), delimiterChar, '"');
             */

            BoundedRangeReader bris;
            bris = new BoundedRangeReader(file, true);
            p.setModel(bris);
            in = new CSVReader(bris, delimiterChar, '"');
            ArrayList<ScriptModel> result = new ArrayList<ScriptModel>();

            String[] columns;
            // Skip the first line
            in.readln();

            // Read all subsequent lines
            int lineNumber = 0;
            while ((columns = in.readln()) != null) {
                p.setNote("Importing line " + (++lineNumber) + "...");
                result.add(importScript(columns, columnIndices));
            }
            return result;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private ScriptModel importScript(String[] columns, int[] columnIndices)
            throws IOException {
        ScriptModel item = new ScriptModel();

        int index;
        index = columnIndices[NAME_INDEX];
        if (index != -1 && columns.length > index) {
            item.setName(columns[index]);
        }
        index = columnIndices[SCRIPT_INDEX];
        if (index != -1 && columns.length > index) {
            item.setScript(columns[index]);
        }
        index = columnIndices[DESCRIPTION_INDEX];
        if (index != -1 && columns.length > index) {
            item.setDescription(columns[index]);
        }
        index = columnIndices[AUTHOR_INDEX];
        if (index != -1 && columns.length > index) {
            item.setAuthor(columns[index]);
        }
        index = columnIndices[DATE_INDEX];
        if (index != -1 && columns.length > index) {
            item.setDate(columns[index]);
        }
        index = columnIndices[MACROS_INDEX];
        if (index != -1 && columns.length > index) {
            String macros = columns[index];
            if (macros != null && macros.length() > 0) {
                CSVReader macroIn = new CSVReader(new StringReader(macros), '=', '"');
                String[] macroColumns;
                while ((macroColumns = macroIn.readln()) != null) {
                    MacroModel macro = new MacroModel();
                    macro.setIdentifier(macroColumns[0]);
                    macro.setScript(macroColumns[1]);
                    item.getMacroModels().add(macro);
                }
            }
        }

        return item;
    }

    public void setDocumentModel(DocumentModel model) {
        this.documentModel = model;
    }

    public void setPreviewFile(File file) {
        this.previewFile = file;
        updateView();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel columnsLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    @Override
    public JComponent getComponent() {
        return this;
    }

}
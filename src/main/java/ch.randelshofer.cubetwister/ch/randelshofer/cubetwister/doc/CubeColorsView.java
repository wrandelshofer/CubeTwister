/*
 * @(#)CubeColorsView.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.Fonts;
import ch.randelshofer.gui.table.ColorTableCellEditor;
import ch.randelshofer.gui.table.ColorTableCellRenderer;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.Color;
/**
 * CubeColorsView is used to present the "Colors" page of CubeView.
 *
 * @author  Werner Randelshofer
 */
public class CubeColorsView extends AbstractEntityView {
    private final static long serialVersionUID = 1L;
    CubeColorsTableModel tableModel;

    /** Creates new form CubeColorsView */
    public CubeColorsView() {
        initComponents();


        tableModel = new CubeColorsTableModel();
        table.setModel(tableModel);
        table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
        table.setDefaultEditor(Color.class, new ColorTableCellEditor());

        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.moveColumn(1, 0);
        table.putClientProperty("Quaqua.Table.style","striped");

        JTextField textField = new JTextField();
        textField.setBorder(new MatteBorder(1,1,1,1, Color.black));
        DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
        table.getColumnModel().getColumn(1).setCellEditor(cellEditor);
        table.setFont(Fonts.getSmallDialogFont());
        textField.setFont(Fonts.getSmallDialogFont());
        if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        }
    }

    public void setModel(CubeModel s) {
        if (table.getCellEditor() != null) {
        table.getCellEditor().stopCellEditing();
        }
        tableModel.setModel(s);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        scrollPane = new javax.swing.JScrollPane();
        table = new ch.randelshofer.gui.MutableJTable();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        scrollPane.setViewportView(table);

        add(scrollPane, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    @Nonnull
    public JComponent getViewComponent() {
        return this;
    }

    public void setModel(EntityModel newValue) {
        setModel((CubeModel) newValue);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private ch.randelshofer.gui.MutableJTable table;
    // End of variables declaration//GEN-END:variables

}

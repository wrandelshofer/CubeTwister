/*
 * @(#)PreferencesTemplatesPanel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister;

import ch.randelshofer.util.Files;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * PreferencesTemplatesPanel.
 *
 * @author Werner Randelshofer
 */
public class PreferencesTemplatesPanel extends javax.swing.JPanel {
    private final static long serialVersionUID = 1L;

    private Preferences userPrefs = Preferences.userNodeForPackage(PreferencesTemplatesPanel.class);
    @Nonnull
    private ResourceBundleUtil labels = new ResourceBundleUtil(
            ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));
    private JFileChooser importFileChooser;
    private JFileChooser exportFileChooser;

    /**
     * Creates new instance.
     */
    public PreferencesTemplatesPanel() {
        initComponents();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        templateChoiceLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jDnDList1 = new ch.randelshofer.gui.JDnDList();
        templateAddButton = new javax.swing.JButton();
        templateRemoveButton = new javax.swing.JButton();
        templateExportButton = new javax.swing.JButton();

        templateChoiceLabel.setText(labels.getString("preferences.templateChoice.text")); // NOI18N

        jScrollPane1.setViewportView(jDnDList1);

        templateAddButton.setText(labels.getString("preferences.add.text")); // NOI18N
        templateAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseExternalTemplate(evt);
            }
        });

        templateRemoveButton.setText(labels.getString("preferences.remove.text")); // NOI18N

        templateExportButton.setText(labels.getString("preferences.exportTemplate.text")); // NOI18N
        templateExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportInternalTemplate(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(templateChoiceLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(templateAddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(templateRemoveButton))
                    .addComponent(templateExportButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(templateChoiceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(templateAddButton)
                    .addComponent(templateRemoveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(templateExportButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void exportInternalTemplate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportInternalTemplate
        if (exportFileChooser == null) {
            exportFileChooser = new JFileChooser();
            exportFileChooser.setFileFilter(new ExtensionFileFilter("zip", "Zip Archive"));
            exportFileChooser.setSelectedFile(new File(userPrefs.get("cubetwister.exportedHTMLTemplate", "CubeTwister HTML-Template.zip")));
            exportFileChooser.setApproveButtonText(labels.getString("filechooser.export"));
        }
        if (JFileChooser.APPROVE_OPTION == exportFileChooser.showSaveDialog(this)) {
            userPrefs.put("cubetwister.exportedHTMLTemplate", exportFileChooser.getSelectedFile().getPath());
            final File target = exportFileChooser.getSelectedFile();
            new BackgroundTask() {

                @Override
                public void construct() throws IOException {
                    if (!target.getParentFile().exists()) {
                        target.getParentFile().mkdirs();
                    }
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = getClass().getResourceAsStream("/htmltemplates.tar.bz");
                        out = new FileOutputStream(target);
                        exportTarBZasZip(in, out);
                    } finally {
                        try {
                            if (in != null) {
                                in.close();
                            }
                        } finally {
                            if (out != null) {
                                out.close();
                            }
                        }
                    }
                }

                private void exportTarBZasZip(InputStream in, @Nonnull OutputStream out) throws IOException {
                    ZipOutputStream zout = new ZipOutputStream(out);
                    TarInputStream tin = new TarInputStream(new BZip2CompressorInputStream(in));

                    for (TarArchiveEntry inEntry = tin.getNextEntry(); inEntry != null; ) {
                        ZipEntry outEntry = new ZipEntry(inEntry.getName());
                        zout.putNextEntry(outEntry);
                        if (!inEntry.isDirectory()) {
                            Files.copyStream(tin, zout);
                        }
                        zout.closeEntry();
                    }
                    zout.finish();
                }
            }.start();
        }
    }//GEN-LAST:event_exportInternalTemplate

    private void chooseExternalTemplate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseExternalTemplate
        if (importFileChooser == null) {
            importFileChooser = new JFileChooser();
            importFileChooser.setFileFilter(new ExtensionFileFilter("zip", "Zip Archive"));
            importFileChooser.setSelectedFile(new File(userPrefs.get("cubetwister.externalTemplate", "TinyLMS Template.zip")));
            importFileChooser.setApproveButtonText(labels.getString("filechooser.choose"));
        }
        if (JFileChooser.APPROVE_OPTION == importFileChooser.showOpenDialog(this)) {
            userPrefs.put("cubetwister.externalHTMLTemplate", importFileChooser.getSelectedFile().getPath());
            //externalTemplateField.setText(
            //      userPrefs.get("cubetwister.externalHTMLTemplate", ""));
        }
    }//GEN-LAST:event_chooseExternalTemplate
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ch.randelshofer.gui.JDnDList jDnDList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton templateAddButton;
    private javax.swing.JLabel templateChoiceLabel;
    private javax.swing.JButton templateExportButton;
    private javax.swing.JButton templateRemoveButton;
    // End of variables declaration//GEN-END:variables
}
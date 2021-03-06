/*
 * @(#)PreferencesPanel.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister;

import org.jhotdraw.util.ResourceBundleUtil;

import java.util.ResourceBundle;

/**
 * PreferencesPanel.
 *
 * @author Werner Randelshofer
 */
public class PreferencesPanel extends javax.swing.JPanel {

    private final static long serialVersionUID = 1L;
    private ResourceBundleUtil labels;

    /**
     * Creates new form PreferencesPanel
     */
    public PreferencesPanel() {
        labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));
        initComponents();
        templatesPanel.setViewClassName("ch.randelshofer.cubetwister.PreferencesTemplatesPanel");
        cachesPanel.setViewClassName("ch.randelshofer.cubetwister.PreferencesCachesPanel");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        cachesPanel = new ch.randelshofer.gui.LazyPanel();
        templatesPanel = new ch.randelshofer.gui.LazyPanel();

        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.addTab(labels.getString("preferences.caches.text"), cachesPanel); // NOI18N
        jTabbedPane1.addTab(labels.getString("preferences.templates.text"), templatesPanel); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ch.randelshofer.gui.LazyPanel cachesPanel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private ch.randelshofer.gui.LazyPanel templatesPanel;
    // End of variables declaration//GEN-END:variables

}

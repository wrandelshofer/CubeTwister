/* @(#)PicturesView.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import ch.randelshofer.util.*;
import java.beans.*;
import javax.swing.*;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * PicturesView.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class CubeStickersImageView extends AbstractEntityView implements PropertyChangeListener {
    private final static long serialVersionUID = 1L;
    private CubeModel model;
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;
    /**
     * Creates a new instance.
     */
    public CubeStickersImageView() {
        labels = ResourceBundleUtil.getBundle("ch.randelshofer.cubetwister.Labels");
        initComponents();
        scrollPane.getViewport().setOpaque(false);
        stickersImageInfoLabel.setFont(UIManager.getFont("SmallSystemFont"));
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = new javax.swing.JScrollPane();
        layoutPanel = new javax.swing.JPanel();
        stickersImageCheck = new javax.swing.JCheckBox();
        stickersImageWell = new ch.randelshofer.gui.JImageWell();
        stickersImageInfoLabel = new javax.swing.JLabel();
        strutPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        layoutPanel.setLayout(new java.awt.GridBagLayout());

        layoutPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 20, 12));
        stickersImageCheck.setSelected(true);
        stickersImageCheck.setText(labels.getString("stickersImageCheck"));
        stickersImageCheck.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                stickersImageStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        layoutPanel.add(stickersImageCheck, gridBagConstraints);

        stickersImageWell.setMinimumSize(new java.awt.Dimension(72, 72));
        stickersImageWell.setPreferredSize(new java.awt.Dimension(100, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 22, 0, 0);
        layoutPanel.add(stickersImageWell, gridBagConstraints);

        stickersImageInfoLabel.setFont(UIManager.getFont("SmallSystemFont"));
        stickersImageInfoLabel.setText("<html>Drag an image into the box above.<br>Right click the box for a pop up menu.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 22, 0, 0);
        layoutPanel.add(stickersImageInfoLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 99;
        gridBagConstraints.gridy = 99;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        layoutPanel.add(strutPanel, gridBagConstraints);

        scrollPane.setViewportView(layoutPanel);

        add(scrollPane, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents
            
    private void stickersImageStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_stickersImageStateChanged
        if (model != null) {
            model.setStickersImageVisible(stickersImageCheck.isSelected());
        }
        validate();
    }//GEN-LAST:event_stickersImageStateChanged
    
    @Override
    public JComponent getViewComponent() {
        return this;
    }
    
    @Override
    public void setModel(EntityModel newValue) {
        setModel((CubeModel) newValue);
    }
    public void setModel(CubeModel newValue) {
        if (model != null) {
            stickersImageWell.setModel(new DefaultImageWellModel());
            model.removePropertyChangeListener(this);
        }
        model = newValue;
        if (model != null) {
            stickersImageWell.setModel(model.getStickersImageModel());
            model.addPropertyChangeListener(this);
            
            updateStickersImageVisible();
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (name.equals(CubeModel.STICKERS_IMAGE_VISIBLE_PROPERTY)) {
            updateStickersImageVisible();
        }
    }
    
    private void updateStickersImageVisible() {
        boolean b = model.isStickersImageVisible();
            stickersImageCheck.setSelected(b);
        stickersImageWell.setVisible(b);
        stickersImageInfoLabel.setVisible(b);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel layoutPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JCheckBox stickersImageCheck;
    private javax.swing.JLabel stickersImageInfoLabel;
    private ch.randelshofer.gui.JImageWell stickersImageWell;
    private javax.swing.JPanel strutPanel;
    // End of variables declaration//GEN-END:variables
    
}

/* @(#)NotationView.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import ch.randelshofer.gui.border.PlacardButtonBorder;
import ch.randelshofer.undo.*;
import ch.randelshofer.util.*;

import java.awt.*;
import java.beans.*;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * NotationView.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class NotationView extends JPanel
        implements PropertyChangeListener,
        Undoable, EntityView, UndoableEditListener {
    private final static long serialVersionUID = 1L;

    /**
     * This combo box model is used to select
     * different layer sizes.
     */
    private class LayersComboBoxModel
            extends DefaultComboBoxModel {
    private final static long serialVersionUID = 1L;

        public LayersComboBoxModel() {
            super(new String[]{"2x2", "3x3", "4x4", "5x5", "6x6", "7x7"});
        }

        public void setSelectedItem(Object item) {
            int number = ((String) item).charAt(0) - '0';

            if (model != null) {
                boolean wasDefaultNotation = model.isDefaultNotation();
                model.setLayerCount(number);
                if (wasDefaultNotation) {
                    model.getDocument().setDefaultNotation(model);
                }
            }
            fireContentsChanged();
        }

        public Object getSelectedItem() {
            return (model == null) ? null : getElementAt(model.getLayerCount() - 2);
        }

        public void fireContentsChanged() {
            fireContentsChanged(this, -1, -1);
        }
    }
    private NotationModel model;
    /** The listeners waiting for model changes. */
    private EventListenerList listenerList = new javax.swing.event.EventListenerList();
    private Preferences prefs;
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;

    /** Creates a new instance. */
    public NotationView() {
        this(null);
    }

    public NotationView(NotationModel m) {
        init();
        setModel(m);
    }

    public void init() {
        prefs = Preferences.userNodeForPackage(getClass());

        labels = ResourceBundleUtil.getBundle("ch.randelshofer.cubetwister.Labels");
        initComponents();
        layersComboBox.setModel(new LayersComboBoxModel());

        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
            placardButton.putClientProperty("Quaqua.Button.style", "placard");
            placardButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -2, -1, -1));
            placardButton.setPreferredSize(new Dimension(20, 23));
            placardButton.setMinimumSize(new Dimension(20, 23));
            contentPanel.setBorder(new EmptyBorder(0,-3,-20,-3));
        } else if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            placardButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            placardButton.setMinimumSize(new Dimension(10, 23));
            placardButton.setPreferredSize(new Dimension(10, 23));
            contentPanel.setBorder(new EmptyBorder(6,10,-7,9));
            tabsPanel.setBorder(new EmptyBorder(0,0,0,-1));
        } else {
            placardButton.setVisible(false);
        }

        movesView.setViewClassName("ch.randelshofer.cubetwister.doc.NotationMovesView");
        constructsView.setViewClassName("ch.randelshofer.cubetwister.doc.NotationConstructsView");
        macrosView.setViewClassName("ch.randelshofer.cubetwister.doc.NotationMacrosView");
        infoView.setViewClassName("ch.randelshofer.cubetwister.doc.InfoView");

        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.setFont(Fonts.getSmallDialogFont());
        PreferencesUtil.installTabbedPanePrefsHandler(prefs, "NotationView.selectedTab", tabbedPane);
    }

    /**
     * An undoable edit happened
     */
    public void undoableEditHappened(UndoableEditEvent e) {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
    }

    /**
     * Removes an UndoableEditListener.
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
    }

    /**
     * Adds an UndoableEditListener.
     */
    public void addUndoableEditListener(UndoableEditListener l) {
    }

    public void setModel(EntityModel newValue) {
        setModel((NotationModel) newValue);
    }

    public void setModel(NotationModel value) {
        //long start = System.currentTimeMillis();
        NotationModel oldValue = model;

        if (model != null) {
            model.removePropertyChangeListener(this);
            if (model.getDocument() != null) {
                model.getDocument().removePropertyChangeListener(this);
            } else if (value != null && value.getDocument() != null) {
                value.getDocument().removePropertyChangeListener(this);
            }
        }


        model = value;
        if (model == null) {
            nameField.setDocument(new PlainDocument());
            defaultCheckBox.setEnabled(false);
        } else {
            model.addPropertyChangeListener(this);
            model.getDocument().addPropertyChangeListener(this);
            nameField.setDocument(model.getNameDocument());
            defaultCheckBox.setSelected(model.getDocument().getDefaultNotation(model.getLayerCount()) == model);
            defaultCheckBox.setEnabled(!defaultCheckBox.isSelected());

            if (model.getDocument() != null) {
                boolean b = model.isDefaultNotation();
                defaultCheckBox.setSelected(b);
                defaultCheckBox.setEnabled(!b);
            }
        }

        updateEnabled();
        movesView.setModel(model);
        constructsView.setModel(model);
        macrosView.setModel(model);
        infoView.setModel(model);
        ((LayersComboBoxModel) layersComboBox.getModel()).fireContentsChanged();
    }

    public void updateEnabled() {
        boolean b = model != null && isEnabled();
        nameLabel.setEnabled(b);
        nameField.setEnabled(b);
        defaultCheckBox.setEnabled(b && !defaultCheckBox.isSelected());
        layersComboBox.setEnabled(b);
        layersLabel.setEnabled(b);

        tabbedPane.setEnabled(b);
        /*
        partsView.setEnabled(b);
        colorsView.setEnabled(b);
        infoView.setEnabled(b);
        stickersView.setEnabled(b);
        optionsView.setEnabled(b);
        twistToggleButton.setEnabled(b);
        if (cubeCanvas != null) cubeCanvas.setEnabled(b);
        stickersToggleButton.setEnabled(b);
        partsToggleButton.setEnabled(b);
         */
    }

    public JComponent getViewComponent() {
        return this;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
        nameField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        layersLabel = new javax.swing.JLabel();
        layersComboBox = new javax.swing.JComboBox();
        defaultCheckBox = new javax.swing.JCheckBox();
        tabsPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        movesView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        constructsView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        macrosView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        infoView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        placardButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        nameLabel.setText(labels.getString("name")); // NOI18N

        layersLabel.setText(labels.getString("layers")); // NOI18N

        defaultCheckBox.setText(labels.getString("useAsDefaultNotation")); // NOI18N
        defaultCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useAsDefaultPerformed(evt);
            }
        });

        tabsPanel.setLayout(new java.awt.BorderLayout());

        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPane.setFont(UIManager.getFont("SmallSystemFont"));
        tabbedPane.addTab(labels.getString("moveTab"), movesView); // NOI18N
        tabbedPane.addTab(labels.getString("constructsTab"), constructsView); // NOI18N
        tabbedPane.addTab(labels.getString("macrosTitle"), macrosView); // NOI18N
        tabbedPane.addTab(labels.getString("notesTab"), infoView); // NOI18N

        tabsPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 386, Short.MAX_VALUE)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(layersLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(defaultCheckBox)
                            .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                            .addComponent(layersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(layersLabel)
                    .addComponent(layersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(defaultCheckBox)
                .addGap(18, 18, 18)
                .addComponent(tabsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(contentPanel, java.awt.BorderLayout.CENTER);

        placardButton.setText(" ");
        placardButton.setEnabled(false);
        add(placardButton, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void useAsDefaultPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useAsDefaultPerformed
        if (defaultCheckBox.isSelected()) {
            defaultCheckBox.setEnabled(false);
            model.getDocument().setDefaultNotation(model);
        }

    }//GEN-LAST:event_useAsDefaultPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ch.randelshofer.cubetwister.doc.LazyEntityView constructsView;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JCheckBox defaultCheckBox;
    private ch.randelshofer.cubetwister.doc.LazyEntityView infoView;
    private javax.swing.JComboBox layersComboBox;
    private javax.swing.JLabel layersLabel;
    private ch.randelshofer.cubetwister.doc.LazyEntityView macrosView;
    private ch.randelshofer.cubetwister.doc.LazyEntityView movesView;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton placardButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel tabsPanel;
    // End of variables declaration//GEN-END:variables
}

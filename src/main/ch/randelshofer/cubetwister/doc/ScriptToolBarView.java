/* @(#)ScriptToolBarView.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import ch.randelshofer.gui.border.PlacardButtonBorder;
import ch.randelshofer.gui.event.ModifierTracker;
import ch.randelshofer.gui.plaf.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.undo.*;
import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * ScriptToolBarView.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.1 2010-08-18 Changes icon of reset button when option key is
 * pressed.
 * <br>1.0  June 3, 2006  Created.
 */
public class ScriptToolBarView extends JPanel implements EntityView {
    private final static long serialVersionUID = 1L;
    /**
     * Script View.
     */
    private ScriptModel model;
    private ScriptView scriptView;
    /**
     * The listeners waiting for UndoableEdit events.
     * /
    private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
     */
    private PropertyChangeListener propertyHandler = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String n = evt.getPropertyName();
            if (evt.getSource() == model) {
                if (n == ScriptModel.CUBE_PROPERTY) {
                    updateSolver();
                }
            } else if (evt.getSource() == ModifierTracker.class) {
                if (n == ModifierTracker.MODIFIERS_EX_PROPERTY) {
                    int modifiersEx = (Integer) evt.getNewValue();
                    resetButton.setIcon(//
                            (modifiersEx & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0//
                            ? Icons.get(Icons.PLAYER_PARTIAL_RESET_PLACARD) : Icons.get(Icons.PLAYER_RESET_PLACARD));

                }
            }
        }
    };

    /**
     * The color icon is used in the tool bar to
     * let the user choose a sticker color.
     */
    private class ColorIcon extends PolygonIcon {

        private int faceIndex;
        private int stickerIndex;

        public ColorIcon(int faceIndex, int stickerIndex) {
            super(new Polygon(
                    new int[]{0, 10, 10, 0},
                    new int[]{0, 0, 10, 10},
                    4),
                    new Dimension(10, 10));
            this.faceIndex = faceIndex;
            this.stickerIndex = stickerIndex;
        }

        @Override
        public Color getFillColor() {
            return (model == null || model.getSolverModel() == null)
                    ? Color.black
                    : model.getSolverModel().getStickerFillColor(stickerIndex);
        }

        @Override
        public Color getForeground() {
            return (model == null || model.getSolverModel() == null)
                    ? Color.black
                    : Colors.shadow(model.getSolverModel().getStickerFillColor(stickerIndex), 38);
        }
    }
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;

    /** Creates a new instance. */
    public ScriptToolBarView() {
        init();
    }

    private void init() {
        // Load the resource bundle
        labels = ResourceBundleUtil.getBundle("ch.randelshofer.cubetwister.doc.Labels");

        // Initialise the components as far as the IDE supports it.
        initComponents();

        //solverModel.addPropertyChangeListener(this);

        Color bg = UIManager.getColor("TextField.background");

        toolBar.putClientProperty("Quaqua.Border.insets", new Insets(0, 0, 0, 0));
        toolBar.putClientProperty("Quaqua.ToolBar.style", "placard");
        toolBar.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, -1));
        
        
        // Decorate the reset button 2 with an icon looking like '|<' and a west bevel.
        resetButton.setIcon(Icons.get(Icons.PLAYER_RESET_PLACARD));
        Icons.get(Icons.PLAYER_PARTIAL_RESET_PLACARD); // lazily load this icon
        resetButton.putClientProperty("Quaqua.Button.style", "placard");
        resetButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        resetButton.setFocusable(false);
        ModifierTracker.addModifierListener(new WeakPropertyChangeListener(propertyHandler));

        // Decorate the toggle buttons
        twistToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);
        twistToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        twistToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        twistToggleButton.setFont(Fonts.getSmallDialogFont());

        frontToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        frontToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        rightToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        rightToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        frontToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);
        downToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        downToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        frontToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);
        upToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        upToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        frontToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);
        leftToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        leftToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        frontToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);
        backToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        backToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        frontToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);

        // Decorate the popup button 2 with an icon looking like a small popup menu.
        //popupButton.setIcon(Icons.POPUP_ICON);
        popupButton.setIcon(Icons.get(Icons.ACTIONS_PLACARD));
        popupButton.putClientProperty("Quaqua.Button.style", "placard");
        popupButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        frontToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);

        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
            /*
            toolbarPanel.setBorder(UIManager.getBorder("Button.border"));
            toolbarPanel.putClientProperty("Quaqua.Button.style", "placard");
            toolbarPanel.putClientProperty("Quaqua.Border.insets", new Insets(0, 0, 0, 0));
            toolbarPanel.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -2, -1, -2));*/
            toolbarPanel.setPreferredSize(new Dimension(24, 24));
            twistToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, -1));
            frontToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, -1));
            rightToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, -1));
            downToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, -1));
            upToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, -1));
            backToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, -1));
            leftToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, -1));
        } else if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            toolBar.setBorder(new PlacardButtonBorder(SwingConstants.CENTER));
            resetButton.setUI((ButtonUI) BasicButtonUI.createUI(resetButton));
            resetButton.setBorder(new PlacardButtonBorder(-1));
            twistToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(twistToggleButton));
            twistToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            frontToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(frontToggleButton));
            frontToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            rightToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(rightToggleButton));
            rightToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            downToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(downToggleButton));
            downToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            upToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(upToggleButton));
            upToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            backToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(backToggleButton));
            backToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            leftToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(leftToggleButton));
            leftToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            popupButton.setUI((ButtonUI) BasicButtonUI.createUI(popupButton));
            popupButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
        }
        // Install a popup menu listener on all components
        MouseAdapter popupListener = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showPopup(evt, false);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showPopup(evt, false);
                }
            }
        };
        addMouseListener(popupListener);


    }

    /**
     * Dynamically creates and shows the popup menu if the component is enabled.
     *
     * @param evt The mouse event which triggered the popup menu.
     * @param isTriggeredByButton This parameter is set to true, when the
     *        popup menu has been triggered by a button. The popup menu must
     *        be placed outside the bounds of the buttons.
     */
    public void showPopup(MouseEvent evt, boolean isTriggeredByButton) {
        if (scriptView != null) {
            scriptView.showPopup(evt, isTriggeredByButton);
        }
    }

    public void setScriptView(ScriptView newValue) {
        scriptView = newValue;
    }

    /**
     * Removes an UndoableEditListener.
     */
    @Override
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }

    /**
     * Adds an UndoableEditListener.
     */
    @Override
    public void addUndoableEditListener(UndoableEditListener l) {
        listenerList.add(UndoableEditListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    public void fireUndoableEditHappened(UndoableEdit edit) {
        if (listenerList != null) {
            UndoableEditEvent evt = null;

            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == UndoableEditListener.class) {
                    // Lazily create the event
                    if (evt == null) {
                        evt = new UndoableEditEvent(this, edit);
                    }
                    ((UndoableEditListener) listeners[i + 1]).undoableEditHappened(evt);
                }
            }
        }
    }

    @Override
    public void setModel(EntityModel newValue) {
        setModel((ScriptModel) newValue);
    }

    public void setModel(ScriptModel newValue) {
        if (model != null) {
            model.removePropertyChangeListener(propertyHandler);
        }
        model = newValue;
        if (model != null) {
            model.addPropertyChangeListener(propertyHandler);
            updateSolver();
        }
    }

    protected void updateSolver() {
        SolverModel sm = model.getSolverModel();

        boolean b = sm.isSolverSupported();
        backToggleButton.setVisible(b);
        frontToggleButton.setVisible(b);
        rightToggleButton.setVisible(b);
        leftToggleButton.setVisible(b);
        upToggleButton.setVisible(b);
        downToggleButton.setVisible(b);
        twistToggleButton.setVisible(b);
        if (b) {
            int sc = sm.getStickerCount(0);
            int offset = sc / 2;
            frontToggleButton.setIcon(new ColorIcon(0, 0 * sc + offset));
            rightToggleButton.setIcon(new ColorIcon(1, 1 * sc + offset));
            downToggleButton.setIcon(new ColorIcon(2, 2 * sc + offset));
            backToggleButton.setIcon(new ColorIcon(3, 3 * sc + offset));
            leftToggleButton.setIcon(new ColorIcon(4, 4 * sc + offset));
            upToggleButton.setIcon(new ColorIcon(5, 5 * sc + offset));
        } else {
            twistToggleButton.setSelected(true);
            frontToggleButton.setIcon(new ColorIcon(0, 0));
            rightToggleButton.setIcon(new ColorIcon(0, 0));
            downToggleButton.setIcon(new ColorIcon(0, 0));
            backToggleButton.setIcon(new ColorIcon(0, 0));
            leftToggleButton.setIcon(new ColorIcon(0, 0));
            upToggleButton.setIcon(new ColorIcon(0, 0));
        }

        repaint(); // repaint all buttons
    }

    @Override
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
        java.awt.GridBagConstraints gridBagConstraints;

        toggleButtonGroup = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        toolbarPanel = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        twistToggleButton = new javax.swing.JToggleButton();
        rightToggleButton = new javax.swing.JToggleButton();
        upToggleButton = new javax.swing.JToggleButton();
        frontToggleButton = new javax.swing.JToggleButton();
        leftToggleButton = new javax.swing.JToggleButton();
        downToggleButton = new javax.swing.JToggleButton();
        backToggleButton = new javax.swing.JToggleButton();
        popupButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);

        toolbarPanel.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/randelshofer/cubetwister/Labels"); // NOI18N
        resetButton.setToolTipText(bundle.getString("resetTip")); // NOI18N
        resetButton.setFocusable(false);
        resetButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(resetButton, gridBagConstraints);

        toggleButtonGroup.add(twistToggleButton);
        twistToggleButton.setFont(new java.awt.Font("Lucida Grande", 0, 13)); // NOI18N
        twistToggleButton.setSelected(true);
        twistToggleButton.setText(labels.getString("twistToggle")); // NOI18N
        twistToggleButton.setToolTipText(labels.getString("twistToggleTip")); // NOI18N
        twistToggleButton.setFocusable(false);
        twistToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        twistToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(twistToggleButton, gridBagConstraints);

        toggleButtonGroup.add(rightToggleButton);
        rightToggleButton.setToolTipText(labels.getString("rightToggleTip")); // NOI18N
        rightToggleButton.setFocusable(false);
        rightToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        rightToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(rightToggleButton, gridBagConstraints);

        toggleButtonGroup.add(upToggleButton);
        upToggleButton.setToolTipText(labels.getString("upToggleTip")); // NOI18N
        upToggleButton.setFocusable(false);
        upToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        upToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(upToggleButton, gridBagConstraints);

        toggleButtonGroup.add(frontToggleButton);
        frontToggleButton.setToolTipText(labels.getString("frontToggleTip")); // NOI18N
        frontToggleButton.setFocusable(false);
        frontToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        frontToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(frontToggleButton, gridBagConstraints);

        toggleButtonGroup.add(leftToggleButton);
        leftToggleButton.setToolTipText(labels.getString("leftToggleTip")); // NOI18N
        leftToggleButton.setFocusable(false);
        leftToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        leftToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(leftToggleButton, gridBagConstraints);

        toggleButtonGroup.add(downToggleButton);
        downToggleButton.setToolTipText(labels.getString("downToggleTip")); // NOI18N
        downToggleButton.setFocusable(false);
        downToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        downToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(downToggleButton, gridBagConstraints);

        toggleButtonGroup.add(backToggleButton);
        backToggleButton.setToolTipText(labels.getString("backToggleTip")); // NOI18N
        backToggleButton.setFocusable(false);
        backToggleButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        backToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(backToggleButton, gridBagConstraints);

        popupButton.setToolTipText(bundle.getString("popupTip")); // NOI18N
        popupButton.setFocusable(false);
        popupButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        popupButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                popup(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        toolbarPanel.add(popupButton, gridBagConstraints);

        toolBar.add(toolbarPanel);

        add(toolBar, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void toggleButtonChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_toggleButtonChanged
        JToggleButton source = (JToggleButton) evt.getSource();
        if (source.getIcon() instanceof ColorIcon) {
            ColorIcon icon = (ColorIcon) source.getIcon();
            model.getSolverModel().setPaintFace(icon.faceIndex);
        } else {
            model.getSolverModel().setPaintFace(-1);
        }
    }//GEN-LAST:event_toggleButtonChanged

    private void popup(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_popup
        showPopup(evt, true);
    }//GEN-LAST:event_popup

    private void reset(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset
        if ((evt.getModifiers() & (ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)) == 0) {
            model.reset();
            scriptView.resetViews();
        } else {
            scriptView.resetViews();
        }

    }//GEN-LAST:event_reset
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton backToggleButton;
    private javax.swing.JToggleButton downToggleButton;
    private javax.swing.JToggleButton frontToggleButton;
    private javax.swing.JToggleButton leftToggleButton;
    private javax.swing.JButton popupButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JToggleButton rightToggleButton;
    private javax.swing.ButtonGroup toggleButtonGroup;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JToggleButton twistToggleButton;
    private javax.swing.JToggleButton upToggleButton;
    // End of variables declaration//GEN-END:variables
}

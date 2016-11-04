/*
 * @(#)CubeView.java  2.4  2010-08-18
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import ch.randelshofer.gui.border.PlacardButtonBorder;
import ch.randelshofer.gui.event.ModifierTracker;
import ch.randelshofer.gui.plaf.*;
import ch.randelshofer.rubik.*;
import ch.randelshofer.undo.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.*;
import javax.swing.undo.*;
import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 *
 * @author Werner Randelshofer
 * @version 2.4 2010-08-10 Toggles icon of reset button when option key is
 * pressed.
 * <br>2.3 2010-05-02 Option-Click on reset button only resets
 * the canvas but not the cube.
 * <br>2.2 2009-01-09 Use modifiersEx instead of modifiersEx.
 * <br>2.1 2008-08-23 Added support for modifiersEx key to "parts" tool.
 * <br>2.0.1 2008-04-28 Preferences of selected tab were not updated. 
 * <br>2.0 2005-01-05 Reworked for CubeTwister 2.0.
 * <br>1.1.3 2003-08-03 Fixed some font settings.
 * <br>1.1.2 2002-11-14 Undo implementaton of DimensionView changed.
 * <br>1.1.1 2002-02-07 Cube must be twisted only, when the Twist Toggle Button
 * is selected.
 * <br>1.1 2002-02-03 Orientation of Edge twists changed.
 */
public class CubeView extends JPanel implements Undoable, EntityView {
    private final static long serialVersionUID = 1L;

    private CubeModel model;
    private Cube3D cube3D;
    private Cube cube;
    /** The listeners waiting for model changes. * /
    private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
     */
    private JCubeCanvasIdx3D cubeCanvas;
    private Preferences prefs;
    private PropertyChangeListener propertyHandler = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Object source = evt.getSource();
            String n = evt.getPropertyName();
            if (source == model) {
                if (n == CubeModel.KIND_PROPERTY) {
                    updateCube3D();
                }
            } else if (model != null && source == model.getDocument()) {
                defaultCheckBox.setSelected(model.getDocument().getDefaultCube() == model);
                defaultCheckBox.setEnabled(!defaultCheckBox.isSelected());
            } else if (evt.getSource() == ModifierTracker.class) {
                if (n == ModifierTracker.MODIFIERS_EX_PROPERTY) {
                    int modifiersEx = (Integer) evt.getNewValue();
                    resetButton.setIcon(//
                            (modifiersEx & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0//
                            ? Icons.get(Icons.PLAYER_PARTIAL_RESET_PLACARD) : Icons.get(Icons.PLAYER_RESET_PLACARD));
                    resetButton2.setIcon(//
                            (modifiersEx & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0//
                            ? Icons.get(Icons.PLAYER_PARTIAL_RESET_PLACARD) : Icons.get(Icons.PLAYER_RESET_PLACARD));

                }
            }

        }
    };
    private UndoableEditListener undoableEditHandler = new UndoableEditListener() {

        @Override
        public void undoableEditHappened(UndoableEditEvent evt) {
            // forward events from Dimension view
            fireUndoableEditHappened(evt.getEdit());
        }
    };
    private Cube3DListener cube3DHandler = new Cube3DAdapter() {

        /**
         * Invoked when a mouse event on a part of the cube occurs.
         */
        @Override
        public void actionPerformed(final Cube3DEvent evt) {
//System.out.println("CubeView.actionPerformed "+evt);            
            if (partsToggleButton.isSelected() && evt.getPartIndex() != -1) {
                int modifiersEx = evt.getModifiersEx();
                int offset;
                int length;
                CubeModel attr = model;
                int sideIndex = evt.getSideIndex();
                int stickerIndex = evt.getStickerIndex();
                int partIndex = evt.getPartIndex();
                boolean endState = !attr.isPartVisible(partIndex);

                boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
                int relevantModifiersEx = (isMac) ? //
                        modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                        modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
                switch (relevantModifiersEx) {
                    case InputEvent.SHIFT_DOWN_MASK:
                        // Shift affects all parts of the same color
                        offset = 0;
                        int face = Cubes.getFaceOfSticker(attr, stickerIndex);
                        if (face != -1) {
                            offset = attr.getStickerOffset(face);
                            length = attr.getStickerCount(face);
                            for (int i = 0; i < length; i++) {
                                attr.setPartVisible(
                                        cube3D.getPartIndexForStickerIndex(i + offset),
                                        endState);
                            }
                        }
                        break;

                    case InputEvent.META_DOWN_MASK:
                    case InputEvent.CTRL_DOWN_MASK:
                        // Meta affects all parts on the same face
                        if (sideIndex != -1) {
                            offset = attr.getStickerOffset(sideIndex);
                            for (int i = 0; i < attr.getStickerCount(sideIndex); i++) {
                                attr.setPartVisible(
                                        cube.getPartAt(cube3D.getPartIndexForStickerIndex(i + offset)),
                                        endState);
                            }
                        }
                        break;

                    case InputEvent.ALT_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK:
                        // Alt affects all parts of the same type
                        int indices[];
                        if (partIndex < cube.getCornerCount()) {
                            indices = new int[cube.getCornerCount()];
                            for (int i = 0; i < indices.length; i++) {
                                indices[i] = i;
                            }
                        } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount()) {
                            indices = new int[cube.getEdgeCount()];
                            offset = cube.getCornerCount();
                            for (int i = 0; i < indices.length; i++) {
                                indices[i] = offset + i;
                            }
                        } else if (partIndex < cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount()) {
                            indices = new int[cube.getSideCount()];
                            offset = cube.getCornerCount() + cube.getEdgeCount();
                            for (int i = 0; i < indices.length; i++) {
                                indices[i] = offset + i;
                            }
                        } else {
                            offset = cube.getCornerCount() + cube.getEdgeCount() + cube.getSideCount();
                            indices = new int[attr.getPartCount() - offset];
                            for (int i = 0; i < indices.length; i++) {
                                indices[i] = offset + i;
                            }
                        }
                        for (int i = 0; i < indices.length; i++) {
                            attr.setPartVisible(indices[i], endState);
                        }
                        break;

                    case InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                    case InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                    case InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                    case InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                    case InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                        // Shift+Alt affects all parts except the center part
                        for (int i = 0, n = cube3D.getPartCount() - 1; i < n; i++) {
                            attr.setPartVisible(i, endState);
                        }
                        break;

                    case 0:
                        // No modifiersEx affects a single part only
                        attr.setPartVisible(partIndex, endState);
                        break;
                }
                /*
                if ((evt.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0) {

                } else if ((evt.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK)) != 0) {

                } else {
                ((CubePartModel) model.getParts().getChildAt(evt.getPartIndex())).setVisible(! model.isPartVisible(evt.getPartIndex()));
                }*/

            } else if (stickersToggleButton.isSelected() && evt.getStickerIndex() != -1) {
                int modifiersEx = evt.getModifiersEx();
                int offset;
                int length;
                CubeModel attr = model;
                int sideIndex = evt.getSideIndex();
                int stickerIndex = evt.getStickerIndex();
                int partIndex = evt.getPartIndex();
                boolean endState = !attr.isPartVisible(partIndex);

                boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
                int relevantModifiersEx = (isMac) ? //
                        modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK) : //
                        modifiersEx & (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.CTRL_DOWN_MASK);
                switch (relevantModifiersEx) {
                    case InputEvent.SHIFT_DOWN_MASK: {
                        // Shift affects all parts of the same color
                        offset = 0;
                        int face = Cubes.getFaceOfSticker(attr, stickerIndex);
                        offset = attr.getStickerOffset(face);
                        length = attr.getStickerCount(face);
                        boolean b = !model.isStickerVisible(evt.getStickerIndex());
                        EntityModel sm = model.getStickers();
                        for (int i = offset, n = offset + length; i < n; i++) {
                            ((CubeStickerModel) sm.getChildAt(i)).setVisible(b);
                        }
                        break;
                    }
                    case InputEvent.META_DOWN_MASK:
                    case InputEvent.CTRL_DOWN_MASK:
                    case InputEvent.ALT_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK:
                    case InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                    case InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                    case InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.META_DOWN_MASK:
                    case InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.CTRL_DOWN_MASK:
                    case InputEvent.SHIFT_DOWN_MASK | InputEvent.META_DOWN_MASK:
                    case InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK: {
                        // Shift+Alt affects all stickers
                        boolean b = !model.isStickerVisible(evt.getStickerIndex());
                        EntityModel sm = model.getStickers();
                        for (int i = 0, n = model.getStickerCount(); i < n; i++) {
                            ((CubeStickerModel) sm.getChildAt(i)).setVisible(b);
                        }
                        break;
                    }
                    case 0:
                        // No modifiersEx affects a single part only
                        ((CubeStickerModel) model.getStickers().getChildAt(evt.getStickerIndex())).setVisible(!model.isStickerVisible(evt.getStickerIndex()));
                        break;
                }

            } else if (twistToggleButton.isSelected() && evt.getOrientation() != -1) {
                if (evt.getSideIndex() != -1) {
                    Cube cube = cube3D.getCube();
                    evt.applyTo(cube);
                }
                // evt.applyTo(cube3D.getCube());

                // Don't include cube twists into undo/redo.
            /*
                fireUndoableEditHappened(
                new AbstractUndoableEdit() {
                public String getPresentationName() { return "Twist"; }
                public void undo() {
                super.undo();
                evt.applyTo(cube3D.getCube(), true);
                }
                public void redo() {
                super.redo();
                evt.applyTo(cube3D.getCube(), false);
                }
                }
                );*/
            }
        }
    };

    /**
     * This combo box model is used to select
     * different cube kinds.
     */
    private class KindComboBoxModel
            extends DefaultComboBoxModel {
    private final static long serialVersionUID = 1L;

        public KindComboBoxModel() {
            super(CubeKind.values());
        }

        @Override
        public void setSelectedItem(Object item) {
            if (model != null) {
                model.setKind((CubeKind) item);
            }
            fireContentsChanged();
        }

        @Override
        public Object getSelectedItem() {
            return (model == null) ? null : model.getKind();
        }

        public void fireContentsChanged() {
            fireContentsChanged(this, -1, -1);
        }
    }
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;

    /** Creates new form CubeView, without intializing it. Call init() before you are
     * going to use it. */
    public CubeView() {
    }

    /** Creates new form CubeView */
    public CubeView(CubeModel m) {
        init();
        setModel(m);
    }

    public void init() {
        prefs = Preferences.userNodeForPackage(getClass());
        int selectedTab = prefs.getInt("CubeView.selectedTab", 0);


        labels = ResourceBundleUtil.getBundle("ch.randelshofer.cubetwister.Labels");

        initComponents();

        leftPanel.setBackground(UIManager.getColor("TextField.background"));
        leftPanel.setOpaque(true);

        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
            toolbarPanel.setBorder(UIManager.getBorder("Button.border"));
            toolbarPanel.putClientProperty("Quaqua.Button.style", "placard");
            toolbarPanel.putClientProperty("Quaqua.Border.insets", new Insets(0, 0, 0, 0));
            toolbarPanel.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -2, -1, -2));
            contextPanel.setBorder(new EmptyBorder(0, -10, -6, 0));
            //
            toolBar.putClientProperty("Quaqua.ToolBar.style", "gradient");
            toolBar.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -2, -1, -1));
            toolBar.setPreferredSize(new Dimension(24, 24));
            toolBar.setMinimumSize(new Dimension(24, 24));
            
        } else if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            toolbarPanel.setBorder(new PlacardButtonBorder(SwingConstants.CENTER));
            resetButton2.setUI((ButtonUI) BasicButtonUI.createUI(resetButton2));
            resetButton2.setBorder(new PlacardButtonBorder(-1));
            twistToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(twistToggleButton));
            twistToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            partsToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(partsToggleButton));
            partsToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            stickersToggleButton.setUI((ButtonUI) BasicButtonUI.createUI(stickersToggleButton));
            stickersToggleButton.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            splitPane.setDividerSize(1);
            contextPanel.setBorder(new EmptyBorder(6, 6, 12, 10));
            tabsPanel.setBorder(new EmptyBorder(0, 0, 0, 18));
            //
            toolBar.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            toolBar.setMinimumSize(new Dimension(10, 23));
        } else {
             toolBar.setVisible(false);
        }

        resetButton.setIcon(Icons.get(Icons.PLAYER_RESET_PLACARD));
        Icons.get(Icons.PLAYER_PARTIAL_RESET_PLACARD); // lazily load home icon
        resetButton.putClientProperty("Quaqua.Button.style", "placard");
        resetButton.setMargin(new Insets(0, 2, 0, 2));
        resetButton.setFocusable(false);
        ModifierTracker.addModifierListener(new WeakPropertyChangeListener(propertyHandler));

        resetButton2.putClientProperty("Quaqua.Button.style", "placard");
        resetButton2.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, -1, 0));
        resetButton2.setFocusable(false);
        resetButton2.setIcon(Icons.get(Icons.PLAYER_RESET_PLACARD));


        twistToggleButton.putClientProperty(CustomToggleButtonUI.PROP_BEVEL, CustomButtonUI.NONE);
        twistToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        twistToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, 0));
        twistToggleButton.setFocusable(false);

        partsToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        partsToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, 0));
        partsToggleButton.setFocusable(false);

        stickersToggleButton.putClientProperty("Quaqua.Button.style", "placard");
        stickersToggleButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -1, -1, 0));
        stickersToggleButton.setFocusable(false);

        shapeComboBox.setModel(new KindComboBoxModel());

        tabbedPane.setFont(Fonts.getSmallDialogFont());

        splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
        /*splitPane.setBorder(new Border() {

            private Border placardBorder = new QuaquaPlacardButtonBorder();

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(0, 0, 0, 0);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                int dx = splitPane.getDividerLocation();
                int dw = splitPane.getDividerSize();
                int ph = placardButton.getHeight();

                Graphics gr = g.create(dx, height - ph, dw, ph);
                placardBorder.paintBorder(c, gr, -2, -1, dw + 4, ph + 3);
                gr.setColor(new Color(0xa5a5a5));
                gr.drawLine(0, 0, 0, ph);
                gr.dispose();
            }
        });*/

        partsView.setViewClassName("ch.randelshofer.cubetwister.doc.CubePartsView");
        colorsView.setViewClassName("ch.randelshofer.cubetwister.doc.CubeColorsView");
        infoView.setViewClassName("ch.randelshofer.cubetwister.doc.InfoView");
        stickersView.setViewClassName("ch.randelshofer.cubetwister.doc.CubeStickersView");
        optionsView.setViewClassName("ch.randelshofer.cubetwister.doc.CubeOptionsView");


        splitPane.setDividerLocation(prefs.getInt("CubeView.dividerLocation", 200));
        tabbedPane.setSelectedIndex(selectedTab);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    }

    private Cube3DCanvas getCubeCanvas() {
        if (cubeCanvas == null) {
            cubeCanvas = new JCubeCanvasIdx3D();
            cubeCanvas.setCamera("Front");
            leftPanel.add(cubeCanvas);
            leftPanel.revalidate();
        }
        return cubeCanvas;
    }

    @Override
    public void setModel(EntityModel newValue) {
        setModel((CubeModel) newValue);
    }

    public void setModel(CubeModel value) {
        //long start = System.currentTimeMillis();
        CubeModel oldValue = model;

        if (model != null) {
            model.removePropertyChangeListener(propertyHandler);
            if (model.getDocument() != null) {
                model.getDocument().removePropertyChangeListener(propertyHandler);
            } else if (value != null && value.getDocument() != null) {
                value.getDocument().removePropertyChangeListener(propertyHandler);
            }
        }


        model = value;
        if (model == null) {
            nameTextField.setDocument(new PlainDocument());
            getCubeCanvas().setCube3D(null);
            defaultCheckBox.setEnabled(false);
            updateCube3D();
        } else {
            model.addPropertyChangeListener(propertyHandler);
            model.getDocument().addPropertyChangeListener(propertyHandler);
            nameTextField.setDocument(model.getNameDocument());
            defaultCheckBox.setSelected(model.getDocument().getDefaultCube() == model);
            defaultCheckBox.setEnabled(!defaultCheckBox.isSelected());

            updateCube3D();
            cube3D.getCube().reset();
        }

        updateEnabled();
        partsView.setModel(model);
        colorsView.setModel(model);
        infoView.setModel(model);
        stickersView.setModel(model);
        optionsView.setModel(model);

        ((KindComboBoxModel) shapeComboBox.getModel()).fireContentsChanged();

        getCubeCanvas().reset();
        //long end = System.currentTimeMillis();
        //System.out.println("CubeView.setModel elapsed="+(end-start));
    }

    private void updateCube3D() {
        if (cube3D != null) {
            cube3D.setAttributes(null);
            cube3D.removeCube3DListener(cube3DHandler);
            getCubeCanvas().setCube3D(null);
            cube3D.dispose();
            cube3D = null;
        }
        if (model != null) {
            try {
                cube3D = (Cube3D) model.getCube3DClass().newInstance();
                if (cube == null || cube.getLayerCount() != cube3D.getCube().getLayerCount()) {
                    cube = cube3D.getCube();
                } else {
                    cube3D.setCube(cube);
                }
                cube3D.setAttributes(model);
                cube3D.addCube3DListener(cube3DHandler);
                cube3D.setAnimated(true);
                cube3D.setShowGhostParts(partsToggleButton.isSelected());

                getCubeCanvas().setCube3D(cube3D);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateEnabled() {
        boolean b = model != null && isEnabled();
        nameLabel.setEnabled(b);
        nameTextField.setEnabled(b);
        defaultCheckBox.setEnabled(b && !defaultCheckBox.isSelected());
        shapeLabel.setEnabled(b);
        shapeComboBox.setEnabled(b);

        tabbedPane.setEnabled(b);
        partsView.setEnabled(b);
        colorsView.setEnabled(b);
        infoView.setEnabled(b);
        stickersView.setEnabled(b);
        optionsView.setEnabled(b);
        twistToggleButton.setEnabled(b);
        if (cubeCanvas != null) {
            cubeCanvas.setEnabled(b);
        }
        stickersToggleButton.setEnabled(b);
        partsToggleButton.setEnabled(b);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        // FIXME - Should be: if (isInitialized) {
        if (nameLabel != null) {
            updateEnabled();
        }
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

    public void resetViews() {
        if (cubeCanvas != null) {
            cubeCanvas.reset();
            cubeCanvas.repaint();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup = new javax.swing.ButtonGroup();
        splitPane = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        toolbarPanel = new javax.swing.JPanel();
        controlsPanel = new javax.swing.JPanel();
        resetButton2 = new javax.swing.JButton();
        twistToggleButton = new javax.swing.JToggleButton();
        partsToggleButton = new javax.swing.JToggleButton();
        stickersToggleButton = new javax.swing.JToggleButton();
        rightPanel = new javax.swing.JPanel();
        contextPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        shapeLabel = new javax.swing.JLabel();
        shapeComboBox = new javax.swing.JComboBox();
        defaultCheckBox = new javax.swing.JCheckBox();
        resetButton = new javax.swing.JButton();
        tabsPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        stickersView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        partsView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        colorsView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        optionsView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        infoView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        toolBar = new javax.swing.JToolBar();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        splitPane.setBorder(null);
        splitPane.setDividerLocation(180);
        splitPane.setOneTouchExpandable(true);
        splitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitPaneChanged(evt);
            }
        });

        leftPanel.setMinimumSize(new java.awt.Dimension(100, 100));
        leftPanel.setLayout(new java.awt.BorderLayout());

        toolbarPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        controlsPanel.setLayout(new java.awt.GridBagLayout());

        resetButton2.setToolTipText(labels.getString("resetButton.toolTipText")); // NOI18N
        resetButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        controlsPanel.add(resetButton2, gridBagConstraints);

        buttonGroup.add(twistToggleButton);
        twistToggleButton.setSelected(true);
        twistToggleButton.setText(labels.getString("twistToggle")); // NOI18N
        twistToggleButton.setToolTipText(labels.getString("twistToggle.toolTipText")); // NOI18N
        twistToggleButton.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        controlsPanel.add(twistToggleButton, gridBagConstraints);

        buttonGroup.add(partsToggleButton);
        partsToggleButton.setText(labels.getString("partsToggle")); // NOI18N
        partsToggleButton.setToolTipText(labels.getString("partsToggleTip")); // NOI18N
        partsToggleButton.setRequestFocusEnabled(false);
        partsToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                partsToggled(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        controlsPanel.add(partsToggleButton, gridBagConstraints);

        buttonGroup.add(stickersToggleButton);
        stickersToggleButton.setText(labels.getString("stickersToggle")); // NOI18N
        stickersToggleButton.setToolTipText(labels.getString("stickersToggleTip")); // NOI18N
        stickersToggleButton.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        controlsPanel.add(stickersToggleButton, gridBagConstraints);

        toolbarPanel.add(controlsPanel);

        leftPanel.add(toolbarPanel, java.awt.BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        rightPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        rightPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(labels.getString("entity.name")); // NOI18N

        nameTextField.setColumns(40);

        shapeLabel.setText(labels.getString("cube.shape")); // NOI18N

        defaultCheckBox.setText(labels.getString("cube.useAsDefault")); // NOI18N
        defaultCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultCheckboxPerformed(evt);
            }
        });

        resetButton.setToolTipText(labels.getString("resetButton.toolTipText")); // NOI18N
        resetButton.setRequestFocusEnabled(false);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset(evt);
            }
        });

        javax.swing.GroupLayout contextPanelLayout = new javax.swing.GroupLayout(contextPanel);
        contextPanel.setLayout(contextPanelLayout);
        contextPanelLayout.setHorizontalGroup(
            contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resetButton)
                    .addComponent(shapeLabel)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(shapeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defaultCheckBox)
                    .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                .addContainerGap())
        );
        contextPanelLayout.setVerticalGroup(
            contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(shapeLabel)
                    .addComponent(shapeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(contextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetButton)
                    .addComponent(defaultCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rightPanel.add(contextPanel, gridBagConstraints);

        tabsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 17));
        tabsPanel.setLayout(new java.awt.BorderLayout());

        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPane.setFont(UIManager.getFont("SmallSystemFont"));
        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneChanged(evt);
            }
        });
        tabbedPane.addTab(labels.getString("cube.stickersTab"), stickersView); // NOI18N
        tabbedPane.addTab(labels.getString("cube.partsTab"), partsView); // NOI18N
        tabbedPane.addTab(labels.getString("cube.colorsTab"), colorsView); // NOI18N
        tabbedPane.addTab(labels.getString("cube.optionsTab"), optionsView); // NOI18N
        tabbedPane.addTab(labels.getString("entity.notesTab"), infoView); // NOI18N

        tabsPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 7, 0, 0);
        rightPanel.add(tabsPanel, gridBagConstraints);

        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new java.awt.Dimension(8, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        rightPanel.add(toolBar, gridBagConstraints);

        splitPane.setRightComponent(rightPanel);

        add(splitPane);
    }// </editor-fold>//GEN-END:initComponents

    private void splitPaneChanged(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_splitPaneChanged
        if ("dividerLocation".equals(evt.getPropertyName())) {
            prefs.putInt("CubeView.dividerLocation", ((Integer) evt.getNewValue()).intValue());
        }
    }//GEN-LAST:event_splitPaneChanged

    private void reset(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset
        if ((evt.getModifiers() & (ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)) == 0) {
            cube3D.getCube().reset();
            resetViews();
        } else {
            resetViews();
        }

    }//GEN-LAST:event_reset

    private void partsToggled(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_partsToggled
        //ghostCubeAttributes.setGhosting(partsToggleButton.isSelected());
        if (cube3D != null) {
            cube3D.setShowGhostParts(partsToggleButton.isSelected());
        }
    }//GEN-LAST:event_partsToggled

    private void defaultCheckboxPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultCheckboxPerformed
        // Add your handling code here:
        if (defaultCheckBox.isSelected()) {
            model.getDocument().setDefaultCube(model);
            defaultCheckBox.setEnabled(false);
        }
    }//GEN-LAST:event_defaultCheckboxPerformed

    private void tabbedPaneChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneChanged
        prefs.putInt("CubeView.selectedTab", tabbedPane.getSelectedIndex());
    }//GEN-LAST:event_tabbedPaneChanged

    @Override
    public JComponent getViewComponent() {
        return this;
    }

    public void dispose() {
        //    System.out.println("CubeView.dispose "+this);
        setModel(null);
        removeAll();
        if (cubeCanvas != null) {
            cubeCanvas.dispose();
            cubeCanvas = null;
        }
        if (cube3D != null) {
            cube3D.dispose();
            cube3D = null;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private ch.randelshofer.cubetwister.doc.LazyEntityView colorsView;
    private javax.swing.JPanel contextPanel;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JCheckBox defaultCheckBox;
    private ch.randelshofer.cubetwister.doc.LazyEntityView infoView;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private ch.randelshofer.cubetwister.doc.LazyEntityView optionsView;
    private javax.swing.JToggleButton partsToggleButton;
    private ch.randelshofer.cubetwister.doc.LazyEntityView partsView;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton resetButton2;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JComboBox shapeComboBox;
    private javax.swing.JLabel shapeLabel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JToggleButton stickersToggleButton;
    private ch.randelshofer.cubetwister.doc.LazyEntityView stickersView;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel tabsPanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JToggleButton twistToggleButton;
    // End of variables declaration//GEN-END:variables

    @Override
    protected void finalize() {
        System.out.println("CubeView.finalize ");
    }
}

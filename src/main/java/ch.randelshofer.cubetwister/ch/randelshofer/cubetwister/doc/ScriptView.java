/*
 * @(#)ScriptView.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.Fonts;
import ch.randelshofer.gui.Icons;
import ch.randelshofer.gui.ProgressObserver;
import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.gui.border.BackdropBorder;
import ch.randelshofer.gui.border.ButtonStateBorder;
import ch.randelshofer.gui.border.ImageBevelBorder;
import ch.randelshofer.gui.border.PlacardButtonBorder;
import ch.randelshofer.gui.event.ModifierTracker;
import ch.randelshofer.gui.image.Images;
import ch.randelshofer.gui.plaf.CustomButtonUI;
import ch.randelshofer.gui.text.WavyHighlighter;
import ch.randelshofer.io.ParseException;
import ch.randelshofer.rubik.Cube3DAdapter;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.Cubes;
import ch.randelshofer.rubik.cube.RubiksCube;
import ch.randelshofer.rubik.cube3d.Cube3DEvent;
import ch.randelshofer.rubik.cube3d.Cube3DListener;
import ch.randelshofer.rubik.parser.MoveNode;
import ch.randelshofer.rubik.parser.Node;
import ch.randelshofer.rubik.parser.SequenceNode;
import ch.randelshofer.rubik.solver.CubeParser;
import ch.randelshofer.rubik.solver.FaceletCube;
import ch.randelshofer.rubik.solver.KociembaCube;
import ch.randelshofer.rubik.solver.Solver;
import ch.randelshofer.undo.CompositeEdit;
import ch.randelshofer.undo.Undoable;
import ch.randelshofer.undo.UndoableBooleanEdit;
import ch.randelshofer.util.RunnableWorker;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static java.lang.Math.min;

/**
 * The ScriptView is an editor for ScriptModel's. Users can record,
 * edit and play back scripts. The view features also a state panel,
 * which shows the current permutation of the cube, a macro panel,
 * which can be used to enter local macros for the script, and a
 * info panel for entering comments.
 *
 * @author Werner Randelshofer
 */
public class ScriptView
        extends JPanel
        implements Undoable, EntityView {
    public final static long serialVersionUID = 1L;

    private Color highlightBackground = Color.LIGHT_GRAY;
    private Color highlightForeground = Color.BLACK;
    private JFileChooser exportChooser;
    /**
     * The ScriptModel of the ScriptView.
     */
    private ScriptModel model;
    @Nullable
    private Object wavyLineHighlightTag;
    @Nullable
    private Object playheadHighlightTag;

    /*
    private static class ScriptModelPlayer extends ScriptPlayer {
    private ScriptModel scriptModel;
    public ScriptModelPlayer() {
    }
    public void setScriptModel(ScriptModel newValue) {
    scriptModel = newValue;
    }
    public ScriptModel getScriptModel() {
    return scriptModel;
    }
    public void scramble() {
    scriptModel.scramble();
    }
    };
    private ScriptModelPlayer model.getPlayer();
     */
    // private ScriptPlayer model.getPlayer();
    /**
     * The listeners waiting for UndoableEdit events.
     */
    // private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;
    @Nonnull
    private ChangeListener playerHandler = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent evt) {
            SwingUtilities.invokeLater(
                    new Runnable() {

                        @Override
                        public void run() {
                            updateHighlight();
                        }
                    });
        }
    };
    @Nonnull
    private PropertyChangeListener propertyHandler = new PropertyChangeListener() {

        @Override
        public void propertyChange(@Nonnull PropertyChangeEvent evt) {
            String n = evt.getPropertyName();
            if (evt.getSource() == model) {
                if (n == ScriptModel.CHECKED_PROPERTY) {
                    boolean b = model.isChecked();
                    checkButton.setEnabled(!b);
                    if (b) {
                        scriptTextArea.setToolTipText(null);
                    }
                    if (!b) {
                        model.getPlayer().setScript(null);
                    }
                } else if (n == ScriptModel.NOTATION_PROPERTY) {
                    //cachedParser = null;
                    //updateState();
                } else if (n == ScriptModel.CUBE_PROPERTY) {
                    updateSolver();
                }
                /*} else if (evt.getSource() == model) {
                System.out.println("ScriptView.propertyHandler.propertyChange " + n);
                 */
            } else if (evt.getSource() == ModifierTracker.class) {
                if (n == ModifierTracker.MODIFIERS_EX_PROPERTY) {
                    int modifiersEx = (Integer) evt.getNewValue();
                    resetButton.setIcon(//
                            (modifiersEx & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0//
                                    ? Icons.get(Icons.PLAYER_PARTIAL_RESET) : Icons.get(Icons.PLAYER_RESET));

                }
            }
        }
    };
    @Nullable
    private Cube3DListener scriptRecorder;
    private Preferences prefs;
    /**
     * This listener listens for changes in the script document, and resets
     * the colors of the script.
     */
    @Nonnull
    private DocumentListener scriptColorResetter = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            resetColors();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            resetColors();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //resetColors();
        }

        private void resetColors() {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (model != null && model.getParseException() != null) {
                        model.clearParseException();
                        StyledDocument doc = model.getScriptDocument();
                        doc.setCharacterAttributes(0, doc.getLength(), new SimpleAttributeSet(), true);
                        scriptTextArea.setCharacterAttributes(new SimpleAttributeSet(), true);
                    }
                    if (wavyLineHighlightTag != null) {
                        scriptTextArea.getHighlighter().removeHighlight(wavyLineHighlightTag);
                        wavyLineHighlightTag = null;
                    }
                }
            });
        }
    };

    /**
     * Creates a new instance with a default model.
     */
    public ScriptView() {
        init();
    }

    /**
     * Creates a new instance and connects it to
     * the given ScriptModel.
     */
    public ScriptView(ScriptModel m) {
        init();
        setModel(m);
    }

    /**
     * Initialises the view.
     * This method must be called exactly once per instance.
     * Multiple calls lead to undefined results.
     */
    private void init() {
        prefs = Preferences.userNodeForPackage(getClass());
        labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));

        // Initialise the components as far as the IDE supports it.
        initComponents();

        // Decorate the reset button with an icon looking like '|<' and
        // a west bevel.
        Border eastBorder = new BackdropBorder(
                new ButtonStateBorder(
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderEast.png"),
                new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4)),
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderEastP.png"),
                new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4))));
        Border westBorder = new BackdropBorder(
                new ButtonStateBorder(
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderWest.png"),
                new Insets(1, 1, 1, 0), new Insets(0, 4, 1, 4)),
                new ImageBevelBorder(
                Images.createImage("org.monte.media", "/org/monte/media/swing/player/images/Player.borderWestP.png"),
                new Insets(1, 1, 1, 0), new Insets(0, 4, 1, 4))));

        resetButton.setIcon(Icons.get(Icons.PLAYER_RESET));
        resetButton.setDisabledIcon(Icons.get(Icons.PLAYER_RESET_DISABLED));
        Icons.get(Icons.PLAYER_PARTIAL_RESET); // lazily load home icon
        resetButton.setUI((ButtonUI) CustomButtonUI.createUI(resetButton));
        resetButton.setBorder(westBorder);
        resetButton.setMargin(new Insets(0, 0, 0, 0));
        resetButton.setPreferredSize(new Dimension(16, 16));
        resetButton.setMinimumSize(resetButton.getPreferredSize());
        ModifierTracker.addModifierListener(new WeakPropertyChangeListener(propertyHandler));

        // Decorate the reset button with an icon looking like 'o' and a center bevel.
        recordButton.setIcon(Icons.get(Icons.PLAYER_START_RECORDING));
        recordButton.setDisabledIcon(Icons.get(Icons.PLAYER_START_RECORDING_DISABLED));
        recordButton.setUI((ButtonUI) CustomButtonUI.createUI(recordButton));
        recordButton.setBorder(westBorder);
        recordButton.setMargin(new Insets(0, 0, 0, 0));
        recordButton.setPreferredSize(new Dimension(16, 16));
        recordButton.setMinimumSize(recordButton.getPreferredSize());

        // Decorate the check button with an icon looking like a tick '/' and an east bevel.
        checkButton.setIcon(Icons.get(Icons.SCRIPT_CHECK));
        checkButton.setDisabledIcon(Icons.get(Icons.SCRIPT_CHECK_DISABLED));
        checkButton.setUI((ButtonUI) CustomButtonUI.createUI(checkButton));
        checkButton.setBorder(eastBorder);
        checkButton.setMargin(new Insets(0, 0, 0, 0));
        checkButton.setPreferredSize(new Dimension(16, 16));
        checkButton.setMinimumSize(checkButton.getPreferredSize());

        // Decorate the popup button with an icon looking like a small popup menu.
        popupButton.setIcon(Icons.get(Icons.PLAYER_ACTIONS));
        popupButton.setDisabledIcon(Icons.get(Icons.PLAYER_ACTIONS_DISABLED));
        popupButton.setUI((ButtonUI) CustomButtonUI.createUI(popupButton));
        popupButton.setBorder(eastBorder);
        popupButton.setMargin(new Insets(0, 0, 0, 0));
        popupButton.setPreferredSize(new Dimension(16, 16));
        popupButton.setMinimumSize(popupButton.getPreferredSize());

        // Rearange the position of the player controls: put them between
        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
            outerControlsPanel.setBorder(new EmptyBorder(0, 3, 0, 3));
            // scriptScrollPane.putClientProperty("Quaqua.Component.visualMargin", new Insets(-1,3,3,3));
        }


        // Install a popup menu listener on all components
        MouseAdapter popupListener = new MouseAdapter() {

            @Override
            public void mousePressed(@Nonnull MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showPopup(evt, false);
                }
            }

            @Override
            public void mouseReleased(@Nonnull MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    showPopup(evt, false);
                }
            }
        };
        addMouseListener(popupListener);
        componentsPanel.addMouseListener(popupListener);
        controlsPanel.addMouseListener(popupListener);
        scriptTextArea.addMouseListener(popupListener);

        Color bg = UIManager.getColor("TextField.background");

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
                int ph = toolBar.getHeight();

                Graphics gr = g.create(dx, height - ph, dw, ph);
                placardBorder.paintBorder(c, gr, -2, -1, dw + 4, ph + 3);
                gr.setColor(new Color(0xa5a5a5));
                gr.drawLine(0, 0, 0, ph);
                gr.dispose();
            }
        });*/

        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) {
            toolBar.putClientProperty("Quaqua.ToolBar.style", "gradient");
            toolBar.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, -2, -1, -1));
            toolBar.setPreferredSize(new Dimension(24, 24));
            toolBar.setMinimumSize(new Dimension(24, 24));
        } else if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            toolBar.setBorder(new PlacardButtonBorder(SwingConstants.RIGHT));
            toolBar.setMinimumSize(new Dimension(10, 23));
            splitPane.setDividerSize(1);
            editorPanel.setBorder(new EmptyBorder(1, 20, 8, 20));
            tabsPanel.setBorder(new EmptyBorder(8, 12, 4, 1));
        } else {
            toolBar.setVisible(false);
        }

        infoView.setViewClassName("ch.randelshofer.cubetwister.doc.InfoView");
        macrosView.setViewClassName("ch.randelshofer.cubetwister.doc.ScriptMacrosView");
        optionsView.setViewClassName("ch.randelshofer.cubetwister.doc.ScriptOptionsView");
        secondaryView.setViewClassName("ch.randelshofer.cubetwister.doc.ScriptSecondaryView");
        stateView.setViewClassName("ch.randelshofer.cubetwister.doc.ScriptStateView");

        primaryView.setModel(model);
        toolBarView.setModel(model);
        toolBarView.setScriptView(this);
        /*stateView.setScriptView(this);*/
        splitPane.setDividerLocation(prefs.getInt("ScriptView.dividerLocation", 200));

        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        PreferencesUtil.installTabbedPanePrefsHandler(prefs, "ScriptView.selectedTab", tabbedPane);
    }

    public void setModel(ScriptModel newValue) {
        recordButton.getModel().setSelected(false);

        if (model != null) {
            model.getPlayer().stop();
            model.removePropertyChangeListener(propertyHandler);
            model.getScriptDocument().removeDocumentListener(scriptColorResetter);
            model.getPlayer().removeChangeListener(playerHandler);
            if (scriptRecorder != null) {
                model.getCube3D().removeCube3DListener(scriptRecorder);
            }
        }
        model = newValue;
        if (model == null) {
            nameTextField.setDocument(new PlainDocument());
            scriptTextArea.setDocument(new DefaultStyledDocument());
            controlsPanel.removeAll();
        } else {
            model.addPropertyChangeListener(propertyHandler);
            nameTextField.setDocument(model.getNameDocument());
            scriptTextArea.setDocument(model.getScriptDocument());
            model.getScriptDocument().addDocumentListener(scriptColorResetter);
            checkButton.setEnabled(!model.isChecked());
            scriptTextArea.setToolTipText(null);
            // Install a change listener on the player, which
            // highlights the current token in the script text area
            // when the play head moves.
            model.getPlayer().addChangeListener(playerHandler);
            controlsPanel.removeAll();
            controlsPanel.add(model.getPlayer().getControlPanelComponent());
            controlsPanel.validate();
            check(null);
            /*
            try {
            model.check();
            } catch (IOException e) {
            // Do nothing
            }*/
            model.reset();
            if (model.isGenerator()) {
                model.getPlayer().getTimeModel().setValue(model.getPlayer().getTimeModel().getMaximum());
            } else {
                model.getPlayer().getTimeModel().setValue(model.getPlayer().getTimeModel().getMinimum());
            }
            model.getPlayer().setHandle3DEvents(false);
            if (scriptRecorder != null) {
                model.getCube3D().addCube3DListener(scriptRecorder);
            }
        }

        stateView.setModel(model);
        toolBarView.setModel(model);
        primaryView.setModel(model);
        secondaryView.setModel(model);
        macrosView.setModel(model);
        infoView.setModel(model);
        optionsView.setModel(model);
        //model.getSolverModel().reset();
        updateEnabled();
        if (model != null) {
            updateSolver();
        }
    }

    public ScriptModel getModel() {
        return model;
    }

    private void updateSolver() {
        boolean b = model.getSolverModel().isSolverSupported();
        resetStickersItem.setEnabled(b);
        clearStickersItem.setEnabled(b);
        fillStickersItem.setEnabled(b);
        solveCubeItem.setEnabled(b);
        generateCubeItem.setEnabled(b);
    }

    /**
     * Dynamically creates and shows the popup menu if the component is enabled.
     *
     * @param evt                 The mouse event which triggered the popup menu.
     * @param isTriggeredByButton This parameter is set to true, when the
     *                            popup menu has been triggered by a button. The popup menu must
     *                            be placed outside the bounds of the buttons.
     */
    public void showPopup(@Nonnull MouseEvent evt, boolean isTriggeredByButton) {
        if (isEnabled() && model != null) {
            cubeMenu.removeAll();
            translateMenu.removeAll();
            notationMenu.removeAll();

            // Create the Translate Menu and Notation Menu
            ButtonGroup radioGroup = new ButtonGroup();
            JRadioButtonMenuItem radioItem;
            for (EntityModel node : model.getDocument().getNotations().getChildren()) {
                final NotationModel notation = (NotationModel) node;

                if (notation.getLayerCount() == model.getCube().getLayerCount()) {

                    radioItem = new JRadioButtonMenuItem(notation.getName());
                    radioGroup.add(radioItem);
                    JMenuItem menuItem = new JMenuItem(notation.toString());
                    if (!model.isUsingDefaultNotation() && notation == model.getNotationModel()) {
                        radioItem.setSelected(true);

                    }
                    notationMenu.add(radioItem);
                    translateMenu.add(menuItem);
                    radioItem.addItemListener(
                            new ItemListener() {

                                @Override
                                public void itemStateChanged(@Nonnull ItemEvent evt) {
                                    if (evt.getStateChange() == ItemEvent.SELECTED) {
                                        model.setNotationModel(notation);
                                    }
                                }
                            });
                    menuItem.addActionListener(
                            new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    translateInto(notation);
                                }
                            });
                }
            }
            notationMenu.addSeparator();
            radioItem = new JRadioButtonMenuItem("Default (" + model.getDocument().getDefaultNotation(model.getCube().getLayerCount()) + ")");
            radioGroup.add(radioItem);
            radioItem.setSelected(model.isUsingDefaultNotation());
            notationMenu.add(radioItem);
            radioItem.addItemListener(
                    new ItemListener() {

                        @Override
                        public void itemStateChanged(@Nonnull ItemEvent evt) {
                            if (evt.getStateChange() == ItemEvent.SELECTED) {
                                model.setNotationModel(null);
                            }
                        }
                    });

            // Create the cube menu
            radioGroup = new ButtonGroup();
            for (EntityModel node : model.getDocument().getCubes().getChildren()) {
                final CubeModel cube = (CubeModel) node;
                radioItem = new JRadioButtonMenuItem(cube.getName());
                radioGroup.add(radioItem);
                if (!model.isUsingDefaultCube() && cube == model.getCubeModel()) {
                    radioItem.setSelected(true);
                }
                cubeMenu.add(radioItem);
                radioItem.addItemListener(
                        new ItemListener() {

                            @Override
                            public void itemStateChanged(@Nonnull ItemEvent evt) {
                                if (evt.getStateChange() == ItemEvent.SELECTED) {
                                    model.setCubeModel(cube);
                                }
                            }
                        });
            }
            cubeMenu.addSeparator();
            radioItem = new JRadioButtonMenuItem("Default (" + model.getDocument().getDefaultCube() + ")");
            radioGroup.add(radioItem);
            radioItem.setSelected(model.isUsingDefaultCube());
            cubeMenu.add(radioItem);
            radioItem.addItemListener(
                    new ItemListener() {

                        @Override
                        public void itemStateChanged(@Nonnull ItemEvent evt) {
                            if (evt.getStateChange() == ItemEvent.SELECTED) {
                                model.setCubeModel(null);
                            }
                        }
                    });

            Component c = evt.getComponent();
            if (isTriggeredByButton && c.getGraphicsConfiguration() != null) {
                // If the popup menu is triggered by a button, it is
                // presented at one of the borders of the button.
                Point cScreenLocation = c.getLocationOnScreen();
                Rectangle cBounds = c.getBounds();
                Rectangle screenBounds = c.getGraphicsConfiguration().getBounds();
                Dimension pDimension = popupMenu.getPreferredSize();

                if (screenBounds.contains(cScreenLocation.x, cScreenLocation.y + cBounds.height, pDimension.width, pDimension.height)) {
                    popupMenu.show(c, 0, cBounds.height);
                } else if (screenBounds.contains(cScreenLocation.x + cBounds.width - pDimension.width, cScreenLocation.y + cBounds.height, pDimension.width, pDimension.height)) {
                    popupMenu.show(c, cBounds.width - pDimension.width, cBounds.height);
                } else if (screenBounds.contains(cScreenLocation.x, cScreenLocation.y - pDimension.height, pDimension.width, pDimension.height)) {
                    popupMenu.show(c, 0, -pDimension.height);
                } else {
                    popupMenu.show(c, cBounds.width - pDimension.width, -pDimension.height);
                }
            } else {
                // If the popup menu is triggered by a mouse event, it is
                // presented at the location where the mouse event happened
                popupMenu.show(c, evt.getX(), evt.getY());
            }
        }
    }

    public void translateInto(NotationModel n) {
        try {
            model.translateInto(n);
            check(null);
        } catch (ch.randelshofer.io.ParseException e) {
            model.getPlayer().setScript(new SequenceNode());
            scriptTextArea.setToolTipText(e.getMessage() + "@" + e.getStartPosition() + ".." + e.getEndPosition());
            scriptTextArea.setCaretPosition(e.getStartPosition());
            scriptTextArea.moveCaretPosition(e.getEndPosition() + 1);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Translation failed", JOptionPane.INFORMATION_MESSAGE);
            scriptTextArea.requestFocus();
        } catch (IOException e) {
            model.getPlayer().setScript(new SequenceNode());
            scriptTextArea.setToolTipText(e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Translation failed", JOptionPane.INFORMATION_MESSAGE);
            scriptTextArea.requestFocus();
        }
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        updateEnabled();
    }

    public void updateEnabled() {
        boolean b = model != null && isEnabled();

        java.awt.Component[] c = componentsPanel.getComponents();
        for (int i = 0; i < c.length; i++) {
            c[i].setEnabled(b);
        }
        scriptTextArea.setEnabled(b);

        if (model != null) {
            model.getPlayer().setEnabled(b);
        }
        infoView.setEnabled(b);
        macrosView.setEnabled(b);
        optionsView.setEnabled(b);
        primaryView.setEnabled(b);
        controlsPanel.setEnabled(b);

        recordButton.setEnabled(b);
        checkButton.setEnabled((model != null) ? !model.isChecked() & b : false);
        popupButton.setEnabled(b);
        resetButton.setEnabled(b);
        scriptScrollPane.setEnabled(b);

        if (b == false) {
            popupMenu.setVisible(false);
        }
    }

    public void updateHighlight() {
        if (model == null) {
            return;
        }

        // Remove previously added highlights
        if (wavyLineHighlightTag != null) {
            scriptTextArea.getHighlighter().removeHighlight(wavyLineHighlightTag);
            wavyLineHighlightTag = null;
        }
        if (playheadHighlightTag != null) {
            scriptTextArea.getHighlighter().removeHighlight(playheadHighlightTag);
            playheadHighlightTag = null;
        }


        Node current = model.getPlayer().getCurrentNode();
        boolean isProcessing = model.getPlayer().isProcessingCurrentNode();

        StyledDocument doc = (StyledDocument) scriptTextArea.getDocument();
        if (doc.getLength() > 0) {
            CompositeEdit ce = new CompositeEdit("Highlight", false);
            fireUndoableEditHappened(ce);

            MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setForeground(attr, Color.black);
            doc.setCharacterAttributes(0, doc.getLength(), attr, true);

            if (model.isChecked()) {
                if (current != null) {
                    /*
                    attr = new SimpleAttributeSet();
                    StyleConstants.setBackground(attr, highlightBackground);
                    StyleConstants.setForeground(attr, highlightForeground);

                    doc.setCharacterAttributes(
                    current.getStartPosition(),
                    current.getEndPosition() - current.getStartPosition() + 1,
                    attr,
                    true);
                    scriptTextArea.setCaretPosition(
                    current.getStartPosition() //isProcessing ? current.getStartPosition() : current.getEndPosition() + 1
                    );*/
                    DefaultHighlighter.DefaultHighlightPainter dh = new DefaultHighlighter.DefaultHighlightPainter(highlightBackground);
                    try {
                        playheadHighlightTag = scriptTextArea.getHighlighter().addHighlight(current.getStartPosition(), current.getEndPosition() + 1, dh);
                    } catch (BadLocationException ble) {
                    }
                } else {
                    scriptTextArea.setCaretPosition(doc.getLength());
                }

            } else {
                ParseException parseException;
                if ((parseException = model.getParseException()) != null) {
                    /*attr = new SimpleAttributeSet();

                    StyleConstants.setBackground(attr, new Color(255, 140, 140));
                    StyleConstants.setForeground(attr, Color.black);
                    doc.setCharacterAttributes(
                    parseException.getStartPosition(),
                    parseException.getEndPosition() - parseException.getStartPosition() + 1,
                    attr,
                    true);*/
                    WavyHighlighter red = new WavyHighlighter(Color.RED);

                    try {
                        wavyLineHighlightTag = scriptTextArea.getHighlighter().addHighlight(parseException.getStartPosition(), parseException.getEndPosition() + 1, red);
                    } catch (BadLocationException ble) {
                    }
                }
            }
            fireUndoableEditHappened(ce);
        }
    }

    public void resetViews() {
        primaryView.reset();
        primaryView.repaint();
        if (secondaryView != null) {
            secondaryView.repaint();
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

        popupMenu = new javax.swing.JPopupMenu();
        cubeMenu = new javax.swing.JMenu();
        notationMenu = new javax.swing.JMenu();
        translateMenu = new javax.swing.JMenu();
        transformMenu = new javax.swing.JMenu();
        CRItem = new javax.swing.JMenuItem();
        CUItem = new javax.swing.JMenuItem();
        CFItem = new javax.swing.JMenuItem();
        CLItem = new javax.swing.JMenuItem();
        CDItem = new javax.swing.JMenuItem();
        CBItem = new javax.swing.JMenuItem();
        InverseItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        resetStickersItem = new javax.swing.JMenuItem();
        clearStickersItem = new javax.swing.JMenuItem();
        fillStickersItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        solveCubeItem = new javax.swing.JMenuItem();
        generateCubeItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        exportVideoItem = new javax.swing.JMenuItem();
        toolbarGroup = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        splitPane = new javax.swing.JSplitPane();
        componentsPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        editorPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        outerControlsPanel = new javax.swing.JPanel();
        resetButton = new javax.swing.JButton();
        recordButton = new javax.swing.JToggleButton();
        controlsPanel = new javax.swing.JPanel();
        checkButton = new javax.swing.JButton();
        popupButton = new javax.swing.JButton();
        scriptScrollPane = new javax.swing.JScrollPane();
        scriptTextArea = new javax.swing.JTextPane();
        lowerPanel = new javax.swing.JPanel();
        tabsPanel = new javax.swing.JPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        stateView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        macrosView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        secondaryView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        optionsView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        infoView = new ch.randelshofer.cubetwister.doc.LazyEntityView();
        toolBar = new javax.swing.JToolBar();
        leftPanel = new javax.swing.JPanel();
        primaryView = new ch.randelshofer.cubetwister.doc.ScriptPrimaryView();
        toolBarView = new ch.randelshofer.cubetwister.doc.ScriptToolBarView();

        cubeMenu.setText(labels.getString("cubeMenu")); // NOI18N
        popupMenu.add(cubeMenu);

        notationMenu.setText(labels.getString("notationMenu")); // NOI18N
        popupMenu.add(notationMenu);

        translateMenu.setText(labels.getString("translateMenu")); // NOI18N
        popupMenu.add(translateMenu);

        transformMenu.setText("Transform Script");
        transformMenu.setEnabled(false);

        CRItem.setText("Rotate Right");
        CRItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(@Nonnull java.awt.event.ActionEvent evt) {
                transformScript(evt);
            }
        });
        transformMenu.add(CRItem);

        CUItem.setText("Rotate Up");
        CUItem.setEnabled(false);
        transformMenu.add(CUItem);

        CFItem.setText("Rotate Front");
        CFItem.setEnabled(false);
        transformMenu.add(CFItem);

        CLItem.setText("Rotate Left");
        CLItem.setEnabled(false);
        transformMenu.add(CLItem);

        CDItem.setText("Rotate Down");
        CDItem.setEnabled(false);
        transformMenu.add(CDItem);

        CBItem.setText("Rotate Back");
        CBItem.setEnabled(false);
        transformMenu.add(CBItem);

        InverseItem.setText("Inverse");
        InverseItem.setEnabled(false);
        transformMenu.add(InverseItem);

        popupMenu.add(transformMenu);
        popupMenu.add(jSeparator1);

        resetStickersItem.setText(labels.getString("resetStickersItem")); // NOI18N
        resetStickersItem.setEnabled(false);
        resetStickersItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetStickers(evt);
            }
        });
        popupMenu.add(resetStickersItem);

        clearStickersItem.setText(labels.getString("clearStickersItem")); // NOI18N
        clearStickersItem.setEnabled(false);
        clearStickersItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear(evt);
            }
        });
        popupMenu.add(clearStickersItem);

        fillStickersItem.setText(labels.getString("fillStickersItem")); // NOI18N
        fillStickersItem.setEnabled(false);
        fillStickersItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fill(evt);
            }
        });
        popupMenu.add(fillStickersItem);
        popupMenu.add(jSeparator3);

        solveCubeItem.setText(labels.getString("solveCubeItem")); // NOI18N
        solveCubeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solve(evt);
            }
        });
        popupMenu.add(solveCubeItem);

        generateCubeItem.setText(labels.getString("generateCubeItem")); // NOI18N
        generateCubeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generate(evt);
            }
        });
        popupMenu.add(generateCubeItem);
        popupMenu.add(jSeparator2);

        exportVideoItem.setText("Export as Video…");
        exportVideoItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportVideo(evt);
            }
        });
        popupMenu.add(exportVideoItem);

        jToolBar1.setRollover(true);

        setLayout(new java.awt.BorderLayout());

        splitPane.setBorder(null);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);
        splitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(@Nonnull java.beans.PropertyChangeEvent evt) {
                splitPaneChanged(evt);
            }
        });

        componentsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 0, 0, 0));
        componentsPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        componentsPanel.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        editorPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 17));
        editorPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(labels.getString("entity.name")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        editorPanel.add(nameLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 0);
        editorPanel.add(nameTextField, gridBagConstraints);

        outerControlsPanel.setLayout(new java.awt.GridBagLayout());

        resetButton.setToolTipText(labels.getString("resetTip")); // NOI18N
        resetButton.setFocusable(false);
        resetButton.setRequestFocusEnabled(false);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(@Nonnull java.awt.event.ActionEvent evt) {
                reset(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        outerControlsPanel.add(resetButton, gridBagConstraints);

        recordButton.setToolTipText(labels.getString("recordTip")); // NOI18N
        recordButton.setFocusable(false);
        recordButton.setRequestFocusEnabled(false);
        recordButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                recordButtonStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        outerControlsPanel.add(recordButton, gridBagConstraints);

        controlsPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        outerControlsPanel.add(controlsPanel, gridBagConstraints);

        checkButton.setToolTipText(labels.getString("checkTip")); // NOI18N
        checkButton.setFocusable(false);
        checkButton.setRequestFocusEnabled(false);
        checkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        outerControlsPanel.add(checkButton, gridBagConstraints);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/randelshofer/cubetwister/Labels"); // NOI18N
        popupButton.setToolTipText(bundle.getString("popupTip")); // NOI18N
        popupButton.setFocusable(false);
        popupButton.setRequestFocusEnabled(false);
        popupButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(@Nonnull java.awt.event.MouseEvent evt) {
                popup(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        outerControlsPanel.add(popupButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        editorPanel.add(outerControlsPanel, gridBagConstraints);

        scriptScrollPane.setViewportView(scriptTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        editorPanel.add(scriptScrollPane, gridBagConstraints);

        jSplitPane1.setLeftComponent(editorPanel);

        lowerPanel.setLayout(new java.awt.BorderLayout());

        tabsPanel.setLayout(new java.awt.BorderLayout());

        tabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 17));
        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPane.addTab(labels.getString("script.stateTab"), stateView); // NOI18N
        tabbedPane.addTab(labels.getString("script.macrosTab"), macrosView); // NOI18N
        tabbedPane.addTab(labels.getString("script.viewTab"), secondaryView); // NOI18N
        tabbedPane.addTab(labels.getString("script.optionsTab"), optionsView); // NOI18N
        tabbedPane.addTab(labels.getString("entity.notesTab"), infoView); // NOI18N

        tabsPanel.add(tabbedPane, java.awt.BorderLayout.CENTER);

        lowerPanel.add(tabsPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(lowerPanel);

        componentsPanel.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        toolBar.setFloatable(false);
        toolBar.setPreferredSize(new java.awt.Dimension(8, 26));
        componentsPanel.add(toolBar, java.awt.BorderLayout.SOUTH);

        splitPane.setRightComponent(componentsPanel);

        leftPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        leftPanel.setLayout(new java.awt.BorderLayout());
        leftPanel.add(primaryView, java.awt.BorderLayout.CENTER);
        leftPanel.add(toolBarView, java.awt.BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void splitPaneChanged(@Nonnull java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_splitPaneChanged
        if ("dividerLocation".equals(evt.getPropertyName())) {
            prefs.putInt("ScriptView.dividerLocation", ((Integer) evt.getNewValue()).intValue());
        }

    }//GEN-LAST:event_splitPaneChanged

    private void popup(@Nonnull java.awt.event.MouseEvent evt) {//GEN-FIRST:event_popup
        showPopup(evt, true);

    }//GEN-LAST:event_popup

    private void generate(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generate
        model.getPlayer().stop();
        recordButton.getModel().setSelected(false);

        if (model.getProgressView() != null) {
            JOptionPane.showMessageDialog(
                    ScriptView.this,
                    "<html><font face=Dialog><b>You can not run more than one operation on a script.</b><br><font size=-1>Another operation is already in progress on this script.<br>Please wait until it has finished.",
                    "Cube Twister: Generate Cube",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        if (!model.getSolverModel().isSolveable()) {
            JOptionPane.showMessageDialog(
                    ScriptView.this,
                    "<html><font face=Dialog><b>Can not generate this cube.</b><br><font size=-1>Couldn't translate the stickers<br>into a permutation of the cube.",
                    "Cube Twister: Generate Cube",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final Cube rcube;
        if (model.getSolverModel().isPainting()) {
            rcube = model.getSolverModel().getMappedCube((RubiksCube) model.getPlayer().getCube());
            model.getSolverModel().reset();
            model.getPlayer().getCube().setTo(rcube);
        } else {
            rcube = (RubiksCube) model.getPlayer().getCube().clone();
        }

        final ScriptModel m = model;
        final ProgressObserver progressMonitor = new ProgressView("Generating \"" + m.getName() + "\"", "Waiting for Processor...", 0, Integer.MAX_VALUE);
        m.setProgressView(progressMonitor);
        progressMonitor.setDoCancel(new Runnable() {

            @Override
            public void run() {
                if (m.getProgressView() == progressMonitor) {
                    m.setProgressView(null);
                    progressMonitor.complete();
                }
            }
        });
        m.getDocument().dispatchSolver(
                new RunnableWorker<Object>() {

                    @Nullable
                    @Override
                    public Object construct() {
                        try {
                            if (progressMonitor.isCanceled()) {
                                return null;
                            }

                            // Parse the input and initialize a "FaceletCube".
                            // The FaceletCube represents the cube by the markings
                            //   of the 54 individual facelets.
                            FaceletCube faceletCube = new FaceletCube();
                            CubeParser cubeParser = new CubeParser();

                            int status = cubeParser.parseInput(Cubes.toNormalizedStickersString(rcube), faceletCube);
                            if (status != CubeParser.VALID) {
                                return new ParseException(cubeParser.getErrorText(status));
                            }

                            // Validate the facelet representation in terms  of
                            //  legal cubie markings, permutation, and parity
                            //   and initialize a "standard" cube.  The standard
                            //   cube represents the cube state in terms of cubie
                            //   permutation and parity.
                            KociembaCube kcube = new KociembaCube();
                            if ((status = faceletCube.validate(kcube)) != FaceletCube.VALID) {
                                return new ParseException(faceletCube.getErrorText(status));
                            }

                            // Create a solver, initialize the move mapping and
                            //   pruning tables, and invoke the search for a
                            //   solution.  Since the cube is in a valid configuration
                            //   at this point, a solution should always be found.
                            Solver solver = new Solver();
                            int result = solver.solve(progressMonitor, kcube, model.getNotationModel());
                            if (result == Solver.ABORT) {
                                return null;
                            } else {
                                SequenceNode solution = solver.getSolution();
                                solution.invert();
                                solution.transformOrientation(rcube.getLayerCount(), rcube.getCubeOrientation(), false);
                                return solution.toString(model.getNotationModel());
                            }
                        } catch (Throwable e) {
                            return e;
                        }
                    }

                    @Override
                    public void finished(Object result) {
                        progressMonitor.complete();
                        if (m.getProgressView() == progressMonitor) {
                            m.setProgressView(null);
                        }
                        if (result instanceof String) {
                            CompositeEdit edit = new CompositeEdit("Generate Cube");
                            fireUndoableEditHappened(edit);
                            m.setScript((String) result);
                            m.setGenerator(true);
                            if (m == ScriptView.this.model) {
                                check(null);
                            }
                            fireUndoableEditHappened(edit);
                        } else if (result instanceof Throwable) {
                            Throwable e = (Throwable) result;
                            JOptionPane.showMessageDialog(
                                    ScriptView.this,
                                    "<html><font face=Dialog><b>Generator failed</b><br><font size=-1>" + e.getMessage(),
                                    "Cube Twister: Generate Cube",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
    }//GEN-LAST:event_generate

    private void resetStickers(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetStickers
        model.getSolverModel().reset();

    }//GEN-LAST:event_resetStickers

    private void fill(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fill
        model.getSolverModel().fill();
    }//GEN-LAST:event_fill

    private void clear(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clear
        model.getSolverModel().clear();
    }//GEN-LAST:event_clear

    private void solve(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solve
        model.getPlayer().stop();
        recordButton.getModel().setSelected(false);

        if (model.getProgressView() != null) {
            JOptionPane.showMessageDialog(
                    ScriptView.this,
                    "<html><font face=Dialog><b>You can not run more than one operation on a script.</b><br><font size=-1>Another operation is already in progress on this script.<br>Please wait until it has finished.",
                    "Cube Twister: Solve Cube",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (!model.getSolverModel().isSolveable()) {
            JOptionPane.showMessageDialog(
                    ScriptView.this,
                    "<html><font face=Dialog><b>Cannot solve this cube.</b><br><font size=-1>Couldn't translate the stickers<br>into a permutation of the cube.",
                    "Cube Twister: Solve Cube",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final Cube rcube;
        if (model.getSolverModel().isPainting()) {
            rcube = model.getSolverModel().getMappedCube(model.getPlayer().getCube());
            //model.getPlayer().getCube().setLastFrame(rcube);
        } else {
            rcube = (Cube) model.getPlayer().getCube().clone();
        }

        final ScriptModel m = model;
        final ProgressObserver progressMonitor = new ProgressView("Solving \"" + m.getName() + "\"", "Waiting for Processor...", 0, Integer.MAX_VALUE);
        m.setProgressView(progressMonitor);
        progressMonitor.setDoCancel(new Runnable() {

            @Override
            public void run() {
                if (m.getProgressView() == progressMonitor) {
                    m.setProgressView(null);
                    progressMonitor.complete();
                }
            }
        });
        m.getDocument().dispatchSolver(
                new RunnableWorker<Object>() {

                    @Nullable
                    @Override
                    public Object construct() {
                        try {
                            if (progressMonitor.isCanceled()) {
                                return null;
                            }

                            // Parse the input and initialize a "FaceletCube".
                            // The FaceletCube represents the cube by the markings
                            //   of the 54 individual facelets.
                            FaceletCube faceletCube = new FaceletCube();
                            CubeParser cubeParser = new CubeParser();

                            int status = cubeParser.parseInput(Cubes.toNormalizedStickersString(rcube), faceletCube);
                            if (status != CubeParser.VALID) {
                                return new ParseException(cubeParser.getErrorText(status));
                            }

                            // Validate the facelet representation in terms  of
                            //  legal cubie markings, permutation, and parity
                            //   and initialize a "standard" cube.  The standard
                            //   cube represents the cube state in terms of cubie
                            //   permutation and parity.
                            KociembaCube kcube = new KociembaCube();
                            if ((status = faceletCube.validate(kcube)) != FaceletCube.VALID) {
                                return new ParseException(faceletCube.getErrorText(status));
                            }

                            // Create a solver, initialize the move mapping and
                            //   pruning tables, and invoke the search for a
                            //   solution.  Since the cube is in a valid configuration
                            //   at this point, a solution should always be found.
                            Solver solver = new Solver();
                            int result = solver.solve(progressMonitor, kcube, model.getNotationModel());
                            if (result == Solver.ABORT) {
                                return null;
                            } else {
                                SequenceNode solution = solver.getSolution();
                                solution.transformOrientation(rcube.getLayerCount(), rcube.getCubeOrientation(), true);
                                return solution.toString(model.getNotationModel());
                            }
                        } catch (Throwable e) {
                            return e;
                        }
                    }

                    @Override
                    public void finished(Object result) {
                        progressMonitor.close();
                        if (m.getProgressView() == progressMonitor) {
                            m.setProgressView(null);
                        }

                        if (result instanceof String) {
                            model.getSolverModel().reset();
                            CompositeEdit edit = new CompositeEdit("Solve Cube");
                            fireUndoableEditHappened(edit);
                            m.setScript((String) result);
                            m.setGenerator(false);
                            if (m == ScriptView.this.model) {
                                check(null);
                            }
                            fireUndoableEditHappened(edit);
                        } else if (result instanceof Throwable) {
                            Throwable e = (Throwable) result;
                            JOptionPane.showMessageDialog(
                                    ScriptView.this,
                                    "<html><font face=Dialog><b>Solver failed</b><br><font size=-1>" + e.getMessage(),
                                    "Cube Twister: Solve Cube",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
    }//GEN-LAST:event_solve

    private void recordButtonStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_recordButtonStateChanged
        boolean s = recordButton.isSelected();

        if (s) {
            model.getPlayer().stop();
            // twistToggleButton.setSelected(true);
            if (scriptRecorder == null) {
                scriptRecorder = new Cube3DAdapter() {

                    @Override
                    public void actionPerformed(@Nonnull Cube3DEvent evt) {
                        model.getPlayer().stop();

                        try {
                            MoveNode mn = new MoveNode(evt.getCube().getLayerCount(), evt.getAxis(), evt.getLayerMask(), evt.getAngle(), 0, 0);
                            String recorded = mn.toString(model.getNotationModel());
                            String existingText = scriptTextArea.getText();
                            if (scriptTextArea.getSelectionStart() > 0 && existingText.charAt(scriptTextArea.getSelectionStart() - 1) != ' ') {
                                recorded = " " + recorded;
                            }
                            if (scriptTextArea.getSelectionEnd() < existingText.length() - 1 && existingText.charAt(scriptTextArea.getSelectionEnd()) != ' ') {
                                recorded += " ";
                            }
                            MutableAttributeSet attr = new SimpleAttributeSet();
                            StyleConstants.setForeground(attr, Color.black);
                            StyleConstants.setBackground(attr, Color.white);
                            scriptTextArea.setCharacterAttributes(attr, true);
                            scriptTextArea.replaceSelection(recorded);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                };

            }
            model.getCube3D().addCube3DListener(scriptRecorder);

        } else {
            if (scriptRecorder != null) {

                model.getCube3D().removeCube3DListener(scriptRecorder);
                scriptRecorder = null;
            }
        }

        fireUndoableEditHappened(
                new UndoableBooleanEdit(this, "Record", !s, s, false) {
    private final static long serialVersionUID = 1L;

                    @Override
                    public void revert(boolean a, boolean b) {
                        recordButton.getModel().setSelected(b);
                    }
                });


    }//GEN-LAST:event_recordButtonStateChanged

    private void reset(@Nonnull java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset
        if ((evt.getModifiers() & (ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)) == 0) {
            model.reset();
            resetViews();
        } else {
            resetViews();
        }

    }//GEN-LAST:event_reset

    private void check(@Nullable java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check
        try {
            model.check();
            model.reset();
            if (model.isGenerator()) {
                BoundedRangeModel brm = model.getPlayer().getTimeModel();
                brm.setValue(brm.getMaximum());
            }
        } catch (ParseException e) {
            model.getPlayer().setScript(null);
            scriptTextArea.setToolTipText(e.getMessage() + "@" + e.getStartPosition() + ".." + e.getEndPosition());
            updateHighlight();
            scriptTextArea.setCaretPosition(e.getStartPosition());
            scriptTextArea.moveCaretPosition(min(e.getEndPosition() + 1, scriptTextArea.getDocument().getLength() - 1));
            if (evt != null) {
                JOptionPane.showMessageDialog(
                        ScriptView.this,
                        "<html><font face=Dialog><b>Check failed</b><br><font size=-1>" + e.getMessage(),
                        "Cube Twister: Check",
                        JOptionPane.ERROR_MESSAGE);
                scriptTextArea.requestFocus();
            }
        }

    }//GEN-LAST:event_check

    private void exportVideo(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportVideo
        if (model.getProgressView() != null) {
            JOptionPane.showMessageDialog(
                    ScriptView.this,
                    "<html><font face=Dialog><b>You can not run more than one operation on a script.</b><br><font size=-1>Another operation is already in progress on this script.<br>Please wait until it has finished.",
                    "Cube Twister: Export as Video",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (exportChooser == null) {
            exportChooser = new JFileChooser();
            exportChooser.setAccessory(new ScriptExportVideoAccessoryPanel(getModel(), exportChooser));
            //exportChooser.setApproveButtonText("Export");
            exportChooser.setDialogTitle("Export as Video");
        }
        ScriptExportVideoAccessoryPanel sevap=(ScriptExportVideoAccessoryPanel)exportChooser.getAccessory();
        sevap.setScriptModel(getModel());
        File f = new File(prefs.get("Script.exportVideo.file", System.getProperty("user.home") + File.separatorChar + getModel().getName()));
        f = new File(f.getParentFile(), getModel().getName());
        exportChooser.setSelectedFile(f);

        if (exportChooser.showDialog(this, "Export") == JFileChooser.APPROVE_OPTION) {
            final ScriptModel m = getModel();
            final File file = exportChooser.getSelectedFile();
            prefs.put("Script.exportVideo.file", file.getPath());
            ScriptExportVideoAccessoryPanel accessory = (ScriptExportVideoAccessoryPanel) exportChooser.getAccessory();
            final ProgressObserver progressMonitor = new ProgressView("Exporting \"" + m.getName() + "\" to \"" + exportChooser.getSelectedFile().getName() + "\"", "Waiting for Processor...", 0, Integer.MAX_VALUE);
            final ScriptVideoExporter sve = accessory.createExporter(file, progressMonitor);
            m.setProgressView(progressMonitor);
            progressMonitor.setDoCancel(new Runnable() {

                @Override
                public void run() {
                    if (m.getProgressView() == progressMonitor) {
                        m.setProgressView(null);
                        progressMonitor.complete();
                    }
                }
            });
            m.getDocument().dispatchSolver(
                    new BackgroundTask() {

                        @Override
                        public void construct() throws IOException {
                            if (progressMonitor.isCanceled()) {
                                return;
                            }
                            sve.start();
                        }

                        @Override
                        public void finished() {
                            progressMonitor.close();
                            if (m.getProgressView() == progressMonitor) {
                                m.setProgressView(null);
                            }

                        }

                        @Override
                        public void failed(@Nonnull Throwable e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(
                                    ScriptView.this,
                                    "<html><font face=Dialog><b>Export as video failed</b><br><font size=-1>" + ((e.getMessage() == null) ? e : e.getMessage()),
                                    "Cube Twister: Export as Video",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });

        }
    }//GEN-LAST:event_exportVideo

    private void transformScript(@Nonnull java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformScript
        Object src = evt.getSource();
        Node sn = model.getParsedScript();
        int layerMask = (2 << model.getCube().getLayerCount()) - 1;
        if (src == CRItem) {
            sn.transform(0, layerMask, 1);
        } else if (src == CFItem) {
            sn.transform(1, layerMask, 1);
        } else if (src == CUItem) {
            sn.transform(2, layerMask, 1);
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            String str = sn.toString(model.getNotationModel());
            model.setScript(str);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }//GEN-LAST:event_transformScript

    /**
     * Removes an UndoableEditListener.
     */
    @Override
    public void removeUndoableEditListener(UndoableEditListener l) {
        infoView.removeUndoableEditListener(l);
        if (true) {
            return;
        }
        if (true) {
            throw new InternalError("not implemented");
        }
//        model.getPlayer().removeUndoableEditListener(l);
        model.getSolverModel().removeUndoableEditListener(l);
        listenerList.remove(UndoableEditListener.class, l);
    }

    /**
     * Adds an UndoableEditListener.
     */
    @Override
    public void addUndoableEditListener(UndoableEditListener l) {
        infoView.addUndoableEditListener(l);
        if (true) {
            return;
        }
        if (true) {
            throw new InternalError("not implemented");
        }
//        model.getPlayer().addUndoableEditListener(l);
        model.getSolverModel().addUndoableEditListener(l);
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

    @Nonnull
    @Override
    public JComponent getViewComponent() {
        return this;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem CBItem;
    private javax.swing.JMenuItem CDItem;
    private javax.swing.JMenuItem CFItem;
    private javax.swing.JMenuItem CLItem;
    private javax.swing.JMenuItem CRItem;
    private javax.swing.JMenuItem CUItem;
    private javax.swing.JMenuItem InverseItem;
    private javax.swing.JButton checkButton;
    private javax.swing.JMenuItem clearStickersItem;
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JMenu cubeMenu;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JMenuItem exportVideoItem;
    private javax.swing.JMenuItem fillStickersItem;
    private javax.swing.JMenuItem generateCubeItem;
    private ch.randelshofer.cubetwister.doc.LazyEntityView infoView;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel lowerPanel;
    private ch.randelshofer.cubetwister.doc.LazyEntityView macrosView;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JMenu notationMenu;
    private ch.randelshofer.cubetwister.doc.LazyEntityView optionsView;
    private javax.swing.JPanel outerControlsPanel;
    private javax.swing.JButton popupButton;
    private javax.swing.JPopupMenu popupMenu;
    private ch.randelshofer.cubetwister.doc.ScriptPrimaryView primaryView;
    private javax.swing.JToggleButton recordButton;
    private javax.swing.JButton resetButton;
    private javax.swing.JMenuItem resetStickersItem;
    private javax.swing.JScrollPane scriptScrollPane;
    private javax.swing.JTextPane scriptTextArea;
    private ch.randelshofer.cubetwister.doc.LazyEntityView secondaryView;
    private javax.swing.JMenuItem solveCubeItem;
    private javax.swing.JSplitPane splitPane;
    private ch.randelshofer.cubetwister.doc.LazyEntityView stateView;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel tabsPanel;
    private javax.swing.JToolBar toolBar;
    private ch.randelshofer.cubetwister.doc.ScriptToolBarView toolBarView;
    private javax.swing.ButtonGroup toolbarGroup;
    private javax.swing.JMenu transformMenu;
    private javax.swing.JMenu translateMenu;
    // End of variables declaration//GEN-END:variables
}

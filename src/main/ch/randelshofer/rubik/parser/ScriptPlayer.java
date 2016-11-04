/*
 * @(#)ScriptPlayer.java  7.9.2  2011-02-17
 *
 * Copyright (c) 2000-2011 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.rubik.parser;

import ch.randelshofer.rubik.*;
import ch.randelshofer.gui.border.*;
import ch.randelshofer.gui.event.ModifierTracker;
import ch.randelshofer.gui.plaf.CustomButtonUI;
import org.monte.media.AbstractPlayer;
import org.monte.media.gui.JMovieControlAqua;
import org.monte.media.MovieControl;
import ch.randelshofer.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.plaf.ButtonUI;

/**
 * ScriptPlayer.
 *
 * @author  Werner Randelshofer
 * @version 7.9.2 2011-02-17 Merged with Media project.
 * <br>7.9.1 2010-11-13 Fixes enabled state of reset and scramble button.
 * <br>7.9 2010-08-03 The Locale of the player can now be changed by
 * setting a new locale on its control panel component.
 * <br>7.8 2010-05-02 Alt- or Ctrl-Click on reset button only resets
 * the canvas but not the cube.
 * <br>7.7 2009-07-26 Enable/Disable the movie control.
 * <br>7.6 2009-01-09 Use modifiersEx instead of modifiers.
 * <br>7.5 2008-12-30 Localized tooltips.
 * <br>7.4 2008-12-26 Added methods setScrambleButtonVisible.
 * <br>7.3 2008-01-08 Fixed highlighting during seeks.
 * <br>7.2 2008-01-07 Implemented methods isProcessingCurrentNode and getCurrentNode. 
 * <br>7.1 2008-01-05 Replaced AbstractButtonAWT by JButton. 
 * <br>7.0 2007-11-15 Upgraded to Java 1.4.
 * <br>6.1 2007-09-09 Changed modifiersEx: Alt-Key twists counter-clockwise,
 * Shift-Key twists by 180°, Meta-Key twists two layers. 
 * <br>6.0.1 2007-06-10 Added sleeps after seeks to method doStarted. 
 * <br>6.0  2005-03-13 Reworked.
 */
public class ScriptPlayer extends AbstractPlayer {

    private BoundedRangeModel cachingModel = new DefaultBoundedRangeModel(1, 0, 0, 1);
    private BoundedRangeModel progress = new DefaultBoundedRangeModel();
    private MovieControl movieControl;
    private Container controlPanelComponent;
    private long duration;
    private JButton resetButton;
    private JButton scrambleButton;
    private Cube cube;
    private Cube resetCube;
    private Cube3D cube3D;
    private Cube3DCanvas canvas;
    private Node script;
    private java.util.List<Node> resolvedScript = new ArrayList<Node>();
    private int scriptIndex;
    private Random random = new Random();
    private boolean isEnabled = true;
    private boolean isHideControlsIfNoScript = false;
    private ImageIcon partialResetIcon;
    private ImageIcon resetIcon;
    /**
     * Move count in face turn metric.
     */
    private int ftm;
    /**
     * Move count in block turn metric.
     */
    private int btm;
    /**
     * Move count in layer turn metric.
     */
    private int ltm;
    /**
     * Move count in quarter turn metric.
     */
    private int qtm;
    /**
     * When this flag is true, the script player handles 3D events.
     * That is, twists the cube, when a user clicks on a sticker of
     * the cube.
     */
    private boolean isHandling3DEvents = true;
    /**
     * Holds the node which is currently being processed.
     * This can be null, when the player is not active.
     */
    private Node activeNode;
    /**
     * Holds the current node which is currently being processed or will
     * be processed next.
     * This can be null, when the player has no script, or when the playhead
     * is at the end of a script.
     */
    private Node currentNode;

    public void setMovieControl(MovieControl mc) {
        if (this.movieControl != null) {
            controlPanelComponent.remove(this.movieControl.getComponent());
        }
        this.movieControl = mc;
        movieControl.setPlayer(this);
        movieControl.setVisible(false);
        controlPanelComponent.add(movieControl.getComponent());
    }

    @Override
    public boolean isCached() {
        return true;
    }

    private class Handler implements ChangeListener, ActionListener, Cube3DListener, PropertyChangeListener {

        @Override
        public void stateChanged(ChangeEvent evt) {
            if (evt.getSource() == progress) {
                fireStateChanged();
            }

            if (evt.getSource() == progress && !isActive()) {
                cube3D.stopAnimation();
                Runnable runner = new Runnable() {

                    @Override
                    public void run() {
                        int newIndex = progress.getValue();
                        if (scriptIndex >= 0 || scriptIndex <= resolvedScript.size()) {
                            if (scriptIndex == newIndex - 1) {
                                currentNode = activeNode = resolvedScript.get(scriptIndex);
                                ftm += activeNode.getFaceTurnCount();
                                btm += activeNode.getBlockTurnCount();
                                ltm += activeNode.getLayerTurnCount();
                                qtm += activeNode.getQuarterTurnCount();
                                fireStateChanged();
                                activeNode.applyTo(cube, false);
                                scriptIndex++;
                            } else if (scriptIndex == newIndex + 1) {
                                currentNode = activeNode = resolvedScript.get(--scriptIndex);
                                ftm -= activeNode.getFaceTurnCount();
                                btm -= activeNode.getBlockTurnCount();
                                ltm -= activeNode.getLayerTurnCount();
                                qtm -= activeNode.getQuarterTurnCount();
                                fireStateChanged();
                                activeNode.applyTo(cube, true);
                            } else {
                                currentNode = activeNode = script;
                                fireStateChanged();
                                cube.setQuiet(true);
                                while (scriptIndex < newIndex) {
                                    Node node = resolvedScript.get(scriptIndex++);
                                    node.applyTo(cube, false);
                                    ftm += activeNode.getFaceTurnCount();
                                    btm += activeNode.getBlockTurnCount();
                                    ltm += activeNode.getLayerTurnCount();
                                    qtm += activeNode.getQuarterTurnCount();
                                }
                                while (scriptIndex > newIndex) {
                                    Node node = resolvedScript.get(--scriptIndex);
                                    node.applyTo(cube, true);
                                    ftm -= activeNode.getFaceTurnCount();
                                    btm -= activeNode.getBlockTurnCount();
                                    ltm -= activeNode.getLayerTurnCount();
                                    qtm -= activeNode.getQuarterTurnCount();
                                }
                                cube.setQuiet(false);
                            }
                            activeNode = null;
                            currentNode = (progress.getValue() < resolvedScript.size()) ? resolvedScript.get(progress.getValue()) : null;
                            fireStateChanged();
                        }
                    }
                };
                if (cube3D.isAnimated() && !cube3D.getAnimator().isSynchronous()) {
                    //cube3D.getDispatcher().dispatch(runner);
                    dispatcher.dispatch(runner);
                } else {
                    runner.run();
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object src = evt.getSource();
            if (src == resetButton) {
                if ((evt.getModifiers() & (ActionEvent.ALT_MASK | ActionEvent.CTRL_MASK)) == 0) {
                    reset();
                } else {
                    if (canvas != null) {
                        canvas.reset();
                    }
                }
            } else if (src == scrambleButton) {
                scramble();
            }
        }

        @Override
        public void mouseEntered(Cube3DEvent evt) {
        }

        @Override
        public void mouseExited(Cube3DEvent evt) {
        }

        @Override
        public void mouseReleased(Cube3DEvent evt) {
        }

        @Override
        public void mousePressed(Cube3DEvent evt) {
        }

        @Override
        public void actionPerformed(Cube3DEvent evt) {
            if (isHandling3DEvents && evt.getOrientation() != -1) {
                evt.applyTo(cube);
            }
        }

        private void updateResetButton(InputEvent evt) {
            if (evt.getSource() == resetButton) {
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName() == ModifierTracker.MODIFIERS_EX_PROPERTY) {
                int modifiersEx = (Integer) evt.getNewValue();
                resetButton.setIcon(//
                        (modifiersEx & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0//
                        ? partialResetIcon : resetIcon);
            }
        }
    }
    private Handler handler = new Handler();

    /** Creates a new instance. */
    public ScriptPlayer() {

        controlPanelComponent = new JPanel();
        controlPanelComponent.setLayout(new BorderLayout());
        controlPanelComponent.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == "locale") {
                    updateLocale();
                }
            }
        });

        Border eastBorder = new BackdropBorder(
                new ButtonStateBorder(
                new ImageBevelBorder(
                Images.createImage(getClass(), "/org/monte/media/gui/images/Player.borderEast.png"),
                new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4)),
                new ImageBevelBorder(
                Images.createImage(getClass(), "/org/monte/media/gui/images/Player.borderEastP.png"),
                new Insets(1, 1, 1, 1), new Insets(0, 4, 1, 4))));

        Border westBorder = new BackdropBorder(
                new ButtonStateBorder(
                new ImageBevelBorder(
                Images.createImage(getClass(), "/org/monte/media/gui/images/Player.borderWest.png"),
                new Insets(1, 1, 1, 0), new Insets(0, 4, 1, 4)),
                new ImageBevelBorder(
                Images.createImage(getClass(), "/org/monte/media/gui/images/Player.borderWestP.png"),
                new Insets(1, 1, 1, 0), new Insets(0, 4, 1, 4))));

        resetButton = new JButton();
        resetButton.setName("Reset");
        resetButton.setIcon(resetIcon = new ImageIcon(Images.createImage(getClass(), "/org/monte/media/gui/images/PlayerReset.png")));
        resetButton.setDisabledIcon(new ImageIcon(Images.createImage(getClass(), "/org/monte/media/gui/images/PlayerReset.disabled.png")));
        partialResetIcon = new ImageIcon(Images.createImage(getClass(), "/org/monte/media/gui/images/PlayerPartialReset.png"));
        resetButton.setUI((ButtonUI) CustomButtonUI.createUI(resetButton));
        resetButton.setBorder(westBorder);
        resetButton.setMargin(new Insets(0, 0, 0, 0));
        resetButton.setPreferredSize(new Dimension(16, 16));
        resetButton.addActionListener(handler);
        resetButton.setFocusable(false);

        scrambleButton = new JButton();
        scrambleButton.setName("Scramble");
        scrambleButton.setIcon(new ImageIcon(Images.createImage(getClass(), "/org/monte/media/gui/images/PlayerScramble.png")));
        scrambleButton.setDisabledIcon(new ImageIcon(Images.createImage(getClass(), "/org/monte/media/gui/images/PlayerScramble.disabled.png")));
        scrambleButton.setUI((ButtonUI) CustomButtonUI.createUI(scrambleButton));
        scrambleButton.setBorder(westBorder);
        scrambleButton.setMargin(new Insets(0, 0, 0, 0));
        scrambleButton.setPreferredSize(new Dimension(16, 16));

        scrambleButton.addActionListener(handler);
        scrambleButton.setFocusable(false);
//        ModifierTracker.addModifierListener(new WeakPropertyChangeListener(handler));

        Container additionalControls;
        additionalControls = new JPanel();
        additionalControls.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        additionalControls.add(resetButton);
        additionalControls.add(scrambleButton);


        controlPanelComponent.add(additionalControls, BorderLayout.WEST);
        setMovieControl(new JMovieControlAqua());
        progress.setMaximum(0);
        progress.addChangeListener(handler);

        updateLocale();
    }

    private void updateLocale() {
        ResourceBundle labels = ResourceBundle.getBundle("ch.randelshofer.rubik.parser.Labels", controlPanelComponent.getLocale());
        resetButton.setToolTipText(labels.getString("reset.toolTipText"));
        scrambleButton.setToolTipText(labels.getString("scramble.toolTipText"));
        movieControl.getComponent().setLocale(controlPanelComponent.getLocale());
    }

    public void setCube(Cube newValue) {
        Cube oldValue = this.cube;
        this.cube = newValue;
        propertyChangeSupport.firePropertyChange("cube", oldValue, newValue);
    }

    public Cube getCube() {
        return cube;
    }

    public void setResetCube(Cube newValue) {
        Cube oldValue = this.resetCube;
        this.resetCube = newValue;
        propertyChangeSupport.firePropertyChange("resetCube", oldValue, newValue);
    }

    public Cube getResetCube() {
        return resetCube;
    }

    public void setResetButtonVisible(boolean newValue) {
        boolean oldValue = resetButton.isVisible();
        this.resetButton.setVisible(newValue);
        propertyChangeSupport.firePropertyChange("resetButtonVisible", oldValue, newValue);
    }

    public boolean isResetButtonVisible() {
        return resetButton.isVisible();
    }

    public void setScrambleButtonVisible(boolean newValue) {
        boolean oldValue = scrambleButton.isVisible();
        this.scrambleButton.setVisible(newValue);
        propertyChangeSupport.firePropertyChange("scrambleButtonVisible", oldValue, newValue);
    }

    public boolean isScrambleButtonVisible() {
        return scrambleButton.isVisible();
    }

    public void setEnabled(boolean newValue) {
        boolean oldValue = isEnabled;
        isEnabled = newValue;
        if (movieControl != null) {
            movieControl.setEnabled(newValue);
        }
        resetButton.setEnabled(newValue);
        scrambleButton.setEnabled(newValue);
        propertyChangeSupport.firePropertyChange("enabled", oldValue, newValue);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setCube3D(Cube3D newValue) {
        Cube3D oldValue = this.cube3D;
        if (oldValue != null) {
            oldValue.removeCube3DListener(handler);
        }
        this.cube3D = newValue;
        if (newValue != null && isHandling3DEvents) {
            newValue.addCube3DListener(handler);
        }
        propertyChangeSupport.firePropertyChange("cube3D", oldValue, newValue);
    }

    public Cube3D getCube3D() {
        return cube3D;
    }

    public void setScript(Node newValue) {
        stop();
        Node oldValue = this.script;
        this.script = newValue;
        /*
        resolvedScript.removeAllElements();
        if (newValue != null) {
        for (Enumeration i = newValue.resolvedEnumeration(false); i.hasMoreElements();) {
        Node node = (Node) i.nextElement();
        if (node.getSymbol().isTerminalSymbol() || node.getSymbol() == Symbol.PERMUTATION) {
        resolvedScript.addElement(node);
        }
        }
        }*/
        List<Node> empty = Collections.emptyList();
        resolvedScript = (script == null) ? empty : script.toResolvedList();
        scriptIndex = 0;
        ftm = btm = ltm = qtm = 0;
        progress.setValue(0);
        progress.setMaximum(resolvedScript.size());

        movieControl.setEnabled(newValue != null);
        movieControl.setVisible(newValue != null || !isHideControlsIfNoScript);
        controlPanelComponent.validate();

        currentNode = (resolvedScript.size() == 0) ? null : resolvedScript.get(0);
        activeNode = null;

        propertyChangeSupport.firePropertyChange("script", oldValue, newValue);
    }

    public Node getScript() {
        return script;
    }

    public void setCanvas(Cube3DCanvas newValue) {
        Cube3DCanvas oldValue = canvas;
        canvas = newValue;
        propertyChangeSupport.firePropertyChange("canvas", oldValue, newValue);
    }

    @Override
    public Component getVisualComponent() {
        return (canvas == null) ? null : canvas.getVisualComponent();
    }

    public Cube3DCanvas getCanvas() {
        return canvas;
    }

    public Node getCurrentNode() {
        return currentNode;
        /*
        return (progress.getValue() < resolvedScript.size())
        ? (Node) resolvedScript.get(progress.getValue())
        : null;*/
    }

    public boolean isProcessingCurrentNode() {
//        return activeNode != null && (scriptIndex < resolvedScript.size()) &&
//              activeNode == resolvedScript.get(scriptIndex);
        return activeNode != null
                && activeNode == currentNode;
    }

    @Override
    public void stop() {
        super.stop();
        if (cube3D != null) {
            cube3D.stopAnimation();
        }
        try {
            dispatcher.join();
        } catch (InterruptedException ex) {
            // do nothing
        }
        try {
            if (cube3D != null) {
                cube3D.getDispatcher().join();
            }
        } catch (InterruptedException ex) {
            // do nothing
        }
    }

    /**
     * Stops the player, sets the playhead to the start position and resets the
     * cube and its 3D representation.
     */
    public void reset() {
        stop();
        boolean wasAnimated = cube3D.isAnimated();
        cube3D.setAnimated(false);
        scriptIndex = 0;
        progress.removeChangeListener(handler);
        progress.setValue(0);
        if (cube != null) {
            if (resetCube != null) {
                cube.setTo(resetCube);
            } else {
                cube.reset();
            }
        }
        if (canvas != null) {
            canvas.reset();
        }
        progress.addChangeListener(handler);
        cube3D.setAnimated(wasAnimated);
        currentNode = (scriptIndex < resolvedScript.size()) ? resolvedScript.get(scriptIndex) : null;
        ftm = btm = ltm = qtm = 0;
        fireStateChanged();
    }

    /**
     * Moves the playhead to an Node node, which is located at the specified
     * caret.
     */
    public void moveToCaret(int caret) {
        int v = progress.getValue();
        boolean searchBackwards = false;
        if (v == resolvedScript.size()) {
            v--;
            searchBackwards = true;
        } else if (v < resolvedScript.size()) {
            Node node = resolvedScript.get(v);
            searchBackwards = (node.getEndPosition() > caret);
        }
        if (searchBackwards) {
            for (int i = v; i >= 0; i--) {
                Node node = resolvedScript.get(i);
                if (node.getStartPosition() <= caret && caret <= node.getEndPosition()) {
                    progress.setValue(i);
                    return;
                }
            }
        } else {
            for (int i = v, n = resolvedScript.size(); i < n; i++) {
                Node node = resolvedScript.get(i);
                if (node.getStartPosition() <= caret && caret <= node.getEndPosition()) {
                    progress.setValue(i);
                    return;
                } else {
                }
            }
        }
        progress.setValue(progress.getMaximum());
    }

    @Override
    protected void doClosed() {
    }

    @Override
    protected void doPrefetched() {
    }

    @Override
    protected void doPrefetching() {
    }

    @Override
    protected void doRealized() {
    }

    @Override
    protected void doRealizing() {
    }

    @Override
    protected void doStarted() {
        cube3D.setInStartedPlayer(true);

        int newIndex, oldValue;

        if (progress.getMaximum() > 0) {
            if (progress.getValue() == progress.getMaximum()) {
                currentNode = activeNode = script;
                fireStateChanged();
                progress.setValue(0);
                cube.setQuiet(true);
                while (scriptIndex > 0) {
                    currentNode = activeNode = resolvedScript.get(--scriptIndex);
                    activeNode.applyTo(cube, true);
                }
                ftm = btm = ltm = qtm = 0;
                cube.setQuiet(false);
                fireStateChanged();
                // Sleep a bit, so that we don't get straight from a
                // seek into the next move.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

            while (isActive() && progress.getValue() != progress.getMaximum()) {
                newIndex = progress.getValue() + 1;
                if (scriptIndex == newIndex - 1) {
                    currentNode = activeNode = resolvedScript.get(scriptIndex);
                    ftm += activeNode.getFaceTurnCount();
                    btm += activeNode.getBlockTurnCount();
                    ltm += activeNode.getLayerTurnCount();
                    qtm += activeNode.getQuarterTurnCount();
                    fireStateChanged();
                    activeNode.applyTo(cube, false);
                    scriptIndex++;
                } else if (scriptIndex == newIndex + 1) {
                    currentNode = activeNode = resolvedScript.get(--scriptIndex);
                    ftm -= activeNode.getFaceTurnCount();
                    btm -= activeNode.getBlockTurnCount();
                    ltm -= activeNode.getLayerTurnCount();
                    qtm -= activeNode.getQuarterTurnCount();
                    fireStateChanged();
                    activeNode.applyTo(cube, true);
                } else if (scriptIndex < newIndex - 1) {
                    currentNode = activeNode = script;
                    fireStateChanged();
                    cube.setQuiet(true);
                    while (scriptIndex < newIndex - 1) {
                        Node node = resolvedScript.get(scriptIndex++);
                        node.applyTo(cube, false);
                        ftm += activeNode.getFaceTurnCount();
                        btm += activeNode.getBlockTurnCount();
                        ltm += activeNode.getLayerTurnCount();
                        qtm += activeNode.getQuarterTurnCount();
                    }
                    cube.setQuiet(false);
                    currentNode = activeNode = resolvedScript.get(scriptIndex);
                    fireStateChanged();
                    activeNode.applyTo(cube, false);
                    scriptIndex++;
                } else if (scriptIndex > newIndex + 1) {
                    currentNode = activeNode = script;
                    fireStateChanged();
                    cube.setQuiet(true);
                    while (scriptIndex > newIndex - 1) {
                        Node node = resolvedScript.get(--scriptIndex);
                        node.applyTo(cube, true);
                        ftm -= activeNode.getFaceTurnCount();
                        btm -= activeNode.getBlockTurnCount();
                        ltm -= activeNode.getLayerTurnCount();
                        qtm -= activeNode.getQuarterTurnCount();
                    }
                    newIndex--;
                    cube.setQuiet(false);
                    fireStateChanged();
                }
                /*
                // Sleep a bit, so that we don't get straight from a
                // move into the next move.
                try {
                Thread.sleep(100);
                } catch (InterruptedException e) {

                }*/
                activeNode = null;
                currentNode = (newIndex < resolvedScript.size()) ? resolvedScript.get(newIndex) : null;
                fireStateChanged();
                if (newIndex == progress.getValue() + 1) {
                    progress.setValue(newIndex);
                }
            }
        }
        cube3D.setInStartedPlayer(false);
    }

    @Override
    protected void doUnrealized() {
    }

    @Override
    public BoundedRangeModel getCachingModel() {
        return cachingModel;
    }

    @Override
    public Component getControlPanelComponent() {
        return controlPanelComponent;
    }

    @Override
    public BoundedRangeModel getTimeModel() {
        return progress;
    }

    @Override
    public long getTotalDuration() {
        return duration;
    }

    public void setHideControlsIfNoScript(boolean newValue) {
        boolean oldValue = isHideControlsIfNoScript;
        isHideControlsIfNoScript = newValue;

        if (script == null) {
            movieControl.setVisible(!newValue);
        }
    }

    @Override
    public boolean isAudioAvailable() {
        return false;
    }

    @Override
    public boolean isAudioEnabled() {
        return false;
    }

    @Override
    public void setAudioEnabled(boolean b) {
        // empty
    }

    /**
     * Set this to false, if the player shall not
     * handle 3D events. If set to true, the player
     * twists the cube, when the user clicks on a sticker.
     * <p>
     * The default value is true.
     */
    public void setHandle3DEvents(boolean value) {
        boolean oldValue = isHandling3DEvents;
        if (oldValue != value) {
            isHandling3DEvents = value;
            if (cube3D != null) {
                if (value == true) {
                    cube3D.addCube3DListener(handler);
                } else {
                    cube3D.removeCube3DListener(handler);
                }
            }
        }
    }

    public void scramble() {
        reset();
        cube.setQuiet(true);
        // Keep track of previous axis, to avoid two subsequent moves on
        // the same axis.
        int prevAxis = -1;
        int axis, layerMask, angle;
        for (int i = 0; i < 30; i++) {
            while ((axis = random.nextInt(3)) == prevAxis) {
            }
            prevAxis = axis;
            while ((layerMask = random.nextInt(1 << cube.getLayerCount())) == 0) {
            }
            while ((angle = random.nextInt(5) - 2) == 0) {
            }
            cube.transform(axis, layerMask, angle);
        }
        cube.setQuiet(false);
    }

    public int getFaceTurnCount() {
        return ftm;
    }

    public int getBlockTurnCount() {
        return btm;
    }

    public int getLayerTurnCount() {
        return ltm;
    }

    public int getQuarterTurnCount() {
        return qtm;
    }
}

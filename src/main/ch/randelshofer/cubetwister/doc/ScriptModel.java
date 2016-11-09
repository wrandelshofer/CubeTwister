/* @(#)ScriptModel.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.*;
import ch.randelshofer.gui.text.*;
import ch.randelshofer.gui.tree.TreeNodeImpl;
import ch.randelshofer.io.ParseException;
import org.monte.media.gui.JMovieControlAqua;
import ch.randelshofer.rubik.parser.*;
import ch.randelshofer.rubik.*;
import ch.randelshofer.undo.*;
import java.awt.Toolkit;

import java.io.*;
import java.text.Normalizer;
import java.util.*;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.undo.*;

/**
 * Holds a Script and its attributes.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 * <br>2.2 Normalize script to Unicode NFKC before parsing.
 * <br>2.1 2007-08-11 Moved "name" property into superclass.
 * <br>1.3 2006-09-24 Reworked.
 * <br>1.2.2 2004-07-31 Variable parseException was not properly assigned
 * in method check().
 * <br>1.2.1 2003-03-05 ProProgressPanelst not be cloned.
 * <br>1.2 2002-12-26 Method translateInto(NotatationModel) improved.
 * <br>1.1.2 2002-11-20 Set the notationModel property to getDocument.getDefaultNotation(),
 * when the notationModel is removed from the document.
 * <br>1.1.1 2002-02-03 TranslateInto and change of Notation
 * could not be undone.
 */
public class ScriptModel
        extends InfoModel {
    private final static long serialVersionUID = 1L;
    /**
     * The progress view is used to access a cubeModel solver that writes its
     * result into this script model.
     *
     * The progress view must not be made persistent and must not be cloned.
     */
    private transient ProgressObserver progressView;
    public static final String PROP_NAME = "Name";
    public static final String PROP_SCRIPT = "Script";
    public static final String NOTATION_PROPERTY = "Notation";
    public static final String CHECKED_PROPERTY = "IsChecked";
    public static final String PROP_STATE_INFO = "StateInfo";
    public static final String CUBE_PROPERTY = "Cube";
    public static final String PROP_IS_GENERATOR = "IsGenerator";
    public static final String PROP_SOLVER_MODEL = "SolverModel";
    /** Macros is the only permitted child. It always has index 0. */
    private final static int MACRO_INDEX = 0;
    // Persistent attributes
    private StyledDocumentProxy script = new StyledDocumentProxy();
    private NotationModel notationModel;
    private CubeModel cubeModel;
    private transient boolean isGenerator = true;
    // Non-persistent attributes
    private SequenceNode parsedScript;
    private transient boolean isChecked;
    private transient String stateInfo;
    private ParseException parseException;
    /**
     * The solver model is used for putting stickers on
     * the cube and to invoke the solver.
     */
    private SolverModel solverModel;

    /** Handler for changes in the script. */
    private static class ScriptHandler implements ChangeListener {

        private ScriptModel model;

        public ScriptHandler(ScriptModel model) {
            this.model = model;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            model.uncheck();
        }
    };
    private ScriptHandler scriptHandler;

    /** Creates new CubeScript */
    public ScriptModel() {
        // Macros is the only permitted child
        setAllowsChildren(true);
        add(new EntityModel("Macros", true));

        script.addUndoableEditListener(this);
        script.setIgnoreAttributeEdits(true);
        scriptHandler = new ScriptHandler(this);
        script.addChangeListener(scriptHandler);
    }

    public CubeModel getCubeModel() {
        return (cubeModel == null) ? getDocument().getDefaultCube() : cubeModel;
    }

    public boolean isUsingDefaultCube() {
        return cubeModel == null;
    }

    public void setCubeModel(CubeModel value) {
        CubeModel oldValue = cubeModel;
        cubeModel = value;
        Cube3D oldCube3D = cube3D;
        Cube oldCube = cube;
        SolverModel oldSolverModel = solverModel;
        cube = null;
        cube3D = null;
        solverModel = null;

        if (hasPropertyListeners()) {
            firePropertyChange(PROP_CUBE_3D, oldCube3D, getCube3D());
            firePropertyChange(CUBE_PROPERTY, oldCube, getCube());
            firePropertyChange(PROP_SOLVER_MODEL, oldSolverModel, getSolverModel());
        }
        UndoableEdit edit = new UndoableObjectEdit(this, "Cube", oldValue, value) {
    private final static long serialVersionUID = 1L;
            @Override
            public void revert(Object a, Object b) {
                cubeModel = (CubeModel) b;
                firePropertyChange(CUBE_PROPERTY, a, b);
            }
        };
        fireUndoableEditHappened(edit);

        uncheck();
    }

    public String getScript() {
        return script.getText();
    }

    public StyledDocument getScriptDocument() {
        return script;
    }

    public void setScript(String value) {
        String oldValue = script.getText();
        if (oldValue == null || value == null || !oldValue.equals(value)) {
            basicSetScript(value);
            firePropertyChange(PROP_SCRIPT, oldValue, value);
        }
    }

    public void basicSetScript(String value) {
        script.setText(value);
    }

    public NotationModel getNotationModel() {
        return (notationModel == null && getDocument() != null) ? getDocument().getDefaultNotation(getCube().getLayerCount()) : notationModel;
    }

    public boolean isUsingDefaultNotation() {
        return notationModel == null;
    }

    public void setNotationModel(NotationModel value) {
        if (value != notationModel) {
            NotationModel oldValue = notationModel;
            notationModel = value;
            uncheck();
            firePropertyChange(NOTATION_PROPERTY, oldValue, value);

            UndoableEdit edit = new UndoableObjectEdit(this, "Notation", oldValue, value) {
    private final static long serialVersionUID = 1L;
                @Override
                public void revert(Object a, Object b) {
                    notationModel = (NotationModel) b;
                    firePropertyChange(NOTATION_PROPERTY, a, b);
                }
            };
            fireUndoableEditHappened(edit);
        }
    }

    public void addNotify(EntityModel m) {
    }

    public void removeNotify(EntityModel m) {
        if (m == getNotationModel()) {
            setNotationModel(null);
        }
        if (m == getCubeModel()) {
            setCubeModel(null);
        }
    }

    public ScriptParser getParser() {
        ArrayList<MacroNode> localMacros = new ArrayList<MacroNode>();
        DefaultMutableTreeNode macros = getMacroModels();
        for (int i = 0, n = macros.getChildCount(); i < n; i++) {
            MacroModel mm = (MacroModel) macros.getChildAt(i);
            StringTokenizer st = new StringTokenizer(mm.getIdentifier());
            while (st.hasMoreTokens()) {
                localMacros.add(new MacroNode(getNotationModel().getLayerCount(), st.nextToken(), mm.getScript(), 0, mm.getScript().length()));
            }
        }
        return getNotationModel().getParser(localMacros);
    }

    public ParseException getParseException() {
        return parseException;
    }

    public void clearParseException() {
        parseException = null;
    }

    public void check()
            throws ParseException {

        // Normalize text to Unicode NFC.
        String normalizedScript = Normalizer.normalize(script.getText(), Normalizer.Form.NFC);
        if (!normalizedScript.equals(script.getText())) {
            script.setText(normalizedScript);
        }

        parseException = null;
        if (!isChecked && getNotationModel() != null && getScript() != null) {
            ScriptParser p = getParser();
            try {
                parsedScript = p.parse(normalizedScript);
                isChecked = true;
                setStateInfo("Twists:\n" +
                        parsedScript.getBlockTurnCount() + " btm, " +
                        parsedScript.getLayerTurnCount() + " ltm, " +
                        parsedScript.getFaceTurnCount() + " ftm, " +
                        parsedScript.getQuarterTurnCount() + " qtm");
                firePropertyChange(CHECKED_PROPERTY, false, true);
                getPlayer().setScript(parsedScript);
                if (isGenerator()) {
                    getPlayer().setResetCube(null);
                } else {
                    ch.randelshofer.rubik.Cube resetCube = (ch.randelshofer.rubik.Cube) getCube().clone();
                    resetCube.reset();
                    parsedScript.applyTo(resetCube, true);
                    getPlayer().setResetCube(resetCube);
                }
            } catch (ParseException e) {
                // FIXME should look better
                getPlayer().setScript(new SequenceNode(getNotationModel().getLayerCount()));
                getPlayer().setResetCube(null);
                parseException = e;
                setStateInfo("Error:\n" + e.getMessage() + " @" + e.getStartPosition() + ".." + e.getEndPosition());
                throw e;
            } catch (Exception e) {
                getPlayer().setScript(new SequenceNode(notationModel.getLayerCount()));
                getPlayer().setResetCube(null);
                parseException = null;
                setStateInfo("Error:\n" + e);
                System.out.flush();
                e.printStackTrace();
                System.err.flush();
                throw new ParseException(e.getMessage(), 0, script.getLength() - 1);
            }
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isGenerator() {
        return isGenerator;
    }

    public void setGenerator(boolean value) {
        boolean oldValue = isGenerator;
        isGenerator = value;

        firePropertyChange(PROP_IS_GENERATOR, oldValue, value);
        UndoableEdit edit = new UndoableObjectEdit(this, "Script Type", new Boolean(oldValue), new Boolean(value)) {
    private final static long serialVersionUID = 1L;
            @Override
            public void revert(Object a, Object b) {
                isGenerator = ((Boolean) b).booleanValue();
                firePropertyChange(PROP_IS_GENERATOR, a, b);
            }
        };
        fireUndoableEditHappened(edit);
        uncheck();
    }

    public void uncheck() {
        if (isChecked) {
            isChecked = false;
            firePropertyChange(CHECKED_PROPERTY, true, false);
            setStateInfo("");
            parseException = null;
        }
    }

    private void setStateInfo(String value) {
        String oldValue = stateInfo;
        stateInfo = value;
        firePropertyChange(PROP_STATE_INFO, oldValue, value);
    }

    public String getStateInfo() {
        return (stateInfo == null) ? "" : stateInfo;
    }

    /**
     * Returns a parsed version of the script.
     * Returns an empty script, if the parsing failed - never returns null.
     */
    public SequenceNode getParsedScript() {
        try {
            check();
        } catch (ParseException e) {
            // Set parsed script to empty, if parsing failed.
            // FIXME - We should fire property change if the parsed script changes!!!
            parsedScript = new SequenceNode(notationModel.getLayerCount());
        }
        return parsedScript;
    }

    public List<MacroNode> getMacros() {
        List<MacroNode> macros = new LinkedList<MacroNode>();
        DefaultMutableTreeNode mms = getMacroModels();
        for (int i = 0, m = mms.getChildCount(); i < m; i++) {
            MacroModel mm = (MacroModel) mms.getChildAt(i);
            StringTokenizer st = new StringTokenizer(mm.getIdentifier());
            while (st.hasMoreTokens()) {
                MacroNode newMacro = new MacroNode(notationModel.getLayerCount(), st.nextToken(), mm.getScript(), 0, 0);
                macros.add(newMacro);
            }
        }
        return macros;
    }

    public EntityModel getMacroModels() {
        return getChildAt(MACRO_INDEX);
    }

    public void translateInto(NotationModel n)
            throws IOException {
        CompositeEdit edit = new CompositeEdit("Translate");
        fireUndoableEditHappened(edit);
        try {
            ArrayList<MacroNode> oldLocalMacros = new ArrayList<MacroNode>();
            DefaultMutableTreeNode macros = getMacroModels();
            for (int i = 0, m = macros.getChildCount(); i < m; i++) {
                MacroModel mm = (MacroModel) macros.getChildAt(i);
                StringTokenizer st = new StringTokenizer(mm.getIdentifier());
                while (st.hasMoreTokens()) {
                    oldLocalMacros.add(new MacroNode(notationModel.getLayerCount(), st.nextToken(), mm.getScript(), 0, mm.getScript().length()));
                }
            }
            NotationModel oldNotation = getNotationModel();
            NotationModel newNotation = n;
            ScriptParser oldParser = getNotationModel().getParser(oldLocalMacros);

            // Parse the script
            SequenceNode oldParsedScript = oldParser.parse(getScript());

            // Parse the local macros
            for (MacroNode macroNode : oldLocalMacros) {
                macroNode.expand(oldParser);
            }

            // Translate the Script and write it back
            if (oldNotation.isSupported(Symbol.MOVE) && !newNotation.isSupported(Symbol.MOVE)) {
                Cube cube = (Cube) getCube().clone();
                cube.reset();
                oldParsedScript.applyTo(cube, false);
                setScript(Cubes.toPermutationString(cube, newNotation));
            } else {
                System.err.println("Warning: ScriptModel parsing with pre-parsed macros not yet supported!");
                //setScript(oldParser.parse(getScript(), parsedMacros).toString(newNotation));
                setScript(oldParsedScript.toString(newNotation, oldLocalMacros));
            }
            setNotationModel(n);

            // Translate the macros and write them back
            for (int i = 0, m = macros.getChildCount(); i < m; i++) {
                MacroModel mm = (MacroModel) macros.getChildAt(i);
                MacroNode newMacro = new MacroNode(notationModel.getLayerCount(), mm.getIdentifier(), mm.getScript(), 0, 0);
                newMacro.expand(oldParser);
                mm.setScript(newMacro.toString(newNotation, oldLocalMacros));
            }
        } catch (IOException e) {
            System.out.println(getName() + " Translation was not successful");
            System.out.println(e);
            throw e;
        } finally {
            fireUndoableEditHappened(edit);
        }
    }

    @Override
    public ScriptModel clone() {
        ScriptModel that = (ScriptModel) super.clone();

        that.script = (StyledDocumentProxy) this.script.clone();
        // The progress view must not be cloned
        that.progressView = null;

        that.script.addUndoableEditListener(that);
        that.scriptHandler = new ScriptHandler(that);
        that.script.addChangeListener(that.scriptHandler);

        DefaultMutableTreeNode macros = new EntityModel("Macros", true);
        for (EntityModel node: this.getChildAt(MACRO_INDEX).getChildren()){
            macros.add((MacroModel) node.clone());
        }
        that.add(macros);

        return that;
    }

    /**
     * Sets the progress view of a running solver that writes its results
     * into this script model.
     */
    public void setProgressView(ProgressObserver view) {
        this.progressView = view;

    }

    /**
     * Gets the progress view of a running solver that writes its results
     * into this script model.
     */
    public ProgressObserver getProgressView() {
        return progressView;
    }

    /**
     * Returns true if the node may be removed from its parent.
     */
    @Override
    public boolean isRemovable() {
        // The script is not removable if it is associated with a solver
        return (progressView == null) && super.isRemovable();
    }
// ---------------------------------------
    public static final String PROP_SCRIPT_MODEL = "ScriptModel";
    public static final String PROP_INTERACTION_MODE = "InteractionMode";
    public static final String PROP_CUBE_3D = "Cube3D";
    public final static int MODE_TWIST = 0;
    public final static int MODE_STICKER = 1;
    public final static int MODE_RECORD = 2;
    private int interactionMode = MODE_TWIST;
    private Random random = new Random();
    private Cube3DListener cube3DHandler = new Cube3DAdapter() {

        /**
         * Invoked when a mouse event on a part of the cube occurs.
         */
        @Override
        public void actionPerformed(Cube3DEvent evt) {
            switch (getInteractionMode()) {
                case MODE_TWIST:
                    handleTwistEvent(evt);
                    break;
                case MODE_RECORD:
                    handleTwistEvent(evt);
                    break;
                case MODE_STICKER:
                    handleStickerEvent(evt);
                    break;
            }
        }

        private void handleStickerEvent(Cube3DEvent evt) {
            System.err.println("ScriptPrimaryView stickermode not implemented");
            getPlayer().stop();
            int i = evt.getStickerIndex();
            if (i != -1) {
                if (i % 9 == 4) {
                    /*
                    switch (i / 9) {
                    case 0 : frontToggleButton.setSelected(true); break;
                    case 1 : rightToggleButton.setSelected(true); break;
                    case 2 : downToggleButton.setSelected(true); break;
                    case 3 : backToggleButton.setSelected(true); break;
                    case 4 : leftToggleButton.setSelected(true); break;
                    case 5 : upToggleButton.setSelected(true); break;
                    }*/
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    /*
                    int side;
                    if (frontToggleButton.isSelected()) {
                    side = 0;
                    } else if (rightToggleButton.isSelected()) {
                    side = 1;
                    } else if (downToggleButton.isSelected()) {
                    side = 2;
                    } else if (backToggleButton.isSelected()) {
                    side = 3;
                    } else if (leftToggleButton.isSelected()) {
                    side = 4;
                    } else {
                    side = 5;
                    }
                    solverModel.setStickerMapping(i, side);
                     */
                }
            }
        }

        private void handleTwistEvent(final Cube3DEvent evt) {
            getPlayer().stop();
            if (evt.getOrientation() != -1) {
                final Cube oldState = (Cube) getCube().clone();
                evt.applyTo(getCube());
                final Cube newState = (Cube) getCube().clone();

                // Report the twist to the undo manager
                fireUndoableEditHappened(
                        new UndoableCubeEdit("Twist", getCube(), oldState, newState));
            }
        /*
        // Only handle orientation changes
        if (evt.getOrientation() != -1) {
        // Create a composite edit for the undo manager
        CompositeEdit edit = new CompositeEdit("Twist");
        fireUndoableEditHappened(edit);
        // Twist the cube model according to the event
        int symbol;
        final ch.randelshofer.rubik.Cube cubeModel = player.getCube();
        evt.applyTo(cubeModel, false);
        // Record the twist if the record button is selected
        if (recordButton.isSelected() && model != null && model.getNotationModel() != null) {
        try {
        ScriptParser parser = getParser();
        StringWriter w = new StringWriter();
        if (true) throw new InternalError("not implemented");
        //                    parser.writeToken(w, symbol);
        w.close();
        String token = w.toString();
        char delimiter = (token.indexOf('\n') == -1) ? ' ' : '\n';
        if (token != null) {
        if (scriptTextArea.getSelectionStart() > 0
        && scriptTextArea.getDocument().getText(scriptTextArea.getSelectionStart() - 1, 1).charAt(0) > ' ') {
        token = delimiter+token;
        }
        if (scriptTextArea.getSelectionEnd() < scriptTextArea.getDocument().getLength() - 1
        && scriptTextArea.getDocument().getText(scriptTextArea.getSelectionEnd(), 1).charAt(0) > ' ') {
        token = token+delimiter;
        }
        scriptTextArea.replaceSelection(token);
        scriptTextArea.setCaretPosition(scriptTextArea.getSelectionEnd());
        }
        } catch (IOException e) {
        throw new InternalError(e.getMessage());
        } catch (BadLocationException e) {
        throw new InternalError(e.getMessage());
        }
        }
        // Close the composite edit
        fireUndoableEditHappened(edit);
        }*/
        }
    };
    /*
    private PropertyChangeListener propertyHandler = new PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() == scriptModel) {
    String n = evt.getPropertyName();
    if (n == ScriptModel.CUBE_PROPERTY) {
    updateCube3D();
    } else if (n == ScriptModel.CHECKED_PROPERTY) {
    updateInvertedCube();
    }
    }
    }
    };*/
    /**
     * This parser instance is used for output of permutations in the
     * state text area and for recording scripts.
     */
    private ScriptParser parser;
    private ScriptPlayer player;
    private ch.randelshofer.rubik.Cube cube;
    private Cube3D cube3D;

    /*
    private void updateCube3D() {
    Cube3D oldValue = cube3D;
    if (oldValue != null) {
    oldValue.removeCube3DListener(cube3DHandler);
    }
    try {
    cube3D = (Cube3D) getCubeModel().getCube3DClass().newInstance();
    } catch (Exception e) {
    InternalError er = new InternalError();
    er.initCause(e);
    throw er;
    }
    Cube3D newValue = cube3D;
    if (newValue != null) {
    newValue.addCube3DListener(cube3DHandler);
    }
    player.setCube3D(cube3D);
    player.setCube(cube3D.getCube());
    invertedCube = (Cube) cube3D.getCube().clone();
    cube3D.setAnimated(true);
    cube3D.setAttributes(getCubeModel());
    firePropertyChange(PROP_CUBE_3D, oldValue, newValue);
    firePropertyChange(CUBE_PROPERTY, (oldValue == null) ? null : oldValue.getCube(), newValue.getCube());
    }
     */
    private void setInteractionMode(int newValue) {
        int oldValue = interactionMode;
        interactionMode = newValue;
        firePropertyChange(PROP_INTERACTION_MODE, oldValue, newValue);
    }

    public int getInteractionMode() {
        return interactionMode;
    }

    public ScriptPlayer getPlayer() {
        if (player == null) {
            player = new ScriptPlayer();
            player.setMovieControl(new JMovieControlAqua());
            player.setResetButtonVisible(false);
            player.setHideControlsIfNoScript(false);
            if (solverModel!=null) {
                solverModel.setPlayer(player);
            }
            getCube3D();
        }
        return player;
    }

    public ch.randelshofer.rubik.Cube getCube() {
        if (cube == null) {
            cube = getCube3D().getCube();
        }
        return cube;
    }

    public Cube3D getCube3D() {
        if (cube3D == null) {
            try {
                if (getCubeModel() == null) {
                        CubeKind kind;
                    NotationModel nm = notationModel;
                    if (nm == null) {
                       CubeModel defaultCube = getDocument().getDefaultCube();
                       if (defaultCube != null) {
                           nm = getDocument().getDefaultNotation(defaultCube.getLayerCount());
                       } else {
                           for (int i=2; i < 8 && nm == null; i++) {
                           nm = getDocument().getDefaultNotation(i);
                                   }
                       }
                    }
                    if (nm == null) {
                        kind = CubeKind.RUBIK;
                    } else {
                        switch (nm.getLayerCount()) {
                            case 2:
                        kind = CubeKind.POCKET;
                                break;
                            case 3:
                            default:
                        kind = CubeKind.RUBIK;
                                break;
                            case 4:
                        kind = CubeKind.REVENGE;
                                break;
                            case 5:
                        kind = CubeKind.PROFESSOR;
                                break;
                            case 6:
                        kind = CubeKind.VCUBE_6;
                                break;
                            case 7:
                        kind = CubeKind.VCUBE_7;
                                break;
                        }
                    }
                    CubeModel cm = new CubeModel(kind);
                    cube3D = (Cube3D) cm.getCube3DClass().newInstance();
                    solverModel = new SolverModel(cube3D,player, cm);
                } else {
                    cube3D = (Cube3D) getCubeModel().getCube3DClass().newInstance();
                    solverModel = new SolverModel(cube3D,player, getCubeModel());
                }
                cube3D.setAttributes(solverModel);
                cube3D.setAnimated(true);
                if (player != null) {
                    player.setCube3D(cube3D);
                    player.setCube(getCube());

                    player.setHandle3DEvents(false);
                }
            } catch (Exception e) {
                InternalError error = new InternalError(e.getMessage());
                error.initCause(e);
                throw error;
            }
        }
        return cube3D;
    }

    public SolverModel getSolverModel() {
        if (solverModel == null) {
            getCube3D();
        }
        return solverModel;
    }

    public void reset() {
        player.stop();
        final Cube oldState = (Cube) getCube().clone();

        player.reset();
        /*
        if (isChecked()) {
        if (isGenerator()) {
        getPlayer().getTimeModel().setValue(getPlayer().getTimeModel().getMaximum());
        }
        }*/
        final Cube newState = (Cube) getCube().clone();

    // Report the twist to the undo manager
        /* XXX - This must be moved to the event source.
    fireUndoableEditHappened(
    new UndoableCubeEdit("Reset", getCube(), oldState, newState)
    );*/
    }

     public void scramble() {
        player.stop();
        Cube cube = getCube();
        final Cube oldState = (Cube) cube.clone();
        cube.setQuiet(true);
        // Keep track of previous axis, to avoid two subsequent twists on
        // the same axis.
        int prevAxis = -1;



        int axis, layerMask, angle;
        for (int i = 0; i < 30; i++) {
            while ((axis = random.nextInt(3)) == prevAxis) {
                ;
            }
            prevAxis = axis;
            while ((layerMask = random.nextInt(1 << cube.getLayerCount())) == 0) {
                ;
            }
            while ((angle = random.nextInt(5) - 2) == 0) {
                ;
            }
            cube.transform(axis, layerMask, angle);
        }
        cube.setQuiet(false);
        final Cube newState = (Cube) cube.clone();
        fireUndoableEditHappened(
                new UndoableCubeEdit("Scramble", cube, oldState, newState));
    }
}

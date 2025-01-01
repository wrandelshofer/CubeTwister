/*
 * @(#)ScriptStateView.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.Fonts;
import ch.randelshofer.rubik.cube.Cube;
import ch.randelshofer.rubik.cube.CubeEvent;
import ch.randelshofer.rubik.cube.CubeListener;
import ch.randelshofer.rubik.cube.Cubes;
import org.jhotdraw.annotation.Nonnull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

//import ch.randelshofer.rubik.solver.*;

/**
 * ScriptStateView.
 *
 * @author  Werner Randelshofer
 */
public class ScriptStateView extends JPanel implements EntityView {
    private final static long serialVersionUID = 1L;

    private final static boolean DEBUG = false;
    private ScriptModel model;
    @Nonnull
    private PropertyChangeListener propertyHandler = new PropertyChangeListener() {

        public void propertyChange(@Nonnull PropertyChangeEvent evt) {
            String n = evt.getPropertyName();
            if (evt.getSource() == model) {
                if (n == ScriptModel.CHECKED_PROPERTY) {
                } else if (n == ScriptModel.NOTATION_PROPERTY) {
                    updateState();
                } else if (n == ScriptModel.CUBE_PROPERTY) {
                    if (evt.getOldValue() != null) {
                        ((Cube) evt.getOldValue()).removeCubeListener(cubeHandler);
                    }
                    ((Cube) evt.getNewValue()).addCubeListener(cubeHandler);
                    updateState();
                }
            } else if (model != null && evt.getSource() == model.getSolverModel()) {
                updateState();
            }
        }
    };
    @Nonnull
    private CubeListener cubeHandler = new CubeListener() {

        public void cubeTwisted(final CubeEvent evt) {
            update();
        }

        public void cubePartRotated(CubeEvent evt) {
            update();
        }

        public void cubeChanged(CubeEvent evt) {
            update();
        }

        public void update() {
            updateState();
        }
    };

    /** Creates a new instance. */
    public ScriptStateView() {
        initComponents();
        if ("Windows".equals(UIManager.getLookAndFeel().getID())) {
            scrollPane.setBorder(new EmptyBorder(0,0,0,0));
        }
    }

    public void removeUndoableEditListener(UndoableEditListener l) {
    }

    public void addUndoableEditListener(UndoableEditListener l) {
    }

    public void setModel(EntityModel newValue) {
        setModel((ScriptModel) newValue);
    }

    public void setModel(ScriptModel newValue) {
        if (model != null) {
            model.removePropertyChangeListener(propertyHandler);
            model.getSolverModel().removePropertyChangeListener(propertyHandler);
            model.getCube().removeCubeListener(cubeHandler);
        }
        model = newValue;
        if (model != null) {
            model.addPropertyChangeListener(propertyHandler);
            model.getSolverModel().addPropertyChangeListener(propertyHandler);
            model.getCube().addCubeListener(cubeHandler);
            updateState();
        }
    }

    @Nonnull
    public JComponent getViewComponent() {
        return this;
    }

    public void updateState() {
        Runnable r = new Runnable() {

            public void run() {
                try {
                    Object lock = (model == null) ? null : model.getCube();
                    if (lock == null) {
                        return;
                    }
                    synchronized (lock) {
                        MutableAttributeSet bold = new SimpleAttributeSet();
                        StyleConstants.setBold(bold, true);
                        MutableAttributeSet plain = new SimpleAttributeSet();
                        MutableAttributeSet red = new SimpleAttributeSet();
                        StyleConstants.setForeground(red, Color.red);
                        Cube cube = model.getCube();
                        if (DEBUG) {
                            System.out.println("ScriptStateView.updateState  cube=Cube#" + System.identityHashCode(cube));
                        }
                        MutableAttributeSet code = new SimpleAttributeSet();
                        StyleConstants.setFontFamily(code, Fonts.getMonospaceFont().getFamily());
                        StyleConstants.setFontSize(code, 10);

                        StyledDocument doc = (StyledDocument) stateTextArea.getDocument();
                        doc.getStyle(StyleContext.DEFAULT_STYLE).addAttribute(StyleConstants.FontFamily, Fonts.getSmallDialogFont().getFamily());
                        doc.getStyle(StyleContext.DEFAULT_STYLE).addAttribute(StyleConstants.FontSize, new Integer(Fonts.getSmallDialogFont().getSize()));
                        doc.remove(0, doc.getLength());
                        NotationModel notationModel = model.getNotationModel();
                        if (model != null && notationModel != null) {
                            String stateInfo = model.getStateInfo();
                            int pos = stateInfo.indexOf('\n');
                            if (pos != -1) {
                                doc.insertString(doc.getLength(), stateInfo.substring(0, pos), bold);
                                doc.insertString(doc.getLength(), " ", plain);

                                doc.insertString(doc.getLength(), stateInfo.substring(pos + 1), (stateInfo.startsWith("Error")) ? red : plain);
                            }
                            doc.insertString(doc.getLength(), "\n\n", plain);
                        }

                        doc.insertString(doc.getLength(), "Order: ", bold);
                        doc.insertString(doc.getLength(), Integer.toString(Cubes.getVisibleOrder(cube)), plain);
                        doc.insertString(doc.getLength(), " v, ", plain);
                        doc.insertString(doc.getLength(), Integer.toString(Cubes.getOrder(cube)), plain);
                        doc.insertString(doc.getLength(), " r", plain);

                        if (DEBUG) {
                            System.out.println("ScriptStateView.updateState2 cube=Cube#" + System.identityHashCode(cube));
                        }
                        if (model != null) {
                            SolverModel solverModel = model.getSolverModel();
                            if (!solverModel.isSolverSupported() ||
                                    solverModel.isSolveable()) {
                                doc.insertString(doc.getLength(), "\n\n", plain);
                                doc.insertString(doc.getLength(), "Permutation:\n", bold);
                                if (DEBUG) {
                                    System.out.println("ScriptStateView.updateState3 cube=Cube#" + System.identityHashCode(cube));
                                }
                                if (notationModel != null) {
                                    doc.insertString(doc.getLength(), solverModel.getMappedPermutationString(cube, notationModel), plain);
                                }
                            } else {
                                doc.insertString(doc.getLength(), "\n\n", plain);
                                doc.insertString(doc.getLength(), "Stickers:\n", bold);
                                doc.insertString(doc.getLength(), solverModel.getMappedStickersString(cube), plain);
                            }
                        }
                    }
                } catch (BadLocationException e) {
                    stateTextArea.setText(e.toString());
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        stateTextArea = new javax.swing.JTextPane();

        setLayout(new java.awt.BorderLayout());

        stateTextArea.setEditable(false);
        scrollPane.setViewportView(stateTextArea);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextPane stateTextArea;
    // End of variables declaration//GEN-END:variables
}

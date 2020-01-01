/* @(#)ScriptSecondaryView.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.rubik.Cube3DCanvas;
import ch.randelshofer.rubik.JCubeCanvasIdx3D;
import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
/**
 * ScriptSecondaryView.
 *
 *
 * @author Werner Randelshofer
 */
public class ScriptSecondaryView extends JPanel implements EntityView {
    private final static long serialVersionUID = 1L;
    
    /**
     * The listeners waiting for UndoableEdit events.
     */
    @Nonnull
    private javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();


    @Nonnull
    private PropertyChangeListener propertyHandler = new PropertyChangeListener() {
        public void propertyChange(@Nonnull PropertyChangeEvent evt) {
            String n = evt.getPropertyName();
            if (evt.getSource() == model) {
                if (n == ScriptModel.CUBE_PROPERTY) {
                    updateCube3D();
                } else if (n == ScriptModel.PROP_CUBE_3D) {
                    updateCube3D();
                }
            }
        }
    };
    
    /**
     * The resource bundle used for internationalisation.
     */
    private ResourceBundleUtil labels;
    
    private ScriptModel model;
    
    private JCubeCanvasIdx3D cubeCanvas;
    /*
    private Cube3D cube3D;
    private ch.randelshofer.rubik.Cube cube;
    */
    
    
    /** Creates a new instance. */
    public ScriptSecondaryView() {
        init();
    }
    private void init() {
        JCubeCanvasIdx3D cubeCanvas = new JCubeCanvasIdx3D();
        
        // Load the resource bundle
        labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));
        
        // Initialise the components as far as the IDE supports it.
        initComponents();
    }
    
    private Cube3DCanvas getCubeCanvas() {
        if (cubeCanvas == null) {
            cubeCanvas = new JCubeCanvasIdx3D();
            cubeCanvas.setCamera("Rear");
            add(cubeCanvas);
            revalidate();
        }
        return cubeCanvas;
    }
    /**
     * Removes an UndoableEditListener.
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
        listenerList.remove(UndoableEditListener.class, l);
    }
    public void reset() {
        cubeCanvas.reset();
    }
    
    /**
     * Adds an UndoableEditListener.
     */
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
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==UndoableEditListener.class) {
                    // Lazily create the event
                    if (evt == null)
                        evt = new UndoableEditEvent(this, edit);
                    ((UndoableEditListener)listeners[i+1]).undoableEditHappened(evt);
                }
            }
        }
    }
    
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
        }
        
        updateCube3D();
        getCubeCanvas().reset();
        
    }
    
    private void updateCube3D() {
        if (model != null) {
                getCubeCanvas().setCube3D(model.getCube3D());
               // getCubeCanvas().setLock(model.getCube());
                }
        /*
        if (cube3D != null) {
            cube3D.setAttributes(null);
            cube3D.removeCube3DListener(cube3DHandler);
            getCubeCanvas().setCube3D(null);
            cube3D.dispose();
            cube3D = null;
        }
        if (model != null) {
                getCubeCanvas().setCube3D(scriptView.getCube3D());
            try {
                cube3D = (Cube3D) model.getCubeModel().getCube3DClass().newInstance();
                if (cube == null || cube.getLayerCount() != cube3D.getCube().getLayerCount()) {
                    cube = scriptView.getCube();
                } else {
                    cube3D.setCube(cube);
                }
                cube3D.setAttributes(model.getCubeModel());
                cube3D.addCube3DListener(cube3DHandler);
                cube3D.setAnimated(true);
                //cube3D.setShowGhostParts(partsToggleButton.isSelected());
                
                getCubeCanvas().setCube3D(cube3D);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    @Nonnull
    public JComponent getViewComponent() {
        return this;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        toggleButtonGroup = new javax.swing.ButtonGroup();

        setLayout(new java.awt.BorderLayout());

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup toggleButtonGroup;
    // End of variables declaration//GEN-END:variables
    
}

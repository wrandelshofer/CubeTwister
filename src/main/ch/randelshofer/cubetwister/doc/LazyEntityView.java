/*
/* @(#)LazyEntityView.java
 * Copyright (c) 2006 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.gui.icon.BusyIcon;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import org.jhotdraw.gui.Worker;

/**
 * LazyEntityView postpones instantiation of a real EntityView until it needs to
 * be drawn on screen.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * instead of a Class object.
 * <br>1.0 February 1, 2006 Created.
 */
public class LazyEntityView extends JPanel
        implements EntityView {
    private final static long serialVersionUID = 1L;
    private String viewClassName;
    private EntityView lazyView;
    private EntityModel model;
    private EventListenerList listeners;
    private Worker<EntityView> worker;
    private boolean isRealized = false;

    /** Creates new form. */
    public LazyEntityView() {
        setLayout(new BorderLayout());
    }

    public void setViewClassName(String clazz) {
        viewClassName = clazz;
        isRealized = false;
    }

    @Override
    public void paint(Graphics g) {
        realizeView();
        super.paint(g);
    }

    public void realizeView() {
        if (!isRealized) {
            if (viewClassName != null && lazyView == null && worker == null) {
                isRealized = true;
                removeAll();
                
                JLabel progressIndicator = new JLabel(BusyIcon.getInstance());
                progressIndicator.setHorizontalAlignment(JLabel.CENTER);
                add(progressIndicator);
                if (getParent() != null) {
                    getParent().validate();
                }
                worker = new Worker<EntityView>() {

                    @Override
                    public EntityView construct() throws Exception {
                            return (EntityView)Class.forName(viewClassName).newInstance();
                    }

                    @Override
                    public void done(EntityView value) {
                        lazyView = value;
                        lazyView.setModel(model);
                        if (listeners != null) {
                            for (UndoableEditListener l : listeners.getListeners(UndoableEditListener.class)) {
                                lazyView.addUndoableEditListener(l);
                            }
                        }
                        removeAll();
                        add(lazyView.getViewComponent(), BorderLayout.CENTER);
                        invalidate();
                        if (getParent() != null) {
                            getParent().validate();
                        }
                    }

                    @Override
                    protected void failed(Throwable error) {
                        System.err.println("LazyEntityView couldn't construct "+viewClassName);
                        error.printStackTrace();
                    }

                    @Override
                    protected void finished() {
                        worker = null;
                    }

                };
                worker.start();
            }
        }
    }

    @Override
    public void setModel(EntityModel newValue) {
        if (model != null) {
        }
        model = newValue;
        if (model != null) {
        }
        if (lazyView != null) {
            lazyView.setModel(model);
        }
    }

    @Override
    public JComponent getViewComponent() {
        return this;
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener l) {
        if (listeners != null) {
            listeners.remove(UndoableEditListener.class, l);
            if (lazyView != null) {
                lazyView.removeUndoableEditListener(l);
            }
            if (listeners.getListenerCount() == 0) {
                listeners = null;
            }
        }
    }

    @Override
    public void addUndoableEditListener(UndoableEditListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(UndoableEditListener.class, l);
        if (lazyView != null) {
            lazyView.addUndoableEditListener(l);
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     * /
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

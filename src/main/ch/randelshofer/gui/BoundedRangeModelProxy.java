/*
 * @(#)BoundedRangeModelProxy.java  2.0  2007-11-15
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui;

import javax.swing.*;
import javax.swing.event.*;
/**
 * BoundedRangeModelProxy.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 * <br>1.0 January 3, 2006 Created.
 */
public class BoundedRangeModelProxy implements BoundedRangeModel, ChangeListener {
    private BoundedRangeModel target;
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();
    protected transient ChangeEvent changeEvent = null;
    /**
     * Creates a new instance.
     */
    public BoundedRangeModelProxy(BoundedRangeModel target) {
        this.target = target;
        target.addChangeListener(this);
    }
    
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    public int getExtent() {
        return target.getExtent();
    }
    
    public int getMaximum() {
        return target.getMaximum();
    }
    
    public int getMinimum() {
        return target.getMinimum();
    }
    
    public int getValue() {
        return target.getValue();
    }
    
    public boolean getValueIsAdjusting() {
        return target.getValueIsAdjusting();
    }
    
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    public void setExtent(int newExtent) {
        target.setExtent(newExtent);
    }
    
    public void setMaximum(int newMaximum) {
        target.setMaximum(newMaximum);
    }
    
    public void setMinimum(int newMinimum) {
        target.setMinimum(newMinimum);
    }
    
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        target.setRangeProperties(value, extent, min, max, adjusting);
    }
    
    public void setValue(int newValue) {
        target.setValue(newValue);
    }
    
    public void setValueIsAdjusting(boolean b) {
        target.setValueIsAdjusting(b);
    }
    
    public void stateChanged(ChangeEvent event) {
        fireStateChanged();
    }
    
    /** 
     * Run each ChangeListeners stateChanged() method.
     * 
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged() 
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }   
}

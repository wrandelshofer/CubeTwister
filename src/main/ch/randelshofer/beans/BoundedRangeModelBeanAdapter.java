/*
 * @(#)BoundedRangeModelBeanAdapter.java  1.1  2010-04-09
 * Copyright (c) 2006 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.beans;

import ch.randelshofer.reflect.*;
import java.beans.*;
import javax.swing.*;

/**
 * Provides a BoundedRangeModel interface for a JavaBeans property.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 * <br>1.0 April 9, 2006 Created.
 */
public class BoundedRangeModelBeanAdapter
        extends AbstractStateModel
        implements BoundedRangeModel, PropertyChangeListener {

    private Object bean;
    private String propertyName;
    private String setterName;
    private String getterName;
    private int min;
    private int max;
    private int extent;
    private boolean isAdjusting;
    private boolean isIsAdjustingSupported;

    /** Creates a new instance. */
    public BoundedRangeModelBeanAdapter() {
    }

    public void setBean(Object newValue) {
        int oldValue = getValue();
        try {
            if (bean != null) {
                Methods.invoke(bean, "removePropertyChangeListener", PropertyChangeListener.class, this);
            }
            this.bean = newValue;
            if (bean != null) {
                Methods.invoke(bean, "addPropertyChangeListener", PropertyChangeListener.class, this);
            }
            if (propertyName != null && oldValue!=getValue()) {
                fireStateChanged();
            }
        } catch (NoSuchMethodException ex) {
            InternalError error = new InternalError("no setter for " + propertyName + " on bean " + bean);
            error.initCause(ex);
            throw error;
        }
    }

    public void setPropertyName(String newValue) {
        propertyName = newValue;
        setterName = "set" + Character.toUpperCase(newValue.charAt(0)) + propertyName.substring(1);
        getterName = "get" + Character.toUpperCase(newValue.charAt(0)) + propertyName.substring(1);
        if (bean != null) {
            fireStateChanged();
        }
    }

    public void setIsAdjustingSupported(boolean newValue) {
        isIsAdjustingSupported = newValue;
    }

    @Override
    public void setValue(int newValue) {
        if (bean != null) {
            newValue = Math.min(max - extent, Math.max(min, newValue));

            try {
                Methods.invoke(bean, setterName, newValue);
            } catch (Throwable ex) {
                InternalError error = new InternalError("no method " + setterName + "(int) on " + bean.getClass());
                error.initCause(ex);
                throw error;
            }
        }
    }

    @Override
    public void setMinimum(int newMinimum) {
        min = newMinimum;
        fireStateChanged();
    }

    @Override
    public void setMaximum(int newMaximum) {
        max = newMaximum;
        fireStateChanged();
    }

    @Override
    public void setExtent(int newExtent) {
        extent = newExtent;
        fireStateChanged();
    }

    @Override
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {
        this.extent = extent;
        this.min = min;
        this.max = max;
        this.isAdjusting = adjusting;
        setValue(value);
        fireStateChanged();
    }

    public void setValueIsAdjusting(boolean b) {
        if (bean != null && isIsAdjustingSupported) {
            try {
                Methods.invoke(bean, "setValueIsAdjusting", b);
            } catch (NoSuchMethodException ex) {
                InternalError error = new InternalError("no setValueIsAdjusting method in bean " + bean);
                error.initCause(ex);
                throw error;
            }
        } else {
            isAdjusting = b;
        }
        fireStateChanged();
    }

    public boolean getValueIsAdjusting() {
        if (bean != null && isIsAdjustingSupported) {
            return Methods.invokeGetter(bean, "isAdjusting", false);
        } else {
            return isAdjusting;
        }
    }

    public int getValue() {
        if (bean == null) {
            return min;
        }
        return Methods.invokeGetter(bean, getterName, min);
    }

    public int getMinimum() {
        return min;
    }

    public int getMaximum() {
        return max;
    }

    public int getExtent() {
        return extent;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == propertyName) {
            fireStateChanged();
        }
    }
}

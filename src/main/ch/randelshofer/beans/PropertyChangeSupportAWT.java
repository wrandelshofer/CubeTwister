/*
 * @(#)PropertyChangeSupportAWT.java  1.0  January 5, 2006
 *
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.beans;

import java.beans.*;
/**
 * PropertyChangeSupportAWT.
 *
 * @author  Werner Randelshofer
 * @version 1.0 January 5, 2006 Created.
 */
public class PropertyChangeSupportAWT extends PropertyChangeSupport {
    private final static long serialVersionUID = 1L;
    
    /**
     * Creates a new instance.
     */
    public PropertyChangeSupportAWT(Object sourceBean) {
        super(sourceBean);
    }
    
    /**
     * Report a boolean bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * <p>
     * This is merely a convenience wrapper around the more general
     * firePropertyChange method that takes Object values.
     *
     * @param propertyName  The programmatic name of the property
     *		that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    public void firePropertyChange(String propertyName, 
					boolean oldValue, boolean newValue) {
	if (oldValue == newValue) {
	    return;
	}
	firePropertyChange(propertyName, new Boolean(oldValue), new Boolean(newValue));
    }
}

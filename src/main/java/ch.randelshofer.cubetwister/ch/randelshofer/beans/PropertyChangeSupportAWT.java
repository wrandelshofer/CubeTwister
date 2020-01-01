/* @(#)PropertyChangeSupportAWT.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.beans;

import org.jhotdraw.annotation.Nonnull;

import java.beans.PropertyChangeSupport;
/**
 * PropertyChangeSupportAWT.
 *
 * @author  Werner Randelshofer
 */
public class PropertyChangeSupportAWT extends PropertyChangeSupport {
    private final static long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public PropertyChangeSupportAWT(@Nonnull Object sourceBean) {
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

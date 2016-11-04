/*
 * @(#)BoundedRangeReader.java  1.0  April 23, 2004
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.io;

import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
/**
 * BoundedRangeReader.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class BoundedRangeReader 
extends FilterReader 
implements BoundedRangeModel {
    private int nread = 0;
    private int size = 0;
    private boolean valueIsAdjusting;
    
    /**
     * Only one ChangeEvent is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected transient ChangeEvent changeEvent = null;
    
    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();
    
    /**
     * Create a new instance.
     */
    public BoundedRangeReader(File file, boolean isBuffered) throws IOException {
        super((isBuffered) 
        ? (Reader) new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        : (Reader) new InputStreamReader(new FileInputStream(file))
        );
        
        size = (int) file.length();
    }
    /**
     * Create a new instance.
     */
    public BoundedRangeReader(File file, String charsetName, boolean isBuffered) throws IOException {
        super((isBuffered) 
        ? (Reader) new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName))
        : (Reader) new InputStreamReader(new FileInputStream(file), charsetName)
        );
        
        size = (int) file.length();
    }
    /**
     * Creates a new instance.
     * @param file The file is used to determine the maximum value of the bounded
     * range model. The maximum value is only approximate, because the characters
     * may be encoded by multiple bytes.
     */
    public BoundedRangeReader(File file, Reader in) throws IOException {
        super(in);
        size = (int) file.length();   
    }
    /**
     * Create a new instance.
     * Note that you to set the maximum value using setMaximum to get
     * a bounded range.
     */
    public BoundedRangeReader(Reader in) throws IOException {
        super(in);
        //size = in.available();
    }
    
    
    /**
     * Read a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
        int c = in.read();
        if (c >=0) {
            incrementValue(1);
        }
        return c;
    }

    /**
     * Read characters into a portion of an array.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        int nr = in.read(cbuf, off, len);
        incrementValue(nr);
        return nr;
    }

    /**
     * Skip characters.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException {
        long nr = in.skip(n);
        incrementValue( (int)nr);
        return nr;
    }


    /**
     * Tell whether this stream supports the mark() operation.
     */
    public boolean markSupported() {
	return false;
    }

    /**
     * Mark the present position in the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException {
	throw new IOException("Mark not supported");
    }

    /**
     * Reset the stream.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void reset() throws IOException {
	throw new IOException("Reset not supported");
    }

    /**
     * Increments the extent by the indicated value.
     * Negative Increments are ignored.
     *
     * @param  inc  The incrementValue value.
     */
    private void incrementValue(int inc) {
        if (inc > 0) {
            nread+=inc;
            if (nread > size) {
                size = nread;
            }
            fireStateChanged();
        }
    }
    /**
     * Returns the minimum acceptable value.
     *
     * @return the value of the minimum property
     * @see #setMinimum
     */
    public int getMinimum() {
        return 0;
    }
    
    
    /**
     * Ignored: The minimum of an input stream is always zero.
     *
     * Sets the model's minimum to <I>newMinimum</I>.   The
     * other three properties may be changed as well, to ensure
     * that:
     * <pre>
     * minimum &lt;= value &lt;= value+extent &lt;= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newMinimum the model's new minimum
     * @see #getMinimum
     * @see #addChangeListener
     */
    public void setMinimum(int newMinimum) {}
    
    
    /**
     * Returns the model's maximum.  Note that the upper
     * limit on the model's value is (maximum - extent).
     *
     * @return the value of the maximum property.
     * @see #setMaximum
     * @see #setExtent
     */
    public int getMaximum() {
        return size;
    }
    
    
    /**
     * Ignored: The maximum of an input stream can not be changed.
     * #
     * Sets the model's maximum to <I>newMaximum</I>. The other
     * three properties may be changed as well, to ensure that
     * <pre>
     * minimum &lt;= value &lt;= value+extent &lt;= maximum
     * </pre>
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newMaximum the model's new maximum
     * @see #getMaximum
     * @see #addChangeListener
     */
    public void setMaximum(int newMaximum) {
        size = newMaximum;
        fireStateChanged();
    }
    
    
    /**
     * Returns the current read position.
     *
     * Returns the model's current value.  Note that the upper
     * limit on the model's value is <code>maximum - extent</code>
     * and the lower limit is <code>minimum</code>.
     *
     * @return  the model's value
     * @see     #setValue
     */
    public int getValue() {
        return nread;
    }
    
    
    /**
     * Ignored: The value is always zero.
     *
     * Sets the model's current value to <code>newValue</code> if <code>newValue</code>
     * satisfies the model's constraints. Those constraints are:
     * <pre>
     * minimum &lt;= value &lt;= value+extent &lt;= maximum
     * </pre>
     * Otherwise, if <code>newValue</code> is less than <code>minimum</code>
     * it's set to <code>minimum</code>, if its greater than
     * <code>maximum</code> then it's set to <code>maximum</code>, and
     * if it's greater than <code>value+extent</code> then it's set to
     * <code>value+extent</code>.
     * <p>
     * When a BoundedRange model is used with a scrollbar the value
     * specifies the origin of the scrollbar knob (aka the "thumb" or
     * "elevator").  The value usually represents the origin of the
     * visible part of the object being scrolled.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param newValue the model's new value
     * @see #getValue
     */
    public void setValue(int newValue) {}
    
    
    /**
     * This attribute indicates that any upcoming changes to the value
     * of the model should be considered a single event. This attribute
     * will be set to true at the start of a series of changes to the value,
     * and will be set to false when the value has finished changing.  Normally
     * this allows a listener to only take action when the final value change in
     * committed, instead of having to do updates for all intermediate values.
     * <p>
     * Sliders and scrollbars use this property when a drag is underway.
     *
     * @param b true if the upcoming changes to the value property are part of a series
     */
    public void setValueIsAdjusting(boolean b) {
        valueIsAdjusting = b;
    }
    
    
    /**
     * Returns true if the current changes to the value property are part
     * of a series of changes.
     *
     * @return the valueIsAdjustingProperty.
     * @see #setValueIsAdjusting
     */
    public boolean getValueIsAdjusting() {
        return valueIsAdjusting;
    }
    
    
    /**
     * Returns the model's extent, the length of the inner range that
     * begins at the model's value.
     *
     * @return  the value of the model's extent property
     * @see     #setExtent
     * @see     #setValue
     */
    public int getExtent() {
        return 0;
    }
    
    
    /**
     * Ignored: The extent is always zero.
     *
     * The extent depends on the current read position.
     *
     * Sets the model's extent.  The <I>newExtent</I> is forced to
     * be greater than or equal to zero and less than or equal to
     * maximum - value.
     * <p>
     * When a BoundedRange model is used with a scrollbar the extent
     * defines the length of the scrollbar knob (aka the "thumb" or
     * "elevator").  The extent usually represents how much of the
     * object being scrolled is visible. When used with a slider,
     * the extent determines how much the value can "jump", for
     * example when the user presses PgUp or PgDn.
     * <p>
     * Notifies any listeners if the model changes.
     *
     * @param  newExtent the model's new extent
     * @see #getExtent
     * @see #setValue
     */
    public void setExtent(int newExtent) {}
    
    
    
    /**
     * Ignored: All values depend on the input stream.
     *
     * This method sets all of the model's data with a single method call.
     * The method results in a single change event being generated. This is
     * convenient when you need to adjust all the model data simulaneously and
     * do not want individual change events to occur.
     *
     * @param value  an int giving the current value
     * @param extent an int giving the amount by which the value can "jump"
     * @param min    an int giving the minimum value
     * @param max    an int giving the maximum value
     * @param adjusting a boolean, true if a series of changes are in
     *                    progress
     *
     * @see #setValue
     * @see #setExtent
     * @see #setMinimum
     * @see #setMaximum
     * @see #setValueIsAdjusting
     */
    public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {}
    
    
    
    /**
     * Adds a ChangeListener to the model's listener list.
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    
    /**
     * Removes a ChangeListener.
     *
     * @param l the ChangeListener to remove
     * @see #addChangeListener
     * @see BoundedRangeModel#removeChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    
    /**
     * Run each ChangeListeners stateChanged() method.
     *
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged() {
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

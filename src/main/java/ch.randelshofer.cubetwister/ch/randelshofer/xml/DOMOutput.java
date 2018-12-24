/* @(#)DOMOutput.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland. MIT License.
 */


package ch.randelshofer.xml;

/**
 * DOMOutput.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface DOMOutput {
    
    /**
     * Adds a new element to the DOM Document.
     * The new element is added as a child to the current element in the DOM
     * document. Then it becomes the current element.
     * The element must be closed using closeElement.
     */
    public void addElement(String tagName);
    /**
     * Closes the current element of the DOM Document.
     * The parent of the current element becomes the current element.
     * @exception IllegalArgumentException if the provided tagName does
     * not match the tag name of the element.
     */
    public void closeElement();
    /**
     * Adds a comment to the current element of the DOM Document.
     */
    public void addComment(String comment);
    /**
     * Adds a text to current element of the DOM Document.
     * Note: Multiple consecutives texts will be merged.
     */
    public void addText(String text);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void setAttribute(String name, String value);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void setAttribute(String name, int value);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void setAttribute(String name, boolean value);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void setAttribute(String name, float value);
    /**
     * Adds an attribute to current element of the DOM Document.
     */
    public void setAttribute(String name, double value);
    /**
     * Writes an object.
     */
    public void writeObject(Object o);
}

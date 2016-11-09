/* @(#)StiledDocumentProxy.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.text;

import ch.randelshofer.gui.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.undo.UndoableEdit;
/**
 * This class is designed to be less memory expensive than
 * a standard StyledDocument object.
 * This class tries to represent the document in form of a String.
 * If the document is in use, the String is converted into a StyledDocument.
 * If the document is no longer in use, then the PlainDocument is converted
 * into a String (losing all styles).
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.1 2004-04-04 Creates now a styled document with a proportional font
 * instead of with a fixed width font.
 * <br>1.0.1 2002-02-02 Clone method returned DocumentProxy instead of StyledDocumentProxy.
 */
public class StyledDocumentProxy extends ch.randelshofer.gui.text.DocumentProxy implements javax.swing.text.StyledDocument {
    /**
     * If this attribute is set to true, then StyledDocumentProxy does not
     * fire attribute changes.
     */
    private boolean isIgnoreAttributeEdits;
    private int isEditingAttributes;
    
    /** Creates new StyledDocumentProxy */
    public StyledDocumentProxy() {
    }
    
    /**
     * Sets whether AttributeEdits should be ignored.
     * If this is set to true, the DocumentProxy will not fire edit events
     * that result from attribute changes.
     */
    public void setIgnoreAttributeEdits(boolean newValue) {
        isIgnoreAttributeEdits = newValue;
    }
    
    /**
     * Adds a new style into the logical style hierarchy.  Style attributes
     * resolve from bottom up so an attribute specified in a child
     * will override an attribute specified in the parent.
     *
     * @param nm   the name of the style (must be unique within the
     *  collection of named styles).  The name may be null if the style
     *  is unnamed, but the caller is responsible
     *  for managing the reference returned as an unnamed style can't
     *  be fetched by name.  An unnamed style may be useful for things
     *  like character attribute overrides such as found in a style
     *  run.
     * @param parent the parent style.  This may be null if unspecified
     *  attributes need not be resolved in some other style.
     * @return the style
     */
    public Style addStyle(String nm, Style parent) {
        return ((StyledDocument) getDocumentModel()).addStyle(nm, parent);
    }
    
    /**
     * Takes a set of attributes and turn it into a background color
     * specification.  This might be used to specify things
     * like brighter, more hue, etc.
     *
     * @param attr the set of attributes
     * @return the color
     */
    public Color getBackground(AttributeSet attr) {
        return ((StyledDocument) getDocumentModel()).getBackground(attr);
    }
    
    /**
     * Gets the element that represents the character that
     * is at the given offset within the document.
     *
     * @param pos the offset &gt;= 0
     * @return the element
     */
    public Element getCharacterElement(int pos) {
        return ((StyledDocument) getDocumentModel()).getCharacterElement(pos);
    }
    
    /**
     * Takes a set of attributes and turn it into a font
     * specification.  This can be used to turn things like
     * family, style, size, etc into a font that is available
     * on the system the document is currently being used on.
     *
     * @param attr the set of attributes
     * @return the font
     */
    public Font getFont(AttributeSet attr) {
        return ((StyledDocument) getDocumentModel()).getFont(attr);
    }
    
    /**
     * Takes a set of attributes and turn it into a foreground color
     * specification.  This might be used to specify things
     * like brighter, more hue, etc.
     *
     * @param attr the set of attributes
     * @return the color
     */
    public Color getForeground(AttributeSet attr) {
        return ((StyledDocument) getDocumentModel()).getForeground(attr);
    }
    
    /**
     * Gets a logical style for a given position in a paragraph.
     *
     * @param p the position &gt;= 0
     * @return the style
     */
    public Style getLogicalStyle(int p) {
        return ((StyledDocument) getDocumentModel()).getLogicalStyle(p);
    }
    
    /**
     * Gets the element that represents the paragraph that
     * encloses the given offset within the document.
     *
     * @param pos the offset &gt;= 0
     * @return the element
     */
    public Element getParagraphElement(int pos) {
        return ((StyledDocument) getDocumentModel()).getParagraphElement(pos);
    }
    
    /**
     * Fetches a named style previously added.
     *
     * @param nm  the name of the style
     * @return the style
     */
    public Style getStyle(String nm) {
        return ((StyledDocument) getDocumentModel()).getStyle(nm);
    }
    
    /**
     * Removes a named style previously added to the document.
     *
     * @param nm  the name of the style to remove
     */
    public void removeStyle(String nm) {
        ((StyledDocument) getDocumentModel()).removeStyle(nm);
    }
    
    /**
     * Changes the content element attributes used for the given range of
     * existing content in the document.  All of the attributes
     * defined in the given Attributes argument are applied to the
     * given range.  This method can be used to completely remove
     * all content level attributes for the given range by
     * giving an Attributes argument that has no attributes defined
     * and setting replace to true.
     *
     * @param offset the start of the change &gt;= 0
     * @param length the length of the change &gt;= 0
     * @param s    the non-null attributes to change to.  Any attributes
     * defined will be applied to the text for the given range.
     * @param replace indicates whether or not the previous
     * attributes should be cleared before the new attributes
     * as set.  If true, the operation will replace the
     * previous attributes entirely.  If false, the new
     * attributes will be merged with the previous attributes.
     */
    public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace) {
        isEditingAttributes++;
        ((StyledDocument) getDocumentModel()).setCharacterAttributes(offset, length, s, replace);
        isEditingAttributes--;
    }
    
    /**
     * Sets the logical style to use for the paragraph at the
     * given position.  If attributes aren't explicitly set
     * for character and paragraph attributes they will resolve
     * through the logical style assigned to the paragraph, which
     * in turn may resolve through some hierarchy completely
     * independent of the element hierarchy in the document.
     *
     * @param pos the starting position &gt;= 0
     * @param s the style to set
     */
    public void setLogicalStyle(int pos, Style s) {
        isEditingAttributes++;
        ((StyledDocument) getDocumentModel()).setLogicalStyle(pos, s);
        isEditingAttributes--;
    }
    
    /**
     * Sets paragraph attributes.
     *
     * @param offset the start of the change &gt;= 0
     * @param length the length of the change &gt;= 0
     * @param s    the non-null attributes to change to.  Any attributes
     * defined will be applied to the text for the given range.
     * @param replace indicates whether or not the previous
     * attributes should be cleared before the new attributes
     * are set.  If true, the operation will replace the
     * previous attributes entirely.  If false, the new
     * attributes will be merged with the previous attributes.
     */
    public void setParagraphAttributes(int offset, int length, AttributeSet s, boolean replace) {
        isEditingAttributes++;
        ((StyledDocument) getDocumentModel()).setParagraphAttributes(offset, length, s, replace);
        isEditingAttributes--;
    }
    
    protected Document createDocumentModel() {
        StyleContext styles = new StyleContext();
        Style style = styles.getStyle(StyleContext.DEFAULT_STYLE);
        style.addAttribute(StyleConstants.FontFamily, Fonts.getDialogFont().getFamily());
        style.addAttribute(StyleConstants.FontSize, new Integer(Fonts.getDialogFont().getSize()));
        DefaultStyledDocument doc = new DefaultStyledDocument(styles);
        return doc;
    }
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     */
    public void fireUndoableEditHappened(UndoableEdit edit) {
        if (! isIgnoreAttributeEdits || isEditingAttributes == 0) {
 super.fireUndoableEditHappened(edit);
        }
    }
}

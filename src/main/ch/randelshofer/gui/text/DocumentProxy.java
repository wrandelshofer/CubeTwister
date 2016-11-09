/* @(#)DocumentProxy.java
 * Copyright (c) 2001 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.gui.text;

import ch.randelshofer.gui.*;
import ch.randelshofer.gui.event.DefaultDocumentEvent;
import java.awt.event.*;
import ch.randelshofer.undo.*;
import java.util.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import java.lang.ref.*;

/**
 * This class is designed to be less memory expensive than a standard Document
 * object. This class tries to represent the document in form of a char array.
 * If the document is in use, the char array is converted into a PlainDocument.
 * If the document is no longer in use, then the PlainDocument is converted back
 * into a char array.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * font instead of with a fixed width font.
 * <br>2.0 2003-10-31 Use a char array instead of a String.
 * <br>1.0.2 2002-05-13 Clone did still not work properly.
 * <br>1.0.1 2002-02-02 Clone did not work properly.
 * <br>1.0 2001-10-09
 */
public class DocumentProxy
        implements javax.swing.text.Document, Cloneable, DocumentListener, UndoableEditListener {

    /**
     */
    private final static char[] EMPTY = new char[0];
    /**
     * When at least one DocumentListener is registered to us, then our target
     * is an instance of Document. If there is no listener registered, then our
     * target is a char array. Implentation note: We treat the char array as
     * immutable.
     */
    private Object target = EMPTY;
    /**
     * We need this to determine, if there are listeners.
     */
    private EventListenerList listenerList;
    /**
     * Soft reference to the Document.
     */
    private SoftReference<Document> softReference;

    /**
     * Creates new DocumentProxy.
     */
    public DocumentProxy() {
    }

    /**
     * This method allows an application to mark a place in a sequence of
     * character content. This mark can then be used to tracks change as
     * insertions and removals are made in the content. The policy is that
     * insertions always occur prior to the current position (the most common
     * case) unless the insertion location is zero, in which case the insertion
     * is forced to a position that follows the original position.
     *
     * @param offs the offset from the start of the document &gt;= 0
     * @return the position
     * @exception BadLocationException if the given position does not represent
     * a valid location in the associated document
     */
    public Position createPosition(int offs) throws BadLocationException {
        return getDocumentModel().createPosition(offs);
    }

    /**
     * Returns the root element that views should be based upon, unless some
     * other mechanism for assigning views to element structures is provided.
     *
     * @return the root element
     */
    public Element getDefaultRootElement() {
        return getDocumentModel().getDefaultRootElement();
    }

    /**
     * Returns a position that represents the end of the document. The position
     * returned can be counted on to track change and stay located at the end of
     * the document.
     *
     * @return the position
     */
    public Position getEndPosition() {
        return getDocumentModel().getEndPosition();
    }

    /**
     * Returns number of characters of content currently in the document.
     *
     * @return number of characters &gt;= 0
     */
    public int getLength() {
        return (target instanceof Document) ? ((Document) target).getLength() : ((char[]) target).length;
    }

    /**
     * Gets properties associated with the document. Allows one to store things
     * like the document title, author, etc.
     *
     * @param key a non-null property
     * @return the properties
     */
    public Object getProperty(Object key) {
        return getDocumentModel().getProperty(key);
    }

    /**
     * Returns all of the root elements that are defined.
     * <p>
     * Typically there will be only one document structure, but the interface
     * supports building an arbitrary number of structural projections over the
     * text data. The document can have multiple root elements to support
     * multiple document structures. Some examples might be:
     * </p>
     * <ul>
     * <li>Text direction.
     * <li>Lexical token streams.
     * <li>Parse trees.
     * <li>Conversions to formats other than the native format.
     * <li>Modification specifications.
     * <li>Annotations.
     * </ul>
     *
     * @return the root element
     */
    public Element[] getRootElements() {
        return getDocumentModel().getRootElements();
    }

    /**
     * Returns a position that represents the start of the document. The
     * position returned can be counted on to track change and stay located at
     * the beginning of the document.
     *
     * @return the position
     */
    public Position getStartPosition() {
        return getDocumentModel().getStartPosition();
    }

    /**
     * Fetches the text contained within the given portion of the document.
     *
     * @param offset the offset into the document representing the desired start
     * of the text &gt;= 0
     * @param length the length of the desired string &gt;= 0
     * @return the text, in a String of length &gt;= 0
     * @exception BadLocationException some portion of the given range was not a
     * valid part of the document. The location in the exception is the first
     * bad position encountered.
     */
    public String getText(int offset, int length) throws BadLocationException {
        return (target instanceof Document)
                ? ((Document) target).getText(offset, length)
                : new String((char[]) target, offset, length);
    }

    /**
     * Fetches the text contained within the given portion of the document.
     *
     * @param offset the offset into the document representing the desired start
     * of the text &gt;= 0
     * @param length the length of the desired string &gt;= 0
     * @param txt the Segment object to return the text in
     *
     * @exception BadLocationException Some portion of the given range was not a
     * valid part of the document. The location in the exception is the first
     * bad position encountered.
     */
    public void getText(int offset, int length, Segment txt) throws BadLocationException {
        getDocumentModel().getText(offset, length, txt);
    }

    /**
     * Inserts a string of content. This will cause a DocumentEvent of type
     * DocumentEvent.EventType.INSERT to be sent to the registered
     * DocumentListers, unless an exception is thrown. The DocumentEvent will be
     * delivered by calling the insertUpdate method on the DocumentListener. The
     * offset and length of the generated DocumentEvent will indicate what
     * change was actually made to the Document.
     * 
     * <p>
     * If the Document structure changed as result of the insertion, the details
     * of what Elements were inserted and removed in response to the change will
     * also be contained in the generated DocumentEvent. It is up to the
     * implementation of a Document to decide how the structure should change in
     * response to an insertion.
     * <p>
     * If the Document supports undo/redo, an UndoableEditEvent will also be
     * generated.
     *
     * @param offset the offset into the document to insert the content &gt;= 0.
     * All positions that track change at or after the given location will move.
     * @param str the string to insert
     * @param a the attributes to associate with the inserted content. This may
     * be null if there are no attributes.
     * @exception BadLocationException the given insert position is not a valid
     * position within the document
     * @see javax.swing.event.DocumentEvent
     * @see javax.swing.event.DocumentListener
     * @see javax.swing.event.UndoableEditEvent
     * @see javax.swing.event.UndoableEditListener
     */
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        getDocumentModel().insertString(offset, str, a);
        fireStateChanged();
    }

    /**
     * Puts a new property on the list.
     *
     * @param key the non-null property key
     * @param value the property value
     */
    public void putProperty(Object key, Object value) {
        getDocumentModel().putProperty(key, value);
    }

    /**
     * Removes a portion of the content of the document. This will cause a
     * DocumentEvent of type DocumentEvent.EventType.REMOVE to be sent to the
     * registered DocumentListeners, unless an exception is thrown. The
     * notification will be sent to the listeners by calling the removeUpdate
     * method on the DocumentListeners.
     * <p>
     * To ensure reasonable behavior in the face of concurrency, the event is
     * dispatched after the mutation has occurred. This means that by the time a
     * notification of removal is dispatched, the document has already been
     * updated and any marks created by createPosition have already changed. For
     * a removal, the end of the removal range is collapsed down to the start of
     * the range, and any marks in the removal range are collapsed down to the
     * start of the range.
     * <p>
     * If the Document structure changed as result of the removal, the details
     * of what Elements were inserted and removed in response to the change will
     * also be contained in the generated DocumentEvent. It is up to the
     * implementation of a Document to decide how the structure should change in
     * response to a remove.
     * <p>
     * If the Document supports undo/redo, an UndoableEditEvent will also be
     * generated.
     *
     * @param offs the offset from the begining &gt;= 0
     * @param len the number of characters to remove &gt;= 0
     * @exception BadLocationException some portion of the removal range was not
     * a valid part of the document. The location in the exception is the first
     * bad position encountered.
     * @see javax.swing.event.DocumentEvent
     * @see javax.swing.event.DocumentListener
     * @see javax.swing.event.UndoableEditEvent
     * @see javax.swing.event.UndoableEditListener
     */
    public void remove(int offs, int len) throws BadLocationException {
        getDocumentModel().remove(offs, len);
        fireStateChanged();
    }

    /**
     * Registers the given observer to begin receiving notifications when
     * changes are made to the document.
     *
     * @param listener the observer to register
     * @see Document#removeDocumentListener
     */
    public void addDocumentListener(DocumentListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(DocumentListener.class, listener);
        getDocumentModel();
    }

    /**
     * Registers the given observer to begin receiving notifications when
     * undoable edits are made to the document.
     *
     * @param listener the observer to register
     * @see javax.swing.event.UndoableEditEvent
     */
    public void addUndoableEditListener(UndoableEditListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(UndoableEditListener.class, listener);
    }

    /**
     * Registers the given observer to begin receiving notifications when
     * changes are made to the document.
     *
     * @param listener the observer to register
     * @see Document#removeDocumentListener
     */
    public void addChangeListener(ChangeListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(ChangeListener.class, listener);
        getDocumentModel();
    }

    /**
     * Unregisters the given observer from the notification list so it will no
     * longer receive change updates.
     *
     * @param listener the observer to register
     * @see Document#addDocumentListener
     */
    public void removeDocumentListener(DocumentListener listener) {
        if (listenerList != null) {
            listenerList.remove(DocumentListener.class, listener);
            if (!hasDocumentListeners()) {
                flushDocumentModel();
            }
            if (listenerList.getListenerCount() == 0) {
                listenerList = null;
            }
        }
    }

    /**
     * Unregisters the given observer from the notification list so it will no
     * longer receive updates.
     *
     * @param listener the observer to remove
     * @see javax.swing.event.UndoableEditEvent
     */
    public void removeUndoableEditListener(UndoableEditListener listener) {
        if (listenerList != null) {
            listenerList.remove(UndoableEditListener.class, listener);
            if (listenerList.getListenerCount() == 0) {
                listenerList = null;
            }
        }
    }

    /**
     * Unregisters the given observer from the notification list so it will no
     * longer receive updates.
     *
     * @param listener the observer to remove
     * @see javax.swing.event.UndoableEditEvent
     */
    public void removeChangeListener(ChangeListener listener) {
        if (listenerList != null) {
            listenerList.remove(ChangeListener.class, listener);
            if (listenerList.getListenerCount() == 0) {
                listenerList = null;
            }
        }
    }

    /**
     * This allows the model to be safely rendered in the presence of currency,
     * if the model supports being updated asynchronously. The given runnable
     * will be executed in a way that allows it to safely read the model with no
     * changes while the runnable is being executed. The runnable itself may
     * <em>not</em>
     * make any mutations.
     *
     * @param r a Runnable used to render the model
     */
    public void render(Runnable r) {
        getDocumentModel().render(r);
    }

    public void setText(String text) {
        if (text == null) {
            text = "";
        }

        Document softDocument = (softReference != null) ? softReference.get() : null;
        if (target instanceof char[] && softDocument == null) {
            //if (! charModel.equals(text)) {
            char[] oldCharModel = (char[]) target;
            String oldValue = new String((char[]) target);
            target = text.toCharArray();

            fireRemoveUpdate(new DefaultDocumentEvent(this, 0, oldValue.length(), DocumentEvent.EventType.REMOVE));
            fireInsertUpdate(new DefaultDocumentEvent(this, 0, text.length(), DocumentEvent.EventType.INSERT));

            fireUndoableEditHappened(
                    new UndoableObjectEdit(this, "Text", oldCharModel, target) {
                        private final static long serialVersionUID = 1L;

                        public void revert(Object a, Object b) {
                            if (target instanceof char[]) {
                                target = (char[]) b;
                            } else {
                                try {
                                    Document document = (Document) target;
                                    document.remove(0, document.getLength());
                                    document.insertString(0, new String((char[]) b), null);
                                } catch (BadLocationException e) {
                                    throw new InternalError(e.getMessage());
                                }
                            }
                        }
                    }
            );
            //}
            fireStateChanged();
        } else {
            if (target instanceof char[]) {
                target = softDocument;
            }

            CompositeEdit ce = new CompositeEdit("Text");
            fireUndoableEditHappened(ce);
            try {
                Document document = (Document) target;
                document.remove(0, document.getLength());
                document.insertString(0, text, null);
            } catch (BadLocationException e) {
                throw new InternalError(e.getMessage());
            }
            fireUndoableEditHappened(ce);
            fireStateChanged();
        }
    }

    public String getText() {
        try {
            if (target instanceof char[]) {
                return new String((char[]) target);
            } else {
                Document doc = (Document) target;
                return doc.getText(0, doc.getLength());
            }
        } catch (BadLocationException e) {
            throw new InternalError(e.getMessage());
        }
    }

    protected Document getDocumentModel() {
        try {
            if (target instanceof char[]) {
                Document doc = null;
                if (softReference != null) {
                    doc = softReference.get();
                }
                if (doc == null) {
                    doc = createDocumentModel();
                    doc.insertString(0, new String((char[]) target), null);
                    softReference = new SoftReference<Document>(doc);
                }
                target = doc;

                doc.addDocumentListener(this);
                doc.addUndoableEditListener(this);
            }
            return (Document) target;
        } catch (BadLocationException e) {
            throw new InternalError(e.getMessage());
        }
    }

    protected Document createDocumentModel() {
        StyleContext styles = new StyleContext();
        Style style = styles.getStyle(StyleContext.DEFAULT_STYLE);
        style.addAttribute(StyleConstants.FontFamily, Fonts.getDialogFont().getFamily());
        style.addAttribute(StyleConstants.FontSize, new Integer(Fonts.getDialogFont().getSize()));
        PlainDocument doc = new PlainDocument();
        //doc.styles);
        return doc;
        //return new PlainDocument();
    }

    protected void flushDocumentModel() {
        try {
            if (target instanceof Document) {
                Document doc = (Document) target;
                doc.removeDocumentListener(this);
                doc.removeUndoableEditListener(this);
                target = doc.getText(0, doc.getLength()).toCharArray();
            }
        } catch (BadLocationException e) {
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     */
    public void fireUndoableEditHappened(UndoableEdit edit) {
        if (listenerList != null) {
            UndoableEditEvent evt = null;

            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == UndoableEditListener.class) {
                    // Lazily create the event
                    if (evt == null) {
                        evt = new UndoableEditEvent(this, edit);
                    }
                    ((UndoableEditListener) listeners[i + 1]).undoableEditHappened(evt);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     */
    public void fireStateChanged() {
        if (listenerList != null) {
            ChangeEvent evt = null;

            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChangeListener.class) {
                    // Lazily create the event
                    if (evt == null) {
                        evt = new ChangeEvent(this);
                    }
                    ((ChangeListener) listeners[i + 1]).stateChanged(evt);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     */
    public void fireInsertUpdate(DocumentEvent evt) {
        if (listenerList != null) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == DocumentListener.class) {
                    ((DocumentListener) listeners[i + 1]).insertUpdate(evt);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     */
    public void fireRemoveUpdate(DocumentEvent evt) {
        if (listenerList != null) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == DocumentListener.class) {
                    ((DocumentListener) listeners[i + 1]).removeUpdate(evt);
                }
            }
        }
    }

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     */
    public void fireChangedUpdate(DocumentEvent evt) {
        if (listenerList != null) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == DocumentListener.class) {
                    ((DocumentListener) listeners[i + 1]).changedUpdate(evt);
                }
            }
        }
    }

    /**
     * Counts all registered DocumentListeners and all registered
     * UndoableEditListeners.
     */
    public boolean hasDocumentListeners() {
        if (listenerList != null) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == DocumentListener.class
                        && listeners[i] != this) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a clone of this object.
     */
    public Object clone() {
        try {
            DocumentProxy that = (DocumentProxy) super.clone();
            if (target instanceof Document) {
                Document doc = (Document) target;
                that.target = doc.getText(0, doc.getLength()).toCharArray();
            } else {
                //This comment stands here to show, that we _share_ the
                //target array with our clone. This is the reasy why it
                //has to be immutable.
                //that.target = this.target;
            }
            that.listenerList = null;
            that.softReference = null;

            return that;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        } catch (BadLocationException e) {
            throw new InternalError(e.getMessage());
        }
    }

    /**
     * Gives notification that a portion of the document has been removed. The
     * range is given in terms of what the view last saw (that is, before
     * updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate(DocumentEvent e) {
        // FIXME: Should create a new DocumentEvent
        fireRemoveUpdate(e);
    }

    /**
     * Gives notification that there was an insert into the document. The range
     * given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate(DocumentEvent e) {
        // FIXME: Should create a new DocumentEvent
        fireInsertUpdate(e);
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate(DocumentEvent e) {
        // FIXME: Should create a new DocumentEvent
        fireChangedUpdate(e);
    }

    /**
     * An undoable edit happened
     */
    public void undoableEditHappened(UndoableEditEvent e) {
        fireUndoableEditHappened(e.getEdit());
    }
}

/*
 * @(#)DefaultDocumentEvent.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.event;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.util.EventObject;

/**
 * DefaultDocumentEvent.
 * @author  Werner Randelshofer
 */
public class DefaultDocumentEvent
        extends EventObject
implements DocumentEvent {
    private final static long serialVersionUID = 1L;
    private int offset;
    private int length;
    private DocumentEvent.EventType type;

    /**
     * Creates a new DefaultDocumentEvent.
     *
     * @param src  The Source of the event.
     * @param offs the offset into the document of the change &gt;= 0
     * @param len  the length of the change &gt;= 0
     * @param type the type of event DocumentEvent.EventType);
     */
    public DefaultDocumentEvent(@Nonnull Document src, int offs, int len, DocumentEvent.EventType type) {
        super(src);
        this.offset = offs;
        this.length = len;
        this.type = type;
    }

    /**
     * Gets the change information for the given element.
     * The change information describes what elements were
     * added and removed and the location.  If there were
     * no changes, null is returned.
     * <p>
     * This method is for observers to discover the structural
     * changes that were made.  This means that only elements
     * that existed prior to the mutation (and still exist after
     * the mutatino) need to have ElementChange records.
     * The changes made available need not be recursive.
     * <p>
     * For example, if the an element is removed from it's
     * parent, this method should report that the parent
     * changed and provide an ElementChange implementation
     * that describes the change to the parent.  If the
     * child element removed had children, these elements
     * do not need to be reported as removed.
     * <p>
     * If an child element is insert into a parent element,
     * the parent element should report a change.  If the
     * child element also had elements inserted into it
     * (grandchildren to the parent) these elements need
     * not report change.
     *
     * @param elem the element
     * @return the change information, or null if the
     *  element was not modified
     */
    @Nullable
    public ElementChange getChange(Element elem) {
        return null;
    }

    /**
     * Gets the document that sourced the change event.
     *
     * @return the document
     */
    @Nonnull
    public Document getDocument() {
        return (Document) getSource();
    }

    /**
     * Returns the length of the change.
     *
     * @return the length &gt;= 0
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the type of event.
     *
     * @return the type
     */
    public DocumentEvent.EventType getType() {
        return type;
    }

    /**
     * Returns the offset within the document of the start
     * of the change.
     *
     * @return the offset &gt;= 0
     */
    public int getOffset() {
        return offset;
    }

}

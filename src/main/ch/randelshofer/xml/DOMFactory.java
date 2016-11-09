/* @(#)DOMFactory.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.xml;

/**
 * DOMFactory.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface DOMFactory {
    /**
     * Returns the tag name for the specified object.
     * Note: The tag names "string", "int", "float", "long", "double", "boolean", 
     * "null" are reserved and must not be returned by this operation.
     */
    public String getTagName(DOMStorable o);
    public Object create(String tagName);
}
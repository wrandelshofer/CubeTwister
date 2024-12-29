/*
 * @(#)DOMFactory.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.xml;

/**
 * DOMFactory.
 *
 * @author  Werner Randelshofer
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
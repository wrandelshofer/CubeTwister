/* @(#)DOMStorable.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.xml;

/**
 * DOMStorable.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface DOMStorable {
    public void write(DOMOutput out);
    public void read(DOMInput in);
}

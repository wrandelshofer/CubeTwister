/*
 * @(#)DOMStorable.java  1.0  February 17, 2004
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
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

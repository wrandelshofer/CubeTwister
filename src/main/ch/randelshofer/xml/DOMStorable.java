/*
 * @(#)DOMStorable.java  1.0  February 17, 2004
 *
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.xml;

/**
 * DOMStorable.
 *
 * @author  Werner Randelshofer
 * @version 1.0 February 17, 2004 Create..
 */
public interface DOMStorable {
    public void write(DOMOutput out);
    public void read(DOMInput in);
}

/*
 * @(#)CubeListener.java  1.0  December 21, 2003
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.rubik;

/**
 * The listener interface for receiving cube events.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface CubeListener extends java.util.EventListener {
    public void cubeTwisted(CubeEvent evt);
    public void cubeChanged(CubeEvent evt);
}

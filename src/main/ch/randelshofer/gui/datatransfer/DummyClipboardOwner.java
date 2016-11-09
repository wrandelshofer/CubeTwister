/* @(#)DummyClipboardOwner.java
 * Copyright (c) 2003 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.gui.datatransfer;

/**
 * DummyClipboardOwner.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class DummyClipboardOwner implements java.awt.datatransfer.ClipboardOwner {
    private static DummyClipboardOwner instance;
    
    public static DummyClipboardOwner getInstance() {
        if (instance == null) {
            instance = new DummyClipboardOwner();
        }
        return instance;
    }
    
    /** Creates a new instance. */
    private DummyClipboardOwner() {
    }
    
    public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, java.awt.datatransfer.Transferable contents) {
    }
    
}

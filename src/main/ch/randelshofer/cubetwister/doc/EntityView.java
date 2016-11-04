/*
 * @(#)EntityView.java  1.0  January 5, 2006
 *
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.cubetwister.doc;

import javax.swing.*;

import ch.randelshofer.undo.*;

/**
 * Presents an EntityModel in a JComponent.
 *
 * @author  Werner Randelshofer
 * @version 1.0 January 5, 2006 Created.
 */
public interface EntityView extends Undoable {
    public void setModel(EntityModel newValue);
    public JComponent getViewComponent();
}

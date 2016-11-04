/*
 * @(#)EntityView.java  1.0  January 5, 2006
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.cubetwister.doc;

import javax.swing.*;

import ch.randelshofer.undo.*;

/**
 * Presents an EntityModel in a JComponent.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface EntityView extends Undoable {
    public void setModel(EntityModel newValue);
    public JComponent getViewComponent();
}

/* @(#)EntityView.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
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

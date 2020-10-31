/*
 * @(#)EntityView.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.cubetwister.doc;

import ch.randelshofer.undo.Undoable;

import javax.swing.JComponent;

/**
 * Presents an EntityModel in a JComponent.
 *
 * @author  Werner Randelshofer
 */
public interface EntityView extends Undoable {
    public void setModel(EntityModel newValue);
    public JComponent getViewComponent();
}

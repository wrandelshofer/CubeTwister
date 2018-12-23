/* @(#)WizardModel.java
 * Copyright (c) 2004 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import javax.swing.*;
import java.beans.*;

/**
 * The wizard model extends the list model.
 * The list model functionality is used to display the steps of a task.
 * The list model is extended by a getPanel method which is used to retrieve
 * a panel for the current step.
 *
 * @author  werni
 */
public interface WizardModel {
    /**
     * Returns the number of panels.
     * If this changes, the model fires a property change event with the
     * name "panelCount".
     */
    public int getPanelCount();
    /**
     * Returns a panel.
     */
    public JComponent getPanel(int index);
    /**
     * Returns the title of a panel.
     */
    public String getPanelTitle(int index);
    /**
     * Returns true, if the wizard can be finished.
     */
    public boolean canFinish();
    /**
     * Finishes the wizard.
     */
    public void finish();
    /**
     * Cancels the wizard.
     */
    public void cancel();
    /**
     * The title of the wizard.
     */
    public String getTitle();
    
    public void addPropertyChangeListener(PropertyChangeListener l);
    public void removePropertyChangeListener(PropertyChangeListener l);
}

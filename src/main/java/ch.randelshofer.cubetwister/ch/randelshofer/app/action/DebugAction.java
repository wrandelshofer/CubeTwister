/* @(#)DebugAction.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.app.action;

import ch.randelshofer.debug.*;
import org.jhotdraw.app.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;
import org.jhotdraw.util.ResourceBundleUtil;
/**
 * DebugAction.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class DebugAction extends AbstractAction {
    private final static long serialVersionUID = 1L;
    public final static String ID = "application.debug";
    private Application app;
    private Debugger debugger;

    /** Creates a new instance. */
    public DebugAction(Application app) {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.app.Labels"));
        labels.configureAction(this, ID);
        this.app = app;
        }

    public void actionPerformed(ActionEvent evt) {
        if (debugger == null) {
            debugger = new Debugger();
            debugger.setSize(400,400);
            debugger.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        debugger.setVisible(true);
    }
}

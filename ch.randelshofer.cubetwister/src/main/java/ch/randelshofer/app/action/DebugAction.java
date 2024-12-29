/*
 * @(#)DebugAction.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.app.action;

import ch.randelshofer.debug.Debugger;
import org.jhotdraw.app.Application;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
/**
 * DebugAction.
 *
 * @author  Werner Randelshofer
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

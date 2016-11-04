/*
 * @(#)DebugAction.java  1.0  January 6, 2006
 * Copyright (c) 2005 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */

package ch.randelshofer.app.action;

import ch.randelshofer.debug.*;
import org.jhotdraw.app.*;
import java.awt.*;
import java.awt.event.*;
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
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ch.randelshofer.app.Labels");
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

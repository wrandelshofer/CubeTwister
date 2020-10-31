/*
 * @(#)PreferencesAction.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.action.app.AbstractPreferencesAction;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * PreferencesAction.
 *
 * @author Werner Randelshofer
 */
public class PreferencesAction extends AbstractPreferencesAction {
    private final static long serialVersionUID = 1L;

    private static JPanel panel;
    @Nullable
    private static JDialog dialog;

    public PreferencesAction(Application app) {
        super(app);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (panel == null) {
            panel = new PreferencesPanel();
            ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.cubetwister.Labels"));
            Component appComponent=getApplication().getComponent();
            dialog = new JDialog(appComponent==null?null:SwingUtilities.getWindowAncestor(appComponent));
            dialog.add(panel);
            dialog.setTitle(labels.getString("application.preferences.text"));
            dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            Preferences prefs = Preferences.userNodeForPackage(PreferencesAction.class);
            PreferencesUtil.installFramePrefsHandler(prefs, "preferencesWindow", dialog, new Dimension(400,300));
        }
        dialog.setVisible(true);
    }
}

/* @(#)HelpAction.java
 * Copyright (c) 2008 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.app.action;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.ResourceBundle;

/**
 * HelpAction.
 *
 * @author Werner Randelshofer
 */
public class HelpAction extends AbstractAction {
    private final static long serialVersionUID = 1L;

    public final static String ID = "help";
    @Nullable
    private static HelpSet hs;
    private static HelpBroker hb;

    public HelpAction() {
        ResourceBundleUtil labels = new ResourceBundleUtil(ResourceBundle.getBundle("ch.randelshofer.app.Labels"));
        labels.configureAction(this, ID);
    }

    public void actionPerformed(@Nonnull ActionEvent e) {
        if (hs == null) {
// Find the HelpSet file and create the HelpSet object: 
            String helpHS = "help/helpset.hs";
            ClassLoader cl = HelpAction.class.getClassLoader();
            try {
                URL hsURL = HelpSet.findHelpSet(cl, helpHS);
                if (hsURL == null) {
                    throw new Exception("Could not load HelpSet \"" + helpHS + "\"");
                }
                hsURL = HelpAction.fixJarURL(hsURL); // Fixup jar protocol URL for JWS 1.5.0_16
                hs = new HelpSet(null, hsURL);

            } catch (Exception ee) {
                // Say what the exception really is 
                System.out.println("HelpSet " + ee.getMessage());
                System.out.println("HelpSet " + helpHS + " not found");
                return;
            }
        }
        if (hb == null) {
// Create a HelpBroker object: 
            hb = hs.createHelpBroker();
        }
        new CSH.DisplayHelpFromSource(hb).actionPerformed(e);

    }

    @Nonnull
    public static URL fixJarURL(@Nonnull URL url) {
        String originalURLProtocol = url.getProtocol();
        if ("jar".equalsIgnoreCase(originalURLProtocol) == false) {
            return url;
        }

        String originalURLString = url.toString();
        int bangSlashIndex = originalURLString.indexOf("!/");
        if (bangSlashIndex > -1) {
            return url;
        }

        String originalURLPath = url.getPath();

        URLConnection urlConnection;
        try {
            urlConnection = url.openConnection();
            if (urlConnection == null) {
                throw new IOException("urlConnection is null");
            }
        } catch (IOException e) {
            return url;
        }

        Permission urlConnectionPermission;
        try {
            urlConnectionPermission = urlConnection.getPermission();
            if (urlConnectionPermission == null) {
                throw new IOException("urlConnectionPermission is null");
            }
        } catch (IOException e) {
            return url;
        }

        String urlConnectionPermissionName = urlConnectionPermission.getName();
        if (urlConnectionPermissionName == null) {
            return url;
        }

        File file = new File(urlConnectionPermissionName);
        if (file.exists() == false) {
            return url;
        }

        String newURLStr;
        try {
            newURLStr = "jar:" + file.toURI().toURL().toExternalForm() + "!/" + originalURLPath;
        } catch (MalformedURLException e) {
            return url;
        }

        try {
            url = new URL(newURLStr);
        } catch (MalformedURLException e) {
            return url;
        }
        return url;
    }
}

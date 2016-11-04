/*
 * @(#)HelpAction.java  1.1  2008-12-20
 * 
 * Copyright (c) 2008 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.app.action;

import java.awt.event.ActionEvent;
import java.net.*;
import java.io.*;
import java.security.Permission;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * HelpAction.
 *
 * @author Werner Randelshofer
 * @version 1.1 2008-12-20 Fix JAR URL for JWS.
 * <br>1.0 2008-10-12 Created.
 */
public class HelpAction extends AbstractAction {
    private final static long serialVersionUID = 1L;

    public final static String ID = "help";
    private static HelpSet hs;
    private static HelpBroker hb;

    public HelpAction() {
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("ch.randelshofer.app.Labels");
        labels.configureAction(this, ID);
    }

    public void actionPerformed(ActionEvent e) {
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

    public static URL fixJarURL(URL url) {
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

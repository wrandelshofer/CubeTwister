/* @(#)Main.java
 * Copyright (c) 2005 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.cubetwister;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jhotdraw.app.*;

/**
 * Main.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class Main {

    /**
     * Creates a new instance.
     */
    public Main() {
    }

    public static String getVersion() {
        String version = Main.class.getPackage().getImplementationVersion();
        return version == null ? "unknown" : version;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // Enforce English Locale
        Locale.setDefault(Locale.ENGLISH);

        // Enforce Windows
        //ResourceBundleUtil.setVerbose(true);
        //ResourceBundleUtil.putPropertyNameModifier("os", "win", "default");

        // CubeTwisterOSXApplication.main(args);
        CubeTwisterApplicationModel am = new CubeTwisterApplicationModel();
        am.setCopyright("\u00a9 Werner Randelshofer. All Rights Reserved.");
        am.setName("CubeTwister");
        am.setVersion(getVersion());
        am.setViewFactory(()->{
            try {
             return (View)   Class.forName("ch.randelshofer.cubetwister.CubeTwisterView").getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
       });

        Application app;
        if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            app = new OSXApplication();
        } else {
            app = new SDIApplication();
        }
          //  app = new SDIApplication();
        app.setModel(am);
        app.launch(args);

        if (Runtime.getRuntime().maxMemory() < 500*1024*1024) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                JOptionPane.showMessageDialog(null, //
                        "CubeTwister needs at least 500 MB of RAM to run.\n"+//
                        "Please relaunch CubeTwister with the Java option -Xmx512M .",//
                        "CubeTwister",//
                        JOptionPane.WARNING_MESSAGE);
              }
            });
        }

    //System.out.println("Main.main elapsed="+(System.currentTimeMillis() - start));
    }
}

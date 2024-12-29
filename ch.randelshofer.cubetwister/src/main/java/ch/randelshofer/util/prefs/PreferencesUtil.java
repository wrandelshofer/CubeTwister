/*
 * @(#)PreferencesUtil.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.util.prefs;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;
/**
 * PreferencesUtil.
 *
 * @author Werner Randelshofer
 */
public class PreferencesUtil {

    /** Creates a new instance. */
    private PreferencesUtil() {
    }

    /**
     * Installs a window preferences handler.
     * On first run, sets the window to its preferred size at the top left
     * corner of the screen.
     * On subsequent runs, sets the window the last size and location where
     * the user has placed it before.
     *
     * @param prefs  Preferences for storing/retrieving preferences values.
     * @param name   Base name of the preference.
     * @param window The window for which to track preferences.
     */
    public static void installPrefsHandler(@Nonnull final Preferences prefs, final String name, @Nonnull Window window) {
        GraphicsConfiguration conf = window.getGraphicsConfiguration();
        Rectangle screenBounds = conf.getBounds();
        Insets screenInsets = window.getToolkit().getScreenInsets(conf);

        screenBounds.x += screenInsets.left;
        screenBounds.y += screenInsets.top;
        screenBounds.width -= screenInsets.left + screenInsets.right;
        screenBounds.height -= screenInsets.top + screenInsets.bottom;

        Dimension preferredSize = window.getPreferredSize();

        Rectangle bounds = new Rectangle(
                prefs.getInt(name + ".x", 0),
                prefs.getInt(name + ".y", 0),
                prefs.getInt(name + ".width", preferredSize.width),
                prefs.getInt(name + ".height", preferredSize.height)
        );
        bounds.width = Math.min(Math.max(200, bounds.width), screenBounds.width);
        bounds.height = Math.min(Math.max(200, bounds.height), screenBounds.height);
        if (! screenBounds.contains(bounds)) {
            bounds.x = screenBounds.x + (screenBounds.width - bounds.width) / 2;
            bounds.y = screenBounds.y + (screenBounds.height - bounds.height) / 2;
            Rectangle.intersect(screenBounds, bounds, bounds);
        }
        window.setBounds(bounds);

        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(@Nonnull ComponentEvent evt) {
                prefs.putInt(name + ".x", evt.getComponent().getX());
                prefs.putInt(name + ".y", evt.getComponent().getY());
            }

            @Override
            public void componentResized(@Nonnull ComponentEvent evt) {
                prefs.putInt(name + ".width", evt.getComponent().getWidth());
                prefs.putInt(name + ".height", evt.getComponent().getHeight());
            }
        });

    }

    /**
     * Installs a JTabbedPane preferences handler.
     * On first run, sets the JTabbedPane to its preferred tab.
     *
     * @param prefs      Preferences for storing/retrieving preferences values.
     * @param name       Base name of the preference.
     * @param tabbedPane The JTabbedPane for which to track preferences.
     */
    public static void installPrefsHandler(@Nonnull final Preferences prefs, final String name, @Nonnull final JTabbedPane tabbedPane) {
        int selectedTab = prefs.getInt(name, 0);
        try {
            tabbedPane.setSelectedIndex(selectedTab);
        } catch (IndexOutOfBoundsException e) {

        }
        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                prefs.putInt(name, tabbedPane.getSelectedIndex());
            }

        });
    }
}

/*
 * @(#)Icons.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui;

import org.jhotdraw.annotation.Nonnull;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides constants for commonly used Icons.
 *
 * @author Werner Randelshofer
 */
public class Icons {
    // Player icons: size 10 x 10
    public final static int PLAYER_START = 0;
    public final static int PLAYER_STOP = 1;
    public final static int PLAYER_NEXT = 2;
    public final static int PLAYER_BACK = 3;
    public final static int PLAYER_START_RECORDING = 4;
    public final static int PLAYER_START_RECORDING_DISABLED = 5;
    public final static int PLAYER_STOP_RECORDING = 6;
    public final static int PLAYER_HOME = 7;
    public final static int PLAYER_RESET = 8;
    public final static int PLAYER_RESET_DISABLED = 9;
    public final static int PLAYER_PARTIAL_RESET = 10;
    public final static int PLAYER_SCRAMBLE = 11;
    public final static int PLAYER_SCRAMBLE_DISABLED = 12;
    public final static int PLAYER_ACTIONS = 13;
    public final static int PLAYER_ACTIONS_DISABLED = 14;

    public final static int SCRIPT_CHECK = 15;
    public final static int SCRIPT_CHECK_DISABLED = 16;
    public final static int ACTIONS_TABLE_HEADER = 17;
    // Placard icons: size 20 x 19
    public final static int ACTIONS_PLACARD = 18;
    public final static int PLAYER_RESET_PLACARD = 19;
    public final static int PLAYER_PARTIAL_RESET_PLACARD = 20;
    public final static int PLAYER_HOME_PLACARD = 21;
    public final static int EDIT_ADD_PLACARD = 22;

    @Nonnull
    private static Icon[] icons = new Icon[23];
    @Nonnull
    private static String[] names = {
            "PlayerStart.png",
            "PlayerStop.png",
            "PlayerNext.png",
            "PlayerBack.png",
            "PlayerStartRecording.png",
            "PlayerStartRecording.disabled.png",
            "PlayerStopRecording.png",
            "PlayerHome.png",
            "PlayerReset.png",
            "PlayerReset.disabled.png",
            "PlayerPartialReset.png",
            "PlayerScramble.png",
            "PlayerScramble.disabled.png",
            "PlayerActions.png",
            "PlayerActions.disabled.png",
            "-script check-",
            "-script check disabled-",
            "PlayerActions.png", // table header
            "Actions.placard.png",
            "PlayerReset.placard.png",
            "PlayerPartialReset.placard.png",
            "PlayerHome.placard.png",
            "EditAdd.placard.png",
    };

    /**
     * The reset icon consists of a rectangle (at west)
     * and a triangle pointing to the rectangle (towards west).
     */
    public static Icon get(int index) {
        if (index == SCRIPT_CHECK) {
            return getScriptCheckIcon();
        }
        if (index == SCRIPT_CHECK_DISABLED) {
            return getScriptCheckDisabledIcon();
        }
        List<String> searchedPaths = new ArrayList<>();
        if (icons[index] == null) {
            String path = "/org/monte/media/swing/player/images/" + names[index];
            searchedPaths.add(path);
            InputStream stream = null;
            try {
                stream = ModuleLayer.boot().findModule("org.monte.media.swing").get().getResourceAsStream(path);
            } catch (IOException e) {
                stream = null;
            }
            if (stream == null) {
                path = "/org/monte/media/swing/images/" + names[index];
                searchedPaths.add(path);
                try {
                    stream = ModuleLayer.boot().findModule("org.monte.media.swing").get().getResourceAsStream(path);
                } catch (IOException e) {
                    stream = null;
                }
            }
            if (stream == null) {
                throw new RuntimeException("Icons icon not found:" + searchedPaths);
            }
            icons[index] = new LazyImageIcon(
                    stream,
                    index < ACTIONS_PLACARD ? 10 : 20, index < ACTIONS_PLACARD ? 10 : 19,
                    index < ACTIONS_PLACARD ? -3 : 0, index < ACTIONS_PLACARD ? -3 : 0
            );
        }
        return icons[index];
    }

    private static Icon getScriptCheckIcon() {
        if (icons[SCRIPT_CHECK] == null) {
            icons[SCRIPT_CHECK] = new VectorIcon(
                    new Polygon(
                            new int[]{0, 1, 3, 8, 9, 4, 3},
                            new int[]{5, 5, 8, 0, 0, 9, 9},
                            7
                    ),
                    10, 10, Color.black, Color.black
            );
        }
        return icons[SCRIPT_CHECK];
    }

    private static Icon getScriptCheckDisabledIcon() {
        if (icons[SCRIPT_CHECK_DISABLED] == null) {
            icons[SCRIPT_CHECK_DISABLED] = new VectorIcon(
                    new Polygon(
                            new int[]{0, 1, 3, 8, 9, 4, 3},
                            new int[]{5, 5, 8, 0, 0, 9, 9},
                            7
                    ),
                    10, 10, new Color(128, 128, 128), new Color(128, 128, 128)
            );
        }
        return icons[SCRIPT_CHECK_DISABLED];
    }


    /**
     * Private to prevent creation of instances.
     */
    private Icons() {
    }

}

/*
 * @(#)Strings.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

/**
 * This class contains various methods for manipulating strings.
 *
 * @author Werner Randelshofer
 */
public class Strings extends Object {
    /**
     * Translates all occurences of characters in
     * String 'from' to the characters in String 'to'.
     * This is a convenience method for calling
     * <code>translate(s, from.toCharArray(), to.toCharArray())</code>
     *
     * @param s The string to be translated.
     * @param from The characters to be replaced.
     * @param to   The replacement characters.
     *
     *
     * @return The translated String.
     */
    @Nullable
    public static String translate(@Nullable String s, @Nonnull String from, @Nonnull String to) {
        if (s == null) {
            return null;
        }
        return translate(s, from.toCharArray(), to.toCharArray());
    }

    /**
     * Translates all occurences of characters in the
     * char array 'from' to the characters in 'to'.
     *
     * @param s    The string to be translated.
     * @param from The characters to be replaced.
     * @param to   The replacement characters.
     * @return The translated String.
     */
    @Nonnull
    public static String translate(@Nonnull String s, @Nonnull char[] from, char[] to) {
        char[] map = new char[256];

        int i;

        for (i = 0; i < 256; i++) {
            map[i] = (char) i;
        }

        for (i = 0; i < from.length; i++) {
            map[from[i]] = to[i];
        }


        char[] result = new char[s.length()];
        for (i=0; i < result.length; i++) {
            result[i] = map[s.charAt(i)];
        }

        return new String(result);
    }

    /**
     * Replaces all occurences of 'from' by 'to'.
     *
     * @param str The string to be processed.
     * @param from The text to be replaced.
     * @param to   The replacement text.
     *
     * @return The translated String.
     */
    @Nonnull
    public static String replace(@Nonnull String str, @Nonnull String from, String to) {
        int p0 = 0, p1 = 0;
        StringBuilder buf = new StringBuilder();

        while ((p1 = str.indexOf(from, p0)) != -1) {
            buf.append(str.substring(p0, p1));
            buf.append(to);
            p0 = p1 + from.length();
        }
        buf.append(str.substring(p0, str.length()));

        return buf.toString();
    }

    /**
     * Reverses the characters in a String.
     *
     * @param str The string to be reversed.
     *
     * @return The reversed String.
     */
    @Nonnull
    public static String reverse(@Nonnull String str) {
        char[] chars = new char[str.length()];
        for (int i = 0, n = str.length(); i < n; i++) {
            chars[i] = str.charAt(n - i - 1);
        }
        return new String(chars);
    }
}

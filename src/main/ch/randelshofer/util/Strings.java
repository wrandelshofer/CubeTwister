/*
 * @(#)Strings.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 */
package ch.randelshofer.util;

/**
 * This class contains various methods for manipulating strings.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * <br>1.2.1 2003-06-13 Method translate(String, char, char) removed.
 * <br>1.2 2002-05-10 Method replace(String, String, String) added.
 * <br>1.1 2002-02-05 Created.
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
    public static String translate(String s, String from, String to) {
        if (s == null) return null;
        return translate(s, from.toCharArray(), to.toCharArray());
    }

    /**
     * Translates all occurences of characters in the
     * char array 'from' to the characters in 'to'.
     *
     * @param s The string to be translated.
     * @param from The characters to be replaced.
     * @param to   The replacement characters.
     *
     * @return The translated String.
     */
    public static String translate(String s, char[] from, char[] to) {
        char[] map = new char[256];

        int i;

        for (i=0; i < 256; i++) {
            map[i] = (char) i;
        }

        for (i=0; i < from.length; i++) {
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
    public static String replace(String str, String from, String to) {
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
    public static String reverse(String str) {
        char[] chars = new char[str.length()];
        for (int i=0, n = str.length(); i < n; i++) {
            chars[i] = str.charAt(n - i - 1);
        }
        return new String(chars);
    }
}

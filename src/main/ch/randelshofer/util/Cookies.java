/*
 * @(#)Cookies.java  1.0  2009-04-11
 *
 * Copyright (c) 2009 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.util;

import java.applet.Applet;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import netscape.javascript.JSObject;

/**
 * Usefull methods for cookies.
 * <p>
 * This class uses JSObject to access cookies of the browser through the
 * Java-to-Javascript communication.
 * This way the applet does not need to be signed. The applet tag must include a
 * MAYSCRIPT attribute though.
 * <p>
 * For more information about JSObjbect see.
 * http://java.sun.com/javase/6/docs/technotes/guides/plugin/developer_guide/java_js.html#jsobject
 *
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-04-11 Created.
 */
public class Cookies extends Object {
    /**
     * If you set this to true, Cookies will write diagnostic output to
     * System.out and System.err.
     */
    private static boolean debug = false;

    /**
     * This DateFormat object is used to format the elapsed date of a cookie.
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy hh:mm:ss z", Locale.ENGLISH);

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private Cookies() {
    }

    /**
     * Gets the raw cookie string from the HTML document which contains the
     * applet.
     *
     * @param applet
     * @return All cookies as key, value pairs.
     */
    public static String getEncodedCookie(Applet applet) {
        try {
            String encodedCookie = (String) JSObject.getWindow(applet).eval("document.cookie");
            return encodedCookie;
        } catch (Throwable e) {
            if (debug) {
                System.err.println("Cookies.getEncodedCookie() Warning: This browser does not have an API for reading cookies.");
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Gets the specified cookie.
     *
     * @param applet the applet
     * @param name the name of the cookie
     * @param defaultValue the default value is returned if the cookie does not exist.
     *
     * @return The value of the specified cookie or the default value.
     */
    public static String getCookie(Applet applet, String name, String defaultValue) {
        String encodedCookie = getEncodedCookie(applet);
        for (String str : encodedCookie.split(";")) {

            String[] parts = str.split("=", 2);
            try {
                if (URLDecoder.decode(parts[0].trim(), "UTF-8").equals(name)) {
                    return URLDecoder.decode(parts[1].trim(), "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                if (debug) {
                    System.err.println("Cookies.getCookie() Warning: Couldn't decode cookie:" + str);
                    e.printStackTrace();
                }
            }
        }

        return defaultValue;
    }

    /**
     * Puts the specified cookie value.
     *
     * @param applet the applet
     * @param name the name of the cookie
     * @param value the value of the cooke
     * @param expires the expiration date, specify null, for a session cookie.
     */
    public static void putCookie(Applet applet, String name, String value, Date expires) {
        try {
            String encodedCookie = URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            if (expires != null) {
                encodedCookie += "; expires=" + dateFormat.format(expires);
            }
            JSObject.getWindow(applet).eval("document.cookie ='" + encodedCookie + "';");

        } catch (Throwable e) {
            if (debug) {
                System.err.println("Cookies.putCookie() Warning: This browser does not allow to write cookies.");
                e.printStackTrace();
            }
        }
    }
}

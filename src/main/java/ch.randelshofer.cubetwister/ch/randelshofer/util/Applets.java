/*
 * @(#)Applets.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.applet.Applet;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Usefull methods for applets.
 *
 * @author Werner Randelshofer
 */
public class Applets extends Object {

    private static boolean debug = false;
    /** Fallback parameters */
    private static HashMap<String, String> fallback;

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private Applets() {
    }

    public static void setDebug(boolean newValue) {
        debug = newValue;
    }

    /**
     * Sets command line arguments. These arguments are used as fallback,
     * when the applet is run as an application.
     *
     * @param args
     */
    public static void setMainArgs(@Nonnull Applet applet, @Nonnull String[] args) {
        fallback = new HashMap<String, String>();

        HashMap<String, String[]> info = new HashMap<String, String[]>();
        for (String[] pinf : applet.getParameterInfo()) {
            info.put(pinf[0], pinf);
        }

        boolean helpNeeded = false;

        for (int i = 0; i < args.length; i++) {
            if ("-help".equals(args[i])) {
                helpNeeded=true;
                continue;
            }
            if (i<args.length-1 && args[i].startsWith("-")) {
                if (info.containsKey(args[i].substring(1))) {
                    fallback.put(args[i].substring(1), args[i + 1]);
                    i++;
                }
            } else {
                System.err.println("Illegal parameter: "+args[i]);
            }
        }

        if (helpNeeded) {
                 System.out.println(applet.getAppletInfo());
                System.out.println("\nSupported Parameters:");
                for (String[] pinf : applet.getParameterInfo()) {
                    System.out.println(pinf[0]+"\t"+pinf[1]+"\t"+pinf[2]);
                }
       }
    }
    //----------------NEW
    /**
     * HTML 4.1 standard color names.
     * @see http://www.w3.org/TR/html4/types.html#h-6.5
     */
    private final static HashMap<String,Color> htmlColors = new HashMap<String,Color>();

    static {
        htmlColors.put("black", new Color(0x000000));
        htmlColors.put("green", new Color(0x008000));
        htmlColors.put("silver", new Color(0xC0C0C0));
        htmlColors.put("lime", new Color(0x00FF00));
        htmlColors.put("gray", new Color(0x808080));
        htmlColors.put("olive", new Color(0x808000));
        htmlColors.put("white", new Color(0xFFFFFF));
        htmlColors.put("yellow", new Color(0xFFFF00));
        htmlColors.put("maroon", new Color(0x800000));
        htmlColors.put("navy", new Color(0x000080));
        htmlColors.put("red", new Color(0xFF0000));
        htmlColors.put("blue", new Color(0x0000FF));
        htmlColors.put("purple", new Color(0x800080));
        htmlColors.put("teal", new Color(0x008080));
        htmlColors.put("fuchsia", new Color(0xFF00FF));
        htmlColors.put("aqua", new Color(0x00FFFF));
    }
    /**
     * Java standard color names.
     * @see java.awt.Color
     */
    private final static HashMap<String,Color> javaColors = new HashMap<String,Color>();

    static {
        javaColors.put("white", Color.white);
        javaColors.put("lightgray", Color.lightGray);
        javaColors.put("gray", Color.gray);
        javaColors.put("darkgray", Color.darkGray);
        javaColors.put("black", Color.black);
        javaColors.put("red", Color.red);
        javaColors.put("pink", Color.pink);
        javaColors.put("orange", Color.orange);
        javaColors.put("yellow", Color.yellow);
        javaColors.put("green", Color.green);
        javaColors.put("magenta", Color.magenta);
        javaColors.put("cyan", Color.cyan);
        javaColors.put("blue", Color.blue);
    }

    /**
     * Reads a parameter as a color value.
     * <p>
     * The color can be represented in the following formats:
     * <ul>
     * <li>any Color from java.awt.Color;
     *
     * r,g,b where r, g, and b are integers in the range of 0-255 that would render an opaque standard RGB (sRGB) color in the Color constructor Color(int r, int g, int b);
     *
     * standard HTML colors: silver, green, maroon, purple, navy, teal, and olive; or
     *
     * hexadecimal color forma</li>
     * <li></li>
     * <li></li>
     * <li></li>
     * </ul>
     *
     * @param applet The applet.
     * @param name   The name of the parameter.
     * @param defaultValue The default value.
     *
     * @return Returns the value of the parameter, or the default value, if
     * the parameter does not exist, or does not represent a double.
     */
    public static Color getParameter(@Nonnull Applet applet, String name, Color defaultValue) {
        String value = getParameter(applet, name);
        if (value != null) {
            value = value.toLowerCase();
            if (htmlColors.containsKey(value)) {
                return htmlColors.get(value);
            }
            if (javaColors.containsKey(value)) {
                return javaColors.get(value);
            }
            int[] ints = getParameters(applet, name, new int[0]);
            switch (ints.length) {
                case 1:
                    return new Color(ints[0]);
                case 3:
                    return new Color(ints[0], ints[1], ints[2]);
            }
        }
        return defaultValue;
    }

    /**
     * Returns the String value of an applet parameter or the
     * default value if the parameter is null.
     *
     * @param applet       The applet from which we want to get the parameter.
     * @param name         The name of the parameter.
     * @param defaultValue This value is returned when the applet returns
     *                     null for the parameter.
     */
    @Nullable
    public static String getParameter(@Nonnull Applet applet, String name, String defaultValue) {
        String value = getParameter(applet, name);
        value = value != null ? value : defaultValue;
        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + value);
        }
        return value;
    }

    /**
     * Returns the parameter value for the specified applet or the
     * default value if the parameter is null, the value is
     * returned as an array. The values of the array consist
     * of each word found in the value. The words are delimited
     * by spaces or by commas.
     *
     * @param   applet  The applet from which we want to get the parameter.
     * @param   name    The name of the parameter.
     * @param   defaultValue This value is returned when the applet returns
     *                  null for the parameter.
     */
    @Nullable
    public static String[] getParameters(@Nonnull Applet applet, String name, @Nullable String[] defaultValue) {
        String param = getParameter(applet, name);
        if (param != null) {
            StringTokenizer scanner = new StringTokenizer(param, ", ");
            String[] values = new String[scanner.countTokens()];
            for (int i = 0; i < values.length; i++) {
                values[i] = scanner.nextToken();
            }
            if (debug) {
                System.out.println("Applets.getParameter(" + name + "):" + Arrays.asList(values));
            }
            return values;
        }
        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + ((defaultValue == null) ? null : Arrays.asList(defaultValue)));
        }
        return defaultValue;
    }

    /**
     * Returns the parameter value for the specified applet or the
     * command line parameter (main args) when the applet is run as an application.
     *
     * @param applet The applet from which we want to get the parameter.
     * @param name   The name of the parameter.
     */
    @Nullable
    public static String getParameter(@Nonnull Applet applet, String name) {
        try {
            return applet.getParameter(name);
        } catch (NullPointerException e) {
            if (fallback != null) {
                return fallback.get(name);
            }
            return null;
        }
    }

    /**
     * Reads a parameter as an int value.
     *
     * @param applet       The applet.
     * @param name         The name of the parameter.
     * @param defaultValue The default value.
     * @return Returns the value of the parameter, or the default value, if
     * the parameter does not exist, or does not represent an int.
     */
    public static int getParameter(@Nonnull Applet applet, String name, int defaultValue) {
        String value = getParameter(applet, name);
        if (value != null) {
            try {
                //return Integer.parseInt(value);
                int intValue = decode(value).intValue();
                if (debug) {
                    System.out.println("Applets.getParameter(" + name + "):" + intValue);
                }
                return intValue;
            } catch (NumberFormatException e) {
            }
        }

        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Reads a parameter as a boolean value.
     *
     * @param applet       The applet.
     * @param name         The name of the parameter.
     * @param defaultValue The default value.
     * @return Returns the value of the parameter, or the default value, if
     * the parameter does not exist, or does not represent a boolean.
     */
    public static boolean getParameter(@Nonnull Applet applet, String name, boolean defaultValue) {
        String value = getParameter(applet, name);
        if (value != null) {
            boolean booleanValue = Boolean.valueOf(value).booleanValue();
            if (debug) {
                System.out.println("Applets.getParameter(" + name + "):" + booleanValue);
            }
            return booleanValue;
        }
        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Reads a parameter as an array of comma separated int values.
     *
     * @param applet       The applet.
     * @param name         The name of the parameter.
     * @param defaultValue The default value.
     * @return Returns the value of the parameter, or the default value, if
     * the parameter does not exist, or does not represent an array of int's.
     */
    @Nullable
    public static int[] getParameters(@Nonnull Applet applet, String name, @Nullable int[] defaultValue) {
        String value = getParameter(applet, name);
        if (value != null) {
            try {
                StringTokenizer scanner = new StringTokenizer(value, ", ");
                int[] values = new int[scanner.countTokens()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = decode(scanner.nextToken()).intValue();
                }
                if (debug) {
                    System.out.println("Applets.getParameter(" + name + "):" + ArrayUtil.asList(values));
                }
                return values;
            } catch (NumberFormatException e) {
            }
        }
        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + ((defaultValue == null) ? null : ArrayUtil.asList(defaultValue)));
        }
        return defaultValue;
    }

    /**
     * Reads a parameter as a double value.
     *
     * @param applet       The applet.
     * @param name         The name of the parameter.
     * @param defaultValue The default value.
     * @return Returns the value of the parameter, or the default value, if
     * the parameter does not exist, or does not represent a double.
     */
    public static double getParameter(@Nonnull Applet applet, String name, double defaultValue) {
        String value = getParameter(applet, name);
        if (value != null) {
            try {
                double doubleValue = Double.valueOf(value).doubleValue();
                if (debug) {
                    System.out.println("Applets.getParameter(" + name + "):" + doubleValue);
                }
                return doubleValue;
            } catch (NumberFormatException e) {
            }
        }

        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Returns the parameter value of the specified applet or the
     * default value if the parameter is null, the value is
     * returned as a Hashtable.
     * <p>
     * The parameter value is parsed according to the following EBNF production:
     * <pre>
     * list ::= entry {"," entry}
     * entry ::= [key "="] value
     * key ::= String
     * value ::= String
     * </pre>
     * <p>
     * The returned Hashtable contains up to two key value pairs for each entry.
     * <br>If an entry does specify a key, it is put using the specified key into
     * the hashtable.
     * <br>For each entry a key is generated which denotes the index of the entry.
     * If the Hashtable does not contain an entry for the generated key. Then an
     * entry is put using the generated key as well.
     *
     * @param   applet  The applet from which we want to get the parameter.
     * @param   name    The name of the parameter.
     * @param   defaultValue This value is returned when the applet returns
     *                  null for the parameter.
     * @return A hashtable containing a key value pair for each entry.
     */
    public static HashMap<String, String> getIndexedKeyValueParameters(@Nonnull Applet applet, String name, HashMap<String, String> defaultValue) {

        String entry, key, value, generatedKey;
        int pos;
        String param = getParameter(applet, name);
        if (param != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            StringTokenizer scanner = new StringTokenizer(param, ", ");
            int count = scanner.countTokens();
            for (int i = 0; i < count; i++) {
                entry = scanner.nextToken();
                pos = entry.indexOf('=');
                if (pos < 1) {
                    key = null;
                    value = entry;
                } else {
                    key = entry.substring(0, pos);
                    value = entry.substring(pos + 1);
                }
                generatedKey = Integer.toString(i);
                if (key != null) {
                    map.put(key, value);
                }
                if (!map.containsKey(generatedKey)) {
                    map.put(generatedKey, value);
                }
            }
            if (debug) {
                System.out.println("Applets.getParameter(" + name + "):" + map);
            }
            return map;
        }
        if (debug) {
            System.out.println("Applets.getParameter(" + name + "):" + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Decodes a <code>String</code> into an <code>Integer</code>.  Accepts
     * decimal, hexadecimal, and octal numbers, in the following formats:
     * <pre>
     *     [-]    &lt;decimal constant&gt;
     *     [-] 0x     &lt;hex constant&gt;
     *     [-] #      &lt;hex constant&gt;
     *     [-] 0    &lt;octal constant&gt;
     * </pre>
     *
     * The constant following an (optional) negative sign and/or "radix
     * specifier" is parsed as by the <code>Integer.parseInt</code> method
     * with the specified radix (10, 8 or 16).  This constant must be positive
     * or a NumberFormatException will result.  The result is made negative if
     * first character of the specified <code>String</code> is the negative
     * sign.  No whitespace characters are permitted in the
     * <code>String</code>.
     *
     * @param     nm the <code>String</code> to decode.
     * @return the <code>Integer</code> represented by the specified string.
     * @exception NumberFormatException  if the <code>String</code> does not
     *            contain a parsable integer.
     * @see java.lang.Integer#parseInt(String, int)
     */
    public static Integer decode(@Nonnull String nm) throws NumberFormatException {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        Integer result;

        // Handle minus sign, if present
        if (nm.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present
        if (nm.startsWith("0x", index) || nm.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (nm.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (nm.startsWith("0", index) && nm.length() > 1 + index) {
            index++;
            radix = 8;
        }

        if (nm.startsWith("-", index)) {
            throw new NumberFormatException("Negative sign in wrong position");
        }
        try {
            result = Integer.valueOf(nm.substring(index), radix);
            result = negative ? new Integer(-result.intValue()) : result;
        } catch (NumberFormatException e) {
            // If number is Integer.MIN_VALUE, we'll end up here. The next line
            // handles this case, and causes any genuine format error to be
            // rethrown.
            String constant = negative ? new String("-" + nm.substring(index))
                    : nm.substring(index);
            result = Integer.valueOf(constant, radix);
        }
        return result;
    }
}

/*
 * @(#)AppletParameterException.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

/**
 * AppletParameterException.
 *
 * @author Werner Randelshofer
 */
public class AppletParameterException extends Exception {
    private final static long serialVersionUID = 1L;
    /**
     * The name of the Applet parameter.
     */
    private String name;
    /**
     * The value of the Applet parameter.
     */
    private String value;
    /**
     * The start position of the invalid part of the value.
     */
    private int start;
    /**
     * The end position of the invalid part of the value plus 1.
     */
    private int end;

    public AppletParameterException(String name, @Nullable String value) {
        this(name, value, 0, value == null ? 0 : value.length());
    }

    public AppletParameterException(String message, String name, @Nullable String value) {
        this(message, name, value, 0, value == null ? 0 : value.length());
    }

    public AppletParameterException(String name, @Nullable String value, Throwable cause) {
        this(name, value, 0, value == null ? 0 : value.length(), cause);
    }

    public AppletParameterException(String name, String value, int start, int end) {
        this("The Applet parameter \"" + name + "\" has an illegal value.",
                name, value, start, end);
    }

    public AppletParameterException(String message, String name, String value, int start, int end) {
        super(message);
        this.name = name;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public AppletParameterException(String name, @Nullable String value, int start, int end, @Nullable Throwable cause) {
        this("The Applet parameter \"" + name + "\" has an illegal value." + (cause == null ? "" : " " + cause.getMessage()),
                name, value, 0, value == null ? 0 : value.length(), cause);
    }

    public AppletParameterException(String message, String name, String value, int start, int end, Throwable cause) {
        super(message, cause);
        this.name = name;
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getMessageText() {
        return super.getMessage();
    }

    @Nonnull
    @Override
    public String getMessage() {
        if (value==null || start == 0 && end == value.length()) {
            return super.getMessage() + " Value=\"" +
                    value +
                    "\"";
        } else {
            return super.getMessage() + " Value=\"" +
                    value.substring(0, start) + "[" +
                    value.substring(start, end+1) + "]" +
                    ((end < value.length()) ? value.substring(end+1) : "")+
                    "\"";
        }
    }
}

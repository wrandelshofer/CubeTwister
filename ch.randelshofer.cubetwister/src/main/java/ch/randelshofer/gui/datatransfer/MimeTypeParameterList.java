/*
 * @(#)MimeTypeParameterList.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.gui.datatransfer;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An object that encapsualtes the parameter list of a MimeType as defined in
 * RFC 2045 and 2046.
 * <p>
 * Implementation taken from java.awt.datatransfer.MimeTypeParameterList.java
 * 1.12 01/12/03
 *
 * @author Werner Randelshofer
 */
class MimeTypeParameterList implements Cloneable {

    private HashMap<String, String> parameters;

    /**
     * Default constructor.
     */
    public MimeTypeParameterList() {
        parameters = new HashMap<String, String>();
    }

    public MimeTypeParameterList(@Nonnull String rawdata)
            throws MimeTypeParseException {
        parameters = new HashMap<String, String>();

        //    now parse rawdata
        parse(rawdata);
    }

    @Override
    public int hashCode() {
        int code = Integer.MAX_VALUE / 45; // "random" value for empty lists
        for (Map.Entry<String,String> entry :parameters.entrySet()) {

            code += entry.getKey().hashCode();
            code += entry.getValue().hashCode();
        }

        return code;
    } // hashCode()

    /**
     * Two parameter lists are considered equal if they have exactly the same
     * set of parameter names and associated values. The order of the parameters
     * is not considered.
     */
    public boolean equals(Object thatObject) {
        //System.out.println("MimeTypeParameterList.equals("+this+","+thatObject+")");
        if (!(thatObject instanceof MimeTypeParameterList)) {
            return false;
        }
        MimeTypeParameterList that = (MimeTypeParameterList) thatObject;
        if (this.size() != that.size()) {
            return false;
        }
        String name = null;
        String thisValue = null;
        String thatValue = null;
        Set<Map.Entry<String, String>> entries = parameters.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        Map.Entry<String, String> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();
            name = entry.getKey();
            thisValue = entry.getValue();
            thatValue = that.parameters.get(name);
            if ((thisValue == null) || (thatValue == null)) {
                // both null -> equal, only one null -> not equal
                if (thisValue != thatValue) {
                    return false;
                }
            } else if (!thisValue.equals(thatValue)) {
                return false;
            }
        } // while iterator

        return true;
    } // equals()

    /**
     * A routine for parsing the parameter list out of a String.
     */
    protected void parse(@Nonnull String rawdata) throws MimeTypeParseException {
        int length = rawdata.length();
        if (length > 0) {
            int currentIndex = skipWhiteSpace(rawdata, 0);
            int lastIndex = 0;

            if (currentIndex < length) {
                char currentChar = rawdata.charAt(currentIndex);
                while ((currentIndex < length) && (currentChar == ';')) {
                    String name;
                    String value;
                    boolean foundit;

                    //    eat the ';'
                    ++currentIndex;

                    //    now parse the parameter name
                    //    skip whitespace
                    currentIndex = skipWhiteSpace(rawdata, currentIndex);

                    if (currentIndex < length) {
                        //    find the end of the token char run
                        lastIndex = currentIndex;
                        currentChar = rawdata.charAt(currentIndex);
                        while ((currentIndex < length) && isTokenChar(currentChar)) {
                            ++currentIndex;
                            currentChar = rawdata.charAt(currentIndex);
                        }
                        name = rawdata.substring(lastIndex, currentIndex).toLowerCase();

                        //    now parse the '=' that separates the name from the value
                        //    skip whitespace
                        currentIndex = skipWhiteSpace(rawdata, currentIndex);

                        if ((currentIndex < length) && (rawdata.charAt(currentIndex) == '=')) {
                            //    eat it and parse the parameter value
                            ++currentIndex;

                            //    skip whitespace
                            currentIndex = skipWhiteSpace(rawdata, currentIndex);

                            if (currentIndex < length) {
                                //    now find out whether or not we have a quoted value
                                currentChar = rawdata.charAt(currentIndex);
                                if (currentChar == '"') {
                                    //    yup it's quoted so eat it and capture the quoted string
                                    ++currentIndex;
                                    lastIndex = currentIndex;

                                    if (currentIndex < length) {
                                        //    find the next unescqped quote
                                        foundit = false;
                                        while ((currentIndex < length) && !foundit) {
                                            currentChar = rawdata.charAt(currentIndex);
                                            if (currentChar == '\\') {
                                                //    found an escape sequence so pass this and the next character
                                                currentIndex += 2;
                                            } else if (currentChar == '"') {
                                                //    foundit!
                                                foundit = true;
                                            } else {
                                                ++currentIndex;
                                            }
                                        }
                                        if (currentChar == '"') {
                                            value = unquote(rawdata.substring(lastIndex, currentIndex));
                                            //    eat the quote
                                            ++currentIndex;
                                        } else {
                                            throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                                        }
                                    } else {
                                        throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                                    }
                                } else if (isTokenChar(currentChar)) {
                                    //    nope it's an ordinary token so it ends with a non-token char
                                    lastIndex = currentIndex;
                                    foundit = false;
                                    while ((currentIndex < length) && !foundit) {
                                        currentChar = rawdata.charAt(currentIndex);

                                        if (isTokenChar(currentChar)) {
                                            ++currentIndex;
                                        } else {
                                            foundit = true;
                                        }
                                    }
                                    value = rawdata.substring(lastIndex, currentIndex);
                                } else {
                                    //    it ain't a value
                                    throw new MimeTypeParseException("Unexpected character encountered at index " + currentIndex);
                                }

                                //    now put the data into the hashtable
                                parameters.put(name, value);
                            } else {
                                throw new MimeTypeParseException("Couldn't find a value for parameter named " + name);
                            }
                        } else {
                            throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
                        }
                    } else {
                        throw new MimeTypeParseException("Couldn't find parameter name");
                    }

                    //    setup the next iteration
                    currentIndex = skipWhiteSpace(rawdata, currentIndex);
                    if (currentIndex < length) {
                        currentChar = rawdata.charAt(currentIndex);
                    }
                }
                if (currentIndex < length) {
                    throw new MimeTypeParseException("More characters encountered in input than expected.");
                }
            }
        }
    }

    /**
     * return the number of name-value pairs in this list.
     */
    public int size() {
        return parameters.size();
    }

    /**
     * Determine whether or not this list is empty.
     */
    public boolean isEmpty() {
        return parameters.isEmpty();
    }

    /**
     * Retrieve the value associated with the given name, or null if there is no
     * current association.
     */
    public String get(@Nonnull String name) {
        return parameters.get(name.trim().toLowerCase());
    }

    /**
     * Set the value to be associated with the given name, replacing any
     * previous association.
     */
    public void set(@Nonnull String name, String value) {
        parameters.put(name.trim().toLowerCase(), value);
    }

    /**
     * Remove any value associated with the given name.
     */
    public void remove(@Nonnull String name) {
        parameters.remove(name.trim().toLowerCase());
    }

    /**
     * Retrieve an enumeration of all the names in this list.
     */
    @Nonnull
    public Iterable<String> getNames() {
        return parameters.keySet();
    }

    @Nonnull
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.ensureCapacity(parameters.size() * 16);    //    heuristic: 8 characters per field

        for (Map.Entry<String,String> entry : parameters.entrySet()) {
            buffer.append("; ");
            buffer.append(entry.getKey());
            buffer.append('=');
            buffer.append(quote(entry.getValue()));
        }

        return buffer.toString();
    }

    /**
     * @return a clone of this object
     */
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public MimeTypeParameterList clone() {
        MimeTypeParameterList that = null;
        try {
            that = (MimeTypeParameterList) super.clone();
        } catch (CloneNotSupportedException cannotHappen) {
        }
        that.parameters = (HashMap<String,String>)parameters.clone();
        return that;
    }

    //    below here be scary parsing related things
    /**
     * Determine whether or not a given character belongs to a legal token.
     */
    private static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    /**
     * return the index of the first non white space character in rawdata at or
     * after index i.
     */
    private static int skipWhiteSpace(@Nonnull String rawdata, int i) {
        int length = rawdata.length();
        if (i < length) {
            char c = rawdata.charAt(i);
            while ((i < length) && Character.isWhitespace(c)) {
                ++i;
                c = rawdata.charAt(i);
            }
        }

        return i;
    }

    /**
     * A routine that knows how and when to quote and escape the given value.
     */
    @Nonnull
    private static String quote(@Nonnull String value) {
        boolean needsQuotes = false;

        //    check to see if we actually have to quote this thing
        int length = value.length();
        for (int i = 0; (i < length) && !needsQuotes; ++i) {
            needsQuotes = !isTokenChar(value.charAt(i));
        }

        if (needsQuotes) {
            StringBuilder buffer = new StringBuilder();
            buffer.ensureCapacity((int) (length * 1.5));

            //    add the initial quote
            buffer.append('"');

            //    add the properly escaped text
            for (int i = 0; i < length; ++i) {
                char c = value.charAt(i);
                if ((c == '\\') || (c == '"')) {
                    buffer.append('\\');
                }
                buffer.append(c);
            }

            //    add the closing quote
            buffer.append('"');

            return buffer.toString();
        } else {
            return value;
        }
    }

    /**
     * A routine that knows how to strip the quotes and escape sequences from
     * the given value.
     */
    @Nonnull
    private static String unquote(@Nonnull String value) {
        int valueLength = value.length();
        StringBuilder buffer = new StringBuilder();
        buffer.ensureCapacity(valueLength);

        boolean escaped = false;
        for (int i = 0; i < valueLength; ++i) {
            char currentChar = value.charAt(i);
            if (!escaped && (currentChar != '\\')) {
                buffer.append(currentChar);
            } else if (escaped) {
                buffer.append(currentChar);
                escaped = false;
            } else {
                escaped = true;
            }
        }

        return buffer.toString();
    }

    /**
     * A string that holds all the special chars.
     */
    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";

}

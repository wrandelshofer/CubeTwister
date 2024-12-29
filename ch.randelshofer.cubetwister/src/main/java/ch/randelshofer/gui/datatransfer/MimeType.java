/*
 * @(#)MimeType.java
 * CubeTwister. Copyright © 2020 Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.gui.datatransfer;

import org.jhotdraw.annotation.Nonnull;
import org.jhotdraw.annotation.Nullable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
/**
 * A Multipurpose Internet Mail Extension (MIME) type, as defined
 * in RFC 2045 and 2046.
 * <p>
 * Implementation taken from java.awt.datatransfer.MimeType 1.20 01/12/03
 *
 *
 * @author  Werner Randelshofer
 */
public class MimeType implements Externalizable, Cloneable {

    /*
     * serialization support
     */
    static final long serialVersionUID = -6568722458793895906L;

    /**
     * Constructor for externalization; this constructor should not be
     * called directly by an application, since the result will be an
     * uninitialized, immutable <code>MimeType</code> object.
     */
    public MimeType() {
    }

    /**
     * Builds a <code>MimeType</code> from a <code>String</code>.
     *
     * @param rawdata text used to initialize the <code>MimeType</code>
     */
    public MimeType(@Nonnull String rawdata) throws MimeTypeParseException {
        parse(rawdata);
    }

    /**
     * Builds a <code>MimeType</code> with the given primary and sub
     * type but has an empty parameter list.
     *
     * @param primary the primary type of this <code>MimeType</code>
     * @param sub     the subtype of this <code>MimeType</code>
     */
    public MimeType(@Nonnull String primary, @Nonnull String sub) throws MimeTypeParseException {
        this(primary, sub, new MimeTypeParameterList());
    }

    /**
     * Builds a <code>MimeType</code> with a pre-defined
     * and valid (or empty) parameter list.
     *
     * @param primary the primary type of this <code>MimeType</code>
     * @param sub     the subtype of this <code>MimeType</code>
     * @param mtpl    the requested parameter list
     */
    public MimeType(@Nonnull String primary, @Nonnull String sub, @Nonnull MimeTypeParameterList mtpl) throws
            MimeTypeParseException {
        //    check to see if primary is valid
        if (isValidToken(primary)) {
            primaryType = primary.toLowerCase();
        } else {
            throw new MimeTypeParseException("Primary type is invalid.");
        }

        //    check to see if sub is valid
        if (isValidToken(sub)) {
            subType = sub.toLowerCase();
        } else {
            throw new MimeTypeParseException("Sub type is invalid.");
        }

        parameters = mtpl.clone();
    }

    public int hashCode() {

        // We sum up the hash codes for all of the strings. This
        // way, the order of the strings is irrelevant
        int code = 0;
        code += primaryType.hashCode();
        code += subType.hashCode();
        code += parameters.hashCode();
        return code;
    } // hashCode()

    /**
     * <code>MimeType</code>s are equal if their primary types,
     * subtypes, and  parameters are all equal. No default values
     * are taken into account.
     * @param thatObject the object to be evaluated as a
     *    <code>MimeType</code>
     * @return <code>true</code> if <code>thatObject</code> is
     *    a <code>MimeType</code>; otherwise returns <code>false</code>
     */
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof MimeType)) {
            return false;
        }
        MimeType that = (MimeType)thatObject;
        boolean isIt =
        ((this.primaryType.equals(that.primaryType)) &&
        (this.subType.equals(that.subType)) &&
        (this.parameters.equals(that.parameters)));
        return isIt;
    } // equals()

    /**
     * A routine for parsing the MIME type out of a String.
     */
    private void parse(@Nonnull String rawdata) throws MimeTypeParseException {
        //System.out.println("MimeType.parse("+rawdata+")");
        int slashIndex = rawdata.indexOf('/');
        int semIndex = rawdata.indexOf(';');
        if ((slashIndex < 0) && (semIndex < 0)) {
            //    neither character is present, so treat it
            //    as an error
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else if ((slashIndex < 0) && (semIndex >= 0)) {
            //    we have a ';' (and therefore a parameter list),
            //    but no '/' indicating a sub type is present
            throw new MimeTypeParseException("Unable to find a sub type.");
        } else if((slashIndex >= 0) && (semIndex < 0)) {
            //    we have a primary and sub type but no parameter list
            primaryType = rawdata.substring(0,
            slashIndex).trim().toLowerCase();
            subType = rawdata.substring(slashIndex +
            1).trim().toLowerCase();
            parameters = new MimeTypeParameterList();
        } else if (slashIndex < semIndex) {
            //    we have all three items in the proper sequence
            primaryType = rawdata.substring(0,
            slashIndex).trim().toLowerCase();
            subType = rawdata.substring(slashIndex + 1,
            semIndex).trim().toLowerCase();
            parameters = new
            MimeTypeParameterList(rawdata.substring(semIndex));
        } else {
            //    we have a ';' lexically before a '/' which means we have a primary type
            //    & a parameter list but no sub type
            throw new MimeTypeParseException("Unable to find a sub type.");
        }

        //    now validate the primary and sub types

        //    check to see if primary is valid
        if(!isValidToken(primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }

        //    check to see if sub is valid
        if(!isValidToken(subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }

    /**
     * Retrieve the primary type of this object.
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Retrieve the sub type of this object.
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Retrieve a copy of this object's parameter list.
     */
    public MimeTypeParameterList getParameters() {
        return parameters.clone();
    }

    /**
     * Retrieve the value associated with the given name, or null if there
     * is no current association.
     */
    public String getParameter(@Nonnull String name) {
        return parameters.get(name);
    }

    /**
     * Set the value to be associated with the given name, replacing
     * any previous association.
     *
     * @throws IllegalArgumentException if parameter or value is illegal
     */
    public void setParameter(@Nonnull String name, String value) {
        parameters.set(name, value);
    }

    /**
     * Remove any value associated with the given name.
     *
     * @throws IllegalArgumentException if parameter may not be deleted
     */
    public void removeParameter(@Nonnull String name) {
        parameters.remove(name);
    }

    /**
     * Return the String representation of this object.
     */
    @Nonnull
    public String toString() {
        return getBaseType() + parameters.toString();
    }

    /**
     * Return a String representation of this object
     * without the parameter list.
     */
    @Nonnull
    public String getBaseType() {
        return primaryType + "/" + subType;
    }

    /**
     * Returns <code>true</code> if the primary type and the
     * subtype of this object are the same as the specified
     * <code>type</code>; otherwise returns <code>false</code>.
     *
     * @param type the type to compare to <code>this</code>'s type
     * @return <code>true</code> if the primary type and the
     * subtype of this object are the same as the
     * specified <code>type</code>; otherwise returns
     * <code>false</code>
     */
    public boolean match(@Nullable MimeType type) {
        if (type == null) {
            return false;
        }
        return primaryType.equals(type.getPrimaryType())
                && ("*".equals(subType)
                || "*".equals(type.getSubType())
                || (subType.equals(type.getSubType())));
    }

    /**
     * Returns <code>true</code> if the primary type and the
     * subtype of this object are the same as the content type
     * described in <code>rawdata</code>; otherwise returns
     * <code>false</code>.
     *
     * @param rawdata the raw data to be examined
     * @return <code>true</code> if the primary type and the
     *    subtype of this object are the same as the content type
     *    described in <code>rawdata</code>; otherwise returns
     *    <code>false</code>; if <code>rawdata</code> is
     *    <code>null</code>, returns <code>false</code>
     */
    public boolean match(@Nullable String rawdata) throws MimeTypeParseException {
        if (rawdata == null) {
            return false;
        }
        return match(new MimeType(rawdata));
    }

    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings
     * and arrays.
     *
     * @throws IOException Includes any I/O exceptions that may occur
     */
    public void writeExternal(@Nonnull ObjectOutput out) throws IOException {
        out.writeUTF(toString());
    }

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     *
     * @throws ClassNotFoundException If the class for an object being
     *                                restored cannot be found.
     */
    public void readExternal(@Nonnull ObjectInput in) throws IOException,
            ClassNotFoundException {
        try {
            parse(in.readUTF());
        } catch (MimeTypeParseException e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * Returns a clone of this object.
     * @return a clone of this object
     */

    @Nullable
    public Object clone() {
        MimeType that = null;
        try {
            that = (MimeType)super.clone();
        } catch (CloneNotSupportedException cannotHappen) {
        }
        that.parameters = parameters.clone();
        return that;
    }

    private String    primaryType;
    private String    subType;
    private MimeTypeParameterList parameters;

    //    below here be scary parsing related things

    /**
     * Determines whether or not a given character belongs to a legal token.
     */
    private static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }

    /**
     * Determines whether or not a given string is a legal token.
     */
    private boolean isValidToken(@Nonnull String s) {
        int len = s.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * A string that holds all the special chars.
     */

    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";

} // class MimeType


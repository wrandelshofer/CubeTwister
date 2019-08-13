/* @(#)ArrayUtil.java
 *
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 *
 * Implementation derived from Sun's Java 1.4 VM.
 */

package ch.randelshofer.util;

import java.util.ArrayList;

/**
 * This class contains various methods for manipulating arrays (such as
 * sorting and searching). 
 * 
 */
public class ArrayUtil {

    /** Suppresses default constructor, ensuring non-instantiability.*/
    private ArrayUtil() {
    }

    /**
     * Returns the biggest integer value of the array.
     */
    public static int max(int[] a) {
        int v = Integer.MIN_VALUE;
        for (int i=a.length - 1; i != -1; i--) {
            if (a[i] > v) v = a[i];
        }
        return v;
    }    
    /**
     * Performs a linear search to determine the index of the specified
     * Object.
     *
     * @param a The array.
     * @param o The object.
     * @return The index of the object in the array, or -1 if the object
     * is not contained in the array.
     */
    public static int indexOf(Object[] a, Object o) {
        for (int i=0; i < a.length; i++) {
            if (a[i].equals(o)) return i;
        }
        return -1;
    }
    
    public static ArrayList<Integer> asList(int[] values) {
        if (values == null) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<Integer>(values.length);
        for (int i=0; i < values.length; i++) {
            list.add(new Integer(values[i]));
        }
        return list;
    }
    
}

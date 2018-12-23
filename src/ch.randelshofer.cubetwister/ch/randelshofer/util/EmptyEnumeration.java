/* @(#)EmptyEnumeration.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * EmptyEnumeration.
 *
 *@author Werner Randelshofer
 * @version $Id$
 * @version $Id$
 */
public class EmptyEnumeration<T>
implements Enumeration<T> {
    @SuppressWarnings("rawtypes")
  private final static Enumeration EMPTY_ENUMERATION = new EmptyEnumeration();

  private EmptyEnumeration() {
  }

  @Override
  public boolean hasMoreElements() {
    return false;
  }

  @Override
  public T nextElement() {
    throw new NoSuchElementException();
  }

  @SuppressWarnings("unchecked")
  public static <T> Enumeration<T> getInstance() {
    return (Enumeration<T>) EMPTY_ENUMERATION;
  }
}


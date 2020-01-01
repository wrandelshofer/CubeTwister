/* @(#)EmptyEnumeration.java
 * Copyright (c) 1999 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import org.jhotdraw.annotation.Nonnull;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * EmptyEnumeration.
 *
 *@author Werner Randelshofer
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

    @Nonnull
    @Override
    public T nextElement() {
        throw new NoSuchElementException();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Enumeration<T> getInstance() {
        return (Enumeration<T>) EMPTY_ENUMERATION;
    }
}


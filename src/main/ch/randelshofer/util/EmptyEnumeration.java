/*
 * @(#)EmptyEnumeration.java  1.0  2001-02-25
 *
 * Copyright (c) 1999 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * EmptyEnumeration.
 *
 *@author Werner Randelshofer
 * @version 1.0  2001-02-25  Method <code>getInstance</code> added.
 * @version 0.0  2000-01-02  Draft.
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


/* @(#)EmptyIterator.java
 * Copyright (c) 2013 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Empty Iterator.
 * @author Werner Randelshofer
 */
public class EmptyIterator<T> implements Iterator<T> {
    @SuppressWarnings("rawtypes")
  private final static Iterator EMPTY_ITERATOR = new EmptyIterator();

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new NoSuchElementException();
    }
    
  @SuppressWarnings("unchecked")
  public static <T> Iterator<T> getInstance() {
    return (Iterator<T>) EMPTY_ITERATOR;
  }
}

/* @(#)PriorityQueue.java
 * Copyright (c) 2002 Werner Randelshofer, Switzerland.
 * You may only use this software in accordance with the license terms.
 *
 * Original code is copyright by  JDCTechTips 2002-08-22. John Zukowski.
 */

package ch.randelshofer.util;


import java.io.Serializable;
import java.util.*;

/**
 * JDCTechTips 2002-08-22. John Zukowski.
 *
 * @author Werner Randelshofer
 */
public class PriorityQueue<E>
extends AbstractList<E>
implements Serializable {
        private final static long serialVersionUID = 1L;

    private final static int DEFAULT_PRIORITY_COUNT = 10;
    private final static int DEFAULT_PRIORITY = 0;
    
    private List<E>[] queue;
    
    /**
     * Constructs an empty priority queue which supports the priorities 0 to 9
     * (10 distinct priorities).
     */
    public PriorityQueue() {
        this(DEFAULT_PRIORITY_COUNT);
    }
    
    /**
     * Constructs a priority queue containing the elements of the specified
     * collection, in the order they are returned by the collection'siterator.
     * The created PriorityQueue supports the priorities 0 to 9 (10 distinct
     * priorities).
     * All elements are added using priority 0 (the lowest priority).
     */
    public PriorityQueue(Collection<? extends E> col) {
        this(col, DEFAULT_PRIORITY_COUNT);
    }
    
    /**
     * Constructs an empty priority queue which supports the specified
     * count of priorities.
     */
    public PriorityQueue(int count) {
        this(null, count);
    }
    
    /**
     * Constructs a priority queue containing the elements of the specified
     * collection, in the order they are returned by the collection'siterator.
     * The created PriorityQueue supports <code>count</code> distinct priorities.
     * All elements are added using priority 0 (the lowest priority).
     *
     * @exception IllegalArgumentException if the specified priority count
     * is is out of range <code>count &lt; 0</code>.
     */
    @SuppressWarnings("unchecked")
    public PriorityQueue(Collection<? extends E> col, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException(
            "Illegal priority count: "+ count);
        }
        queue = new List[count];
        if (col != null) {
            addAll(col);
        }
    }
    
    /**
     * Inserts an element to the priority queue using priority 0 (the lowest
     * priority).
     */
    public boolean add(E element) {
        insert(element, DEFAULT_PRIORITY);
        return true;
    }
    
    /**
     * Returns the priority count.
     */
    public int getPriorityCount() {
        return queue.length;
    }
    
    /**
     * Inserts an element to the priority queue using the specified priority.
     *
     * @exception IllegalArgumentException  if the specified priority
     * is is out of range <code>(priority &lt; 0 || priority &gt;= getPriorityCount())</code>.
     */
    public void insert(E element, int priority) {
        if (priority < 0 || priority >= getPriorityCount()) {
            throw new IllegalArgumentException(
            "Illegal priority: " + priority);
        }
        if (queue[priority] == null) {
            queue[priority] = new LinkedList<E>();
        }
        queue[priority].add(element);
        modCount++;
    }
    
    /**
     * Returns the first element in this priority queue.
     * Elements are returned highest priority first.
     */
    public E getFirst() {
        return iterator().next();
    }
    
    /**
     * Returns the element at the specified position in this priority queue.
     * The elements are ordered by priority.
     *
     * @exception IndexOutOfBoundsException if the specified index
     * is is out of range <code>(index &lt; 0 || index &gt;= size())</code>.
     */
    public E get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(
            "Illegal index: "+ index);
        }
        Iterator<E> iter = iterator();
        int pos = 0;
        while (iter.hasNext()) {
            if (pos == index) {
                return iter.next();
            } else {
                pos++;
            }
        }
        throw new IndexOutOfBoundsException(
        "Illegal index: "+ index);
    }

    /**
     * Removes all of the elements from this priority queue.
     */
    public void clear() {
        for (int i=0, n=queue.length; i < n; i++) {
            if (queue[i] != null) queue[i].clear();
        }
    }
    
    /**
     * Returns the first element in this priority queue and returns it.
     * Elements are returned highest priority first.
     */
    public E removeFirst() {
        Iterator<E> iter = iterator();
        E obj = iter.next();
        iter.remove();
        return obj;
    }
    
    /**
     * Returns the number of elements in this priority queue.
     */
    public int size() {
        int size = 0;
        for (int i=0, n=queue.length; i<n; i++) {
            if (queue[i] != null) {
                size += queue[i].size();
            }
        }
        return size;
    }
    
    /**
     * Returns an iterator of the elements in this priority queue (elements
     * with higher priorities first).
     */
    public Iterator<E> iterator() {
        Iterator<E> iter = new Iterator<E>() {
            int expectedModCount = modCount;
            int priority = queue.length - 1;
            int count = 0;
            int size = size();
            
            // Used to prevent successive remove() calls
            int lastRet = -1;
            
            Iterator<E> tempIter;
            
            // Get iterator for highest priority
            {
                if (queue[priority] == null) {
                    tempIter = null;
                } else {
                    tempIter = queue[priority].iterator();
                }
            }
            
            private final void checkForComodification() {
                if (modCount != expectedModCount) {
                    throw new ConcurrentModificationException();
                }
            }
            
            public boolean hasNext() {
                return count != size();
            }
            
            public E next() {
                while (true) {
                    if ((tempIter != null) && (
                    tempIter.hasNext())) {
                        E next = tempIter.next();
                        checkForComodification();
                        lastRet = count++;
                        return next;
                    } else {
                        // Get next iterator
                        if (--priority < 0) {
                            checkForComodification();
                            throw new NoSuchElementException();
                        } else {
                            if (queue[priority] == null) {
                                tempIter = null;
                            } else {
                                tempIter = queue[priority].iterator();
                            }
                        }
                    }
                }
            }
            
            public void remove() {
                if (lastRet == -1) {
                    throw new IllegalStateException();
                }
                checkForComodification();
                
                tempIter.remove();
                count--;
                lastRet = -1;
                expectedModCount = modCount;
            }
        };
        return iter;
    }
    
    /**
     * Returns a string representation of this collection. The string
     * representation consists of a list of the collection's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * ("[]"). Adjacent elements are separated by the characters ", "
     * (comma and space). Elements are converted to strings as by
     * String.valueOf(Object).
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("{");
        for (int n=queue.length-1, i=n; i>=0; --i) {
            if (i != n) {
                buffer.append(",");
            }
            buffer.append(i + ":");
            if ((queue[i] != null)
            && (queue[i].size() > 0)) {
                buffer.append(queue[i].toString());
            }
        }
        buffer.append("}");
        return buffer.toString();
    }
}


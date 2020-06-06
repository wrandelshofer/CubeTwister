package ch.randelshofer.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Breadth first iterator.
 */
public class BreadthFirstIterator<V> implements Iterator<V> {

    private final Function<V, Iterable<V>> nextFunction;
    private final Deque<V> deque;
    private final Predicate<V> visited;

    public BreadthFirstIterator(Function<V, Iterable<V>> nextFunction, V root) {
        this(nextFunction, root, new HashSet<>()::add);
    }

    public BreadthFirstIterator(Function<V, Iterable<V>> nextFunction, V root, Predicate<V> visited) {
        Objects.requireNonNull(nextFunction, "nextFunction is null");
        Objects.requireNonNull(root, "root is null");
        this.nextFunction = nextFunction;
        this.visited = visited;
        deque = new ArrayDeque<>(16);
        if (visited.test(root)) {
            deque.add(root);
        }
    }

    public static <V> Iterable<V> iterable(Function<V, Iterable<V>> nextFunction, V root) {
        return () -> new BreadthFirstIterator<>(nextFunction, root);
    }

    public static <V> Iterable<V> iterable(Function<V, Iterable<V>> nextFunction, V root, Predicate<V> visited) {
        return () -> new BreadthFirstIterator<>(nextFunction, root, visited);
    }

    @Override
    public boolean hasNext() {
        return !deque.isEmpty();
    }

    @Override
    public V next() {
        V next = deque.pollFirst();
        if (next == null) {
            throw new NoSuchElementException();
        }
        for (V v : nextFunction.apply(next)) {
            if (visited.test(v)) {
                deque.addLast(v);
            }
        }
        return next;
    }
}

package uk.ac.ucl.cs.programming.coursework.support;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 *
 * @param <T>
 */
public class FilterIterator<T> implements Iterator<T> {

    /** The actual iterator instance that this iterator filters. */
    private Iterator<T> delegate;

    /** The filter that this iterator uses to decide whether it should return a certain element or not. */
    private Filter<T> filter;

    /** The next element in the iteration. */
    private T next = null;

    // ------------------------------------------ Constructors

    public FilterIterator(Iterator<T> delegate, Filter<T> filter) {
        if (delegate == null) {
            throw new IllegalArgumentException(
                    "The given iteration delegate must not be null.");
        }
        if (filter == null) {
            throw new IllegalArgumentException(
                    "The given filter must not be null.");
        }

        this.delegate = delegate;
        this.filter = filter;

        // Immediately try to look for the first valid element. In
        // doing so, we know that this iterator has reached the end
        // of the collection if "next" is null.
        advance();
    }

    // ------------------------------------------ Iterator methods

    /**
     * <p>Returns <code>true</code> if the filtered iteration has more elements.</p>
     *
     * @return <code>true</code> if the iterator has more elements.
     */
    public boolean hasNext() {
        return next != null;
    }

    /**
     * <p>Returns the next element in the iteration.</p>
     *
     * @return the next element in the iteration.
     *
     * @throws NoSuchElementException iteration has no more elements.
     */
    public T next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        
        T obj = next;
        advance();
        return obj;
    }

    /**
     * <p>Usually removes the last element returned from the underlying
     * collection, however, this operation is not supported. You would
     * have to override this method.</p> 
     *
     * @throws UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
     */
    public void remove() {
        throw new UnsupportedOperationException(
                "This iterator doesn't allow / enable you to remove rules.");
    }

    // ------------------------------------------ Utility methods

    /**
     * <p>Advances to the next element in the iteration, i.e. it loops over
     * the delegate iterator and tries to find the next accepted element. If
     * there are no such elements left, the field next is going to be null
     * after the method call.</p>
     */
    private void advance() {
        while (delegate.hasNext()) {
            T element = delegate.next();
            if (filter.accept(element)) {
                next = element;
                return;
            }
        }

        next = null;
    }

    // ------------------------------------------ Nested interfaces

    /**
     * <p>Callback interface that the calling iterator uses to determine whether it
     * should include certain elements or not, so basically the whole filtering bit
     * has to be implemented by an implementation of this interface.</p>
     * 
     * @param <T> the element type of the collection that this iterator iterates over
     */
    public interface Filter<T> {

        /**
         * <p>Callback method that tells the calling iterator whether the given element
         * should be returned by the iterator or not.</p>
         *
         * @param element the next element in the iteration
         *
         * @return <code>true</code>, if the calling iterator should include this element,
         *          <code>false</code> otherwise
         */
        public boolean accept(T element);

    }

    // ------------------------------------------ Public utility classes

    public static class NonNullFilter<T> implements Filter<T> {
        
        public boolean accept(T element) {
            return element != null;
        }

    }


}

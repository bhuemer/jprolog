package uk.ac.ucl.cs.programming.coursework.support;

import java.util.Iterator;

/**
 *
 *
 * @param <T>
 * @param <U>
 */
public class AdaptionIterator<T, U> implements Iterator<U> {

    private Iterator<T> delegate;

    private Adaptor<T, U> adaptor;

    // ------------------------------------------ Constructors

    public AdaptionIterator(Iterator<T> delegate, Adaptor<T, U> adaptor) {
        this.delegate = delegate;
        this.adaptor = adaptor;
    }

    // ------------------------------------------ Iterator methods

    public boolean hasNext() {
        return delegate.hasNext();
    }

    public U next() {
        return adaptor.adapt(delegate.next());
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

    // ------------------------------------------ Nested interfaces

    public interface Adaptor<T, U> {

        /**
         * <p>Adapts the given element, which means it takes an element of a certain type and returns
         * another object, that probably wraps the given object but is of a different type.</p>
         *
         * @param element the element to adapt
         * 
         * @return the adapted object
         */
        public U adapt(T element);

    }
    
}

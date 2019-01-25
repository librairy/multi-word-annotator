package edu.mit.jmwe.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wrapper for a given iterator that will return a limited number of elements
 * from that iterator.
 * 
 * @param <T>
 *            the type of objects that the iterator returns
 * @author M.A. Finlayson
 * @version $Id: LimitedIterator.java 267 2011-05-05 19:22:32Z markaf $
 * @since jMWE 1.0.0
 */
public class LimitedIterator<T> implements Iterator<T> {
	
	// instance fields set on construction
	private final int limit;
	private final Iterator<? extends T> itr;
	
	// instance fields set dynamically
	private int count = 0;

	/**
	 * Constructs the limited iterator from a backing iterator and a limit that
	 * determines how many of the backing iterator's elements will be returned
	 * by the limited iterator.
	 * 
	 * @param backingItr
	 *            the iterator that will be used to back this iterator. May not
	 *            be <code>null</code>.
	 * @param limit
	 *            the number of elements that will be returned by the limited
	 *            iterator. If the limit is zero or negative, there is no limit
	 *            on the number of items returned.
	 * @throws NullPointerException
	 *             if the backing iterator is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public LimitedIterator(Iterator<? extends T> backingItr, int limit) {
		if(backingItr == null)
			throw new NullPointerException();
		this.limit = limit;
		this.itr = backingItr;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if(limit > 0 && count >= limit)
			return false;
		return itr.hasNext();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Iterator#next()
	 */
	public T next() {
		if(count >= limit) 
			throw new NoSuchElementException();
		T next = itr.next();
		count++;
		return next;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		itr.remove();
	}
	
}
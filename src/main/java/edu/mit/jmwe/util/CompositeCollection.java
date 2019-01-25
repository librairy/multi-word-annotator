/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A collection that is composed of several subsets of elements.
 * 
 * @param <E>
 *            the element type in the collection
 * @author M.A. Finlayson
 * @version $Id: CompositeCollection.java 265 2011-05-05 19:20:59Z markaf $
 * @since jMWE 1.0.0
 */
public class CompositeCollection<E> extends AbstractCollection<E> {
	
	protected final List<Collection<? extends E>> subsets;
	
	/**
	 * Constructs this composite collection from the given array of subsets.
	 * 
	 * @param subsets
	 *            the collections containing the elements in this composite
	 *            collection.
	 * @throws NullPointerException
	 *             if any of the subsets are <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if all of the subsets are empty.
	 * @since jMWE 1.0.0
	 */
	public CompositeCollection(Collection<? extends E>... subsets){
		this(Arrays.asList(subsets));
	}
	
	/**
	 * Constructs this composite collection from the given collection of subsets.
	 * 
	 * @param subsets
	 *            the collections containing the elements in this composite
	 *            collection.
	 * @throws NullPointerException
	 *             if any of the subsets are <code>null</code>.
	 * @throws IllegalArgumentException
	 *             if all of the subsets are empty.
	 * @since jMWE 1.0.0
	 */
	public CompositeCollection(Iterable<Collection<? extends E>> subsets){
		ArrayList<Collection<? extends E>> hidden = new ArrayList<Collection<? extends E>>();
		for(Collection<? extends E> subset : subsets){
			if(subset == null)
				throw new NullPointerException();
			hidden.add(subset);
		}
		if(hidden.isEmpty())
			throw new IllegalArgumentException();
		hidden.trimToSize();
		this.subsets = Collections.unmodifiableList(hidden);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		int size = 0;
		for(Collection<?> subset : subsets)
			size += subset.size();
		return size;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return new CompositeCollectionItr();
	}
	
	/**
	 * An iterator over the elements of this composite collection.
	 * 
	 * @author M.A. Finlayson
	 * @version $Id: CompositeCollection.java 265 2011-05-05 19:20:59Z markaf $
	 * @since jMWE 1.0.0
	 */
	protected class CompositeCollectionItr implements Iterator<E> {
		
		private final LinkedList<Iterator<? extends E>> itrs;
		private Iterator<? extends E> currItr;
		 
		public CompositeCollectionItr(){
			itrs = new LinkedList<Iterator<? extends E>>();
			for(Collection<? extends E> subset : subsets)
				itrs.add(subset.iterator());
			currItr = itrs.getFirst();
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			for(Iterator<?> i : itrs)
				if(i.hasNext())
					return true;
			return false;
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#next()
		 */
		public E next() {
			if(!currItr.hasNext()){
				itrs.poll();
				if(itrs.isEmpty())
					throw new NoSuchElementException();
				currItr = itrs.getFirst();
			}
			return currItr.next();
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			currItr.remove();
		}
		
	}

}

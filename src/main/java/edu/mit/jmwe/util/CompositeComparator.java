/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A comparator constructed out of multiple comparators.
 *
 * @author M.A. Finlayson
 * @version $Id: CompositeComparator.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class CompositeComparator<T> extends AbstractList<Comparator<T>> implements Comparator<T>, List<Comparator<T>> {

	// final instance field
	private final List<Comparator<T>> cs;

	/**
	 * Constructs a composite comparator from two of comparators. The array of
	 * comparators should be ordered by descending preference. The most
	 * preferred comparator should be first, the second-most-preferred second,
	 * etc. The comparators may not be <code>null</code>
	 * 
	 * @param one
	 *            the first comparator to use, may not be <code>null</code>
	 * @param two
	 *            the second comparator to use, may not be <code>null</code>
	 * @throws NullPointerException
	 *             if any specified comparator <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public CompositeComparator(Comparator<T> one, Comparator<T> two){
		this(Arrays.asList(one, two));
	}
	
	/**
	 * Constructs a composite comparator from three of comparators. The array of
	 * comparators should be ordered by descending preference. The most
	 * preferred comparator should be first, the second-most-preferred second,
	 * etc. The comparators may not be <code>null</code>
	 * 
	 * @param one
	 *            the first comparator to use, may not be <code>null</code>
	 * @param two
	 *            the second comparator to use, may not be <code>null</code>
	 * @param three
	 *            the third comparator to use, may not be <code>null</code>
	 * @throws NullPointerException
	 *             if any specified comparator <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public CompositeComparator(Comparator<T> one, Comparator<T> two, Comparator<T> three){
		this(Arrays.asList(one, two, three));
	}

	/**
	 * Constructs a composite comparator from an array of comparators. The array
	 * of comparators should be ordered by descending preference. The most
	 * preferred comparator should be first, the second-most-preferred second,
	 * etc.
	 * 
	 * @param cs
	 *            the array of comparators in order of descending preference.
	 *            May not be <code>null</code>, nor contain <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified array is <code>null</code> or if any of its
	 *             elements are <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public CompositeComparator(Comparator<T>... cs){
		this(Arrays.asList(cs));
	}
	
	/**
	 * Constructs a composite comparator from an array of comparators. The array
	 * of comparators should be ordered by descending preference. The most
	 * preferred comparator should be first, the second-most-preferred second,
	 * etc.
	 * 
	 * @param cs
	 *            the list of comparators in order of descending preference.
	 *            May not be <code>null</code>, nor contain <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified array is <code>null</code> or if any of its
	 *             elements are <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public CompositeComparator(List<? extends Comparator<T>> cs){
		for(Comparator<T> c : cs)
			if(c == null)
				throw new NullPointerException();
		this.cs = new ArrayList<Comparator<T>>(cs);
	}
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(T one, T two) {
		int cmp;
		for(Comparator<T> c : cs){
			cmp = c.compare(one, two);
			if(cmp != 0)
				return cmp;
		}
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public Comparator<T> get(int index) {
		return cs.get(index);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return cs.size();
	}

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for constructing a {@link Comparator} from a map of tokens in a
 * list to their positions in the list. Subclasses are comparators that compare
 * objects that consist of these tokens by using the tokens' indices.
 * 
 * @param <T>
 *            the type of the tokens that are indexed
 * @param <C>
 *            the type of the object being compared
 * @author M.A. Finlayson
 * @version $Id: AbstractIndexComparator.java 264 2011-05-05 19:19:15Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class AbstractIndexComparator<T, C> implements Comparator<C> {
	
	// final instance fields
	protected final Map<T, Integer> indexMap;
	
	/**
	 * Constructs the comparator from the given list of objects by mapping each
	 * object in the list to its index in the list.
	 * 
	 * @param list
	 *            the list of objects used to construct this comparator. May not
	 *            be <code>null</code>.
	 * @throws NullPointerException
	 *             if the given list is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public AbstractIndexComparator(List<T> list){
		this(createIndexMap(list));
	}
	
	/**
	 * Constructs the comparator from the given index map of each object in a
	 * list to its index in the list.
	 * 
	 * @param indexMap
	 *            map of each object in a list to its index in the list. May not
	 *            be <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified map is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public AbstractIndexComparator(Map<T, Integer> indexMap){
		if(indexMap == null)
			throw new NullPointerException();
		this.indexMap = indexMap;
	}
	
	/**
	 * Returns the index map on which this comparator is based.
	 * 
	 * @return the non-<code>null</code> index map on which this comparator is
	 *         based.
	 * @since jMWE 1.0.0
	 */
	public Map<T,Integer> getIndexMap(){
		return indexMap;
	}

	/**
	 * Returns a map of each object in the list mapped to its index in the list.
	 * 
	 * @param <T>
	 *            the type of objects in the sentence
	 * @param list
	 *            a non-null list
	 * @return a map of each object in the list mapped to its index in the list
	 * @throws NullPointerException
	 *             if the given list is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public static <T> Map<T, Integer> createIndexMap(List<T> list) {
		Map<T, Integer> idxMap = new HashMap<T, Integer>(list.size());
		for(int i = 0; i < list.size(); i++)
			idxMap.put(list.get(i), i);
		return Collections.unmodifiableMap(idxMap);
	}

}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Default, concrete implementation of the {@link ITagList} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class TagList extends AbstractList<ISenseLocation> implements ITagList {

	private final String key;
	private final int num;
	private final List<ISenseLocation> locs;
	
	/**
	 * Constructs a new tag list with the specified key, number, and sense
	 * location list. The sense location array is used to back the internal list
	 * for this TagList, so clients should not retain a pointer to the array.
	 * 
	 * @throws IllegalArgumentException
	 *             if the key is empty or all whitespace, or the number is less
	 *             than zero.
	 * @since JSemcor 1.0.0
	 */
	public TagList(String key, int num, Collection<? extends ISenseLocation> locs) {
		if (key.trim().length() == 0) throw new IllegalArgumentException();
		if (locs.size() < 1) throw new IllegalArgumentException();
		if (num < 1) throw new IllegalArgumentException();

		this.key = key;
		this.num = num;
		this.locs = new ArrayList<ISenseLocation>(locs);
	}

	/**
	 * Constructs a new tag list with the specified key, number, and sense
	 * location list. The sense location array is used to back the internal list
	 * for this TagList, so clients should not retain a pointer to the array.
	 * 
	 * @throws IllegalArgumentException
	 *             if the key is empty or all whitespace, or the number is less
	 *             than zero.
	 * @since JSemcor 1.0.0
	 */
	public TagList(String key, int num, ISenseLocation... locs) {
		if (key.trim().length() == 0) throw new IllegalArgumentException();
		if (locs.length < 1) throw new IllegalArgumentException();
		if (num < 1) throw new IllegalArgumentException();

		this.key = key;
		this.num = num;
		this.locs = Arrays.asList(locs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.data.tags.ITagList#getSenseKey()
	 */
	public String getSenseKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.data.tags.ITagList#getSenseNumber()
	 */
	public int getSenseNumber() {
		return num;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public ISenseLocation get(int index) {
		return locs.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return locs.size();
	}
	
}

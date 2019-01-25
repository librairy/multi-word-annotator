/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.List;
import java.util.Map;

import edu.mit.jmwe.util.AbstractIndexComparator;

/**
 * A comparator that compares {@link IMWE}s by checking which MWE starts earlier
 * in the list of tokens used to construct this comparator. If the two MWEs have
 * the same tokens, uses the part of speech to determine the order.
 * 
 * @param <T> the type of token for this comparator
 * @author M.A. Finlayson
 * @version $Id: MWEComparator.java 327 2011-05-08 21:13:58Z markaf $
 * @since jMWE 1.0.0
 */
public class MWEComparator<T extends IToken> extends AbstractIndexComparator<T, IMWE<T>>{

	/**
	 * Constructs the comparator from the given list of tokens by mapping each
	 * token in the list to its index in the list.
	 * 
	 * @param list
	 *            the list of objects used to construct this comparator. May not
	 *            be <code>null</code>.
	 * @throws NullPointerException
	 *             if the given list is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public MWEComparator(List<T> list) {
		super(list);
	}

	/**
	 * Constructs the comparator from the given index map of each token in a
	 * list to its index in the list.
	 * 
	 * @param indexMap
	 *            map of each token in a list to its index in the list. May not
	 *            be <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public MWEComparator(Map<T, Integer> indexMap) {
		super(indexMap);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(IMWE<T> one, IMWE<T> two) {
		if(one == two)
			return 0;
		// if tokens are the same then order by part of speech
		if(one.getTokens().equals(two.getTokens())) 
			return one.getEntry().getPOS().compareTo(two.getEntry().getPOS());
		
		return earlier(one, two) ? -1 : 1;
	}

	/**
	 * Internal method used to determine if one multi-word expression appears in
	 * the sentence before another.
	 * 
	 * @param one
	 *            multi-word expression being compared
	 * @param two
	 *            multi-word expression being compared
	 * @return <code>true</code> if one is before two
	 *         <code>false</code> otherwise.
	 * 
	 * @since jMWE 1.0.0
	 */
	protected boolean earlier(IMWE<T> one, IMWE<T> two) {
		IToken token1, token2;
		for(int n = 0; n < one.getTokens().size();n++){
			token1 = one.getTokens().get(n);
			if(n < two.getTokens().size()){
				token2 = two.getTokens().get(n);
				if (token2 == token1)
					continue;
				return indexMap.get(token1) < indexMap.get(token2);
			} else {
				return true;
			}
		}
		return false;
	}

}

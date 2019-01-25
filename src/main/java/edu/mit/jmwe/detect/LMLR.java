/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.util.ListComparator;

/**
 * Stands for "Longest Match, Left to Right". A detector that resolves conflicts
 * in the results of its backing detector by returning the MWE that started the
 * earliest in the sentence. If the conflicting MWEs started at the same
 * position, returns the longer of them.
 * 
 * @author M.A. Finlayson
 * @version $Id: LMLR.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class LMLR extends HasMWEDetector implements IMWEDetectorResolver {

	/**
	 * Constructs the detector from the backing detector. May not be
	 * <code>null</code>.
	 * 
	 * @param detector
	 *            the detector used to back this detector.
	 * @since jMWE 1.0.0
	 */
	public LMLR(IMWEDetector detector) {
		super(detector);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> s) {
		
		// run embedded detector
		List<IMWE<T>> results = super.detect(s);
		
		// construct a map from tokens to mwes
		Map<T, IMWE<T>> map = new LinkedHashMap<T, IMWE<T>>(s.size());
		for(T token : s)
			map.put(token, null);
		
		// insert each mwe into the map under it's first token
		// if there's already an mwe in that slot, keep the longest one
		T first;
		IMWE<T> filler;
		Comparator<T> c = new ListComparator<T>(s); 
		Map<IMWE<T>, T> firstTokens = new HashMap<IMWE<T>, T>(results.size());
		for(IMWE<T> mwe : results){
			first = getFirstToken(mwe.getTokens(), c);
			firstTokens.put(mwe, first);  // save first token for later
			filler = map.get(first);
			filler = longest(filler, mwe, c);
			map.put(first, filler);
		}
		
		// moving from left to right, throw out conflicting MWEs
		IMWE<T> mwe;
		for(T token : s){
			mwe = map.get(token);
			if(mwe == null)
				continue;
			first = firstTokens.get(mwe);
			// clear out mwe's filed under other
			// tokens (except the first one)
			for(T mweToken : mwe.getTokens())
				if(mweToken != first)
					map.put(mweToken, null);
		}
		
		// retain original order
		results.retainAll(map.values());
		return results;
		
	}
	
	/**
	 * Returns the token that is the first in a given iterable collection of
	 * tokens. Uses the given comparator to compare two tokens.
	 * 
	 * @param <T>
	 *            the type of tokens being compared
	 * @param tokens
	 *            the tokens being compared
	 * @param c
	 *            the comparator used to determine if one token comes before
	 *            another
	 * @return the first token of the given tokens.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> T getFirstToken(Iterable<? extends T> tokens, Comparator<T> c){
		Iterator<? extends T> i = tokens.iterator();
		if(!i.hasNext())
			throw new IllegalArgumentException();
		T first = i.next();
		T next;
		while(i.hasNext()){
			next = i.next();
			first = c.compare(first, next) <= 0 ? first : next;
		}
		return first;
	}
	
	/**
	 * Compares two MWEs and returns the longest MWE. If they are the same size,
	 * returns the MWE that has more tokens to the left.
	 * 
	 * @param <T>
	 *            the type of tokens in the MWE
	 * @param one
	 *            the first MWE
	 * @param two
	 *            the second MWE
	 * @param c
	 *            the comparator to use
	 * @return the longest MWE. If they are the same size, returns the MWE that
	 *         has more tokens to the left.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> IMWE<T> longest(IMWE<T> one, IMWE<T> two, Comparator<T> c){
		
		// error if both are null
		if(one == null && two == null)
			throw new NullPointerException();
		
		if(one == two)
			return one;
		
		// if one is null, return the other
		if(one == null)
			return two;
		if(two == null)
			return one;
		
		// take longest by count
		if(one.getTokens().size() > two.getTokens().size())
			return one;
		if(one.getTokens().size() < two.getTokens().size())
			return two;
		
		// same number of tokens, so find the MWE with more tokens to the left
		int cmp;
		for(int i = 0; i < one.getTokens().size(); i++){
			cmp = c.compare(one.getTokens().get(i), two.getTokens().get(i));
			if(cmp < 0)
				return one;
			if(cmp > 0)
				return two;
		}
		
		// for christ's sake, just return the first one
		return one;
	}

}

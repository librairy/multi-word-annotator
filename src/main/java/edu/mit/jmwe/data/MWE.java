/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.jmwe.data.IMWEDesc.IPart;


/**
 * Default implementation of the <code>IMWE</code> interface.
 * 
 * @param <T>
 *            type of {@link IToken} objects that form the multi-word expression
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: MWE.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class MWE<T extends IToken> implements IMWE<T>{
	
	// final instance fields
	private final IMWEDesc entry;
	private final String form;
	private final List<T> tokens;
	private final boolean isInflected;
	private final Map<T, IPart> partMap;

	private Long offset;
	
	/**
	 * Constructs a new multi-word expression from a map of tokens to parts.
	 * This constructor allocates a new internal map, and so subsequent changes
	 * to the source map will not affect this object.
	 * 
	 * @param partMap
	 *            the map of tokens to MWE parts that will make up this
	 *            multi-word expression, may not be <code>null</code> or empty,
	 *            nor contain <code>null</code>. Iterating over the map should
	 *            return the tokens in the same order they are found in the
	 *            original sentence.
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>, or the map contains
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the token map is empty
	 * @since jMWE 1.0.0
	 */
	public MWE(Map<T, IPart> partMap){
		this(partMap, true);
	}

	/**
	 * Constructs a new multi-word expression from a map of tokens to parts.
	 * This constructor may or may not allocate a new internal map, depending on
	 * the value of the reallocation flag. If no reallocation is requested, this
	 * constructor reuses the given map, merely wrapping it to make it
	 * unmodifiable, and so subsequent changes to the source list will affect
	 * this object.
	 * 
	 * @param partMap
	 *            the map of tokens to MWE parts that will make up this
	 *            multi-word expression, may not be <code>null</code> or empty,
	 *            nor contain <code>null</code>. Iterating over the map should
	 *            return the tokens in the same order they are found in the
	 *            original sentence.
	 * @param reallocate
	 *            if <code>true</code>, reallocate the specified map; otherwise,
	 *            reuse the specified map
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>, or the map contains
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the part map is empty, or the mwe description does not
	 *             match between the parts
	 * @since jMWE 1.0.0
	 */
	public MWE(Map<T, IPart> partMap, boolean reallocate){
		if(partMap == null)
			throw new NullPointerException();
		if(partMap.isEmpty()) 
			throw new IllegalArgumentException();
		
		// check for nulls in part map, extract entry
		StringBuilder form = new StringBuilder();
		IMWEDesc entry = null;
		boolean isInflected = false;
		Entry<T, IPart> e;
		for(Iterator<Entry<T, IPart>> i = partMap.entrySet().iterator(); i.hasNext(); ){
			e = i.next();
			if(e.getKey() == null)
				throw new NullPointerException();
			if(e.getValue() == null)
				throw new NullPointerException();
			if(entry == null){
				entry = e.getValue().getParent();
			} else {
				if(e.getValue().getParent() != entry)
					throw new IllegalArgumentException();
			}
			isInflected |= !e.getKey().getForm().equalsIgnoreCase(e.getValue().getForm());
			form.append(e.getKey().getForm().toLowerCase());
			if(i.hasNext())
				form.append('_');
		}
		if(entry == null)
			throw new NullPointerException();
		
		// do reallocation
		partMap = reallocate ? 
				new LinkedHashMap<T, IPart>(partMap) : 
				partMap;
				
		// Fix for Bug001
		// construct properly order token list
		final Map<T, IPart> map = partMap;
		List<T> ts = new ArrayList<T>(partMap.keySet());
		Collections.sort(ts, new Comparator<T>(){
			public int compare(T one, T two) {
				IPart pOne = map.get(one);
				IPart pTwo = map.get(two);
				return pOne.compareTo(pTwo);
			}
		});
		// End fix for Bug001
		
		// field assignment
		this.entry = entry;
		this.form = form.toString();
		this.isInflected = isInflected;
		this.tokens = Collections.unmodifiableList(ts);
		this.partMap = Collections.unmodifiableMap(partMap);


		this.offset = partMap.keySet().stream().map(t -> (Token) t).map(token -> token.getOffset()).reduce((a,b) -> a<b? a : b).get();

	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasForm#getForm()
	 */
	public String getForm() {
		return form;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWE#getEntry()
	 */
	public IMWEDesc getEntry() {
		return entry;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWE#getTokens()
	 */
	public List<T> getTokens() {
		return tokens;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWE#getPartMap()
	 */
	public Map<T, IPart> getPartMap() {
		return partMap;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWE#isInflected()
	 */
	public boolean isInflected() {
		return isInflected;
	}

	@Override
	public Long getOffset() {
		return offset;
	}

	/*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString(){
		StringBuilder buf = new StringBuilder();
		buf.append(entry.getID().toString());
		buf.append("={");
		for(Iterator<T> i = partMap.keySet().iterator(); i.hasNext(); ){
			buf.append(i.next());
			if(i.hasNext())
				buf.append(',');
		}
		buf.append('}');
		return buf.toString();
	}
	

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entry.hashCode();
		result = prime * result + partMap.hashCode();
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		if (obj == null) 
			return false;
		MWE<?> other = (MWE<?>) obj;
		if (!entry.equals(other.getEntry())) 
			return false;
		if(!partMap.equals(other.partMap))
			return false;
		return true;
	}

	/**
	 * Returns true if the two MWEs use the same tokens and are assigned the
	 * same root entries.
	 * 
	 * @param one
	 *            the first MWE to be compared; may be <code>null</code>
	 * @param two
	 *            the second MWE to be compared; may be <code>null</code>
	 * @return true if the two MWEs use the same tokens and are assigned the
	 *         same root entries.
	 * @since jMWE 1.0.0
	 */
	public static boolean equals(IMWE<?> one, IMWE<?> two){
		if(one == two)
			return true;
		if(one != null && two == null)
			return false;
		if(two != null && one == null)
			return false;
		
		if(one.equals(two))
			return true;
		
		// make sure they use the same tokens
		if(!one.getPartMap().keySet().equals(two.getPartMap().keySet()))
			return false;
		
		// make sure they are assigned the same root entries
		if(!AbstractMWEDesc.equalsRoots(one.getEntry(), two.getEntry()))
			return false;
			
		return true;
	}

	/**
	 * Returns a score which is the ratio of the number of tokens shared between
	 * the two MWEs and the total number of unique tokens in both MWEs together.
	 * <p>
	 * If the two MWEs being compared do not come from the same sentence, or
	 * share no tokens, the score will be zero.
	 * 
	 * @param one
	 *            the first MWE to be compared, may not be <code>null</code>
	 * @param two
	 *            the second MWE to be compared, may not be <code>null</code>
	 * @return an alignment score which is zero if the two MWEs don't overlap,
	 *         one if they overlap perfectly, and somewhere in between otherwise
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static double overlap(IMWE<?> one, IMWE<?> two){
		
		int maxSize = one.getTokens().size() + two.getTokens().size();
		Map<Object, Object> tokens = new IdentityHashMap<Object, Object>(maxSize);
		
		// calculate total number of unique tokens
		for(Object token : one.getTokens())
			tokens.put(token, null);
		for(Object token : two.getTokens())
			tokens.put(token, null);
		double total = tokens.size();
		
		// calculate number of objects shared between the sets
		tokens.clear();
		for(Object token : one.getTokens())
			tokens.put(token, null);
		tokens.keySet().retainAll(two.getTokens());
		double overlap = tokens.size();
		
		return overlap/total; 
		
	}

}

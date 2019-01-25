/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Default, concrete implementation of the {@link ISentence} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.62, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Sentence extends AbstractList<IToken> implements ISentence {

	private final int number;
	private final List<IToken> tokenList;
	private final List<IWordform> wfList;
	private final Map<Integer, IWordform> wfMap;
	
	private List<List<IWordform>> collocs;
	private String data;

	/**
	 * Constructs a new sentence with the specified number, using the specified
	 * tokens as constituents.
	 * 
	 * @throws IllegalArgumentException
	 *             if number is less than 1, or if the token list is empty
	 * @throws NullPointerException
	 *             if the token list or any of its elements are
	 *             <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public Sentence(int number, List<IToken> tokens) {
		if(tokens == null) throw new NullPointerException();
		if(tokens.isEmpty()) throw new IllegalArgumentException();
		checkSentenceNumber(number);
		
		this.number = number;
		
		// count number of wfs
		int numWfs = 0;
		for(IToken token : tokens){
			if(token instanceof IWordform) numWfs++;
		}

		// allocate lists
		this.tokenList = new ArrayList<IToken>(tokens.size());
		List<IWordform> hidden = new ArrayList<IWordform>(numWfs);
		this.wfMap = new HashMap<Integer, IWordform>(numWfs);

		IWordform wf;
		for(IToken token : tokens) {
			if(token == null) throw new NullPointerException();
			if(token instanceof IWordform) {
				wf = (IWordform)token;
				hidden.add(wf);
				wfMap.put(wf.getNumber(), wf);
				if (wf.getNumber() != hidden.size()) logNonSequentialWordNum(hidden.size(), wf.getNumber());
			}
			tokenList.add(token);
		}
		this.wfList = Collections.unmodifiableList(hidden);
	}
	
	/**
	 * Logs the presence of a non-sequential word numbers. This implementation
	 * does nothing, and is left to subclasses to override if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logNonSequentialWordNum(int expected, int actual){
		//System.err("Word form numbers are not sequential, expected=" + expected + ", actual="+actual);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.element.ISentence#getNumber()
	 */
	public int getNumber() {
		return number;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISentence#getWord(int)
	 */
	public IWordform getWord(int wordNumber) {
		return wfMap.get(wordNumber);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISentence#getCollocations()
	 */
	public List<List<IWordform>> getCollocations() {
		if(collocs == null){
			int rootIdx;
			ArrayList<IWordform> arrayList;
			SortedMap<Integer, ArrayList<IWordform>> cs = null;
			for(IWordform wf : wfList){
				if(wf.getDistance() == 0) continue;
				if(cs == null) cs = new TreeMap<Integer, ArrayList<IWordform>>();
				rootIdx = wf.getTokenIndex() + wf.getDistance();
				arrayList = cs.get(rootIdx);
				if(arrayList == null){
					arrayList = new ArrayList<IWordform>(2);
					cs.put(rootIdx, arrayList);
				}
				arrayList.add(wf);
			}

			if(cs == null){
				// there are no collocation, use the empty list
				collocs = Collections.<List<IWordform>>emptyList();
			} else {
				// there are collocations
				
				// add in roots
				IWordform wf;
				for(Entry<Integer, ArrayList<IWordform>> entry : cs.entrySet()){
					wf = (IWordform)get(entry.getKey());
					entry.getValue().add(0, wf);
				}
				
				// trim lists and make them unmodifiable
				collocs = new ArrayList<List<IWordform>>(cs.size());
				for(ArrayList<IWordform> list : cs.values()){
					list.trimToSize();
					collocs.add(Collections.unmodifiableList(list));
				}
				collocs = Collections.unmodifiableList(collocs);
			}
		}
		return collocs;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISentence#getWordList()
	 */
	public List<IWordform> getWordList() {
		return wfList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public IToken get(int index) {
		return tokenList.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return tokenList.size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if(data == null){
			
			// calculate size
			int size = 16;
			for(IToken t : tokenList){
				size = t.getData().length() + 1;
			}
			
			// allocate buffer
			StringBuilder sb = new StringBuilder(size);
			
			sb.append(TAG_SENT_LEFT);
			sb.append(SPACE);
			sb.append(ATTR_SNUM);
			sb.append(EQUALS);
			sb.append(number);
			sb.append(RIGHT_ANGLE_BRACKET);
			sb.append(NEWLINE);
			
			for(IToken t : tokenList){
				sb.append(t.getData());
				sb.append(NEWLINE);
			}
			
			sb.append(TAG_SENT_RIGHT);
			data = sb.toString();
		}
		return data;
	}

	/**
	 * Returns whether the specified integer is a valid sentence number. Valid
	 * sentence numbers are strictly greater than zero.
	 * 
	 * @return <code>true</code> if the specifed number is a legal sentence
	 *         number, namely, if it is greater than or equal to 1;
	 *         <code>false</code> otherwise.
	 * @since JSemcor 1.0.0
	 */
	public static boolean isIllegalSentenceNumber(int number){
		return number < 1;
	}
	
	/**
	 * Throws an {@link IllegalArgumentException} if the specified number is not
	 * a valid sentence number. Valid sentence numbers are strictly greater than
	 * zero.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specifed number is not a legal sentence number,
	 *             namely, if it is less than 1.
	 * @since JSemcor 1.0.0
	 */
	public static void checkSentenceNumber(int number){
		if(isIllegalSentenceNumber(number)) throw new IllegalArgumentException();
	}

}

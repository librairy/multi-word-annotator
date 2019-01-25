/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.element.Sentence;
import edu.mit.jsemcor.element.Wordform;

/**
 * Concrete, default implementation of the {@link ISenseLocation} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.52, 19 Sep 2008
 * @since JSemcor 1.0.0
 */
public class SenseLocation implements ISenseLocation {
	
	private final IContextID cid;
	private final String key;
	private final int num, sent, word;
	
	/**
	 * Constructs a new sense location object with the specified parameters.
	 * 
	 * @throws IllegalArgumentException
	 *             if the key is empty or all whitespace,
	 * @throws IllegalArgumentException
	 *             if the sentence number is not a legal sentence number
	 * @throws IllegalArgumentException
	 *             if the word number is not a legal word number
	 * @since JSemcor 1.0.0
	 */
	public SenseLocation(IContextID cid, String key, int num, int sent, int word){
		
		if(cid == null) throw new NullPointerException();
		if(key.trim().length() == 0) throw new IllegalArgumentException();
		Sentence.checkSentenceNumber(sent);
		Wordform.checkWordNumber(sent);

		this.cid = cid;
		this.key = key;
		this.num = num;
		this.sent = sent;
		this.word = word;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.tags.ISenseLocation#getContextID()
	 */
	public IContextID getContextID() {
		return cid;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.tags.ISenseLocation#getSenseKey()
	 */
	public String getSenseKey() {
		return key;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.tags.ISenseLocation#getSenseNumber()
	 */
	public int getSenseNumber() {
		return num;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.tags.ISenseLocation#getSentenceNumber()
	 */
	public int getSentenceNumber() {
		return sent;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.tags.ISenseLocation#getWordNumber()
	 */
	public int getWordNumber() {
		return word;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("/");
		sb.append(num);
		sb.append("/");
		sb.append(cid);
		sb.append(',');
		sb.append("s=");
		sb.append(sent);
		sb.append(",w=");
		sb.append(word);
		return sb.toString();
	}
	
}

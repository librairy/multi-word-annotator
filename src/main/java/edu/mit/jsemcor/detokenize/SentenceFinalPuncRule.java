/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.detokenize;

import edu.mit.jsemcor.element.IToken;
import edu.mit.jsemcor.element.Punc;

/**
 * Implements the rule that there is no space before sentence-final punctuation
 * (periods, question marks, and exclamation points), and a space after, unless
 * the next token is a closing delimiter. To obtain an instance of this class,
 * call {@link #getInstance()}.
 * 
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class SentenceFinalPuncRule implements ISeparatorRule {
	
	/**
	 * Static singleton instance of this class, accessed and initialized by
	 * {@link #getInstance()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static SentenceFinalPuncRule instance;
	
	/**
	 * Returns (initializing if necessary) the static singleton instance of this
	 * class.
	 * 
	 * @return the static, singleton instance of this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static SentenceFinalPuncRule getInstance(){
		if(instance == null) instance = new SentenceFinalPuncRule();
		return instance;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * sub-classed, but not directly instantiated. To obtain an instance of this
	 * class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected SentenceFinalPuncRule(){}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(edu.mit.jsemcor.element.IToken, edu.mit.jsemcor.element.IToken)
	 */
	public String getSeparatorString(IToken one, IToken two) {
		// no space before
		if(Punc.isSentenceFinal(two)) return EMPTY;
		
		// if before a closing delimiter, no space
		if(Punc.isSentenceFinal(one) && Punc.isClosingDelimiter(two)) return EMPTY;
		
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		// no space before
		if(Punc.isSentenceFinal(two)) return EMPTY;
		
		// if before a closing delimiter, no space
		if(Punc.isSentenceFinal(one) && Punc.isClosingDelimiter(two)) return EMPTY;
		
		return null;
	}

}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.detokenize;

import edu.mit.jsemcor.element.IPunc;
import edu.mit.jsemcor.element.IToken;
import edu.mit.jsemcor.element.Punc;

/**
 * Implements a rule encoding the fact that there are spaces before but not
 * after opening parentheses, brackets, and quotes, and spaces after and not
 * before closing instances of the same. This class is thread-safe. To obtain an
 * instance of this class, call {@link #getInstance()}.
 * 
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class DelimiterRule implements ISeparatorRule {
	
	/**
	 * Static singleton instance of this class, accessed and initialized by
	 * {@link #getInstance()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static DelimiterRule instance;
	
	/**
	 * Returns (initializing if necessary) the static singleton instance of this
	 * class.
	 * 
	 * @return the static, singleton instance of this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static DelimiterRule getInstance(){
		if(instance == null) instance = new DelimiterRule();
		return instance;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * sub-classed, but not directly instantiated. To obtain an instance of this
	 * class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected DelimiterRule(){}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.IDetokenizer#getSeparatorString(edu.mit.jsemcor.element.IToken, edu.mit.jsemcor.element.IToken)
	 */
	public String getSeparatorString(IToken one, IToken two) {
		if(Punc.isClosingDelimiter(two)) return EMPTY;
		if(Punc.isOpeningDelimiter(one)) return EMPTY;
		if(Punc.isClosingDelimiter(one) && two instanceof IPunc) return EMPTY;
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		if(Punc.isClosingDelimiter(two)) return EMPTY;
		if(Punc.isOpeningDelimiter(one)) return EMPTY;
		if(Punc.isClosingDelimiter(one) && isNonAlphanumeric(two)) return EMPTY;
		return null;
	}
	
	/**
	 * Returns whether the specified string is all non-alphanumeric characters,
	 * as defined by {@link Character#isLetterOrDigit(char)}.
	 * 
	 * @return <code>true</code> if the specified string has no letters or
	 *         digits in it; <code>false</code> otherwise.
	 * @throws NullPointerException
	 *             if the specified string is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public static boolean isNonAlphanumeric(String s){
		for(int i = 0; i < s.length(); i++) if(Character.isLetterOrDigit(s.charAt(i))) return false;
		return true;
	}

}

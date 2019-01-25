/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.detokenize;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.mit.jsemcor.element.IToken;
import edu.mit.jsemcor.element.IWordform;
import edu.mit.jsemcor.element.Punc;
import edu.mit.jsemcor.term.POSTag;

/**
 * A separator rule encoding the fact that the string separating a word and it's
 * following apostrophe-s ('s) should not be separated by whitespace. This class
 * is thread-safe. To obtain an instance of this class, call
 * {@link #getInstance()}.
 *
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class ApostropheSRule implements ISeparatorRule {
	
	/**
	 * The apostrophe-s {@link String}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String apostropheS = "'s";
	
	private static final Set<POSTag> tags = new HashSet<POSTag>(Arrays.asList(POSTag.NN, POSTag.NNP, POSTag.NNPS, POSTag.NNS, POSTag.NP, POSTag.NPS));
	
	/**
	 * Static singleton instance of this class, accessed and initialized by
	 * {@link #getInstance()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static ApostropheSRule instance;
	
	/**
	 * Returns (initializing if necessary) the static singleton instance of this
	 * class.
	 * 
	 * @return the static, singleton instance of this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static ApostropheSRule getInstance(){
		if(instance == null) instance = new ApostropheSRule();
		return instance;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * sub-classed, but not directly instantiated. To obtain an instance of this
	 * class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected ApostropheSRule(){}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(edu.mit.jsemcor.element.IToken, edu.mit.jsemcor.element.IToken)
	 */
	public String getSeparatorString(IToken one, IToken two) {
		
		// catch instance of 's; they should be marked as possessives
		if(one instanceof IWordform && two instanceof IWordform){
			IWordform second = (IWordform)two;
			if(second.getPOSTag() == POSTag.POS) return EMPTY;
		} 
		
		// catch instances of s'; they will not be marked
		// properly, but we can guess
		if(one instanceof IWordform && two == Punc.APOSTROPHE){
			char c = one.getText().charAt(one.getText().length()-1);
			if((c == 's' || c == 'S') && tags.contains(((IWordform)one).getPOSTag())) return EMPTY;
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		
		// catch instance of 's
		if(two.equalsIgnoreCase(apostropheS)) return EMPTY;
		
		// try to catch instances of trailing apostrophe
		char c = one.charAt(one.length()-1);
		if((c == 's' || c == 'S') && two.equalsIgnoreCase("'")) return EMPTY;
		
		return null;
	}

}

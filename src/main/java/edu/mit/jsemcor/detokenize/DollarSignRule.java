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
import edu.mit.jsemcor.element.IWordform;
import edu.mit.jsemcor.element.Punc;
import edu.mit.jsemcor.term.IPOSTag;
import edu.mit.jsemcor.term.POSTag;

/**
 * Implements a rule encoding the fact that if a dollar sign is followed by an
 * adjective (JJ*) or a cardinal number (CD), there is no space between them,
 * e.g.,
 * 
 * <code>Vandiver likely will mention the $100 million highway bond issue approved earlier...</code>
 * (JJ, from br-a01), and
 * 
 * <code>The bill would increase from $5000000 to $15000000 the maximum loan the state could make...</code>
 * (CD, from br-a02). This class is thread-safe. To obtain an instance of this
 * class, call {@link #getInstance()}.
 *
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class DollarSignRule implements ISeparatorRule {
	
	/**
	 * Static singleton instance of this class, accessed and initialized by
	 * {@link #getInstance()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static DollarSignRule instance;
	
	/**
	 * Returns (initializing if necessary) the static singleton instance of this
	 * class.
	 * 
	 * @return the static, singleton instance of this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static DollarSignRule getInstance(){
		if(instance == null) instance = new DollarSignRule();
		return instance;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * sub-classed, but not directly instantiated. To obtain an instance of this
	 * class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected DollarSignRule(){}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(edu.mit.jsemcor.element.IToken, edu.mit.jsemcor.element.IToken)
	 */
	public String getSeparatorString(IToken one, IToken two) {
		if(one == Punc.DOLLAR_SIGN && two instanceof IWordform){
			IPOSTag tag = ((IWordform)two).getPOSTag();
			if(tag.getValue().startsWith(POSTag.JJ.getValue())){
				return EMPTY;
			} else if(tag.getValue().startsWith(POSTag.CD.getValue())){
				return EMPTY;
			}
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		return one.equalsIgnoreCase(Punc.DOLLAR_SIGN.getText()) ? EMPTY : null;
	}

}

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

/**
 * Creates a composite detokenizer with all the standard rules. If one of its
 * rules does not come up with an answer, it returns the default, a single
 * space.
 * 
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class DefaultDetokenizer extends CompositeDetokenizer {
	
	private static final long serialVersionUID = 4136617489598602262L;

	/**
	 * Returns a new instance of the default detokenizer, that can be modified
	 * by the caller.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public DefaultDetokenizer() {
		super(ApostropheSRule.getInstance(),
			  ContractionRule.getInstance(),
			  DelimiterRule.getInstance(),
			  DollarSignRule.getInstance(),
			  PhraseDelimiterRule.getInstance(),
			  SentenceFinalPuncRule.getInstance());
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.CompositeDetokenizer#getSeparatorString(edu.mit.jsemcor.element.IToken, edu.mit.jsemcor.element.IToken)
	 */
	@Override
	public String getSeparatorString(IToken one, IToken two) {
		String result = super.getSeparatorString(one, two);
		return result == null ? SPACE : result;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.CompositeDetokenizer#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		String result = super.getSeparatorString(one, two);
		return result == null ? SPACE : result;
	}

}

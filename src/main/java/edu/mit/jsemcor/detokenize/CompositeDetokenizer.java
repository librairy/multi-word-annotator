/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.detokenize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.mit.jsemcor.element.IToken;
import edu.mit.jsemcor.element.IWordform;

/**
 * An implementation of a detokenizer that is a composite of other detokenizers.
 * This class maintains a list of detokenizers, and when asked to produce a
 * separator string, queries each of it's children detokenizers in turn. The
 * first non-null answer is returned. Otherwise, it returns <code>null</code>
 * 
 * @author M.A. Finlayson
 * @version 1.63, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class CompositeDetokenizer extends ArrayList<ISeparatorRule> implements IDetokenizer, ISeparatorRule {

	private static final long serialVersionUID = -7507069458713258521L;

	/**
	 * Constructs an instance of this class with no separator rules.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public CompositeDetokenizer() {
		super();
	}

	/**
	 * Constructs an instance of this class with the specified separator rules.
	 * 
	 * @throws NullPointerException
	 *             if the collection of specified separator rules is
	 *             <code>null</code>, or the collection contains any
	 *             <code>null</code> values.
	 * @since JSemcor 1.0.0
	 */
	public CompositeDetokenizer(Collection<? extends ISeparatorRule> rules) {
		super(rules);
	}

	/**
	 * Constructs an instance of this class with the specified separator rules.
	 * 
	 * @throws NullPointerException
	 *             if the specified array or any of its elements is
	 *             <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public CompositeDetokenizer(ISeparatorRule... children) {
		this(Arrays.asList(children));
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.IDetokenizer#detokenizeStrings(java.util.List)
	 */
	public String detokenizeStrings(List<String> tokens) {
		StringBuilder sb = new StringBuilder();
		String lastToken = null, sepStr;
		for(String currToken : tokens){
			
			// add a separator string after the last token
			// if there is a last token
			if(lastToken != null){
				sepStr = getSeparatorString(lastToken, currToken);
				if(sepStr != null) sb.append(sepStr);
			} 
			
			// add the current token
			sb.append(currToken);
			lastToken = currToken;
		}
		return sb.toString();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.IDetokenizer#detokenize(java.util.List)
	 */
	public String detokenize(List<IToken> tokens) {
		
		StringBuilder sb = new StringBuilder();
		IToken lastToken = null;
		String lastSub, sepStr;
		for(IToken currToken : tokens){
			
			// add a separator string after the last token
			// if there is a last token
			if(lastToken != null){
				sepStr = getSeparatorString(lastToken, currToken);
				if(sepStr != null) sb.append(sepStr);
			} 
			
			// if the current token is a wordform, process its
			// constitutent tokens, if it has any
			if(currToken instanceof IWordform){
				lastSub = null;
				IWordform wf = (IWordform)currToken;
				for(String currSub : wf.getConstituentTokens()){
					if(lastSub != null){
						sepStr = getSeparatorString(lastSub, currSub);
						if(sepStr != null) sb.append(sepStr);
					}
					sb.append(currSub);
					lastSub = currSub;
				}
			} else {
				// otherwise, just add the text
				sb.append(currToken.getText());
			}
			lastToken = currToken;
		}
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.element.IDetokenizer#getSeparatorString(edu.mit.jsemcor.element.IToken,
	 *      edu.mit.jsemcor.element.IToken)
	 */
	public String getSeparatorString(IToken one, IToken two) {
		String result = null;
		for (ISeparatorRule detokenizer : this) {
			result = detokenizer.getSeparatorString(one, two);
			if (result != null) return result;
		}
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		String result = null;
		for (ISeparatorRule detokenizer : this) {
			result = detokenizer.getSeparatorString(one, two);
			if (result != null) return result;
		}
		return null;
	}
	
}

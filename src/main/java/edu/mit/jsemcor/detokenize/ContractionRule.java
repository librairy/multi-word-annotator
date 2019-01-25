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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.mit.jsemcor.element.IToken;

/**
 * Implements a rule encoding the fact that the contractions {@code 're},
 * {@code n't} and {@code 've} come directly after the previous token, with no
 * space. This class is thread-safe. To obtain an instance of this class, call
 * {@link #getInstance()}.
 * 
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class ContractionRule implements ISeparatorRule {
	
	/**
	 * The unmodifiable set of contractions used in this rule.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final Set<String> CONTRACTIONS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("'re", "n't", "'ve")));
	
	/**
	 * Static singleton instance of this class, accessed and initialized by
	 * {@link #getInstance()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static ContractionRule instance;
	
	/**
	 * Returns (initializing if necessary) the static singleton instance of this
	 * class.
	 * 
	 * @return the static, singleton instance of this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static ContractionRule getInstance(){
		if(instance == null) instance = new ContractionRule();
		return instance;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * sub-classed, but not directly instantiated. To obtain an instance of this
	 * class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected ContractionRule(){}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(edu.mit.jsemcor.element.IToken, edu.mit.jsemcor.element.IToken)
	 */
	public String getSeparatorString(IToken one, IToken two) {
		return getSeparatorString(one.getText(), two.getText());
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.detokenize.ISeparatorRule#getSeparatorString(java.lang.String, java.lang.String)
	 */
	public String getSeparatorString(String one, String two) {
		return CONTRACTIONS.contains(two.toLowerCase()) ? EMPTY : null;
	}

}

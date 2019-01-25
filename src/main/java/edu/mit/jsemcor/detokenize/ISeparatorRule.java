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
 * Classes that implement this interface provide the information necessary to
 * re-concatenating tokens into properly formatted text.
 * 
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ISeparatorRule {

	/**
	 * Convenience field that holdS the empty string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String EMPTY = "";

	/**
	 * Convenience field that hold the space string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String SPACE = " ";

	/**
	 * Returns the separator string, if any, that should be placed between the
	 * text of these two strings. If this rule cannot calculate what the string
	 * should be between these two strings, it returns <code>null</code>. If
	 * it knows there should be no characters between these two strings, it
	 * should return the empty string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getSeparatorString(String one, String two);

	/**
	 * Returns the separator string, if any, that should be placed between the
	 * text of these two tokens. If this rule cannot calculate what the string
	 * should be between these two tokens, it returns <code>null</code>. If
	 * it knows there should be no characters between these two tokens, it
	 * should return the empty string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getSeparatorString(IToken one, IToken two);

}

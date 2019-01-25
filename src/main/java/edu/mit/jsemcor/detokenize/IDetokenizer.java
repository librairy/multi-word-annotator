/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.detokenize;

import java.util.List;

import edu.mit.jsemcor.element.IToken;

/**
 * A detokenizer is a class that can transform a list of tokens into a
 * properly-whitespace delimited string. For example, given the list of tokens
 * {@code [John, 's, dog, ran, .]}, it would return the string
 * <tt>John's dog ran.</tt>, with spaces inserted after the third and fourth
 * tokens, but not the others. A detokenizer can operate over just lists of
 * strings or lists of {@link IToken} objects.
 * 
 * @author M.A. Finlayson
 * @version 1.56, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IDetokenizer {

	/**
	 * Transforms the list of tokens into a properly whitespace-separated
	 * string.
	 * 
	 * @return A string containing all the tokens properly separated by
	 *         whitespace, according to normal typographic conventions.
	 * @throws NullPointerException
	 *             if the specified list is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public String detokenize(List<IToken> tokens);

	/**
	 * Transforms the list of tokens into a properly whitespace-separated
	 * string.
	 * 
	 * @return A string containing all the tokens properly separated by
	 *         whitespace, according to normal typographic conventions.
	 * @throws NullPointerException
	 *             if the specified list is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public String detokenizeStrings(List<String> tokens);

}

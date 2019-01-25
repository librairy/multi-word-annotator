/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWEDesc.IPart;

/**
 * A multi-word expression found in a list of tokens.
 * 
 * @param <T>
 *            type of {@link IToken} objects that form the multi-word expression
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: IMWE.java 341 2011-09-26 21:03:51Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWE<T extends IToken> extends IHasForm {

	/**
	 * Gets the list of tokens identified as comprising the multi-word
	 * expression. The order of the tokens should correspond to the order of the
	 * words in the multi-word expression. This method should never return
	 * <code>null</code> or an empty list.
	 * 
	 * @return the non-<code>null</code>, non-empty list of tokens that comprise
	 *         the multi-word expression.
	 * @since jMWE 1.0.0
	 */
	public List<T> getTokens();

	/**
	 * Gets the MWE description object corresponding to this multi-word
	 * expression. Useful for retrieving the the lemma, list of parts, and part
	 * of speech of the multi-word expression. This method should never return
	 * <code>null</code>.
	 * 
	 * @return the non-<code>null</code> MWE description corresponding to the
	 *         multi-word expression represented by this object.
	 * @since jMWE 1.0.0
	 */
	public IMWEDesc getEntry();

	/**
	 * Gets the mapping from tokens to parts in this multi-word expression.
	 * Useful when determining which token corresponds to which part in the
	 * expression, especially when some parts of the expression are repeated or
	 * if the tokens are not in the canonical order. This method should never
	 * return <code>null</code>. Iteration order of the map should correspond to
	 * the order of tokens in the original sentence.
	 * 
	 * @return the non-<code>null</code> map from tokens to parts in this MWE
	 *         object
	 * @since jMWE 1.0.0
	 */
	public Map<T, IPart> getPartMap();

	/**
	 * Returns <code>true</code> if this MWE is inflected relative to its
	 * associated MWE description; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this MWE is inflected relative to its
	 *         associated MWE description; <code>false</code> otherwise.
	 * @since jMWE 1.0.0
	 */
	public boolean isInflected();

	public Long getOffset();

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Stores MWEs under the type of error they make.
 * 
 * @param <T>
 *            the token type
 * @author N. Kulkarni
 * @version $Id: IErrorResult.java 327 2011-05-08 21:13:58Z markaf $
 * @since jMWE 1.0.0
 */
public interface IErrorResult<T extends IToken> {

	/**
	 * Returns the total number of errors for the result. Will always be zero or
	 * greater.
	 * 
	 * @return the total number of errors for the result, always non-negative.
	 * @since jMWE 1.0.0
	 */
	public int getTotalErrors();

	/**
	 * Gets the number of MWEs that fall under the given error class.
	 * 
	 * @param id
	 *            the ID of the error class.
	 * @return the number of multi-word expression that fall under the given
	 *         error class and zero if this error class is not represented in
	 *         the error result.
	 * @since jMWE 1.0.0
	 */
	public int getNumErrors(String id);

	/**
	 * Returns a {@link Map} that stores multi-word expressions under the ID of
	 * the error class they belong to. Should never be <code>null</code>.
	 * 
	 * @return a non-null {@link Map} that stores multi-word expressions under
	 *         the ID of the error class they belong to.
	 * @since jMWE 1.0.0
	 */
	public Map<String, List<IMWE<T>>> getDetails();

}

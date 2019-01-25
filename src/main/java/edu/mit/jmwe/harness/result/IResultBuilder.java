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

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.IMarkedSentence;

/**
 * Classes implementing this interface build an {@link IOverallResult} object.
 * These classes store the sentence result objects collected from running a
 * test harness over a group of {@link IMarkedSentence} objects and carry out
 * all the calculations needed to build the final result.
 * 
 * @param <T>
 *            the type of tokens in the unit
 * @param <S>
 *            the type of sentence the harness runs over. Is parameterized by tokens
 *            of type T.
 * @author M.A. Finlayson
 * @author Nidhi Kulkarni
 * @version $Id: IResultBuilder.java 327 2011-05-08 21:13:58Z markaf $
 * @since jMWE 1.0.0
 */
public interface IResultBuilder<T extends IToken, S extends IMarkedSentence<T>>{

	/**
	 * Updates the internal data stored in this builder by comparing the
	 * multi-word expressions found by an MWE detector to the answer
	 * multi-word expressions.
	 * 
	 * @param found
	 *            A non-null list of multi-word expressions found by an
	 *            IMWEDetector.
	 * @param answers
	 *            A non-null list of answer multi-word expressions
	 * @throws NullPointerException
	 *             if either list is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public void process(List<IMWE<T>> found, List<IMWE<T>> answers);

	/**
	 * Stores the results for a unit under its ID.
	 * 
	 * @param id
	 *            the non-null identification String for the unit
	 * @param detail
	 *            the unit result. May not be <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public void addDetail(String id, ISentenceResult<T, S> detail);

	/**
	 * Creates a result from the data stored in this builder. Should not return
	 * <code>null</code>.
	 * 
	 * @return a non-null result that contains the data stored in this builder.
	 * @since jMWE 1.0.0
	 */
	public IOverallResult<T, S> createResult();
	

}

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
 * Contains results for one {@link IMarkedSentence} object. Contains a list of
 * false positives, false negatives, and true positives as well as the precision
 * and recall for that sentence.
 * 
 * @param <T>
 *            the type of tokens contained in the sentence.
 * @param <S>
 *            the type of sentence whose results are stored. Is parameterized by
 *            tokens of type T.
 * @author Nidhi Kulkarni
 * @version $Id: ISentenceResult.java 321 2011-05-06 23:44:50Z markaf $
 * @since jMWE 1.0.0
 */
public interface ISentenceResult<T extends IToken, S extends IMarkedSentence<T>> extends IBaseResult {
	
	/**
	 * Returns the sentence corresponding to this result.
	 * 
	 * @return the sentence corresponding to this result.
	 * @since jMWE 1.0.0
	 */
	public S getSentence();

	/**
	 * Returns a list of the false positives. Should return the empty list if
	 * there are no false positives. Should never return <code>null</code>.
	 * 
	 * @return a list of false positives. Should return the empty list if there
	 *         are no false positives. Should never return <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public List<IMWE<T>> getFalsePositives();

	/**
	 * Returns a list of the false negatives. Should return the empty list if
	 * there are no false negatives. Should never return <code>null</code>.
	 * 
	 * @return a list of false negatives. Should return the empty list if there
	 *         are no false negatives. Should never return <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public List<IMWE<T>> getFalseNegatives();

	/**
	 * Returns a list of the true positives. Should return the empty list if
	 * there are no true positives. Should never return <code>null</code>.
	 * 
	 * @return a list of true positives. Should return the empty list if there
	 *         are no true positives. Should never return <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public List<IMWE<T>> getTruePositives();

	/**
	 * Returns the answer multi-word expression in the sentence. Should return
	 * the empty list if there are no answer multi-word expressions. Should
	 * never return <code>null</code>.
	 * 
	 * @return a list of the answer multi-word expression in the sentence.
	 *         Should return the empty list if there are no answer multi-word
	 *         expression. Should never return <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public List<IMWE<T>> getAnswers();

	/**
	 * Returns the multi-word expression found by the detector in the sentence.
	 * Should return the empty list if the detector found no multi-word
	 * expressions. Should never return <code>null</code>.
	 * 
	 * @return a list of the multi-word expression found by the detector in the
	 *         sentence. Should return the empty list if the detector found no
	 *         multi-word expressions. Should never return <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public List<IMWE<T>> getFound();

}

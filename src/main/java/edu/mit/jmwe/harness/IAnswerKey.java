/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness;

import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.IMarkedSentence;

/**
 * Classes implementing this interface find the correct multi-word expressions
 * for an <code>IMarkedSentence</code> object. The resulting list of multi-word
 * expressions is used as an answer key that other test lists can be compared
 * against.
 * 
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: IAnswerKey.java 319 2011-05-06 20:22:54Z markaf $
 * @since jMWE 1.0.0
 */
public interface IAnswerKey {

	/**
	 * Gets the answer multi-word expressions from the given sentence. If there
	 * are no answers, should return the empty list. Should never return
	 * <code>null</code>.
	 * 
	 * @param <T>
	 *            type of tokens that are contained in the sentence.
	 * @param sentence
	 *            the sentence for which the answers should be retrieved May not
	 *            be <code>null</code>.
	 * @return a non-null, possibly empty list of answer multi-word expressions
	 *         for the given sentence
	 * @throws NullPointerException
	 *             if the unit is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken> List<IMWE<T>> getAnswers(IMarkedSentence<T> sentence);
	
}

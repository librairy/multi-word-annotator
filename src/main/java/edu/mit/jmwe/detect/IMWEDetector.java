/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * A detector for multi-word expressions. Classes implementing this interface
 * can be used to find the multi-word expressions in a sentence.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: IMWEDetector.java 308 2011-05-06 03:33:34Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWEDetector {

	/**
	 * Given a list of tokens, the detector searches for the MWEs in the list.
	 * It returns a set of <code>IMWE</code> objects representing these
	 * multi-word expressions. The method returns an empty list if no MWEs are
	 * found; the method should never return <code>null</code>.
	 * 
	 * @param <T>
	 *            the type of the tokens in the sentence
	 * @param sentence
	 *            a sentence which the detector should search for multi-word
	 *            expressions.
	 * @return a list of <code>IMWE</code> objects representing the multi-word
	 *         expressions found in the sentence. Returns an empty list if no
	 *         multi-word expressions are found; never returns <code>null</code>
	 * @throws NullPointerException
	 *             if the specified sentence is <code>null</code>, or contains
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified sentence is empty
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence);

}

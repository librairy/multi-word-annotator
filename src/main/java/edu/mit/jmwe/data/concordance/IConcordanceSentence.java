/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data.concordance;

import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jsemcor.element.IContextID;

/**
 * A sentence drawn from the Semcor corpus.
 * <p>
 * This class requires JSemcor to be on the classpath.
 * 
 * @author M.A. Finlayson
 * @version $Id: IConcordanceSentence.java 326 2011-05-08 20:21:37Z markaf $
 * @since jMWE 1.0.0
 */
public interface IConcordanceSentence extends IMarkedSentence<IConcordanceToken> {

	/**
	 * Returns the context id from which this sentence was drawn. May not return
	 * <code>null</code>.
	 * 
	 * @return the non-<code>null</code> context id from which this sentence was
	 *         drawn.
	 * @since jMWE 1.0.0
	 */
	public IContextID getContextID();

	/**
	 * Returns the sentence number of this sentence in the specified Semcor
	 * context.
	 * 
	 * @return the sentence number of this sentence in the specified Semcor
	 *         context.
	 * @since jMWE 1.0.0
	 */
	public int getSentenceNumber();

}

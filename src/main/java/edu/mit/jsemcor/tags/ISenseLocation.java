/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.element.ISentence;
import edu.mit.jsemcor.element.IWordform;

/**
 * Indicates that this object points to the location of a sense, namely, the
 * context file, sentence, and word where the sense can be found.
 *
 * @author M.A. Finlayson
 * @version 1.74, 25 Jan 2009
 * @since JSemcor 1.0.0
 */
public interface ISenseLocation extends IHasSense {

	/**
	 * Returns the context id associated with this sense location
	 * 
	 * @return the context id associated with this sense location
	 * @since JSemcor 1.0.0
	 */
	public IContextID getContextID();

	/**
	 * Returns the sentence number associated with this sense location. Sentence
	 * numbers correspond to the number of the {@link ISentence} found in the
	 * context object. The first sentence in each context is numbered 1, and
	 * sentence numbers are incremented sequentially throughout the context.
	 * 
	 * @return the sentence number associated with this sense location
	 * @since JSemcor 1.0.0
	 */
	public int getSentenceNumber();

	/**
	 * Returns the word number associated with this sense location. Word numbers
	 * are sequentially assigned to the {@link IWordform} objects found in an
	 * {@link ISentence}, beginning with 1. Note that punctuation tokens are not counted
	 * when determining the value of the word number.
	 * 
	 * @return the word number associated with this sense location
	 * @since JSemcor 1.0.0
	 */
	public int getWordNumber();

}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.List;

/**
 * Indicates that this object represents a sentence in a concordance context
 * file.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ISentence extends List<IToken>, IElement {

	/**
	 * The first part of the tag that marks the beginning of a sentence.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_SENT_LEFT = "<s";

	/**
	 * The unadorned tag that marks the beginning of a sentence.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_SENT_NAME = TAG_SENT_LEFT
			+ RIGHT_ANGLE_BRACKET;

	/**
	 * The tag that marks the end of a sentence.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_SENT_RIGHT = "</s>";

	/**
	 * The attributes that marks the number of a sentence.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_SNUM = "snum";

	/**
	 * Gets the number of this sentence. Will always be 1 or greater.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public int getNumber();

	/**
	 * Gets the word with the specified number, or <code>null</code> if there
	 * is no such word.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public IWordform getWord(int number);

	/**
	 * Returns an unmodifiable list of word forms contained in this sentence, in
	 * increasing word number order.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<IWordform> getWordList();

	/**
	 * Returns a non-null, though possibly empty, list of lists of word forms,
	 * corresponding to the set of multi-wordform collocations that are declared
	 * in the underlying context data. By 'declared', it is meant that for every
	 * list of wordforms L in the list returned, there are at least two
	 * wordforms in each list, and the following property holds: for all i > 0
	 * and also a valid list index, L.get(0).getWordIndex() ==
	 * L.get(i).getWordIndex + L.get(i).getDistance() In other words, every
	 * wordform after the first refers back to the first wordform with it's
	 * 'distance' parameter.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<List<IWordform>> getCollocations();

}

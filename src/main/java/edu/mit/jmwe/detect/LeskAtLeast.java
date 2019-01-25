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
import edu.mit.jmwe.detect.score.IScorer;
import edu.mit.jmwe.detect.score.LeskScore;
import edu.mit.jwi.IDictionary;

/**
 * A filter detector that throws out MWEs whose lesk score is less than some value.
 *
 * @author M.A. Finlayson
 * @version $Id: LeskAtLeast.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class LeskAtLeast extends FilterByScore {
	
	// final instance fields
	protected final int minScore;
	protected final IDictionary dict;

	/**
	 * Constructs a new filter that filters out MWEs whose lesk score is not at
	 * least some value.
	 * 
	 * @param detector
	 *            the wrapped detector, may not be <code>null</code>
	 * @param dict
	 *            the dictionary to use, may not be <code>null</code>
	 * @param minScore
	 *            the minimum score to use. If the specified score is less than
	 *            zero, the score used is zero.
	 * @throws NullPointerException
	 *             if the detector or dictionary are <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public LeskAtLeast(IMWEDetector detector, IDictionary dict, int minScore) {
		super(detector);
		if(dict == null)
			throw new NullPointerException();
		this.dict = dict;
		this.minScore = Math.max(minScore, 0);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.FilterByScore#isGoodScore(double)
	 */
	@Override
	protected boolean isGoodScore(double score) {
		return score >= minScore;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.FilterByScore#getScorer(java.util.List)
	 */
	@Override
	protected <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence) {
		return new LeskScore<T>(sentence, dict);
	}

}

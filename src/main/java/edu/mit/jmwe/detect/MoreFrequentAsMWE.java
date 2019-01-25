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
import edu.mit.jmwe.detect.score.FractionAsMWEScore;
import edu.mit.jmwe.detect.score.IScorer;

/**
 * Filters the results of its backing detector to include only those MWEs whose
 * parts, in a continuous run of tokens, are more often marked as an MWE than as
 * individual tokens.
 * 
 * @author M.A. Finlayson
 * @version $Id: MoreFrequentAsMWE.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class MoreFrequentAsMWE extends FilterByScore {

	/**
	 * Constructs the detector from the given backing detector.
	 * 
	 * @param detector
	 *            the IMWEDetector that will be used to back this detector. May
	 *            not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the backing detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public MoreFrequentAsMWE(IMWEDetector detector) {
		super(detector);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.FilterByScore#isGoodScore(double)
	 */
	@Override
	protected boolean isGoodScore(double score) {
		return score > 0.5;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.FilterByScore#getScorer(java.util.List)
	 */
	@Override
	protected <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence) {
		return FractionAsMWEScore.getInstance();
	}

}

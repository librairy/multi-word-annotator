/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.score.IScorer;

/**
 * Removes all MWEs from the wrapped detector's results whose score is
 * "no good", where "no good" is implementation dependent.
 * 
 * @author M.A. Finlayson
 * @version $Id: FilterByScore.java 308 2011-05-06 03:33:34Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class FilterByScore extends HasMWEDetector implements IMWEDetectorFilter {
	
	/**
	 * Constructs a new MWE detector constraint that wraps the specified MWE Detector.
	 * 
	 * @param detector
	 *            the detector wrapped by this object
	 * @throws NullPointerException
	 *             if the specified detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public FilterByScore(IMWEDetector detector) {
		super(detector);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// raw results
		List<IMWE<T>> results = super.detect(sentence);
		
		// filter
		IScorer<IMWE<T>> scorer = getScorer(sentence);
		for(Iterator<IMWE<T>> i = results.iterator(); i.hasNext(); )
			if(!isGoodScore(scorer.score(i.next())))
				i.remove();
			
		// return results
		return new ArrayList<IMWE<T>>(results);
	}

	/**
	 * Returns <code>true</code> if the score passes the filter (i.e., the
	 * scored MWE should be kept); <code>false</code> otherwise
	 * 
	 * @param score
	 *            the score to be checked
	 * @return <code>true</code> if the score passes the filter (i.e., the
	 *         scored MWE should be kept); <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	protected abstract boolean isGoodScore(double score);

	/**
	 * Returns a scoring function for the specified sentence.
	 * 
	 * @param <T>
	 *            the type of token in the sentence
	 * @param sentence
	 *            the sentence for which the scorer should be constructed
	 * @return a scorer for MWEs in the specified sentence
	 * @since jMWE 1.0.0
	 */
	protected abstract <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence);
	
}

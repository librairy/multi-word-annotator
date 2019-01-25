/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect.score;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEComparator;

/**
 * Scores each MWE with its index variance.
 * 
 * @param <T>
 *            the type of token used by this scorer
 * @author M.A. Finlayson
 * @version $Id: VarianceScore.java 308 2011-05-06 03:33:34Z markaf $
 * @since jMWE 1.0.0
 */
public class VarianceScore<T extends IToken> extends AbstractScorer<IMWE<T>> {
	
	// final instance fields
	protected final Map<T, Integer> idxMap;

	/**
	 * Constructs a new variance scorer for the specified sentence
	 * 
	 * @param sentence
	 *            the sentence for the scorer
	 * @since jMWE 1.0.0
	 */
	public VarianceScore(List<T> sentence){
		this.idxMap = Collections.unmodifiableMap(MWEComparator.createIndexMap(sentence));
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.score.IScorer#score(java.lang.Object)
	 */
	public double score(IMWE<T> mwe) {
		int sum = 0;
		double mean = 0.0;
		double variance = 0.0;
		
		//calculate the mean token index
		for (T token : mwe.getTokens())
			sum += idxMap.get(token);
		mean = sum / mwe.getTokens().size();
		
		//calculate the variance
		for (T token : mwe.getTokens())
			variance += Math.pow(idxMap.get(token) - mean, 2);
		
		return variance / mwe.getTokens().size();
	}

}

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
import edu.mit.jmwe.detect.score.VarianceScore;

/**
 * A detector resolver that chooses the MWE with the smallest variance.
 *
 * @author M.A. Finlayson
 * @version $Id: SmallestVariance.java 317 2011-05-06 20:05:20Z markaf $
 * @since jMWE 1.0.0
 */
public class SmallestVariance extends ResolveByScore {

	/**
	 * A new resolver that wraps the specified detector.
	 * 
	 * @param detector
	 *            the wrapped detector, may not be <code>null</code>
	 * @throws NullPointerException
	 *             if the specified detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public SmallestVariance(IMWEDetector detector) {
		super(detector, false);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.ResolveByScore#getScorer(java.util.List)
	 */
	@Override
	protected <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence) {
		return new VarianceScore<T>(sentence);
	}

}

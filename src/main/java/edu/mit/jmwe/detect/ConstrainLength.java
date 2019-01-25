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
import edu.mit.jmwe.detect.score.LengthScore;

/**
 * A detector filter that constrains MWEs by length.
 *
 * @author M.A. Finlayson
 * @version $Id$
 * @since jMWE 1.0.0
 */
public class ConstrainLength extends FilterByScore {
	
	// final instance fields
	protected final int length;
	protected final boolean isMaxLength;

	/**
	 * Construct a new filter on top of the specified detector
	 * 
	 * @param detector
	 *            the wrapped detector; may not be <code>null</code>
	 * @param length
	 *            the length limit; either max or min depending on the
	 *            <code>isMaxLength</code> flag
	 * @param isMaxLength
	 *            <code>true</code> if the all MWEs with lengths greater than
	 *            the specified length are to be discarded; <code>false</code>
	 *            otherwise
	 * @throws NullPointerException
	 *             if the detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ConstrainLength(IMWEDetector detector, int length, boolean isMaxLength) {
		super(detector);
		this.length = Math.max(0, length);
		this.isMaxLength = isMaxLength;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.FilterByScore#isGoodScore(double)
	 */
	@Override
	protected boolean isGoodScore(double score) {
		return isMaxLength ?
				score <= length : 
					score > length;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.FilterByScore#getScorer(java.util.List)
	 */
	@Override
	protected <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence) {
		return LengthScore.getInstance();
	}

}

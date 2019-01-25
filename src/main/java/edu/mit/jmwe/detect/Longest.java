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
 * A detector resolver that chooses the longest MWEs in a set of conflicts.
 * 
 * @author M.A. Finlayson
 * @version $Id: Longest.java 317 2011-05-06 20:05:20Z markaf $
 * @since jMWE 1.0.0
 */
public class Longest extends ResolveByScore {

	/**
	 * Constructs a new instance of this detector.
	 * 
	 * @param detector
	 *            the wrapped detector; may not be <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public Longest(IMWEDetector detector) {
		super(detector, true);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.ResolveByScore#getScorer(java.util.List)
	 */
	@Override
	protected <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> scorer) {
		return LengthScore.getInstance();
	}

}

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
import edu.mit.jmwe.detect.score.StartingIndexScore;

/**
 * A resolver that chooses the leftmost MWE.
 *
 * @author M.A. Finlayson
 * @version $Id: Leftmost.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
public class Leftmost extends ResolveByScore {

	/**
	 * Constructs a new resolver that wraps the specified detector
	 * 
	 * @param detector
	 *            the wrapped detector; may not be <code>null</code>
	 * @throws NullPointerException
	 *             if the specified detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public Leftmost(IMWEDetector detector) {
		super(detector, false);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.ResolveByScore#getScorer(java.util.List)
	 */
	@Override
	protected <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence) {
		return new StartingIndexScore<T>(sentence);
	}

}

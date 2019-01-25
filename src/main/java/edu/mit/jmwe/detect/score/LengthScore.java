/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect.score;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Scores a MWE with its length
 * 
 * @param <T>
 *            the type of token used by this scorer
 * @author M.A. Finlayson
 * @version $Id: LengthScore.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class LengthScore<T extends IToken> extends AbstractScorer<IMWE<T>> {
	
	//the singleton instance
	private static LengthScore<?> instance = null;
	
	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @param <T>
	 *            the type of token used by this scorer
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IToken> LengthScore<T> getInstance() {
		if (instance == null)
			instance = new LengthScore<IToken>();
		return (LengthScore<T>)instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected LengthScore() {}
	
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.score.IScorer#score(java.lang.Object)
	 */
	public double score(IMWE<T> mwe) {
		return mwe.getTokens().size();
	}

}

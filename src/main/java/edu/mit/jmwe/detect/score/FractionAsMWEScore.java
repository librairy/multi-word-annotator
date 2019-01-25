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
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IToken;

/**
 * A scorer that scores with the fraction of times it appears marked as an MWE,
 * as opposed to a run of unmarked tokens.
 * 
 * @param <T>
 *            the type of token used by this scorer
 * @author M.A. Finlayson
 * @version $Id: FractionAsMWEScore.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class FractionAsMWEScore<T extends IToken> extends AbstractScorer<IMWE<T>> {
	
	//the singleton instance
	private static FractionAsMWEScore<?> instance = null;
	
	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @param <T>
	 *            the type of token used by this scorer
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IToken> FractionAsMWEScore<T> getInstance() {
		if (instance == null)
			instance = new FractionAsMWEScore<IToken>();
		return (FractionAsMWEScore<T>)instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected FractionAsMWEScore() {}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.score.IScorer#score(java.lang.Object)
	 */
	public double score(IMWE<T> mwe) {
		IMWEDesc desc = mwe.getEntry();
		double numer = desc.getMarkedContinuous();
		double denom = numer + desc.getUnmarkedExact();
		if(Double.compare(denom, 0.0) == 0)
			return 1.0;
		return numer / denom;
	}

}

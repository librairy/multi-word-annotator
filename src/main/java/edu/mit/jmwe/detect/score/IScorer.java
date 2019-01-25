/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect.score;

import java.util.Comparator;

/**
 * A scorer provides a score for an object.
 *
 * @author M.A. Finlayson
 * @version $Id: IScorer.java 308 2011-05-06 03:33:34Z markaf $
 * @since jMWE 1.0.0
 */
public interface IScorer<T> extends Comparator<T> {

	/**
	 * Score the specified object. The object may be <code>null</code>,
	 * depending on the implementation.
	 * 
	 * @param obj
	 *            the object to be scored
	 * @return the score
	 * @since jMWE 1.0.0
	 */
	public double score(T obj);

}

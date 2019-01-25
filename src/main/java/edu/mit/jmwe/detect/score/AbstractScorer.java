/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect.score;

/**
 * Base class for scorers that provides the comparator functionality.
 * 
 * @param <T>
 *            the type of object used by this scorer
 * @author M.A. Finlayson
 * @version $Id: AbstractScorer.java 308 2011-05-06 03:33:34Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class AbstractScorer<T> implements IScorer<T> {

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(T o1, T o2) {
		return Double.compare(score(o1), score(o2));
	}


}

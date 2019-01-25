/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Arrays;

import edu.mit.jmwe.index.IMWEIndex;

/**
 * Returns a composite detector backed by the {@link Exhaustive} and
 * {@link StopWords} detection strategies.
 * 
 * @author N. Kulkarni
 * @version $Id: TrulyExhaustive.java 317 2011-05-06 20:05:20Z markaf $
 * @since jMWE 1.0.0
 */
public class TrulyExhaustive extends CompositeDetector {

	/**
	 * Constructs a new detector that looks up everything, stop words included.
	 * 
	 * @param index
	 *            the index to be used by this detector; may not be
	 * @since jMWE 1.0.0
	 */
	public TrulyExhaustive(IMWEIndex index) {
		super(Arrays.asList(new Exhaustive(index), new StopWords()));
	}

}

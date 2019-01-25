/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.io.IOException;
import java.util.Arrays;

import edu.mit.jmwe.index.IMWEIndex;

/**
 * A baseline implementation of the <code>IMWEDetector</code> interface. Runs
 * both the {@link Exhaustive} and {@link ProperNouns} detectors to find MWEs in a
 * sentence.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: Baseline.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class Baseline extends CompositeDetector {

	/**
	 * Constructs the BaselineDetector from a {@link Exhaustive} and
	 * {@link ProperNouns}.
	 * 
	 * @param index
	 *            the MWE index that will back the simple lookup detector.
	 * @throws NullPointerException
	 *             if the index is <code>null</code>
	 * @throws IOException
	 *             if an IOException occurs when opening or reading from the
	 *             index.
	 * @since jMWE 1.0.0
	 */
	public Baseline(IMWEIndex index) throws IOException {
		super(Arrays.asList(new ProperNouns(), new Exhaustive(index)));
	}
	
}

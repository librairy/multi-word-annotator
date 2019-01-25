/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.LinkedList;
import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Abstract base class for MWE Detectors that wrap, and constraint the output
 * of, other MWE detectors.
 * 
 * @author M.A. Finlayson
 * @version $Id: HasMWEDetector.java 308 2011-05-06 03:33:34Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class HasMWEDetector implements IHasMWEDetector {

	// final instance field
	private final IMWEDetector detector;

	/**
	 * Constructs a new MWE detector constraint that wraps the specified MWE Detector.
	 * 
	 * @param detector
	 *            the detector wrapped by this object
	 * @throws NullPointerException
	 *             if the specified detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public HasMWEDetector(IMWEDetector detector) {
		if(detector == null)
			throw new NullPointerException();
		this.detector = detector;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetectorConstraint#getWrappedDetector()
	 */
	public IMWEDetector getWrappedDetector() {
		return detector;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		List<IMWE<T>> results = getWrappedDetector().detect(sentence);
		return new LinkedList<IMWE<T>>(results);
	}

}

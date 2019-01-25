/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

/**
 * A MWE Detector that wraps another MWE detector.
 * 
 * @author M.A. Finlayson
 * @version $Id: IHasMWEDetector.java 309 2011-05-06 03:53:23Z markaf $
 * @since jMWE 1.0.0
 */
public interface IHasMWEDetector {

	/**
	 * Returns the MWE detector associated with this contraint detector.
	 * 
	 * @return the MWE detector associated with this object
	 * @since jMWE 1.0.0
	 */
	public IMWEDetector getWrappedDetector();

}

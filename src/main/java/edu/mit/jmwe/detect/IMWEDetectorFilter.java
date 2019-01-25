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
 * An interface for MWE detectors that act as filters for other MWE detectors
 * 
 * @author M.A. Finlayson
 * @version $Id: IMWEDetectorFilter.java 301 2011-05-05 23:07:33Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWEDetectorFilter extends IMWEDetector, IHasMWEDetector {

}

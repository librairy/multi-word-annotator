/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result.error;


/**
 * This error detector looks for several types of common errors made by MWE detectors.
 * 
 * @author N. Kulkarni
 * @version $Id: CommonErrorDetector.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class CommonErrorDetector extends CompositeErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.common";

	// the singleton instance
	private static CommonErrorDetector instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static CommonErrorDetector getInstance() {
		if (instance == null)
			instance = new CommonErrorDetector();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected CommonErrorDetector() {
		super(PNounLong.getInstance(), 
			  PNounShort.getInstance(),
			  WrongPOS.getInstance(),
			  AllStopWords.getInstance(), 
			  UntaggedPNoun.getInstance(),
			  ExtraPrep.getInstance(),
			  ExtraPOS.getInstance(),
			  OutOfOrder.getInstance(),
			  InflectionError.getInstance(),
			  InterstitialTokens.getInstance(),
			  InflectionPatternError.getInstance(),
			  VBDVBN.getInstance()
		);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.CompositeErrorDetector#getID()
	 */
	@Override
	public String getID() {
		return ID;
	}

}

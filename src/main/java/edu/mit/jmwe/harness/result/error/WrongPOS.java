/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result.error;

import java.util.ArrayList;
import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Finds the multi-word expressions detected by the {@link IMWEDetector} that
 * have the right tokens but the wrong part of speech.
 * 
 * @author N. Kulkarni
 * @version $Id: WrongPOS.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class WrongPOS extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.WrongPOS";
	
	// the singleton instance
	private static WrongPOS instance = null;
	
	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static WrongPOS getInstance() {
		if (instance == null)
			instance = new WrongPOS();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected WrongPOS() {
		super(ID);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, U extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, U> result) {
		List<IMWE<T>> mwes = new ArrayList<IMWE<T>>();
		
		IMWE<T> test = null;
		for(IMWE<T> mwe : result.getFalseNegatives()){
			// first find the false positive MWE that has same tokens as false negative
			for(IMWE<T> fp : result.getFalsePositives())
				if(MWE.overlap(fp, mwe) == 1){
					test = fp;
					break;
				}
			if(test != null && !test.getEntry().getPOS().equals(mwe.getEntry().getPOS()))
				mwes.add(test);
		}
		
		return new ErrorResult<T>(getID(), mwes);
	}
}

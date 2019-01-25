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
import edu.mit.jmwe.data.StopWords;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Finds the multi-word expressions the detector failed to identify whose parts
 * are all stop words.
 * 
 * @author N. Kulkarni
 * @version $Id: AllStopWords.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class AllStopWords extends AbstractErrorDetector {

	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.AllStopWords";

	// the singleton instance
	private static AllStopWords instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static AllStopWords getInstance() {
		if (instance == null) 
			instance = new AllStopWords();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 * 
	 * @since jMWE 1.0.0
	 */
	protected AllStopWords() {
		super(ID);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, S> result) {
		List<IMWE<T>> mwes = new ArrayList<IMWE<T>>();
		for(IMWE<T> mwe : result.getFalseNegatives())
			if(StopWords.hasAllStopWords(mwe.getEntry().getParts()))
				mwes.add(mwe);
		return new ErrorResult<T>(this.getID(), mwes);
	}

}

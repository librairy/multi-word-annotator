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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;
import edu.mit.jmwe.harness.result.SentenceResult;

/**
 * Counts and stores those MWEs that were not found by the backing detector.
 * Used to identify those MWEs that two detectors disagree on.
 * 
 * @author N. Kulkarni
 * @version $Id: DetectorDisagreement.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class DetectorDisagreement extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.Disagree";
	
	// final instance field
	protected final IMWEDetector d;

	/**
	 * Constructs a new error detector that uses the specified MWE detector for
	 * backing.
	 * 
	 * @param d
	 *            the detector used by this error detector
	 * @throws NullPointerException
	 *             if the specified detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public DetectorDisagreement(IMWEDetector d) {
		super(ID);
		if(d == null)
			throw new NullPointerException();
		this.d = d;
	}

	/**
	 * Returns the MWE detector for this error detector.
	 * 
	 * @return the MWE detector for this error detector, never <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public IMWEDetector getDetector() {
		return d;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, S> result) {
		
		//find error MWEs
		List<IMWE<T>> problems = new ArrayList<IMWE<T>>();
		
		Map<String, List<IMWE<T>>> details = new HashMap<String, List<IMWE<T>>>(3);
		
		for(IMWE<T> mwe : result.getFalsePositives())
			if(isProblem(mwe, result, d)){
				System.out.println(mwe);
				System.out.println(SentenceResult.toString(result, result.getSentence()));
				problems.add(mwe);
			}
		details.put(getID()+".FalsePos", new ArrayList<IMWE<T>>(problems));
		problems.clear();

		for(IMWE<T> mwe : result.getFalseNegatives())
			if(isProblem(mwe, result, d))
				problems.add(mwe);
		details.put(getID()+".FalseNeg", new ArrayList<IMWE<T>>(problems));
		problems.clear();
		
		for(IMWE<T> mwe : result.getTruePositives())
			if(isProblem(mwe, result, d))
				problems.add(mwe);
		details.put(getID()+".TruePos", new ArrayList<IMWE<T>>(problems));
		
		return new ErrorResult<T>(details);
		
	}
	
	/**
	 * Determines if the specified MWE is a problem relative to the specified
	 * sentence according to this error class.
	 * 
	 * @param <T>
	 *            the token type of the MWE
	 * @param <S>
	 *            the sentence type of the Sentence
	 * @param mwe
	 *            the MWE in question, may not be <code>null</code>
	 * @param result
	 *            the result to be used, may not be <code>null</code>
	 * @param detector
	 *            the detector to use to find the problem
	 * @return <code>true</code> if the MWE is a problem; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the specified mwe is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken, S extends IMarkedSentence<T>> boolean isProblem(IMWE<T> mwe, ISentenceResult<T, S> result, IMWEDetector detector){
		List<IMWE<T>> found = detector.detect(result.getSentence());
		return !found.contains(mwe);
	}

}

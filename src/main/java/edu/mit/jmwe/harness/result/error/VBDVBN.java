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
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * 
 * Finds the two-token MWEs that have the tag sequence VBD_VBN.
 * 
 * @author N. Kulkarni
 * @version $Id: VBDVBN.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class VBDVBN extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.VBDVBN";

	// the singleton instance
	private static VBDVBN instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static VBDVBN getInstance() {
		if (instance == null) 
			instance = new VBDVBN();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected VBDVBN() {
		super(ID);
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
			if(isProblem(mwe))
				problems.add(mwe);
		details.put(getID()+".FalsePos", new ArrayList<IMWE<T>>(problems));
		problems.clear();

		for(IMWE<T> mwe : result.getFalseNegatives())
			if(isProblem(mwe))
				problems.add(mwe);
		details.put(getID()+".FalseNeg", new ArrayList<IMWE<T>>(problems));
		problems.clear();
		
		for(IMWE<T> mwe : result.getTruePositives())
			if(isProblem(mwe))
				problems.add(mwe);
		details.put(getID()+".TruePos", new ArrayList<IMWE<T>>(problems));
		problems.clear();
		
		return new ErrorResult<T>(details);
	}

	/**
	 * Determines if the specified MWE is a problem according to this error
	 * class.
	 * 
	 * @param <T>
	 *            the token type of the MWE
	 * @param mwe
	 *            the MWE in question, may not be <code>null</code>
	 * @return <code>true</code> if the MWE is a problem; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the specified mwe is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isProblem(IMWE<T> mwe){
		if(mwe.getTokens().size()!= 2)
			return false;
		if(!mwe.getTokens().get(0).getTag().equals("VBD"))
			return false;
		if(!mwe.getTokens().get(1).getTag().equals("VBN"))
			return false;
		return true;
	}

}

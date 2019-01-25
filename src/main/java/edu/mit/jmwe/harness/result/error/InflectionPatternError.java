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
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.InflectionRule;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Identifies those multi-word expressions that do not follow the inflection
 * patterns listed in the {@link InflectionRule} enum. This includes multi-word
 * expressions that syntactically match a rule, but whose parts do not inflect
 * according to the rule as well as those expressions that do not match any
 * rule.
 * 
 * @author N. Kulkarni
 * @version $Id: InflectionPatternError.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class InflectionPatternError extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.PAT";
	
	// the singleton instance
	private static InflectionPatternError instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static InflectionPatternError  getInstance() {
		if (instance == null) 
			instance = new InflectionPatternError();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected InflectionPatternError() {
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
		
		// false positives
		for(IMWE<T> mwe : result.getFalsePositives())
			if(mwe.isInflected() && InflectionRule.isInflectedByPattern(mwe))
				problems.add(mwe);
		details.put(getID()+".FalsePos", new ArrayList<IMWE<T>>(problems));
		problems.clear();

		// false negatives
		for(IMWE<T> mwe : result.getFalseNegatives())
			if(mwe.isInflected() && InflectionRule.isInflectedByPattern(mwe))
				problems.add(mwe);
		details.put(getID()+".FalseNeg", new ArrayList<IMWE<T>>(problems));
		problems.clear();
		
		// true positives
		for(IMWE<T> mwe : result.getTruePositives())
			if(mwe.isInflected() && InflectionRule.isInflectedByPattern(mwe))
				problems.add(mwe);
		details.put(getID()+".TruePos", new ArrayList<IMWE<T>>(problems));
		problems.clear();
		
		return new ErrorResult<T>(details);
	}

}

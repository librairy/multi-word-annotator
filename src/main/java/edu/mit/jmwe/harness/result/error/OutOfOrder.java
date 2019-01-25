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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.detect.InOrder;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Finds the proper noun multi-word expressions with one or more parts tagged as
 * something other than a proper noun that the {@link IMWEDetector} failed to
 * identify
 * 
 * @author N. Kulkarni
 * @version $Id: OutOfOrder.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class OutOfOrder extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.OutOfOrder";
	
	// the singleton instance
	private static OutOfOrder instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static OutOfOrder getInstance() {
		if(instance == null) 
			instance = new OutOfOrder();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected OutOfOrder() {
		super(ID);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, U extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, U> result) {
		
		// result
		Map<String, List<IMWE<T>>> details = new HashMap<String, List<IMWE<T>>>(3);
		
		// false positives
		List<IMWE<T>> problems = new LinkedList<IMWE<T>>();
		for(IMWE<T> mwe : result.getFalsePositives())
			if(InOrder.isOutOfOrder(mwe))
				problems.add(mwe);
		details.put(getID()+".FalsePos", new ArrayList<IMWE<T>>(problems));

		// false negatives
		problems = new LinkedList<IMWE<T>>();
		for(IMWE<T> mwe : result.getFalseNegatives())
			if(InOrder.isOutOfOrder(mwe))
				problems.add(mwe);
		details.put(getID()+".FalseNeg", new ArrayList<IMWE<T>>(problems));
		
		// true positives
		problems = new LinkedList<IMWE<T>>();
		for(IMWE<T> mwe : result.getTruePositives())
			if(InOrder.isOutOfOrder(mwe))
				problems.add(mwe);
		details.put(getID()+".TruePos", new ArrayList<IMWE<T>>(problems));
		problems.clear();
		
		return new ErrorResult<T>(details);
	}
	

}

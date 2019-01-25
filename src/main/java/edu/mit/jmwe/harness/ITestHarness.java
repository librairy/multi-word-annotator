/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness;

import java.util.Iterator;
import java.util.Map;

import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.harness.result.IResultBuilder;
import edu.mit.jmwe.util.IProgressBar;

/**
 * Classes implementing this interface test an {@link IMWEDetector} by comparing
 * the multi-word expressions it finds to those found in an {@link IAnswerKey}
 * object. Calculates the precision and recall scores of the detector.
 * 
 * @author M.A. Finlayson
 * @version $Id: ITestHarness.java 319 2011-05-06 20:22:54Z markaf $
 * @since jMWE 1.0.0
 */
public interface ITestHarness {

	/**
	 * Runs the detector in the test harness and stores the results in the
	 * provided result builder.
	 * 
	 * @param <T>
	 *            the type of tokens in the {@link IMarkedSentence} objects the
	 *            harness runs over
	 * @param <S>
	 *            the type of unit the harness runs over. Is parameterized by
	 *            tokens of type T.
	 * @param detector
	 *            the detector being tested. May not be <code>null</code>
	 * @param results
	 *            the builder in which the results should be stored; may not be
	 *            <code>null</code>
	 * @param itr
	 *            an iterator over the units the detector will be tested on. May
	 *            not be <code>null</code>.
	 * @param answers
	 *            an answer key that can be used to find the answer multi-word
	 *            expressions in a unit. May not be <code>null</code>.
	 * @param pb
	 *            a progress bar to which the harness should report progress.
	 *            May be <code>null</code>
	 * @throws NullPointerException
	 *             if the any of the detector, iterator or answer key are
	 *             <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> void run(IMWEDetector detector, IResultBuilder<T, S> results, 
			Iterator<S> itr, IAnswerKey answers, IProgressBar pb);

	/**
	 * Runs the detectors in the test harness and stores the results in the
	 * associated result builder.
	 * 
	 * @param <T>
	 *            the type of tokens in the {@link IMarkedSentence} objects the
	 *            harness runs over
	 * @param <S>
	 *            the type of unit the harness runs over. Is parameterized by
	 *            tokens of type T.
	 * @param detectors
	 *            the detector-to-builder map being tested. May not be
	 *            <code>null</code>
	 * @param itr
	 *            an iterator over the units the detector will be tested on. May
	 *            not be <code>null</code>.
	 * @param answers
	 *            an answer key that can be used to find the answer multi-word
	 *            expressions in a unit. May not be <code>null</code>.
	 * @param pb
	 *            a progress bar to which the harness should report progress.
	 *            May be <code>null</code>
	 * @throws NullPointerException
	 *             if the any of the detector map, iterator, or answer key are
	 *             <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> void run(Map<IMWEDetector, IResultBuilder<T, S>> detectors, 
			Iterator<S> itr, IAnswerKey answers, IProgressBar pb);
	
	
}



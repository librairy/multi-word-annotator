/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.harness.result.IResultBuilder;
import edu.mit.jmwe.harness.result.ISentenceResult;
import edu.mit.jmwe.harness.result.SentenceResult;
import edu.mit.jmwe.util.IProgressBar;
import edu.mit.jmwe.util.NullProgressBar;

/**
 * Runs an {@link IMWEDetector} over a corpus and compares the
 * multi-word expressions the detector finds to the multi-word expressions found
 * in the answer key. Stores results of the runs in a result builder.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: TestHarness.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class TestHarness implements ITestHarness {
	
	//the singleton instance
	private static TestHarness instance = null;
	
	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static TestHarness getInstance() {
		if (instance == null)
			instance = new TestHarness();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected TestHarness() {}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.ITestHarness#run(edu.mit.jmwe.detect.IMWEDetector, java.util.Iterator, edu.mit.jmwe.harness.answer.IAnswerKey, edu.mit.jmwe.harness.IProgressBar, boolean)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> void run(IMWEDetector detector, 
			IResultBuilder<T, S> result, Iterator<S> itr, IAnswerKey answers, IProgressBar pb) {
		run(Collections.singletonMap(detector, result), itr, answers, pb);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.ITestHarness#run(java.util.Map, java.util.Iterator, edu.mit.jmwe.harness.answer.IAnswerKey, edu.mit.jmwe.util.IProgressBar)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> void run(
			Map<IMWEDetector, IResultBuilder<T, S>> detectors, Iterator<S> itr,
			IAnswerKey answers, IProgressBar pb) {
		
		// check arguments
		for(Entry<?,?> e : detectors.entrySet()){
			if(e.getKey() == null)
				throw new NullPointerException();
			if(e.getValue() == null)
				throw new NullPointerException();
		}
		if(pb == null)
			pb = NullProgressBar.getInstance();
		
		S sent;
		List<IMWE<T>> answerMWEs;
		while(itr.hasNext()){
			sent = itr.next();
			answerMWEs = answers.getAnswers(sent);
			runDetectors(detectors, sent, answerMWEs);
			pb.increment();
		}
		pb.finish();
	}

	/**
	 * Runs a set of detectors on the specified sentence, comparing the results
	 * to the specified answers.
	 * 
	 * @param <T>
	 *            the token type
	 * @param <S>
	 *            the sentence type
	 * @param detectors
	 *            the detector-to-builder map
	 * @param sent
	 *            the sentence on which the detectors should be run
	 * @param answers
	 *            the set of answers for the sentence
	 * @throws NullPointerException
	 *             if any argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken, S extends IMarkedSentence<T>> void runDetectors(Map<IMWEDetector,IResultBuilder<T, S>> detectors, S sent, List<IMWE<T>> answers){
		for(Entry<IMWEDetector, IResultBuilder<T, S>> e : detectors.entrySet())
			runDetector(e.getKey(), e.getValue(), sent, answers);
	}

	/**
	 * Runs the detector over a single sentence, storing the result as an
	 * {@link ISentenceResult} in the given result builder.
	 * 
	 * @param <T>
	 *            the type of tokens in the sentence
	 * @param <S>
	 *            the type of sentence
	 * @param detector
	 *            the detector to be tested
	 * @param builder
	 *            the result builder being used to store the results of the test
	 *            harness
	 * @param sent
	 *            the sentence the detector will be run over
	 * @param answers
	 *            the list of answer MWEs in the sentence
	 * @return the list of detected MWEs
	 * @throws NullPointerException
	 *             if any argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken, S extends IMarkedSentence<T>> List<IMWE<T>> runDetector(IMWEDetector detector, IResultBuilder<T, S> builder, S sent, List<IMWE<T>> answers){
		List<IMWE<T>> results = detector.detect(sent);
		builder.process(results, answers);
		builder.addDetail(sent.getID(), new SentenceResult<T, S>(answers, results, sent));
		return results;
	}

}

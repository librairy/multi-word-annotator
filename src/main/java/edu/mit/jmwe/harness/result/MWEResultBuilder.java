/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.util.AtomicDouble;

/**
 * Builds an {@link MWEResult} by processing the data in {@link ISentenceResult}
 * objects. Calculates the overall precision and recall scores with and without
 * partial credit. All of the data stored in this builder is organized by part
 * of speech.
 * 
 * Partial credit is given when the detector correctly identifies some but not
 * all of the tokens in an expression or if it includes some extra incorrect
 * tokens in the expression. The partial credit is the alignment score between a
 * partially correct MWE and an answer MWE. This alignment score is the ratio of
 * the number of tokens shared between the two MWEs and the total number of
 * unique tokens in both MWEs. It will be somewhere in between 0 (not
 * overlapping at all) and 1 (overlapping perfectly). For example, in
 * 
 * <pre>
 * Senator Ben Cardin of Maryland voted for the health insurance bill.
 * </pre>
 * 
 * If the detector correctly identifies <b>health insurance</b> but identifies
 * <b>Senator Ben Cardin of Maryland</b> instead of <b>Senator Ben Cardin</b>,
 * the scores would be
 * 
 * <pre>
 * Found   Actual  Correct   pc    Pr    Re    Pr/pc    Re/pc
 * ----------------------------------------------------------
 * 2       2       1         0.6   0.5   0.5   0.8      0.8
 * </pre>
 * 
 * @param <T>
 *            the type of tokens contained in the unit and its associated
 *            multi-word expressions.
 * @param <S>
 *            the type of unit whose results are stored. Is parameterized by
 *            tokens of type T.
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: MWEResultBuilder.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class MWEResultBuilder<T extends IToken, S extends IMarkedSentence<T>> implements IResultBuilder<T, S> {
	
	// final instance fields
	public final Map<MWEPOS, AtomicInteger> answerData = initIntegerMap();
	public final Map<MWEPOS, AtomicInteger> foundData = initIntegerMap();
	public final Map<MWEPOS, AtomicInteger> correctData = initIntegerMap();
	public final Map<MWEPOS, AtomicDouble<T, S>> partialScores = initDoubleMap();
	public final Map<String, ISentenceResult<T, S>> details;

	/**
	 * Constructs a new builder that stores the detailed results obtained from
	 * individual sentences.
	 * 
	 * @since jMWE 1.0.0
	 */
	public MWEResultBuilder() {
		this(true);
	}

	/**
	 * Constructs a new builder that, if the captureDetials flag is true, will
	 * stores the detailed results obtained from individual sentences.
	 * 
	 * @param captureDetails
	 *            the flag that if <code>true</code>, means that this builder will store
	 *            results from each sentence.
	 * @since jMWE 1.0.0
	 */
	public MWEResultBuilder(boolean captureDetails){
		this.details = captureDetails ? new TreeMap<String, ISentenceResult<T,S>>() : null;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IResultBuilder#process(java.util.List, java.util.List)
	 */
	public void process(List<IMWE<T>> found, List<IMWE<T>> answers){
		
		// true positives
		List<IMWE<T>> truePos = new ArrayList<IMWE<T>>();
		
		// false positives
		Map<MWEPOS, List<IMWE<T>>> falsePos = new HashMap<MWEPOS, List<IMWE<T>>>();
		for(MWEPOS ps : MWEPOS.values()) 
			falsePos.put(ps, new ArrayList<IMWE<T>>());
		
		// identify true and false positives
		MWEPOS pos;
		for(IMWE<T> mwe: found){
			pos = mwe.getEntry().getPOS();
			
			foundData.get(pos).incrementAndGet();
			if(answers.contains(mwe)){
				truePos.add(mwe);
				correctData.get(pos).incrementAndGet();
			} else {
				falsePos.get(mwe.getEntry().getPOS()).add(mwe);
			}
		}
		
		// false negatives
		Map<IMWE<T>, Double> falseNeg = new HashMap<IMWE<T>, Double>();
		for(IMWE<T> mwe: answers) {
			pos = mwe.getEntry().getPOS();
			answerData.get(pos).incrementAndGet();
			if(!truePos.contains(mwe))
				falseNeg.put(mwe, 0.0);
		}
		
		// calculate partial scores
		double overlap;
		for(Entry<IMWE<T>, Double> e: falseNeg.entrySet()){
			for(IMWE<T> m : falsePos.get(e.getKey().getEntry().getPOS())){


				overlap = MWE.overlap(e.getKey(), m);
				if(overlap > e.getValue())
					falseNeg.put(e.getKey(), overlap);
			}
		}
		// transfer to global map
		for(Entry<IMWE<T>, Double> e: falseNeg.entrySet()){
			pos = e.getKey().getEntry().getPOS();
			partialScores.get(pos).increment(e.getValue());
		}
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IResultBuilder#addDetail(java.lang.String, edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public void addDetail(String ID, ISentenceResult<T,S> detail){
		if(detail != null)
			details.put(ID, detail);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IResultBuilder#createResult()
	 */
	public MWEResult<T, S> createResult(){
		Map<MWEPOS, Integer> answers = toIntMap(answerData);
		Map<MWEPOS, Integer> found = toIntMap(foundData);
		Map<MWEPOS, Integer> correct = toIntMap(correctData);
		Map<MWEPOS, Double> partial = toDblMap(partialScores);
		return new MWEResult<T,S>(answers, found, correct, partial, details);
	}

	/**
	 * Converts a map of AtomicIntegers to a map of Integers.
	 * 
	 * @param map
	 *            the map to be converted
	 * @return the converted map
	 * @since jMWE 1.0.0
	 */
	protected Map<MWEPOS, Integer> toIntMap(Map<MWEPOS, AtomicInteger> map){
		Map<MWEPOS, Integer> result = new HashMap<MWEPOS, Integer>(map.size());
		for(Entry<MWEPOS, AtomicInteger> e : map.entrySet())
			result.put(e.getKey(), e.getValue().intValue());
		return result;
	}

	/**
	 * Converts a map of AtomicDoubles to a map of Doubles.
	 * 
	 * @param map
	 *            the map to be converted
	 * @return the converted map
	 * @since jMWE 1.0.0
	 */
	protected Map<MWEPOS, Double> toDblMap(Map<MWEPOS, AtomicDouble<T, S>> map){
		Map<MWEPOS, Double> result = new HashMap<MWEPOS, Double>(map.size());
		for(Entry<MWEPOS, AtomicDouble<T, S>> e : map.entrySet())
			result.put(e.getKey(), e.getValue().doubleValue());
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return createResult().toString();
	}

	/**
	 * Provides a new map for integer values.
	 * 
	 * @return a new map with MWEPOS objects mapped to zeroed atomic integer
	 *         objects
	 * @since jMWE 1.0.0
	 */
	protected Map<MWEPOS, AtomicInteger> initIntegerMap(){
		Map<MWEPOS, AtomicInteger> map = new HashMap<MWEPOS, AtomicInteger>(MWEPOS.values().length);
		for(MWEPOS pos : MWEPOS.values())
			map.put(pos, new AtomicInteger());
		return map;
	}

	/**
	 * Provides a new, empty map for double values.
	 * 
	 * @return a new map with MWEPOS objects mapped to zeroed atomic double
	 *         objects
	 * @since jMWE 1.0.0
	 */
	protected Map<MWEPOS, AtomicDouble<T, S>> initDoubleMap(){
		Map<MWEPOS, AtomicDouble<T, S>> map = new HashMap<MWEPOS, AtomicDouble<T, S>>(MWEPOS.values().length);
		for(MWEPOS pos : MWEPOS.values())
			map.put(pos, new AtomicDouble<T, S>());
		return map;
	}


}

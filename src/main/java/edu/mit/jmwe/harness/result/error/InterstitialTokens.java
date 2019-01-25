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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.Continuous;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;
import edu.mit.jmwe.util.ListComparator;

/**
 * Counts and stores the multi-word expressions that have tokens that are separated
 * by one or more tokens in the sentence that are not also a part of the MWE.
 * 
 * @author N. Kulkarni
 * @version $Id: InterstitialTokens.java 314 2011-05-06 18:51:51Z nidhik $
 * @since jMWE 1.0.0
 */
public class InterstitialTokens extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.Interstitial";
	
	// the singleton instance
	private static InterstitialTokens instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 * 
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static InterstitialTokens getInstance() {
		if (instance == null) 
			instance = new InterstitialTokens();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected InterstitialTokens() {
		super(ID);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, U extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, U> result) {
		
		Map<String, List<IMWE<T>>> details;
		
		if(result.getSentence() == null){
			details = Collections.emptyMap();
			return new ErrorResult<T>(details);
		}
		
		//find error MWEs
		List<IMWE<T>> problems;
		details = new HashMap<String, List<IMWE<T>>>(3);
		
		Map<T, Integer> indexMap = ListComparator.createIndexMap(result.getSentence());
		
		// look at false positives
		problems = new LinkedList<IMWE<T>>();
		for(IMWE<T> mwe : result.getFalsePositives())
			if(Continuous.isDiscontinuous(mwe, indexMap))
				problems.add(mwe);
		details.put(getID()+".FalsePos", new ArrayList<IMWE<T>>(problems));
		
		// count particle subset of false positives 
		for(Iterator<IMWE<T>> i = problems.iterator(); i.hasNext();)
			if(!hasParticle(i.next(), result.getSentence()))
				i.remove();
		details.put(getID()+".FalsePos"+".Particle", new ArrayList<IMWE<T>>(problems));

		// look at false negatives
		problems = new LinkedList<IMWE<T>>();
		for(IMWE<T> mwe : result.getFalseNegatives())
			if(Continuous.isDiscontinuous(mwe, indexMap))
				problems.add(mwe);
		details.put(getID()+".FalseNeg", new ArrayList<IMWE<T>>(problems));
		
		// count particle subset of false negatives
		for(Iterator<IMWE<T>> i = problems.iterator(); i.hasNext();)
			if(!hasParticle(i.next(), result.getSentence()))
				i.remove();
		details.put(getID()+".FalseNeg"+".Particle", new ArrayList<IMWE<T>>(problems));
		
		// look at true positives
		problems = new LinkedList<IMWE<T>>();
		for(IMWE<T> mwe : result.getTruePositives())
			if(Continuous.isDiscontinuous(mwe, indexMap))
				problems.add(mwe);
		details.put(getID()+".TruePos", new ArrayList<IMWE<T>>(problems));
		
		// count particle subset of true positives
		for(Iterator<IMWE<T>> i = problems.iterator(); i.hasNext();)
			if(!hasParticle(i.next(), result.getSentence()))
				i.remove();
		details.put(getID()+".TruePos"+".Particle", new ArrayList<IMWE<T>>(problems));
		
		return new ErrorResult<T>(details);
	}
	
	/**
	 * Returns <code>true</code> if the specified token is tagged as a particle;
	 * <code>false</code> otherwise
	 * 
	 * @param <T>
	 *            the type of the token to be checked
	 * @param token
	 *            the token to be checked
	 * @return <code>true</code> if the specified token is tagged as a particle;
	 *         <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isParticle(T token){
		String tag = token.getTag();
		return tag.equals("RP") || tag.equals("TO");
	}
	
	/**
	 * Returns true if the given MWE contains a token that is a particle and is
	 * separated from the previous token in the MWE by one or more non-MWE
	 * tokens in the sentence.
	 * 
	 * @param <T>
	 *            the type of tokens in the sentence
	 * @param mwe
	 *            the MWE being checked
	 * @param sentence
	 *            the sentence of which the MWE is a part
	 * @return <code>true</code> if the given MWE contains a token that is a particle and is
	 *             separated from the previous token in the MWE by one or more
	 *             non-MWE tokens in the sentence; <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean hasParticle(IMWE<T> mwe, List<T> sentence){
		T last = mwe.getTokens().get(0);
		for(T curr : mwe.getTokens()){
			if(sentence.indexOf(curr) > sentence.indexOf(last) + 1 && isParticle(curr))
				return true;
			last = curr;
		}
		return false;
	}
}

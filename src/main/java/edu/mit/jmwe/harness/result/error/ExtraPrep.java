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
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Finds the proper noun multi-word expressions that were truncated incorrectly
 * because of a token with a prepositional tag. For example:
 * 
 * <pre>
 * He-NN lives-VB in-IN the-DT United-PRP States-PRP of-IN America-PRP.
 * </pre>
 * 
 * The detector might only return <i>United States;P</i> instead of the correct
 * <i>United_States_of_America;P</i>.
 * 
 * @author N. Kulkarni
 * @version $Id: ExtraPrep.java 341 2011-09-26 21:03:51Z markaf $
 * @since jMWE 1.0.0
 */
public class ExtraPrep extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.ExtraPrep";

	// the singleton instance
	private static ExtraPrep instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static ExtraPrep getInstance() {
		if (instance == null) 
			instance = new ExtraPrep();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected ExtraPrep() {
		super(ID);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, S> result) {
		List<IMWE<T>> mwes = new ArrayList<IMWE<T>>();
		//loop variable
		IMWE<T> test = null;
		for(IMWE<T> fn : result.getFalseNegatives()){
			if(! fn.getEntry().getPOS().equals(MWEPOS.PROPER_NOUN)) continue;
			//first find the false positive MWE that overlaps the false negative
			for(IMWE<T> fp : result.getFalsePositives()){
				if(! fp.getEntry().getPOS().equals(MWEPOS.PROPER_NOUN)) continue;
				if(MWE.overlap(fp, fn) > 0){
					test = fp;
					break;
				}
			}
			if(test == null) continue;
			if(test.getTokens().size() < fn.getTokens().size() && (ExtraPrep.findTag(test, "IN") == -1 && ExtraPrep.findTag(fn, "IN")!= -1))
				mwes.add(test);
			test = null;
		}
		Map<String, List<IMWE<T>>> details = new HashMap<String, List<IMWE<T>>>();
		details.put(this.getID(), mwes);
		return new ErrorResult<T>(details);
	}
	
	/**
	 * Returns the index of the first token in the MWE with the specified tag.
	 * If no tokens have this tag, returns -1.
	 * 
	 * @param <T>
	 *            the type of tokens in the MWE
	 * @param test
	 *            the MWE whose tokens will be searched
	 * @param tag
	 *            the tag being searched for
	 * @return the index of the first token in the MWE with the specified tag.
	 *         If no tokens have this tag, returns -1.
	 * @since jMWE 1.0.0
	 */
	protected static <T extends IToken> int findTag(IMWE<T> test, String tag){
		int i = 0;
		for(T token: test.getTokens()){
			if(token.getTag().equalsIgnoreCase(tag))
				return i;
			i++;
		}
		return -1;
	}

}

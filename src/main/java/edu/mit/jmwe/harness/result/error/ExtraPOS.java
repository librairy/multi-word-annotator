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
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Finds the proper noun multi-word expressions that were truncated incorrectly
 * because of a token with a possessive ending tag. For example:
 * 
 * <pre>
 * Today-NN is-VB Saint-PRP Agnes-PRP 's-POS Eve-PRP.
 * </pre>
 * 
 * A detector might only return <i>Saint_Agnes;P</i> instead of the correct
 * <i>Saint_Agnes_'s_Eve;P</i>.
 * 
 * @author N. Kulkarni
 * @version $Id: ExtraPOS.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class ExtraPOS extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.ExtraPOS";

	// the singleton instance
	private static ExtraPOS instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static ExtraPOS getInstance() {
		if (instance == null)
			instance = new ExtraPOS();
		return instance;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected ExtraPOS() {
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
			if(test.getTokens().size() < fn.getTokens().size() && (ExtraPrep.findTag(test, "POS") == -1 && ExtraPrep.findTag(fn, "POS")!= -1))
				mwes.add(test);
			test = null;
		}
		
		return new ErrorResult<T>(this.getID(), mwes);
	}

}

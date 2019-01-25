/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Uses a given <code>IMWEDetector</code> to find multi-word expressions in a
 * sentence but discards inflected multi-word expressions if the inflected form
 * does not correspond to an inflection pattern give by a set of inflection
 * rules.
 * <p>
 * This implementation uses the default set of inflection rules listed in
 * {@link InflectionRule}. Subclasses may change the set of rules by overriding
 * the {@link #getRules()} method.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: InflectionPattern.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class InflectionPattern extends HasMWEDetector implements IMWEDetectorFilter {
	
	// cached set
	private static final Set<IInflectionRule> rules = Collections.<IInflectionRule>unmodifiableSet(EnumSet.allOf(InflectionRule.class));
	
	/**
	 * Constructs the detector from the given backing detector.
	 * 
	 * @param d
	 *            the IMWEDetector that will be used to back this detector. May
	 *            not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the backing detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public InflectionPattern(IMWEDetector d){
		super(d);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// get raw results from backing detector
		List<IMWE<T>> results = super.detect(sentence);
		
		// remove all inflected mwe's that do not match a pattern
		IMWE<T> mwe;
		for(Iterator<IMWE<T>> i = results.iterator(); i.hasNext();){
			mwe = i.next();
			if(mwe.isInflected() && !InflectionRule.isInflectedByPattern(mwe, getRules()))
				i.remove();
		}
		
		return results;
	}

	/**
	 * Returns the set of inflection rules to be used by this detector. May be
	 * empty but may not be <code>null</code>.
	 * 
	 * @return the set of inflection rules to be used by this detector.
	 * @since jMWE 1.0.0
	 */
	public Collection<IInflectionRule> getRules(){
		return rules;
	}

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.util.AbstractIndexComparator;

/**
 * A detector that filters the results of its backing detector to return only
 * those MWEs whose parts are continuous in the sentence.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: Continuous.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class Continuous extends HasMWEDetector implements IMWEDetectorFilter {
	
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
	public Continuous(IMWEDetector d){
		super(d);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// get raw results from simple lookup detector
		List<IMWE<T>> results = super.detect(sentence);
		
		// filter out non-continuous MWEs
		if(!results.isEmpty()){
			Map<T, Integer> idxMap = MWEComparator.createIndexMap(sentence);
			for(Iterator<IMWE<T>> i = results.iterator(); i.hasNext();)
				if(isDiscontinuous(i.next(), idxMap)) 
					i.remove();
		}
		
		// return
		return results;
	}

	/**
	 * Determines if the specified MWE is continuous, i.e., there are no
	 * interstitial tokens inside its boundaries that are not a part of the MWE.
	 * 
	 * @param <T>
	 *            the token type of the MWE
	 * @param mwe
	 *            the MWE in question, may not be <code>null</code>
	 * @param sentence
	 *            the sentence from which the MWE is drawn
	 * @return <code>true</code> if the MWE is continuous; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the specified mwe is <code>null</code>, or does not come
	 *             from the specified sentence
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isDiscontinuous(IMWE<T> mwe, List<T> sentence){
		return isDiscontinuous(mwe, AbstractIndexComparator.createIndexMap(sentence));
	}

	/**
	 * Determines if the specified MWE is continuous, i.e., there are no
	 * interstitial tokens inside its boundaries that are not a part of the MWE.
	 * 
	 * @param <T>
	 *            the token type of the MWE
	 * @param mwe
	 *            the MWE in question, may not be <code>null</code>
	 * @param indexMap
	 *            a map from sentence tokens to their index in the sentence
	 * @return <code>true</code> if the MWE is continuous; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the specified mwe is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isDiscontinuous(IMWE<T> mwe, Map<T,Integer> indexMap){
		T first = mwe.getPartMap().keySet().iterator().next();
		int idx = indexMap.get(first);
		for(T token : mwe.getPartMap().keySet())
			if(indexMap.get(token) != idx++)
				return true;
		return false;
	}

}

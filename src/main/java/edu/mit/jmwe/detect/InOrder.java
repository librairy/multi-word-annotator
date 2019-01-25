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

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Filters the output of a given MWE detector, removing those MWEs whose parts
 * appear do not appear in the sentence in the same order as they appear in the MWE description.
 * 
 * @author N. Kulkarni
 * @version $Id: InOrder.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class InOrder extends HasMWEDetector implements IMWEDetectorFilter {

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
	public InOrder(IMWEDetector d){
		super(d);
	}
		
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// get raw results
		List<IMWE<T>> results = super.detect(sentence);
		
		// filter
		for(Iterator<IMWE<T>> i = results.iterator(); i.hasNext();)
			if(isOutOfOrder(i.next()))
				i.remove();
		
		// return 
		return results;
	}

	/**
	 * Determines if the constituents of the specified MWE are out of order.
	 * 
	 * @param <T>
	 *            the token type of the MWE
	 * @param mwe
	 *            the MWE in question, may not be <code>null</code>
	 * @return <code>true</code> if the MWE is a problem; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the specified mwe is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isOutOfOrder(IMWE<T> mwe){
		Iterator<T> tokens = mwe.getTokens().iterator();
		Iterator<T> parts = mwe.getPartMap().keySet().iterator();
		while(tokens.hasNext() && parts.hasNext())
			if(tokens.next() != parts.next())
				return true;
		return false;
	}

}


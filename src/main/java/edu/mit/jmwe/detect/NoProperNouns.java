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
import edu.mit.jmwe.data.MWEPOS;

/**
 * A detector that filters all proper noun MWEs from the results of its backing detector.
 * 
 * @author M.A. Finlayson
 * @version $Id: NoProperNouns.java 309 2011-05-06 03:53:23Z markaf $
 * @since jMWE 1.0.0
 */
public class NoProperNouns extends HasMWEDetector implements IMWEDetectorFilter {

	/**
	 * Constructs the detector from the given backing detector. May not be
	 * <code>null</code>.
	 * 
	 * @param detector
	 *            the detector used to back this detector
	 * @since jMWE 1.0.0
	 */
	public NoProperNouns(IMWEDetector detector) {
		super(detector);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// raw results
		List<IMWE<T>> results = super.detect(sentence);
		
		// filter
		for(Iterator<IMWE<T>> i = results.iterator(); i.hasNext(); )
			if(i.next().getEntry().getPOS() == MWEPOS.PROPER_NOUN)
				i.remove();
		
		// return
		return results;
	}

}

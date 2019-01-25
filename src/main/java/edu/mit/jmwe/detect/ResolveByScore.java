/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.detect.score.IScorer;
import edu.mit.jmwe.util.CompositeComparator;

/**
 * Abstract base class for MWE detector resolvers that use a simple score to
 * resolve conflicts.
 * 
 * @author M.A. Finlayson
 * @version $Id: ResolveByScore.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class ResolveByScore extends HasMWEDetector implements IMWEDetectorResolver {
	
	// final instance fields
	protected final boolean chooseLargest;

	/**
	 * Constructs a new MWE detector constraint that wraps the specified MWE
	 * Detector.
	 * 
	 * @param detector
	 *            the detector wrapped by this object, may not be
	 *            {@link NullPointerException}
	 * @param chooseLargest
	 *            <code>true</code> if the largest score should be chosen;
	 *            <code>false</code> if the smallest should be used.
	 * @throws NullPointerException
	 *             if the specified detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ResolveByScore(IMWEDetector detector, boolean chooseLargest) {
		super(detector);
		this.chooseLargest = chooseLargest;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// run embedded detector
		List<IMWE<T>> results = super.detect(sentence);
		
		// get comparators
		Comparator<IMWE<T>> scoreComp = getScorer(sentence);
		if(chooseLargest)
			scoreComp = Collections.reverseOrder(scoreComp);
		Comparator<IMWE<T>> mweComp = new MWEComparator<T>(sentence);
		Comparator<IMWE<T>> c = new CompositeComparator<IMWE<T>>(scoreComp, mweComp);
		
		// sort first score (best score first), then leftmost mwe index
		Collections.sort(results, c);
		LinkedList<IMWE<T>> queue = new LinkedList<IMWE<T>>(results);

		// pick the best scoring MWE, and then throw away everything
		// that conflicts with it.  Repeat until done.
		IMWE<T> mwe;
		Set<IMWE<T>> resultSet = new LinkedHashSet<IMWE<T>>();
		while(!queue.isEmpty()){
			mwe = queue.removeFirst();
			if(resultSet.add(mwe))
				for(Iterator<IMWE<T>> i = queue.iterator(); i.hasNext(); )
					if(MWE.overlap(mwe, i.next()) > 0)
						i.remove();
		}
		
		// dump to result list
		results = new ArrayList<IMWE<T>>(resultSet);
		Collections.sort(results, mweComp);
		return results;
	}

	/**
	 * Returns the scoring function for this filter.
	 * 
	 * @param <T>
	 *            they type of the token
	 * @param sentence
	 *            the sentence to be scored
	 * @return the scorer
	 * @since jMWE 1.0.0
	 */
	protected abstract <T extends IToken> IScorer<IMWE<T>> getScorer(List<T> sentence);

}

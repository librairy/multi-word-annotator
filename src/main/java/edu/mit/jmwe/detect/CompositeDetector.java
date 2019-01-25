/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEComparator;

/**
 * A detector composed of multiple simpler detectors.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: CompositeDetector.java 317 2011-05-06 20:05:20Z markaf $
 * @since jMWE 1.0.0
 */
public class CompositeDetector extends AbstractList<IMWEDetector> implements IMWEDetector{
	
	// instance fields
	private final IMWEDetector[] backingArray;

	/**
	 * Constructs a composite detector from an array of detectors. The array of
	 * detectors should be ordered by descending preference. The most preferred
	 * detector should be first, the second-most-preferred second, etc. If there
	 * is a conflict between multi-word expressions found by two detectors, the
	 * multi-word expression found by the more-preferred detector will be
	 * chosen.
	 * 
	 * @param ds
	 *            the array of detectors in order of descending preference. May
	 *            not be <code>null</code>. May not contain any <code>null</code>
	 *            detectors.
	 * @throws NullPointerException
	 *             if the specified array is <code>null</code> or if any of the detectors
	 *             in the list are <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public CompositeDetector(IMWEDetector... ds){
		for(IMWEDetector d : ds)
			if(d == null)
				throw new NullPointerException();
		backingArray = ds;
	}
	
	/**
	 * Constructs a composite detector from a list of detectors. The list of
	 * detectors should be ordered by descending preference. The most preferred
	 * detector should be first, the second-most-preferred second, etc. If there
	 * is a conflict between multi-word expressions found by two detectors, the
	 * multi-word expression found by the more-preferred detector will be
	 * chosen.
	 * 
	 * @param ds
	 *            the list of detectors in order of descending preference. May
	 *            not be <code>null</code>. May not contain any <code>null</code>
	 *            detectors.
	 * @throws NullPointerException
	 *             if the specified list is <code>null</code> or if any of the detectors
	 *             in the list are <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public CompositeDetector(List<? extends IMWEDetector> ds){
		for(IMWEDetector d : ds)
			if(d == null)
				throw new NullPointerException();
		this.backingArray = ds.toArray(new IMWEDetector[ds.size()]);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(edu.mit.jmwe.data.ISentence)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		Set<IMWE<T>> resultSet = new LinkedHashSet<IMWE<T>>();
		for(IMWEDetector d : backingArray)
			resultSet.addAll(d.detect(sentence));
		List<IMWE<T>> results = new ArrayList<IMWE<T>>(resultSet);
		Collections.sort(results, new MWEComparator<T>(sentence));
		return results;
	}
	
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public IMWEDetector get(int index) {
		return backingArray[index];
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return backingArray.length;
	}

}

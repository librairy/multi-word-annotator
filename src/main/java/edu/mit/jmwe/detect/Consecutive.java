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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.index.HasMWEIndex;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.util.ListComparator;

/**
 * Detector that uses an index to detect the MWEs whose parts appear
 * continuously in the sentence.
 * 
 * @author M.A. Finlayson
 * @version $Id: Consecutive.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class Consecutive extends HasMWEIndex implements IMWEDetector {

	/**
	 * Constructs the detector from the given index.
	 * 
	 * @param index
	 *            the index to be used when searching for MWEs. May not be
	 *            <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public Consecutive(IMWEIndex index) {
		super(index);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sent) {
		
		Set<? extends IMWEDesc> descs;
		MWEBuilder<T> builder;
		Comparator<T> c = new ListComparator<T>(sent);
		
		List<MWEBuilder<T>> inProgress = new LinkedList<MWEBuilder<T>>();
		List<MWEBuilder<T>> done = new LinkedList<MWEBuilder<T>>();
		
		for(T t : sent){
			
			// first try to fill slots in current builders
			// if we fail to fill a slot in any particular builder,
			// we can toss that one, because we are looking for
			// continuous runs of tokens - note that interstitial 
			// punctuation will also cause a builder to be dumped
			for(Iterator<MWEBuilder<T>> i = inProgress.iterator(); i.hasNext(); ){
				builder  = i.next();
				if(!fillNextSlot(builder, t))
					i.remove();
			}
			
			// create a new builder for each mwe desc we have 
			// whose first slot matches the current token
			descs = getMWEDescs(t);
			if(descs != null)
				for(IMWEDesc d : descs){
					builder = new MWEBuilder<T>(d, c);
					if(fillNextSlot(builder, t))
						inProgress.add(builder);
				}
			
			// move full builders to done list 
			for(Iterator<MWEBuilder<T>> i = inProgress.iterator(); i.hasNext(); ){
				builder = i.next();
				if(builder.isFull()){
					i.remove();
					done.add(builder);
				}
			}
		}
		
		// remove duplicates
		Set<IMWE<T>> resultSet = new HashSet<IMWE<T>>(done.size());
		for(MWEBuilder<T> b : done)
			resultSet.add(b.toMWE());
		
		// sort
		List<IMWE<T>> results = new ArrayList<IMWE<T>>(resultSet);
		Collections.sort(results, new MWEComparator<T>(sent));
		return results;
	}
	
	/**
	 * Fills the first non-null (empty) slot in the given builder. Each slot can
	 * be filled by a token that matches the part. Returns true if the slot can
	 * be filled, false otherwise.
	 * 
	 * @param builder
	 *            the builder to be filled
	 * @param t
	 *            the token to fill the builder's next open slot with
	 * @param <T>
	 *            the type of the token being used
	 * @return true if the next open slot can be filled, false otherwise.
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken> boolean fillNextSlot(MWEBuilder<T> builder, T t) {
		return MWEBuilder.fillNextSlot(builder, t);
	}
	
	/**
	 * Returns all the MWE entries in the index that contain the given token or
	 * one of its stems as a part. If no entries are found, returns an empty
	 * set.
	 * 
	 * @param token
	 *            the token to be looked up in the index
	 * @return a possibly empty set of MWE descriptions in the index that
	 *         contain the given token or a stem as a part.
	 * @since jMWE 1.0.0
	 */
	protected Set<? extends IMWEDesc> getMWEDescs(IToken token){
		Set<IMWEDesc> results = new HashSet<IMWEDesc>();
		IMWEIndex index = getMWEIndex();
		results.addAll(index.get(token.getForm().toLowerCase()));
		if(token.getStems() != null){
			for(String stem : token.getStems())
				results.addAll(index.get(stem));
		}
		return results;
	}

}

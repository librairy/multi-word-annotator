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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc.IPart;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.data.StopWords;
import edu.mit.jmwe.index.HasMWEIndex;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.util.ListComparator;

/**
 * Implements an exhaustive algorithm that detects all possible non-stop-word
 * MWEs in a sentence, including MWEs that are out of order or discontinuous. A
 * "Stop Word MWE" is an MWE that consists of only stop words, as defined by the
 * set of strings returned by the {@link #getStopWords()} method.
 * <p>
 * To detect stop word MWEs, use the {@link StopWords} or {@link TrulyExhaustive} detectors.
 * 
 * @author N. Kulkarni
 * @version $Id: Exhaustive.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class Exhaustive extends HasMWEIndex implements IMWEDetector {
	
	/**
	 * Constructs the simple lookup detector from the given index of multi-word
	 * expressions.
	 * 
	 * @param index
	 *            An IMWEIndex that can be used by the detector to look up MWEs.
	 *            May not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the index is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public Exhaustive(IMWEIndex index) {
		super(index);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		IMWEIndex index = getMWEIndex();
		
		Map<IRootMWEDesc, Set<MWEBuilder<T>>> recordMap = new HashMap<IRootMWEDesc, Set<MWEBuilder<T>>>();
		Comparator<T> comparator = new ListComparator<T>(sentence);
		
		// loop variables
		Set<IRootMWEDesc> entries = new HashSet<IRootMWEDesc>();
		Set<MWEBuilder<T>> records;
		
		Set<String> foundStopWords = new HashSet<String>();
		List<T> stopTokens  = new ArrayList<T>();
	
		for(T token : sentence){
			entries.clear();
			
			// if token is a stop word, don't search for MWEs now, 
			// but add to list to deal with later.
			if(getStopWords().contains(token.getForm().toLowerCase())){
				stopTokens.add(token);
				foundStopWords.add(token.getForm().toLowerCase());
				continue;
			}
			
			// retrieve relevant entries
			entries.addAll(index.get(token.getForm().toLowerCase()));
			// MWEs should be indexed under the stem of the content word
			// sometimes MWEs are erroneously indexed under the surface form
			if(token.getStems() != null)
				for(String stem : token.getStems())
					if(!stem.equals(token.getForm().toLowerCase()))
						entries.addAll(index.get(stem)); 
			
			// for each token, find all possible MWEs of which it could be a part
			for(IRootMWEDesc entry : entries){
				 records = recordMap.get(entry);
				 
				 // initialize list of records if necessary
				 if(records == null){
					 records = new HashSet<MWEBuilder<T>>();
					 recordMap.put(entry, records);
				 }
				 
				 // add the new record
				 records.add(new MWEBuilder<T>(entry, comparator));
				 MWEBuilder.fillSlots(records, token);
			}
		}
		
		// we will return this to the caller
		List<IMWE<T>> results = new LinkedList<IMWE<T>>();
		
		// prune incomplete records
		// add records with stop words to stop list
		// add complete records to result list
		IMWE<T> mwe;
		Set<MWEBuilder<T>> stopRecords = new HashSet<MWEBuilder<T>>();
		for(Set<MWEBuilder<T>> recList : recordMap.values()){
			for(MWEBuilder<T> rec : recList ){
				
				// if record is completely full, add it if it is not duplicated already
				if(rec.isFull() && !containsDuplicate(results, mwe = rec.toMWE())){
						results.add(mwe);
				} else if(rec.isContentFull()){
					for(IPart part : rec.getSlots().keySet()){
						if(foundStopWords.contains(part.getForm())){
							stopRecords.add(rec);
							break;
						}
					}
				} 
			}
		}
		
		// now for hasStop words, repeat the same process with possible records
		for(T token : stopTokens)
			MWEBuilder.fillSlots(stopRecords, token);
		
		// add all complete records to the result list
		for (MWEBuilder<T> r : stopRecords)
			if (r.isFull() && !containsDuplicate(results, mwe = r.toMWE()))
				results.add(mwe);
		
		Collections.sort(results, new MWEComparator<T>(sentence));
		return results;
	}

	/**
	 * Returns the stop words used by this detector. Subclasses may override to
	 * provide their own set of stop words.
	 * 
	 * @return the set of stop words for this detector
	 * @since jMWE 1.0.0
	 */
	protected Set<String> getStopWords() {
		return StopWords.get();
	}

	/**
	 * Returns true if the given collection of MWEs already contains a
	 * particular MWE.
	 * 
	 * @param <T>
	 *            the type of tokens in the MWEs
	 * @param results
	 *            the collection to be checked
	 * @param mwe
	 *            the MWE being searched for
	 * @return true if the given collection of MWEs already contains a
	 *         particular MWE, false otherwise.
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken> boolean containsDuplicate(Collection<? extends IMWE<T>> results, IMWE<T> mwe){
		for(IMWE<T> result : results)
			if(result.getEntry().getPOS() == mwe.getEntry().getPOS())
				if(new HashSet<T>(result.getTokens()).equals(new HashSet<T>(mwe.getTokens())))
					return true;
		return false;
	}
	
	
		
}
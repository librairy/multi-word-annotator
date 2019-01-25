/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.MWEIndex;
import edu.mit.jmwe.util.ListComparator;

/**
 * Uses the exhaustive strategy specified in {@link Exhaustive} to find the MWEs
 * that consist entirely of stop words in a sentence.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: StopWords.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class StopWords implements IMWEDetector {

	/**
	 * A list of MWEs whose parts consist entirely of stop words.
	 *
	 * @since jMWE 1.0.0
	 */
	public static List<String> defaultIndex = Collections.unmodifiableList(Arrays.asList(
			"and_then_R", "by_and_by_R", 
			"as_to_J", "as_to_O", "as_is_V", "such_as_J", "as_it_is_R", "as_such_R", 
			"at_will_R", "be_on_V", "be_with_it_V", "but_then_R", "in_for_J", "if_not_R", 
			"in_on_R", "in_that_R", "in_this_R", "that_is_R", "be_with_it_V",
			"on_it_R", "to_it_R", "be_on_V", "in_on_R", "on_that_R", "such_that_J", "to_that_R", "with_that_R",
			"and_then_R", "but_then_R", "at_will_R", "with_that_R", "of_this_R", "of_it_R", "with_it_R", "of_that_R", "at_a_R", 
			"this_and_that_R", "such_and_such_J", "in_the_R", "on_the_R", "such_as_R", "is_there_V", "was_there_V", "as_it_R",
			"of_a_N", "to_the_R"));
	
	// final instance field
	private final IMWEIndex index;
	
	/**
	 * Constructs this detector from the default stop word MWE index.
	 * 
	 * @since jMWE 1.0.0
	 */
	public StopWords(){
		this(getStopWordIndex());
	}
	
	/**
	 * Constructs this detector from the given stop word MWE index.
	 * 
	 * @param index the index of stop words that backs this detector
	 * @throws NullPointerException
	 *             if the index is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public StopWords(IMWEIndex index){
		if(index == null)
			throw new NullPointerException();
		this.index = index;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		Map<IRootMWEDesc, Set<MWEBuilder<T>>> recordMap = new HashMap<IRootMWEDesc, Set<MWEBuilder<T>>>();
		ListComparator<T> comparator = new ListComparator<T>(sentence);
		
		// loop variables
		List<IRootMWEDesc> entries = new ArrayList<IRootMWEDesc>();
		Set<MWEBuilder<T>> records;
	
		for(T token : sentence){
			entries.clear();
			
			// MWEs should be indexed under the stem of the content word
			// sometimes MWEs are erroneously indexed under the surface form
			if(token.getStems() != null)
				for(String stem : token.getStems())
					if(!stem.equals(token.getForm().toLowerCase()))
						entries.addAll(index.get(stem)); 	

			
			// for each token, find all possible MWEs of which it could be a part
			entries.addAll(index.get(token.getForm().toLowerCase()));
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
		List<IMWE<T>> result = new LinkedList<IMWE<T>>();
		
		// prune incomplete records
		// add complete records to result list
		for(Set<MWEBuilder<T>> recList : recordMap.values())
			for(MWEBuilder<T> rec : recList )
				if(rec.isFull() && !result.contains(rec.toMWE()))
					result.add(rec.toMWE());
		
		Collections.sort(result, new MWEComparator<T>(comparator.getIndexMap()));
		return result;
	}

	/**
	 * Creates, opens, and returns a new stop word index.
	 *
	 * @return a new, open stop word index.
	 * @since jMWE 1.0.0
	 */
	protected static IMWEIndex getStopWordIndex(){
		IMWEIndex index = new MWEIndex(defaultIndex);
		try {
			index.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return index;
	}

}

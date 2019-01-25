/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Standard list of stop words, taken from Apache Lucene library.
 * 
 * Copyright 2006 The Apache Software Foundation
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: StopWords.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class StopWords {

	// private set of stop words
	private static Set<String> stopWords = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
			"a", "an", "and", "are", "as", "at", "be", "but", "by",
		    "for", "if", "in", "into", "is", "it",
		    "no", "not", "of", "on", "or", "such",
		    "that", "the", "their", "then", "there", "these",
		    "they", "this", "to", "was", "will", "with")));
	
	/**
	 * Returns a set of some common English words that are not usually useful
	 * for searching.
	 * 
	 * @return the set of stop words
	 * @since jMWE 1.0.0
	 */
	public static Set<String> get(){
		return stopWords;
	}

	/**
	 * Returns true if the given lemma is a stop word.
	 * 
	 * @param lemma
	 *            the lemma to be checked
	 * @return true if the given lemma is a stop word.
	 * @since jMWE 1.0.0
	 */
	public static boolean isStopWord(String lemma){
		return stopWords.contains(lemma);
	}

	/**
	 * Returns <code>true</code> if each form in the list is a stop word;
	 * <code>false</code> otherwise.
	 * 
	 * @param words
	 *            the collection of stop words to be checked
	 * @return <code>true</code> if each form in the list is a stop word;
	 *         <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	public static boolean hasAllStopWords(Collection<? extends IHasForm> words){
		for(IHasForm word : words)
			if(!get().contains(word.getForm().toLowerCase()))
				return false;
		return true;
	}

}

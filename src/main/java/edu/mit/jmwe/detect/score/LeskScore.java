/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.data.StopWords;
import edu.mit.jmwe.util.JWIPOS;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * Scores an object with its lesk-score overlap with dictionary glosses.
 * 
 * @param <T>
 *            the type of token used by this scorer
 * @author M.A. Finlayson
 * @version $Id: LeskScore.java 327 2011-05-08 21:13:58Z markaf $
 * @since jMWE 1.0.0
 */
public class LeskScore<T extends IToken> extends AbstractScorer<IMWE<T>> {
	
	// final instance fields
	protected final Set<String> contextWords;
	protected final IDictionary dict;
	protected final IStemmer stemmer;

	/**
	 * Constructs a new lesk scorer for the specified sentence and dictionary.
	 * 
	 * @param sentence
	 *            the sentence for the scorer
	 * @param dict
	 *            the dictionary to be used by the scorer; may not be
	 *            <code>null</code>
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public LeskScore(List<T> sentence, IDictionary dict){
		if(dict == null)
			throw new NullPointerException();
		this.dict = dict;
		this.stemmer = new WordnetStemmer(dict);
		
		StringBuilder sb = new StringBuilder();
		for(T token: sentence)
			sb.append(token.getForm()+" ");
		List<String> contextWords = getContentWords(sb.toString());
		Set<String> contextSet = getStemmedWords(contextWords);
		this.contextWords = Collections.unmodifiableSet(contextSet);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.score.IScorer#score(java.lang.Object)
	 */
	public double score(IMWE<T> mwe) {
		List<String> glosses = getGlosses(mwe.getEntry().getForm(), mwe.getEntry().getPOS());
		if(glosses.isEmpty())
			return 0.0;
		
		// construct a list of context words for the MWE

		
		// find the best overlap between the words in the mwe
		// and the glosses
		double bestScore = 0;
		double overlap;
		for(String gloss : glosses){
			overlap = overlap(gloss);
			if(overlap > bestScore)
				bestScore = overlap;
		}
		return bestScore;
	}
	
	protected static final Pattern whitespace = Pattern.compile("\\s+");
	protected static final Pattern punctuation = Pattern.compile("\\p{Punct}");

	/**
	 * Given a string representation of a sentence, removes all punctuation and
	 * stop words. Returns a list of the remaining content words (assuming words
	 * are delimited by whitespace).
	 * 
	 * @param str the string from which the content words will be extracted
	 * @return a list of all the content words in the string, in lower case.
	 * @since jMWE 1.0.0
	 */
	protected List<String> getContentWords(String str){
		
		// normalize, getting rid of punctuation
		str = punctuation.matcher(str.toLowerCase()).replaceAll("");
		
		// split on whitespace into words
		String[] wordArray = whitespace.split(str);
		List<String> wordList = new LinkedList<String>(Arrays.asList(wordArray));
		
		// get rid of stop words
		Set<String> stopWords = getStopWords();
		for(Iterator<String> i = wordList.iterator(); i.hasNext();)
			if(stopWords.contains(i.next()))
					i.remove();
		
		// return result
		return wordList;
	}

	/**
	 * Returns the set of stop words for this scorer.
	 * 
	 * @return the set of stop words for this scorer
	 * @since jMWE 1.0.0
	 */
	protected Set<String> getStopWords(){
		return StopWords.get();
	}

	/**
	 * Returns a list of the glosses of a word or MWE by looking up its lemma
	 * and part of speech in the dictionary.
	 * 
	 * @param lemma
	 *            the lemma of the word or MWE
	 * @param pos
	 *            the part of speech of the word. If it is a proper noun, this
	 *            method will try looking up the word as a noun, just in case it
	 *            is listed as such in the dictionary.
	 * @return a list of the glosses of a word or MWE, empty if none were found.
	 * @since jMWE 1.0.0
	 */
	protected List<String> getGlosses(String lemma, MWEPOS pos){
		
		// transform jMWE pos to JWI pos
		POS p = (pos.getIdentifier() == 'P') ? POS.NOUN : JWIPOS.toPOS(pos);
		if(p == null)
			return Collections.emptyList();
		
		// get the relevant index word
		IIndexWord word = dict.getIndexWord(lemma, p);
		if(word == null)
			return Collections.emptyList();
		
		// collect all glosses and return
		List<String> glosses = new ArrayList<String>();
		for(IWordID id : word.getWordIDs())
			glosses.add(dict.getWord(id).getSynset().getGloss());
		return glosses;
	}

	/**
	 * Returns the number of elements the gloss has in common with the stemmed
	 * word list
	 * 
	 * @param gloss
	 *            the gloss
	 * @return the number of elements in common
	 * @since jMWE 1.0.0
	 */
	protected int overlap(String gloss) {
		List<String> wordList = getContentWords(gloss);
		Set<String> wordSet = getStemmedWords(wordList);
		wordSet.retainAll(contextWords);
		return wordSet.size();
	}

	/**
	 * Returns a set of string containing all the string in the specified list, as well as all the stemmed versions of those strings.
	 * 
	 * @param words
	 *            the collection of strings to be stemmed
	 * @return all the words and all of their stems
	 * @since jMWE 1.0.0
	 */
	protected Set<String> getStemmedWords(Collection<String> words) {
		Set<String> result = new HashSet<String>(words);
		result.removeAll(getStopWords());
		for(String word : words)
			result.addAll(stemmer.findStems(word, null));
		return result;
	}
}

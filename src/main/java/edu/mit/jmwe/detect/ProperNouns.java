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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IMWEDesc.IPart;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.data.RootMWEDesc;
import edu.mit.jmwe.data.MWEPOS;

/**
 * Detects the proper nouns in an ISentence. For this detector to work, the ITokens in the sentence must
 * be tagged with a part of speech. 
 *
 * @author N. Kulkarni
 * @version $Id: ProperNouns.java 317 2011-05-06 20:05:20Z markaf $
 * @since jMWE 1.0.0
 */
public class ProperNouns implements IMWEDetector { 
	
	// the singleton instance
	private static ProperNouns instance = null;

	/**
	 * Returns the singleton instance of this class, instantiating if necessary.
	 *
	 * @return the singleton instance of this class
	 * @since jMWE 1.0.0
	 */
	public static ProperNouns getInstance() {
		if (instance == null) 
			instance = new ProperNouns();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 *
	 * @since jMWE 1.0.0
	 */
	protected ProperNouns() {}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#findMWETokens(edu.mit.jmwe.data.ISentence)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		List<IMWE<T>> result = new LinkedList<IMWE<T>>();
		LinkedList<T> tokens = null;
		List<String> parts;
		Map<T,IPart> idxparts;
		IRootMWEDesc mweDesc;
		boolean foundEnd = false;
		
		for(T token: sentence){
			if(sentence.indexOf(token) == (sentence.size()-1)) foundEnd = true;
			if(isProperNoun(token)){
				if(tokens == null) tokens = new LinkedList<T>();
				tokens.add(token);
			} else {
				if(tokens == null) continue;
				if(isValidInterstitial(token, tokens)){
					tokens.add(token);
					foundEnd = false;
				} else {
					removeIncorrectInterstitials(tokens);
					foundEnd = true;
					if(tokens.isEmpty()||tokens.size()==1){
						tokens = null;
						foundEnd = false;
						continue;	
					}
				}
			}
			
			if(foundEnd && tokens.size()>1){
				parts = new ArrayList<String>(tokens.size());
				for(T t : tokens)
					parts.add(t.getForm());
				mweDesc = new RootMWEDesc(parts, MWEPOS.PROPER_NOUN);
				idxparts = new LinkedHashMap<T, IPart>();
				for(int i = 0; i < tokens.size(); i++)
					idxparts.put(tokens.get(i), mweDesc.getParts().get(i));
				result.add(new MWE<T>(idxparts));
				tokens = null;
				foundEnd = false;
			}
		}
		
		Collections.sort(result, new MWEComparator<T>(sentence));
		return result;
	}

	/**
	 * Checks if a token that is not a proper noun may still be a part of a
	 * proper noun MWE. A valid interstitial is a preposition, number,
	 * determiner, 's, or . after a middle initial.
	 * 
	 * @param <T> the type of token
	 * @param token
	 *            is the token to check
	 * @param tokens
	 *            List of {@link IToken} objects representing a possible MWE
	 * @return True if the token is a preposition, number, determiner, 's, or .
	 *         after a middle initial.
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken> boolean isValidInterstitial(T token, LinkedList<T> tokens){
		return false;
//		boolean result = false;
//		String tag = token.getTag();
//		String lastText = tokens.getLast().getText();
//		String lastTag = tokens.getLast().getTag();
//		
//		if(tag.equalsIgnoreCase("IN")||tag.equalsIgnoreCase("CD"))//preposition or cardinal number
//			result = true;
//		else if(tag.equalsIgnoreCase("DT") && lastTag.equalsIgnoreCase("IN"))//determiner after a preposition
//			result = true;
//		else if(tag.equals(".") && lastText.matches("[A-Z]"))//period in a middle initial as in Elizabeth A. Jones
//			result = true;
//		else if(tag.equals("POS"))//possessive ending
//			result = true;
//		return result;
		
	}
	
	/**
	 * Removes all the tokens from the end of the given list that are not proper
	 * nouns. Stops removing tokens as soon as the last element of the list is a
	 * proper noun.
	 * 
	 * @param <T> the type of tokens in the list
	 * @param cs
	 *            the list from which tokens will be removed until the last
	 *            token is a proper noun
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken> void removeIncorrectInterstitials(LinkedList<T> cs){
		if (cs.isEmpty()) return;
		while(!isProperNoun(cs.getLast())){
			cs.removeLast();
			if (cs.isEmpty()) return;
		}
	}

	/**
	 * Checks if the token represents a proper noun by checking its part of
	 * speech tag.
	 * 
	 * @param <T> the type of token
	 * @param token
	 *            is the token to check
	 * @return true if the token is a proper noun
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isProperNoun(T token) {
		String text = token.getForm();
		if(text.equals("(")||text.equals(")")||text.equals("\""))
			return false;
		return MWEPOS.toMWEPOS(token.getTag()).equals(MWEPOS.PROPER_NOUN);
	}

}

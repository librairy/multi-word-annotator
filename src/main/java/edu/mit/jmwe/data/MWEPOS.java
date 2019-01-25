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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents the part of speech of a multi-word expression.
 *
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: MWEPOS.java 290 2011-05-05 19:54:11Z markaf $
 * @since jMWE 1.0.0
 */
public enum MWEPOS {
	
	// Note: these are in alphabetical order of character identifier.
	// This is important for searching.
	ADJECTIVE  ('J', "JJ"), 
	NOUN       ('N', "NN"), 
	OTHER      ('O', (String[])null),
	PROPER_NOUN('P', "NNP"),
	ADVERB     ('R', "RB","WRB"),
	VERB       ('V', "VB");

	// final instance fields
	private final char identifier;
	private final Set<String> prefixes;

	/**
	 * Private enum constructor that takes the character identifier for the
	 * part-of-speech
	 * 
	 * @param identifier
	 *            the character identifier for the part-of-speech
	 * @param prefixes
	 *            the prefixes for the part of speech. All tags for this part of
	 *            speech will begin with one of these prefixes.
	 * @since jMWE 1.0.0
	 */
	private MWEPOS(char identifier, String... prefixes){
		
		Set<String> pfxs;
		if(prefixes == null){
			pfxs = Collections.emptySet();
		} else {
			for(int i = 0; i < prefixes.length; i++){
				prefixes[i] = prefixes[i].trim();
				for(int j = 0; j < prefixes[i].length(); j++)
					if(Character.isWhitespace(prefixes[i].charAt(j)))
						throw new IllegalArgumentException();
			}
			pfxs = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(prefixes)));
		}
		
		this.identifier = identifier;
		this.prefixes = pfxs;
	}

	/**
	 * Returns the character identifier for the part-of-speech.
	 * 
	 * @return the character identifier for the part-of-speech.
	 * @since jMWE 1.0.0
	 */
    public char getIdentifier(){
    	return identifier;
	}

	/**
	 * Returns the set of prefixes for the part-of-speech. Will not return
	 * <code>null</code>, but may return an empty set.
	 * 
	 * @return the non-null, possibly empty set of prefixes for the
	 *         part-of-speech.
	 * @since jMWE 1.0.0
	 */
    public Set<String> getPrefixes(){
    	return prefixes;
    }
    
	// static fields
	private static Map<Character, MWEPOS> charMap;
	private static SortedMap<String, MWEPOS> prefixMap;
    
	// map initialization block
    static {
    	// initialize char map
    	charMap = new HashMap<Character, MWEPOS>(values().length);
    	for(MWEPOS pos : values()) 
    		charMap.put(pos.getIdentifier(), pos);
    	
    	// initialize prefix map
    	prefixMap = new TreeMap<String, MWEPOS>(Collections.reverseOrder());
    	for(MWEPOS pos : values()) 
    		for(String prefix : pos.getPrefixes()) 
    			prefixMap.put(prefix, pos);
    }

	/**
	 * This convenience method allows retrieval of the {@link MWEPOS} object
	 * given the part of speech character.
	 * 
	 * @param identifier
	 *            the identifier for which the MWEPOS is needed; if no MWEPOS
	 *            object corresponds to the specified identifier, this method
	 *            returns <code>null</code>
	 * @return the MWEPOS corresponding to the specified identifier
	 * @since jMWE 1.0.0
	 */
    public static MWEPOS fromChar(char identifier){
    	return charMap.get(identifier);
    }

	/**
	 * This convenience method allows retrieval of the {@link MWEPOS} object
	 * given the part of speech tag as a String.
	 * 
	 * @param tag
	 *            the part-of-speech tag to be converted to an
	 *            <code>MWEPOS</code> object
	 * @return the MWE part of speech object corresponding to the tag; if no
	 *         appropriate tag is found, will return the {@link #OTHER} object;
	 *         will never return <code>null</code>
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the argument is empty or all whitespace
	 * @since jMWE 1.0.0
	 */
    public static MWEPOS toMWEPOS(String tag){
    	if(tag == null)
    		throw new NullPointerException();
    	tag = tag.trim();
    	if(tag.length() == 0)
    		throw new IllegalArgumentException();
    	for(Entry<String, MWEPOS> entry : prefixMap.entrySet())
    		if(tag.startsWith(entry.getKey())) 
    			return entry.getValue();
    	return MWEPOS.OTHER;
    }

}

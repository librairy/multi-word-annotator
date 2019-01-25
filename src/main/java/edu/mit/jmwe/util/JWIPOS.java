/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.util;

import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jsemcor.term.IPOSTag;
import edu.mit.jwi.item.POS;

/**
 * Utility class used to translate part of speech tags into JWI {@code POS}
 * objects as well as translate between jMWE, JWI and jSemcor part of speech
 * objects.
 * 
 * This class requires the JWI and JSemcor libraries to be on the classpath. 
 * 
 * @author M.A. Finlayson
 * @version $Id: JWIPOS.java 346 2013-12-10 18:11:38Z markaf $
 * @since jMWE 1.0.0
 */
public class JWIPOS {
	
	/**
	 * Translates a JWI <code>POS</code> object into a jMWE {@link MWEPOS} object.
	 * 
	 * @param pos
	 *            the JWI part of speech to be translated
	 * @return the jMWE {@link MWEPOS} object that is the equivalent of
	 * @throws NullPointerException
	 *             if the specified part of speech is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified part of speech is unknown
	 * @since jMWE 1.0.0
	 */
	public static MWEPOS toMWEPOS(POS pos){
		switch(pos){
			case NOUN 		: 	return MWEPOS.NOUN;
			case ADJECTIVE 	: 	return MWEPOS.ADJECTIVE;
			case VERB 		: 	return MWEPOS.VERB;
			case ADVERB 	: 	return MWEPOS.ADVERB;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Translates a jMWE {@link MWEPOS} object into a JWI {@code POS} object.
	 * 
	 * @param pos
	 *            the jMWE part of speech to be translated
	 * @return the JWI {@code POS} object that is the equivalent,
	 *         <code>null</code> if the part of speech is unknown.
	 * @throws NullPointerException
	 *             if the specified part of speech is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static POS toPOS(MWEPOS pos){
		switch(pos){
			case NOUN:      return POS.NOUN;
			case VERB:      return POS.VERB;
			case ADJECTIVE: return POS.ADJECTIVE;
			case ADVERB:    return POS.ADVERB;
			default:		return null;
		}
	}

	/**
	 * Translates a JSemcor {@code IPOSTag} object into a JWI {@code POS}
	 * object.
	 * 
	 * @param tag
	 *            the jSemcor part of speech tag to be translated
	 * @return the JWI {@code POS} object that is the equivalent,
	 *         <code>null</code> if the part of speech is unknown.
	 * @throws NullPointerException
	 *             if the specified part of speech is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static POS toPOS(IPOSTag tag){
		return toPOS(tag.getValue());
	}

	/**
	 * Translates a pos tag into a JWI {@code POS} object.
	 * 
	 * @param tag
	 *            the part of speech tag to be translated
	 * @return the JWI {@code POS} object that is the equivalent,
	 *         <code>null</code> if the part of speech is unknown.
	 * @throws NullPointerException
	 *             if the specified part of speech is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static POS toPOS(String tag){
		MWEPOS pos = MWEPOS.toMWEPOS(tag);
		return toPOS(pos);
	}
	
}

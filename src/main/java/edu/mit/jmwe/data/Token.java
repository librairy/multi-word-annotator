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
import java.util.List;

/**
 * Default implementation of the <code>IToken</code> interface.
 *
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: Token.java 348 2015-11-13 18:28:49Z markaf $
 * @since jMWE 1.0.0
 */
public class Token implements IToken {
	
	// final instance fields
	private final String tag, text;
	private final List<String> stems;

	/**
	 * Constructs a new token object with the specified text and tag,
	 * with no stems yet assigned.
	 * 
	 * @param text
	 *            the surface form of the token as it appears in the sentence,
	 *            capitalization intact
	 * @param tag
	 *            the tag of the token, if assigned, otherwise <code>null</code>
	 * @throws NullPointerException
	 *             if the text is <code>null</code>
	 * @throws NullPointerException
	 *             if the text is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the trimmed text is empty or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public Token(String text, String tag){
		this(text, tag, (String[])null);
	}

	/**
	 * Constructs a new token object with the specified text, tag, and stems.
	 * 
	 * @param text
	 *            the surface form of the token as it appears in the sentence,
	 *            capitalization intact
	 * @param tag
	 *            the tag of the token, if assigned, otherwise <code>null</code>
	 * @param stems
	 *            the array of stems, possibly empty or <code>null</code>, but
	 *            not containing <code>null</code>. If <code>null</code>, this
	 *            means that no stemming has yet been attempted. If empty, this
	 *            means the token is not stemmable.
	 * @throws NullPointerException
	 *             if the text is <code>null</code>, or any of the stems are
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the trimmed text is empty or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public Token(String text, String tag, String... stems){
		
		// check arguments
		text = checkString(text);
		List<String> stemList = checkStems(stems);

		// assign fields
		this.text = text;
		this.tag = tag;
		this.stems = stemList;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasForm#getForm()
	 */
	public String getForm() {
		return text;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IToken#getTag()
	 */
	public String getTag() {
		return tag;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IToken#getStems()
	 */
	public List<String> getStems() {
		return stems;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return text + "_" + tag;
	}

	/**
	 * Checks the specified string to see that, once trimmed, it is not empty
	 * and does not contain whitespace. If not, the trimmed string is returned.
	 * Otherwise, the method throws an exception.
	 * 
	 * @throws NullPointerException
	 *             if the specified String is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if, after being trimmed, the specified String is empty, or
	 *             contains whitespace or an underscore
	 * @param text
	 *            the text to be checked
	 * @return the trimmed String
	 * @since jMWE 1.0.0
	 */
	public static String checkString(String text){
		if(text == null)
			throw new NullPointerException();
		text = text.trim();
		if(text.length() == 0)
			throw new IllegalArgumentException();
		char c;
		for(int i = 0; i < text.length(); i++){
			c = text.charAt(i);
			if(Character.isWhitespace(c))
				throw new IllegalArgumentException("Whitespace found in text '" + text + "'");
			// added this check
			if(c == '_')
				throw new IllegalArgumentException("Underscore found in text '" + text + "'");
		}
		return text;
	}

	/**
	 * Checks the specified array of strings to ensure each one is non-
	 * <code>null</code>, and, once trimmed, is not empty and does not contain
	 * whitespace or an underscore. If all strings check out, an unmodifiable
	 * list of the trimmed, lowercase strings is returned. Otherwise, the method
	 * throws an exception.
	 * 
	 * @param stems
	 *            the list of stems to check; may be <code>null</code> or empty,
	 *            but may not contain <code>null</code>
	 * @return an unmodifiable list of trimmed, lowercase strings
	 * @throws NullPointerException
	 *             if the any string in the array is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if, after being trimmed, any string in the array is empty or
	 *             contains whitespace
	 * @since jMWE 1.0.0
	 */
	public static List<String> checkStems(String[] stems){
		if(stems == null)
			return null;
		if(stems.length == 0)
			return Collections.emptyList();
		for(int i = 0; i < stems.length; i++){
			if(stems[i] == null)
				throw new NullPointerException("null stem at index " + i);
			stems[i] = checkString(stems[i]).toLowerCase();
		}
		return Collections.unmodifiableList(Arrays.asList(stems));
	}	

}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

/**
 * Indicates an object represents a punctuation element in a Context file.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IPunc extends IToken {

	/**
	 * The SGML tag used to indicate a punctuation element.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_PUNC_VALUE = "punc";

	/**
	 * String used to start punctuation expressions.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_PUNC_START = "<punc>";

	/**
	 * String used to end punctuation expressions.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_PUNC_END = "</punc>";

	/**
	 * Returns a user-readable name that identifies this punctuation object.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getName();

	/**
	 * Returns the symbol (one or possibly two characters) used to identify this
	 * punctuation in context files.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getSymbol();

	/**
	 * Returns the character that is used to display this punctuation on screen.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public char getDisplayCharacter();

}

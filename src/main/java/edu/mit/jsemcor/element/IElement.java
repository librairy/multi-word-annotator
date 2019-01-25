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
 * Represents a generic element in an SGML-like context file.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IElement {

	/**
	 * The space character, used to separate attribute/value pairs within tags.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char SPACE = ' ';

	/**
	 * The space string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String SPACE_STR = Character.toString(SPACE);

	/**
	 * The underscore character, used to separate words in collocations within
	 * tags.
	 */
	public static final char UNDERSCORE = '_';

	/**
	 * The underscore string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String UNDERSCORE_STR = Character.toString(UNDERSCORE);

	/**
	 * The newline character, used to separate lines, naturally.
	 */
	public static final char NEWLINE = '\n';

	/**
	 * Character used to open tags, {@code <}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char LEFT_ANGLE_BRACKET = '<';

	/**
	 * Character used to start the right-hand tag, {@code /}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char FORWARD_SLASH = '/';

	/**
	 * Character used to close tags, {@code >}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char RIGHT_ANGLE_BRACKET = '>';

	/**
	 * The equals character, used to separate the attribute name from the
	 * value in SGML-like representations of this terminal element.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char EQUALS = '=';

	/**
	 * The quote character, which is used at the beginning of values that need
	 * to be surrounded by quotation marks in context files.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char OPEN_QUOTE = '"';

	/**
	 * The quote character, which is used at the end of values that need to be
	 * surrounded by quotation marks in context files.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final char CLOSE_QUOTE = '"';

	/**
	 * Returns the {@link String} representation of this element, as it would be
	 * found in the concordance file.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getData();

}

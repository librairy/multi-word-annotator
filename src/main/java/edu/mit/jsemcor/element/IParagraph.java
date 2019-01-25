/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.List;

/**
 * Represents a paragraph in a context. Contexts may or may not have paragraphs.
 * IParagraph objects are composed of {@link ISentence} objects.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IParagraph extends List<ISentence>, IElement {

	/**
	 * The first part of the tag that marks the beginning of a paragraph.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_PARA_LEFT = "<p";

	/**
	 * The unadorned paragraph starting tag.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_PARA_NAME = TAG_PARA_LEFT
			+ RIGHT_ANGLE_BRACKET;

	/**
	 * The tag that marks the end of a paragraph.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_PARA_RIGHT = "</p>";

	/**
	 * The attributes that marks the number of a sentence.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_PNUM = "pnum";

	/**
	 * Returns the number of the paragraph. Will always be 1 or greater.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public int getNumber();

}

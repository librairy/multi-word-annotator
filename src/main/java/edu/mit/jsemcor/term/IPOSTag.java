/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.term;

/**
 * Indicates that this object represents a particular part of speech.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IPOSTag extends ITerminal {

	/**
	 * The attribute tag used in SGML-like format context files for these
	 * objects.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_POS = "pos";

	/**
	 * A human-readable interpretation of what this part of speech represents.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getMeaning();

}

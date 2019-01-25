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
 * Indicates that the object represents a category into which a proper noun
 * could be categorized.
 * 
 * @author M.A. Finlayson
 * @version 1.52, 19 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ICategory extends ITerminal {

	/**
	 * The attribute tag used in SGML-like format context files for these
	 * category objects, 'pn'
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_PN = "pn";

}

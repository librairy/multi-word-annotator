/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.term;

import edu.mit.jsemcor.element.IWordform;

/**
 * Indicates this object represents a type of note that may be attached to an
 * {@link IWordform} object.
 * 
 * @author M.A. Finlayson
 * @version 1.52, 19 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface INoteType extends ITerminal {

	/**
	 * The attribute tag used in SGML-like format context files for these
	 * category objects, 'tagnote'
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_TAGNOTE = "tagnote";

}

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
 * Represents a command tag in an {@link IWordform} object.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ICommand extends ITerminal {

	/**
	 * The attribute tag used in SGML-like format context files for these
	 * objects.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_CMD = "cmd";

	/**
	 * Returns a human-readable description of what this tag means, if anything.
	 * Will not return <code>null</code>, but may return the empty string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getMeaning();

}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.term;

import edu.mit.jsemcor.element.IElement;

/**
 * Terminals are name-value pairs that attached to concordance elements.
 * 
 * @author M.A. Finlayson
 * @version 1.52, 19 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ITerminal extends IElement {

	/**
	 * Returns the attribute string for this terminal type.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getAttribute();

	/**
	 * Returns the tag value for this terminal type. In the case of values that
	 * must be surrounded by quotes, the string returned by this method does not
	 * include the quotes.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getValue();

}

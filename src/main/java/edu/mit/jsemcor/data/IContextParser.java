/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.data;

import java.io.IOException;
import java.io.Reader;

import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.IContextID;

/**
 * Objects implementing this interface have the ability to construct
 * {@link IContext} objects from raw string data.
 * 
 * @author M.A. Finlayson
 * @version 1.156, 21 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IContextParser {
	
	/**
	 * Return an {@link IContext} object with the specified {@link IContextID}
	 * with data corresponding to the characters returned by the specified
	 * {@link Reader} source.
	 * 
	 * @return the {@link IContext} object constructed from the character data,
	 *         or <code>null</code> if no such the object could be constructed
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @throws IOException
	 *             if there is an IO problem accessing the character data
	 * @since JSemcor 1.0.0
	 */
	public IContext parse(IContextID id, Reader source) throws IOException ;
	
}

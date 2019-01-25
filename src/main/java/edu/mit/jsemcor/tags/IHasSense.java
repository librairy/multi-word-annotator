/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

/**
 * Classes implementing this interface have a 'sense', which is a string and a
 * number corresponding to a Wordnet sense key and the numbered sense within
 * that key.
 *
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IHasSense {

	/**
	 * Returns the sense key for this object.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getSenseKey();

	/**
	 * Returns the sense number for this object.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public int getSenseNumber();

}

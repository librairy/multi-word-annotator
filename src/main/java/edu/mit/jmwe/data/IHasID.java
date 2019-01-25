/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

/**
 * An object with a unique id. The id may be unique for the instance or the
 * class, depending on the implementation.
 * 
 * @author M.A. Finlayson
 * @version $Id: IHasID.java 276 2011-05-05 19:34:38Z markaf $
 * @since jMWE 1.0.0
 */
public interface IHasID {

	/**
	 * Returns an ID string that uniquely identifies this object or object type.
	 * Should never return <code>null</code>.
	 * 
	 * @return the non-null id String
	 * @since jMWE 1.0.0
	 */
	public String getID();

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

/**
 * Classes implementing this interface use a {@link IMWEIndex}.
 *
 * @author M.A. Finlayson
 * @version $Id: IHasMWEIndex.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
public interface IHasMWEIndex {
	
	/**
	 * Returns the index this object wraps.
	 * 
	 * @return the index this object wraps.
	 * @since jMWE 1.0.0
	 */
	public IMWEIndex getMWEIndex();

}

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
 * An object that is one of the parts of speech enumerated in {@link MWEPOS}.
 *
 * @author M.A. Finlayson
 * @version $Id: IHasMWEPOS.java 277 2011-05-05 19:35:04Z markaf $
 * @since jMWE 1.0.0
 */
public interface IHasMWEPOS {

	/**
	 * Returns the part of speech of this object. May not return
	 * <code>null</code>.
	 * 
	 * @return the non-null part of speech of the MWE.
	 * @since jMWE 1.0.0
	 */
	public MWEPOS getPOS();

}

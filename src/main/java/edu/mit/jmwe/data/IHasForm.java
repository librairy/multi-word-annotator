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
 * Items that have a textual surface form implement this interface.
 *
 * @author M.A. Finlayson
 * @version $Id: IHasForm.java 274 2011-05-05 19:33:58Z markaf $
 * @since jMWE 1.0.0
 */
public interface IHasForm {
	
	/**
	 * Returns the object's surface form text, exactly as it appears in its
	 * original context, with capitalization intact. May be a single word or
	 * punctuation. The surface form may not contain whitespace or underscores.
	 * This method will never return <code>null</code>.
	 * 
	 * @return the original text, never <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public String getForm();

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.regex.Pattern;

/**
 * A MWE description ID that can be used to retrieve the MWE's lemma, part of
 * speech, and inflected form.
 * 
 * @author M.A. Finlayson
 * @version $Id: IMWEDescID.java 282 2011-05-05 19:40:35Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWEDescID extends IHasForm, IHasMWEPOS, Comparable<IMWEDescID> {

	/**
	 * Captures the format of a well-formed form associated with this ID. The
	 * form must have its parts delimited by underscores.
	 * 
	 * @since jMWE 1.0.0
	 */
	public Pattern formPattern = Pattern.compile("([\\S&&[^_]]+_)+([\\S&&[^_]]+)");

	/**
	 * Returns the root ID of this decription ID. The root ID contains the MWE's
	 * lemma and part of speech.
	 * 
	 * @return the root ID of this decription ID
	 * @since jMWE 1.0.0
	 */
	public IMWEDescID getRootID();

	/**
	 * Returns an inflected form of the MWE associated with this ID.
	 * 
	 * @return the inflected form of this MWE.
	 * @since jMWE 1.0.0
	 */
	public String getInflectedForm();
}

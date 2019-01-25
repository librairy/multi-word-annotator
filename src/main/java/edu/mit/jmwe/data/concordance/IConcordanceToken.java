/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data.concordance;

import edu.mit.jmwe.data.IToken;

/**
 * A token from a Semcor sentence.
 * 
 * @author M.A. Finlayson
 * @version $Id: IConcordanceToken.java 326 2011-05-08 20:21:37Z markaf $
 * @since jMWE 1.0.0
 */
public interface IConcordanceToken extends IToken {

	/**
	 * Returns the index of the token in the Semcor sentence from which it was
	 * extracted.
	 * 
	 * @return the token number of the token in the Semcor sentence from which
	 *         it was extracted.
	 * @since jMWE 1.0.0
	 */
	public int getTokenNumber();

	/**
	 * Returns the index of the part in the Semcor token from which this part
	 * was extracted. This number will be greater than zero only if the token
	 * was originally a part of a multi-word expression in Semcor.
	 * 
	 * @return the index of the part in the Semcor token from which this part
	 *         was extracted.
	 * @since jMWE 1.0.0
	 */
	public int getPartNumber();
	

}

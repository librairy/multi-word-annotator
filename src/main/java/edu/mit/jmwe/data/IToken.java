/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.List;

/**
 * A token that is a constituent of an sentence. They may represent single runs
 * of non-whitespace characters (not containing whitespace) or punctuation.
 * Tokens may or may not be tagged, and may or may not already have stems
 * assigned.
 * 
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: IToken.java 285 2011-05-05 19:44:16Z markaf $
 * @since jMWE 1.0.0
 */
public interface IToken extends IHasForm {

	/**
	 * Returns the part of speech tag for this token, or <code>null</code> if
	 * the token is not tagged. If the part of speech is <code>null</code>, no
	 * part of speech has yet been assigned.
	 * 
	 * @return the part of speech tag for this token, or <code>null</code> if
	 *         the token is not tagged.
	 * @since jMWE 1.0.0
	 */
	public String getTag();

	/**
	 * Returns an unmodifiable list of stems, all in lowercase. The order of the
	 * stems depends on the implementation. No stem should be repeated in the
	 * list. If the method returns an empty list, this means that the token is
	 * not stemmable. If the method returns <code>null</code>, this means no
	 * stemming has yet been attempted.
	 * 
	 * @return a possibly <code>null</code>, possibly empty list of lowercase
	 *         stems
	 * @since jMWE 1.0.0
	 */
	public List<String> getStems();

	public Long getOffset();

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Represents a pattern according to which the parts of a multi-word expression
 * may be inflected.
 * 
 * @author N. Kulkarni
 * @version $Id: IInflectionRule.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public interface IInflectionRule {

	/**
	 * Returns <code>true</code> if this MWE follows the rule;
	 * <code>false</code> otherwise.
	 * 
	 * @param <T>
	 *            the type of tokens in the MWE
	 * @param mwe
	 *            the MWE to which the rule may/may not be applied
	 * @throws IllegalArgumentException
	 *             if this rule may not be applied to the given MWE. May only
	 *             throw this exception if the <code>matches(IMWE&lt;T&gt; mwe)</code>
	 *             method returns false.
	 * @return <code>true</code> if this MWE follows the rule;
	 *         <code>false</code> otherwise.
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken> boolean isValid(IMWE<T> mwe);

	/**
	 * Returns <code>true</code> if the given MWE has the same syntax as this
	 * rule. In other words, returns <code>true</code> if this rule may be
	 * applied to the given MWE; <code>false</code> otherwise.
	 * 
	 * @param <T>
	 *            the type of tokens in the MWE
	 * @param mwe
	 *            the MWE to which the rule may/may not be applied
	 * @return returns <code>true</code> if this rule may be applied to the
	 *         given MWE, <code>false</code> otherwise.
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken> boolean matches(IMWE<T> mwe);

}

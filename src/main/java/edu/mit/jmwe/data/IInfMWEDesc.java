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
 * A description of an inflected form of a multi-word expression (MWE). Included
 * are {@link IRootMWEDesc} of the multi-word expression and a list of inflected
 * parts that comprise this form.
 * 
 * @author M.A. Finlayson
 * @version $Id: IInfMWEDesc.java 278 2011-05-05 19:36:38Z markaf $
 * @since jMWE 1.0.0
 */
public interface IInfMWEDesc extends IMWEDesc {
	
	/**
	 * Expected number of counts associated with an index entry that implements
	 * this interface.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static final int EXPECTED_COUNT_LENGTH = 5;

	/**
	 * Returns the root description of the multi-word expression (MWE). IThis
	 * description includes the lemma, a list of parts, and part of speech of
	 * the multi-word expression.
	 * 
	 * @return the description of the non-inflected form of this multi-word
	 *         expression (MWE).
	 * @since jMWE 1.0.0
	 */
	public IRootMWEDesc getRootMWEDesc();

	/**
	 * Returns an unmodifiable list of parts that comprise the MWE.
	 * 
	 * @return an unmodifiable list of parts that comprise the MWE.
	 * @since jMWE 1.0.0
	 */
	public List<? extends IInfPart> getParts();

	/**
	 * The number of times this description occurs in the reference concordance
	 * being marked as an occurrence of the MWE, while matching one of the known
	 * inflection patterns. To be counted as a pattern-inflected unmarked
	 * occurrence, there must be a continuous run of tokens whose forms or stems
	 * match, in order, the forms of the parts (ignoring case) of this MWE
	 * description, and whose inflection pattern matches one of reference
	 * inflection patterns. Will always zero or a positive number.
	 * 
	 * @return the number of inflected unmarked occurrences of this MWE in the
	 *         reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int getMarkedPattern();
	
	/**
	 * A part of an inflected multi-word expression.
	 *
	 * @author M.A. Finlayson
	 * @version $Id: IInfMWEDesc.java 278 2011-05-05 19:36:38Z markaf $
	 * @since jMWE 1.0.0
	 */
	public interface IInfPart extends IPart {
		
		/**
		 * Returns the MWE description of which this part is a part.
		 * Will never return <code>null</code>.
		 * 
		 * @return the MWE description that contains this part.
		 * @since jMWE 1.0.0
		 */
		public IInfMWEDesc getParent();
		
	}

}

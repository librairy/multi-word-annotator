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
import java.util.Map;

/**
 * A description of a multi-word expression (MWE). Included are the lemma, a
 * list of parts, and part of speech of the multi-word expression.
 * 
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: IRootMWEDesc.java 284 2011-05-05 19:43:43Z markaf $
 * @since jMWE 1.0.0
 */
public interface IRootMWEDesc extends IMWEDesc {
	
	/**
	 * Expected number of counts associated with an index entry that implements
	 * this interface.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static final int EXPECTED_COUNT_LENGTH = 5;

	/**
	 * Returns an unmodifiable list of parts that comprise the MWE.
	 * 
	 * @return an unmodifiable list of parts that comprise the MWE.
	 * @since jMWE 1.0.0
	 */
	public List<? extends IRootPart> getParts();

	/**
	 * Returns an unmodifiable set of MWE descriptions corresponding to the
	 * inflected versions of this form. If the method returns an empty set, this
	 * means that the expression cannot be inflected. If the method returns
	 * <code>null</code>, this means the no inflected forms have yet been
	 * assigned.
	 * 
	 * @return a possibly <code>null</code>, possibly empty set of inflected MWE
	 *         descriptions.
	 * @since jMWE 1.0.0
	 */
	public Map<String, ? extends IInfMWEDesc> getInflected();

	/**
	 * Returns the number of times an inflected form of this MWE description
	 * occurs in the reference concordance without being marked as an occurrence
	 * of the MWE, and without being an exact match to a known inflected form of
	 * this root. To be counted as an inflected unmarked occurrence, there must
	 * be a continuous run of tokens whose forms or stems match, in order, the
	 * forms of the parts (ignoring case) of this MWE description. Will always
	 * zero or a positive number.
	 * 
	 * @return the number of inflected unmarked occurrences of this MWE in the
	 *         reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int getUnmarkedInflected();
		
	/**
	 * A part of a root multi-word expression.
	 *
	 * @author M.A. Finlayson
	 * @version $Id: IRootMWEDesc.java 284 2011-05-05 19:43:43Z markaf $
	 * @since jMWE 1.0.0
	 */
	public interface IRootPart extends IPart {

		/**
		 * Returns the MWE description of which this part is a part. Will never
		 * return <code>null</code>.
		 * 
		 * @return the MWE description that contains this part.
		 * @since jMWE 1.0.0
		 */
		public IRootMWEDesc getParent();
		
	
	}

	
}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import edu.mit.jsemcor.main.IConcordance;
import edu.mit.jsemcor.main.IConcordanceSet;

/**
 * A context id object provides enough information to retrieve a context from a
 * concordance or concordance set. Concordances take the information in the id
 * and, using an internal mapping between ids and data sources, resolve the
 * desired context object.
 * 
 * @see IConcordance#getContext(IContextID)
 * @see IConcordanceSet#getContext(IContextID)
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IContextID {

	/**
	 * Returns the name of this context. Will not return <code>null</code>,
	 * the empty string, or all whitespace.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getContextName();

	/**
	 * Returns the concordance for this context. Will not return
	 * <code>null</code>, the empty string, or all whitespace.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getConcordanceName();

}

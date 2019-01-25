/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

import java.util.Set;

import edu.mit.jmwe.data.IInfMWEDesc;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IRootMWEDesc;

/**
 * A set of MWE description objects that provides methods to retrieve the root
 * and inflected MWE descriptions it contains.
 * 
 * @author M.A. Finlayson
 * @version $Id: IMWEDescSet.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWEDescSet extends Set<IMWEDesc> {

	/**
	 * Makes this set unmodifiable. After this method has been called, attempted
	 * changes to the set will cause an {@link UnsupportedOperationException}.
	 * 
	 * @since jMWE 1.0.0
	 */
	public void makeUnmodifiable();

	/**
	 * Returns the set of root MWE desc objects contained herein.
	 * 
	 * @return the set of root MWE desc objects contained herein
	 * @since jMWE 1.0.0
	 */
	public Set<IRootMWEDesc> getRootMWEDescs();

	/**
	 * Returns the set of inflected MWE desc objects contained herein.
	 * 
	 * @return the set of inflected MWE desc objects contained herein
	 * @since jMWE 1.0.0
	 */
	public Set<IInfMWEDesc> getInflectedMWEDescs();

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

/**
 * An object that wraps an MWE index.
 * 
 * @author M.A. Finlayson
 * @version 1.552, 05 May 2011
 * @since jMWE 1.0.0
 */
public class HasMWEIndex implements IHasMWEIndex {

	// final instance field
	private final IMWEIndex index;

	/**
	 * Constructs a new object that has a pointer to an MWE index
	 * 
	 * @param index
	 *            the index wrapped by this object
	 * @throws NullPointerException
	 *             if the specified index is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public HasMWEIndex(IMWEIndex index) {
		if(index == null)
			throw new NullPointerException();
		this.index = index;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IHasMWEIndex#getMWEIndex()
	 */
	public IMWEIndex getMWEIndex() {
		return index;
	}

}

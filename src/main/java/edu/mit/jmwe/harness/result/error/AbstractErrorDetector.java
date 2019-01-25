/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result.error;

/**
 * Abstract base class implementation of the error detector interface that takes
 * care of the ID boilerplate.
 * 
 * @author M.A. Finlayson
 * @version $Id: AbstractErrorDetector.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class AbstractErrorDetector implements IErrorDetector {
	
	// final instance field
	private final String id;

	/**
	 * Constructs a new abstract error detector with the specified id.
	 * 
	 * @param id
	 *            the id
	 * @throws NullPointerException
	 *             if the id is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the id is empty or all whitespace
	 * @since jMWE 1.0.0
	 */
	public AbstractErrorDetector(String id){
		id = id.trim();
		if(id.length() == 0)
			throw new IllegalArgumentException();
		this.id = id;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasID#getID()
	 */
	public String getID() {
		return id;
	}

}

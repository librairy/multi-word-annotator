/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result.error;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.harness.result.ErrorResult.ErrorResultBuilder;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Error detectors extending this class are composed of multiple simpler error
 * detectors.
 * 
 * @author N. Kulkarni
 * @version $Id: CompositeErrorDetector.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public class CompositeErrorDetector extends AbstractList<IErrorDetector> implements IErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.composite";
	
	// instance fields set on construction
	private final IErrorDetector[] backingArray;
	
	/**
	 * Constructs a composite error detector from an array of error detectors.
	 * 
	 * @param detectors
	 *            the array of error detectors. May not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the list of error detectors is <code>null</code> or
	 *             contains <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified array is empty
	 * @since jMWE 1.0.0
	 */
	public CompositeErrorDetector(IErrorDetector... detectors){
		this(Arrays.asList(detectors));
	}
	
	/**
	 * Constructs a composite error detector from a list of error detectors.
	 * 
	 * @param detectors
	 *            the list of error detectors. May not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the list of error detectors is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public CompositeErrorDetector(List<? extends IErrorDetector> detectors){
		if(detectors.size() == 0)
			throw new IllegalArgumentException();
		this.backingArray = new IErrorDetector[detectors.size()];
		int i = 0;
		for(IErrorDetector d : detectors){
			if(d == null)
				throw new NullPointerException();
			backingArray[i++] = d;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasID#getID()
	 */
	public String getID(){
		return ID;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T,S> result){
		ErrorResultBuilder<T> builder = new ErrorResultBuilder<T>();
		for(IErrorDetector d : backingArray)
			builder.addDetail(d.detect(result));
		return builder.create();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public IErrorDetector get(int index) {
		return backingArray[index];
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return backingArray.length;
	}
	
}

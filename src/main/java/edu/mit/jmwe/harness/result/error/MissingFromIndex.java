/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IMWEDesc.IPart;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.harness.result.ErrorResult;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;
import edu.mit.jmwe.index.IMWEIndex;

/**
 * Finds those MWEs that were not found because they do not appear in the given
 * index.
 * 
 * @author N. Kulkarni
 * @version $Id: MissingFromIndex.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class MissingFromIndex extends AbstractErrorDetector {
	
	/**
	 * The ID for this error detector, {@value}
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String ID = "edu.mit.jmwe.error.Miss";
	
	// final instance field
	protected final IMWEIndex index;
	
	/**
	 * Constructs a new error detector that looks in the specified index for
	 * missing MWEs
	 * 
	 * @param index
	 *            the index which backs this detector
	 * @throws NullPointerException
	 *             if the specified index is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public MissingFromIndex(IMWEIndex index) {
		super(ID);
		if(index == null)
			throw new NullPointerException();
		this.index = index;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.error.IErrorDetector#detect(edu.mit.jmwe.harness.result.ISentenceResult)
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, S> result) {
		
		//find error MWEs
		List<IMWE<T>> problems = new ArrayList<IMWE<T>>();
		
		Map<MWEPOS,List<IMWE<T>>> map = new HashMap<MWEPOS,List<IMWE<T>>>(6);
		for(MWEPOS pos : MWEPOS.values()) map.put(pos, new ArrayList<IMWE<T>>());
		
		Map<String, List<IMWE<T>>> details = new HashMap<String, List<IMWE<T>>>(3);
	
		for(IMWE<T> mwe : result.getFalseNegatives())
			if(isProblem(mwe, index)){
				problems.add(mwe);
				map.get(mwe.getEntry().getPOS()).add(mwe);
			}
		details.put(getID()+".FalseNeg", new ArrayList<IMWE<T>>(problems));
		
		for(MWEPOS pos : MWEPOS.values())
			details.put(getID()+"."+pos.getIdentifier(), map.get(pos));
		
		return new ErrorResult<T>(details);
	}

	/**
	 * Determines if the specified MWE is a problem, relative to the specified
	 * index, according to this error class.
	 * 
	 * @param <T>
	 *            the token type of the MWE
	 * @param mwe
	 *            the MWE in question, may not be <code>null</code>
	 * @param index
	 *            the index to use, may not be <code>null</code>
	 * @return <code>true</code> if the MWE is a problem; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the specified MWE or index is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean isProblem(IMWE<T> mwe, IMWEIndex index){
		
		IMWEDesc desc = mwe.getEntry();
		//go through each part in desc
		for(IPart part : desc.getParts())
			if(index.get(part.getForm()).contains(desc))
				return false;
		//go through each token's text and its stems
		for(T t : mwe.getPartMap().keySet()){
			if(index.get(t.getForm().toLowerCase()).contains(desc))
				return false;
			if(t.getStems() != null)
				for(String stem: t.getStems())
					if(index.get(stem).contains(desc))
						return false;
		}
		
		//not listed in the index
		return true;
	}

}

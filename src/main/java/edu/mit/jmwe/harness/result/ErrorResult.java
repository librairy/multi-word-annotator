
/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;

/**
 * Default implementation of <code>IErrorResult</code> interface.
 * 
 * @param <T>
 *            the type of tokens that form the multi-word expressions stored in
 *            this result
 * @author N. Kulkarni
 * @version $Id: ErrorResult.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class ErrorResult<T extends IToken> implements IErrorResult<T>{

	// final instance fields
	private final Map<String, List<IMWE<T>>> details;
	private final int totalErrors;

	/**
	 * Constructs the error result that stores the given multi-word expressions
	 * under the given ID of the error class that they belong to. This
	 * constructor allocates a new internal map, and so subsequent changes to
	 * the source list will not affect this object.
	 * 
	 * @param errors
	 *            a non-null list of error multi-word expressions
	 * @param errorID
	 *            the ID of the error class that the multi-word expressions
	 *            belong to
	 * @throws NullPointerException
	 *             if the multi-word expression list is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public ErrorResult(String errorID, List<IMWE<T>> errors){
		this(Collections.singletonMap(errorID, errors));
	}

	/**
	 * Constructs the error result from a map that stores MWEs under the ID of
	 * the error class that they belong to. This constructor allocates a new
	 * internal map, and so subsequent changes to the source list will not
	 * affect this object.
	 * 
	 * @param details
	 *            a non-null map that stores multi-word expressions under the ID
	 *            of the error class that they belong to.
	 * @throws NullPointerException
	 *             if the map is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public ErrorResult(Map<String, List<IMWE<T>>> details){
		this(details, true);
	}
	
	/**
	 * Constructs the error result from a {@link Map} that stores multi-word
	 * expressions under the ID of the error class that they belong to.This
	 * constructor may or may not allocate a new internal map, depending on the
	 * value of the reallocation flag. If no reallocation is requested, this
	 * constructor reuses the given map, and so subsequent changes to the source
	 * list will affect this object.
	 * 
	 * @param details
	 *            a non-null map that stores multi-word expressions under the ID
	 *            of the error class that they belong to.
	 * @param reallocate
	 *            If true, will allocate a new internal map for details
	 * @throws NullPointerException
	 *             if the map is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public ErrorResult(Map<String, List<IMWE<T>>> details, boolean reallocate){
		this.details = reallocate ? Collections.unmodifiableMap(details) : details;
		int sum = 0;
		for(List<IMWE<T>> mwes : details.values()) 
			sum+= mwes.size();
		totalErrors = sum;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IErrorResult#getDetails()
	 */
	public Map<String, List<IMWE<T>>> getDetails() {
		return details;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IErrorResult#getTotalErrors()
	 */
	public int getTotalErrors() {
		return totalErrors;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IErrorResult#getNumErrors(java.lang.String)
	 */
	public int getNumErrors(String errorID) {
		List<IMWE<T>> list = details.get(errorID);
		return (list == null) ? 
				0 : 
					list.size();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return ErrorResult.toString(this);
	}
	
	/**
	 * Creates a table displaying the number of instances of each error class.
	 * 
	 * @param <T>
	 *            the type of tokens that form the multi-word expressions stored
	 *            in this result
	 * @param result
	 *            a non-null error result
	 * @return A String that displays the number of instances of each error
	 *         class in a table.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> String toString(IErrorResult<T> result){
		String strFmt = "%1$-60s";
		
		StringBuilder sb = new StringBuilder(1024);
		Formatter f = new Formatter(sb);
		
		f.format(strFmt, "Error");
		f.format(strFmt, "Instances");
		int length = sb.length();
		sb.append('\n');
		
		// add divider
		for(int i = 0; i < length; i++) sb.append('-');
		sb.append('\n');
		
		for(String error : result.getDetails().keySet()){
			f.format(strFmt, error);
			f.format(strFmt, result.getNumErrors(error));
			sb.append('\n');
		}
		
		// add divider
		for(int i = 0; i < length; i++) sb.append('-');
		sb.append('\n');
		
		//add total
		f.format(strFmt, "Total");
		f.format(strFmt, result.getTotalErrors());
		sb.append('\n');
		
		f.close();
		
		return sb.toString();
	}

	/**
	 * An object that builds an error result.
	 * 
	 * @param <T>
	 *            the token type
	 * @author M.A. Finlayson
	 * @version $Id: ErrorResult.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	public static class ErrorResultBuilder<T extends IToken> {
		
		// instance fields set dynamically
		private final Map<String, List<IMWE<T>>> details = new HashMap<String, List<IMWE<T>>>();
		
		/**
		 * Adds the specified error detail to this builder
		 * 
		 * @param result
		 *            the detail to be added to the error result builder
		 * @throws NullPointerException
		 *             if the specified result is <code>null</code>
		 * @since jMWE 1.0.0
		 */
		public void addDetail(IErrorResult<T> result) {
			List<IMWE<T>> list;
			for(Entry<String, List<IMWE<T>>> e : result.getDetails().entrySet()){
				list = details.get(e.getKey());
				if(list == null){
					list = new LinkedList<IMWE<T>>();
					details.put(e.getKey(), list);
				}
				list.addAll(e.getValue());
			}
		}

		/**
		 * Creates a new error result, or throws an exception if it cannot
		 * create a valid result.
		 * 
		 * @return a new error result capturing the information in the builder
		 * @since jMWE 1.0.0
		 */
		public IErrorResult<T> create() {
			return new ErrorResult<T>(details);
		}
	}

}

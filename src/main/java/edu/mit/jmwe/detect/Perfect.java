/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.List;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.harness.IAnswerKey;

/**
 * A detector that finds all of the MWEs in a sentence by using an answer key.
 * <p>
 * Sentences passed to this detector must implement {@link IMarkedSentence},
 * otherwise the {@link #detect(List)} method will throw an exception.
 * 
 * @author M.A. Finlayson
 * @version $Id: Perfect.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class Perfect implements IMWEDetector {
	
	private final IAnswerKey key;
	
	/**
	 * Constructs a new detector that uses the specified answer key
	 *
	 * @param key
	 *            the answer key to use, may not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified answer key is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public Perfect(IAnswerKey key){
		if(key == null)
			throw new NullPointerException();
		this.key = key;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		if(sentence instanceof IMarkedSentence){
			IMarkedSentence<T> marked = (IMarkedSentence<T>)sentence;
			return key.getAnswers(marked);
		} else {
			throw new IllegalArgumentException("Unable to locate answers for sentence: " + sentence);
		}
	}

}

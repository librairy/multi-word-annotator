/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result.error;

import edu.mit.jmwe.data.IHasID;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.harness.result.IErrorResult;
import edu.mit.jmwe.harness.result.ISentenceResult;

/**
 * Error detectors look for  errors made by an
 * {@link IMWEDetector} by analyzing the {@link ISentenceResult} obtained by
 * running the detector over a marked sentence using a test harness.
 * <p>
 * The ID should follow the reverse namespace pattern. For example, the ID for
 * an error class "A" should be "edu.mit.jmwe.error.A".
 * 
 * @author N. Kulkarni
 * @version $Id: IErrorDetector.java 322 2011-05-07 00:02:36Z markaf $
 * @since jMWE 1.0.0
 */
public interface IErrorDetector extends IHasID {

	/**
	 * Identifies the multi-word expressions in a unit result that fall under
	 * the specific error class this detector identifies.
	 * 
	 * @param <T>
	 *            the type of tokens contained in the unit.
	 * @param <S>
	 *            the type of marked sentence whose results are stored. Is
	 *            parameterized by tokens of type T.
	 * @param result
	 *            the sentence result obtained by running an IMWEDetector over a
	 *            unit
	 * @return an error result containing the MWEs identified by this error
	 *         detector
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken, S extends IMarkedSentence<T>> IErrorResult<T> detect(ISentenceResult<T, S> result);
}

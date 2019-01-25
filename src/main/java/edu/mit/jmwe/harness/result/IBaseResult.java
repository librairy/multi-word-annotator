/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

/**
 * Results store, at a minimum, f-measure, precision, and recall scores and the
 * values used to calculate them.
 * 
 * @author M.A. Finlayson
 * @version $Id: IBaseResult.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public interface IBaseResult {

	/**
	 * Returns the precision, a double between zero and 1, inclusive.
	 * 
	 * @return the precision, a double between zero and 1, inclusive.
	 * @since jMWE 1.0.0
	 */
	public double getPrecision();

	/**
	 * Returns the recall, a double between zero and 1, inclusive.
	 * 
	 * @return the recall, a double between zero and 1, inclusive.
	 * @since jMWE 1.0.0
	 */
	public double getRecall();

	/**
	 * Returns the F1 score, a double between zero and 1, inclusive. The F1
	 * score, or f-measure, is the harmonic mean of the precision and the
	 * recall.
	 * 
	 * @return the F1 score, a double between zero and 1, inclusive.
	 * @since jMWE 1.0.0
	 */
	public double getFScore();

	/**
	 * Returns the total number items found.
	 * 
	 * @return the total number of items found
	 * @since jMWE 1.0.0
	 */
	public int getTotalFound();

	/**
	 * Returns the total number of answers.
	 *
	 * @return the total number of answers
	 * @since jMWE 1.0.0
	 */
	public int getTotalAnswers();

	/**
	 * Returns the total number of items correctly identified.
	 *
	 * @return the total number of items correctly identified
	 * @since jMWE 1.0.0
	 */
	public int getTotalCorrect();

}

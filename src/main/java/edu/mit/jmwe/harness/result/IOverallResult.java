/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

import java.util.Map;

import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.MWEPOS;

/**
 * Contains results collected from running a test harness over a group of
 * {@link IMarkedSentence} objects. Contains overall precision and recall scores as well
 * as detailed results, an {@link ISentenceResult}, for each unit the harness runs
 * over. The results are organized by part of speech.
 * 
 * @param <T>
 *            the type of tokens in the sentence
 * @param <S>
 *            the type of sentence the harness runs over. Is parameterized by tokens
 *            of type T.
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: IOverallResult.java 321 2011-05-06 23:44:50Z markaf $
 * @since jMWE 1.0.0
 */
public interface IOverallResult<T extends IToken, S extends IMarkedSentence<T>> extends IBaseResult {

	/**
	 * Returns a map that stores the results for a unit under its ID. Should
	 * never be <code>null</code>.
	 * 
	 * @return a map that stores a unit result under its ID.
	 * @since jMWE 1.0.0
	 */
	public Map<String, ISentenceResult<T, S>> getDetails();

	/**
	 * Returns a map that stores the number of answer multi-word expressions for
	 * each part of speech. Should never be <code>null</code>.
	 * 
	 * @return a map that stores the number of answer multi-word expressions for
	 *         each part of speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Integer> getAnswerData();

	/**
	 * Returns a map that stores the number of multi-word expressions found by
	 * the detector for each part of speech. Should never be <code>null</code>.
	 * 
	 * @return a map that stores the number of number of multi-word expressions
	 *         found by the detector for each part of speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Integer> getFoundData();

	/**
	 * Returns a map that stores the number of multi-word expressions correctly
	 * found by the detector for each part of speech. Should never be
	 * <code>null</code>.
	 * 
	 * @return a map that stores the number of number of multi-word expressions
	 *         correctly found by the detector for each part of speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Integer> getCorrectData();

	/**
	 * Returns a map that stores the partial credit for the partially correct
	 * multi-word expressions found by the detector for each part of speech.
	 * Partial credit should be given when the detector correctly identifies
	 * some but not all of the tokens in an expression or if it includes some
	 * extra incorrect tokens in the expression. Should never be
	 * <code>null</code>.
	 * 
	 * @return a map that stores the partial credit for the partially correct
	 *         multi-word expressions found by the detector for each part of
	 *         speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getPartialScores();
	
	/**
	 * Returns a map that stores the precision of the detector for
	 * each part of speech. Should never be <code>null</code>.
	 * 
	 * @return a map that stores the precision the detector for each part of
	 *         speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getPrecisionScores();

	/**
	 * Returns a map that stores the f1 measures of the detector for each part
	 * of speech. Should never be <code>null</code>.
	 * 
	 * @return a map that stores the f1 measures the detector for each part of
	 *         speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getF1Scores();

	/**
	 * Returns a map that stores the recall of the detector for each part of
	 * speech. Should never be <code>null</code>.
	 * 
	 * @return a map that stores the recall the detector for each part of
	 *         speech.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getRecallScores();

	/**
	 * Returns a map that stores the precision of the detector for each part of
	 * speech after adding the partial credit to its correct score. Should never
	 * be <code>null</code>.
	 * 
	 * @return a map that stores the precision the detector for each part of
	 *         speech after counting partial credit.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getPartialPrecisionScores();
	
	/**
	 * Returns a map that stores the recall of the detector for
	 * each part of speech after adding the partial credit to its correct score.
	 * Should never be <code>null</code>.
	 * 
	 * @return a map that stores the recall the detector for each part of
	 *         speech after counting partial credit.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getPartialRecallScores();

	/**
	 * Returns a map that stores the f1 measure of the detector for each part of
	 * speech after adding the partial credit to its correct score. Should never
	 * be <code>null</code>.
	 * 
	 * @return a map that stores the f1 measure the detector for each part of
	 *         speech after counting partial credit.
	 * @since jMWE 1.0.0
	 */
	public Map<MWEPOS, Double> getPartialF1Scores();

	/**
	 * Returns the total F score earned by the detector, taking into account
	 * partial credit.
	 * 
	 * @return the total F score earned by the detector, taking into account
	 *         partial credit.
	 * @since jMWE 1.0.0
	 */
	public double getPartialF1Score();

	/**
	 * Returns the total partial credit earned by the detector. Partial credit
	 * should be given when the detector correctly identifies some but not all
	 * of the tokens in a multi-word expression or if it includes some extra
	 * incorrect tokens in the expression.
	 * 
	 * @return the total partial credit earned by the detector
	 * @since jMWE 1.0.0
	 */
	public double getPartialScore();

	/**
	 * Returns the total precision of the detector after counting partial
	 * credit. Must be a number between 0 and 1.
	 * 
	 * @return the total precision with partial credit
	 * @since jMWE 1.0.0
	 */
	public double getPartialPrecision();

	/**
	 * Returns the total recall of the detector after counting partial credit.
	 * Must be a number between 0 and 1.
	 * 
	 * @return the total precision with partial credit
	 * @since jMWE 1.0.0
	 */
	public double getPartialRecall();

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEPOS;

/**
 * Default implementation of {@link IOverallResult} interface. Contains the
 * overall precision and recall scores with and without partial credit. All of
 * the data stored in this result is organized by part of speech.
 * 
 * @param <T>
 *            the type of tokens contained in the unit and its associated
 *            multi-word expressions.
 * @param <S>
 *            the type of sentence whose results are stored. Is parameterized by
 *            tokens of type T.
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: MWEResult.java 346 2013-12-10 18:11:38Z markaf $
 * @since jMWE 1.0.0
 */
public class MWEResult<T extends IToken, S extends IMarkedSentence<T>> implements IOverallResult<T,S> {
	
	// final instance fields
	private final Map<MWEPOS, Integer> answerData;
	private final Map<MWEPOS, Integer> foundData;
	private final Map<MWEPOS, Integer> correctData;
	private final Map<MWEPOS, Double> partialScores;
	private final Map<String, ISentenceResult<T,S>> details;

	// dynamic fields
	private double precision = Double.NaN;
	private double recall = Double.NaN;
	private double f1score = Double.NaN;
	private double partialPrecision = Double.NaN;
	private double partialRecall = Double.NaN;
	private double partialF1Score = Double.NaN;
	private double partialScore = Double.NaN;
	private int totalRetrieved = -1;
	private int totalAnswers = -1;
	private int totalCorrect = -1;
	private Map<MWEPOS, Double> precisionScores;
	private Map<MWEPOS, Double> recallScores;
	private Map<MWEPOS, Double> f1Scores;
	private Map<MWEPOS, Double> partialPrecisionScores;
	private Map<MWEPOS, Double> partialRecallScores;
	private Map<MWEPOS, Double> partialF1Scores;

	/**
	 * Constructs the result from the answer, found and correct data, and the
	 * precision, recall and partial credit scores. Also takes a map that stores
	 * the {@link ISentenceResult} objects for a unit under its ID.
	 * 
	 * @param answer
	 *            a map that stores the number of answer multi-word expressions
	 *            for each part of speech.
	 * @param found
	 *            a map that stores the number of multi-word expressions found
	 *            by the detector for each part of speech.
	 * @param correct
	 *            a map that stores the number of multi-word expressions
	 *            correctly found by the detector for each part of speech.
	 * @param partial
	 *            a map that stores the partial credit for the partially correct
	 *            multi-word expressions found by the detector for each part of
	 *            speech.
	 * @param detailed
	 *            a map that stores the results for a unit under its ID.
	 * @since jMWE 1.0.0
	 */
	public MWEResult(Map<MWEPOS, Integer> answer, 
			Map<MWEPOS, Integer> found, 
			Map<MWEPOS, Integer> correct, 
			Map<MWEPOS, Double> partial,
			Map<String, ISentenceResult<T,S>> detailed) {
		answerData = Collections.unmodifiableMap(answer);
		foundData = Collections.unmodifiableMap(found);
		correctData = Collections.unmodifiableMap(correct);
		partialScores = Collections.unmodifiableMap(partial);
		details = detailed;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getPrecision()
	 */
	public double getPrecision() {
		if(Double.isNaN(precision))
			precision = calcPrecision(null);
		return precision;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getRecall()
	 */
	public double getRecall() {
		if(Double.isNaN(recall))
			recall = calcRecall(null);
		return recall;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPrecisionScores()
	 */
	public Map<MWEPOS, Double> getPrecisionScores() {
		if(precisionScores == null){
			Map<MWEPOS, Double> map = new HashMap<MWEPOS, Double>(MWEPOS.values().length);
			for(MWEPOS p : MWEPOS.values())
				map.put(p, calcPrecision(p));
			precisionScores = Collections.unmodifiableMap(map);
		}
		return precisionScores;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getRecallScores()
	 */
	public Map<MWEPOS, Double> getRecallScores() {
		if(recallScores == null){
			Map<MWEPOS, Double> map = new HashMap<MWEPOS, Double>(MWEPOS.values().length);
			for(MWEPOS p : MWEPOS.values())
				map.put(p, calcRecall(p));
			recallScores = Collections.unmodifiableMap(map);
		}
		return recallScores;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getRecallScores()
	 */
	public Map<MWEPOS, Double> getF1Scores() {
		if(f1Scores == null){
			Map<MWEPOS, Double> ps = getPrecisionScores();
			Map<MWEPOS, Double> rs = getRecallScores();
			Map<MWEPOS, Double> map = new HashMap<MWEPOS, Double>(MWEPOS.values().length);
			for(MWEPOS p : MWEPOS.values())
				map.put(p, calcF1Score(ps.get(p), rs.get(p)));
			f1Scores = Collections.unmodifiableMap(map);
		}
		return f1Scores;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getTotalAnswers()
	 */
	public int getTotalAnswers() {
		if(totalAnswers == -1)
			totalAnswers = sumInt(answerData.values());
		return totalAnswers;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getTotalCorrect()
	 */
	public int getTotalCorrect() {
		if(totalCorrect == -1)
			totalCorrect = sumInt(correctData.values());
		return totalCorrect;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getTotalFound()
	 */
	public int getTotalFound() {
		if(totalRetrieved == -1)
			totalRetrieved = sumInt(foundData.values());
		return totalRetrieved;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getAnswerData()
	 */
	public Map<MWEPOS, Integer> getAnswerData() {
		return answerData;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getFoundData()
	 */
	public Map<MWEPOS, Integer> getFoundData() {
		return foundData;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getCorrectData()
	 */
	public Map<MWEPOS, Integer> getCorrectData() {
		return correctData;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialPrecision()
	 */
	public double getPartialPrecision() {
		if(Double.isNaN(partialPrecision))
			partialPrecision = calcPartialPrecision(null);
		return partialPrecision;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialRecall()
	 */
	public double getPartialRecall() {
		if(Double.isNaN(partialRecall))
			partialRecall = calcPartialRecall(null);
		return partialRecall;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialScoreData()
	 */
	public Map<MWEPOS, Double> getPartialScores() {
		return partialScores;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialPrecisionScores()
	 */
	public Map<MWEPOS, Double> getPartialPrecisionScores() {
		if(partialPrecisionScores == null){
			Map<MWEPOS, Double> map = new HashMap<MWEPOS, Double>(MWEPOS.values().length);
			for(MWEPOS p : MWEPOS.values())
				map.put(p, calcPartialPrecision(p));
			partialPrecisionScores = Collections.unmodifiableMap(map);
		}
		return partialPrecisionScores;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialRecallScores()
	 */
	public Map<MWEPOS, Double> getPartialRecallScores() {
		if(partialRecallScores == null){
			Map<MWEPOS, Double> map = new HashMap<MWEPOS, Double>(MWEPOS.values().length);
			for(MWEPOS p : MWEPOS.values())
				map.put(p, calcPartialRecall(p));
			partialRecallScores = Collections.unmodifiableMap(map);
		}
		return partialRecallScores;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getRecallScores()
	 */
	public Map<MWEPOS, Double> getPartialF1Scores() {
		if(partialF1Scores == null){
			Map<MWEPOS, Double> ps = getPartialPrecisionScores();
			Map<MWEPOS, Double> rs = getPartialRecallScores();
			Map<MWEPOS, Double> map = new HashMap<MWEPOS, Double>(MWEPOS.values().length);
			for(MWEPOS p : MWEPOS.values())
				map.put(p, calcF1Score(ps.get(p), rs.get(p)));
			partialF1Scores = Collections.unmodifiableMap(map);
		}
		return partialF1Scores;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialScore()
	 */
	public double getPartialScore() {
		if(Double.isNaN(partialScore))
			partialScore = sumDbl(partialScores.values());
		return partialScore;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getFScore()
	 */
	public double getFScore() {
		if(Double.isNaN(f1score))
			f1score = calcF1Score(getPrecision(), getRecall());
		return f1score;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getPartialF1Score()
	 */
	public double getPartialF1Score(){
		if(Double.isNaN(partialF1Score))
			partialF1Score = calcF1Score(getPartialPrecision(), getPartialRecall());
		return partialF1Score;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IOverallResult#getDetails()
	 */
	public Map<String, ISentenceResult<T,S>> getDetails() {
		return details;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return MWEResult.toString(this);
		
	}
	
	/**
	 * Calculates the precision over the specified parts of speech, or all parts of
	 * speech if the specified part of speech is <code>null</code>
	 * 
	 * @param pos
	 *            the part of speech over which the precision is to be calculated;
	 *            <code>null</code> means all parts of speech
	 * 
	 * @return the precision over the specified part of speech
	 * @since jMWE 1.0.0
	 */
	protected double calcPrecision(MWEPOS pos){
		EnumSet<MWEPOS> ps = (pos == null) ? EnumSet.allOf(MWEPOS.class) : EnumSet.of(pos);
		int correctlyRetrieved = 0;
		int totalRetrieved = 0;
		for(MWEPOS p : ps){
			totalRetrieved += foundData.get(p).intValue();
			correctlyRetrieved += correctData.get(p).intValue();
		}
		return calcPrecision(correctlyRetrieved, totalRetrieved);
	}

	/**
	 * Calculates the recall over the specified parts of speech, or all parts of
	 * speech if the specified part of speech is <code>null</code>
	 * 
	 * @param pos
	 *            the part of speech over which the recall is to be calculated;
	 *            <code>null</code> means all parts of speech
	 * 
	 * @return the recall over the specified part of speech
	 * @since jMWE 1.0.0
	 */
	protected double calcRecall(MWEPOS pos){
		EnumSet<MWEPOS> ps = (pos == null) ? EnumSet.allOf(MWEPOS.class) : EnumSet.of(pos);
		int correctlyRetrieved = 0;
		int totalActual = 0;
		for(MWEPOS p : ps){
			totalActual += answerData.get(p).intValue();
			correctlyRetrieved += correctData.get(p).intValue();
		}
		
		return calcRecall(correctlyRetrieved, totalActual);
	}

	/**
	 * Calculates the precision over the specified part of speech, taking into
	 * account partial credit for the specified MWE part of speech. The partial
	 * credit is the alignment score between a partially correct MWE and an
	 * answer MWE.
	 * 
	 * @param pos
	 *            the MWE pos for which the precision should be calculated; or
	 *            <code>null</code> if for all
	 * @return the total precision with partial credit
	 * @since jMWE 1.0.0
	 */
	protected double calcPartialPrecision(MWEPOS pos){
		EnumSet<MWEPOS> ps = (pos == null) ? EnumSet.allOf(MWEPOS.class) : EnumSet.of(pos);
		double correctlyRetrieved = 0;
		int totalRetrieved = 0;
		for(MWEPOS p : ps){
			correctlyRetrieved += correctData.get(p).intValue();
			correctlyRetrieved += partialScores.get(p).doubleValue();
			totalRetrieved += foundData.get(p).intValue();
		}
		return calcPrecision(correctlyRetrieved, totalRetrieved);
	}

	/**
	 * Calculates the recall over the specified part of speech, taking into
	 * account partial credit for the specified MWE part of speech. The partial
	 * credit is the alignment score between a partially correct MWE and an
	 * answer MWE.
	 * 
	 * @param pos
	 *            the MWE pos for which the recall should be calculated; or
	 *            <code>null</code> if for all
	 * @return the total precision with partial credit
	 * @since jMWE 1.0.0
	 */
	protected double calcPartialRecall(MWEPOS pos){
		EnumSet<MWEPOS> ps = (pos == null) ? EnumSet.allOf(MWEPOS.class) : EnumSet.of(pos);
		double correctlyRetrieved = 0;
		int totalActual = 0;
		for(MWEPOS p : ps){
			correctlyRetrieved += correctData.get(p).intValue();
			correctlyRetrieved += partialScores.get(p).doubleValue();
			totalActual += answerData.get(p).intValue();
		}
		return calcRecall(correctlyRetrieved, totalActual);
	}

	/**
	 * Creates a table displaying the number of answer, found and correct
	 * multi-word expressions and the precision, recall and partial credit
	 * scores of the detector for each part of speech.
	 * 
	 * @param result
	 *            a non-null IResult
	 * @return A String that displays the information stored in the result in a
	 *         table.
	 * @since jMWE 1.0.0
	 */
	public static String toString(IOverallResult<?,?> result){
		
		String strFmt =  "%1$-12s";
		String intFmt =  "%1$-8s";
		String dblFmt =  "%1$-10.3f";
		String dblFmtH = "%1$-10s";
	
		// rows
		List<String> headers = new ArrayList<String>();
		List<String> formatsH = new ArrayList<String>();
		List<String> formats = new ArrayList<String>();
		List<Map<MWEPOS, ? extends Number>> maps = new ArrayList<Map<MWEPOS, ? extends Number>>();
		List<Object> totals = new ArrayList<Object>();
		
		// columns
		headers.add("POS");
		formats.add(strFmt);
		formatsH.add(strFmt);
		maps.add(null);
		totals.add("Totals");
		
		headers.add("Found");
		formats.add(intFmt);
		formatsH.add(intFmt);
		maps.add(result.getFoundData());
		totals.add(result.getTotalFound());
		
		headers.add("Actual");
		formats.add(intFmt);
		formatsH.add(intFmt);
		maps.add(result.getAnswerData());
		totals.add(result.getTotalAnswers());
		
		headers.add("Correct");
		formats.add(intFmt);
		formatsH.add(intFmt);
		maps.add(result.getCorrectData());
		totals.add(result.getTotalCorrect());
		
		headers.add("F1");
		formats.add(dblFmt);
		formatsH.add(dblFmtH);
		maps.add(result.getF1Scores());
		totals.add(Double.valueOf(result.getFScore()));
		
		headers.add("Pr");
		formats.add(dblFmt);
		formatsH.add(dblFmtH);
		maps.add(result.getPrecisionScores());
		totals.add(Double.valueOf(result.getPrecision()));

		headers.add("Re");
		formats.add(dblFmt);
		formatsH.add(dblFmtH);
		maps.add(result.getRecallScores());
		totals.add(Double.valueOf(result.getRecall()));
		
//		headers.add("pc");
//		formats.add(dblFmt);
//		maps.add(result.getPartialScores());
//		totals.add(Double.valueOf(result.getPartialScore()));
//
//		headers.add("Pr(partial)");
//		formats.add(dblFmt);
//		totals.add(Double.valueOf(result.getPartialPrecision()));
//		maps.add(result.getPartialPrecisionScores());
//
//		headers.add("Re(partial)");
//		formats.add(dblFmt);
//		totals.add(Double.valueOf(result.getPartialRecall()));
//		maps.add(result.getPartialRecallScores());
		
		StringBuilder sb = new StringBuilder(1024);
		Formatter f = new Formatter(sb);
		
		// headers
		for(int i = 0; i < headers.size(); i++) 
			f.format(formatsH.get(i), headers.get(i));
		int length = sb.length();
		sb.append('\n');
		
		// add divider
		for(int i = 0; i < length; i++)
			sb.append('-');
		sb.append('\n');
		
		// data
		Map<?,?> map;
		for(MWEPOS pos : MWEPOS.values()){
			for(int i = 0; i < headers.size(); i++){
				map = maps.get(i);
				if(map == null){
					f.format(formats.get(i), pos.name());
				} else {
					f.format(formats.get(i), maps.get(i).get(pos));
				}
			}
			sb.append('\n');
		}
		
		// add another divider
		for(int i = 0; i < length; i++)
			sb.append('-');
		sb.append('\n');
		
		for(int i = 0; i < totals.size(); i++)
			f.format(formats.get(i), totals.get(i));
		
		sb.append("\n");
		
		f.close();
		
		return sb.toString();
	}

	/**
	 * Given the total number of MWEs retrieved and the number of MWEs that are
	 * correct, calculates precision. Will be <code>Double.NaN</code> if the
	 * number of correct MWEs is greater than 0 and there are no found MWEs,
	 * since this is undefined. Will return 1.0, a perfect score, if both the
	 * number of found MWEs and correct MWEs are 0 or if both numbers are equal.
	 * @param correctlyRetrieved
	 *            the number of MWEs that are correctly identified
	 * @param totalRetrieved
	 *            the number of MWEs retrieved
	 * 
	 * @return the precision. Will be <code>Double.NaN</code> if the number of
	 *         correct MWEs is greater than 0 and there are no found MWEs. Will
	 *         return 1.0, a perfect score, if both the number of found MWEs and
	 *         correct MWEs are 0 or if both numbers are equal.
	 * @since jMWE 1.0.0
	 */
	public static double calcPrecision(double correctlyRetrieved, double totalRetrieved) {
		if(totalRetrieved == 0 && correctlyRetrieved == 0)
			return 1.0;
		if(totalRetrieved == 0)
			return Double.NaN;
		double result = correctlyRetrieved / totalRetrieved;
		if(result > 1.0 || result< 0.0 )
			throw new IllegalStateException();
		return result;
	}

	/**
	 * Given the total number of answer MWEs and the number of MWEs that are
	 * correct, calculates recall. Will be <code>Double.NaN</code> if the number
	 * of correct MWEs is greater than 0 and there are no answer MWEs,
	 * since this is undefined. Will return 1.0, a perfect score, if both the
	 * number of answer MWEs and correct MWEs are 0 or if both numbers are
	 * equal.
	 * @param correctlyRetrieved
	 *            the number of MWEs that are correctly identified
	 * @param totalAnswers
	 *            the total number of answer MWEs
	 * 
	 * @return the recall. Will be <code>Double.NaN</code> if the number of
	 *         correct MWEs is greater than 0 and there are no answer MWEs.
	 *         Will return 1.0, a perfect score, if both the number of answer
	 *         MWEs and correct MWEs are 0 or if both numbers are equal.
	 * @since jMWE 1.0.0
	 */
	public static double calcRecall(double correctlyRetrieved, double totalAnswers) {
		if(totalAnswers == 0 && correctlyRetrieved == 0)
			return 1.0;
		if(totalAnswers == 0)
			return Double.NaN;
		double result = correctlyRetrieved / totalAnswers;
		if(result > 1.0 || result < 0.0 )
			throw new IllegalStateException();
		return result;
	}

	/**
	 * Calculates the f1 score. It is the harmonic mean of the precision and
	 * recall, reaching its best value at 1 and worst value at 0. If either the
	 * precision or recall is undefined, will return <code>Double.NaN</code>.
	 * 
	 * @param precision
	 *            the precision. Must be a non-negative double less than 1.0.
	 * @param recall
	 *            the recall. Must be a non-negative double less than 1.0.
	 * @return the f1 score. Will be <code>Double.NaN</code> if either the
	 *         precision or recall is undefined; otherwise, will be a
	 *         nonnegative double between 0.0 and 1.0.
	 * @since jMWE 1.0.0
	 */
	public static double calcF1Score(double precision, double recall){
		if(precision > 1.0 || precision < 0.0 || recall > 1.0 || recall < 0.0 )
			throw new IllegalStateException();
		if(precision == Double.NaN || recall == Double.NaN)
			return Double.NaN;
		return 2*(precision*recall)/(precision+recall);
	}

	/**
	 * Return the sum of all the values stored in the map.
	 * 
	 * @param m
	 *            the map whose values are to be summed
	 * @return the sum of all the values stored in the map.
	 * @since jMWE 1.0.0
	 */
	public static int sumInt(Iterable<? extends Integer> m){
		int result = 0;
		for (Integer d : m)
			result += d;
		return result;
	}
	
	/**
	 * Return the sum of all the values stored in the map.
	 * 
	 * @param m
	 *            the map whose values are to be summed
	 * @return the sum of all the values stored in the map.
	 * @since jMWE 1.0.0
	 */
	public static double sumDbl(Iterable<? extends Double> m){
		double result = 0;
		for (Double d : m)
			result += d;
		return result;
	}

}

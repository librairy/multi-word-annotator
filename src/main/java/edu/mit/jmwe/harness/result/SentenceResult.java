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
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.IMarkedSentence;

/**
 * Default implementation of the <code>ISentenceResult</code> interface.
 * 
 * @param <T>
 *            the token type
 * @param <S>
 *            the sentence type
 * @author Nidhi Kulkarni
 * @version $Id: SentenceResult.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class SentenceResult<T extends IToken, S extends IMarkedSentence<T>> implements ISentenceResult<T,S> {

	// instance fields set on construction
	private final S sentence;
	private double precision;
	private double recall;
	private double fscore;
	private final List<IMWE<T>> answers;
	private final List<IMWE<T>> found;
	private final List<IMWE<T>> falseNegatives;
	private final List<IMWE<T>> falsePositives;
	private final List<IMWE<T>> truePositives;

	/**
	 * Constructs a sentence result from a list of answer multi-word expressions
	 * and a list of multi-word expressions found by the detector. Finds and
	 * stores the false negatives, false positives, and true positives by
	 * comparing the given lists. Reallocates new internal lists for the answer
	 * and retrieved lists, and so subsequent changes to the source lists will
	 * not affect this object.
	 * 
	 * @param answer
	 *            a non-null list of answer multi-word expressions.
	 * @param retrieved
	 *            a non-null list of multi-word expressions found by the
	 *            detector.
	 * @param sentence
	 *            the sentence for which this object is a result
	 * @throws NullPointerException
	 *             if either list is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public SentenceResult(List<IMWE<T>> answer, List<IMWE<T>> retrieved, S sentence){
		this(answer, retrieved, sentence, true);
	}
	
	/**
	 * Constructs a sentence result from a list of answer multi-word expressions and
	 * a list of multi-word expressions found by the detector. Finds and stores
	 * the false negatives, false positives, and true positives by comparing the
	 * given lists. If no reallocation is requested, this constructor reuses the
	 * given lists, and so subsequent changes to the source list will affect
	 * this object.
	 * 
	 * @param answer
	 *            a non-null list of answer multi-word expressions.
	 * @param retrieved
	 *            a non-null list of multi-word expressions found by the
	 *            detector.
	 * @param sentence
	 *            the sentence for which this object is a result
	 * @param reallocate
	 *            If true, will allocate new internal lists for the answer and
	 *            retrieved lists.
	 * @throws NullPointerException
	 *             if either list is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public SentenceResult(List<IMWE<T>> answer, List<IMWE<T>> retrieved, S sentence, boolean reallocate){
		
		this.sentence = sentence;
		
		this.answers = reallocate ? new ArrayList<IMWE<T>>(answer)  : answer;
		this.found = reallocate ? new ArrayList<IMWE<T>>(retrieved) : retrieved;
		
		this.falseNegatives = new ArrayList<IMWE<T>>();
		this.falsePositives = new ArrayList<IMWE<T>>();
		this.truePositives = new ArrayList<IMWE<T>>();
		
		for(IMWE<T> item : found){
			if(answers.contains(item)) truePositives.add(item);
			else falsePositives.add(item);
		}
		
		for(IMWE<T> ans : answers){
			if(! found.contains(ans)) falseNegatives.add(ans);
		}
		
		precision = MWEResult.calcPrecision(truePositives.size(), found.size());
		recall = MWEResult.calcRecall(truePositives.size(), answers.size());
		fscore = MWEResult.calcF1Score(precision, recall);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.ISentenceResult#getSentence()
	 */
	public S getSentence() {
		return sentence;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.ISentenceResult#getFalseNegatives()
	 */
	public List<IMWE<T>> getFalseNegatives() {
		return Collections.unmodifiableList(falseNegatives);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.ISentenceResult#getFalsePositives()
	 */
	public List<IMWE<T>> getFalsePositives() {
		return Collections.unmodifiableList(falsePositives);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.ISentenceResult#getTruePositives()
	 */
	public List<IMWE<T>> getTruePositives() {
		return Collections.unmodifiableList(truePositives);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.ISentenceResult#getAnswers()
	 */
	public List<IMWE<T>> getAnswers() {
		return Collections.unmodifiableList(answers);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.ISentenceResult#getFound()
	 */
	public List<IMWE<T>> getFound() {
		return Collections.unmodifiableList(found);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getPrecision()
	 */
	public double getPrecision() {
		return precision;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getRecall()
	 */
	public double getRecall() {
		return recall;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getFScore()
	 */
	public double getFScore() {
		return fscore;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getTotalAnswers()
	 */
	public int getTotalAnswers() {
		return answers.size();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getTotalCorrect()
	 */
	public int getTotalCorrect() {
		return truePositives.size();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.result.IBaseResult#getTotalFound()
	 */
	public int getTotalFound() {
		return found.size();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(1024);
		sb.append("correct: "+ getTruePositives());
		sb.append("\nfalse positive: " + getFalsePositives());
		sb.append("\nfalse negative: " + getFalseNegatives());
		sb.append('\n');
		return sb.toString();
	}

	/**
	 * Creates a graphical representation of the multi-word expressions found by
	 * the detector for a given sentence. Prints the sentence and each multi-word
	 * expression the detector finds on its own line under the corresponding
	 * tokens in the sentence. Underneath the sentence, prints out the correct,
	 * false negative, and false positive multi-word expressions in columns.
	 * 
	 * @param <T> the type of tokens in the sentence and its associated multi-word
	 *            expressions
	 * @param <U> the type of sentence. Is parameterized by tokens of type T.
	 * @param result
	 *            the sentence result obtained after running the detector over the
	 *            sentence
	 * @param sentence
	 *            the sentence the detector is run over
	 * @return a graphical representation of the multi-word expressions found by
	 *         the detector for a given sentence.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken, U extends IMarkedSentence<T>> String toString(ISentenceResult<T,U> result, U sentence){
		return toString(result, sentence, true);
	}
	
	/**
	 * Creates a graphical representation of the multi-word expressions found by
	 * the detector for a given sentence. Prints the sentence and each multi-word
	 * expression the detector finds on its own line under the corresponding
	 * tokens in the sentence. May or may not print out the correct, false negative,
	 * and false positive multi-word expressions in columns under the sentence
	 * depending on the value of the table flag.
	 * 
	 * @param <T> the type of tokens in the sentence and its associated multi-word
	 *            expressions
	 * @param <S> the type of sentence. Is parameterized by tokens of type T.
	 * @param result
	 *            the sentence result obtained after running the detector over the
	 *            sentence
	 * @param sentence
	 *            the sentence the detector is run over
	 * @param table
	 *            if true, prints the correct, false negative, and false
	 *            positive expressions in columns under the sentence.
	 * @return a graphical representation of the multi-word expressions found by
	 *         the detector for a given sentence.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken, S extends IMarkedSentence<T>> String toString(ISentenceResult<T,S> result, S sentence, boolean table){
		StringBuilder sb = new StringBuilder(1024);
		
		Formatter f = new Formatter(sb);
		Map<T, List<String>> map = new HashMap<T, List<String>>(sentence.size());
		
		String[]initial = new String[result.getTotalFound()];
		Arrays.fill(initial, "");
		//initialize map with equal size lists of empty strings for each token
		for(T token : sentence) map.put(token, new ArrayList<String>(Arrays.asList(initial)));
		
		IMWE<T> mwe;
		for(int i = 0; i < result.getTotalFound(); i++){
			mwe = result.getFound().get(i);
			for(T tkn : mwe.getTokens()) {
				map.get(tkn).set(i, tkn.getForm().concat("_" + tkn.getTag()));	
			}
		}
		
		String args;
		
		//write sentence
		sb.append("=================================================\n");
		sb.append(sentence.getID());
		sb.append('\n');
		
		
		
		for(T token : sentence) {
			args = "%1$-"+(token.getForm().concat("_"+token.getTag()).length()+1)+"s";
			f.format(args, token.getForm().concat("_"+token.getTag()));
		}
		sb.append('\n');
		
		//write each MWE on its own line
		for(int i = 0; i < result.getTotalFound(); i++){
			for(T token : sentence){
				args = "%1$-"+(token.getForm().concat("_"+token.getTag()).length()+1)+"s";
				f.format(args, map.get(token).get(i));
			}
			sb.append('\n');
		}
		sb.append('\n');
		
		if(table)
			printTable(sb, result, f);
		
		return sb.toString();
	}

	/**
	 * Prints a table of the correct, false negative and false positive
	 * expressions found by the detector in columns.
	 * 
	 * @param <T>
	 *            the type of tokens in the sentence and its associated
	 *            multi-word expressions
	 * @param <S>
	 *            the type of sentence. Is parameterized by tokens of type T.
	 * @param sb
	 *            the string builder to which the table should be written
	 * @param result
	 *            the result to be written
	 * @param f
	 *            the formatter to be used
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken, S extends IMarkedSentence<T>> void printTable(StringBuilder sb, ISentenceResult<T,S> result, Formatter f){
		
		String[] headers = new String[]{"Correct:", "False Negatives:", "False Positives:"};
		Map<String, List<IMWE<T>>> resultMap = new HashMap<String, List<IMWE<T>>>();
		resultMap.put("Correct:", result.getTruePositives());
		resultMap.put("False Negatives:", result.getFalseNegatives());
		resultMap.put("False Positives:", result.getFalsePositives());
		
		//get length of longest list in map
		int max = Collections.max(Arrays.asList(result.getTruePositives().size(),result.getFalsePositives().size(), result.getFalseNegatives().size()));
		
		//write separator line
		for(@SuppressWarnings("unused") String header : headers)
			for(int i = 0; i < 90; i++)
				f.format("%1$s", "-");
		f.format("%1$s", "\n");
		
		//write headers
		for(String header : headers)
			f.format("%1$-90s", header);

		sb.append('\n');
		//write MWEs
		for(int i = 0; i<max; i++){
			for(String header : headers){
				if (i< resultMap.get(header).size()) f.format("%1$-90s", resultMap.get(header).get(i));
				else f.format("%1$-90s", "");
			}
			sb.append('\n');
		}
		sb.append('\n');
	}

	
	
}

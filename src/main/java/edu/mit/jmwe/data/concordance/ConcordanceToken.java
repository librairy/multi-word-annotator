/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data.concordance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.Token;
import edu.mit.jsemcor.element.ISentence;
import edu.mit.jsemcor.element.IToken;
import edu.mit.jsemcor.element.IWordform;

/**
 * Default implementation of {@link IConcordanceToken}.
 * <p>
 * This class requires JSemcor to be on the classpath.
 *
 * @author M.A. Finlayson
 * @version $Id: ConcordanceToken.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class ConcordanceToken extends Token implements IConcordanceToken {

	// final instance fields
	private final int partNum;
	private final int tokenNum; 

	private transient String toString;

	/**
	 * Constructs a new semcor token object with the specified text, tag, token
	 * number, part number and stems. The stem array may be <code>null</code> or
	 * empty. If <code>null</code>, no stems have been assigned. If empty, the
	 * token is unstemmable.
	 * 
	 * @param text
	 *            the surface form of the token as it appears in the sentence,
	 *            capitalization intact
	 * @param tag
	 *            the tag of the token, if assigned, otherwise <code>null</code>
	 * @param tokenNum
	 *            the token number. Must be greater than or equal to 0.
	 * @param partNum
	 *            the part number representing the index of the token in a
	 *            multi-word expression, 0 if it is not part of one. Must be
	 *            greater than or equal to 0.
	 * @param stems
	 *            the list of stems, possibly empty or <code>null</code>
	 * @throws NullPointerException
	 *             if the text is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the text is empty or all whitespace or if the token number
	 *             or part number is less than 0.
	 * @since jMWE 1.0.0
	 */
	public ConcordanceToken(String text, String tag, int tokenNum, int partNum, String... stems) {
		super(text, tag, stems);
		if(tokenNum < 0)
			throw new IllegalArgumentException();
		if(partNum < 0)
			throw new IllegalArgumentException();
		this.tokenNum = tokenNum;
		this.partNum = partNum;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.concordance.ISemcorToken#getTokenNumber()
	 */
	public int getTokenNumber() {
		return tokenNum;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.concordance.ISemcorToken#getPartNumber()
	 */
	public int getPartNumber(){
		return partNum;
	}
		
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.Token#toString()
	 */
	@Override
	public String toString(){
		if(toString == null)
			toString = toString(this);
		return toString;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + partNum;
		result = prime * result + tokenNum;
		result = prime * result + this.getForm().hashCode();
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		ConcordanceToken other = (ConcordanceToken) obj;
		if (partNum != other.partNum)
			return false;
		if (tokenNum != other.tokenNum)
			return false;
		if(!this.getForm().equals(other.getForm()))
			return false;
		return true;
	}

	/**
	 * Returns the String representation of the given token. This has the form:
	 * 
	 * form_tag_stem[1]_stem[2]_..._stem[n]_tokenNumber_partNumber
	 * 
	 * @param token
	 *            the token to be represented as a string
	 * @return the String representation of the given token.
	 * @since jMWE 1.0.0
	 */
	public static String toString(IConcordanceToken token){
		StringBuilder sb = new StringBuilder();
		
		// surface form
		sb.append(token.getForm());
		sb.append('_');
		
		// tag
		if(token.getTag() != null)
			sb.append(token.getTag());
		
		// stems
		sb.append('_');
		if(token.getStems() != null)
			for(Iterator<String> i = token.getStems().iterator(); i.hasNext(); ){
				sb.append(i.next());
				if(i.hasNext())
					sb.append('_');
			}
		
		// token numbers
		sb.append('_');
		sb.append(Integer.toString(token.getTokenNumber()));
		
		// part number
		sb.append('_');
		sb.append(Integer.toString(token.getPartNumber()));
		
		return sb.toString();
	}

	/**
	 * A compiled regular expression pattern that captures the string
	 * representation of tagged tokens.
	 * 
	 * Pattern: <b>([\\S&amp;&amp;[^_]]+)_([\\S&amp;&amp;[^_]]+)_(\\S*)_(\\d+)_(\\d+)</b>
	 * <ol>
	 * <li><b>([\\S&amp;&amp;[^_]]+)</b> group 1, token string as it appears in the sentence</li>
	 * <li><b>([\\S&amp;&amp;[^_]]+)_</b> group 2, part of speech tag</li>
	 * <li><b>(\\S*)</b> group 3, list of stems, may or may not occur,</li>
	 * <li><b>(\\d+)</b> group 4, token number</li>
	 * <li><b>(\\d+)</b> group 5, part number</li>
	 * </ol>
	 * 
	 * @since jMWE 1.0.0
	 */
	public final static Pattern semcorTokenPattern = Pattern.compile("([\\S&&[^_]]+)_([\\S&&[^_]]+)_(\\S*?)_(\\d+)_(\\d+)");
	
	/**
	 * A compiled regular expression a non-empty run of whitespace. 
	 *
	 * @since jMWE 1.0.0
	 */
	public final static Pattern whitespaceDelimited = Pattern.compile("\\s+");
	
	/**
	 * Parses a string of the form "test_NN_stem1_stem2_..._stemN_1_0" into a
	 * {@link ConcordanceToken} instance.
	 * 
	 * @param str
	 *            the string representing the tagged token
	 * @return a SemcorToken instance
	 * @throws NullPointerException
	 *             if the specified string is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified string does not match the expected format
	 * @since jMWE 1.0.0
	 */
	public static ConcordanceToken parse(String str){
		
		try {
			Matcher matcher = semcorTokenPattern.matcher(str.trim());
			if(!matcher.matches())
				throw new IllegalArgumentException();
			
			String text = matcher.group(1);
			String tag = matcher.group(2);
			int tokenNum = Integer.parseInt(matcher.group(4));
			int partNum = Integer.parseInt(matcher.group(5));
			String stemList = matcher.group(3);
			String[] stems = (stemList.trim().length() == 0) ? new String[0] : IRootMWEDesc.underscore.split(stemList);
	
			return new ConcordanceToken(text, tag, tokenNum, partNum, stems);
		} catch(Throwable t){
			throw new IllegalArgumentException("Exception parsing token: " + str, t);
		}
	}

	/**
	 * Parses a string formed from the concatenation of strings of the form
	 * "test-1-0-NN-stem1:stem2 " into a list of corresponding
	 * {@link ConcordanceToken} instances.
	 * 
	 * @param str
	 *            the concatenated string representing the tagged token
	 * @return a list of SemcorToken instances, or an empty list if the
	 *         specified string does not contain any well-formed tagged token
	 *         strings
	 * @throws NullPointerException
	 *             if the specified string is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified string does not conform to the expected
	 *             format
	 * @since jMWE 1.0.0
	 */
	public static List<ConcordanceToken> parseList(String str){
		str = str.trim();
		String[] fields = whitespaceDelimited.split(str);
		if(fields.length == 0)
			throw new IllegalArgumentException();
		List<ConcordanceToken> results = new LinkedList<ConcordanceToken>();
		for(String field : fields)
			results.add(parse(field));
		return results;
	}

	/**
	 * Returns a list of Concordance token objects if the token specified by the
	 * token number in the sentence is a continuous MWE. Otherwise, returns a
	 * singleton list.
	 * 
	 * @param t
	 *            the token specified by the token number in the given sentence
	 * @param tokenNum
	 *            the token number of the token to be translated into a
	 *            concordance token object
	 * @param sent
	 *            the sentence
	 * @return a list of concordance tokens constructed from the specified token
	 * @since jMWE 1.0.0
	 */
	public static List<ConcordanceToken> toTokens(IToken t, int tokenNum, ISentence sent) {
		
		if(t instanceof IWordform){
			IWordform wf = (IWordform)t;
			List<String> ts = wf.getConstituentTokens();
			List<ConcordanceToken> result = new ArrayList<ConcordanceToken>(ts.size());
			for(int i = 0; i < ts.size(); i++)
				result.add(toToken(tokenNum, i, wf, sent));
			return result;
		} else {
			ConcordanceToken result = new ConcordanceToken(t.getText(), null, tokenNum, 0, (String[])null);
			return Collections.singletonList(result);
		}
	}

	/**
	 * Constructs a semcor token object from the given token number, part
	 * number, IWordform, and sentence drawn from the semcor corpus. Uses the
	 * word form's semantic tag to get the lemma. If the wordform is part of a
	 * discontinuous MWE, uses the semantic tag of the first wordform in
	 * the MWE to obtain the lemma.
	 * 
	 * @param tokenNum
	 *            the token number
	 * @param partNum
	 *            the part number
	 * @param wf
	 *            the word form
	 * @param sent
	 *            the JSemcor sentence
	 * @return a new semcor token constructed out of the specified information
	 * @since jMWE 1.0.0
	 */
	public static ConcordanceToken toToken(int tokenNum, int partNum, IWordform wf, ISentence sent){
		
		String text = wf.getConstituentTokens().get(partNum);
		String lemma = null;
		String tag = null;
		
		String markedLemma = null;
		String[] lemmaParts = null;
		if(wf.getSemanticTag() != null)
			markedLemma = wf.getSemanticTag().getLemma();
		
		int lemmaNum = partNum;
		
		if(markedLemma == null && wf.getDistance() != 0){
			
			List<IWordform> split = null;
			int idx = -1;
			for(List<IWordform> wfs : sent.getCollocations())
				if((idx = wfs.indexOf(wf)) > -1){
					split = wfs;
					break;
				}
			
			IWordform headWf = (IWordform)split.get(0);
			if(headWf.getSemanticTag() != null){
				markedLemma = headWf.getSemanticTag().getLemma();
				lemmaNum = idx;
			}
		}
		
		if(markedLemma != null)
			lemmaParts = IMWEDesc.underscore.split(markedLemma);


		// single token
		if(wf.getConstituentTokens().size() == 1){
			if(wf.getPOSTag() != null)
				tag = wf.getPOSTag().getValue();
			if(lemmaParts != null)
				lemma = lemmaParts[lemmaNum];
		} else {
			if(lemmaParts != null){
				if(lemmaParts.length == wf.getConstituentTokens().size()){
					lemma = lemmaParts[lemmaNum];
				} else {
					lemma = null;
				}
			}
		}
		String[] stems = (lemma == null) ? null : new String[]{lemma};
		return new ConcordanceToken(text, tag, tokenNum, partNum, stems);

	}
	
}

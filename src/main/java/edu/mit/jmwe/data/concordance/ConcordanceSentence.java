/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data.concordance;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jsemcor.element.ContextID;
import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.element.ISentence;

/**
 * Default implementation of <code>ISemcorSentence</code>
 * 
 * <p>
 * This class requires JSemcor to be on the classpath.
 *
 * @author M.A. Finlayson
 * @version $Id: ConcordanceSentence.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class ConcordanceSentence extends AbstractList<IConcordanceToken> implements IConcordanceSentence, RandomAccess {
	
	// final instance fields
	private final String id;
	private final IContextID cid;
	private final int sentNum;
	private final List<? extends IConcordanceToken> backingList;
	
	private transient String toString;

	/**
	 * Constructs a new semcor sentence from the specified context id and
	 * JSemcor sentence object.
	 * 
	 * @param cid
	 *            the context id for the JSemcor sentence; may not be
	 *            <code>null</code>
	 * @param sent
	 *            the JSemcor sentence; may not be <code>null</code>
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ConcordanceSentence(IContextID cid, ISentence sent){
		if(cid == null)
			throw new NullPointerException();
		if(sent == null)
			throw new NullPointerException();
		
		List<IConcordanceToken> tokens = new LinkedList<IConcordanceToken>();
		for(int i = 0; i < sent.size(); i++)
			tokens.addAll(ConcordanceToken.toTokens(sent.get(i), i, sent));
		
		this.id = makeID(cid, sent.getNumber());
		this.backingList = Collections.unmodifiableList(new ArrayList<IConcordanceToken>(tokens));
		this.cid = cid;
		this.sentNum = sent.getNumber();
	}

	/**
	 * Constructs a new semcor sentence from the list of tokens. This
	 * constructor allocates a new internal list, and so subsequent changes to
	 * the source list will not affect this object.
	 * 
	 * @param cid
	 *            the context id for the JSemcor sentence; may not be
	 *            <code>null</code>
	 * @param sentNum
	 *            the sentence number; must be positive
	 * @param tokens
	 *            the list of tokens that will make up this list, may not be
	 *            <code>null</code> or empty
	 * @throws NullPointerException
	 *             if the context id, or the list of source tokens is
	 *             <code>null</code> or contains <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the sentence number is non-positive, or the list is empty
	 * @since jMWE 1.0.0
	 */
	public ConcordanceSentence(IContextID cid, int sentNum, List<? extends IConcordanceToken> tokens) {
		// check arguments
		if(cid == null)
			throw new NullPointerException();
		for(Object t : tokens)
			if(t == null)
				throw new NullPointerException();
		if(sentNum < 1) 
			throw new IllegalArgumentException();
		if(tokens.isEmpty())
			throw new IllegalArgumentException();
		
		// field assigmment
		this.id = makeID(cid, sentNum);
		this.cid = cid;
		this.sentNum = sentNum;
		this.backingList = new ArrayList<IConcordanceToken>(tokens);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasID#getID()
	 */
	public String getID() {
		return id;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.concordance.ISemcorSentence#getContextID()
	 */
	public IContextID getContextID(){
		return cid;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.concordance.ISemcorSentence#getSentenceNumber()
	 */
	public int getSentenceNumber(){
		return sentNum;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public IConcordanceToken get(int index) {
		return backingList.get(index);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return backingList.size();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#toString()
	 */
	public String toString(){
		if(toString == null){
			StringBuilder sb = new StringBuilder();
			sb.append(id);
			sb.append(' ');
			for(Iterator<? extends IConcordanceToken> i = backingList.iterator(); i.hasNext(); ){
				sb.append(i.next());
				if(i.hasNext())
					sb.append(' ');
			}
			toString = sb.toString();
		}
		return toString;
	}
	
	/**
	 * A compiled regular expression pattern that captures the string
	 * representation of a Semcor sentence.
	 * [\\S&amp;&amp;[^/]]
	 * Pattern: <b>^\\s*([\\S&amp;&amp;[^/]]+)/([\\S&amp;&amp;[^/]]+)/(\\d+)\\s+(\\S.*)$</b>
	 * <ol>
	 * <li><b>^</b> beginning of the line</li>
	 * <li><b>\\s*</b> any amount of whitespace</li>
	 * <li><b>([\\S&amp;&amp;[^/]]+)/</b> capturing group 1, concordance name (unbroken run of non-whitespace, non-forward-slash characters) followed by a forward slash</li>
	 * <li><b>([\\S&amp;&amp;[^/]]+)/</b> capturing group 2, context name (unbroken run of non-whitespace, non-forward-slash characters) followed by a forward slash</li>
	 * <li><b>(\\d+)</b> capturing group 3, sentence number (unbroken run of digits)</li>
	 * <li><b>\\s+</b> some amount of whitespace</li>
	 * <li><b>(\S.*)</b> capturing group 4, the first non-whitespace character plus the remainder of the characters to the end of the line</li>
	 * <li><b>$</b> end of the line</li>
	 * </ol>
	 * @since jMWE 1.0.0
	 */
	public static final Pattern taggedSemcorSentencePattern = Pattern.compile("^\\s*(\\S+)/(\\S+)/(\\d+)\\s+(\\S.*)$");

	/**
	 * Parses a string formed from the a string of the form
	 * 
	 * <pre>
	 * concordanceName/contextID/sentNumber [tok_tag_stems_num_part]+
	 * </pre>
	 * 
	 * into a {@link ConcordanceSentence} instance.
	 * 
	 * @param toString
	 *            the string representing the tagged semcor sentence.
	 * @return a SemcorSentence instance
	 * @throws NullPointerException
	 *             if the specified string is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified string does not conform to the expected
	 *             format
	 * @since jMWE 1.0.0
	 */
	public static ConcordanceSentence parse(String toString){
		try {
			// see if line matches the pattern
			Matcher matcher = taggedSemcorSentencePattern.matcher(toString);
			if(!matcher.matches())
				throw new IllegalArgumentException();
			
			// extract parameters and tokens
			IContextID cid = new ContextID(matcher.group(2), matcher.group(1));
			int sentNum = Integer.parseInt(matcher.group(3));
			List<ConcordanceToken> tokens = ConcordanceToken.parseList(matcher.group(4));
			
			// make sentence object
			return new ConcordanceSentence(cid, sentNum, tokens);
		} catch(Throwable t){
			throw new IllegalArgumentException("Exception parsing semcor sentence: " + toString, t);
		}
	}

	/**
	 * Returns a string ID constructed from the given {@code IContextID} and
	 * sentence number. For the Semcor corpus, this ID has the form:
	 * 
	 * <p>
	 * brown1/br-a01/1
	 * </p>
	 * 
	 * @param cid
	 *            the context id
	 * @param sentNum
	 *            the sentence number
	 * @return a string ID constructed from the given context id and sentence
	 *         number:
	 * @since jMWE 1.0.0
	 */
	public static String makeID(IContextID cid, int sentNum){
		// construct id
		StringBuilder id = new StringBuilder();
		id.append(cid.getConcordanceName());
		id.append("/");
		id.append(cid.getContextName());
		id.append("/");
		id.append(sentNum);
		return id.toString();
	}

}

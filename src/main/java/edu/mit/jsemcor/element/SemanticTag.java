/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import edu.mit.jsemcor.term.ICategory;

/**
 * Concrete, default implementation of the {@link ISemanticTag} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.62, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class SemanticTag implements ISemanticTag {
	
	private static final Pattern semicolon = Pattern.compile("\\Q;\\E"); 
	
	private final String lemma;
	private final List<String> lexSenses;
	private final List<Integer> senseNums;
	private final ICategory category;
	private List<String> senseKeys;
	private String data;
	
	/**
	 * Constructs a new semantic tag object from the specified parameters.
	 * 
	 * @param lemma
	 *            the lemma of this word
	 * @param wnsn
	 *            the string representing the word sense of this semantic tag;
	 *            may comprise multiple word sense strings separated by
	 *            semicolons
	 * @param lexsn
	 *            the string representing the lexical sense of this semantic
	 *            tag; may comprise multiple lexical sense strings separated by
	 *            semicolons
	 * @param category
	 *            the category object to be used by this semantic tag
	 * @throws NullPointerException
	 *             if any of the String arguments are <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the lemma, wnsn, or lexsn strings are empty or all
	 *             whitespace, or if the wnsn and lexsn strings do not represent
	 *             the same number of senses.
	 * @since JSemcor 1.0.0
	 */
	public SemanticTag(String lemma, String wnsn, String lexsn, ICategory category){
		if(lemma.trim().length() == 0) throw new IllegalArgumentException();
		if(wnsn.trim().length() == 0) throw new IllegalArgumentException();
		if(lexsn.trim().length() == 0) throw new IllegalArgumentException();
		
		this.lemma = lemma;
		this.category = category;
		
		String[] wnsns = semicolon.split(wnsn);
		String[] lexsns = semicolon.split(lexsn);
		if(wnsns.length != lexsns.length) throw new IllegalArgumentException();
		
		lexSenses = Collections.unmodifiableList(Arrays.asList(lexsns));
		List<Integer> hiddenSenseNums = Arrays.asList(new Integer[wnsns.length]);
		
		// assign sense number value
		Integer senseNum;
		for(int i = 0; i < wnsns.length; i++){
			senseNum = Integer.parseInt(wnsns[i]); 
			hiddenSenseNums.set(i, senseNum);
		}
		senseNums = Collections.unmodifiableList(hiddenSenseNums);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISemanticTag#getLemma()
	 */
	public String getLemma() {
		return lemma;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISemanticTag#getSenseNumber()
	 */
	public List<Integer> getSenseNumber() {
		return senseNums;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISemanticTag#getLexicalSense()
	 */
	public List<String> getLexicalSense() {
		return lexSenses;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISemanticTag#getSenseKey()
	 */
	public List<String> getSenseKeys() {
		
		// make sense key list on demand
		if(senseKeys == null){
			List<String> hiddenSenseKeys = Arrays.asList(new String[lexSenses.size()]);
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < lexSenses.size(); i++){
				sb.setLength(0);
				sb.ensureCapacity(lexSenses.get(i).length() + lemma.length() + 1);
				
				// make sense key
				sb.append(lemma);
				sb.append(PERCENT);
				sb.append(lexSenses.get(i));
				hiddenSenseKeys.set(i, sb.toString());
			}
			senseKeys = Collections.unmodifiableList(hiddenSenseKeys);
		}
		return senseKeys;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.ISemanticTag#getProperNounCategory()
	 */
	public ICategory getProperNounCategory() {
		return category;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if(data == null){
			StringBuilder sb = new StringBuilder();
			sb.append(ATTR_LEMMA);
			sb.append(EQUALS);
			sb.append(lemma);
			sb.append(SPACE);
			
			sb.append(ATTR_WNSN);
			sb.append(EQUALS);
			for(Iterator<Integer> i = senseNums.iterator(); i.hasNext(); ){
				sb.append(i.next().toString());
				if(i.hasNext()) sb.append(SEMICOLON);
			}
			sb.append(SPACE);
			
			sb.append(ATTR_LEXSN);
			sb.append(EQUALS);
			for(Iterator<String> i = lexSenses.iterator(); i.hasNext(); ){
				sb.append(i.next());
				if(i.hasNext()) sb.append(SEMICOLON);
			}
			
			if(category != null){
				sb.append(SPACE);
				sb.append(category.getData());
			}
			
			data = sb.toString();
		}
		return data;
	}

}

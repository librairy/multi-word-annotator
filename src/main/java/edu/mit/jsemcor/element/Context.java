/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default, concrete implementation of the {@link IContext} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.62, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Context implements IContext {
	
	private final IContextID id;
	private final String corpus, filename;
	private final List<IParagraph> paraList;
	private final List<ISentence> sentList;
	private final Map<Integer, ISentence> sentMap;
	private String data;
	
	/**
	 * Constructs a new context. The id object is required, as is a non-empty
	 * list. What type of elements are in the list depends on the boolean flag.
	 * if hasParas is set <code>true</code>, then the elements in the list
	 * must be castable to the {@link IParagraph} interface. If false, the
	 * elements in the list must be castable to the {@link ISentence} interface.
	 * 
	 * @throws NullPointerException
	 *             if the id or element list is null, or an element of the list
	 *             is null
	 * @throws IllegalArgumentException
	 *             if the specified list is empty
	 * @throws ClassCastException
	 *             if {@code useParas} is <code>true</code> and the elements
	 *             in the list cannot be cast to {@link IParagraph}; or if
	 *             {@code useParas} is <code>false</code> and the elements in
	 *             the list cannot be cast to {@link ISentence}
	 * @since JSemcor 1.0.0
	 */
	public Context(IContextID id, String corpus, String filename, List<? extends IElement> elements, boolean useParas){
		if(id == null) throw new NullPointerException();
		if(elements.isEmpty()) throw new IllegalArgumentException(); // also checks for null
		if(corpus.trim().length() == 0) throw new IllegalArgumentException();
		if(filename.trim().length() == 0) throw new IllegalArgumentException();
		
		this.id = id;
		this.corpus = corpus;
		this.filename = filename;
		
		List<ISentence> hiddenSents;
		
		if(useParas){
			// allocate lists
			List<IParagraph> hiddenParas = new ArrayList<IParagraph>(elements.size());
			this.paraList = Collections.unmodifiableList(hiddenParas);
			
			// get paragraphs
			IParagraph p;
			int numSents = 0;
			for(IElement element : elements){
				if(element == null) throw new NullPointerException();
				p = (IParagraph)element; // may get ClassCastException
				hiddenParas.add(p);
				if(p.getNumber() != hiddenParas.size()) logNonSequentialParaNum(hiddenParas.size(), p.getNumber());
				numSents += p.size();
			}
			
			// get sentences
			hiddenSents = new ArrayList<ISentence>(numSents);
			for(IParagraph para : hiddenParas) hiddenSents.addAll(para);
			
		} else {
			// allocate paragraph list
			this.paraList = Collections.emptyList();
			
			ISentence s;
			hiddenSents = new ArrayList<ISentence>(elements.size());
			for(IElement element : elements){
				if(element == null) throw new NullPointerException();
				s = (ISentence)element; // may get ClassCastException
				hiddenSents.add(s);
				if(s.getNumber() != hiddenSents.size()) logNonSequentialSentNum(hiddenSents.size(), s.getNumber());
			}
		}
		
		this.sentList = Collections.unmodifiableList(hiddenSents);
		
		sentMap = new HashMap<Integer, ISentence>(sentList.size());
		for(ISentence s : sentList) sentMap.put(s.getNumber(), s);
	}
	
	/**
	 * Logs the presence of an non-sequential paragraph numbers. This implementation
	 * does nothing, and is left to subclasses to override if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logNonSequentialParaNum(int expected, int actual){
		// System.out.err("Paragraph numbers are not sequential, expected=" + expected + ", actual="+actual);
	}
	
	/**
	 * Logs the presence of a non-sequential sentence numbers. This implementation
	 * does nothing, and is left to subclasses to override if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logNonSequentialSentNum(int expected, int actual){
		// System.err("Sentence numbers are not sequential, expected=" + expected + ", actual="+actual);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#getID()
	 */
	public IContextID getID() {
		return id;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#getSourceConcordance()
	 */
	public String getCorpus() {
		return corpus;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#getFilename()
	 */
	public String getFilename() {
		return filename;
	}



	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#getSentences()
	 */
	public List<ISentence> getSentences() {
		return sentList;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#getSentence(int)
	 */
	public ISentence getSentence(int sentNum) {
		return sentMap.get(sentNum);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#hasParagraphs()
	 */
	public boolean hasParagraphs() {
		return !paraList.isEmpty();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IContext#getParagraphs()
	 */
	public List<IParagraph> getParagraphs() {
		return paraList;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if(data != null) return data;
			
		// calculate size
		int size = 70;
		if(hasParagraphs()){
			for(IParagraph p : getParagraphs()){
				size += p.getData().length() + 1;
			}
		} else {
			for(ISentence s : getSentences()){
				size += s.getData().length()+1;
			}
		}
		
		StringBuilder sb = new StringBuilder(size);
		
		// the <contextfile concordance=brown> line
		sb.append(TAG_CONTEXTFILE_LEFT);
		sb.append(SPACE);
		sb.append(ATTR_CONCORDANCE);
		sb.append(EQUALS);
		sb.append(id.getConcordanceName());
		sb.append(RIGHT_ANGLE_BRACKET);
		sb.append(NEWLINE);
		
		// the <context filename=br-a## paras=yes>
		sb.append(TAG_CONTEXT_LEFT);
		sb.append(SPACE);
		sb.append(ATTR_FILENAME);
		sb.append(EQUALS);
		sb.append(id.getContextName());
		if(hasParagraphs()){
			sb.append(SPACE);
			sb.append(ATTR_PARAS);
			sb.append(EQUALS);
			sb.append(VALUE_YES);
		}
		sb.append(RIGHT_ANGLE_BRACKET);
		sb.append(NEWLINE);
		
		// content
		if(hasParagraphs()){
			for(IParagraph p : getParagraphs()){
				sb.append(p.getData());
				sb.append(NEWLINE);
			}
		} else {
			for(ISentence s : getSentences()){
				sb.append(s.getData());
				sb.append(NEWLINE);
			}
		}
		
		// the end
		sb.append(TAG_CONTEXT_RIGHT);
		sb.append(NEWLINE);
		sb.append(TAG_CONTEXTFILE_RIGHT);
		
		data = sb.toString();
		return data;
	}



}

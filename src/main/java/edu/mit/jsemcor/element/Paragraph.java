/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Default, concrete implementation of the {@link IParagraph} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.62, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Paragraph extends AbstractList<ISentence> implements IParagraph {

	private final int number;
	private final List<ISentence> sentList;
	private String data;

	/**
	 * Constructs a new paragraph with the specified number, using the specified
	 * sentences as constituents.
	 * 
	 * @throws IllegalArgumentException
	 *             if specified the paragraph number is less than 1, or if the
	 *             sentence list is empty,
	 * @throws NullPointerException
	 *             if the sentence list or any of its elements are
	 *             <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public Paragraph(int number, List<ISentence> sents) {
		if(number < 1) throw new IllegalArgumentException();
		if(sents == null) throw new NullPointerException();
		if(sents.isEmpty()) throw new IllegalArgumentException();
		
		this.number = number;
		this.sentList = new ArrayList<ISentence>(sents);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IParagraph#getNumber()
	 */
	public int getNumber() {
		return number;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public ISentence get(int index) {
		return sentList.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return sentList.size();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if(data == null){
			
			// calculate size
			int size = 16;
			for(ISentence s : sentList){
				size = s.getData().length() + 1;
			}
			
			// allocate buffer
			StringBuilder sb = new StringBuilder(size);
			
			sb.append(TAG_PARA_LEFT);
			sb.append(SPACE);
			sb.append(ATTR_PNUM);
			sb.append(EQUALS);
			sb.append(number);
			sb.append(RIGHT_ANGLE_BRACKET);
			sb.append(NEWLINE);
			
			for(ISentence s : sentList){
				sb.append(s.getData());
				sb.append(NEWLINE);
			}
			
			sb.append(TAG_PARA_RIGHT);
			data = sb.toString();
		}
		return data;
	}

}

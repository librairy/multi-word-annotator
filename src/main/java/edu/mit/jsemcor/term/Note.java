/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.term;

import edu.mit.jsemcor.data.ContextParser;
import edu.mit.jsemcor.element.IWordform;

/**
 * Default implementation of an {@link INote} object that may be attached to an
 * {@link IWordform} object.
 * 
 * @author M.A. Finlayson
 * @version 1.62, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Note extends Terminal implements INote {

	private final INoteType type;

	/**
	 * Constructs a new note. The value of the note should not include the
	 * quotation marks that surround the value in the file. If quotes must be
	 * stripped, callers can use {@link ContextParser#trimQuotes(String)} before
	 * constructing this object.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public Note(String value, INoteType type) {
		super(ATTR, value);
		if (type == null) throw new NullPointerException();
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.term.INote#getType()
	 */
	public INoteType getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if (data == null) {
			int size = type.getData().length() + getAttribute().length()
					+ getValue().length() + 4;
			StringBuilder sb = new StringBuilder(size);
			sb.append(type.getData());
			sb.append(SPACE);
			sb.append(getAttribute());
			sb.append(EQUALS);
			sb.append(OPEN_QUOTE);
			sb.append(getValue());
			sb.append(CLOSE_QUOTE);
			data = sb.toString();
		}
		return data;
	}

}

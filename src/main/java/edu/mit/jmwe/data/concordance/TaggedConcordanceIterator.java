/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data.concordance;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.util.StreamAdapter;

/**
 * An iterator over a list of tagged concodrance sentences. This iterator does not
 * support the {@link #remove()} operation. Each tagged sentence must be on its
 * own line, in the following format:
 * 
 * <pre>
 * concordanceName/contextID/sentNumber [tok_tag_stems_num_part]+
 * </pre>
 * 
 * @see ConcordanceTagger
 * @author M.A. Finlayson
 * @version $Id: TaggedConcordanceIterator.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class TaggedConcordanceIterator implements Iterator<IConcordanceSentence> {
	
	// final instance field
	protected final BufferedReader source;
	
	// dynamic instance fields
	protected IConcordanceSentence next;

	/**
	 * Constructs the iterator form the given source file.
	 * 
	 * @param file
	 *            the source file of tagged sentences in the proper format.
	 * @throws IOException
	 *             if an IOException occurs when opening or reading from the
	 *             file.
	 * @since jMWE 1.0.0
	 */
	public TaggedConcordanceIterator(File file) throws IOException {
		this(new InputStreamReader(StreamAdapter.make(file)));
	}

	/**
	 * Constructs the iterator from a {@link URL} pointing to a list of tagged
	 * semcor sentences.
	 * 
	 * @param url
	 *            a url pointing to a list of tagged semcor sentences, may not
	 *            be <code>null</code>
	 * @throws NullPointerException
	 *             if source is <code>null</code>
	 * @throws IOException
	 *             if an IOException occurs when opening or reading from the
	 *             file.
	 * @since jMWE 1.0.0
	 */
	public TaggedConcordanceIterator(URL url) throws IOException {
		this(new InputStreamReader(StreamAdapter.make(url)));
	}
	
	/**
	 * Constructs the iterator from a reader.
	 * 
	 * @param r
	 *            the reader from which the data is read
	 * @throws IOException
	 *             if there is an exception reading from the reader
	 * @throws NullPointerException
	 *             if the reader is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public TaggedConcordanceIterator(Reader r) throws IOException {
		source = new BufferedReader(r);
		advance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public IConcordanceSentence next() {
		IConcordanceSentence result = next;
		try {
			advance();
		} catch (IOException e) {
			throw new NoSuchElementException(e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * Internal method used to advance the iterator to the next element in the
	 * list.
	 * 
	 * @throws IOException
	 *             if the reader reaches the end of the file (there are no
	 *             elements left)
	 * @since jMWE 1.0.0
	 */
	protected void advance() throws IOException {
		next = null;
		String line;
		while ((line = source.readLine()) != null)
			if (!ignoreLine(line)){
				next = ConcordanceSentence.parse(line);
				break;
			}
	}

	/**
	 * Indicates whether the specified line is to be ignored. Lines where the
	 * first non-whitespace character is a double slash ('//') or double
	 * semicolon (';;') are considered comment lines, and will cause this method
	 * to return <code>true</code>. Lines consisting of all whitespace will also
	 * cause this method to return <code>true</code>. Otherwise, the method
	 * returns <code>false</code>.
	 * 
	 * @param line
	 *            the line being considered
	 * @return true if the line begins with a double slash, double semicolon or
	 *         is all whitespace.
	 * @since jMWE 1.0.0
	 */
	protected boolean ignoreLine(String line) {
		line = line.trim();
		if(line.length() == 0)
			return true;
		if(line.startsWith(IMWEIndex.commentDoubleSlash))
			return true;
		if(line.startsWith(IMWEIndex.commentDoubleSemicolon))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return next != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

}

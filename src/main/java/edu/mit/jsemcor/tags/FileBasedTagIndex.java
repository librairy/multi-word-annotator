/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.mit.jsemcor.main.BufferToStreamAdapter;

/**
 * Default, concrete implementation of the {@link ITagIndex} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class FileBasedTagIndex implements ITagIndex {
	
	private final String name;
	private final ByteBuffer buffer;
	
	/**
	 * Constructs a new tag index for the concordance of the specified name,
	 * using the specified {@link File} as a data source.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public FileBasedTagIndex(String concordanceName, File file) throws IOException {
		if(concordanceName.trim().length() == 0) throw new IllegalArgumentException();
		this.name = concordanceName;
		this.buffer = BufferToStreamAdapter.makeByteBuffer(file);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.tags.ITagListFile#getConcordanceName()
	 */
	public String getConcordanceName() {
		return name;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.tags.ITagListFile#getTagList(java.lang.String)
	 */
	public ITagList getTagList(String senseKey) {
		String line = getLine(senseKey);
		if(line == null) return null;
		TagFileLineParser parser = TagFileLineParser.getInstance();
		return parser.parseLine(line, name);
	}
	
	/**
	 * Finds the line that starts with the specified string.
	 * 
	 * @throws NullPointerException
	 *             if the specified string is <code>null</code>
	 * @return the line in the tag index file that begins with the specified
	 *         string, or <code>null</code> if no such line can be found
	 * @since JSemcor 1.0.0
	 */
	protected String getLine(String key) {
		
		Comparator<String> comparator = TagFileLineComparator.getInstance();
		ByteBuffer buffer = this.buffer;
		
		synchronized (buffer) {
			int start = 0;
			int stop = buffer.limit();
			int midpoint = (stop + start) / 2;
			int compare;
			String line;
			while (start < midpoint | stop - start > 1) {

				midpoint = (start + stop) / 2;
				buffer.position(midpoint);
				line = getLine(buffer);
				if (midpoint > 0) line = getLine(buffer);

				if (line == null || line.length() == 0) {
					// we have reached the last line of the file, so return
					// the last line if it matches
					buffer.position(start);
					line = getLine(buffer);
					String newline = getLine(buffer);
					while (newline != null) {
						line = newline;
						newline = getLine(buffer);
					}
					return comparator.compare(line, key) == 0 ? line : null;
				}
				compare = comparator.compare(line, key);
				if (compare == 0) {
					return line;
				}
				else if (compare > 0) {
					stop = midpoint;
				}
				else {
					start = midpoint;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the String from the current position up to, and including, the
	 * next set of newline characters ('\n', '\r', or '\r\n').
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected String getLine(ByteBuffer buf){
		StringBuilder input = new StringBuilder();
		char c;
		boolean eol = false;
		int limit = buf.limit();
		
		while (!eol && buf.position() < limit) {
			c = (char)buf.get();
		    switch (c) {
			    case '\n':
					eol = true;
					break;
			    case '\r':
					eol = true;
					int cur = buf.position();
					c = (char)buf.get();
					if (c != '\n') {
						buf.position(cur);
					} 
					break;
			    default:
					input.append(c);
					break;
		    }
		}

		return (buf.position() == limit && input.length() == 0) ? null : input.toString();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ITagList> iterator() {
		return new TagListFileIterator(buffer);
	}
	
	/**
	 * Iterates over lines in a file. This is a look-ahead iterator.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public class TagListFileIterator implements Iterator<ITagList> {

		final ByteBuffer myBuffer;
		final TagFileLineParser parser;
		ITagList next;

		/**
		 * Constructs a new iterator ontop of the specified buffer. This
		 * iterator begins at the beginning of the file.
		 * 
		 * @throws NullPointerException
		 *             if the specified buffer is null.
		 * 
		 * @since JSemcor 1.0.0
		 */
		public TagListFileIterator(ByteBuffer buffer) {
			this.myBuffer = buffer.asReadOnlyBuffer();
			this.parser = TagFileLineParser.getInstance();
			loadNext();
		}

		/**
		 * Loads the next {@link ITagList} into the iterator to be returned by
		 * the next call to {@link #next()}. Skips over empty lines to find the
		 * next line that would be returned by the iterator in a call to next(), and parses it into 
		 * 
		 * @since JSemcor 1.0.0
		 */
		protected void loadNext() {
			next = null;
			String line;
			do {
				line = getLine(myBuffer);
			} while (line != null && skipLine(line));
			if(line != null) next = parser.parseLine(line, name);
		}
		
		/**
		 * Returns whether this line should be skipped, that is, whether the
		 * line is empty or the first is not a letter or digit.
		 * 
		 * @return <code>true</code> if the specified line is empty, all
		 *         whitespace, or its first character is not a letter or a digit
		 * @throws NullPointerException
		 *             if the specified {@link String} is <code>null</code>
		 * @since JSemcor 1.0.0
		 */
		protected boolean skipLine(String line){
			if(line.trim().length() == 0) return true;
			if(!Character.isLetterOrDigit(line.charAt(0))) return true;
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
		 * @see java.util.Iterator#next()
		 */
		public ITagList next() {
			if(next == null) throw new NoSuchElementException();
			ITagList result = next;
			loadNext();
			return result;
		}

		/**
		 * This iterator does not support this method.
		 * 
		 * @throws UnsupportedOperationException
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

}

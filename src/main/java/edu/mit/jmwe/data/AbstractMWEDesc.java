/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.mit.jmwe.data.IMWEDesc.IPart;
import edu.mit.jmwe.data.IRootMWEDesc.IRootPart;
import edu.mit.jmwe.detect.Exhaustive;

/**
 * A base class for MWE descriptions that can be used to construct a description
 * from some combination of: a surface form, a list of parts, and counts
 * relating to the MWE's appearance in a reference concordance.
 * 
 * @param <P> the type of the part for this mwe description
 * @author M.A. Finlayson
 * @version $Id: AbstractMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public abstract class AbstractMWEDesc<P extends IPart> implements IMWEDesc {

	// final instance fields
	private final String form;
	private final List<P> parts;
	protected final int[] counts;

	/**
	 * Constructs a new MWE description object from the specified surface form
	 * that has no inflected forms.
	 * 
	 * @param surfaceForm
	 *            A string representing the MWE with its words separated by
	 *            underscores
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the surface form does not contain underscores
	 * @since jMWE 1.0.0
	 */
	public AbstractMWEDesc(String surfaceForm) {
		this(splitOnUnderscores(surfaceForm), null);
	}

	/**
	 * Constructs a new MWE description object that has no inflected forms from
	 * the specified surface form and counts relating to the MWE's appearance in
	 * a reference concordance.
	 * 
	 * @param surfaceForm
	 *            A string representing the MWE with its words separated by
	 *            underscores
	 * @param counts
	 *            the implementation-specific counts relating to the MWE's
	 *            appearance in a reference concordance.
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the surface form does not contain underscores
	 * @since jMWE 1.0.0
	 */
	public AbstractMWEDesc(String surfaceForm, int... counts) {
		this(splitOnUnderscores(surfaceForm), counts);
	}

	/**
	 * Constructs a new MWE description object from the list of parts. This
	 * constructor allocates a new internal list, and so subsequent changes to
	 * the source list will not affect this object.
	 * 
	 * @param parts
	 *            the list of parts that will make up this list, may neither be
	 *            <code>null</code> nor empty, and may not contain any
	 *            <code>null</code>s, empty or all whitespace strings, or
	 *            strings that contain the underscore character.
	 * @throws NullPointerException
	 *             if the specified list of parts is <code>null</code>, or
	 *             contains a <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified list has less than two elements, or any
	 *             trimmed string in the list contains an underscore, is empty,
	 *             or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public AbstractMWEDesc(List<String> parts) {
		this(parts, null);
	}

	/**
	 * Constructs a new MWE description object from the list of parts and counts
	 * relating to the MWE's appearance in a reference concordance. This
	 * constructor allocates a new internal list, and so subsequent changes to
	 * the source list will not affect this object.
	 * 
	 * @param parts
	 *            the list of parts that will make up this list, may neither be
	 *            <code>null</code> nor empty, and may not contain any
	 *            <code>null</code>s, empty or all whitespace strings, or
	 *            strings that contain the underscore character.
	 * @param counts
	 *            the implementation-specific counts relating to the MWE's
	 *            appearance in a reference concordance.
	 * @throws NullPointerException
	 *             if the specified list of parts is <code>null</code>, or
	 *             contains a <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified list has less than two elements, or any
	 *             trimmed string in the list contains an underscore, is empty,
	 *             or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public AbstractMWEDesc(List<String> parts, int... counts) {
		if (parts == null)
			throw new NullPointerException();
		if (counts != null)
			for (int count : counts)
				checkCount(count);

		// do parts
		String part;
		StringBuilder form = new StringBuilder();
		List<P> hidden = new ArrayList<P>(parts.size());
		for (ListIterator<String> i = parts.listIterator(); i.hasNext();) {
			part = Token.checkString(i.next()).toLowerCase();
			if (part.indexOf('_') != -1)
				throw new IllegalArgumentException();
			form.append(part);
			if (i.hasNext())
				form.append('_');
			hidden.add(makePart(part, i.previousIndex()));
		}

		// create count array
		int countNum = (counts == null) ? 
				getExpectedCountLength() : 
					Math.max(counts.length, getExpectedCountLength());
		int[] hiddenCounts = new int[countNum];
		if (counts != null)
			System.arraycopy(counts, 0, hiddenCounts, 0, counts.length);

		// assign fields
		this.form = form.toString();
		this.parts = Collections.unmodifiableList(hidden);
		this.counts = hiddenCounts;
	}

	/**
	 * Subclasses should implement this method to return the number of counts
	 * relating to the MWE's appearance in a reference concordance that are
	 * expected in the implementation.
	 * 
	 * @return the number of counts relating to the MWE's appearance in a
	 *         reference concordance.
	 * @since jMWE 1.0.0
	 */
	protected abstract int getExpectedCountLength();

	/**
	 * Checks that each passed in count is non-negative.
	 * 
	 * @param count
	 *            the count to be checked
	 * @throws IllegalArgumentException
	 *             if the count is less than zero
	 * @return the given count if it is non negative.
	 * @since jMWE 1.0.0
	 */
	protected static int checkCount(int count) {
		if (count < 0)
			throw new IllegalArgumentException();
		return count;
	}

	/**
	 * Subclasses should implement this method to construct an {@link IPart}
	 * given the form and index of a part of an MWE.
	 * 
	 * @param form
	 *            the text of the part
	 * @param index
	 *            the index of the part in the MWE
	 * @return the part description object, will not be <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected abstract P makePart(String form, int index);

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasForm#getForm()
	 */
	public String getForm() {
		return form;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getMarkedContinuous()
	 */
	public int getMarkedContinuous() {
		return counts[0];
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getMarkedSplit()
	 */
	public int getMarkedSplit() {
		return counts[1];
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getUnmarkedExact()
	 */
	public int getUnmarkedExact() {
		return counts[2];
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getUnmarkedPattern()
	 */
	public int getUnmarkedPattern() {
		return counts[3];
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getParts()
	 */
	public List<P> getParts() {
		return parts;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getCounts()
	 */
	public int[] getCounts() {
		int[] result = new int[counts.length];
		System.arraycopy(counts, 0, result, 0, counts.length);
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(IMWEDesc id) {
		int cmp = form.compareTo(id.getForm());
		if (cmp != 0)
			return cmp;
		return getPOS().compareTo(id.getPOS());
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return form + "_" + getPOS().getIdentifier();
	}

	/**
	 * Helper method that calculates, for efficiency's sake, whether this MWE
	 * part is a stop word. This implementation uses a standard set of stop
	 * words used by the {@link Exhaustive#getStopWords()} method. Subclasses
	 * may override this method to use a different set of stop words.
	 * 
	 * @param text
	 *            text, to be checked for being a stop word
	 * @return <code>true</code> if the verbatim text is a stop word;
	 *         <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	protected boolean isStopWord(String text) {
		return StopWords.isStopWord(text);
	}

	/**
	 * Returns <code>true</code> if the root descriptions associated with each
	 * of this MWE descriptions are the same; <code>false</code> otherwise.
	 * 
	 * @param one
	 *            the first mwe description
	 * @param two
	 *            the second mwe description
	 * @return <code>true</code> if the root descriptions associated with each
	 *         of this MWE descriptions are the same; <code>false</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static boolean equalsRoots(IMWEDesc one, IMWEDesc two) {
		IRootMWEDesc oneRoot = getRoot(one);
		IRootMWEDesc twoRoot = getRoot(two);
		return oneRoot.getID().equals(twoRoot.getID());
	}

	/**
	 * Returns the root mwe description associated with this object.
	 * 
	 * @param desc
	 *            the mwe object object from which to extract the root
	 * @return the root for this object
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static IRootMWEDesc getRoot(IMWEDesc desc) {
		if(desc == null)
			throw new NullPointerException();
		if (desc instanceof IRootMWEDesc)
			return (IRootMWEDesc) desc;
		IInfMWEDesc infDesc = (IInfMWEDesc) desc;
		return infDesc.getRootMWEDesc();
	}

	/**
	 * Splits a specified string into constituent strings that are separated by
	 * underscores. This method strips leading and trailing whitespace, leading
	 * and trailing runs of underscores, and treats runs of underscores as a
	 * single delimiter.
	 * 
	 * @param str
	 *            a string to be split into underscore-delimited parts
	 * @return an unmodifiable list of strings that were delimited by
	 *         underscores in the original string
	 * @throws NullPointerException
	 *             if the specified string is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static List<String> splitOnUnderscores(String str) {
		str = str.trim();
		str = boundaryUnderscores.matcher(str).replaceAll("");
		String[] parts = underscores.split(str);
		return Collections.unmodifiableList(Arrays.asList(parts));
	}

	/**
	 * Utility method for concatenating collections of strings into a single
	 * string using a specified separator.
	 * 
	 * @param parts
	 *            List of parts to be concatenated, may not be <code>null</code>
	 * @param separator
	 *            String used to separate the parts in the result, may be
	 *            <code>null</code>.
	 * @return a single string resulting from the concatenation of the parts
	 *         with the separator in between each
	 * @throws NullPointerException
	 *             if the specified iterable is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static String concatenate(Iterable<String> parts, String separator) {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> i = parts.iterator(); i.hasNext();) {
			sb.append(i.next().toLowerCase());
			if (i.hasNext())
				sb.append(separator);
		}
		return sb.toString();
	}

	/**
	 * Returns true if the part's lemma matches either the surface form of the
	 * given token or any of the token's stems, regardless of case.
	 * 
	 * @param token
	 *            the token to be compared to the part's lemma
	 * @param part
	 *            the part whose lemma is to be compared to the token
	 * @return true if the part's lemma matches either the surface form of the
	 *         given token or any of the token's stems, regardless of case.
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static boolean isFillerForSlot(IToken token, IPart part) {
		if (token == null)
			throw new NullPointerException();
		if (part == null)
			throw new NullPointerException();
		if (part.getForm().equalsIgnoreCase(token.getForm()))
			return true;
		if (part instanceof IRootPart && token.getStems() != null)
			for (String stem : token.getStems())
				if (part.getForm().equalsIgnoreCase(stem))
					return true;
		return false;
	}

	/**
	 * Default implementation of the <code>IPart</code> interface.
	 * 
	 * @author M.A. Finlayson
	 * @version $Id: AbstractMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	protected abstract class AbstractPart implements IPart {

		// final instance fields
		private final String form;
		private final int index;
		private final boolean isStopWord;

		/**
		 * Constructs a new part.
		 * 
		 * @param form
		 *            the surface text of the part in the multi-word expression,
		 *            in its original form. It's trimmed form not be
		 *            <code>null</code> or contain whitespace.
		 * @param index
		 *            the index of the part in the multi-word expression. May
		 *            not be less than 0.
		 * @throws NullPointerException
		 *             if the parent or text is <code>null</code>.
		 * @throws IllegalArgumentException
		 *             if the trimmed text is empty or contains whitespace, or
		 *             if the index is less than 0.
		 * @since jMWE 1.0.0
		 */
		public AbstractPart(String form, int index) {
			// check arguments
			if (index < 0)
				throw new IllegalArgumentException();
			form = Token.checkString(form);
			boolean isStopWord = AbstractMWEDesc.this.isStopWord(form);

			// assign fields
			this.form = form;
			this.index = index;
			this.isStopWord = isStopWord;
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.IMWEDesc.IPart#getParent()
		 */
		public IMWEDesc getParent() {
			return AbstractMWEDesc.this;
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.IHasForm#getForm()
		 */
		public String getForm() {
			return form;
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.IMWEDesc.IPart#getIndex()
		 */
		public int getIndex() {
			return index;
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.IMWEDesc.IPart#isStopWord()
		 */
		public boolean isStopWord() {
			return isStopWord;
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return form + ':' + Integer.toString(index);
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(IPart o) {
			return index - o.getIndex();
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
			result = prime * result + AbstractMWEDesc.this.hashCode();
			result = prime * result + index;
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
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			AbstractPart other = (AbstractPart) obj;
			if (index != other.index)
				return false;
			if (!AbstractMWEDesc.this.equals(other.getParent()))
				return false;
			return true;
		}
	}
}

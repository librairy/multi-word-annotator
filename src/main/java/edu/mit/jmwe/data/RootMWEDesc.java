/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.mit.jmwe.data.IRootMWEDesc.IRootPart;
import edu.mit.jmwe.data.InfMWEDesc.InfMWEDescBuilder;

/**
 * Default implementation of the <code>IRootMWEDesc</code> interface.
 * 
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: RootMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class RootMWEDesc extends AbstractMWEDesc<IRootPart> implements IRootMWEDesc {
	
	// final instance fields
	private final IMWEDescID id;
	private final Map<String, IInfMWEDesc> inflected;

	/**
	 * Constructs a new MWE description object from the specified surface form
	 * and part of speech, that has no inflected forms
	 * 
	 * @param surfaceForm
	 *            A string representing the MWE with its words separated by
	 *            underscores
	 * @param pos
	 *            The part of speech object representing the part of speech of
	 *            the MWE
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the surface form does not contain underscores
	 * @since jMWE 1.0.0
	 */
	public RootMWEDesc(String surfaceForm, MWEPOS pos){
		this(splitOnUnderscores(surfaceForm), pos, (Collection<? extends InfMWEDescBuilder>)null, null);
	}

	/**
	 * Constructs a new MWE description object from the specified surface form,
	 * inflected forms, part of speech, and counts relating to the MWE's
	 * appearance in the reference concordance.
	 * 
	 * @param surfaceForm
	 *            A string representing the MWE with its words separated by
	 *            underscores
	 * @param pos
	 *            The part of speech object representing the part of speech of
	 *            the MWE
	 * @param inflectedForms
	 *            the collection of builders used to create the descriptions of
	 *            the inflected forms of this MWE.
	 * @param counts
	 *            the counts relating to the MWE's appearance in the reference
	 *            concordance.
	 * @throws NullPointerException
	 *             if the surface form or pos is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the surface form does not contain underscores
	 * @since jMWE 1.0.0
	 */
	public RootMWEDesc(String surfaceForm, MWEPOS pos, Collection<? extends InfMWEDescBuilder> inflectedForms, int... counts){
		this(splitOnUnderscores(surfaceForm), pos, inflectedForms, counts);
	}

	/**
	 * Constructs a new MWE description object from the specified list of parts
	 * and part of speech, that has no inflected forms
	 * 
	 * @param parts
	 *            A list of parts that comprise the MWE
	 * @param pos
	 *            The part of speech object representing the part of speech of
	 *            the MWE
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified list has less than two elements, or any
	 *             trimmed string in the list contains an underscore, is empty,
	 *             or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public RootMWEDesc(List<String> parts, MWEPOS pos){
		this(parts, pos, (Collection<? extends InfMWEDescBuilder>)null, null);
	}

	/**
	 * Constructs a new MWE description object from the specified list of parts,
	 * inflected forms, part of speech, and counts relating to the MWE's
	 * appearance in the reference concordance.
	 * 
	 * @param parts
	 *            A list of parts that comprise the MWE
	 * @param pos
	 *            The part of speech object representing the part of speech of
	 *            the MWE
	 * @param inflectedForms
	 *            the collection of builders used to create the descriptions of
	 *            the inflected forms of this MWE.
	 * @param counts
	 *            the counts relating to the MWE's appearance in the reference
	 *            concordance.
	 * @throws NullPointerException
	 *             if the list of parts or pos is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified list has less than two elements, or any
	 *             trimmed string in the list contains an underscore, is empty,
	 *             or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public RootMWEDesc(List<String> parts, MWEPOS pos, Collection<? extends InfMWEDescBuilder> inflectedForms, int... counts){
		super(parts, counts);
		
		// this is needed to construct the inflected forms
		this.id = new MWEDescID(getForm(), pos);

		// inflected forms
		Map<String, IInfMWEDesc> inflected;
		if(inflectedForms == null || inflectedForms.size() == 0){
			inflected = Collections.emptyMap();
		} else {
			inflected = new TreeMap<String, IInfMWEDesc>();
			IInfMWEDesc infDesc;
			for(InfMWEDescBuilder b : inflectedForms){
				infDesc = b.toInfMWEDesc(this);
				inflected.put(infDesc.getForm(), infDesc);
			}
		}
		
		// assign fields
		this.inflected = Collections.unmodifiableMap(inflected);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.AbstractMWEDesc#getExpectedCountLength()
	 */
	@Override
	protected int getExpectedCountLength() {
		return IRootMWEDesc.EXPECTED_COUNT_LENGTH;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.AbstractMWEDesc#makePart(java.lang.String, int)
	 */
	@Override
	protected IRootPart makePart(String form, int index) {
		return new Part(form, index);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getID()
	 */
	public IMWEDescID getID() {
		return id;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasMWEPOS#getPOS()
	 */
	public MWEPOS getPOS(){
		return id.getPOS();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IRootMWEDesc#getInflected()
	 */
	public Map<String, ? extends IInfMWEDesc> getInflected() {
		return inflected;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IRootMWEDesc#getUnmarkedInflected()
	 */
	public int getUnmarkedInflected() {
		return counts[4];
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
		result = prime * result + id.hashCode();
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
		RootMWEDesc other = (RootMWEDesc) obj;
		if(!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Default implementation of {@link IRootPart}.
	 *
	 * @author M.A. Finlayson
	 * @version $Id: RootMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	protected class Part extends AbstractPart implements IRootPart {

		/**
		 * Constructs the part from its text and index in the MWE.
		 * 
		 * @param form
		 *            the text of the part
		 * @param index
		 *            the position of this part in the MWE
		 * @since jMWE 1.0.0
		 */
		public Part(String form, int index) {
			super(form, index);
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.AbstractMWEDesc.AbstractPart#getParent()
		 */
		@Override
		public IRootMWEDesc getParent() {
			return (IRootMWEDesc)super.getParent();
		}
		
	}

	/**
	 * Returns the String representation of the given description.
	 * 
	 * @param mweDesc
	 *            the description to be represented as a String.
	 * @return the String representation of the given description.
	 * @since jMWE 1.0.0
	 */
	public static String toString(IRootMWEDesc mweDesc) {
		StringBuilder buf = new StringBuilder();
		try {
			toString(mweDesc, buf);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buf.toString();
	}

	/**
	 * Returns the String representation of the given description as:
	 * <p>
	 * <i>id count[0],count[1], ...,count[n] inf[1] inf[2] ... inf[m]</i>
	 * </p>
	 * 
	 * inf[i] is an inflected form of the MWE represented as:
	 * 
	 * <p>
	 * <i>infForm count[0],count[1], ...,count[n]</i>
	 * </p>
	 * 
	 * @param root
	 *            the description to be represented as a String.
	 * @param buf
	 *            the appendable buffer used to build the String.
	 * @throws IOException
	 *             if there is an exception while appending the provided buffer.
	 * @since jMWE 1.0.0
	 */
	public static void toString(IRootMWEDesc root, Appendable buf) throws IOException {
		
		// form and pos
		MWEDescID.toString(root.getID(), buf);
		buf.append(' ');
		toString(root.getCounts(), buf);
		
		for(IInfMWEDesc inf : root.getInflected().values()){
			buf.append(' ');
			buf.append(inf.getForm());
			buf.append(' ');
			toString(inf.getCounts(), buf);
		}
	}

	/**
	 * Returns a string containing the given counts as
	 * <p>
	 * <i>count[0],count[1], ...,count[n]</i>
	 * </p>
	 * 
	 * @param counts
	 *            the counts to be represented as a String
	 * @param buf
	 *            the appendable buffer used to build the String.
	 * @throws IOException
	 *             if there is an exception while appending the provided buffer.
	 * @since jMWE 1.0.0
	 */
	public static void toString(int[] counts, Appendable buf) throws IOException {
		int last = counts.length - 1;
		for(int i = 0; i < counts.length; i++){
			buf.append(Integer.toString(counts[i]));
			if(i < last)
				buf.append(',');
		}
	}

	/**
	 * Parses the given description string into a root mwe description object.
	 * 
	 * @param descStr
	 *            the string to be parsed
	 * @return an {@link IRootMWEDesc} containing the fields specified in the
	 *         given string.
	 * @since jMWE 1.0.0
	 */
	public static IRootMWEDesc parse(String descStr) {
		try {
			StringTokenizer tokens = new StringTokenizer(descStr);
			
			// root form and its counts (if any)
			String rootForm = tokens.nextToken();
			MWEPOS pos = MWEPOS.fromChar(rootForm.charAt(rootForm.length()-1));
			rootForm = rootForm.substring(0, rootForm.length()-2);
			int[] rootCounts = tokens.hasMoreTokens() ? parseCounts(tokens.nextToken()) : null;
			
			// inflected forms
			List<InfMWEDescBuilder> infForms = new LinkedList<InfMWEDescBuilder>();
			InfMWEDescBuilder b;
			while(tokens.hasMoreTokens()){
				b = new InfMWEDescBuilder();
				b.form = tokens.nextToken();
				b.counts = parseCounts(tokens.nextToken());
				infForms.add(b);
			}
			
			return new RootMWEDesc(rootForm, pos, infForms, rootCounts);
		} catch(Throwable t){
			throw new RuntimeException("Unable to parse line: " + descStr, t);
		}
	}

	/**
	 * Parses a string of counts separated by commas into an array of integers.
	 * 
	 * @param countStr
	 *            the string to be parsed.
	 * @return an int array containing the counts in the given string
	 * @since jMWE 1.0.0
	 */
	public static int[] parseCounts(String countStr){
		String[] countStrs = comma.split(countStr);
		int[] counts = new int[countStrs.length];
		for(int i = 0; i < countStrs.length; i++)
			counts[i] = Integer.parseInt(countStrs[i]);
		return counts;
	}
	
}

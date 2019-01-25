/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.List;
import java.util.regex.Pattern;

/**
 * An MWE description consisting of an {@link IMWEDescID}, list of parts, and
 * counts relating to the MWE's appearance in a reference concordance.
 * 
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: IMWEDesc.java 281 2011-05-05 19:40:04Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWEDesc extends IHasForm, IHasMWEPOS, Comparable<IMWEDesc> {

	/**
	 * The pattern consisting of a single underscore.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static Pattern underscore = Pattern.compile("_");

	/**
	 * The pattern consisting of a single underscore.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static Pattern comma = Pattern.compile(",");

	/**
	 * The pattern consisting of one or more underscores.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static Pattern underscores = Pattern.compile("_+");

	/**
	 * The pattern consisting of one or more underscores that occur at the
	 * beginning or end of the input.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static Pattern boundaryUnderscores = Pattern.compile("(^_+)|(_+$)");

	/**
	 * Returns the IMWEDescID associated with this description.
	 * 
	 * @return the IMWEDescID associated with this description. Never
	 *         <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public IMWEDescID getID();

	/**
	 * Returns the number of times this MWE was marked on a continuous run of
	 * tokens in the reference concordance. Will always zero or a positive
	 * number.
	 * 
	 * @return the number of times this MWE was marked on a unbroken run of
	 *         tokens in the reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int getMarkedContinuous();

	/**
	 * Returns the number of times this MWE was marked on a non-continuous run
	 * of tokens in the reference concordance. Will always zero or a positive
	 * number.
	 * 
	 * @return the number of times this MWE was marked on a non-continuous run
	 *         of tokens in the reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int getMarkedSplit();

	/**
	 * Returns the number of times the exact surface form of this MWE
	 * description occurs in the reference concordance without being marked as
	 * an occurrence of the MWE. To be counted as an exact unmarked occurrence,
	 * there must be a continuous run of tokens whose forms match, in order, the
	 * forms of the parts (ignoring case) of this MWE description. Will always
	 * zero or a positive number.
	 * 
	 * @return the number exact unmarked occurrences of this MWE in the
	 *         reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int getUnmarkedExact();

	/**
	 * Returns the number of times a this MWE description occurs in the
	 * reference concordance without being marked as an occurrence of the MWE,
	 * and whose form matches a known inflection pattern. To be counted as a
	 * pattern-inflected occurrence, there must be a continuous run of tokens
	 * whose forms or stems match, in order, the forms of the parts (ignoring
	 * case) of this MWE description, and whose inflection pattern matches one
	 * of reference inflection patterns. Will always zero or a positive number.
	 * 
	 * @return the number of inflected unmarked occurrences of this MWE in the
	 *         reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int getUnmarkedPattern();

	/**
	 * Returns an unmodifiable list of parts that comprise the MWE.
	 * 
	 * @return an unmodifiable list of parts that comprise the MWE.
	 * @since jMWE 1.0.0
	 */
	public List<? extends IPart> getParts();

	/**
	 * Returns an array containing the marked split, marked continuous, unmarked
	 * exact, and unmarked pattern occurrences of this MWE in the reference
	 * concordance.
	 * 
	 * @return an array containing the counts relating to the MWE's appearance
	 *         in the reference concordance.
	 * @since jMWE 1.0.0
	 */
	public int[] getCounts();

	/**
	 * A part of a multi-word expression.
	 * 
	 * @author M.A. Finlayson
	 * @author N. Kulkarni
	 * @version $Id: IMWEDesc.java 281 2011-05-05 19:40:04Z markaf $
	 * @since jMWE 1.0.0
	 */
	public interface IPart extends IHasForm, Comparable<IPart> {

		/**
		 * Returns the MWE description of which this part is a part. Will never
		 * return <code>null</code>.
		 * 
		 * @return the MWE description that contains this part.
		 * @since jMWE 1.0.0
		 */
		public IMWEDesc getParent();

		/**
		 * Returns the index of this part in the multi-word expression, always
		 * zero or greater.
		 * 
		 * @return the non-negative index of this part in the multi-word
		 *         expression.
		 * @since jMWE 1.0.0
		 */
		public int getIndex();

		/**
		 * Returns true if the part is a stop word, that is, a word that is not
		 * usually useful for searching.
		 * 
		 * @return <code>true</code> if the part is a stop word;
		 *         <code>false</code> otherwise.
		 * @since jMWE 1.0.0.
		 */
		public boolean isStopWord();

	}

}

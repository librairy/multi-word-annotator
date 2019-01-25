/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import edu.mit.jmwe.data.IInfMWEDesc;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IMWEDescID;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.MWEPOS;

/**
 * An index of multi-word expressions (MWEs). The individual components of a
 * multi-word expression are called the MWE's <em>parts</em>. This interface
 * provides methods for retrieving MWEs based on their constituent parts, and
 * for testing for the inclusion of a particular MWE in the index.
 * 
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version $Id: IMWEIndex.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMWEIndex {
	
	/**
	 * Denotes that the following text is a comment.
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String commentDoubleSlash = "//";
	
	/**
	 * Denotes that the following text is a comment.
	 *
	 * @since jMWE 1.0.0
	 */
	public static final String commentDoubleSemicolon = ";;";
	
	/**
	 * Prepares the index for use. Mounts necessary files, opens sockets, or
	 * loads other data into memory that allows this index to service calls to
	 * its methods without throwing an {@link IllegalStateException}.
	 * 
	 * Once open, an index can be closed by calling the {@link #close()} method.
	 * 
	 * @return <code>true</code> if the call succeeded, and the index is ready
	 *         to service calls; <code>false</code> otherwise.
	 * @throws IOException
	 *             if an IO exception is thrown while opening the index
	 * @since jMWE 1.0.0
	 */
	public boolean open() throws IOException;
	
	/**
	 * Returns whether the index is open. This method can be called at any time.
	 * 
	 * @return <code>true</code> if the index is open and ready to accept
	 *         calls; <code>false</code> otherwise.
	 * @since jMWE 1.0.0
	 */
	public boolean isOpen();
	
	/**
	 * Closes the index if open. If the index is not open, this method does
	 * nothing. Once closed, and index may be reopened.
	 * 
	 * @since jMWE 1.0.0
	 */
	public void close();

	/**
	 * Returns the MWE description for the specified ID, or <code>null</code> if
	 * this index does not contain said description.
	 * 
	 * @param id
	 *            the id for the MWE description desired; may not be
	 *            <code>null</code>
	 * @return the MWE description for the specified ID, or <code>null</code> if
	 *         none
	 * @throws IllegalStateException
	 *             if this method is called when the index is closed
	 * @since jMWE 1.0.0
	 */
	public IMWEDesc get(IMWEDescID id);

	/**
	 * Returns the root MWE description for the specified lemma and part of
	 * speech, or <code>null</code> if this index does not contain said
	 * description.
	 * 
	 * @param lemma
	 *            the lemma for the MWE description desired; may not be
	 *            <code>null</code>
	 * @param pos
	 *            the part of speech for the MWE description desired; may not be
	 *            <code>null</code>
	 * @return the root MWE description matching the specified lemma and part of
	 *         speech, or <code>null</code> if none
	 * @throws IllegalStateException
	 *             if this method is called when the index is closed
	 * @since jMWE 1.0.0
	 */
	public IRootMWEDesc getRootMWEDesc(String lemma, MWEPOS pos);

	/**
	 * Returns the inflected MWE description for the specified root lemma, part
	 * of speech, and inflected form, or <code>null</code> if this index does
	 * not contain said description.
	 * 
	 * @param lemma
	 *            the lemma for the MWE description desired; may not be
	 *            <code>null</code>
	 * @param pos
	 *            the part of speech for the MWE description desired; may not be
	 *            <code>null</code>
	 * @param inflected
	 *            the inflected form of the MWE description desired; may not be
	 *            <code>null</code>
	 * @return the inflected MWE description matching the specified arguments,
	 *         or <code>null</code> if none
	 * @throws IllegalStateException
	 *             if this method is called when the index is closed
	 * @since jMWE 1.0.0
	 */
	public IInfMWEDesc getInflectedMWEDesc(String lemma, MWEPOS pos, String inflected);

	/**
	 * Returns a set of {@link IRootMWEDesc} objects whose MWEs contain the
	 * specified part. Retrieval is insensitive to case. If no entries are
	 * found, this method returns an empty set. The order of the returned set is
	 * implementation dependent.
	 * 
	 * @param part
	 *            a string representing the part for which entries should be
	 *            retrieved
	 * @return a set of MWE entry objects that contain the specified part and
	 *         are of the specified part of speech
	 * @throws NullPointerException
	 *             if the specified part is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified part is empty or all whitespace
	 * @throws IllegalStateException
	 *             if this method is called when the index is closed
	 * @since jMWE 1.0.0
	 */
	public Set<? extends IRootMWEDesc> get(String part);
	
	/**
	 * Returns a set containing both {@link IRootMWEDesc} and {@link IInfMWEDesc}
	 * objects that the given string is a part of. Retrieval is insensitive to
	 * case. If no entries are found, this method returns an empty set. The
	 * order of the returned set is implementation dependent.
	 * 
	 * @param part
	 *            a string representing the part for which entries should be
	 *            retrieved
	 * @return a set of MWE entry objects that contain the specified part
	 * @throws NullPointerException
	 *             if the specified part is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified part is empty or all whitespace
	 * @throws IllegalStateException
	 *             if this method is called when the index is closed
	 * @since jMWE 1.0.0
	 */
	public Set<? extends IMWEDesc> getAll(String part);

	/**
	 * Returns an iterator that will iterate over all root MWE descriptions in
	 * the index, in order. 
	 * 
	 * @return an iterator that will iterate over all root MWE descriptions in
	 *         the index, in order.
	 * @since jMWE 1.0.0
	 */
	public Iterator<IRootMWEDesc> getRootIterator();

	/**
	 * Returns an iterator that will iterate over all MWE parts in the index, in
	 * order.
	 * 
	 * @return an iterator that will iterate over all MWE parts in the index, in
	 *         order.
	 * @since jMWE 1.0.0
	 */
	public Iterator<String> getIndexIterator();

}



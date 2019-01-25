/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.main;

import java.util.Iterator;
import java.util.Set;

import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.tags.ITagIndex;

/**
 * Objects implementing this interface represent a semantic concordance, namely,
 * a set of texts tagged for their meaning. A concordance has the following
 * structure:
 * 
 * <pre>
 * \name
 *   taglist
 *   \tagfiles
 *     file1
 *     file2
 *     file3
 *     ...
 * </pre>
 * 
 * The root of the concordance is the <tt>name</tt> directory. It contains a
 * child directory indicated here by <tt>tagfiles</tt> that contains all the
 * SGML-markup context files in the concordance. The <tt>name</tt> directory
 * may also contain a file indicated here by <tt>taglist</tt>, which is an
 * index file that has tags mapped to locations in the context files.
 * 
 * Semcor is a set of three concordances whose names are <tt>brown1</tt>,
 * <tt>brown2</tt>, and <tt>brownv</tt>. See {@link IConcordanceSet} and
 * related classes for more details.
 * 
 * @author M.A. Finlayson
 * @version 1.637, 20 Jun 2011
 * @since JSemcor 1.0.0
 */
public interface IConcordance extends Iterable<IContext> {

	/**
	 * Performs all the necessary operations to prepare this concordance to
	 * provide contexts. This may include connecting to this concordance source
	 * URL, discovering the directory structure and file lists, and building
	 * maps from context names to context locations. The return value of this
	 * method indicates if the operations were successful or not. If this method
	 * returns <code>false</code>, then subsequent calls to {@link #isOpen()}
	 * will return <code>false</code> as well. If the concordance is already
	 * open, this method does nothing.
	 * 
	 * @return <code>true</code> if there were no errors in initialization;
	 *         <code>false</code> otherwise.
	 * @since JSemcor 1.0.0
	 */
	public boolean open();

	/**
	 * Returns whether the concordance is in a state such that it can provide
	 * context objects on demand.
	 * 
	 * @return <code>true</code> if the concordance is open;
	 *         <code>false</code> otherwise.
	 * @since JSemcor 1.0.0
	 */
	public boolean isOpen();

	/**
	 * This closes the concordance by disposing of data backing objects or
	 * connections. It should not be irreversible, though: another call to
	 * {@link #open()} should put the concordance back in the ready state. If
	 * the concordance is already closed, this method does nothing.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public void close();

	/**
	 * Returns the name of the concordance, which is a non-null, non-empty
	 * string. For example, Semcor contains three concordances with the names
	 * <tt>brown1</tt>, <tt>brown2</tt>, and <tt>brownv</tt>. This
	 * string will be equal to that returned by
	 * {@link IContextID#getConcordanceName()} method for contexts in this
	 * concordance.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getName();

	/**
	 * Returns a set of context id objects for all contexts in this concordance.
	 * The set may or may not be ordered, depending on the actual structures
	 * backing this object.
	 * 
	 * @throws ConcordanceClosedException
	 *             if the concordance is not open
	 * @since JSemcor 1.0.0
	 */
	public Set<IContextID> getContextIDs();

	/**
	 * A convenience method, identical to {@link #getContext(IContextID)}
	 * 
	 * @return the identified context, if it can be found; <code>null</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the supplied context name is null
	 * @throws IllegalArgumentException
	 *             if the context name is empty or all whitespace
	 * @throws ConcordanceClosedException
	 *             if the concordance is not open
	 * @since JSemcor 1.0.0
	 */
	public IContext getContext(String contextName);

	/**
	 * A convenience method that first checks to see if the
	 * {@link IContextID#getContextName()} equals this concordance's name; if
	 * so, the result is the same as calling {@link #getContext(String)}. If
	 * not, returns <code>null</code>.
	 * 
	 * @return the identified context, if it can be found; <code>null</code>
	 *         otherwise.
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @throws ConcordanceClosedException
	 *             if the concordance is not open
	 * @since JSemcor 1.0.0
	 */
	public IContext getContext(IContextID id);

	/**
	 * Returns an iterator over context objects contained in this concordance.
	 * 
	 * @return an iterator that returns {@link IContext} objects.
	 * @throws ConcordanceClosedException
	 *             if the concordance is not open
	 * @see Iterable#iterator()
	 * @since JSemcor 1.0.0
	 */
	public Iterator<IContext> iterator();

	/**
	 * Returns the tag index object for this concordance, if one exists. If not,
	 * returns <code>null</code>.
	 * 
	 * @return an {@link ITagIndex} object, if one can be created; otherwise,
	 *         <code>null</code>
	 * @throws ConcordanceClosedException
	 *             if the concordance is not open
	 * @since JSemcor 1.0.0
	 */
	public ITagIndex getTagIndex();

}

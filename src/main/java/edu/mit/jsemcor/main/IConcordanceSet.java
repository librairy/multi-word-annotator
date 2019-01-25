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
import java.util.Map;

import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.IContextID;

/**
 * A concordance set is a collection of {@link IConcordance} objects indexed by
 * name. It has global open and close methods that allow the manipulation of all
 * concordances in the set simultaneously.
 * 
 * @author M.A. Finlayson
 * @version 1.637, 20 Jun 2011
 * @since JSemcor 1.0.0
 */
public interface IConcordanceSet extends Map<String, IConcordance>, Iterable<IContext> {

	/**
	 * Performs all the necessary operations to prepare all concordances in this
	 * set to provide contexts. This may include connecting to this URL
	 * containing the concordance roots, constructing the {@link IConcordance}
	 * objects, and calling {@link IConcordance#open()} on those objects. The
	 * return value of this method indicates if the all operations were
	 * successful or not. If this method returns <code>false</code>, then
	 * subsequent calls to {@link #isOpen()} will return <code>false</code> as
	 * well. If the concordance is already open, this method has no effect.
	 * 
	 * @return <code>true</code> if there were no errors in initialization;
	 *         <code>false</code> otherwise.
	 * @since JSemcor 1.0.0
	 */
	public boolean open();

	/**
	 * Returns whether all concordances in the set are open.
	 * 
	 * @return <code>true</code> if all concordances in the set are open;
	 *         <code>false</code> otherwise.
	 * @since JSemcor 1.0.0
	 */
	public boolean isOpen();

	/**
	 * This closes the concordance by disposing of data backing objects or
	 * connections. It should not be irreversible, though: another call to
	 * {@link #open()} should put the concordance back in the ready state. If
	 * the concordance is already closed, this method has no effect.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public void close();

	/**
	 * A convenience method, identical to {@link #getContext(IContextID)}
	 * 
	 * @return the identified context, if it can be found; <code>null</code>
	 *         otherwise.
	 * 
	 * @throws NullPointerException
	 *             if either argument is null
	 * @throws IllegalArgumentException
	 *             if either argument is empty or all whitespace
	 * @throws ConcordanceClosedException
	 *             if the concordance from which the context is requested is not open
	 * @since JSemcor 1.0.0
	 */
	public IContext getContext(String contextName, String concordanceName);

	/**
	 * Returns the specified context, if this concordance set is open and the
	 * specified context can be found.  Otherwise, it returns <code>null</code>.
	 * 
	 * @return the identified context, if it can be found; <code>null</code>
	 *         otherwise.
	 * @throws ConcordanceClosedException
	 *             if the concordance from which the context is requested is not open
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public IContext getContext(IContextID id);
	
	/**
	 * Returns the number of contexts contained in this concordance set.
	 * 
	 * @return the number of contexts contained in this concordance set.
	 * @since JSemcor 1.1.0
	 */
	public int numContexts();
	
	/**
	 * Returns an iterator over context objects contained in the concordances of
	 * this set.
	 * 
	 * @return an iterator that returns {@link IContext} objects available in
	 *         this concordance.
	 * @throws ConcordanceClosedException
	 *             if some concordance in this set is not open
	 * @since JSemcor 1.0.0
	 * @see Iterable#iterator();
	 */
	public Iterator<IContext> iterator();

}

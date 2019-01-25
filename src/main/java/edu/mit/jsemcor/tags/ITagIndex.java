/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

/**
 * A tag index is a iterable list of tag lists. Tag lists can be retrieved by
 * the appropriate sense key, or iterated over in order.
 * 
 * @author M.A. Finlayson
 * @version 1.52, 19 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ITagIndex extends Iterable<ITagList> {

	/**
	 * Returns the name of the concordance to which this tag index applies. All
	 * the {@link ISenseLocation} objects derived from this tag index will share
	 * this concordance name.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getConcordanceName();

	/**
	 * Returns the list of sense locations for the specified sense key, or
	 * <code>null</code> if this tag index contains no sense locations for the
	 * specified sense key.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public ITagList getTagList(String senseKey);

}

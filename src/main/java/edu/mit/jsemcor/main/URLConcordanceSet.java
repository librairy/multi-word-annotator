/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.main;

import java.net.URL;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import edu.mit.jsemcor.element.ContextID;
import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.IContextID;

/**
 * Abstract super class of concordance sets that retrieve their data through a
 * {@link URL} objects. Subclasses must know how to retrieve the set of
 * {@link URLConcordance} objects, given a URL pointing to the root of the
 * concordance set.
 *
 * @author M.A. Finlayson
 * @version 1.637, 20 Jun 2011
 * @since JSemcor 1.0.0
 */
public abstract class URLConcordanceSet extends AbstractMap<String, IConcordance> implements IConcordanceSet {
	
	private URL url;
	
	/**
	 * Whether caching was set as passed to the constructor, or as most recently
	 * set using {@link #setCaching(boolean)}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected boolean isCaching;
	
	/**
	 * The cache limit as passed to the constructor, or as most recently
	 * set using {@link #setCacheLimit(int)}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected int cacheLimit;
	
	/**
	 * This is the actual modifiable map that holds the list of concordance
	 * names mapped to the actual concordance objects.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected Map<String, IConcordance> backingMap;
	
	/**
	 * This map is used to give an entry set to clients.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected Map<String, IConcordance> unmodifiableMap;

	/**
	 * Creates a URL concordance set backed by file data stored at the specified
	 * URL. Caching is turned on by default, with the default cache limit.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public URLConcordanceSet(URL url){
		this(url, true);
	}
	
	/**
	 * Creates a URL concordance set backed by file data stored at the specified
	 * URL, with caching turned either on or off according to the flag. The
	 * default cache limit is used.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public URLConcordanceSet(URL url, boolean cache){
		this(url, cache, URLConcordance.DEFAULT_CACHE_LIMIT);
	}
	
	/**
	 * Creates a URL concordance set backed by file data stored at the specified
	 * URL, with caching turned either on or off according to the flag, and the
	 * cache limit set as specified.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public URLConcordanceSet(URL url, boolean cache, int cacheLimit){
		if(url == null) throw new NullPointerException();
		this.url = url;
		this.backingMap = new LinkedHashMap<String, IConcordance>();
		this.unmodifiableMap = Collections.unmodifiableMap(backingMap);
		setCaching(isCaching);
		setCacheLimit(cacheLimit);
	}
	
	/**
	 * Returns the URL pointing to the root of this concordance set. In Semcor,
	 * this is the folder that contains the three concordances 'brown1',
	 * 'brown2', and 'brownv'
	 * 
	 * @since JSemcor 1.0.0
	 */
	public URL getURL(){
		return url;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordanceSet#open()
	 */
	public boolean open() {
		if(isOpen()) return true;
		
		Set<URLConcordance> cs = getConcordances();
		if(cs == null || cs.isEmpty()) return false;
		
		for(URLConcordance c : cs){
			backingMap.put(c.getName(), c);
			if(!c.open()){
				close();
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Subclasses must implement this method so that the concordance set can
	 * obtain the {@link URLConcordance} objects from the root URL.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected abstract Set<URLConcordance> getConcordances();

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordanceSet#isOpen()
	 */
	public boolean isOpen() {
		if(backingMap.isEmpty()) return false;
		for(IConcordance c : backingMap.values()) if(!c.isOpen()) return false;
		return true;
	}
	
	/**
	 * If the all concordances in the set are not open, calling this method will result in a
	 * {@link ConcordanceClosedException}.
	 * 
	 * @throws ConcordanceClosedException
	 *             if any concordance in the set is closed
	 * @since JSemcor 1.0.0
	 */
	protected void checkOpen(){
		if(!isOpen()) throw new ConcordanceClosedException();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordanceSet#close()
	 */
	public void close() {
		for(IConcordance c : values()) c.close();
		backingMap.clear();
	}
	
	/**
	 * Turns caching on or off for all concordances in this set.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public boolean setCaching(boolean value){
		if(isCaching == value) return false;
		isCaching = value;
		for(IConcordance c : values()) ((URLConcordance)c).setCaching(value);
		return true;
	}

	/**
	 * Sets the cache limit for all concordances in this set.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public boolean setCacheLimit(int limit) {
		if(cacheLimit == limit) return false;
		cacheLimit = Math.max(limit, 1);
		for(IConcordance c : values()) ((URLConcordance)c).setCacheLimit(cacheLimit);
		return true;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordanceSet#numContexts()
	 */
	public int numContexts() {
		int size = 0;
		for(IConcordance c : backingMap.values())
			size += c.getContextIDs().size();
		return size;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractMap#entrySet()
	 */
	@Override
	public Set<Entry<String, IConcordance>> entrySet() {
		return unmodifiableMap.entrySet();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordanceSet#getContext(java.lang.String, java.lang.String)
	 */
	public IContext getContext(String contextName, String concordanceName) {
		return getContext(new ContextID(contextName, concordanceName));
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordanceSet#getContext(edu.mit.jsemcor.element.IContextID)
	 */
	public IContext getContext(IContextID id) {
		IConcordance c = get(id.getConcordanceName());
		return (c == null) ? null : c.getContext(id);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.main.IConcordanceSet#iterator()
	 */
	public Iterator<IContext> iterator() {
		checkOpen();
		return new ContextIterator();
	}
	
	/**
	 * An iterator over contexts in concordances in the concordance set.
	 * This is a look-ahead iterator.
	 *
	 * @author M.A. Finlayson
	 * @version $Rev: 637 $, $LastChangedDate: 2011-06-20 23:17:37 -0700 (Mon, 20 Jun 2011) $
	 * @since JSemcor 1.0.0
	 */
	protected class ContextIterator implements Iterator<IContext> {
		
		final Iterator<IConcordance> setItr;
		Iterator<IContext> conItr;
		IContext next;
		
		public ContextIterator(){
			setItr = backingMap.values().iterator();
			loadNext();
		}
		
		/**
		 * Loads the next context into the iterator.
		 *
		 * @since JSemcor 1.0.0
		 */
		protected void loadNext(){
			next = null;
			
			// if we don't have a context iterator, or the one we have is exhausted,
			// get a new context iterator
			if(conItr == null || !conItr.hasNext()){
				while(setItr.hasNext()){
					conItr = setItr.next().iterator();
					if(conItr.hasNext()) break;
				}
			}
			if(conItr != null && conItr.hasNext()) next = conItr.next();
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
		public IContext next() {
			if(next == null) throw new NoSuchElementException();
			IContext result = next;
			loadNext();
			return result;
		}

		/**
		 * This iterator does not support this operation
		 * 
		 * @throws UnsupportedOperationException
		 * 
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}


}

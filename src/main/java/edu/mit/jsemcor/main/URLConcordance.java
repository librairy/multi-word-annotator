/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.mit.jsemcor.data.ContextParser;
import edu.mit.jsemcor.data.IContextParser;
import edu.mit.jsemcor.element.ContextID;
import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.tags.ITagIndex;

/**
 * This abstract implementation of the {@link IConcordance} interface is
 * appropriate for all concordances that retrieve their data from URLs. This
 * implementation has basic facilities for a size-limited LRU cache. Caching,
 * the cache size limit, can be set with the constructors, or the appropriate
 * methods.
 * 
 * @author M.A. Finlayson
 * @version 1.156, 16 Dec 2008
 * @since JSemcor 1.0.0
 */
public abstract class URLConcordance implements IConcordance {
	
	/**
	 * The default cache size
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final int DEFAULT_CACHE_LIMIT = 20;
	
	/**
	 * The context parser used by this concordance to translate context data
	 * into {@link IContext} objects.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected IContextParser parser;
	
	/**
	 * The map that contains {@link IContextID} objects mapped to their
	 * {@link URL} locations.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected final Map<IContextID, URL> contextMap = new LinkedHashMap<IContextID, URL>();
	
	/**
	 * The cache where context objects are stored.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected Map<IContextID, IContext> cache;
	
	private final URL url;
	private final String name;
	private ITagIndex tagIndex;
	private boolean isCaching;
	private int cacheLimit;
	
	/**
	 * Constructs a new concordance whose base directory is the specified URL.
	 * Caching is by default on.
	 * 
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * 
	 * @since JSemcor 1.0.0
	 */
	public URLConcordance(URL url){
		this(url, true);
	}
	
	/**
	 * Constructs a new concordance whose base directory is the specified URL,
	 * with caching turned on or off according to the flag.
	 * 
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public URLConcordance(URL url, boolean cache){
		this(url, cache, DEFAULT_CACHE_LIMIT);
	}
	
	/**
	 * Constructs a new concordance whose base directory is the specified URL,
	 * with caching turned on or off according to the flag, and the specified
	 * cache limit.
	 * 
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public URLConcordance(URL url, boolean cache, int cacheLimit){
		if(url == null) throw new NullPointerException();
		this.url = url;
		this.name = extractConcordanceName(url);
		setCaching(cache);
		setCacheLimit(cacheLimit);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordance#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the URL that points to the root directory of this concordance.
	 * 
	 * @return the URL that points to the base directory of this concordance.
	 * @since JSemcor 1.0.0
	 */
	public URL getURL(){
		return url;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextProvider#open()
	 */
	public boolean open() {
		if(isOpen()) return true;
		try{
			
			// fill context map
			if(!fillContextURLMap(contextMap)){
				contextMap.clear();
				return false;
			} 
			if(contextMap.isEmpty()) return false;
			
			// if we get here, we're good to go
			// make this an LRU cache
			int cacheSize = Math.max(cacheLimit, 0);
			cacheSize = Math.min(cacheSize, 512);
			cache = new LinkedHashMap<IContextID, IContext>(cacheSize, 0.75f, true);
			
			// get the tag index
			// don't care if this succeeds or not
			tagIndex = createTagIndex();
			
		} catch(IOException e){
			close();
			throw new RuntimeException(e);
		}
		
		return isOpen();
	}
	
	/**
	 * Returns the concordance name given the concordance {@link URL}. This
	 * method is called in the {@link URLConcordance} constructor to extract the
	 * concordance's name from the specified URL.
	 * 
	 * @return the concordance name for the specified {@link URL}
	 * 
	 * @throws NullPointerException
	 *             if the specified {@link URL} is <code>null</code>
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected abstract String extractConcordanceName(URL url);
	
	/**
	 * This method is called during {@link #open()} to construct a map from
	 * context id objects to the URLs from which the context contents can be
	 * downloaded.
	 * 
	 * @return <code>true</code> if the operation completed successfully;
	 *         <code>false</code> otherwise.
	 * 
	 * @throws IOException
	 *             if there is a problem extracting the list of context ids from
	 *             the root {@link URL} location.
	 * @since JSemcor 1.0.0
	 */
	protected abstract boolean fillContextURLMap(Map<IContextID, URL> map) throws IOException;
	
	/**
	 * This method is called during {@link #open()} to construct the tag index
	 * object. If this concordance does not contain the necessary data to create
	 * the tag index, this method will return <code>null</code>.
	 * 
	 * @return The {@link ITagIndex} for this concordance, or <code>null</code>
	 *         if no tag index exists.
	 * @throws IOException
	 *             if there is an IO error when creating the tag index.
	 * @since JSemcor 1.0.0
	 */
	protected abstract ITagIndex createTagIndex() throws IOException;
	
	/**
	 * Returns the parser that should be used to parse raw context contents into
	 * an {@link IContext} object. This method returns the singleton instance of
	 * the parser that this concordance uses. If subclasses which to change the
	 * parser, they should override the {@link #makeParser()} method.
	 * 
	 * @return the context parser for this concordance.
	 * @since JSemcor 1.0.0
	 */
	protected IContextParser getParser(){
		if(parser == null) parser = makeParser();
		return parser;
	}
	
	/**
	 * Returns a new instance of the parser that should be used to parse raw
	 * context contents into an {@link IContext} object. Usually this method is
	 * only ever called once, to create the parser when it is first requested.
	 * The {@link URLConcordance} implementation then caches that instance and
	 * returns on future requests for a parser.
	 * 
	 * @return a new instance of the context parser that should be used to parse
	 *         context data into {@link IContext} objects.
	 * @since JSemcor 1.0.0
	 */
	protected IContextParser makeParser(){
		return ContextParser.getInstance();
	}
	
	/**
	 * If the concordance is not open, calling this method will result in a
	 * {@link ConcordanceClosedException}; if the provider is open, nothing
	 * will happen.
	 * 
	 * @throws ConcordanceClosedException
	 *             if the concordance is closed
	 * @since JSemcor 1.0.0
	 */
	protected void checkOpen(){
		if(!isOpen()) throw new ConcordanceClosedException();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextProvider#isOpen()
	 */
	public boolean isOpen(){
		return !contextMap.isEmpty();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextProvider#close()
	 */
	public void close() {
		contextMap.clear();
		tagIndex = null;
		cache = null;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextProvider#getSources()
	 */
	public Set<IContextID> getContextIDs() {
		checkOpen();
		return contextMap.keySet();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextProvider#getTagIndex()
	 */
	public ITagIndex getTagIndex() {
		checkOpen();
		return tagIndex;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.IConcordance#getContext(java.lang.String)
	 */
	public IContext getContext(String contextName) {
		return getContext(new ContextID(contextName, name));
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextProvider#getContext(edu.mit.jsemcor.element.IContextID)
	 */
	public IContext getContext(IContextID id) {
		checkOpen();
		
		IContext result = cache.get(id);
		if(result == null){
			// return null if we don't know how to find this id
			URL url = contextMap.get(id);
			if(url == null) return null;
			
			try{
				Reader reader = new InputStreamReader(BufferToStreamAdapter.makeStream(url));
				reader = new BufferedReader(reader);
				result = getParser().parse(id, reader);
			} catch(IOException e){
				throw new RuntimeException(e);
			}
			if(isCaching && result != null){
				cache.put(id, result);
				pruneCache();
			}
		}
		return result;
	}
	
	/* (non-Javadoc) @see edu.mit.jsemcor.main.IConcordance#iterator() */
	public Iterator<IContext> iterator() {
		checkOpen();
		return new ContextIterator();
	}
	
	/**
	 * Returns whether this concordance is currently caching the results of
	 * calls to {@link #getContext(IContextID)}.
	 */
	public boolean isCaching() {
		return isCaching;
	}
	
	/**
	 * Turns caching on or off for this concordance. Turning caching off with
	 * the method does not clear that cache, it merely prevents any future
	 * contexts retrieved from being cached. If a context object is already in
	 * the cache, that context will be returned when it's id is requested from
	 * the {@link #getContext(IContextID)} method. To clear the cache, obtain
	 * the cache map instance via {@link #getCache()} method and call
	 * {@link Map#clear()}.
	 */
	public boolean setCaching(boolean value){
		if(isCaching == value) return false;
		isCaching = value;
		return true;
	}

	/**
	 * Sets the cache limit on this concordance. Returns a flag indicating this
	 * call actually changed the cache limit. The method does not allow the
	 * cache limit to be set less than 1; if a number less than 1 is specified,
	 * the cache limit is set to 1. If the cache size is greater than the new
	 * cache limit, the cache is pruned down to size. To turn off caching, use
	 * the {@link #setCaching(boolean)} method.
	 * 
	 * @return <code>true</code> if the cache limit was changed;
	 *         <code>false</code> otherwise
	 */
	public boolean setCacheLimit(int limit) {
		if(cacheLimit == limit) return false;
		cacheLimit = Math.max(limit, 1);
		pruneCache();
		return true;
	}
	
	/**
	 * Returns the current cache limit set on this concordance set
	 * 
	 * @return the current cache limit.
	 */
	public int getCacheLimit(){
		return cacheLimit;
	}
	
	/**
	 * Returns the actual cache map object, so that it can be directly
	 * manipulated by clients.
	 * 
	 * @return the cache in use by the concordance.
	 */
	public Map<IContextID, IContext> getCache(){
		return cache;
	}
	
	/**
	 * Removes items from the cache so that the cache size is below the cache
	 * limit. If the cache size is already less than the cache limit, this
	 * method does nothing.
	 */
	protected void pruneCache(){
		
		// cache has not initialized
		if(cache == null) return;
		
		// see how many items need to be removed
		int removeCount = cache.size()-cacheLimit;
		
		// if less than a single item needs to be removed, don't do anything
		if(removeCount < 1) return;
		
		// remove the appropriate number of items;
		Iterator<IContextID> itr = cache.keySet().iterator();
		for(int i = 0; i < removeCount && itr.hasNext(); i++){
			itr.next();
			itr.remove();
		}
	}
	
	/**
	 * Delivers context objects in order from this concordance.
	 *
	 * @author M.A. Finlayson
	 * @version $Rev: 68 $, $LastChangedDate: 2008-12-16 14:56:38 -0500 (Tue, 16 Dec 2008) $
	 * @since JSemcor 1.0.0
	 */
	protected class ContextIterator implements Iterator<IContext> {
		
		final Iterator<IContextID> idItr;
		
		/**
		 * Constructs a new iterator over context objects in this concordance.
		 *
		 * @since JSemcor 1.0.0
		 */
		public ContextIterator(){
			idItr = contextMap.keySet().iterator();
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return idItr.hasNext();
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.util.Iterator#next()
		 */
		public IContext next() {
			return getContext(idItr.next());
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
	
}

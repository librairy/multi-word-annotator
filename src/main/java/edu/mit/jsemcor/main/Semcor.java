/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.main;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of the {@link IConcordanceSet} interface. Use this
 * class for default behavior when using Semcor files stored on the local file
 * system.
 * 
 * @author M.A. Finlayson
 * @version 1.502, 29 Jan 2011
 * @since JSemcor 1.0.0
 */
public class Semcor extends URLConcordanceSet {
	
	/**
	 * Creates a semcor concordance set backed by file data stored at the
	 * specified {@link URL}.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public Semcor(File file) {
		this(BufferToStreamAdapter.toURL(file));
	}
	
	/**
	 * Creates a semcor concordance set backed by file data stored at the
	 * specified {@link URL}, with caching turned either on or off according to the
	 * flag.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public Semcor(File file, boolean cache) {
		this(BufferToStreamAdapter.toURL(file), cache);
	}
	
	/**
	 * Creates a semcor concordance set backed by file data stored at the
	 * specified {@link URL}, with caching turned either on or off according to
	 * the flag, and the cache limit set as specified.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public Semcor(File file, boolean cache, int cacheLimit) {
		this(BufferToStreamAdapter.toURL(file), cache, cacheLimit);
	}
	
	/**
	 * Creates a semcor concordance set backed by file data stored at the
	 * specified {@link URL}.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public Semcor(URL url) {
		this(url, true);
	}
	
	/**
	 * Creates a semcor concordance set backed by file data stored at the
	 * specified {@link URL}, with caching turned either on or off according to the
	 * flag.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public Semcor(URL url, boolean cache) {
		this(url, cache, URLConcordance.DEFAULT_CACHE_LIMIT);
	}
	
	/**
	 * Creates a semcor concordance set backed by file data stored at the
	 * specified {@link URL}, with caching turned either on or off according to
	 * the flag, and the cache limit set as specified.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified URL does not point a file resource.
	 * @since JSemcor 1.0.0
	 */
	public Semcor(URL url, boolean cache, int cacheLimit) {
		super(url, cache, cacheLimit);
		File baseDir = BufferToStreamAdapter.toFile(url);
		if(baseDir == null || !baseDir.isDirectory()) throw new IllegalArgumentException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.main.URLConcordanceSet#getConcordances()
	 */
	protected Set<URLConcordance> getConcordances() {
		
		File baseDir = BufferToStreamAdapter.toFile(getURL());
		if(!baseDir.isDirectory()) return null;
		
		Set<URLConcordance> result = new LinkedHashSet<URLConcordance>();
		
		try{
			// look at all directories and only add those to the map
			// that contain 'tagfiles' sub-directories
			File tagfileDir;
			for(File child : baseDir.listFiles(new FileFilter(){
				public boolean accept(File file) {
					return file.isDirectory();
				}
			})){
				tagfileDir = new File(child, FileConcordance.DIR_NAME_TAGFILES);
				if(tagfileDir.exists()) result.add(new FileConcordance(child.toURL(), isCaching, cacheLimit));
	
			}
		} catch(MalformedURLException e){
			return null;
		}
		
		return result;
	}

}

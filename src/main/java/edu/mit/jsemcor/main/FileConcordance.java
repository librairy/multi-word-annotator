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
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

import edu.mit.jsemcor.element.ContextID;
import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.tags.FileBasedTagIndex;
import edu.mit.jsemcor.tags.ITagIndex;

/**
 * The default concrete implementation of the {@link IConcordance} interface
 * that uses directories and files on the local filesystem to back the
 * concordances and contexts.
 * 
 * @author M.A. Finlayson
 * @version 1.60, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class FileConcordance extends URLConcordance {
	
	/**
	 * The default name of the tag file directory that contains the context data
	 * files.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String DIR_NAME_TAGFILES = "tagfiles";
	
	/**
	 * The default name of the tag index file that contains the tag index data.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String FILE_NAME_TAGLIST = "taglist";
	
	/**
	 * Constructs a new concordance whose root directory is the specified
	 * {@link File}. Caching is by default on.
	 * 
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified {@link URL} does not point a local
	 *             {@link File} resource, or the specified resource is a
	 *             directory.
	 * @since JSemcor 1.0.0
	 */
	public FileConcordance(URL url){
		this(url, true);
	}
	
	/**
	 * Constructs a new concordance whose base directory is the specified
	 * {@link URL}, with caching turned on or off according to the flag.
	 * 
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified {@link URL} does not point a local
	 *             {@link File} resource, or the specified resource is a
	 *             directory.
	 * @since JSemcor 1.0.0
	 */
	public FileConcordance(URL url, boolean cache) {
		this(url, cache, DEFAULT_CACHE_LIMIT);
	}
	
	/**
	 * Constructs a new concordance whose base directory is the specified URL,
	 * with caching turned on or off according to the flag, and the specified
	 * cache limit.
	 * 
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified {@link URL} does not point a local
	 *             {@link File} resource, or the specified resource is a
	 *             directory.
	 * @since JSemcor 1.0.0
	 */
	public FileConcordance(URL url, boolean cache, int cacheLimit) {
		super(url, cache, cacheLimit);
		File baseDir = BufferToStreamAdapter.toFile(url);
		if(baseDir == null || !baseDir.isDirectory()) throw new IllegalArgumentException();
	}
	
	/**
	 * Returns the name of the directory inside the concordance root in which
	 * tag files are stored. This method is included in the API for ease of
	 * extending this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getTagfileDirName(){
		return DIR_NAME_TAGFILES;
	}
	
	/**
	 * Returns the name of the file inside the concordance root which contains
	 * the tag index data. This method is included in the API for ease of
	 * extending this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getTaglistFileName(){
		return FILE_NAME_TAGLIST;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.URLConcordance#extractConcordanceName(java.net.URL)
	 */
	@Override
	protected String extractConcordanceName(URL url) {
		File baseDir = BufferToStreamAdapter.toFile(url);
		return baseDir.getName();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.main.URLConcordance#fillContextURLMap(java.net.URL, java.util.Map)
	 */
	@Override
	protected boolean fillContextURLMap(Map<IContextID, URL> map) throws IOException {
		
		// we should have been passed a url to the concordance directory 
		File baseDir = BufferToStreamAdapter.toFile(getURL());
		
		// get taglist directory
		File taglistDir = new File(baseDir, getTagfileDirName());
		if(!taglistDir.exists()) return false;
		
		// now get concordance files
		FileFilter filter = getContextFileFilter();
		for(File contextFile : taglistDir.listFiles(filter)){
			map.put(new ContextID(contextFile.getName(), getName()), contextFile.toURL());
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.main.URLConcordance#createTagIndex()
	 */
	@Override
	protected ITagIndex createTagIndex() throws IOException {
		
		// we should have been passed a url to the concordance directory 
		File baseDir = BufferToStreamAdapter.toFile(getURL());
		if(!baseDir.isDirectory()) return null;
		
		// now create the tag index file object
		File tagIndexFile = new File(baseDir, getTaglistFileName());
		return tagIndexFile.exists() ? new FileBasedTagIndex(getName(), tagIndexFile) : null;
	}
	
	/**
	 * Returns an instance of a file filter that, when applied to a taglist
	 * directory in a concordance, returns only context files. This method is
	 * included in the API for ease of extending this class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected FileFilter getContextFileFilter(){
		return new SemcorContextFileFilter();
	}
	
	/**
	 * A context file filter that only returns files named in the standard
	 * semcor naming pattern.
	 * 
	 * @author M.A. Finlayson
	 * @since JSemcor 1.0.0
	 */
	protected class SemcorContextFileFilter implements FileFilter {

		Pattern pattern = Pattern.compile("br-[a-z]\\d\\d");

		/* 
		 * (non-Javadoc)
		 *
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File file) {
			return pattern.matcher(file.getName()).matches();
		}
		
	}

}

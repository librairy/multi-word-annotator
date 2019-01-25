/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import edu.mit.jmwe.data.IMWEDescID;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.util.StreamAdapter;

/**
 * Simple implementation of {@link IMWEIndex} that reads an index, possibly with internal comments
 * prefixed by '//' or ';;', from a URL.
 *
 * @author M.A. Finlayson
 * @version $Id: MWEIndex.java 323 2011-05-07 01:00:47Z markaf $
 * @since jMWE 1.0.0
 */
public class MWEIndex extends InMemoryMWEIndex {
	
	// final instance field
	private final URL url;
	private final Iterable<String> origData;
	private final Map<IMWEDescID, ? extends IRootMWEDesc> origMap;

	/**
	 * Constructs this index from an array of multi-word expression strings. The
	 * parts and part of speech of each multi-word expression should be
	 * separated by underscores and each multi-word expression, if there is more
	 * than one, should be separated by a space character. If the String is
	 * formatted incorrectly, the index will not open.
	 * 
	 * @param rootDescs
	 *            the array of multi-word expression strings this index will be
	 *            constructed from.
	 * @since jMWE 1.0.0
	 */
	public MWEIndex(String... rootDescs){
		this(Arrays.asList(rootDescs));
	}

	/**
	 * Constructs the index from a String of multi-word expressions. The parts
	 * and part of speech of each multi-word expression should be separated by
	 * underscores and each multi-word expression, if there is more than one,
	 * should be separated by a space character. If the string is formatted
	 * incorrectly, the index will not open.
	 * 
	 * For example:
	 * 
	 * <pre>
	 * this_and_that_R
	 * </pre>
	 * 
	 * @param origData
	 *            iterable of multi-word expression definitions. The parts and
	 *            part of speech of each multi-word expression should be
	 *            separated by underscores and each multi-word expression, if
	 *            there is more than one, should be separated by a space
	 *            character.
	 * @since jMWE 1.0.0
	 */
	public MWEIndex(Iterable<String> origData){
		if(origData == null)
			throw new NullPointerException();
		this.origData = origData;
		this.origMap = null;
		this.url = null;
	}

	/**
	 * Constructs the index from a map of IMWEDescIDs to IRootMWEDescs.
	 * 
	 * @param rootDescs
	 *            a map of IMWEDescIDs to IRootMWEDescs. May not be
	 *            <code>/null</code>.
	 * @since jMWE 1.0.0
	 */
	public MWEIndex(Map<IMWEDescID, ? extends IRootMWEDesc> rootDescs){
		if(rootDescs == null)
			throw new NullPointerException();
		this.origMap = rootDescs;
		this.origData = null;
		this.url = null;
	}

	/**
	 * Constructs the index from the given source file pointing to a list of
	 * multi-word expressions.
	 * 
	 * @param file
	 *            the list of multi-word expressions. May not be
	 *            <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public MWEIndex(File file) {
		this(StreamAdapter.toURL(file));
	}

	/**
	 * Constructs a new MWE index that uses the data from the specified URL to
	 * construct its index.
	 * 
	 * @param url
	 *            the URL at which the index data is found
	 * @throws NullPointerException
	 *             if the specified URL is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public MWEIndex(URL url) {
		if(url == null)
			throw new NullPointerException();
		this.origMap = null;
		this.origData = null;
		this.url = url;
	}

	/**
	 * Returns the URL used by this index.
	 * 
	 * @return the URL, non-<code>null</code>, used by this index
	 * @since jMWE 1.0.0
	 */
	public URL getSource(){
		return url;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.InMemoryMWEIndex#createData()
	 */
	@Override
	protected Map<IMWEDescID, ? extends IRootMWEDesc> createData() throws IOException {
		if(origMap != null)
			return origMap;
		if(origData != null)
			return createData(origData);
		
		InputStream is = url.openStream();
		InputStreamReader reader = new InputStreamReader(is);
		Map<IMWEDescID, IRootMWEDesc> result = createData(reader);
		reader.close();
		is.close();
		return result;
	}

}

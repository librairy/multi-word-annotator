/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.mit.jmwe.data.IInfMWEDesc;
import edu.mit.jmwe.data.IInfMWEDesc.IInfPart;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IMWEDescID;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IRootMWEDesc.IRootPart;
import edu.mit.jmwe.data.MWEDescID;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.data.RootMWEDesc;

/**
 * The default abstract implementation of {@link IMWEIndex}. Loads the entire index into
 * memory.  Subclasses must implement the {@link #createData()} method to actually create
 * the index data object.
 * 
 * @author Nidhi Kulkarni
 * @author M.A. Finlayson
 * @version 1.552, 05 May 2011
 * @since jMWE 1.0.0
 */
public abstract class InMemoryMWEIndex implements IMWEIndex {
	
	// final instance fields
	protected final Lock lifecycleLock = new ReentrantLock();
	
	// data
	private Map<IMWEDescID, ? extends IRootMWEDesc> data;
	private Map<String, ? extends IMWEDescSet> index;
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#open()
	 */
	public boolean open() throws IOException {
		try {
			lifecycleLock.lock();
			if(isOpen())
				return true;
			
			// create data and index
			Map<IMWEDescID, ? extends IRootMWEDesc> dat = createData();
			if(dat.isEmpty())
				return false;
			Map<String, ? extends IMWEDescSet> idx = createIndex(dat);
			
			// assign fields if all is well
			this.data = dat;
			this.index = idx;
			return true;
		} finally {
			lifecycleLock.unlock();
		}
	}
	
	protected abstract Map<IMWEDescID, ? extends IRootMWEDesc> createData() throws IOException;
	
	/**
	 * Uses the given map of description IDs to root descriptions in order to
	 * create the index. Makes all the entries in the index unmodifiable.
	 * 
	 * @param data
	 *            the map of IMWEDescID objects to IRootMWEDesc objects used to
	 *            create the index
	 * @return an unmodifiable view of the index that maps parts of MWEs to a
	 *         set containing all the MWEs they are a part of.
	 * @since jMWE 1.0.0
	 */
	protected Map<String, ? extends IMWEDescSet> createIndex(Map<IMWEDescID, ? extends IRootMWEDesc> data){
		
		// the default mode is to create the index from the data
		Map<String, IMWEDescSet> index = new TreeMap<String, IMWEDescSet>();
		for(Entry<IMWEDescID, ? extends IRootMWEDesc> e : data.entrySet()){
			for(IRootPart rootPart : e.getValue().getParts())
				insert(rootPart.getForm(), e.getValue(), index);
			for(IInfMWEDesc infDesc : e.getValue().getInflected().values())
				for(IInfPart infPart : infDesc.getParts())
					insert(infPart.getForm(), infDesc, index);
		}
		
		// make everything unmodifiable
		for(Entry<String, IMWEDescSet> e : index.entrySet())
			e.getValue().makeUnmodifiable();
		
		return Collections.unmodifiableMap(index);
	}
	
	/**
	 * Adds the specified object to the set indexed under the specified key in
	 * the given map.  If no such set exists, the method creates one.
	 * 
	 * @param key
	 *            the key under which the object is to be filed; may be
	 *            <code>null</code>
	 * @param object
	 *            the object to be filed; may be <code>null</code>
	 * @param map
	 *            the map into which the object should be filed; may not be
	 *            <code>null</code>
	 * @throws NullPointerException
	 *             if the specified map is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected final void insert(String key, IMWEDesc object, Map<String, IMWEDescSet> map){
		IMWEDescSet set = map.get(key);
		if(set == null){
			set = new MWEDescSet();
			map.put(key, set);
		}
		set.add(object);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#isOpen()
	 */
	public boolean isOpen() {
		try {
			lifecycleLock.lock();
			return data != null;
		} finally {
			lifecycleLock.unlock();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#close()
	 */
	public void close(){
		try {
			lifecycleLock.lock();
			if(isOpen()){
				data = null;
				index = null;
			}
		} finally {
			lifecycleLock.unlock();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#getMWEDesc(edu.mit.jmwe.data.IMWEDescID)
	 */
	public IMWEDesc get(IMWEDescID id) {
		IMWEDescID rootID = id.getRootID();
		IRootMWEDesc rootDesc = data.get(rootID);
		if(rootDesc == null || rootID == id)
			return rootDesc;
		return rootDesc.getInflected().get(id.getInflectedForm());
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#getRootMWEDesc(java.lang.String, edu.mit.jmwe.data.MWEPOS)
	 */
	public IRootMWEDesc getRootMWEDesc(String lemma, MWEPOS pos) {
		return data.get(new MWEDescID(lemma, pos));
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#getInflectedMWEDesc(java.lang.String, edu.mit.jmwe.data.MWEPOS, java.lang.String)
	 */
	public IInfMWEDesc getInflectedMWEDesc(String lemma, MWEPOS pos, String inflected) {
		IRootMWEDesc root = getRootMWEDesc(lemma, pos);
		if(root == null)
			return null;
		return root.getInflected().get(inflected.trim().toLowerCase());
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#get(java.lang.String)
	 */
	public Set<? extends IRootMWEDesc> get(String part) {
		IMWEDescSet all = getAll(part);
		return all.getRootMWEDescs();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#getAll(java.lang.String)
	 */
	public IMWEDescSet getAll(String part) {
		try {
			lifecycleLock.lock();
			checkOpen();
			if(part.indexOf('_') > -1) 
				throw new IllegalArgumentException();
			IMWEDescSet entries = index.get(part.toLowerCase());
			return entries == null ? 
					MWEDescSet.emptySet() : 
						entries;
		} finally {
			lifecycleLock.unlock();
		}
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#getRootIterator()
	 */
	@SuppressWarnings("unchecked")
	public Iterator<IRootMWEDesc> getRootIterator() {
		return (Iterator<IRootMWEDesc>)data.values().iterator();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEIndex#getIndexIterator()
	 */
	public Iterator<String> getIndexIterator() {
		return index.keySet().iterator();
	}

	/**
	 * Throws an {@link IllegalStateException} if the index is closed.
	 *
	 * @since jMWE 1.0.0
	 */
	protected void checkOpen(){
		if(!isOpen())
			throw new IllegalStateException("index is closed");
	}
	
	/**
	 * Returns true if the given string is a comment; that is, if it starts with 
	 * '//' or ';;'.
	 * @param descStr the string to be checked
	 * @return true if the string begins with '//' or ';;', false otherwise.
	 * @since jMWE 1.0.0
	 */
	public static boolean isComment(String descStr) {
		if(descStr.startsWith(commentDoubleSlash))
			return true;
		if(descStr.startsWith(commentDoubleSemicolon))
			return true;
		return false;
	}

	/**
	 * Utility method that assembles a MWE index data map from a list of
	 * multi-word expression string descriptions. Utility method that assembles
	 * a MWE index data map from a stream of characters that contains a
	 * whitespace-delimited list of multi-word expressions. The stream may
	 * contain single line comments, begun by ';;' or '//', and each
	 * whitespace-delimited entry must be in the form
	 * <code>tok1_tok2_..._tokN_POS</code>, where each <code>tok#</code> is one
	 * part of the multiword expression, and <code>POS</code> is the MWE part of
	 * speech, represented as the identifier character.
	 * 
	 * @param descStrs
	 *            the collection of mwe description strings to be processed into
	 *            the index data object
	 * @return the index data object
	 * @throws NullPointerException
	 *             if the specified iterable is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static Map<IMWEDescID, IRootMWEDesc> createData(Iterable<String> descStrs) {
		Map<IMWEDescID, IRootMWEDesc> result = new TreeMap<IMWEDescID, IRootMWEDesc>();
		IRootMWEDesc rootDesc;
		for(String descStr : descStrs){
			descStr = descStr.trim();
			if(descStr.length() == 0 || isComment(descStr))
				continue;
			rootDesc = RootMWEDesc.parse(descStr);
			result.put(rootDesc.getID(), rootDesc);
		}
		return Collections.unmodifiableMap(result);
	}

	/**
	 * Utility method that assembles a MWE index data map from a stream of
	 * characters that contains a whitespace-delimited list of multi-word
	 * expressions. The stream may contain single line comments, begun by ';;'
	 * or '//', and each whitespace-delimited entry must be in the form
	 * <code>tok1_tok2_..._tokN_POS</code>, where each <code>tok#</code> is one
	 * part of the multiword expression, and <code>POS</code> is the MWE part of
	 * speech, represented as the identifier character.
	 * 
	 * @param r
	 *            the reader from which to create the data, may not be
	 *            <code>null</code>
	 * @return a Map&lt;String, List&lt;IMWDesc&gt;&gt; that maps each part of a multi-word
	 *         expression to a list of multi-word expression descriptions that
	 *         it is a part of.
	 * @throws IOException
	 *             if there is an IOException thrown while reading from the
	 *             reader
	 * @throws NullPointerException
	 *             if the reader is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static Map<IMWEDescID, IRootMWEDesc> createData(Reader r) throws IOException {
		BufferedReader br = new BufferedReader(r);
		Map<IMWEDescID, IRootMWEDesc> result = new TreeMap<IMWEDescID, IRootMWEDesc>();
		IRootMWEDesc rootDesc;
		String line;
		while((line = br.readLine()) != null){
			line = line.trim();
			if(line.length() == 0 || isComment(line))
				continue;
			rootDesc = RootMWEDesc.parse(line);
			result.put(rootDesc.getID(), rootDesc);
		}
		return Collections.unmodifiableMap(result);
	}

}

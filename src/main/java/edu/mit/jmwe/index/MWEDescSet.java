/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import edu.mit.jmwe.data.IInfMWEDesc;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.util.CompositeCollection;

/**
 * Default implementation of the <code>IMWEDescSet</code> interface.
 *
 * @author M.A. Finlayson
 * @version $Id: MWEDescSet.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class MWEDescSet extends CompositeCollection<IMWEDesc> implements IMWEDescSet {
	
	private boolean isUnmodifiable = false;
	private Set<IRootMWEDesc> rootDescs;
	private Set<IInfMWEDesc> infDescs;
	
	/**
	 * Creates a new empty, mutable MWE desc set.
	 *
	 * @since jMWE 1.0.0
	 */
	public MWEDescSet(){
		this(new TreeSet<IRootMWEDesc>(), new TreeSet<IInfMWEDesc>());
	}

	/**
	 * Creates a new mutable MWE desc set that contains the specified elements
	 * 
	 * @param rootDescs
	 *            the root descs to be included in the set; may be
	 *            <code>null</code>
	 * @param infDescs
	 *            the inflected descs to be included in the set; may be
	 *            <code>null</code>
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public MWEDescSet(Set<IRootMWEDesc> rootDescs, Set<IInfMWEDesc> infDescs){
		super((rootDescs = replaceNull(rootDescs)), (infDescs = replaceNull(infDescs)));
		this.rootDescs = rootDescs;
		this.infDescs = infDescs;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEDescSet#getRootMWEDescs()
	 */
	public Set<IRootMWEDesc> getRootMWEDescs() {
		return rootDescs;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEDescSet#getInflectedMWEDescs()
	 */
	public Set<IInfMWEDesc> getInflectedMWEDescs() {
		return infDescs;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	@Override
	public boolean add(IMWEDesc o) {
		if(o == null)
			throw new NullPointerException();
		if(o instanceof IRootMWEDesc)
			return rootDescs.add((IRootMWEDesc)o);
		if(o instanceof IInfMWEDesc)
			return infDescs.add((IInfMWEDesc)o);
		throw new IllegalArgumentException(o.getClass().getCanonicalName() + " implements neither " + IRootMWEDesc.class.getCanonicalName() + " nor " + IInfMWEDesc.class.getCanonicalName());
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		if(o instanceof IRootMWEDesc)
			return rootDescs.remove((IRootMWEDesc)o);
		if(o instanceof IInfMWEDesc)
			return infDescs.remove((IInfMWEDesc)o);
		return false;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.AbstractCollection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean result = false;
		for(Object e : c)
			result |= remove(e);
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.index.IMWEDescSet#makeUnmodifiable()
	 */
	public void makeUnmodifiable() {
		if(isUnmodifiable)
			return;
		rootDescs = makeUnmodifiable(rootDescs);
		infDescs = makeUnmodifiable(infDescs);
		isUnmodifiable = true;
	}

	private static MWEDescSet emptySet;

	/**
	 * Returns an unmodifiable empty description set. If the static field is
	 * <code>null</code>, initializes it so that subsequent calls to this method
	 * return the static instance.
	 * 
	 * @return an unmodifiable empty description set.
	 * @since jMWE 1.0.0
	 */
	public static IMWEDescSet emptySet() {
		if(emptySet == null){
			emptySet = new MWEDescSet();
			emptySet.makeUnmodifiable();
		}
		return emptySet;
	}

	/**
	 * Returns an unmodifiable view of the given set.
	 * 
	 * @param set
	 *            the set to be made unmodifiable
	 * @param <E>
	 *            the types of mwe descs in the set
	 * @return an unmodifiable view of the given set.
	 * @since jMWE 1.0.0
	 */
	public static <E extends IMWEDesc> Set<E> makeUnmodifiable(Set<E> set) {
		if(set.isEmpty())
			return Collections.emptySet();
		return Collections.unmodifiableSet(new TreeSet<E>(set));
	}

	/**
	 * If the given set is <code>null</code>, returns the empty set. Otherwise,
	 * returns the set itself.
	 * 
	 * @param set
	 *            the set
	 * @param <E>
	 *            the types of mwe descs in the set
	 * @return If the given set is <code>null</code>, returns the empty set.
	 *         Otherwise, returns the set itself.
	 * @since jMWE 1.0.0
	 */
	public static <E extends IMWEDesc> Set<E> replaceNull(Set<E> set) {
		if(set == null)
			return Collections.emptySet();
		return set;
	}

}

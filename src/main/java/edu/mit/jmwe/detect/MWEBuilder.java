/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.mit.jmwe.data.AbstractMWEDesc;
import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IMWEDesc.IPart;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.data.RootMWEDesc;
import edu.mit.jmwe.util.ListComparator;

/**
 * A record that is used to hold tokens as the detector passes over a sentence.
 * Contains empty slots for each part of a multi-word expression. Each slot
 * can be filled by a token that matches the part.
 * 
 * @author M.A. Finlayson
 * @version 1.356, 25 Nov 2015
 * @since jMWE 1.0.0
 */
public class MWEBuilder<T extends IToken>{
	
	private final IMWEDesc entry;
	private final Comparator<T> comparator;
	
	private final SortedMap<IPart, T> slots;
	private final boolean hasMultiple;

	/**
	 * Constructs an empty record from the given MWE description and sentence.
	 * 
	 * @param entry
	 *            the description of the MWE associated with this record
	 * @param sentence
	 *            the sentence from which the tokens used to fill this record
	 *            will be drawn
	 * @since jMWE 1.0.0
	 */
	public MWEBuilder(IMWEDesc entry, List<T> sentence){
		this(entry, new ListComparator<T>(sentence));
	}
	
	/**
	 * Constructs an empty record from an {@link IRootMWEDesc} object.
	 * 
	 * @param entry
	 *            a multi-word expression description. May not be
	 *            <code>null</code>.
	 * @param comparator
	 *            the comparator to use, may not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the description is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public MWEBuilder(IMWEDesc entry, Comparator<T> comparator){
		if(entry == null)
			throw new NullPointerException();
		if(comparator == null)
			throw new NullPointerException();
		
		// populate the slot map and check for repeated parts
		SortedMap<IPart,T> slots = new TreeMap<IPart, T>();
		Set<String> uniqueParts = new HashSet<String>();
		for(IPart p : entry.getParts()){
			slots.put(p, null);
			uniqueParts.add(p.getForm().toLowerCase());
		}

		// field assignments
		this.hasMultiple = uniqueParts.size() < entry.getParts().size();
		this.entry = entry;
		this.comparator = comparator;
		this.slots = slots;
	}
			
	/**
	 * Returns the multi-word expression description object corresponding to the record.
	 *
	 * @return the IMWEDesc corresponding to the record.
	 *
	 * @since jMWE 1.0.0
	 */
	public IMWEDesc getEntry(){
		return entry;
	}
	
	/**
	 * Returns the slots of this record. These slots map each index part of
	 * the multi-word expression to a token or to <code>null</code> if the slot
	 * has not yet been filled. 
	 *
	 * @return a {@link SortedMap} that maps each index part of
	 * the multi-word expression to a token or to <code>null</code> if the slot
	 * has not yet been filled. 
	 * @since jMWE 1.0.0
	 */
	public SortedMap<IPart, T> getSlots(){
		return slots;
	}
	
	/**
	 * Returns true if all the slots in this record contain a token.
	 *
	 * @return true if all the slots in this record are full.
	 * @since jMWE 1.0.0
	 */
	public boolean isFull(){
		for(T token : slots.values())
			if(token == null)
				return false;
		return true;
	}
	
	/**
	 * Returns true if the all of the content words in this record are full.
	 *
	 * @return true if the all of the content words in this record are full.
	 * @since jMWE 1.0.0
	 */
	public boolean isContentFull(){
		for(Entry<IPart, T> slot : slots.entrySet())
			if(!slot.getKey().isStopWord() && slot.getValue() == null) 
				return false;
		return true;
	}

	/**
	 * Returns <code>true</code> if at least one token in one slot matches the
	 * part of speech of the MWE description; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if at least one token in one slot matches the
	 *         part of speech of the MWE description; <code>false</code>
	 *         otherwise.
	 * @since jMWE 1.0.0
	 */
	public boolean isPartOfSpeechSatisfied(){
		MWEPOS pos = entry.getPOS();
		for(T token : slots.values())
			if(token != null)
				if(token.getTag() != null)
					if(pos == MWEPOS.toMWEPOS(token.getTag()))
						return true;
		return false;
	}

	/**
	 * Returns true if the matching slots after the given slot in this record are empty.
	 * Matching slots have index parts with the same part lemma.
	 *
	 * @param slot
	 * the slot in consideration. May not be <code>null</code>.
	 * @return true if the matching slots after the given slot in this record are empty.
	 * @since jMWE 1.0.0
	 */
	public boolean hasEmptyMatchingSlots(Entry<IPart, T> slot){
		if(!hasMultiple) 
			return true;
		boolean first = true;
		for(Entry<IPart, T> later : slots.tailMap(slot.getKey()).entrySet()){
			if(first){
				first = false;
				continue;
			}
			if(later.getKey().getForm().equals(slot.getKey().getForm()))
				if(later.getValue() != null) 
					return false;
		}
		return true;
	}
	
	/**
	 * Returns true if the record contains two or more slots whose index parts
	 * have the same part lemma.
	 * 
	 * @return true if the record contains any matching slots
	 * @since jMWE 1.0.0
	 */
	public boolean hasMultiple(){
		return hasMultiple;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	public MWEBuilder<T> clone(){
		MWEBuilder<T> clone = new MWEBuilder<T>(entry, comparator);
		clone.getSlots().putAll(this.slots);
		return clone;
	}
	
	/**
	 * Converts the tokens in a full record into an {@link IMWE} object. If this record is not full,
	 * returns <code>null</code>.
	 *
	 * @return a IMWE object if the record is full, <code>null</code> otherwise.
	 * @since jMWE 1.0.0
	 */
	public IMWE<T> toMWE(){
		if(!isFull())
			throw new IllegalStateException();
		
		Map<T, IPart> partMap = new TreeMap<T, IPart>(comparator);
		for(Entry<IPart,T> e : slots.entrySet())
			if(partMap.put(e.getValue(), e.getKey()) != null)
				throw new IllegalArgumentException("Duplicate token");
		
		return new MWE<T>(partMap, false);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(entry.toString());
		sb.append('=');
		sb.append(slots.toString());
		return sb.toString();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + entry.hashCode();
		result = prime * result + slots.hashCode();
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MWEBuilder<?> other = (MWEBuilder<?>) obj;
		if (!entry.equals(other.entry)) return false;
		if (!slots.equals(other.slots)) return false;
		return true;
	}
	
	/**
	 * Given a set of MWE builders, fills all the slots in the records that can be
	 * filled by the given token. If necessary, adds new records to the set.
	 * @param records
	 *            a set of records whose slots may be filled. May not be
	 *            <code>null</code>.
	 * @param token
	 *            the token that will be used to fill the appropriate slots in
	 *            the records. May not be <code>null</code>.
	 * 
	 * @param <T>
	 *            type of tokens the records hold
	 * @throws NullPointerException
	 *             if either parameter is <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> void fillSlots(Set<MWEBuilder<T>> records, T token){
		
		MWEBuilder<T> record, copy;
		IPart filledSlot;
		
		LinkedList<MWEBuilder<T>> queue = new LinkedList<MWEBuilder<T>>(records);
		records.clear();
		
		while((record = queue.poll()) != null){
			
			filledSlot = null;
			// try to fill a slot in the record
			for(Entry<IPart, T> slot : record.getSlots().entrySet()){
				
				// don't do anything if the token can't fill the slot
				// or if this token is already in the record
				if(!RootMWEDesc.isFillerForSlot(token, slot.getKey()) || record.getSlots().containsValue(token)) continue;
				
				if(slot.getValue() != null && filledSlot == null){
					// if the slot isn't empty, copy the record,
					// clear the slot, and add to the queue
					copy = record.clone();
					//only replace token in slot if the matching slots after it are not filled,
					//because if they are filled they will be filled with tokens that occur earlier in the sentence
					if(record.hasEmptyMatchingSlots(slot))
						copy.getSlots().put(slot.getKey(), token);
					records.add(copy);
				} else if(filledSlot == null){
					// otherwise, if we haven't yet filled a slot for this record, fill the slot
					 slot.setValue(token);
					 filledSlot = slot.getKey();
					 records.add(record);
				 } else{
					 // if we have filled a slot for this record, 
					 // copy the record, clear the previously filled 
					 // slot, and fill the new slot
					 copy = record.clone();
					 copy.getSlots().put(filledSlot, null);
					 copy.getSlots().put(slot.getKey(), token); 
					 records.add(copy);
				 }
			 }
			records.add(record);
		}
		
	}

	/**
	 * Fills the first non-null (empty) slot in the given builder. Each slot can
	 * be filled by a token that matches the part. Returns true if the slot can
	 * be filled, false otherwise.
	 * 
	 * @param builder
	 *            the builder to be filled
	 * @param t
	 *            the token to fill the builder's next open slot with
	 * @param <T>
	 *            the type of tokens used in the MWEs to be built.
	 * @return true if the next open slot can be filled, false otherwise.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean fillNextSlot(MWEBuilder<T> builder, T t) {
		
		// find next empty slot
		Entry<IPart, T> next = null;
		for(Entry<IPart, T> e : builder.getSlots().entrySet())
			if(e.getValue() == null){
				next = e;
				break;
			}
		
		// fill slot
		if(AbstractMWEDesc.isFillerForSlot(t, next.getKey())){
			next.setValue(t);
			return true;
		}
		
		return false;
	}
}



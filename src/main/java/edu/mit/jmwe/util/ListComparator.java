package edu.mit.jmwe.util;

import java.util.List;
import java.util.Map;

/**
 * Compares two elements from a list based on their order of appearance in the
 * list. Returns a negative integer, zero, or a positive integer if the first
 * argument is appears earlier than, is equal to, or after the second.
 * 
 * @param <T> the token type
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: ListComparator.java 268 2011-05-05 19:23:28Z markaf $
 * @since jMWE 1.0.0
 */
public class ListComparator<T> extends AbstractIndexComparator<T, T> {

	/**
	 * Constructs the comparator from the given list of objects by mapping each
	 * object in the list to its index in the list.
	 * 
	 * @param list
	 *            the list of objects used to construct this comparator. May not
	 *            be <code>null</code>.
	 * @throws NullPointerException
	 *             if the given list is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ListComparator(List<T> list) {
		super(list);
	}

	/**
	 * Constructs the comparator from the given index map of each object in a
	 * list to its index in the list.
	 * 
	 * @param indexMap
	 *            map of each object in a list to its index in the list. May not
	 *            be <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified map is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ListComparator(Map<T, Integer> indexMap) {
		super(indexMap);
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(T arg0, T arg1) {
		int idx0 = indexMap.get(arg0);
		int idx1 = indexMap.get(arg1);
		return idx0-idx1;
	}

}
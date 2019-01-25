/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

import java.util.Comparator;

/**
 * A comparator that captures the ordering of lines in a tag list file. This
 * files are ordered alphabetically by sense key.
 * 
 * @author Mark A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class TagFileLineComparator implements Comparator<String> {
	
	/**
	 * The static singleton instance of this class, initialized and accessed by
	 * {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static TagFileLineComparator fInstance;

	/**
	 * Returns (and initializes if necessary) the singleton instance of this
	 * class.
	 * 
	 * @return the singleton instance of this class
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static TagFileLineComparator getInstance() {
		if (fInstance == null) fInstance = new TagFileLineComparator();
		return fInstance;
	}

	/**
	 * This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated. To obtain and instance of
	 * this class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected TagFileLineComparator() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(T, T)
	 */
	public int compare(String line1, String line2) {
		// get sense keys
		int i1 = line1.indexOf(' ');
		int i2 = line2.indexOf(' ');
		line1 = (i1 == -1) ? line1 : line1.substring(0, i1);
		line2 = (i2 == -1) ? line2 : line2.substring(0, i2);
		return line1.compareTo(line2);
	}

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.util;

import java.util.Date;

/**
 * Creates a progress bar that does nothing and prints nothing. Can be used when
 * an implementation requires a progress bar but you have none to give it.
 * <p>
 * This class is a singleton. It may be subclassed, but not directly
 * instantiated.
 * 
 * @author M.A. Finlayson
 * @version $Id: NullProgressBar.java 270 2011-05-05 19:25:15Z markaf $
 * @since jMWE 1.0.0
 */
public class NullProgressBar implements IProgressBar {
	
	// default instance
	private static NullProgressBar instance;

	/**
	 * Returns the singleton instance of this class, creating it if necessary.
	 * 
	 * @return the singleton Null progress bar
	 * @since jMWE 1.0.0
	 */
	public static NullProgressBar getInstance(){
		if(instance == null)
			instance = new NullProgressBar();
		return instance;
	}

	/**
	 * This constructor is marked protected so that this class may be
	 * subclassed, but not directly instantiated.
	 * 
	 * @since jMWE 1.0.0
	 */
	protected NullProgressBar(){}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getCount()
	 */
	public int getCount() {
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getTickCount()
	 */
	public int getTickCount() {
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getTotalExpected()
	 */
	public int getExpected() {
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getTotalExpectedTicks()
	 */
	public int getExpectedTicks() {
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getStepSize()
	 */
	public double getStepSize() {
		return 0;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getStartTime()
	 */
	public Date getStartTime() {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getEndTime()
	 */
	public Date getEndTime() {
		return null;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.IProgressBar#increment()
	 */
	public void increment() {
		// no op
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.IProgressBar#increment(int)
	 */
	public void increment(int amount) {
		// no op
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.IProgressBar#finish()
	 */
	public void finish() {
		// no op
	}

}

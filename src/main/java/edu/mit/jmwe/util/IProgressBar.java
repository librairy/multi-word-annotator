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
 * Progress bars allow operations to report progress. The progress bar is
 * started when instantiated, and expects a certain number of 'units' of
 * progress. A unit of progress is reported by calling the {@link #increment()}
 * or multiple units can be reported by calling {@link #increment(int)}.  When
 * the task is finished, the reporter should call the {@link #finish()} method.
 * 
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: IProgressBar.java 263 2011-05-05 19:17:02Z markaf $
 * @since jMWE 1.0.0
 */
public interface IProgressBar {

	/**
	 * Returns the current state of the progress bar, that is, the number of
	 * units of progress that have been reported to it.
	 * 
	 * @return the number of units of progress so far reported
	 * @since jMWE 1.0.0
	 */
	public abstract int getCount();

	/**
	 * Returns the number of current progress bar ticks that have been used. 
	 *
	 * @return the number of current progress bar ticks that have been used.
	 * @since jMWE 1.0.0
	 */
	public abstract int getTickCount();

	/**
	 * Returns the expected number of units of progress.
	 * 
	 * @return the expected number of units of progress.
	 * @since jMWE 1.0.0
	 */
	public abstract int getExpected();

	/**
	 * Returns the expected number of ticks.
	 * 
	 * @return the expected number of ticks.
	 * @since jMWE 1.0.0
	 */
	public abstract int getExpectedTicks();

	/**
	 * Returns the number of units of progress that are represented by one tick.
	 *
	 * @return the number of units of progress that are represented by one tick.
	 * @since jMWE 1.0.0
	 */
	public abstract double getStepSize();

	/**
	 * Returns the date and time on which this progress bar was started.
	 *
	 * @return the date and time on which this progress bar was started.
	 * @since jMWE 1.0.0
	 */
	public abstract Date getStartTime();

	/**
	 * Returns the date and time on which this progress bar was finished. If the
	 * progress bar is not yet finished, will return <code>null</code>.
	 * 
	 * @return the date and time on which this progress bar was finished, or
	 *         <code>null</code> if not yet finished.
	 * @since jMWE 1.0.0
	 */
	public abstract Date getEndTime();

	/**
	 * Increments the progress bar by 1.
	 *
	 * @since jMWE 1.0.0
	 */
	public void increment();
	
	/**
	 * Increments the progress bar by a given amount.
	 * 
	 * @param amount
	 *            amount by which the progress bar will be incremented
	 * @since jMWE 1.0.0
	 */
	public void increment(int amount);
	
	/**
	 * Stops the progress bar and prints the total time that the progress bar ran.
	 *
	 * @since jMWE 1.0.0
	 */
	public void finish();

}

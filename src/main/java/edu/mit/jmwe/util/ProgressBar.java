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
 * Default implementation of <code>IProgressBar</code>.
 * 
 * @author M.A. Finlayson
 * @version $Id: ProgressBar.java 271 2011-05-05 19:26:12Z markaf $
 * @since jMWE 1.0.0
 */
public class ProgressBar implements IProgressBar {

	/**
	 * Default number of ticks for a progress bar.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static final int DEFAULT_TICKS = 50;
	
	// final instance fields
	private final int expected;
	private final int expectedTicks;
	private final double step;
	private final boolean printDate;
	
	// dynamic instance fields
	private int count = 0;
	private int tickCount = 0;
	private double nextTick = 0;
	private Date start = null;
	private Date end = null;

	/**
	 * Creates a progress bar that expects the specified number of increments.
	 * 
	 * @param expected
	 *            the number of increments that are expected
	 * @throws IllegalArgumentException
	 *             if the number of expected increments is not positive.
	 * @since jMWE 1.0.0
	 */
	public ProgressBar(int expected){
		this(expected, DEFAULT_TICKS);
	}

	/**
	 * Creates a progress bar that expects the specified number of increments,
	 * and reports progress across a bar the specified number of ticks wide.
	 * 
	 * @param expected
	 *            the number of increments that are expected
	 * @param ticks
	 *            the length of the progress bar, in ticks
	 * @throws IllegalArgumentException
	 *             if the number of expected increments or the number of printed
	 *             ticks is not positive.
	 * @since jMWE 1.0.0
	 */
	public ProgressBar(int expected, int ticks){
		this(expected, ticks, true);
	}

	/**
	 * Constructs a new progress bar that has an expected number of observations
	 * compressed into a specified number of ticks, and optionally prints the
	 * time and memory delta between instantiation and calling the
	 * {@link #finish()} method.
	 * 
	 * @param expected
	 *            the number of increments that are expected
	 * @param ticks
	 *            the length of the progress bar, in ticks
	 * @param printDate
	 *            if <code>true</code>, the progress bar will print the start
	 *            and end date
	 * @throws IllegalArgumentException
	 *             if the number of expected increments or the number of printed
	 *             ticks is not positive.
	 * @since jMWE 1.0.0
	 */
	public ProgressBar(int expected, int ticks, boolean printDate){
		if(expected <= 0)
			throw new IllegalArgumentException("Expected increments must be positive");
		if(ticks <= 0)
			throw new IllegalArgumentException("Number of printed ticks must be positive");
		
		this.expected = expected;
		this.expectedTicks = ticks;
		this.step = (double)expected/(double)ticks;
		this.nextTick = step;
		this.printDate = printDate;
		
		// construct console progress bar
		StringBuilder sb = new StringBuilder(ticks);
		sb.append("100% bar: ");
		for(int i = 0; i < ticks; i++)
			sb.append('.');
		sb.append(" (total=" + expected + ")");

		// begin timing
		this.start = new Date();

		// print status to console
		if(printDate)
			System.out.println("Started " + start);
		System.out.println(sb.toString());
		System.out.print("Progress: ");
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#increment()
	 */
	public void increment(){
		increment(1);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#increment(int)
	 */
	public void increment(int i){
		count += i;
		while(nextTick <= count){
			System.out.print('.');
			tickCount++;
			nextTick += step;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#finish()
	 */
	public void finish(){
		
		// print remaining ticks
		while(tickCount < expectedTicks){
			System.out.print('.');
			tickCount++;
		}
		
		// calculate time
		end = new Date();
		long millis = end.getTime()-start.getTime();
		float secs = (float)millis/(float)1000;

		// print out finished
		System.out.println(" (count=" + count + ", time=" + secs +"s)");
		if(printDate)
			System.out.println("Finished " + new Date());
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getTotalExpected()
	 */
	public int getExpected(){
		return expected;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getTotalExpectedTicks()
	 */
	public int getExpectedTicks(){
		return expectedTicks;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getStartTime()
	 */
	public Date getStartTime(){
		return start;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getEndTime()
	 */
	public Date getEndTime(){
		return end;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getCount()
	 */
	public int getCount(){
		return count;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getTickCount()
	 */
	public int getTickCount(){
		return tickCount;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.util.IProgressBar#getStepSize()
	 */
	public double getStepSize(){
		return step;
	}
	
}

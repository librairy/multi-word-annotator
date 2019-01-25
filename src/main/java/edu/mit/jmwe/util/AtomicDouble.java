package edu.mit.jmwe.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A <tt>double</tt> value that may be updated atomically. See the
 * {@link java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * <tt>AtomicDouble</tt> is used in applications such as atomically
 * incremented counters, and cannot be used as a replacement for an
 * {@link java.lang.Integer}. However, this class does extend
 * <tt>Number</tt> to allow uniform access by tools and utilities that
 * deal with numerically-based classes.
 *
 * @author M.A. Finlayson
 * @version $Id: AtomicDouble.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class AtomicDouble<T, S> extends Number { 
	
	private static final long serialVersionUID = -7139501654787647862L;
	private final AtomicLong value;

    /**
	 * Create a new AtomicDouble with initial value <tt>0</tt>.
	 */
	public AtomicDouble() {
		this(0);
	}

	/**
     * Create a new AtomicInteger with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicDouble(double initialValue) {
        this.value = new AtomicLong(Double.doubleToLongBits(initialValue));
    }

    /**
     * Get the current value.
     *
     * @return the current value
     */
    public final double get() {
        return Double.longBitsToDouble(value.get());
    }
    
    /**
     * Set to the given value.
     *
     * @param newValue the new value
     */
    public final void set(double newValue) {
        value.set(Double.doubleToLongBits(newValue));
    }	    
    
	/**
	 * Increments the current value by the specified amount.
	 *
	 * @param amt
	 *            the amount by which to increment the double
	 * @since jMWE 1.0.0
	 */
    public void increment(double amt){
    	double newValue;
    	long expect, update;
    	do {
    		expect = value.get();
    		newValue = Double.longBitsToDouble(expect) + amt;
    		update = Double.doubleToLongBits(newValue);
    	} while(!value.compareAndSet(expect, update));
    }

    /* 
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return Double.toString(get());
    }

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Number#intValue()
	 */
	@Override
	public int intValue() {
		return (int)get();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Number#longValue()
	 */
	@Override
	public long longValue() {
		return (long)get();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Number#floatValue()
	 */
	@Override
	public float floatValue() {
		return (float)get();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Number#doubleValue()
	 */
	@Override
	public double doubleValue() {
		return get();
	}

}
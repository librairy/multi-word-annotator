/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.data;

/**
 * Thrown when a line from a data resource does not match expected formatting
 * conventions.
 * 
 * @author Mark A. Finlayson
 * @version 1.156, 21 Sep 2008
 * @since JSemcor 1.0.0
 */
public class MalformedLineException extends RuntimeException {

	private static final long serialVersionUID = -4402988991209648616L;

	/**
	 * Constructs a new exception with <code>null</code> as its detail
	 * message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public MalformedLineException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @since JSemcor 1.0.0
	 */
	public MalformedLineException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this runtime exception's detail
	 * message.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since JSemcor 1.0.0
	 */
	public MalformedLineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of {@code (cause==null ? null : cause.toString())} (which typically
	 * contains the class and detail message of {@code cause}). This
	 * constructor is useful for runtime exceptions that are little more than
	 * wrappers for other throwables.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since JSemcor 1.0.0
	 */
	public MalformedLineException(Throwable cause) {
		super(cause);
	}
	
}

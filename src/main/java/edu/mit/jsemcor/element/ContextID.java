/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;


/**
 * Default, concrete implementation of the {@link IContextID} interface.
 * 
 * @author M.A. Finlayson
 * @version 1.637, 20 Jun 2011
 * @since JSemcor 1.0.0
 */
public class ContextID implements IContextID {
	
	private final String contextName, concordanceName;
	private transient String toString;

	/**
	 * Constructs a new context id object out the specified context and
	 * concordance names.
	 * 
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if either argument is empty or all whitespace
	 * @since JSemcor 1.0.0
	 */
	public ContextID(String contextName, String concordanceName){
		if(contextName == null)
			throw new NullPointerException();
		if(concordanceName == null)
			throw new NullPointerException();
		if(contextName.trim().length() == 0)
			throw new IllegalArgumentException();
		if(concordanceName.trim().length() == 0)
			throw new IllegalArgumentException();
		this.contextName = contextName;
		this.concordanceName = concordanceName;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextID#getFilename()
	 */
	public String getContextName() {
		return contextName;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.data.IContextID#getConcordanceName()
	 */
	public String getConcordanceName() {
		return concordanceName;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		if(toString == null)
			toString = concordanceName + "/" + contextName;
		return toString;
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
		result = prime * result + contextName.hashCode();
		result = prime * result + concordanceName.hashCode();
		return result;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IContextID))
			return false;
		final IContextID that = (IContextID) obj;
		if (!this.contextName.equals(that.getContextName()))
			return false;
		if (!this.concordanceName.equals(that.getConcordanceName()))
			return false;
		return true;
	}
	
	/**
	 * Parses the specified context id, represented as a String, into a context
	 * id object. The string must be of the format
	 * "concordanceName/contextName".
	 * 
	 * @param cid
	 *            the string to be parsed
	 * @return a context object corresponding to the specified context id string
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the argument is ill-formatted
	 * @since JSemcor 1.1.0
	 */
	public static ContextID parse(String cid){
		if(cid == null)
			throw new NullPointerException();
		cid = cid.trim();
		int idx = cid.indexOf('/');
		if(idx == -1)
			throw new IllegalArgumentException();
		String concordanceName = cid.substring(0, idx);
		String contextName = cid.substring(idx+1, cid.length());
		return new ContextID(contextName, concordanceName);
	}

}

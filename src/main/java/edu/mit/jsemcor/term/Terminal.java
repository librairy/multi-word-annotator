/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.term;

/**
 * A generic implementation of an {@link ITerminal} object.
 * 
 * @author M.A. Finlayson
 * @version 1.52, 19 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Terminal implements ITerminal {
	
	private final String attr;
    private final String value;
    
    String data;

    /**
	 * Constructs a new Terminal with the specified name.
	 * 
	 * @throws NullPointerException
	 *             if either argument is {@link NullPointerException}
	 * @throws IllegalArgumentException
	 *             if either argument is empty or all whitespace
	 * 
	 * @since JSemcor 1.0.0
	 */
    public Terminal(String attr, String value){
    	if(attr.trim().length() == 0) throw new NullPointerException();
    	if(value.trim().length() == 0) throw new IllegalArgumentException();
    	this.value = value;
    	this.attr = attr;
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see edu.mit.jsemcor.term.ITerminal#getAttribute()
     */
    public String getAttribute(){
    	return attr;
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.term.ICategory#getName()
	 */
	public String getValue() {
		return value;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
    	if(data == null){
    		StringBuilder sb = new StringBuilder(attr.length() + value.length() + 1);
    		sb.append(attr);
    		sb.append(EQUALS);
    		sb.append(value);
    		data = sb.toString();
    	}
        return data;
	}
    
    /* 
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return getData();
    }
    
}

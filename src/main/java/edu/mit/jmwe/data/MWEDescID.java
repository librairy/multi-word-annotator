/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.io.IOException;

/**
 * Default implementation of <code>IMWEDescID</code>.
 *
 * @author M.A. Finlayson
 * @version $Id: MWEDescID.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class MWEDescID implements IMWEDescID {
	
	// final instance fields
	private final MWEPOS pos;
	private final String rootForm;
	private final String infForm;
	private final IMWEDescID rootID;

	private transient String toString;

	/**
	 * Constructs this description ID from the MWE's root (non inflected) form
	 * and part of speech. Sets the inflected form of this description to
	 * <code>null</code>.
	 * 
	 * @param rootForm
	 *            the non inflected form of the MWE associated with this id.
	 *            Must be delimited by underscores.
	 * @param pos
	 *            part of speech of the MWE associated with this id.
	 * @since jMWE 1.0.0
	 */
	public MWEDescID(String rootForm, MWEPOS pos){
		if(pos == null)
			throw new NullPointerException();
		rootForm = rootForm.trim().toLowerCase();
		if(!formPattern.matcher(rootForm).matches())
			throw new IllegalArgumentException("Illegal root form: " + rootForm);
		this.rootForm = rootForm;
		this.pos = pos;
		this.infForm = null;
		this.rootID = this;
	}

	/**
	 * Constructs this description ID from the MWE's root ID and inflected form.
	 * 
	 * @param rootID
	 *            the root id of the MWE
	 * @param infForm
	 *            inflected form of the MWE. Must be delimited by underscores.
	 * @since jMWE 1.0.0
	 */
	public MWEDescID(IMWEDescID rootID, String infForm) {
		if(rootID == null)
			throw new NullPointerException();
		if(!formPattern.matcher(infForm).matches())
			throw new IllegalArgumentException("Illegal inflected form: " + infForm);
		this.rootID = rootID;
		this.rootForm = rootID.getForm();
		this.pos = rootID.getPOS();
		this.infForm = infForm;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDescID#getRootID()
	 */
	public IMWEDescID getRootID() {
		return rootID;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasForm#getForm()
	 */
	public String getForm() {
		return rootForm;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDescID#getInflectedForm()
	 */
	public String getInflectedForm() {
		return infForm;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasMWEPOS#getPOS()
	 */
	public MWEPOS getPOS() {
		return pos;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(IMWEDescID id) {
		int cmp = rootForm.compareTo(id.getForm());
		if(cmp != 0)
			return cmp;
		return pos.compareTo(id.getPOS());
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
		result = prime * result + rootForm.hashCode();
		result = prime * result +  pos.hashCode();
		result = prime * result + ((infForm == null) ? 0 : infForm.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		MWEDescID other = (MWEDescID) obj;
		if (pos != other.pos)
			return false;
		if (infForm == null) {
			if (other.infForm != null)
				return false;
		} else if (!infForm.equals(other.infForm))
			return false;
		if (!rootForm.equals(other.rootForm))
			return false;
		return true;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(toString == null)
			toString = toString(this);
		return toString;
	}

	/**
	 * Returns the String representation of the given id.
	 * 
	 * @param id
	 *            the id to be represented as a String.
	 * @return the String representation of the given id.
	 * @since jMWE 1.0.0
	 */
	public static String toString(IMWEDescID id){
		StringBuilder sb = new StringBuilder(id.getForm().length() + 2);
		try {
			toString(id, sb);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

	/**
	 * Returns the String representation of the given id as: form_POS/infform
	 * 
	 * @param id
	 *            the id to be represented as a String.
	 * @param buf
	 *            the appendable buffer used to build the String
	 * @throws IOException
	 *             if there is a problem appending to the buffer
	 * @since jMWE 1.0.0
	 */
	public static void toString(IMWEDescID id, Appendable buf) throws IOException {
		buf.append(id.getForm());
		buf.append('_');
		buf.append(id.getPOS().getIdentifier());
		if(id.getInflectedForm() != null){
			buf.append('/');
			buf.append(id.getInflectedForm());
		}
	}

}

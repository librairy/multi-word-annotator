/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data;

import java.util.List;

import edu.mit.jmwe.data.IInfMWEDesc.IInfPart;

/**
 * Default implementation of the <code>IInfMWEDesc</code> interface.
 * 
 * @author M.A. Finlayson
 * @version $Id: InfMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class InfMWEDesc extends AbstractMWEDesc<IInfPart> implements IInfMWEDesc {
	
	// final instance field
	private final IRootMWEDesc root;
	private final IMWEDescID id;

	/**
	 * Constructs a new MWE description object from the specified root
	 * description and inflected form.
	 * 
	 * @param root
	 *            the root description of the MWE
	 * @param inflectedForm
	 *            A string representing the inflected MWE with its words
	 *            separated by underscores
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the inflected form does not contain underscores
	 * @since jMWE 1.0.0
	 */
	public InfMWEDesc(IRootMWEDesc root, String inflectedForm){
		this(root, splitOnUnderscores(inflectedForm), null);
	}

	/**
	 * Constructs a new inflected MWE description object from the specified
	 * inflected form, root description, and counts relating to the MWE's
	 * appearance in the reference concordance.
	 * 
	 * @param root
	 *            the root description of the MWE
	 * @param inflectedForm
	 *            A string representing the inflected MWE with its words
	 *            separated by underscores
	 * @param counts
	 *            the counts relating to the MWE's appearance in the reference
	 *            concordance.
	 * @throws NullPointerException
	 *             if the root description or inflected form is
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the inflected form does not contain underscores
	 * @since jMWE 1.0.0
	 */
	public InfMWEDesc(IRootMWEDesc root, String inflectedForm, int... counts){
		this(root, splitOnUnderscores(inflectedForm), counts);
	}

	/**
	 * Constructs a new inflected MWE description object from the specified list
	 * of parts and root description
	 * 
	 * @param root
	 *            the root description of the MWE
	 * @param parts
	 *            A list of parts that comprise the MWE
	 * @throws NullPointerException
	 *             if the list of parts or root is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified list has less than two elements, or any
	 *             trimmed string in the list contains an underscore, is empty,
	 *             or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public InfMWEDesc(IRootMWEDesc root, List<String> parts){
		this(root, parts, null);
	}

	/**
	 * Constructs a new MWE description object from the list of parts. This
	 * constructor allocates a new internal list, and so subsequent changes to
	 * the source list will not affect this object.
	 * 
	 * @param root
	 *            the root descriptor to use when constructing the MWE, may not
	 *            be <code>null</code>
	 * @param parts
	 *            the list of parts that will make up this list, may neither be
	 *            <code>null</code> nor empty, and may not contain any
	 *            <code>null</code>s, empty or all whitespace strings, or
	 *            strings that contain the underscore character.
	 * @param counts
	 *            the implementation-specific counts relating to the MWE's
	 *            appearance in a reference concordance.
	 * @throws NullPointerException
	 *             if the specified list of parts is <code>null</code>, or
	 *             contains a <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified list has less than two elements, or any
	 *             trimmed string in the list contains an underscore, is empty,
	 *             or contains whitespace
	 * @since jMWE 1.0.0
	 */
	public InfMWEDesc(IRootMWEDesc root, List<String> parts, int... counts){
		super(parts, counts);
		if(root == null)
			throw new NullPointerException();
		this.id = new MWEDescID(root.getID(), getForm());
		this.root = root;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.AbstractMWEDesc#getExpectedCountLength()
	 */
	@Override
	protected int getExpectedCountLength() {
		return IInfMWEDesc.EXPECTED_COUNT_LENGTH;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IMWEDesc#getID()
	 */
	public IMWEDescID getID() {
		return id;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IHasMWEPOS#getPOS()
	 */
	public MWEPOS getPOS() {
		return id.getPOS();
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IInfMWEDesc#getRootMWEDesc()
	 */
	public IRootMWEDesc getRootMWEDesc() {
		return root;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.IInfMWEDesc#getMarkedPattern()
	 */
	public int getMarkedPattern() {
		return counts[4];
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.data.AbstractMWEDesc#makePart(java.lang.String, int)
	 */
	@Override
	protected IInfPart makePart(String form, int index) {
		return new InfPart(form, index);
	}
	
	

	/**
	 * Default implementation of the {@link IInfPart} interface.
	 * 
	 * @author M.A. Finlayson
	 * @version $Id: InfMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	protected class InfPart extends AbstractPart implements IInfPart {

		/**
		 * Constructs the part from its text and index in the MWE.
		 * 
		 * @param form
		 *            the text of the part
		 * @param index
		 *            the position of this part in the MWE
		 * @since jMWE 1.0.0
		 */
		public InfPart(String form, int index) {
			super(form, index);
		}

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.AbstractMWEDesc.AbstractPart#getParent()
		 */
		@Override
		public IInfMWEDesc getParent() {
			return (IInfMWEDesc)super.getParent();
		}
	
	}
	
	/**
	 * A builder for inflected MWE description objecgts
	 *
	 * @author M.A. Finlayson
	 * @version $Id: InfMWEDesc.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	public static class InfMWEDescBuilder {
		
		// public access fields
		public String form = null;
		public int[] counts = new int[IInfMWEDesc.EXPECTED_COUNT_LENGTH];

		/**
		 * Constructs an inflected MWE description from the root description of
		 * the MWE. The newly created description has a <code>null</code> form??
		 * 
		 * @param root
		 *            the root description of the MWE
		 * @return returns an inflected MWE description from the root
		 *         description of the MWE. The newly created description has a
		 *         <code>null</code> form.
		 * @since jMWE 1.0.0
		 */
		public InfMWEDesc toInfMWEDesc(IRootMWEDesc root){
			return new InfMWEDesc(root, form, counts);
		}

	}

}

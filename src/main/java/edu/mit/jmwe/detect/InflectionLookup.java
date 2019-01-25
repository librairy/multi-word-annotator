/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Iterator;
import java.util.List;

import edu.mit.jmwe.data.IInfMWEDesc;
import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.InfMWEDesc;

/**
 * Uses a given MWE detector to find multi-word expressions in a sentence but
 * discards inflected multi-word expressions whose form is not listed as a valid
 * inflected form by its associated <code>IMWEDesc</code>.
 * 
 * @author N. Kulkarni
 * @author M.A. Finlayson
 * @version $Id: InflectionLookup.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class InflectionLookup extends HasMWEDetector implements IMWEDetectorFilter {

	/**
	 * Constructs the detector from the given backing detector.
	 * 
	 * @param d
	 *            the IMWEDetector that will be used to back this detector. May
	 *            not be <code>null</code>.
	 * @throws NullPointerException
	 *             if the backing detector is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public InflectionLookup(IMWEDetector d){
		super(d);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.IMWEDetector#detect(java.util.List)
	 */
	public <T extends IToken> List<IMWE<T>> detect(List<T> sentence) {
		
		// get raw results from backing detector
		List<IMWE<T>> found = super.detect(sentence);
		
		IMWE<T> mwe;
		IMWEDesc base;
		IRootMWEDesc root;
		IInfMWEDesc inf;
		
		outer:for(Iterator<IMWE<T>> i = found.iterator(); i.hasNext();){
			mwe = i.next();
			if(!mwe.isInflected())
				continue;
			
			// check that the found MWE is a listed as a valid inflected form
			base = mwe.getEntry();
			if(base instanceof IInfMWEDesc)
				continue;
			
			// look in inflected forms
			if(base instanceof IRootMWEDesc){
				root = (IRootMWEDesc)base;
				inf = getSurfaceFormDescription(root, mwe);
				for(IInfMWEDesc infForm : root.getInflected().values())
					if(infForm.equals(inf))
						continue outer;
			}
				
			i.remove();
		}
		return found;
	}

	/**
	 * Returns a multi-word expression description with a lemma that is
	 * constructed by concatenating the tokens of the MWE exactly as they appear
	 * in the sentence with underscores.
	 * 
	 * @param <T>
	 *            the type of token
	 * @param root
	 *            the cognizant root
	 * @param mwe
	 *            the multi-word expression used to construct the description
	 *            object
	 * @return the description of the given MWE with a lemma that is constructed
	 *         by concatenating the tokens of the MWE exactly as they appear in
	 *         the sentence, preserving inflection rather than using the base
	 *         form as the lemma.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> IInfMWEDesc getSurfaceFormDescription(IRootMWEDesc root, IMWE<T> mwe){
		StringBuilder sb = new StringBuilder();
		T token;
		for(Iterator<T> i = mwe.getTokens().iterator(); i.hasNext();){
			token = i.next();
			sb.append(token.getForm().toLowerCase());
			if(i.hasNext())
				sb.append('_');
		}

		return new InfMWEDesc(root, sb.toString());
	}

}

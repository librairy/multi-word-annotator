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
 * A generic token in a context file. Sentences are comprised of tokens, and
 * tokens may either be punctuation or word forms.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IToken extends IElement {

	/**
	 * The actual string contained between the SGML brackets in the Semcor data
	 * file. This may be a collocation, with separate tokens concatenated with
	 * underscores instead of spaces.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getText();

}

/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.List;

/**
 * Objects that implement this interface represent the instantiated form of a
 * context. Usually an object that implements this interface is obtained by
 * parsing a context file.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IContext extends IElement {

	/**
	 * The string that marks the concordance attribute in the
	 * {@code <contextfile>} tag.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_CONCORDANCE = "concordance";

	/**
	 * The string that marks the filename attribute in the {@code <context>}
	 * tag.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_FILENAME = "filename";

	/**
	 * The string the marks the has paragraphs attribute in the
	 * {@code <context>} tag.
	 */
	public static final String ATTR_PARAS = "paras";

	/**
	 * The string that ends both snum= and pnum= number attributes in context
	 * file data.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_NUM = "num";

	/**
	 * The string that marks as true the 'paragraphs' attribute in the
	 * {@code <context>} tag.
	 */
	public static final String VALUE_YES = "yes";

	/**
	 * The beginning of the tag that marks the beginning of a block of context
	 * file data, {@code <contextfile}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_CONTEXTFILE_LEFT = "<contextfile";

	/**
	 * The unadorned tag that marks the beginning of a block of context file
	 * data, {@code <contextfile>}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_CONTEXTFILE = TAG_CONTEXTFILE_LEFT
			+ RIGHT_ANGLE_BRACKET;

	/**
	 * The tag that marks the end of a block of context file data,
	 * {@code </contextfile>}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_CONTEXTFILE_RIGHT = "</contextfile>";

	/**
	 * The beginning of the tag that marks the beginning of a block of context
	 * file data, {@code <context}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_CONTEXT_LEFT = "<context";

	/**
	 * The unadorned tag that marks the beginning of a block of context file
	 * data, {@code <context>}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_CONTEXT = TAG_CONTEXT_LEFT
			+ RIGHT_ANGLE_BRACKET;

	/**
	 * The tag that marks the end of a block of context file data,
	 * {@code </context>}
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_CONTEXT_RIGHT = "</context>";

	/**
	 * Returns the context id (usually equivalent to the filename), which can be
	 * used to retrieve this context from a concordance
	 * 
	 * @since JSemcor 1.0.0
	 */
	public IContextID getID();

	/**
	 * The value of the 'concordance' attribute in the contextfile tag.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getCorpus();

	/**
	 * The value of the 'filename' attribute in the context tag.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getFilename();

	/**
	 * Returns <code>true</code> if the {@link #getParagraphs()} call returns
	 * a non-empty list; otherwise, returns <code>false</code>. Some contexts
	 * are segmented into paragraphs, some are not.
	 * 
	 * @return <code>true</code> if this context has paragraphs;
	 *         <code>false</code> otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public boolean hasParagraphs();

	/**
	 * Returns an unmodifiable list of the paragraphs in this context. If this
	 * context is not segmented into paragraphs, this method returns the empty
	 * list. It will never return <code>null</code>. If non-empty, iterating
	 * over the sentences in the paragraphs in order will produce the same set
	 * of objects as iterating over the sentence list in order.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<IParagraph> getParagraphs();

	/**
	 * Returns an unmodifiable list of all the sentences in this context. This
	 * call will never return <code>null</code>, and if it returns empty, you
	 * have a pathological context.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<ISentence> getSentences();

	/**
	 * Returns the sentence whose {@link ISentence#getNumber()} call returns the
	 * same as that specified in this call. Note that sentences are not
	 * zero-indexed.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public ISentence getSentence(int sentNum);

}

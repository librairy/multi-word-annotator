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

import edu.mit.jsemcor.term.ICommand;
import edu.mit.jsemcor.term.INote;
import edu.mit.jsemcor.term.IOtherTag;
import edu.mit.jsemcor.term.IPOSTag;

/**
 * Indicates that this object contains word form data from a concordance context
 * file.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface IWordform extends IToken {

	/**
	 * String used as the attribute for redefinition strings.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_RDF = "rdf";

	/**
	 * String used as the attribute for separator strings.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_SEP = "sep";

	/**
	 * String used as the attribute for the distance integer.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String ATTR_DC = "dc";

	/**
	 * The SGML tag used to indicate a word form element.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_WF_VALUE = "wf";

	/**
	 * The unadorned tag used to start word form elements.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_WF = "<wf>";

	/**
	 * String used to start word form expressions.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_WF_START = "<wf";

	/**
	 * String used to end word form expressions.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String TAG_WF_END = "</wf>";

	/**
	 * Space, the default separator character. Note that in the actual Semcor
	 * files space is used as the default word separator, but in many cases is
	 * not the appropriate separator character.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final String DEFAULT_SEP = " ";

	/**
	 * Default distance value, 0.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final int DEFAULT_DC = 0;

	/**
	 * Returns the word number in the sentence. Word numbers are sequentially
	 * assigned to each word found in the sentence, beginning with 1. Note that
	 * {@link IPunc} objects (punctuation) are not counted when determining the
	 * word number.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public int getNumber();

	/**
	 * Returns the token number of in the sentence. Token numbers are
	 * sequentially assigned to each token found in the sentence, beginning with
	 * 1.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public int getTokenIndex();

	/**
	 * Returns the command associated with this word form. This method will not
	 * return <code>null</code>
	 * 
	 * @since JSemcor 1.0.0
	 */
	public ICommand getCommand();

	/**
	 * Returns the string to which this word has been 'redefined'. If this
	 * wordform has no redefinition, this method will return <code>null</code>.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getRedefinition();

	/**
	 * Indicates that string that should be displayed between this word form
	 * element and the next. The default word separator is one space. If nothing
	 * is specified for this field in the context file, this call returns
	 * <code>null</code>.
	 * 
	 * @see #DEFAULT_SEP
	 * @since JSemcor 1.0.0
	 */
	public String getSeparatorString();

	/**
	 * The syntactic tag assigned to this word form. If no part of speech tag is
	 * specified for this wordform, this method returns <code>null</code>.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public IPOSTag getPOSTag();

	/**
	 * Indicates that word is part of a discontinuous collocation in which the
	 * words comprising the collocation are not adjacent. The result is an
	 * integer that specifies how many word form elements away the semantic tag
	 * for the collocation is. It is always negative, indicating how many tokens
	 * before this one is the related word. If this wordform is not a part of a
	 * collocation, this method returns 0.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public int getDistance();

	/**
	 * Returns the semantic tag associated with this word form, if any. If this
	 * wordform does not have a semantic tag, this method returns
	 * <code>null</code>.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public ISemanticTag getSemanticTag();

	/**
	 * Returns the other tag associated with this word form, if any. If this
	 * wordform does not have an other tag, this method returns
	 * <code>null</code>
	 * 
	 * @since JSemcor 1.0.0
	 */
	public IOtherTag getOtherTag();

	/**
	 * Returns the note object associated with this word form. If this wordform
	 * does not have a tag note, this method returns <code>null</code>.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public INote getTagNote();

	/**
	 * Returns a list of the tokens that make up this word form, that is, a list
	 * of the strings that are separated by underscores in the string returned
	 * by {@link IToken#getText()}, excluding trailing and leading empty
	 * strings. This method always returns a non-null, non-empty result, even it
	 * is a singleton list.
	 * 
	 * <blockquote> <table cellpadding=1 cellspacing=0 summary="Examples showing
	 * text and resulting token array">
	 * <tr>
	 * <th><i>getText()</i></th>
	 * <th><i>getConstituentTokens()</i></th>
	 * </tr>
	 * <tr>
	 * <td align=center>"The"</td>
	 * <td><tt>["The"]</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>"John_Smith"</td>
	 * <td><tt>["John", "Smith"]</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>"it_'s"</td>
	 * <td><tt>["it", "'s"]</tt></td>
	 * </tr>
	 * <tr>
	 * <td align=center>"_to"</td>
	 * <td><tt>["to"]</tt></td>
	 * </tr>
	 * </table> </blockquote>
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<String> getConstituentTokens();

}

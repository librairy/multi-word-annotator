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

import edu.mit.jsemcor.term.ICategory;

/**
 * Indicates a semantic tag attached to an {@link IWordform} object from a
 * Context file.
 * 
 * @author M.A. Finlayson
 * @version 1.57, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public interface ISemanticTag extends IElement {

	/**
	 * The percent character, which separates the lemma and the lexical sense in
	 * a sense key.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static char PERCENT = '%';

	/**
	 * The semicolon character, which separates multiple word sense and lexical
	 * sense string in the case of multiple senses for an individual word.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static char SEMICOLON = ';';

	/**
	 * The attribute key for the lemma in semantic tags.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static String ATTR_LEMMA = "lemma";

	/**
	 * The attribute key for the wordnet sense string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static String ATTR_WNSN = "wnsn";

	/**
	 * The attribute key for the lexical sense string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static String ATTR_LEXSN = "lexsn";

	/**
	 * The value of the lemma attribute for this semantic tag. The base form of
	 * the word or collocation to which the other attribute/value pairs in this
	 * wordform pertain. This is the form of the string used to search the
	 * WordNet database. If a redefinition is present, lemma is the base form of
	 * the redefinition. When the proper noun property is present, the
	 * redefinition, lemma and category properties all have the same value.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public String getLemma();

	/**
	 * The sense number may contain more than one lexical sense string, each
	 * separated by a semicolon. Each entry is the integer value of the sense
	 * number string. This method ever returns <code>null</code> or an empty
	 * list. The returned value always contains at least one non-null, non-empty
	 * string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<Integer> getSenseNumber();

	/**
	 * The value of the lexical sense attribute. The lexical sense may contain
	 * more than one lexical sense string, each separated by a semicolon. When a
	 * lexical sense string is concatenated onto lemma using the percent
	 * character, this creates a sense_key that indicates the WordNet sense to
	 * which word should be linked. This is the semantic tag for word. This
	 * method never returns <code>null</code> or an empty list. The returned
	 * value always contains at least one non-null, non-empty string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<String> getLexicalSense();

	/**
	 * Indicates that word is a proper noun categorized as one of the values of
	 * person, location, group, or other. When the proper noun property is
	 * present, the redefinition, lemma and category values all have the same
	 * value.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public ICategory getProperNounCategory();

	/**
	 * Returns the sense key of this semantic tag, which is lemma%lexsn. Never
	 * returns <code>null</code> or an empty list. The returned value always
	 * contains at least one non-null, non-empty string.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public List<String> getSenseKeys();

}

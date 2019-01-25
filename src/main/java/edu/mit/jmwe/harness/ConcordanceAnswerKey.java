/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jmwe.data.AbstractMWEDesc;
import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc.IPart;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEComparator;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.data.RootMWEDesc;
import edu.mit.jmwe.data.concordance.IConcordanceSentence;
import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.ISemanticTag;
import edu.mit.jsemcor.element.ISentence;
import edu.mit.jsemcor.element.IWordform;
import edu.mit.jsemcor.main.IConcordance;

/**
 * Default implementation of the <code>IAnswerKey</code> interface. Searches for
 * the answer multi-word expressions in an {@link IConcordanceSentence} by using a
 * Semcor corpus, which has multi-word expressions annotated.
 * <p>
 * This class requires JSemcor to be on the classpath.
 * 
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: ConcordanceAnswerKey.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class ConcordanceAnswerKey implements IAnswerKey {

	/**
	 * A compiled regular expression pattern that captures the string
	 * representation of a Semcor sentence ID.
	 * 
	 * Pattern: <b>(\\S+?)/(\\S+?)/(\\d+)</b>
	 * <ol>
	 * <li><b>(\\S+?)/</b> group 1, concordance name</li>
	 * <li><b>(\\S+?)/</b> group 2, context name</li>
	 * <li><b>(\\d+)</b> group 3, sentence number</li>
	 * </ol>
	 * 
	 * @since jMWE 1.0.0
	 */
	public final static Pattern condordanceSentenceIDPattern = Pattern.compile("(\\S+?)/(\\S+?)/(\\d+)");
	
	/**
	 * A compiled regular expression pattern that captures the string
	 * representation of sense key. ss_type:lex_filenum:lex_id:head_word:head_id
	 * 
	 * Pattern: <b>(\\d):(\\d\\d):(\\d\\d):?:((\\S+):(\\d\\d))?</b>
	 * <ol>
	 * <li><b>(\\d):</b> group 1, synset type, is a one digit decimal integer
	 * representing the synset type for the sense</li>
	 * <li><b>(\\d\\d):</b> group 2, lex_filenum, is a two digit decimal integer
	 * representing the name of the lexicographer file containing the synset for
	 * the sense</li>
	 * <li><b>(\\d\\d):</b> group 3, lex_id is a two digit decimal integer that,
	 * when appended onto lemma , uniquely identifies a sense within a
	 * lexicographer file</li>
	 * <li><b>?:((\\S+):(\\d\\d))?</b> group 4 and 5, head_word and head_id,
	 * may or may not occur</li>
	 * </ol>
	 * 
	 * @since jMWE 1.0.0
	 */
	public final static Pattern lexSensePattern = Pattern.compile("(\\d):(\\d\\d):(\\d\\d):?:((\\S+):(\\d\\d))?");
	
	// instance field set on construction
	private final Map<String, IConcordance> concords;
	
	// dynamic instance fields
	private boolean ignoreProperNouns = false;
	
	/**
	 * Constructs an answer key from a single concordance
	 *
	 * @param c
	 *            the concordance that backs this answer key. May not be
	 *            <code>null</code>.
	 * @since jMWE 1.0.0
	 */
	public ConcordanceAnswerKey(IConcordance c){
		this(Arrays.asList(c));
	}
	
	/**
	 * Constructs an answer key from the given semcor concordance set.
	 * 
	 * @param i
	 *            the set of concordances that backs this answer key. May not be
	 *            <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified concordance set is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ConcordanceAnswerKey(Iterable<? extends IConcordance> i){
		Map<String, IConcordance> concords = new HashMap<String, IConcordance>();
		for(IConcordance c : i)
			concords.put(c.getName(), c);
		this.concords = concords;
	}
	
	/**
	 * Constructs an answer key from the given semcor concordance set.
	 * 
	 * @param concords
	 *            the semcor concordance that backs this answer key. May not be
	 *            <code>null</code>.
	 * @throws NullPointerException
	 *             if the specified concordance set is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public ConcordanceAnswerKey(Map<String, IConcordance> concords){
		if(concords == null)
			throw new NullPointerException();
		this.concords = concords;
	}

	/**
	 * Returns <code>true</code> if this answer key includes proper nouns in its
	 * results; <code>false</code> otherwise
	 * 
	 * @return <code>true</code> if this answer key includes proper nouns in its
	 *         results; <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	public boolean isIgnoringProperNouns() {
		return ignoreProperNouns;
	}

	/**
	 * Sets the flag that, if <code>true</code>, determines that the answer key
	 * will include proper nouns in its results.
	 * 
	 * @param ignoreProperNouns
	 *            <code>true</code> if this answer key should include proper
	 *            nouns in its results; <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	public void setIgnoreProperNouns(boolean ignoreProperNouns) {
		this.ignoreProperNouns = ignoreProperNouns;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.answer.IAnswerKey#getAnswers(edu.mit.jmwe.data.IMarkedSentence)
	 */
	public <T extends IToken> List<IMWE<T>> getAnswers(IMarkedSentence<T> sent) {
		ISentence answers = getSentence(concords, sent);
		return getAnswers(sent, answers);
	}

	/**
	 * Extracts a set of MWE answers from a sentence and its corresponding
	 * answer sentence.
	 * 
	 * @param <T>
	 *            the token type
	 * @param sent
	 *            the sentence for which answers are needed
	 * @param answers
	 *            the answers
	 * @return a list of MWEs that are ground truth for this sentence
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken> List<IMWE<T>> getAnswers(IMarkedSentence<T> sent, ISentence answers){
		if(sent == null)
			throw new NullPointerException();
		if(answers == null)
			throw new NullPointerException();
		
		// extract collocations
		Set<IWordform> used = new HashSet<IWordform>(answers.getWordList().size());
		
		List<IMWE<T>> mwes = getNonContinuousMWEs(sent, answers, used);
		List<IMWE<T>> adjMwes = getContinuousMWEs(sent, answers, used);
		
		// contruct result list
		List<IMWE<T>> result = new ArrayList<IMWE<T>>(adjMwes.size() + mwes.size());
		result.addAll(adjMwes);
		result.addAll(mwes);
		Collections.sort(result, new MWEComparator<T>(sent));
		return result;
	}

	/**
	 * Gets the multi-word expressions from the given sentence that are
	 * non-contiguous (e.g., have a distance value not equal to zero).
	 * 
	 * @param <T>
	 *            the token type
	 * @param sent
	 *            the unit for which the answers are being constructed
	 * @param answer
	 *            the semcor sentence from which the multi-token MWEs should be
	 *            extracted
	 * @param used
	 *            the set of wordforms already used
	 * @return a non-null, possible empty list of multi-word expressions found
	 *         in the given unit that are marked by distance coordinates
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken> List<IMWE<T>> getNonContinuousMWEs(IMarkedSentence<T> sent, ISentence answer, Set<IWordform> used){
		
		if(sent == null)
			throw new NullPointerException();
		
		List<List<IWordform>> nonContMWEs = answer.getCollocations();
		
		// assemble map of wordforms to their starting index
		IWordform wf;
		int unitIdx = 0;
		Map<IWordform, Integer> idxMap = new HashMap<IWordform, Integer>(answer.size());
		for(edu.mit.jsemcor.element.IToken answerToken : answer){
			if(answerToken instanceof IWordform) {
				wf = (IWordform)answerToken;
				idxMap.put(wf, unitIdx);
				unitIdx += wf.getConstituentTokens().size();
			} else {
				unitIdx++;
			}
		}
		
		List<IMWE<T>> results = new ArrayList<IMWE<T>>(nonContMWEs.size());
		
		// assemble list of answers
		MWEPOS pos = null;
		String lemma;
		
		List<T> parts = new ArrayList<T>();
		IRootMWEDesc mweDesc;
		Map<T, IPart> partMap = new LinkedHashMap<T, IPart>();
		ISemanticTag semTag = null;
		T token;
//		String partlemma;
		for(List<IWordform> cs : nonContMWEs){
			semTag = null;
			parts.clear();
			partMap.clear();
			// int j = 0;
			for(Iterator<IWordform> i = cs.iterator(); i.hasNext(); ){
				wf = i.next();
				if(wf.getConstituentTokens().size() > 1) 
					used.add(wf);
				
				if(wf.getSemanticTag() != null){
					if(semTag != null) 
						throw new IllegalStateException();
					semTag = wf.getSemanticTag();
				}
				token = sent.get(idxMap.get(wf));
				parts.add(token);
				// Part may not have right lemma?
				// partlemma  = isIllformattedLemma(wf.getSemanticTag()) ? wf.getText().toLowerCase(): wf.getSemanticTag().getLemma().split("_")[j++];
				partMap.put(token, null);
			}
			
			// figure out lemma
			if(isIllformattedLemma(semTag)){
				StringBuilder sb = new StringBuilder();
				for(Iterator<IWordform> i = cs.iterator(); i.hasNext(); ){
					sb.append(i.next().getText().toLowerCase());
					if(i.hasNext()) 
						sb.append('_');
				}
				lemma = sb.toString();
			} else {
				lemma = semTag.getLemma();
			}
			
			// figure out part of speech
			if(semTag == null){
				for(Iterator<IWordform> i = cs.iterator(); i.hasNext(); ){
					wf = i.next();
					if(wf.getPOSTag() != null){
						if(pos != null && !pos.equals(MWEPOS.toMWEPOS(wf.getPOSTag().getValue()))){
							pos = disambiguatePOS(cs);
							if(pos == null)
								throw new IllegalStateException();
						} else {
							pos = MWEPOS.toMWEPOS(wf.getPOSTag().getValue());
						}
					}
				}
			} else {
				pos = getMWEPOS(semTag.getLexicalSense().get(0));
			}
			
			// construct description object and fill in parts
//				mweDesc = index.getRootMWEDesc(lemma, pos);
			mweDesc = new RootMWEDesc(lemma, pos);
			int i = 0;
			for(Entry<T, IPart> e : partMap.entrySet())
				e.setValue(mweDesc.getParts().get(i++));
			
			if(ignoreProperNouns && pos == MWEPOS.PROPER_NOUN)
				continue;
				
			results.add(new MWE<T>(partMap));
		}
		
		return results;
	}

	/**
	 * Gets the multi-word expressions from the given sentence that are marked
	 * as single tokens.
	 * 
	 * @param <T>
	 *            the token type
	 * @param sent
	 *            the unit for which the answers are being constructed
	 * @param answer
	 *            the semcor sentence from which the single-token MWEs should be
	 *            extracted
	 * @param used
	 *            the set of wordforms already used
	 * @return a non-null, possible empty list of multi-word expressions found
	 *         in the given unit that are marked as a single token
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected <T extends IToken> List<IMWE<T>> getContinuousMWEs(IMarkedSentence<T> sent, ISentence answer, Set<IWordform> used){
		
		if(sent == null)
			throw new NullPointerException();
		
		// return this to caller
		List<IMWE<T>> results = new LinkedList<IMWE<T>>();
		
		// loop variables
		int tokenIdx = 0;
		IWordform wf;
		Map<T, IPart> partMap;
		MWEPOS mwePOS;
		int numParts;
		StringBuilder backupLemma;
		String[] lemmaParts;
		String mweLemma;
		IRootMWEDesc mweDesc;
		
		for(edu.mit.jsemcor.element.IToken wordform : answer){

			// if not a wordform, ignore
			if(!(wordform instanceof IWordform)) {
				tokenIdx++;
				continue;
			}
			wf = (IWordform)wordform;
			
			// if it's not a multi-part wordform, ignore
			if(wf.getConstituentTokens().size() <= 1){
				tokenIdx++;
				continue;
			}
			
			//if it has already been counted as part of a discontinuous collocation, ignore
			if(used.contains(wf)){
				tokenIdx++;
				continue;
			}
			
			// assemble the list of tokens involved in this MWE
			backupLemma = new StringBuilder();
			partMap = new LinkedHashMap<T, IPart>();
			
			T token;
			for(Iterator<String> i = wf.getConstituentTokens().iterator(); i.hasNext(); ){
				token = sent.get(tokenIdx); 
				partMap.put(token, null);
				
				// create backup lemma
				backupLemma.append(i.next().toLowerCase());
				if(i.hasNext())
					backupLemma.append("_");
				tokenIdx++;
			}
			
			// figure out MWE pos
			// if we are ignoring proper nouns, ignore it
			mwePOS = MWEPOS.toMWEPOS(wf.getPOSTag().getValue());
			if(ignoreProperNouns && mwePOS == MWEPOS.PROPER_NOUN)
				continue;
			
			// figure out MWE lemma
			numParts = wf.getConstituentTokens().size();
			if(wf.getSemanticTag() != null){
				lemmaParts = AbstractMWEDesc.underscore.split(wf.getSemanticTag().getLemma());
			} else {
				lemmaParts = new String[0];
			}
			if(isIllformattedLemma(wf.getSemanticTag()) || numParts != lemmaParts.length){
				mweLemma = backupLemma.toString();
			} else {
				mweLemma = wf.getSemanticTag().getLemma();
			}

			// create MWE description object
//			mweDesc = index.getRootMWEDesc(mweLemma, mwePOS);
			mweDesc = new RootMWEDesc(mweLemma, mwePOS);
			int i = 0;
			for(Entry<T, IPart> e : partMap.entrySet())
				e.setValue(mweDesc.getParts().get(i++));
			
			// add to results
			results.add(new MWE<T>(partMap));
		}
		
		return results;
	}

	/**
	 * Given the lexical sense of a word form, extracts the one digit decimal
	 * integer representing the synset type of the sense and returns the
	 * corresponding part of speech.
	 * 
	 * @param lexSense
	 * the lexical sense of a word form. 
	 * @return
	 * the part of speech corresponding to the synset type of the given sense
	 * @since jMWE 1.0.0
	 */
	protected MWEPOS getMWEPOS(String lexSense){
		
		Matcher matcher = lexSensePattern.matcher(lexSense);
		if(!matcher.matches()) return null;
		//get synset type for lexical sense
		int num = Integer.parseInt(matcher.group(1));

		switch(num){
    	case(1): return MWEPOS.NOUN;
    	case(2): return MWEPOS.VERB;
    	case(4): return MWEPOS.ADVERB;
    	case(5): //adjective satellite
    	case(3): return MWEPOS.ADJECTIVE;
    	}
        return null;

	}

	/**
	 * Attempts to disambiguate the part of speech of a multi-expression that
	 * does not have a semantic tag and whose parts are labeled with different
	 * part of speech tags. Will check for the case in which the first part is a
	 * verb and the second part of the multi-word expression is a preposition.
	 * In that case, will return <code>MWEPOS.VERB</code>. Otherwise, returns
	 * <code>null</code>.
	 * 
	 * @param mwe
	 *            the set of wordforms in the MWE
	 * @return The best guess of the method, or <code>null</code> if none
	 * @since jMWE 1.0.0
	 */
	protected MWEPOS disambiguatePOS(List<IWordform> mwe){
		if(mwe.size()!= 2) return null;
		if(MWEPOS.toMWEPOS(mwe.get(0).getPOSTag().getValue()) == MWEPOS.VERB && mwe.get(1).getPOSTag().getValue().equals("IN"))
			return MWEPOS.VERB;
		return null;
	}

	/**
	 * Returns the concordance sentence that corresponds to the specified marked
	 * sentence
	 * 
	 * @param concords
	 *            the concordances which should be searched for the sentence
	 * @param sent
	 *            the sentence corresponding to the concordance sentence that
	 *            should be retrieved
	 * @return the retrieved sentence
	 * @throws IllegalArgumentException
	 *             if unable to find the sentence
	 * @since jMWE 1.0.0
	 */
	public static ISentence getSentence(Map<String, IConcordance> concords, IMarkedSentence<?> sent){
		// extract id
		Matcher matcher = condordanceSentenceIDPattern.matcher(sent.getID());
		if(!matcher.matches())
			throw new IllegalArgumentException("Unable to find parse sentence id " + sent.getID());
		String concordName = matcher.group(1);
		String contextName = matcher.group(2);
		int sentNum = Integer.parseInt(matcher.group(3));
		
		// get concordance
		IConcordance concord = concords.get(concordName); 
		if(concord == null)
			throw new IllegalArgumentException("Unable to find answer condcordance for " + sent.getID());
		
		// get context
		IContext context = concord.getContext(contextName);
		if(context == null)
			throw new IllegalArgumentException("Unable to find answer context for " + sent.getID());
		
		// get sentence
		ISentence answers = context.getSentence(sentNum);
		if(answers == null)
			throw new IllegalArgumentException("Unable to find answer sentence for " + sent.getID());
	
		return answers;
	}

	/**
	 * Returns true if the semantic tag of a multi-word expression is null, tags
	 * a proper noun, or if the lemma encoded in the semantic tag is not
	 * formatted properly, that is, with underscores separating the parts of the
	 * multi-word expression.
	 * 
	 * @param tag
	 *            the semantic tag of a wordform that is a part of a multi-word
	 *            expression.
	 * @return true if the semantic tag of a multi-word expression is null, tags
	 *         a proper noun, or if the lemma encoded in the semantic tag is not
	 *         formatted with underscores separating the parts of the multi-word
	 *         expression.
	 * @since jMWE 1.0.0
	 */
	protected static boolean isIllformattedLemma(ISemanticTag tag){
		if(tag == null)
			return true;
		if(tag.getProperNounCategory() != null)
			return true;
		if(AbstractMWEDesc.splitOnUnderscores(tag.getLemma()).size() < 2)
			return true;
		return false;
	}

}

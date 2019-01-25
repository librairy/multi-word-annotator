/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.detect;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEPOS;

/**
 * Enumerates the ways in which MWEs with certain parts of speech are allowed to inflect.
 * All but the last two rules are specified in Arranz, Asterias and Castillo 2005.
 *
 * @author N. Kulkarni
 * @version $Id: InflectionRule.java 327 2011-05-08 21:13:58Z markaf $
 * @since jMWE 1.0.0
 */
public enum InflectionRule implements IInflectionRule {
	
	// nouns
	// NN NN? NN? [NN] 	access road
	R1("(NNS?)_(NNS?_)?(NNS?_)?(NNS?)",MWEPOS.NOUN, 4),
	// [NN] IN DT? NN 	pain in the neck
	R2("(NNS?)_(IN)_(DT_)?(NNS?)",MWEPOS.NOUN,1),
	// NN POS [NN] 		arm's length
	R3("(NNS?)_(POS)_(NNS?)",MWEPOS.NOUN,3),
	// JJ [NN] 			Analytical Cubism
	R4("(JJ[RS]?)_(NNS?)",MWEPOS.NOUN,2),
	// [NN] IN JJ [NN] 	balance of international payments
	R5("(NNS?)_(IN)_(JJ[RS]?)_(NNS?)",MWEPOS.NOUN,1,4),
	// IN [NN] 			anti-Catholicism
	R6("(IN)_(NNS?)",MWEPOS.NOUN,2),
	// [NN] CC [NN] 	bread and butter or nooks and crannies
	R7("(NNS?)_(CC)_(NNS?)",MWEPOS.NOUN,1,3),
	
	
	// verbs
	// [VB] IN (RB |IN)? (RB|IN)?	allow for
	R8("(VB[DGNPZ]?)_(IN)(_W?RB[RS]?)?(_W?RB[RS]?)?",MWEPOS.VERB,1),
	R9("(VB[DGNPZ]?)_(IN)(_IN)?(_W?RB[RS]?)?",MWEPOS.VERB,1),
	R10("(VB[DGNPZ]?)_(IN)(_W?RB[RS]?)?(_IN)?",MWEPOS.VERB,1),
	R11("(VB[DGNPZ]?)_(IN)(_IN)?(_IN)?",MWEPOS.VERB,1),
	// [VB] RB (RB |IN)? (RB|IN)?	bear down on
	R12("(VB[DGNPZ]?)_(W?RB[RS]?)(_W?RB[RS]?)?(_W?RB[RS]?)?",MWEPOS.VERB,1),
	R13("(VB[DGNPZ]?)_(W?RB[RS]?)(_IN)?(_W?RB[RS]?)?",MWEPOS.VERB,1),
	R14("(VB[DGNPZ]?)_(W?RB[RS]?)(_W?RB[RS]?)?(_IN)?",MWEPOS.VERB,1),
	R15("(VB[DGNPZ]?)_(W?RB[RS]?)(_IN)?(_IN)?",MWEPOS.VERB,1),
	// [VB] TO VB 	bring to bear
	R16("(VB[DGNPZ]?)_(TO)_(VB[DGNPZ]?)",MWEPOS.VERB,1),
	// [VB] JJ		break loose
	R17("(VB[DGNPZ]?)_(JJ[RS]?)",MWEPOS.VERB,1),
	// [VB] IN? (DT |PRP$)? [NN]		take a breath
	R18("(VB[DGNPZ]?)_(IN_)?(DT_)?(NNS?)",MWEPOS.VERB,1,4),
	R19("(VB[DGNPZ]?)_(IN_)?(PRP\\$_)?(NNS?)",MWEPOS.VERB,1,4),
	// verb particles, not in Arranz, Asterias and Castillo 2005
	R20("(VB[DGNPZ]?)_(TO)",MWEPOS.VERB,1),
	R21("(VB[DGNPZ]?)_(RP)",MWEPOS.VERB,1);
	
	// final instance fields
	private final Pattern pattern;
	private final List<Integer> infIdxs;
	private final MWEPOS pos;
	private transient String toString;
	
	// private constructor
	private InflectionRule(String regex, MWEPOS pos, Integer...indicies){
		pattern = Pattern.compile(regex);
		infIdxs = Arrays.asList(indicies);
		Collections.sort(infIdxs);
		this.pos = pos;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.inflect.IInflectionRule#isValid(edu.mit.jmwe.data.IMWE)
	 */
	public <T extends IToken> boolean isValid(IMWE<T> mwe) {
		Matcher matcher = pattern.matcher(getTagPattern(mwe));
		//if it doesn't match the syntactic pattern, the rule cannot apply
		if(!matcher.matches()){
			System.err.println("MWE does not match this rule's syntax. Cannot apply rule");
			throw new IllegalArgumentException();
		}
		//mwe index
		int j = 0;
		for(int i = 1; i <= matcher.groupCount(); i++){
			if(matcher.group(i) == null) continue;
			if(inflects(mwe.getTokens().get(j),mwe)){
				if(!infIdxs.contains(i)){
					return false;
				}
			}
			j++;
		}
		//expression is valid for the whole rule
		return true;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.detect.inflect.IInflectionRule#matches(edu.mit.jmwe.data.IMWE)
	 */
	public <T extends IToken> boolean matches(IMWE<T> mwe) {
		if(mwe.getEntry().getPOS() != pos)
			return false;
		Matcher matcher = pattern.matcher(getTagPattern(mwe));
		return matcher.matches();
	}
	
	/**
	 * Concatenates the tags of each token in the MWE, separating each by
	 * underscores. The resulting string can be checked against the rules.
	 * 
	 * @param <T>
	 *            the type of tokens in the MWE
	 * @param mwe
	 *            the MWE whose tags are being concatenated
	 * @return the tags of each token in the MWE, separated by underscores
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken>String getTagPattern(IMWE<T> mwe){
		StringBuilder sb = new StringBuilder();
		for(Iterator<T> itr = mwe.getTokens().iterator(); itr.hasNext();){
			 sb.append(itr.next().getTag());
			 if(itr.hasNext())
				 sb.append("_");
		}
		return sb.toString();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString(){
		if(toString == null){
			StringBuilder sb = new StringBuilder();
			sb.append(name());
			sb.append('(');
			sb.append(pos.getIdentifier());
			sb.append(')');
			sb.append(':');
			sb.append(pattern.pattern());
			toString = sb.toString();
		}
		return toString;
	}
	
	/**
	 * Returns true if a the text of a token from an MWE does not equal the
	 * corresponding part lemma. In other words, checks if the token is
	 * inflected relative to the base lemma of the MWE.
	 * 
	 *@param <T>
	 *            the type of tokens in the MWE
	 * @param token
	 *            the token being checked
	 * @param mwe
	 *            the MWE from which the token is drawn
	 * @return true if a the text of a token from an MWE does not equal the
	 *         corresponding part lemma., false otherwise.
	 * @since jMWE 1.0.0
	 */
	public static <T extends IToken> boolean inflects(T token, IMWE<T> mwe){
		String partlemma = mwe.getPartMap().get(token).getForm();
		return !token.getForm().equalsIgnoreCase(partlemma);
			
	}

	/**
	 * Returns <code>true</code> if and only if (1) the given multi-word
	 * expressions syntactically matches a rule listed in the enumeration
	 * {@link InflectionRule} and (2) parts inflect according to that rule
	 * 
	 * @param mwe
	 *            the multi-word expression to be tested
	 * @return <code>true</code> if the given multi-word expression inflects
	 *         according to the rule it matches or if it does not match any
	 *         rule; <code>false</code> otherwise.
	 * 
	 * @since jMWE 1.0.0
	 */
	public static boolean isInflectedByPattern(IMWE<?> mwe){
		for(InflectionRule r : InflectionRule.values())
			if(r.matches(mwe) && r.isValid(mwe))
				return true;
		return false;
	}

	/**
	 * Returns <code>true</code> if the specified MWE inflects according to some
	 * rule in the specified collection; <code>false</code> otherwise.
	 * 
	 * @param mwe
	 *            the mwe to check
	 * @param rules
	 *            the set of rules to use; may not be <code>null</code>, but may
	 *            be empty
	 * @return <code>true</code> if the specified MWE inflects according to some
	 *         rule in the specified collection; <code>false</code> otherwise.
	 * @since jMWE 1.0.0
	 */
	public static boolean isInflectedByPattern(IMWE<?> mwe, Collection<? extends IInflectionRule> rules){
		for(IInflectionRule r : rules)
			if(r.matches(mwe) && r.isValid(mwe))
				return true;
		return false;
	}
	
}

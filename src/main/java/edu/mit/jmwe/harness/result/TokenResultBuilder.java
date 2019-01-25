/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.harness.result;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.IMarkedSentence;
import edu.mit.jmwe.data.MWEPOS;

/**
 * A result builder that keeps track of token-level results.
 * 
 * @author N. Kulkarni
 * @version $Id: TokenResultBuilder.java 321 2011-05-06 23:44:50Z markaf $
 * @since jMWE 1.0.0
 */
public class TokenResultBuilder<T extends IToken, U extends IMarkedSentence<T>> extends MWEResultBuilder<T, U> {

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jmwe.harness.IResultBuilder#process(java.util.List, java.util.List)
	 */
	@Override
	public void process(List<IMWE<T>> found, List<IMWE<T>> answers) {
		
		MWEPOS pos;
		Set<IMWE<T>> truePos = new HashSet<IMWE<T>>();
		
		//identify true positives. Log found and correct data.
		for(IMWE<T> mwe: found){
			for(T token : mwe.getTokens()){
				pos = MWEPOS.toMWEPOS(token.getTag());
				foundData.get(pos).incrementAndGet();
				if (answers.contains(mwe)){
					if(!truePos.contains(mwe))
						truePos.add(mwe);
					correctData.get(pos).incrementAndGet();
				}
			}
		}
		
		//log answer data and initialize false negative map
		for(IMWE<T> amwe: answers) {
			for(T token : amwe.getTokens()){
				pos = MWEPOS.toMWEPOS(token.getTag());
				answerData.get(pos).incrementAndGet();
			}
		}
	}
}

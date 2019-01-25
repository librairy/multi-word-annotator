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

import edu.mit.jmwe.harness.IAnswerKey;

/**
 * A marked sentence is a sentence (i.e., a list of tokens) that has been tagged
 * with a unique id. The purpose of marking the sentence with an id is so that,
 * when an MWE detector has been run over the sentence, the correct answers may
 * be retrieved from an {@link IAnswerKey} object. If all you want to do is
 * construct a sentence on which to run a detector, without referencing an
 * answer key, you may just use a normal {@link List}.
 * 
 * 
 * @param <T>
 *            the type of tokens contained in the unit
 * @author N. Kulkarni
 * @version $Id: IMarkedSentence.java 319 2011-05-06 20:22:54Z markaf $
 * @since jMWE 1.0.0
 */
public interface IMarkedSentence<T extends IToken> extends IHasID, List<T> {

}

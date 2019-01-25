/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import edu.mit.jsemcor.term.ICommand;
import edu.mit.jsemcor.term.INote;
import edu.mit.jsemcor.term.IOtherTag;
import edu.mit.jsemcor.term.IPOSTag;

/**
 * Concrete, default implementation of the IWordform interface.
 * 
 * @author M.A. Finlayson
 * @version 1.62, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Wordform implements IWordform {
	
	public static final Pattern underscorePattern = Pattern.compile(UNDERSCORE_STR);

	// final data
	private final ICommand command;
	private final int wordNum, tokIdx, distance;
	private final String redefinition, sepStr, text;
	private final IPOSTag posTag;
	private final ISemanticTag semTag;
	private final IOtherTag otherTag;
	private final INote note;
	
	private List<String> tokens = null;
	private String data = null;

	/**
	 * Constructs a new Wordform object out of the specified arguments.
	 * 
	 * @throws NullPointerException
	 *             if either the command, pos tag, or text string are
	 *             <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the token index is less than zero, or the redefinition
	 *             string is not null but empty or whitespace, or the text is
	 *             empty or all whitespace
	 * 
	 * @since JSemcor 1.0.0
	 */
	public Wordform(int wordNum, int tokIdx, ICommand cmd, int distance, String redef, String sepStr, IPOSTag posTag, ISemanticTag semTag, IOtherTag otherTag, INote note, String text){
		if(cmd == null || posTag == null) throw new NullPointerException();
		if(tokIdx < 0) throw new IllegalArgumentException();
		if(redef != null && redef.trim().length() == 0) throw new IllegalArgumentException();
		if(text.trim().length() == 0) throw new IllegalArgumentException();
		
		checkWordNumber(wordNum);
		
		this.wordNum = wordNum;
		this.tokIdx = tokIdx;
		this.command = cmd;
		this.distance = distance;
		this.redefinition = redef;
		this.sepStr = sepStr;
		this.posTag = posTag;
		this.semTag = semTag;
		this.otherTag = otherTag;
		this.note = note;
		this.text = text;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IToken#getText()
	 */
	public String getText() {
		return text;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getNumber()
	 */
	public int getNumber() {
		return wordNum;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getTokenNumber()
	 */
	public int getTokenIndex() {
		return tokIdx;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getCommand()
	 */
	public ICommand getCommand() {
		return command;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getDistance()
	 */
	public int getDistance() {
		return distance;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getSeparatorString()
	 */
	public String getSeparatorString() {
		return sepStr;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getPOSTag()
	 */
	public IPOSTag getPOSTag() {
		return posTag;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getRedefinition()
	 */
	public String getRedefinition() {
		return redefinition;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getSemanticTag()
	 */
	public ISemanticTag getSemanticTag() {
		return semTag;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getOtherTag()
	 */
	public IOtherTag getOtherTag() {
		return otherTag;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getTagNote()
	 */
	public INote getTagNote() {
		return note;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IWordform#getConstituentTokens()
	 */
	public List<String> getConstituentTokens() {
		if(tokens == null) tokens = createTokenList(getText());
		return tokens;
	}
	
	/**
	 * Constructs a list of tokens from a text string. Constructs the list so
	 * that it is consistent with the specification of the
	 * {@link #getConstituentTokens()} method.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected List<String> createTokenList(String text){
		
		String[] tokens = underscorePattern.split(text);
		
		// make sure there aren't any empties
		int emptyStrs = 0;
		for(String token : tokens) if(token.length() == 0) emptyStrs++;
		
		// make inner list
		List<String> hidden;
		if(emptyStrs > 0){ 
			hidden = new ArrayList<String>(tokens.length-emptyStrs);
			for(String token : tokens) if(token.length() > 0) hidden.add(token);
		} else {
			hidden = Arrays.asList(tokens);
		}
		
		return Collections.unmodifiableList(hidden);
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if(data == null){
			StringBuilder sb = new StringBuilder();
			sb.append(TAG_WF_START);
			sb.append(SPACE);
			sb.append(command.getData());
			
			if(redefinition != null){
				sb.append(SPACE);
				sb.append(ATTR_RDF);
				sb.append(EQUALS);
				sb.append(redefinition);
			}
			
			if(distance != 0){
				sb.append(SPACE);
				sb.append(ATTR_DC);
				sb.append(EQUALS);
				sb.append(distance);
			}
			
			if(sepStr != null){
				sb.append(SPACE);
				sb.append(ATTR_SEP);
				sb.append(EQUALS);
				sb.append(OPEN_QUOTE);
				sb.append(sepStr);
				sb.append(CLOSE_QUOTE);
			}
			
			sb.append(SPACE);
			sb.append(posTag.getData());
			
			if(semTag != null){
				sb.append(SPACE);
				sb.append(semTag.getData());
			}
			
			if(otherTag != null){
				sb.append(SPACE);
				sb.append(otherTag.getData());
			}
			
			if(note != null){
				sb.append(SPACE);
				sb.append(otherTag.getData());
			}
			
			sb.append(RIGHT_ANGLE_BRACKET);
			sb.append(text);
			sb.append(TAG_WF_END);
			
			data = sb.toString();
		}
		return data;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getData() + "(" + getNumber() + ")";
	}

	/**
	 * Returns whether the specified integer is a valid sentence number. Valid
	 * sentence numbers are strictly greater than zero.
	 * 
	 * @return <code>true</code> if the specifed number is a legal sentence
	 *         number, namely, if it is greater than or equal to 1;
	 *         <code>false</code> otherwise.
	 * @since JSemcor 1.0.0
	 */
	public static boolean isIllegalWordNumber(int number){
		return number < 1;
	}
	
	/**
	 * Throws an {@link IllegalArgumentException} if the specified number is not
	 * a valid word number. Valid sentence numbers are strictly greater than
	 * zero.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specifed number is not a legal sentence number,
	 *             namely, if it is less than 1.
	 * @since JSemcor 1.0.0
	 */
	public static void checkWordNumber(int number){
		if(isIllegalWordNumber(number)) throw new IllegalArgumentException();
	}

}

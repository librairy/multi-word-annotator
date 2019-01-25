/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default, concrete implementation of {@link IPunc}, with all punctuation
 * listed in the Semcor documentation, plus a few extras not mentioned, e.g.,
 * {@link #HYPHEN}, {@link #PERCENT}, {@link #STAR}, {@link #PLUS}, and
 * {@link #AMPERSAND}. This class is not implemented as an {@link Enum} so that
 * clients can instantiate their own instances of this class without
 * re-implementing the interface.
 * 
 * @author M.A. Finlayson
 * @version 1.60, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Punc implements IPunc {

	public static final Punc COMMA 			= new Punc("Comma", 				',', ",");
	public static final Punc PERIOD 		= new Punc("Period",				'.', ".");
	public static final Punc QUESTION_MARK 	= new Punc("Question Mark", 		'?', "?");
	public static final Punc EXCLAMATION_PT = new Punc("Exlamation Point", 		'!', "!");
	public static final Punc SEMICOLON 		= new Punc("Semicolon", 			';', ";");
	public static final Punc LEFT_PAREN 	= new Punc("Left Parenthesis", 		'(', "(");
	public static final Punc RIGHT_PAREN 	= new Punc("Right Parenthesis", 	')', ")");
	public static final Punc LEFT_BRACKET 	= new Punc("Left Bracket", 			'[', "[");
	public static final Punc RIGHT_BRACKET 	= new Punc("Right Bracket", 		']', "]");
	public static final Punc APOSTROPHE 	= new Punc("Apostrophe", 			'\'',"'");
	public static final Punc OPEN_QUOTE 	= new Punc("Open Quote", 			'"', "``");
	public static final Punc CLOSED_QUOTE 	= new Punc("Closed Quote", 			'"', "''");
	public static final Punc DOLLAR_SIGN 	= new Punc("Dollar Sign", 			'$', "$");
	public static final Punc COLON 			= new Punc("Colon", 				':', ":");
	public static final Punc HYPHEN 		= new Punc("Hyphen", 				'-', "-");
	public static final Punc SLASH 			= new Punc("Slash", 				'/', "/");
	public static final Punc DEGREES		= new Punc("Degrees", 				'`', "`");
	public static final Punc PERCENT		= new Punc("Percent", 				'%', "%");
	public static final Punc STAR			= new Punc("Star", 					'*', "*");
	public static final Punc PLUS			= new Punc("Plus", 					'+', "+");
	public static final Punc AMPERSAND		= new Punc("Ampersand", 			'&', "&");
	
	private final String name, text;
    private final char displayChar;
    private final String symbol;
    private String data;

    /**
	 * Constructs a new Category with the specified name.
	 * 
	 * @throws NullPointerException
	 *             if either string is null
	 * @throws IllegalArgumentException
	 *             if either string is empty or all whitespace
	 * @since JSemcor 1.0.0
	 */
    public Punc(String name, char displayChar, String symbol){
    	if(symbol == null || name == null) throw new NullPointerException();
    	if(symbol.trim().length() == 0 || name.trim().length() == 0) throw new IllegalArgumentException();
    	
    	this.name = name;
    	this.displayChar = displayChar;
    	this.text = Character.toString(displayChar);
    	this.symbol = symbol;
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see edu.mit.jsemcor.element.IToken#getText()
     */
    public String getText(){
    	return text;
    }
    
    /* 
     * (non-Javadoc)
     *
     * @see edu.mit.jsemcor.element.IPunc#getName()
     */
    public String getName(){
    	return name;
    }
    
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IPunc#getPunctuation()
	 */
	public char getDisplayCharacter() {
		return displayChar;
	}

	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IPunc#getSymbol()
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.element.IElement#getText()
	 */
	public String getData() {
		if(data == null){
			StringBuilder sb = new StringBuilder(TAG_PUNC_START.length() + symbol.length() + TAG_PUNC_END.length());
			sb.append(TAG_PUNC_START);
			sb.append(symbol);
			sb.append(TAG_PUNC_END);
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
		return getData();
	}

	/**
	 * The static private variable that is initialized by {@link #init()} and
	 * returned by a call to {@link #values()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, Punc> types;

	/**
	 * Creates the set of category types declared as static variables in this
	 * concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, Punc> init(){
		
		// get all the fields containing ContentType
		Field[] fields = Punc.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == Punc.class){
				instanceFields.add(field);
			}
		}
		
		// this is the Punc set
		Map<String, Punc> hidden = new LinkedHashMap<String, Punc>(instanceFields.size());
		
		// fill in the backing set
		Punc type;
		for(Field field : instanceFields){
			try{
				type = (Punc)field.get(null);
				if(type == null) continue;
				hidden.put(type.getSymbol(), type);
			} catch(IllegalAccessException e){
				// Ignore
			}
		}
		
		// make the value set unmodifiable
		return Collections.unmodifiableMap(hidden);
	}
	
	/**
	 * This functions emulates the function of the same name found on each
	 * {@link Enum} class. This method returns a static, unmodifiable collection
	 * containing all the instances of this type that are declared in static
	 * fields in this concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final Collection<Punc> values(){
		if(types == null) types = init();
		return types.values();
	}
	
    /**
	 * Returns the {@link Punc} object for the specified value, or
	 * <code>null</code> if there no static final instance for this value
	 * exists.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static final Punc getInstance(String symbol){
    	if(types == null) types = init();
    	return types.get(symbol);
    }
    
    /**
	 * Returns whether the specified token is the same as one of the static
	 * final sentence-final punctuation marks, i.e., the period, exclamation
	 * point, or question mark.
	 * 
	 * @return <code>true</code> if the specified token is equal to the
	 *         {@link #PERIOD}, {@link #EXCLAMATION_PT}, or
	 *         {@link #QUESTION_MARK} punctuation objects; <code>false</code>
	 *         otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isSentenceFinal(IToken p){
    	return p == PERIOD || p == EXCLAMATION_PT || p == QUESTION_MARK;
    }
    
    /**
	 * Returns whether the specified string is the same as one of the static
	 * final sentence-final punctuation mark strings, i.e., the period,
	 * exclamation point, or question mark.
	 * 
	 * @return <code>true</code> if the specified string is equal to the value
	 *         of the {@link #PERIOD}, {@link #EXCLAMATION_PT}, or
	 *         {@link #QUESTION_MARK} punctuation objects; <code>false</code>
	 *         otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isSentenceFinal(String p){
    	return p.equalsIgnoreCase(PERIOD.getText()) || p.equalsIgnoreCase(EXCLAMATION_PT.getText()) || p.equalsIgnoreCase(QUESTION_MARK.getText());
    }
    
    /**
	 * Returns whether the specified token is the same as one of the static
	 * final opening delimiter punctuation marks, i.e., the double-quote, left
	 * parenthesis, or left bracket.
	 * 
	 * @return <code>true</code> if the specified token is equal to the
	 *         {@link #OPEN_QUOTE}, {@link #LEFT_PAREN}, or
	 *         {@link #LEFT_BRACKET} punctuation objects; <code>false</code>
	 *         otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isOpeningDelimiter(IToken p){
    	return p == OPEN_QUOTE || p == LEFT_PAREN || p == LEFT_BRACKET;
    }
    
    /**
	 * Returns whether the specified token is the same as one of the static
	 * final opening delimiter punctuation mark strings, i.e., the double-quote,
	 * left parenthesis, or left bracket.
	 * 
	 * @return <code>true</code> if the specified string is equal to the value
	 *         of the {@link #OPEN_QUOTE}, {@link #LEFT_PAREN}, or
	 *         {@link #LEFT_BRACKET} punctuation objects; <code>false</code>
	 *         otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isOpeningDelimiter(String p){
    	return p.equalsIgnoreCase(OPEN_QUOTE.getText()) || p.equalsIgnoreCase(LEFT_PAREN.getText()) || p.equalsIgnoreCase(LEFT_BRACKET.getText());
    }
    
    /**
	 * Returns whether the specified token is the same as one of the static
	 * final closing delimiter punctuation marks, i.e., the double-quote, right
	 * parenthesis, or right bracket.
	 * 
	 * @return <code>true</code> if the specified token is equal to the
	 *         {@link #OPEN_QUOTE}, {@link #RIGHT_PAREN}, or
	 *         {@link #RIGHT_BRACKET} punctuation objects; <code>false</code>
	 *         otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isClosingDelimiter(IToken p){
    	return p == CLOSED_QUOTE || p == RIGHT_PAREN || p == RIGHT_BRACKET;
    }
    
    /**
	 * Returns whether the specified string is the same as one of the static
	 * final closing delimiter punctuation mark strings, i.e., the double-quote,
	 * right parenthesis, or right bracket.
	 * 
	 * @return <code>true</code> if the specified string is equal to the value
	 *         of the {@link #OPEN_QUOTE}, {@link #RIGHT_PAREN}, or
	 *         {@link #RIGHT_BRACKET} punctuation objects; <code>false</code>
	 *         otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isClosingDelimiter(String p){
    	return p.equalsIgnoreCase(CLOSED_QUOTE.getText()) || p .equalsIgnoreCase(RIGHT_PAREN.getText())|| p.equalsIgnoreCase(RIGHT_BRACKET.getText());
    }
    
    /**
	 * Returns whether the specified token is the same as one of the static
	 * final phrase delimiter punctuation marks, i.e., the comma, colon, or
	 * semicolon.
	 * 
	 * @return <code>true</code> if the specified token is equal to the
	 *         {@link #COMMA}, {@link #COLON}, or {@link #SEMICOLON}
	 *         punctuation objects; <code>false</code> otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isPhraseDelimiter(IToken p){
    	return p == COMMA || p == COLON || p == SEMICOLON;
    }
    
    /**
	 * Returns whether the specified string is the same as one of the static
	 * final phrase delimiter punctuation mark strings, i.e., the comma, colon,
	 * or semicolon.
	 * 
	 * @return <code>true</code> if the specified string is equal to the value
	 *         of the {@link #COMMA}, {@link #COLON}, or {@link #SEMICOLON}
	 *         punctuation objects; <code>false</code> otherwise.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static boolean isPhraseDelimiter(String p){
    	return p.equalsIgnoreCase(COMMA.getText()) || p.equalsIgnoreCase(COLON.getText()) || p.equalsIgnoreCase(SEMICOLON.getText());
    }

}

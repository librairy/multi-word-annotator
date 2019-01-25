/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.term;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default, concrete implementation of {@link IPOSTag}, with all pos tag types
 * listed in the Semcor documentation. The reason this class is not implemented
 * as an {@link Enum} is so that clients can instantiate their own instances of
 * this class without re-implementing the interface.
 *
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class POSTag extends Terminal implements IPOSTag {

	public static final POSTag CC 	= new POSTag("CC", 	"Coordinating conjunction");
	public static final POSTag CD 	= new POSTag("CD", 	"Cardinal number");
	public static final POSTag DT 	= new POSTag("DT", 	"Determiner");
	public static final POSTag EX 	= new POSTag("EX", 	"Existential \"there\"");
	public static final POSTag FW 	= new POSTag("FW", 	"Foreign word");
	public static final POSTag IN 	= new POSTag("IN", 	"Preposition or subordinating conjunction");
	public static final POSTag JJ 	= new POSTag("JJ", 	"Adjective");
	public static final POSTag JJR 	= new POSTag("JJR", "Adjective, comparative");
	public static final POSTag JJS 	= new POSTag("JJS", "Adjective, superlative");
	public static final POSTag LS 	= new POSTag("LS", 	"List item marker");
	public static final POSTag MD 	= new POSTag("MD", 	"Modal");
	public static final POSTag NN 	= new POSTag("NN", 	"Noun, singular or mass");
	public static final POSTag NNP 	= new POSTag("NNP", "Proper noun, singular");
	public static final POSTag NNPS = new POSTag("NNPS", "Proper noun, plural");
	public static final POSTag NNS 	= new POSTag("NNS", "Noun, plural");
	public static final POSTag NP 	= new POSTag("NP", 	"Proper noun, singular");
	public static final POSTag NPS 	= new POSTag("NPS", "Proper noun, plural");
	public static final POSTag PDT 	= new POSTag("PDT", "Predeterminer");
	public static final POSTag POS 	= new POSTag("POS", "Possessive ending");
	public static final POSTag PP 	= new POSTag("PP", 	"Personal pronoun");
	public static final POSTag PR 	= new POSTag("PR", 	"Pronoun");
	public static final POSTag PRP 	= new POSTag("PRP", "Pronoun, personal");
	public static final POSTag PRP$	= new POSTag("PRP$", "Pronoun, personal, plural");
	public static final POSTag RB	= new POSTag("RB", 	"Adverb");
	public static final POSTag RBR	= new POSTag("RBR", "Adverb, comparative");
	public static final POSTag RBS	= new POSTag("RBS", "Adverb, superlative");
	public static final POSTag RP	= new POSTag("RP",  "Particle");
	public static final POSTag SYM	= new POSTag("SYM", "Symbol");
	public static final POSTag TO	= new POSTag("TO",  "\"to\"");
	public static final POSTag UH	= new POSTag("UH",  "Interjection ");
	public static final POSTag VB	= new POSTag("VB",  "Verb, base form ");
	public static final POSTag VBD	= new POSTag("VBD", "Verb, past tense");
	public static final POSTag VBG	= new POSTag("VBG", "Verb, gerund or present participle");
	public static final POSTag VBN	= new POSTag("VBN", "Verb, past participle");
	public static final POSTag VBP	= new POSTag("VBP", "Verb, non-3rd person singular present");
	public static final POSTag VBZ	= new POSTag("VBZ", "Verb, 3rd person singular present");
	public static final POSTag WDT	= new POSTag("WDT", "Wh-determiner");
	public static final POSTag WP	= new POSTag("WP", 	"Wh-pronoun");
	public static final POSTag WP$	= new POSTag("WP$", "Possessive wh-pronoun");
	public static final POSTag WRB	= new POSTag("WRB", "Wh-adverb");

	private final String meaning;
	
	/**
	 * Constructs a new object of type {@link POSTag} with the specified
	 * parameters.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public POSTag(String value, String meaning) {
		super(ATTR_POS, value);
		if(meaning == null) throw new NullPointerException();
		this.meaning = meaning;
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.term.IPOSTag#getMeaning()
	 */
	public String getMeaning() {
		return meaning;
	}
    
	/**
	 * The static private variable that is initialized by {@link #init()} and
	 * returned by a call to {@link #values()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, POSTag> types;
	
	/**
	 * Creates the set of category types declared as static variables in this
	 * concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, POSTag> init(){
		
		// get all the fields containing ContentType
		Field[] fields = POSTag.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == POSTag.class){
				instanceFields.add(field);
			}
		}
		
		// this is the backing set
		Map<String, POSTag> hidden = new LinkedHashMap<String, POSTag>(instanceFields.size());
		
		// fill in the backing set
		POSTag type;
		for(Field field : instanceFields){
			try{
				type = (POSTag)field.get(null);
				if(type == null) continue;
				hidden.put(type.getValue(), type);
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
	public static final Collection<POSTag> values(){
		if(types == null) types = init();
		return types.values();
	}
	
    /**
	 * Returns the {@link POSTag} object for the specified name, or
	 * <code>null</code> if there is none.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static final POSTag getInstance(String value){
    	if(types == null) types = init();
    	return types.get(value);
    }
    
}

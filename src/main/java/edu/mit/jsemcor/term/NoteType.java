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
 * A default, concrete implementation of {@link INoteType}, with all note types
 * listed in the Semcor documentation. The reason this class is not implemented
 * as an {@link Enum} is so that clients can instantiate their own instances of
 * this class without re-implementing the interface.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class NoteType extends Terminal implements INoteType {

	public static final NoteType SENSE_MISSING 		= new NoteType("sns_miss");
	public static final NoteType INDISTINCT_SENSE 	= new NoteType("indist_sns");
	public static final NoteType WORD_MISSING 		= new NoteType("wd_miss");
	public static final NoteType INSUFFICENT_TEXT 	= new NoteType("insuffctxt");
	public static final NoteType SENSE_LOST 		= new NoteType("sense_lost");
	public static final NoteType MISC 				= new NoteType("misc");
	
	/**
	 * Constructs a new note type with the specified value.
	 * 
	 * @throws NullPointerException
	 *             if the argument is {@link NullPointerException}
	 * @throws IllegalArgumentException
	 *             if the argument is empty or all whitespace
	 * @since JSemcor 1.0.0
	 */
	public NoteType(String value) {
		super(ATTR_TAGNOTE, value);
	}
    
	/**
	 * The static private variable that is initialized by {@link #init()} and
	 * returned by a call to {@link #values()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, NoteType> types;
	
	/**
	 * Creates the set of category types declared as static variables in this
	 * concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, NoteType> init(){
		
		// get all the fields containing ContentType
		Field[] fields = NoteType.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == NoteType.class){
				instanceFields.add(field);
			}
		}
		
		// this is the backing set
		Map<String, NoteType> hidden = new LinkedHashMap<String, NoteType>(instanceFields.size());
		
		// fill in the backing set
		NoteType type;
		for(Field field : instanceFields){
			try{
				type = (NoteType)field.get(null);
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
	public static final Collection<NoteType> values(){
		if(types == null) types = init();
		return types.values();
	}
	
    /**
	 * Returns the {@link NoteType} object for the specified name, or
	 * <code>null</code> if there is none.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static final NoteType getInstance(String value){
    	if(types == null) types = init();
    	return types.get(value);
    }

}

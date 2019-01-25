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
 * Default, concrete implementation of {@link IOtherTag}, with all other tag
 * types listed in the Semcor documentation. The reason this class is not
 * implemented as an {@link Enum} is so that clients can instantiate their own
 * instances of this class without re-implementing the interface.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class OtherTag extends Terminal implements IOtherTag {
	
	public static final OtherTag NO_TAG 		= new OtherTag("notag");
	public static final OtherTag METAPHOR 		= new OtherTag("metaphor");
	public static final OtherTag IDIOM 			= new OtherTag("idiom");
	public static final OtherTag COMPLEX_PREP 	= new OtherTag("complexpre");
	public static final OtherTag FOREIGN_WORD 	= new OtherTag("foreignword");
	public static final OtherTag NON_WORD 		= new OtherTag("nonceword");

    /**
	 * Constructs a new OtherTag with the specified value.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public OtherTag(String value){
    	super(ATTR_OT, value);
    }
    
	/**
	 * The static private variable that is initialized by {@link #init()} and
	 * returned by a call to {@link #values()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, OtherTag> types;
	
	/**
	 * Creates the set of category types declared as static variables in this
	 * concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, OtherTag> init(){
		
		// get all the fields containing ContentType
		Field[] fields = OtherTag.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == OtherTag.class){
				instanceFields.add(field);
			}
		}
		
		// this is the backing set
		Map<String, OtherTag> hidden = new LinkedHashMap<String, OtherTag>(instanceFields.size());
		
		// fill in the backing set
		OtherTag type;
		for(Field field : instanceFields){
			try{
				type = (OtherTag)field.get(null);
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
	 * This functions emulates the function of the same value found on each
	 * {@link Enum} class. This method returns a static, unmodifiable collection
	 * containing all the instances of this type that are declared in static
	 * fields in this concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static final Collection<OtherTag> values(){
		if(types == null) types = init();
		return types.values();
	}
	
    /**
	 * Returns the {@link OtherTag} object for the specified value, or
	 * <code>null</code> if there is none.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static final OtherTag getInstance(String value){
    	if(types == null) types = init();
    	return types.get(value);
    }

}

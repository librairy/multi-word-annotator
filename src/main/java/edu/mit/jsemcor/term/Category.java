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
 * The default, concrete implementation of {@link ICategory}, with all category
 * types listed in the Semcor documentation. The reason this class is not implemented
 * as an {@link Enum} is so that clients can instantiate their own instances of
 * this class without re-implementing the interface.
 * 
 * @author M.A. Finlayson
 * @version 1.63, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Category extends Terminal implements ICategory {
	
	public static final Category PERSON 	= new Category("person");
	public static final Category LOCATION 	= new Category("location");
	public static final Category GROUP 		= new Category("group");
	public static final Category OTHER 		= new Category("other");

    /**
	 * Constructs a new Category with the specified value.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public Category(String value){
    	super(ATTR_PN, value);
    }
    
	/**
	 * The static private variable that is initialized by {@link #init()} and
	 * returned by a call to {@link #values()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, Category> types;
	
	/**
	 * Creates the set of category types declared as static variables in this
	 * concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, Category> init(){
		
		// get all the fields containing ContentType
		Field[] fields = Category.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == Category.class){
				instanceFields.add(field);
			}
		}
		
		// this is the backing set
		Map<String, Category> hidden = new LinkedHashMap<String, Category>(instanceFields.size());
		
		// fill in the backing set
		Category type;
		for(Field field : instanceFields){
			try{
				type = (Category)field.get(null);
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
	public static final Collection<Category> values(){
		if(types == null) types = init();
		return types.values();
	}
	
    /**
	 * Returns the {@link Category} object for the specified value, or
	 * <code>null</code> if there is none.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static final Category getInstance(String value){
    	if(types == null) types = init();
    	return types.get(value);
    }
	
}

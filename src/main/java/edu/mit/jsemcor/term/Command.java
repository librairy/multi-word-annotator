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
 * The default, concrete implementation of {@link ICommand}, for use with
 * SemCor. The reason this class is not implemented as an {@link Enum} is so
 * that clients can instantiate their own instances of this class without
 * re-implementing the interface.
 * 
 * @author M.A. Finlayson
 * @version 1.63, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class Command extends Terminal implements ICommand {
	
	public static final Command TAG 	= new Command("tag", 	"word is to be tagged");
	public static final Command DONE 	= new Command("done", 	"word is semantically tagged");
	public static final Command IGNORE 	= new Command("ignore", "word should not be tagged");
	public static final Command UPDATE 	= new Command("update", "used during semantic concordance development only");
	public static final Command RETAG 	= new Command("retag",	"used during semantic concordance development only");

    private final String meaning;

    /**
	 * Constructs a new Category with the specified name.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public Command(String value, String meaning){
    	super(ATTR_CMD, value);
    	if(meaning == null) throw new NullPointerException();
    	if(meaning.trim().length() == 0) throw new IllegalArgumentException();
    	this.meaning = meaning;
    }
    
	/* 
	 * (non-Javadoc)
	 *
	 * @see edu.mit.jsemcor.term.ICommand#getMeaning()
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
	private static Map<String, Command> types;

	/**
	 * Creates the set of category types declared as static variables in this
	 * concrete class.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static Map<String, Command> init(){
		
		// get all the fields containing ContentType
		Field[] fields = Command.class.getFields();
		List<Field> instanceFields = new ArrayList<Field>(); 
		for(Field field : fields){
			if(field.getGenericType() == Command.class){
				instanceFields.add(field);
			}
		}
		
		// this is the Command set
		Map<String, Command> hidden = new LinkedHashMap<String, Command>(instanceFields.size());
		
		// fill in the backing set
		Command type;
		for(Field field : instanceFields){
			try{
				type = (Command)field.get(null);
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
	public static final Collection<Command> values(){
		if(types == null) types = init();
		return types.values();
	}
	
    /**
	 * Returns the {@link Command} object for the specified value, or
	 * <code>null</code> if there is none.
	 * 
	 * @since JSemcor 1.0.0
	 */
    public static final Command getInstance(String value){
    	if(types == null) types = init();
    	return types.get(value);
    }
    
}

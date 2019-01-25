/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.tags;

import java.util.LinkedList;
import java.util.List;

import edu.mit.jsemcor.element.ContextID;
import edu.mit.jsemcor.element.IContextID;

/**
 * 
 * A class that can parse lines from a tag index file. This class follows the
 * singleton design pattern.
 * 
 * @author M.A. Finlayson
 * @version 1.59, 22 Sep 2008
 * @since JSemcor 1.0.0
 */
public class TagFileLineParser {
	
	/**
	 * The static singleton instance of this class, initialized and accessed by
	 * {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static TagFileLineParser fInstance;

	/**
	 * Returns (and initializes if necessary) the singleton instance of this
	 * class.
	 * 
	 * @return the singleton instance of this class
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static TagFileLineParser getInstance() {
		if (fInstance == null) fInstance = new TagFileLineParser();
		return fInstance;
	}
	
	/**
	 * This constructor is marked protected so that the class may be
	 * sub-classed, but not directly instantiated. To obtain and instance of
	 * this class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected TagFileLineParser(){ }
	
	/**
	 * Parses the specified line into a tag list using the specified concordance
	 * name.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public ITagList parseLine(CharSequence line, String concName){
		
		int idx = 0;
		char c;
		StringBuilder sb = new StringBuilder();

		// get sense key
		for(; idx < line.length(); idx++) if(!Character.isWhitespace(line.charAt(idx))) break;
		for(; idx < line.length(); idx++){
			c = line.charAt(idx);
			if(Character.isWhitespace(c)) break;
			sb.append(c);
		}
		String senseKey = sb.toString();
		
		// get sense number
		idx++;
		sb.setLength(0);
		for(; idx < line.length(); idx++) if(!Character.isWhitespace(line.charAt(idx))) break;
		for(; idx < line.length(); idx++){
			c = line.charAt(idx);
			if(Character.isWhitespace(c)) break;
			sb.append(c);
		}
		int senseNum = -1;
		try{
			senseNum = Integer.parseInt(sb.toString());
		} catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		// get sense locations
		List<ISenseLocation> locs = new LinkedList<ISenseLocation>();
		idx++;
		while(idx < line.length()){
			sb.setLength(0);
			for(; idx < line.length(); idx++) if(!Character.isWhitespace(line.charAt(idx))) break;
			for(; idx < line.length(); idx++){
				c = line.charAt(idx);
				if(Character.isWhitespace(c)) break;
				sb.append(c);
			}
			parseLocations(sb, locs, senseKey, senseNum, concName);
		}
		
		return new TagList(senseKey, senseNum, locs.toArray(new ISenseLocation[locs.size()]));
	}
	
	/**
	 * Helper function for the parseLine() method.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void parseLocations(CharSequence cs, List<ISenseLocation> locs, String key, int num, String concName){
		
		// fast forward to first colon
		int start = 0, middle = 0, end = 0;
		char c;

		for(; end < cs.length(); end++){
			c = cs.charAt(end);
			if(c == ':') break;
		}
		
		String contextName = cs.subSequence(0, end).toString();
		IContextID cid = new ContextID(contextName, concName);
		
		int sent, word;
		
		end++;
		start = end;
		while(end < cs.length()){
			for(; end < cs.length(); end++){
				c = cs.charAt(end);
				if(c == ',') middle = end;
				if(c == ';') break;
			}
			sent = Integer.parseInt(cs.subSequence(start, middle).toString());
			word = Integer.parseInt(cs.subSequence(middle+1, end).toString());
			locs.add(new SenseLocation(cid, key, num, sent, word));
			start = end+1;
			end = start;
		}
	}

}

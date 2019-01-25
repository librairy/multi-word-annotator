/********************************************************************************
 * MIT JSemcor Library (JSemcor) v1.0.1
 * Copyright (c) 2008-2011 Massachusetts Institute of Technology
  * 
 * This program and the accompanying materials are made available by MIT under 
 * the terms of the MIT JSemcor License. Refer to the license document included 
 * with this distribution, or contact markaf@alum.mit.edu for further details.
 *******************************************************************************/

package edu.mit.jsemcor.data;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.jsemcor.element.Context;
import edu.mit.jsemcor.element.IContext;
import edu.mit.jsemcor.element.IContextID;
import edu.mit.jsemcor.element.IElement;
import edu.mit.jsemcor.element.IParagraph;
import edu.mit.jsemcor.element.IPunc;
import edu.mit.jsemcor.element.ISemanticTag;
import edu.mit.jsemcor.element.ISentence;
import edu.mit.jsemcor.element.IToken;
import edu.mit.jsemcor.element.IWordform;
import edu.mit.jsemcor.element.Paragraph;
import edu.mit.jsemcor.element.Punc;
import edu.mit.jsemcor.element.SemanticTag;
import edu.mit.jsemcor.element.Sentence;
import edu.mit.jsemcor.element.Wordform;
import edu.mit.jsemcor.term.Category;
import edu.mit.jsemcor.term.Command;
import edu.mit.jsemcor.term.ICategory;
import edu.mit.jsemcor.term.ICommand;
import edu.mit.jsemcor.term.INote;
import edu.mit.jsemcor.term.INoteType;
import edu.mit.jsemcor.term.IOtherTag;
import edu.mit.jsemcor.term.IPOSTag;
import edu.mit.jsemcor.term.Note;
import edu.mit.jsemcor.term.NoteType;
import edu.mit.jsemcor.term.OtherTag;
import edu.mit.jsemcor.term.POSTag;

/**
 * Default, concrete implementation of the {@link IContextParser} interface.
 * This class is thread-safe. To obtain an instance of this class, call
 * {@link #getInstance()}.
 * 
 * @author M.A. Finlayson
 * @version 1.502, 29 Jan 2011
 * @since JSemcor 1.0.0
 */
public class ContextParser implements IContextParser {
	
	/**
	 * Static singleton instance of this class, accessed and initialized by
	 * {@link #getInstance()}.
	 * 
	 * @since JSemcor 1.0.0
	 */
	private static ContextParser cp;
	
	/**
	 * Returns (initializing if necessary) the static singleton instance of this
	 * class.
	 * 
	 * @return the static, singleton {@link ContextParser} instance.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static ContextParser getInstance(){
		if(cp == null) cp = new ContextParser();
		return cp;
	}
	
	/**
	 * This constructor is marked protected so that this class may be
	 * sub-classed, but not directly instantiated. To obtain an instance of this
	 * class, call {@link #getInstance()}
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected ContextParser(){}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.mit.jsemcor.data.parse.IContextParser#parse(java.io.InputStream)
	 */
	public IContext parse(IContextID id, Reader source) throws IOException {

		if(!source.markSupported()) throw new IllegalArgumentException();

		StringBuilder sb = new StringBuilder(1024);
		
		// check first line for <contextfile
		loadLine(source, sb);
		if(sb.indexOf(IContext.TAG_CONTEXTFILE_LEFT) != 0) throwMissingOpeningTag(IContext.TAG_CONTEXT);

		// extract concordance name
		String corpus = extractAttributeValue(IContext.ATTR_CONCORDANCE, sb);
		if(corpus == null) throwMissingAttribute(IContext.ATTR_CONCORDANCE);
		
		// parse all contexts 
		IContext result = null;
		String filename;
		boolean hasParas;
		List<? extends IElement> elementList;
		
		while(loadLine(source, sb) && result == null){
			
			if(sb.indexOf(IContext.TAG_CONTEXT_LEFT) != 0) break; // must have reached end of context elements
			
			// get filename
			filename = extractAttributeValue(IContext.ATTR_FILENAME, sb);
			if(filename == null) throwMissingAttribute(IContext.ATTR_FILENAME);
			
			if(id.getContextName().equals(filename)){
				
				// extract content
				hasParas = IContext.VALUE_YES.equals(extractAttributeValue(IContext.ATTR_PARAS, sb));
				elementList = hasParas ? parseContextParagraphs(id, source, sb) : parseContextSentences(id, 0, source, sb);
				
				// make context
				result = new Context(id, corpus, filename, elementList, hasParas);
				
				// check for </context>, should already be in buffer
				if(sb.indexOf(IContext.TAG_CONTEXT_RIGHT) != 0) throwMissingClosingTag(IContext.TAG_CONTEXT_RIGHT);
				
			} else {
				// forward to next </context>
				while(loadLine(source, sb)){
					if(sb.indexOf(IContext.TAG_CONTEXT_RIGHT) == 0) break;
				}
			}
		}
		
		// check line for </contextfile>
		if(sb.indexOf(IContext.TAG_CONTEXTFILE_RIGHT) != 0) throwMissingClosingTag(IContext.TAG_CONTEXTFILE_RIGHT);
		
		return result;
	}
	
	/**
	 * This method returns a list of {@link IParagraph} objects extracted from
	 * the specified {@link Reader}, in the order they were encountered. The
	 * method assumes the specified reader is positioned before the beginning of
	 * non-whitespace characters on the first paragraph line to be parsed.
	 * 
	 * @return A list of {@link IParagraph} objects corresponding to the
	 *         characters in the {@link Reader} input.
	 * @throws IOException
	 *             if the underlying reader throws an {@link IOException}
	 * @throws MalformedLineException
	 *             if there is a problem with parsing the data
	 * @since JSemcor 1.0.0
	 */
	public List<IParagraph> parseContextParagraphs(IContextID id, Reader source, StringBuilder sb) throws IOException {
		ArrayList<IParagraph> result = new ArrayList<IParagraph>();
		
		String numStr;
		int pnum, numSents = 0;
		IParagraph p;
		
		// load the <p pnum=N> line
		while(loadLine(source, sb)){
			
			if(sb.indexOf(IParagraph.TAG_PARA_LEFT) != 0) return result; // must have exited the list of paragraphs
			
			// extracts and check pnum
			numStr = extractAttributeValue(IContext.ATTR_NUM, sb); 
			try{
				pnum = Integer.parseInt(numStr);
			} catch(NumberFormatException e){
				throw new MalformedLineException(e);
			}
			if(pnum != result.size()+1) logUnexpectedParaNum(id.toString(), result.size()+1, pnum);
			
			// parse <s>...</s>
			p = new Paragraph(pnum, parseContextSentences(id, numSents, source, sb));
			result.add(p); 
			numSents += p.size();
			
			// checks for </p>
			if(sb.indexOf(IParagraph.TAG_PARA_RIGHT) != 0) throwMissingClosingTag(IParagraph.TAG_PARA_RIGHT);
		} 
		
		return result;
	}
	
	/**
	 * This method returns a list of {@link ISentence} objects extracted from
	 * the specified {@link Reader}, in the order they were encountered. The
	 * method assumes the specified reader is positioned before the beginning of
	 * non-whitespace characters on the first sentence line to be parsed.
	 * 
	 * @param numOffset
	 *            the sentence number of the sentence immediately preceding the
	 *            first sentence encountered during this call.
	 * @return A list of {@link ISentence} objects corresponding to the
	 *         characters in the {@link Reader} input.
	 * @throws IOException
	 *             if the underlying reader throws an {@link IOException}
	 * @throws MalformedLineException
	 *             if there is a problem with parsing the data
	 * @since JSemcor 1.0.0
	 */
	public List<ISentence> parseContextSentences(IContextID id, int numOffset, Reader source, StringBuilder sb) throws IOException {
		ArrayList<ISentence> result = new ArrayList<ISentence>();
		
		numOffset++;
		String numStr;
		int snum;
		
		// load the <s snum=N> line
		while(loadLine(source, sb)){ 
			
			if(sb.indexOf(ISentence.TAG_SENT_LEFT) != 0) return result; // must have exited the list of sentences
			
			// extracts and check snum
			numStr = extractAttributeValue(IContext.ATTR_NUM, sb); 
			try{
				snum = Integer.parseInt(numStr);
			} catch(NumberFormatException e){
				throw new MalformedLineException(e);
			}
			if(snum != numOffset+result.size()) logUnexpectedSentNum(id.toString(), numOffset+result.size(), snum);
			
			// parse tokens
			result.add(new Sentence(snum, parseTokens(source, sb))); 
			
			// checks for </s>, the line should already be loaded in the buffer
			if(sb.indexOf(ISentence.TAG_SENT_RIGHT) != 0) throwMissingClosingTag(IParagraph.TAG_PARA_RIGHT);
		} 
		
		return result;
	}
	
	/**
	 * Parses the lines representing sets of tokens in a sentence into a list of
	 * {@link IToken} objects, using the specified {@link StringBuilder} object
	 * as a buffer.
	 * 
	 * @return a non-null but possible empty list of tokens extracted from the
	 *         specified reader
	 * @throws IOException
	 * @since JSemcor 1.0.0
	 */
	public List<IToken> parseTokens(Reader source, StringBuilder sb) throws IOException {
		
		ArrayList<IToken> result = new ArrayList<IToken>();

		IToken token;
		int wordCount = 1;
		while(loadLine(source, sb)){
			if(sb.indexOf(IWordform.TAG_WF_START) == 0){
				token = parseWordFormLine(wordCount, result.size(), sb);
				wordCount++;
			} else if(sb.indexOf(IPunc.TAG_PUNC_START) == 0){
				token = parsePuncLine(sb);
			} else {
				break;
			}
			if(token != null){
				result.add(token);
			} else {
				logNullToken(sb.toString());
			}
		}
		return result;
	}
	
	/**
	 * Parses the specified string into an {@link IWordform} object, if
	 * possible. If not possible, throws a MisformattedLineException.
	 * 
	 * @throws NullPointerException
	 *             if the specified line is <code>null</code>
	 * @throws MalformedLineException
	 *             if the specified line is not a valid word form line.
	 * @since JSemcor 1.0.0
	 */
	public IWordform parseWordFormLine(int wordNum, int tokIdx, CharSequence line) {
		Map<String, String> attrs = parseSGMLLine(line);
		
		String cmdStr = attrs.get(ICommand.ATTR_CMD);
		ICommand cmd = resolveCommand(cmdStr);
		if(cmd == null) throw new MalformedLineException("cmd is null");
		
		int distance = IWordform.DEFAULT_DC;
		if(attrs.containsKey(IWordform.ATTR_DC)){
			try{
				distance = Integer.parseInt(attrs.get(IWordform.ATTR_DC));
			} catch(NumberFormatException e){
				throw new MalformedLineException("problem parsing distance", e);
			}
		}
		
		String redef = attrs.get(IWordform.ATTR_RDF);
		redef = checkAttribute(redef, IWordform.ATTR_RDF);

		String sep = attrs.get(IWordform.ATTR_SEP);
		sep = checkAttribute(sep, IWordform.ATTR_SEP);
		sep = trimQuotes(sep);
		
		String posSym = attrs.get(IPOSTag.ATTR_POS);
		IPOSTag pos = resolvePOSTag(posSym);
		
		if(pos == null) throw new MalformedLineException("pos is null");
		
		ISemanticTag semTag = null;
		String lemma = attrs.get(ISemanticTag.ATTR_LEMMA);
		String wnsn = attrs.get(ISemanticTag.ATTR_WNSN);
		String lexsn = attrs.get(ISemanticTag.ATTR_LEXSN);
		ICategory category = resolveCategory(attrs.get(ICategory.ATTR_PN));
		if(lemma != null && wnsn != null && lexsn != null){
			semTag = new SemanticTag(lemma, wnsn, lexsn, category);
		}
		
		IOtherTag otherTag = resolveOtherTag(attrs.get(IOtherTag.ATTR_OT));
		
		INoteType type = resolveNoteType(attrs.get(INoteType.ATTR_TAGNOTE));
		String noteMsg = attrs.get(INote.ATTR);
		noteMsg = checkAttribute(noteMsg, INote.ATTR);
		noteMsg = trimQuotes(noteMsg);
		INote note = (type != null) ? new Note(noteMsg, type) : null;
		
		String word = attrs.get(IWordform.TAG_WF_VALUE);
		
		return new Wordform(wordNum, tokIdx, cmd, distance, redef, sep, pos, semTag, otherTag, note, word);
		
	}
	
	/**
	 * Parses a line representing a punctuation entry and returns the
	 * corresponding {@link IPunc} object.
	 * 
	 * @return an {@link IPunc} object representing the specified line, or
	 *         <code>null</code> if line cannot be interpreted as an
	 *         {@link IPunc} object.
	 * @throws NullPointerException
	 *             if the specified line is <code>null</code>.
	 * @since JSemcor 1.0.0
	 */
	public IPunc parsePuncLine(CharSequence line){
    	
    	int start = 0;
    	
    	// find <punc>
    	int found = 0;
    	while(start < line.length() && found < IPunc.TAG_PUNC_START.length()){
    		if(line.charAt(start) == IPunc.TAG_PUNC_START.charAt(found)){
    			found++;
    			start++;
    		} else {
    			if(found == 0){
    				start++;
    			} else {
    				found = 0;
    			}
    		}
    	}
    	
    	int end = start;
    	
    	// find </punc>
    	found = 0;
    	while(end < line.length() && found < IPunc.TAG_PUNC_END.length()){
    		if(line.charAt(end) == IPunc.TAG_PUNC_END.charAt(found)){
    			found++;
    			end++;
    		} else {
    			if(found == 0){
    				end++;
    			} else {
    				found = 0;
    			}
    		}
    	}

    	return start == end ? null : resolvePunc(line.subSequence(start, end-found).toString());
    }
	
	/**
	 * Checks to see if the specified value is <code>null</code> or all
	 * whitespace. If so, returns <code>null</code>. Otherwise, just returns
	 * the {@code value} String.
	 * 
	 * @param value
	 *            the value to checked, and what is returned if it is not
	 *            <code>null</code> or all whitespace
	 * @param attribute
	 *            the name of the attribute that is being checked
	 * @return <code>null</code> if the value is <code>null</code> or all
	 *         whitespace; the value String otherwise.
	 * @since JSemcor 1.0.0
	 */
	protected String checkAttribute(String value, String attribute){
		if(value == null) return null;
		if(value.trim().length() == 0){
			logEmptyAttributeValue(attribute);
			return null;
		}
		return value;
	}
	
	/**
	 * Given the specified category string value, returns an {@link ICategory}
	 * object for that value. This implementation uses the static final
	 * instances in the {@link Category} class. If a {@link Category} object cannot be
	 * found that corresponds to this punctuation string, a new {@link Category}
	 * object is created and returned.
	 * 
	 * @return <code>null</code> if the specified value is <code>null</code>;
	 *         Otherwise, a {@link Category} object representing the specified value
	 *         as a category object.
	 * @since JSemcor 1.0.0
	 */
	protected ICategory resolveCategory(String value){
		if(value == null) return null;
		ICategory result = Category.getInstance(value);
		if(value != null && result == null){
			logUnknownCategory(value);
			result = new Category(value);
		}
		return result;
	}

	/**
	 * Given the specified command string value, returns an {@link IPunc}
	 * object for that value. This implementation uses the static final
	 * instances in the {@link Command} class. If a {@link Command} object cannot be
	 * found that corresponds to this punctuation string, a new {@link Command}
	 * object is created and returned.
	 * 
	 * @return <code>null</code> if the specified value is <code>null</code>;
	 *         Otherwise, a {@link Command} object representing the specified value
	 *         as a command object.
	 * @since JSemcor 1.0.0
	 */
	protected ICommand resolveCommand(String value){
		if(value == null) return null;
		ICommand result = Command.getInstance(value);
		if(result == null){
			logUnknownCommand(value); 
			result = new Command(value, "Unknown");
		}
		return result;
	}

	/**
	 * Given the specified note string value, returns an {@link INoteType}
	 * object for that value. This implementation uses the static final
	 * instances in the {@link NoteType} class. If a {@link NoteType} object cannot be
	 * found that corresponds to this punctuation string, a new {@link NoteType}
	 * object is created and returned.
	 * 
	 * @return <code>null</code> if the specified value is <code>null</code>;
	 *         Otherwise, a {@link NoteType} object representing the specified value
	 *         as a note type object.
	 * @since JSemcor 1.0.0
	 */
	protected INoteType resolveNoteType(String value){
		if(value == null) return null;
		INoteType result = NoteType.getInstance(value);
		if(result == null){
			logUnknownNoteType(value);
			result = new NoteType(value);
		}
		return result;
	}

	/**
	 * Given the specified other tag string value, returns an {@link IOtherTag}
	 * object for that value. This implementation uses the static final
	 * instances in the {@link OtherTag} class. If a {@link NoteType} object cannot be
	 * found that corresponds to this punctuation string, a new {@link NoteType}
	 * object is created and returned.
	 * 
	 * @return <code>null</code> if the specified value is <code>null</code>;
	 *         Otherwise, a {@link NoteType} object representing the specified value
	 *         as a note type object.
	 * @since JSemcor 1.0.0
	 */
	protected IOtherTag resolveOtherTag(String value){
		if(value == null) return null;
		IOtherTag result = OtherTag.getInstance(value);
		if(result == null){
			logUnknownOtherTag(value);
			result = new OtherTag(value);
		}
		return result;
	}

	/**
	 * Given the specified part of speech string value, returns an {@link IPOSTag}
	 * object for that value. This implementation uses the static final
	 * instances in the {@link POSTag} class. If a {@link POSTag} object cannot be
	 * found that corresponds to this punctuation string, a new {@link POSTag}
	 * object is created and returned.
	 * 
	 * @return <code>null</code> if the specified value is <code>null</code>;
	 *         Otherwise, a {@link POSTag} object representing the specified value
	 *         as a part of speech object.
	 * @since JSemcor 1.0.0
	 */
	protected IPOSTag resolvePOSTag(String value){
		if(value == null) return null;
		IPOSTag result = POSTag.getInstance(value);
		if(result == null){
			logUnknownPOSTag(value);
			result = new POSTag(value, "Unknown");
		}
		return result;
	}
	
	/**
	 * Given the specified punctuation string value, returns an {@link IPunc}
	 * object for that value. This implementation uses the static final
	 * instances in the {@link Punc} class. If a {@link Punc} object cannot be
	 * found that corresponds to this punctuation string, a new {@link Punc}
	 * object is created and returned.
	 * 
	 * @return <code>null</code> if the specified value is <code>null</code>;
	 *         Otherwise, a {@link Punc} object representing the specified value
	 *         as a punctuation object.
	 * @since JSemcor 1.0.0
	 */
	protected IPunc resolvePunc(String value){
		if(value == null) return null;
		IPunc result =  Punc.getInstance(value);
    	if(result == null){
    		logUnknownPunc(value);
        	result = new Punc("Unknown", '?', value);
    	}
    	return result;
	}
	
	/**
	 * Logs the presence of a null token in the specified line. This
	 * implementation does nothing, and is left to subclasses to override if
	 * required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logNullToken(String line){
		// System.err("Warning: null token encountered for line: " + line);
	}

	/**
	 * Logs the presence of an empty attribute value for an attribute with the
	 * specified name. This implementation does nothing, and is left to
	 * subclasses to override if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logEmptyAttributeValue(String attribute){
		// System.err("Warning: empty value for attribute " + attribute);
	}
	
	/**
	 * Logs the presence of an unexpected sentence number. This implementation
	 * does nothing, and is left to subclasses to override if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnexpectedSentNum(String id, int expected, int actual){
		// System.err("Unexpected sentence number in " + id + ", expected=" + expected + ", actual=" + actual);
	}
	
	/**
	 * Logs the presence of an unexpected paragraph number. This implementation
	 * does nothing, and is left to subclasses to override if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnexpectedParaNum(String id, int expected, int actual){
		// System.err("Unexpected paragraph number in " + id + ", expected=" + expected + ", actual=" + actual);
	}
	
	/**
	 * Logs the presence of an unknown category of the specified type. This
	 * implementation does nothing, and is left to subclasses to override if
	 * required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnknownCategory(String value){
		// System.err("Warning: unknown category " + value);
	}

	/**
	 * Logs the presence of an unknown command of the specified type. This
	 * implementation does nothing, and is left to subclasses to override if
	 * required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnknownCommand(String value){
		// System.err("Warning: unknown cmd " + value);
	}

	/**
	 * Logs the presence of an unknown note of the specified type. This
	 * implementation does nothing, and is left to subclasses to override if
	 * required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnknownNoteType(String value){
		// System.err("Warning: unknown note type " + value);
	}
	
	/**
	 * Logs the presence of an unknown other tag of the specified type. This
	 * implementation does nothing, and is left to subclasses to override if
	 * required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnknownOtherTag(String value){
		// System.err("Warning: unknown other tag type " + value);
	}
	
	/**
	 * Logs the presence of an unknown tag of the specified type. This
	 * implementation does nothing, and is left to subclasses to override if
	 * required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnknownPOSTag(String value){
		// System.err("Warning: unknown tag " + value);
	}

	/**
	 * Logs the presence of an unknown punctuation with the specified value.
	 * This implementation does nothing, and is left to subclasses to override
	 * if required.
	 * 
	 * @since JSemcor 1.0.0
	 */
	protected void logUnknownPunc(String value){
		// System.err("Unknown punctuation: " + value);
	}

	/**
	 * Throws a {@link MalformedLineException} specifying that an expected EOF
	 * (end of file) is missing.
	 * 
	 * @throws MalformedLineException
	 *             specifying that an expected EOF (end of file) is missing.
	 * @since JSemcor 1.0.0
	 */
	protected void throwUnexpectedEOF(){
		throwMalformedLineException("Unexpected EOF", null);
	}

	/**
	 * Throws a {@link MalformedLineException} specifying that an opening tag of
	 * the specified type is missing.
	 * 
	 * @throws MalformedLineException
	 *             specifying that an opening tag of the specified type is
	 *             missing.
	 * @since JSemcor 1.0.0
	 */
	protected void throwMissingOpeningTag(String tag){
		throwMalformedLineException("Missing opening tag " + IContext.TAG_CONTEXTFILE, null);
	}
	
	/**
	 * Throws a {@link MalformedLineException} specifying that an attribute of
	 * the specified type is missing.
	 * 
	 * @throws MalformedLineException
	 *             specifying that an attribute of the specified type is
	 *             missing.
	 * @since JSemcor 1.0.0
	 */
	protected void throwMissingAttribute(String attr){
		throwMalformedLineException("Missing attribute: " + attr, null);
	}
	
	/**
	 * Throws a {@link MalformedLineException} specifying that a closing tag of
	 * the specified type is missing.
	 * 
	 * @throws MalformedLineException
	 *             specifying that a closing tag of the specified type is
	 *             missing.
	 * @since JSemcor 1.0.0
	 */
	protected void throwMissingClosingTag(String tag){
		throwMalformedLineException("Missing closing tag " + tag, null);
	}
	
	/**
	 * Throws a {@link MalformedLineException} with the specified message and
	 * cause. Either argument may be <code>null</code>.
	 * 
	 * @throws MalformedLineException
	 *             with the specified message and cause.
	 * @since JSemcor 1.0.0
	 */
	protected void throwMalformedLineException(String message, Throwable cause){
		throw new MalformedLineException(message, cause);
	}
	
	/**
	 * Takes a list of attribute/value pairs, {@code attr1=val1 attr2=val2 ...
	 * attrN=valN} and returns them as a map of attributes to values. It is
	 * insensitive to whitespace between the pairs, or between the attributes
	 * and value and the equals signs. Values that contain whitespace should be
	 * enclosed in quotes. Because of this, values cannot contain quotes other
	 * than the quotes than surrounding quotes.
	 * 
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * @throws MalformedLineException
	 *             if any attribute occurs more than once.
	 * @since JSemcor 1.0.0
	 */
	public static Map<String, String> parseAttributeList(CharSequence tags){
		
		Map<String, String> result = new HashMap<String, String>();
		
		int begin = 0, equals, end;
		boolean hasQuotes;
		String key, value;
		while(begin < tags.length()){
			
			// forward 'begin' to next non-whitespace character
			while(begin < tags.length()){
				if(!Character.isWhitespace(tags.charAt(begin))) break;
				begin++;
			}
			
			// forward 'equals' to next equals character
			equals = begin+1;
			while(equals < tags.length()){
				if(tags.charAt(equals) == IElement.EQUALS) break;
				equals++;
			}
			
			// forward 'end' to next whitespace character not enclosed by quotes
			end = equals+1;
			hasQuotes = end < tags.length() && tags.charAt(end) == IElement.OPEN_QUOTE;
			if(hasQuotes) end++;
			while(end < tags.length()){
				if(hasQuotes){
					if(tags.charAt(end) == IElement.CLOSE_QUOTE) break;
				} else {
					if(Character.isWhitespace(tags.charAt(end))) break;
				}
				end++;
			}
			
			if(tags.length() <= begin || tags.length() <= equals || tags.length() < end) break; 
			
			key = tags.subSequence(begin, equals).toString();
			value = hasQuotes ? tags.subSequence(equals+2, end).toString() : tags.subSequence(equals+1, end).toString();
			
			if(result.put(key, value) != null) throw new MalformedLineException("Multiple attributes of same type: " + key);
			
			begin = hasQuotes ? end+2 : end+1;
			
		}
		
		return result;
	}

	/**
	 * Given a line of the form
	 * {@code <tag attr1=val1 attr2=val2 ... attrN=valN>text</tag>}, this
	 * method extracts the value for the named attribute, if it is in the line.
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static String extractAttributeValue(String attr, StringBuilder sb){
		
		// find the attribute string
		int begin = sb.indexOf(attr);
		if(begin == -1) return null;
		begin += attr.length();
		
		// fast forward through whitespace
		while(begin < sb.length()){
			if(!Character.isWhitespace(sb.charAt(begin))) break;
			begin++;
		}
		
		if(sb.charAt(begin) != IElement.EQUALS) return null;
		begin++;
		
		// fast forward through whitespace
		while(begin < sb.length()){
			if(!Character.isWhitespace(sb.charAt(begin))) break;
			begin++;
		}
		
		// move forward to first whitespace or right angle bracket
		int end = begin;
		char c;
		while(end < sb.length()){
			c = sb.charAt(end);
			if(c == IElement.RIGHT_ANGLE_BRACKET || Character.isWhitespace(c)) break;
			end++;
		}
		return (end != begin) ? sb.substring(begin, end) : null;
	}

	/**
	 * Takes a line of the form
	 * {@code <tag attr1=val1 attr2=val2 ... >text</tag>} and returns a map of
	 * strings where each attrN is mapped to it's associated valN (with quotes
	 * stripped from the value, if any), and {@code tag} mapped to {@code text}.
	 * 
	 * @return a map from attributes to values as represented by the specified
	 *         character sequence, plus the head tag mapped to the text content
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static Map<String, String> parseSGMLLine(CharSequence line){
		try{
			int leftStart = -1;
			int leftSpace = -1;
			int leftEnd = -1;
			int rightStart = -1;
			int rightSlash = -1;
			int rightEnd = -1;
			
			int idx = 0;
			
			// find first <
			for(; idx < line.length(); idx++){
				if(line.charAt(idx) != IElement.LEFT_ANGLE_BRACKET) continue;
				leftStart = idx;
				break;
			}
			
			// find next space
			for(; idx < line.length(); idx++){
				if(line.charAt(idx) != IElement.SPACE) continue;
				leftSpace = idx;
				break;
			}
			
			// find next > character
			for(; idx < line.length(); idx++){
				if(line.charAt(idx) != IElement.RIGHT_ANGLE_BRACKET) continue;
				leftEnd = idx;
				break;
			}
			
			// find next < character
			for(; idx < line.length(); idx++){
				if(line.charAt(idx) != IElement.LEFT_ANGLE_BRACKET) continue;
				rightStart = idx;
				break;
			}
			
			// find next / character
			for(; idx < line.length(); idx++){
				if(line.charAt(idx) != IElement.FORWARD_SLASH) continue;
				rightSlash = idx;
				break;
			}
			
			// find next > character
			for(; idx < line.length(); idx++){
				if(line.charAt(idx) != IElement.RIGHT_ANGLE_BRACKET) continue;
				rightEnd = idx;
				break;
			}
			
			// make sure we found all the characters
			if(leftStart == -1 || leftSpace == -1 || leftEnd == -1 || rightStart == -1 || rightSlash == -1 || rightEnd == -1){
				throw new MalformedLineException("Tags not properly closed");
			}
			
			// make sure the opening and closing tag match
			String leftTag = line.subSequence(leftStart+1, leftSpace).toString();
			String rightTag = line.subSequence(rightSlash+1, rightEnd).toString();
			if(!leftTag.equals(rightTag)) throw new MalformedLineException("Tags don't match");
			
			// extract tags
			Map<String, String> result = parseAttributeList(line.subSequence(leftSpace+1, leftEnd));
			
			// extract text context
			String word = line.subSequence(leftEnd+1, rightStart).toString();
			result.put(leftTag, word);
			
			return result;
		
		} catch(RuntimeException e){
			throw (e instanceof MalformedLineException) ? e : new MalformedLineException(e);
		}
	}

	/**
	 * This method takes the specified string trims off any excess whitespace.
	 * If the resulting string both begins and ends with the quotation mark
	 * characters ({@link IElement#OPEN_QUOTE} and {@link IElement#CLOSE_QUOTE}),
	 * those characters are stripped off and the resulting string returned.
	 * Otherwise the trimmed {@link String} is returned.
	 * 
	 * @return the string with whitespace and enclosing open and closed quotes
	 *         removed, if present
	 * 
	 * @since JSemcor 1.0.0
	 */
	public static String trimQuotes(String value){
		if(value == null) return value;
		
		// trim off whitespace
		value = value.trim();
		if(value.length() < 2) return value;
		
		// trim off quotes, return result
		if(value.charAt(0) == IElement.OPEN_QUOTE && value.charAt(value.length()-1) == IElement.CLOSE_QUOTE){
			return value.substring(1, value.length()-1);
		} else {
			return value;
		}
	}
	
	/**
	 * Loads the next line from the input stream into the buffer. First the
	 * buffer is emptied, and then, starting from the current position of the
	 * stream, characters are discarded from the stream until the first
	 * non-whitespace character is encountered. From that point, all characters
	 * until the first linebreak ('\n', '\r', or '\r\n' sequences) are read into
	 * the buffer. The linebreak characters themselves are not put in the
	 * buffer. The method returns <code>true</code> if at least one character
	 * was read from the stream, regardless of whether any characters were
	 * placed in the buffer.
	 * 
	 * @return <code>true</code> if at least one character was consumed from
	 *         the stream; <code>false</code> otherwise.
	 * @throws NullPointerException
	 *             if the source or buffer are <code>null</code>
	 * @since JSemcor 1.0.0
	 */
	public static boolean loadLine(Reader source, StringBuilder buffer) throws IOException {
		buffer.setLength(0);
		int c;
		boolean start = true;
		while((c = source.read()) != -1) {
		    switch (c) {
			    case '\n':
					return true;
			    case '\r':
					source.mark(2);
					c = (char)source.read();
					if (c != '\n') source.reset();
					return true;
			    default:
			    	if(start && Character.isWhitespace(c)) continue; // don't append whitespace at beginning of line
			    	if(start) start = false;
			    	buffer.append((char)c);
		    }
		}
		return c != -1;
	}

}

/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the 
 * terms of the jMWE License which accompanies this distribution.  
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IMWEDescID;
import edu.mit.jmwe.data.IRootMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.InfMWEDesc;
import edu.mit.jmwe.data.MWE;
import edu.mit.jmwe.data.MWEDescID;
import edu.mit.jmwe.data.MWEPOS;
import edu.mit.jmwe.data.RootMWEDesc;
import edu.mit.jmwe.data.concordance.IConcordanceSentence;
import edu.mit.jmwe.data.concordance.IConcordanceToken;
import edu.mit.jmwe.data.concordance.TaggedConcordanceIterator;
import edu.mit.jmwe.detect.Consecutive;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.detect.InflectionRule;
import edu.mit.jmwe.harness.ConcordanceAnswerKey;
import edu.mit.jmwe.util.AbstractFileSelector;
import edu.mit.jmwe.util.IProgressBar;
import edu.mit.jmwe.util.JWIPOS;
import edu.mit.jmwe.util.ProgressBar;
import edu.mit.jmwe.util.StreamAdapter;
import edu.mit.jsemcor.element.ISemanticTag;
import edu.mit.jsemcor.main.IConcordanceSet;
import edu.mit.jsemcor.main.Semcor;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;

/**
 * Builds a MWE index that can be loaded into memory from Wordnet, using Semcor
 * as the reference concordance for obtaining frequencies relating to an MWE's
 * occurrence as marked, unmarked, etc.
 * <p>
 * This class requires JWI and JSemcor to be on the classpath.
 * 
 * @author M.A. Finlayson
 * @version $Id: IndexBuilder.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class IndexBuilder extends AbstractFileSelector implements Runnable {

	/**
	 * Constructs the MWE index from Wordnet and Semcor and writes it to a file.
	 * 
	 * @param args
	 *            standard main arguments; ignored
	 * @since jMWE 1.0.0
	 */
	public static void main(String[] args){
		IndexBuilder builder = new IndexBuilder();
		builder.run();
	}
	
	/* 
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		// wordnet
		IDictionary dict = getDictionary();
		if(dict == null)
			return;
		
		// concordance
		IConcordanceSet semcor = getConcordance();
		Iterable<IConcordanceSentence> itr = getTaggedIterator();
		
		// descs
		File descFile = getDataFile();
		if(descFile == null)
			return;

		// index
		File indexFile;
		do {
			indexFile = getIndexFile();
		} while(descFile.equals(indexFile));

		// do the actual work
		try {
			process(dict, itr, semcor, descFile, indexFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the <code>IDictionary</code> that will be used to interface with Wordnet.
	 * 
	 * @return an electronic dictionary from which the MWEs may be extracted. if
	 *         the wordnet directory cannot be found.
	 * @since jMWE 1.0.0
	 */
	protected IDictionary getDictionary() {
		File dir = getDictionaryDir();
		if(dir == null)
			return null;
		IDictionary wordnet = new Dictionary(StreamAdapter.toURL(dir));
		try {
			if(!wordnet.open())
                throw new IllegalStateException("Unable to open Wordnet");
		} catch (IOException e) {
			throw new IllegalStateException("Unable to open Wordnet",e);
		}
		return wordnet;
	}

	/**
	 * Returns the location of the wordnet dictionary; may be overridden by
	 * subclasses to provide a different manner of locating the dictionary.
	 * 
	 * @return the directory containing the wordnet data files
	 * @since jMWE 1.0.0
	 */
	protected File getDictionaryDir(){
		return chooseDirectory("Select Wordnet Directory", IDictionary.class);
	}

	/**
	 * Gets the concordance set that will be used to interface with Semcor.
	 * 
	 * @return the concordance set containing the concordance information, or
	 *         <code>null</code> if none
	 * @since jMWE 1.0.0
	 */
	protected IConcordanceSet getConcordance() {
		File dir = getConcordanceDir();
		if(dir == null)
			return null;
		IConcordanceSet semcor = new Semcor(StreamAdapter.toURL(dir));
		if(!semcor.open())
			throw new IllegalStateException("Unable to open Semcor");
		return semcor;
	}
	
	/**
	 * Returns the location of the concordance data; may be overridden by
	 * subclasses to provide a different manner of locating the concordance.
	 *
	 * @return a directory containing a concordance set in Semcor format, or
	 *         <code>null</code> if the concordance directory cannot be
	 *         found.
	 * @since jMWE 1.0.0
	 */
	protected File getConcordanceDir(){
		return chooseDirectory("Select Semcor Directory (choose Cancel for no inflected forms or counts)", IConcordanceSet.class);
	}
	
	/**
	 * Gets an iterator over the tagged semcor sentences.
	 * 
	 * @return the iterator over tagged semcor sentences. Will return
	 *         <code>null</code> if the tagged concordance file cannot be found
	 *         or if the user chooses to construct this index with no inflected
	 *         forms or counts.
	 * @since jMWE 1.0.0
	 */
	protected Iterable<IConcordanceSentence> getTaggedIterator() {
		final File file = getTaggedConcordanceFile();
		if(file == null)
			return null;
		return new Iterable<IConcordanceSentence>() {
			public Iterator<IConcordanceSentence> iterator() {
				try {
					return new TaggedConcordanceIterator(file);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}
	
	/**
	 * Returns the location of the tagged concordance file; may be overridden by
	 * subclasses to provide a different manner of locating the concordance.
	 *
	 * @return a file containing the tagged concordance data, or
	 *         <code>null</code> if none
	 * @since jMWE 1.0.0
	 */
	protected File getTaggedConcordanceFile(){
		return chooseFile("Select Tagged Semcor File (choose Cancel for no inflected forms or counts)", TaggedConcordanceIterator.class);
	}

	/**
	 * Prompts the user to select the file to which the MWE descriptions will be
	 * written along with their counts relating to their occurrences in the
	 * reference concordance. This file will contain lines of the form:
	 * 
	 * <pre>
	 * account_for_V 14,0,0,0,0 accounted_for 5,0,0,0,5 accounting_for
	 * 1,0,0,0,0 accounts_for 2,0,0,0,1
	 * </pre>
	 * 
	 * @return the file to which the MWE descriptions and counts will be
	 *         written.
	 * @since jMWE 1.0.0
	 */
	protected File getDataFile(){
		return chooseFileForWriting("Select MWEIndex Data File", IMWEDesc.class);
	}
	
	/**
	 * Prompts the user to select the file to which the index will be written.
	 * This file will contain lines of the form:
	 * 
	 * <p>
	 * <i> aberration chromatic_aberration_N chromosomal_aberration_N
	 * optical_aberration_N spherical_aberration_N</i>
	 * </p>
	 * 
	 * @return the file to which the index will be written.
	 * @since jMWE 1.0.0
	 */
	protected File getIndexFile(){
		return chooseFileForWriting("Select MWEIndex Index File (choose Cancel for no index file)", IMWEIndex.class);
	}
	
	/**
	 * Returns the set of lines to be included as a header in the data file.
	 *
	 * @return the set of lines to be included as a header in the data file. 
	 * @since jMWE 1.0.0
	 */
	protected List<String> getDataHeaderLines(){
		List<String> header = new LinkedList<String>();
		header.add("jMWE MWE Description Data File");
		header.add("Generated on " + new Date().toString());
		return header;
	}

	/**
	 * Returns the set of lines to be included as a header in the index file.
	 *
	 * @return the set of lines to be included as a header in the index file. 
	 * @since jMWE 1.0.0
	 */
	protected List<String> getIndexHeaderLines(){
		List<String> header = new LinkedList<String>();
		header.add("jMWE MWE Description Index File");
		header.add("Generated on " + new Date().toString());
		return header;
	}

	/**
	 * Constructs the index in five steps:
	 *
	 *  <p>1. Extracts the MWEs from the given dictionary</p>
	 *  <p>2. Finds the MWEs in the concordance that are missing from the dictionary.</p>
	 *  <p>3. Counts the number of times this MWE was marked as a continuous run of tokens, non-continuous run, appeared with a known inflection pattern, etc.</p>
	 *  <p>4. Records the counts for unmarked sequences of MWE parts</p>
	 *  <p>5. Writes the index to the data and index files </p>
	 * 
	 * If the concordance set provided is <code>null</code>, skips steps 2-4.
	 *
	 * @param dict
	 *            the dictionary containing the MWEs
	 * @param itr
	 *            the iterator over the sentences in the reference concordance
	 * @param cs
	 *            the possibly <code>null</code> reference concordance set.
	 * @param dataFile
	 *            the file to which the descriptions and counts will be written
	 * @param indexFile
	 *            the file to which the index will be written
	 * @throws IOException
	 *            if there is a problem when accessing the specified files or dictionaries
	 * @since jMWE 1.0.0
	 */
	public void process(IDictionary dict, Iterable<? extends IConcordanceSentence> itr, IConcordanceSet cs, File dataFile, File indexFile) throws IOException {
		if(dict == null)
			throw new NullPointerException();
		if(dataFile == null)
			throw new NullPointerException();
		
		// construct list 
		System.out.print("(1 of 5) Extracting MWEs from dictionary...");
		Map<IMWEDescID, MutableRootMWEDesc> data = extractMWEs(dict);
		System.out.println("done.");
		
		IMWEIndex index = null;
		
		// process inflected forms
		if(cs == null){
			System.out.println("\nSkipping steps 2-4 because no concordance was provided");
		} else {

			int sentCnt = getEstimatedSentenceCount();
			IProgressBar pb;
			ConcordanceAnswerKey key = new ConcordanceAnswerKey(cs);
			key.setIgnoreProperNouns(true);
			
			// find MWEs listed in concordance but missing from dictionary
			System.out.println("\n(2 of 5) Finding missing MWEs...");
			Set<MutableRootMWEDesc> missing = new TreeSet<MutableRootMWEDesc>();
			pb = new ProgressBar(sentCnt);
			sentCnt = 0;
			for(IConcordanceSentence s : itr){
				sentCnt++;
				findMissingMWEs(key.getAnswers(s), data, missing);
				pb.increment();
			}
			pb.finish();
			System.out.println("Found " + missing.size() + " missing MWE entries in concordances.");
			
			// Counting marked occurences
			System.out.println("\n(3 of 5) Counting marked occurences...");
			pb = new ProgressBar(sentCnt);
			for(IConcordanceSentence ss : itr){
				countMarked(key.getAnswers(ss), data);
				pb.increment();
			}
			pb.finish();
			
			// generate index with inflected forms
			index = new MWEIndex(data);
			index.open();
			
			// get all observed isolated token sequences that match a known MWE
			System.out.println("\n(4 of 5) Counting unmarked occurences...");
			pb = new ProgressBar(sentCnt);
			IMWEDetector d = getUmarkedDetector(index);
			for(IConcordanceSentence ss : itr){
				countUnmarked(d, ss, key.getAnswers(ss));
				pb.increment();
			}
			pb.finish();
		}
		
		// generate index if needed
		if(index == null)
			index = new MWEIndex(data);

		// write to file
		System.out.print("\n(5 of 5) Writing file(s)...");
		
		// write data file
		dataFile = deleteFile(dataFile, new FileGetter(){public File get() { return getDataFile(); }});
		if(dataFile != null)
			writeDataFile(index, new FileOutputStream(dataFile), getDataHeaderLines());
		
		// write index file
		// XXX: do not generate index right now: no class uses it
//		indexFile = deleteFile(indexFile, new FileGetter(){public File get() { return getIndexFile(); }});
//		if(indexFile != null)
//			writeIndexFile(index, new FileOutputStream(indexFile), getIndexHeaderLines());
		
		System.out.println("done.");
		printTotals(data);
	}
	
	/**
	 * Returns the estimated number of sentences being used from the reference concordance (Semcor).
	 *
	 * @return the estimated number of sentences being used from Semcor.
	 * @since jMWE 1.0.0
	 */
	protected int getEstimatedSentenceCount() {
		return 20138;
	}

	/**
	 * Retrieves multi-word expressions from the specified {@code IDictionary}
	 * object and returns them as a map. Multi-word expressions are indexed in
	 * the map according to their collocates. That is, the keys of the map are
	 * parts of multi-word expressions ({@link String} objects that do not
	 * contain an underscore), and the set associated with that key contains all
	 * multi-word expressions in the dictionary that contain that part.
	 * 
	 * This method returns a map whose keys and value sets are sorted in their
	 * natural order.
	 * 
	 * @param dict
	 *            a JWI {@code IDictionary} object
	 * @return a {@link Map} with collocates as keys and a set of the multi-word
	 *         expressions that they are a part of as values.
	 * @throws NullPointerException
	 *             if the specified dictionary is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public Map<IMWEDescID, MutableRootMWEDesc> extractMWEs(IDictionary dict) {
		
		Map<IMWEDescID, MutableRootMWEDesc> result = new TreeMap<IMWEDescID, MutableRootMWEDesc>();
		
//		// test code
//		MutableMWEDesc desc = new MutableMWEDesc("pass_on", MWEPOS.VERB);
//		result.put(desc.getID(), desc);
//		return result;
		
		// loop variables
		MWEPOS mwepos;
		IIndexWord idxWord;
		Iterator<IIndexWord> itr;
		MutableRootMWEDesc desc;
		// iterate over all JWI parts of speech
		for(POS pos : POS.values()){
			
			// cache the part of speech
			mwepos = JWIPOS.toMWEPOS(pos);
			
			// iterate over all index words in the dictionary
			for(itr = dict.getIndexWordIterator(pos); itr.hasNext(); ){
				idxWord =  itr.next();
				if(isMWE(idxWord)){
					desc = new MutableRootMWEDesc(idxWord.getLemma(), mwepos);
					result.put(desc.getID(), desc);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns true if the given word is an MWE. Tests this by checking whether
	 * its lemma contains an underscore.
	 * 
	 * @param idxWord
	 *            the word to be checked.
	 * @return true if the given word is an MWE
	 * @since jMWE 1.0.0
	 */
	protected boolean isMWE(IIndexWord idxWord){
		return idxWord.getLemma().indexOf('_') > -1;
	}

	/**
	 * Counts instances of marked MWEs
	 * 
	 * @param answers
	 *            the list of answers for a sentence, may not be
	 *            <code>null</code>
	 * @param index
	 *            the index map,may not be <code>null</code>
	 * @throws NullPointerException
	 *             if either argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public void countMarked(List<IMWE<IConcordanceToken>> answers, Map<IMWEDescID, MutableRootMWEDesc> index) {
		
		if(index == null)
			throw new NullPointerException();
		
		IMutableMWEDesc targetDesc;
		MutableRootMWEDesc rootDesc;
		MutableInfMWEDesc infDesc;
		
		for(IMWE<IConcordanceToken> mwe : answers){
			rootDesc = index.get(mwe.getEntry().getID());
			
			if(mwe.isInflected()){
				infDesc = getInflectedForm(rootDesc, mwe.getForm());
				targetDesc = infDesc;
				if(InflectionRule.isInflectedByPattern(mwe))
					infDesc.incrementMarkedPattern();
			} else {
				targetDesc = rootDesc;
			}
			
			if(isSplit(mwe)){
				targetDesc.incrementMarkedSplit();
			} else {
				targetDesc.incrementMarkedContinuous();
			}
		}
	}

	/**
	 * Returns an inflected form that matches the specified surface form,
	 * attached to the root description. If no such inflected form exists, one
	 * is created
	 * 
	 * @param root
	 *            the root form on which the inflected is to be created
	 * @param form
	 *            the inflected form
	 * @return a MWE description object corresponding to the inflected form
	 * @since jMWE 1.0.0
	 */
	protected MutableInfMWEDesc getInflectedForm(MutableRootMWEDesc root, String form){
		for(MutableInfMWEDesc infDesc : root.getInflected().values())
			if(infDesc.getForm().equals(form))
				return infDesc;
		
		// there isn't one, so make it
		MutableInfMWEDesc infDesc = new MutableInfMWEDesc(root, form);
		root.getInflected().put(infDesc.getForm(), infDesc);
		return infDesc;
	}

	/**
	 * Returns <code>true</code> if this MWE is not continuous - if it has
	 * interstitial tokens that are not a part of it; <code>false</code>
	 * otherwise.
	 * 
	 * @param mwe
	 *            the MWE to test; may not be <code>null</code>
	 * @param <T>
	 *            the type of token used by the mwe
	 * @return <code>true</code> if the MWE is split; <code>false</code>
	 *         otherwise
	 * @throws NullPointerException
	 *             if the specified MWE is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected <T extends IConcordanceToken> boolean isSplit(IMWE<T> mwe) {
		// if an MWE is not split, all of its parts
		// will all have come from the same token
		int tokenNum = mwe.getTokens().get(0).getTokenNumber();
		for(T token : mwe.getTokens())
			if(token.getTokenNumber() != tokenNum)
				return true;
		return false;
	}

	/**
	 * Finds MWEs that are marked in the the specified list, but not in the
	 * index.
	 * 
	 * @param <T>
	 *            the token type
	 * @param mwes
	 *            the MWEs that may be unmarked
	 * @param index
	 *            the MWE index
	 * @param missing
	 *            the set to which missing MWEs should be added
	 * @since jMWE 1.0.0
	 */
	public <T extends IToken> void findMissingMWEs(List<IMWE<T>> mwes, Map<IMWEDescID, MutableRootMWEDesc> index, Set<MutableRootMWEDesc> missing) {
		MutableRootMWEDesc rootDesc;
		MutableInfMWEDesc infDesc;
		for(IMWE<T> mwe : mwes){
			
			// ignore proper nouns
			if(mwe.getEntry().getPOS() == MWEPOS.PROPER_NOUN)
				continue;
			
			// if it exists, don't worry about it
			rootDesc = index.get(mwe.getEntry().getID());
			if(rootDesc != null)
				continue;
			
			rootDesc = new MutableRootMWEDesc(mwe.getEntry().getForm(), mwe.getEntry().getPOS());
			index.put(rootDesc.getID(), rootDesc);
			
			// if it's an inflected form, file it
			if(!mwe.getForm().equals(mwe.getEntry().getForm())){
				infDesc = new MutableInfMWEDesc(rootDesc, mwe.getForm());
				rootDesc.getInflected().put(infDesc.getForm(), infDesc);
			}
			
			// add to missing list
			if(missing != null)
				missing.add(rootDesc);
		}
	}

	/**
	 * Creates a detector that can be used to find sequences of tokens
	 * (inflected or not) that match an MWE description, but are not marked as
	 * an MWE.
	 * 
	 * @param index
	 *            the index, may not be <code>null</code>
	 * @return the detector
	 * @throws NullPointerException
	 *             if the specified index is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected IMWEDetector getUmarkedDetector(IMWEIndex index) {
		return new Consecutive(index){
			@Override
			protected Set<? extends IMWEDesc> getMWEDescs(edu.mit.jmwe.data.IToken token) {
				IMWEIndex index = getMWEIndex();
				Set<IMWEDesc> result = new HashSet<IMWEDesc>();
				result.addAll(index.getAll(token.getForm().toLowerCase()));
				if(token.getStems() != null)
					for(String stem : token.getStems())
						result.addAll(index.getAll(stem.toLowerCase()));
				return result;
			}};
	}

	/**
	 * Counts the number of MWEs that are detected by the specified detected,
	 * but not marked in the answer set as being MWEs.
	 * 
	 * @param detector
	 *            the detector to be used; may not be <code>null</code>
	 * @param sent
	 *            the sentence in which MWEs should be detected
	 * @param answers
	 *            the actual set of MWEs for the sentence
	 * @throws NullPointerException
	 *             if any argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public void countUnmarked(IMWEDetector detector, IConcordanceSentence sent, List<IMWE<IConcordanceToken>> answers) {
		
		List<IMWE<IConcordanceToken>> found = detector.detect(sent);
		
		MutableRootMWEDesc rootDesc;
		MutableInfMWEDesc infDesc;
		for(IMWE<IConcordanceToken> mwe : found){
			
			// if marked, ignore
			if(contains(answers, mwe))
				continue;
			
			// for roots
			if(mwe.getEntry() instanceof MutableRootMWEDesc){
				rootDesc = (MutableRootMWEDesc)mwe.getEntry();
				
				if(mwe.isInflected()){
					// if this is an inflected form, see if it corresponds
					// to a recorded inflected form.  If so, don't count it
					// toward the root's inflected form count
					if(rootDesc.getInflected().get(mwe.getForm()) != null)
						continue;
					rootDesc.incrementUnmarkedInflected();
					if(InflectionRule.isInflectedByPattern(mwe))
						rootDesc.incrementUnmarkedPattern();
				} else {
					rootDesc.incrementUnmarkedExact();
				}
			} 
			// for inflected forms
			else if(mwe.getEntry() instanceof MutableInfMWEDesc) {
				infDesc = (MutableInfMWEDesc)mwe.getEntry();
				if(mwe.isInflected())
					throw new IllegalStateException();
				infDesc.incrementUnmarkedExact();
				if(InflectionRule.isInflectedByPattern(mwe))
					infDesc.incrementUnmarkedPattern();
				
			} 
			// fail otherwise
			else {
				throw new IllegalStateException();
			}
		}
	}

	/**
	 * Whether the specified MWE is contained in the specified list
	 * 
	 * @param list
	 *            the list to be searched
	 * @param mwe
	 *            the MWE to look for
	 * @return <code>true</code> if the list contains the specified MWE;
	 *         <code>false</code> otherwise
	 * @since jMWE 1.0.0
	 */
	protected boolean contains(List<IMWE<IConcordanceToken>> list, IMWE<IConcordanceToken> mwe){
		for(IMWE<IConcordanceToken> answer : list)
			if(MWE.equals(answer, mwe))
				return true;
		return false;
	}

	/**
	 * Sums all the counts of the MWEs in the given map and prints the totals.
	 * 
	 * @param entries
	 *            a map of description IDs to root descriptions whose counts
	 *            will be summed and printed
	 * @since jMWE 1.0.0
	 */
	public void printTotals(Map<IMWEDescID, MutableRootMWEDesc> entries){
		
		int[] counts = new int[6];
		
		int numRoots = 0;
		int numInfs = 0;
		
		for(MutableRootMWEDesc baseForm : entries.values()){
			counts[0] += baseForm.getMarkedContinuous();
			counts[1] += baseForm.getMarkedSplit();
			counts[2] += baseForm.getUnmarkedExact();
			counts[3] += baseForm.getUnmarkedPattern();
			counts[4] += baseForm.getUnmarkedInflected();
			numRoots++;
			
			for(MutableInfMWEDesc infForm : baseForm.getInflected().values()){
				counts[0] += infForm.getMarkedContinuous();
				counts[1] += infForm.getMarkedSplit();
				counts[2] += infForm.getUnmarkedExact();
				counts[3] += infForm.getUnmarkedPattern();
				counts[5] += infForm.getMarkedPattern();
				numInfs++;
			}
		}
		
		System.out.println("Marked Continuous  : " + counts[0]);
		System.out.println("Marked Split       : " + counts[1]);
		System.out.println("Unmarked Exact     : " + counts[2]);
		System.out.println("Unmarked Pattern   : " + counts[3]);
		System.out.println("Unmarked Inflected : " + counts[4]);
		System.out.println("Marked Pattern     : " + counts[5]);
		System.out.println("RootMWEDescs    : " + numRoots);
		System.out.println("InfMWEDEsc      : " + numInfs);
	}

	/**
	 * Writes the MWEIndex data to the specified file.
	 * 
	 * @param index
	 *            the MWE index whose data should be written
	 * @param out
	 *            the output stream to which the data should be written
	 * @param headerLines
	 *            comment lines that should be inserted at the beginning of the
	 *            file. The lines may not contain linebreak (\n or \r) characters. Comment
	 *            characters are not needed at the beginning of the lines; these
	 *            are inserted by the method.  This object may be <code>null</code>.
	 * @throws IOException
	 *             if the is an error writing to the file
	 * @throws NullPointerException
	 *             if either of the first two arguments are <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public static void writeDataFile(IMWEIndex index, OutputStream out, Iterable<String> headerLines) throws IOException {
		
		Writer w = new BufferedWriter(new OutputStreamWriter(out));
		
		// double check the lines
		if(headerLines != null){
			for(String line : headerLines){
				if(line.indexOf('\n') > -1)
					throw new IllegalArgumentException("Illegal newline character in header line: " + line);
				if(line.indexOf('\r') > -1)
					throw new IllegalArgumentException("Illegal carriage return character in header line: " + line);
			}
			
			for(String line : headerLines){
				w.append("// ");
				w.append(line);
				w.append('\n');
			}
		}
		
		for(Iterator<IRootMWEDesc> i = index.getRootIterator(); i.hasNext(); ){
			RootMWEDesc.toString(i.next(), w);
			w.append('\n');
		}
		w.flush();
		w.close();
	}
	
	/**
	 * Writes the MWEIndex index to the specified file.
	 * 
	 * @param index
	 *            the MWE index whose index should be written
	 * @param out
	 *            the output stream to which the index should be written
	 * @param headerLines
	 *            comment lines that should be inserted at the beginning of the
	 *            file. The lines may not contain linebreak (\n or \r) characters. Comment
	 *            characters are not needed at the beginning of the lines; these
	 *            are inserted by the method.  This object may be <code>null</code>.
	 * @throws IOException
	 *             if the is an error writing to the file
	 * @throws NullPointerException
	 *             if either of the first two arguments are <code>null</code>
	 */
	public static void writeIndexFile(IMWEIndex index, OutputStream out, Iterable<String> headerLines) throws IOException {
		
		Writer w = new BufferedWriter(new OutputStreamWriter(out));
		
		// double check the lines
		if(headerLines != null){
			for(String line : headerLines){
				if(line.indexOf('\n') > -1)
					throw new IllegalArgumentException("Illegal newline character in header line: " + line);
				if(line.indexOf('\r') > -1)
					throw new IllegalArgumentException("Illegal carriage return character in header line: " + line);
			}
			
			for(String line : headerLines){
				w.append("// ");
				w.append(line);
				w.append('\n');
			}
		}
		
		String key;
		for(Iterator<String> keyItr = index.getIndexIterator(); keyItr.hasNext(); ){
			key = keyItr.next();
			w.append(key);
			for(IMWEDesc desc : index.getAll(key)){
				w.append(' ');
				MWEDescID.toString(desc.getID(), w);
			}
			w.append('\n');
		}
		w.flush();
		w.close();
	}
	
	/**
	 * Gets a pointer to a file that does not exist. If the specified file does
	 * not exist, this is returned. Otherwise, the file is deleted. If that
	 * fails, the file getter is queried for an alternative file and this method
	 * is called again with that new file.
	 * 
	 * @param file
	 *            the file to be deleted
	 * @param fg
	 *            the file getter that allows that supplied an alternative file
	 *            in case the specified file is not suitable
	 * @return the non-existant file finally selected
	 * @since jMWE 1.0.0
	 */
	public static File deleteFile(File file, FileGetter fg){
		
		// nothing to do, return
		if(file == null)
			return null;
		
		// if the file does not exist, just return it
		if(!file.exists())
			return file;
		
		// try to delete
		if(file.delete())
			return file;
			
		// can't delete, so try to get another
		StringBuilder msg = new StringBuilder();
		msg.append("Unable to delete the following file:\n");
		msg.append(file.getAbsolutePath());
		msg.append("\nPlease select another file in the next dialog.");
		JOptionPane.showMessageDialog(null, msg.toString(), "Overwrite Failed", JOptionPane.ERROR_MESSAGE);
		return deleteFile(fg.get(), fg);
	}
	
	/**
	 * Translates the JSemcor {@code ISemanticTag} to a jMWE {@link MWEPOS}
	 * object.
	 * 
	 * @param tag
	 *            the semantic tag to be translated
	 * @return the equivalent MWEPOS object.
	 * @since jMWE 1.0.0
	 */
	public static MWEPOS toMWEPOS(ISemanticTag tag){
		String lexsn = tag.getLexicalSense().get(0);
		int pos = Integer.parseInt(lexsn.substring(0, 1));
		switch(pos){
			case 1 : return MWEPOS.NOUN;
			case 2 : return MWEPOS.VERB;
			case 3 : return MWEPOS.ADJECTIVE;
			case 4 : return MWEPOS.ADVERB;
			case 5 : return MWEPOS.ADJECTIVE;
		}
		throw new IllegalStateException();
	}

	/**
	 * Wouldn't it be nice to have first-class functions in Java?
	 *
	 * @author M.A. Finlayson
	 * @version $Id: IndexBuilder.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	public static interface FileGetter {
		public File get();
	}

	public interface IMutableMWEDesc extends IMWEDesc {
		public void incrementMarkedContinuous();
		public void incrementMarkedSplit();
		public void incrementUnmarkedExact();
		public void incrementUnmarkedPattern();
	}
	
	/**
	 * A root MWE description object whose counts can be incremented. 
	 *
	 * @author markaf
	 * @version 1.612, 06 May 2011
	 * @since jMWE 1.0.0
	 */
	public static class MutableRootMWEDesc extends RootMWEDesc implements IMutableMWEDesc {
		
		private Map<String, MutableInfMWEDesc> infForms = new TreeMap<String, MutableInfMWEDesc>();

		/**
		 * Constructs the mutable root description that has no inflected forms
		 * with the given surface form and pos, initialized with zero for all 5
		 * counts
		 * 
		 * @param surfaceForm
		 *            A string representing the MWE with its words separated by
		 *            underscores
		 * @param pos
		 *            The part of speech object representing the part of speech
		 *            of the MWE
		 * @throws NullPointerException
		 *             if either argument is <code>null</code>
		 * @throws IllegalArgumentException
		 *             if the surface form does not contain underscores
		 */
		public MutableRootMWEDesc(String surfaceForm, MWEPOS pos) {
			super(surfaceForm, pos);
		}
		
		public void incrementMarkedContinuous()  { counts[0]++; }
		public void incrementMarkedSplit()       { counts[1]++; }
		public void incrementUnmarkedExact()     { counts[2]++; }
		public void incrementUnmarkedPattern()   { counts[3]++;	}
		public void incrementUnmarkedInflected() { counts[4]++; }

		/* 
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.RootMWEDesc#getInflected()
		 */
		@Override
		public Map<String, MutableInfMWEDesc> getInflected() {
			return infForms;
		}
		
	}
	
	public static class MutableInfMWEDesc extends InfMWEDesc implements IMutableMWEDesc {

		/**
		 * Constructs a new MWE description object from the specified root
		 * description and inflected form, initialized to zero for all counts.
		 * 
		 * @param root
		 *            the root description of the MWE
		 *@param inflectedForm
		 *            A string representing the inflected MWE with its words
		 *            separated by underscores
		 * @throws NullPointerException
		 *             if either argument is <code>null</code>
		 * @throws IllegalArgumentException
		 *             if the inflected form does not contain underscores
		 * @since jMWE 1.0.0
		 */
		public MutableInfMWEDesc(IRootMWEDesc root, String inflectedForm) {
			super(root, inflectedForm);
		}
		
		public void incrementMarkedContinuous() { counts[0]++; }
		public void incrementMarkedSplit()      { counts[1]++; }
		public void incrementUnmarkedExact()    { counts[2]++; }
		public void incrementUnmarkedPattern()  { counts[3]++; }
		public void incrementMarkedPattern()    { counts[4]++; }
		
	}
	
}

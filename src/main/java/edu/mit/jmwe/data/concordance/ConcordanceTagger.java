/********************************************************************************
 * Java MWE Library (jMWE) v1.0.2
 * Copyright (c) 2008-2015 Mark A. Finlayson & Nidhi Kulkarni
 *
 * This program and the accompanying materials are made available under the
 * terms of the jMWE License which accompanies this distribution.
 * Please check the attached license for more details.
 *******************************************************************************/

package edu.mit.jmwe.data.concordance;

import edu.mit.jmwe.util.*;
import edu.mit.jsemcor.element.*;
import edu.mit.jsemcor.main.IConcordance;
import edu.mit.jsemcor.main.IConcordanceSet;
import edu.mit.jsemcor.main.Semcor;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.ListProcessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Tags with parts of speech all words in all contexts provided in a given concordance set.
 * <p>
 * This class depends on three external libraries: JWI, JSemcor, and the Stanford POS Tagger.
 * <p>
 * Use the main method of this class for its default functionality.
 *
 * @see TaggedConcordanceIterator
 * @author M.A. Finlayson
 * @author N. Kulkarni
 * @version $Id: ConcordanceTagger.java 356 2015-11-25 22:36:46Z markaf $
 * @since jMWE 1.0.0
 */
public class ConcordanceTagger extends AbstractFileSelector implements Runnable {

	/**
	 * Tags the Semcor corpus. Running this method will prompt the user for the
	 * following locations:
	 * <ol>
	 * <li>a directory containing JSemcor-compatible concordance data files,
	 * e.g., SemCor</li>
	 * <li>a directory containing JWI-compatible electronic dictionary files,
	 * e.g., Wordnet</li>
	 * <li>a Stanford-POS-Tagger-compatible tagging model, e.g.,
	 * left3words-wsj-0-18.tagger</li>
	 * <li>a file to which the tagged data should be written</li>
	 * </ol>
	 *
	 * The resulting file in (4) can be used via the {@link TaggedConcordanceIterator} class.
	 *
	 * @param args
	 *            standard main method arguments; ignored
	 * @since jMWE 1.0.0
	 */
	public static void main(String[] args){
		ConcordanceTagger tagger = new ConcordanceTagger();
		tagger.run();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		// semcor
		IConcordanceSet semcor = getSemcor();
		if(semcor == null)
			return;
		// stemmer
		IStemmer stemmer = getStemmer();
		if(stemmer == null)
			return;
		// tagger
		ListProcessor<List<? extends HasWord>,List<TaggedWord>> posTagger;
		try {
			posTagger = getPOSTagger();
			if(posTagger == null)
				return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// output
		Writer writer;
		try {
			writer = getWriter();
			if(writer == null)
				return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// figure out how much work we have to do
		int num = 0;
		for(IConcordance c : semcor.values())
			num += c.getContextIDs().size();
		IProgressBar pb = new ProgressBar(num);

		// do the actual work
		try {
			process(semcor.values(), posTagger, stemmer, writer, pb);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// clean up
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Semcor concordance set or <code>null</code> if the directory
	 * cannot be found.
	 *
	 * @return the Semcor concordance set or <code>null</code> if the directory
	 *         cannot be found.
	 * @since jMWE 1.0.0
	 */
	protected IConcordanceSet getSemcor() {
		File dir = chooseDirectory("Select Semcor Directory", IConcordanceSet.class);
		if(dir == null)
			return null;
		IConcordanceSet semcor = new Semcor(StreamAdapter.toURL(dir));
		if(!semcor.open())
			throw new IllegalStateException("Unable to open Semcor");
		return semcor;
	}

	/**
	 * Returns a stemmer that requires Wordnet or <code>null</code> if the
	 * Wordnet directory cannot be found.
	 *
	 * @return a stemmer that requires Wordnet or <code>null</code> if the
	 *         Wordnet directory cannot be found.
	 * @since jMWE 1.0.0
	 */
	protected IStemmer getStemmer() {
		File dir = chooseDirectory("Select Wordnet Directory", IDictionary.class);
		if(dir == null)
			return null;
		IDictionary wordnet = new Dictionary(StreamAdapter.toURL(dir));
		try {
			if(!wordnet.open())
                throw new IllegalStateException("Unable to open Wordnet");
		} catch (IOException e) {
			throw new IllegalStateException("Unable to open Wordnet",e);
		}
		return new WordnetStemmer(wordnet);
	}

	/**
	 * Returns a maximum entropy tagger using a Stanford NLP tagging model
	 * selected by the user. Will return <code>null</code> if no model is
	 * selected or found.
	 *
	 * @return a {@code MaxentTagger} using a Stanford NLP tagging model
	 *         selected by the user. Will return <code>null</code> if no model
	 *         is selected or found.
	 * @throws Exception
	 *             if there is a problem instantiating the maximum entropy
	 *             tagger.
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("unchecked")
	protected ListProcessor<List<? extends HasWord>,List<TaggedWord>> getPOSTagger() throws Exception {
		File file = chooseFile("Select StanfordNLP Tagging Model", ListProcessor.class);
		if(file == null)
			return null;

		return new MaxentTagger(file.getAbsolutePath());
	}

	/**
	 * Returns a writer for the file to which the tagged concordance will be
	 * written. The file is selected by the user. Will return <code>null</code>
	 * if no output file is selected.
	 *
	 * @return a writer for the file to which the tagged concordance will be
	 *         written. Will return <code>null</code> if no output file is
	 *         selected.
	 * @throws IOException
	 *             if an exception occurs when constructing the file writer
	 * @since jMWE 1.0.0
	 */
	protected Writer getWriter() throws IOException {
		File file = chooseFileForWriting("Select Output File", Writer.class);
		if(file == null)
			return null;
		setLocation(Writer.class, file);
		return new BufferedWriter(new FileWriter(file));
	}

	/**
	 * Utility method for getting a location that has a default stored in the
	 * Java preferences.
	 *
	 * @param key
	 *            the class that serves as key for this location
	 * @return the path to the stored location, or <code>null</code> if none
	 * @since jMWE 1.0.0
	 */
	protected File getLocation(Class<?> key){
		Preferences node = Preferences.userNodeForPackage(ConcordanceTagger.class);
		String path = node.get(key.getCanonicalName(), null);
		return (path == null) ? null : new File(path);
	}

	/**
	 * Sets a default location into the Java Preferences.
	 *
	 * @param key
	 *            the class that serves as key for this location
	 * @param loc
	 *            the location to be saved to the preferences
	 * @since jMWE 1.0.0
	 */
	protected void setLocation(Class<?> key, File loc){
		Preferences node = Preferences.userNodeForPackage(ConcordanceTagger.class);
		node.put(key.getCanonicalName(), loc.getAbsolutePath());
	}

	/**
	 * Tags the all contexts provided by the concordance set, using the
	 * specified tagger, writing the data to the specified writer. This method
	 * does not close the writer when finished.
	 *
	 * @param cs
	 *            the concordance set from which contexts should be drawn, may
	 *            not be <code>null</code>
	 * @param posTagger
	 *            the part of speech tagger to be used to tag the sentences, may
	 *            not be <code>null</code>
	 * @param stemmer
	 *            a stemmer used to stem words
	 * @param writer
	 *            the writer to which results should be written, may not be
	 *            <code>null</code>
	 * @param pb
	 *            the progress bar to which progress is to be reported; may be
	 *            <code>null</code>
	 * @throws IOException
	 *             if there is a problem writing to the provided writer
	 * @throws NullPointerException
	 *             if any argument is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public void process(Iterable<? extends IConcordance> cs,
						ListProcessor<List<? extends HasWord>,List<TaggedWord>> posTagger,
						IStemmer stemmer, Writer writer, IProgressBar pb) throws IOException {
		process(null, -1, cs, posTagger, stemmer, writer, pb);
	}

	/**
	 * Tags the all contexts provided by the concordance set, using the
	 * specified tagger, writing the data to the specified writer. The method
	 * skips ahead past the sentence number in the specified context. If the
	 * context does not occur in the specified concordance set, this method will
	 * do nothing. This method does not close the writer.
	 *
	 * @param startContext
	 *            the context where the tagging should begin. If
	 *            <code>null</code>, the tagging will being with the first
	 *            context.
	 * @param startSent
	 *            the sentence number past which tagging should being. If the
	 *            number is non-positive, no sentences in the specified context
	 *            are skipped
	 * @param cs
	 *            the concordance set from which contexts should be drawn, may
	 *            not be <code>null</code>
	 * @param posTagger
	 *            the part of speech tagger to be used to tag the sentences, may
	 *            not be <code>null</code>
	 * @param stemmer
	 *            a stemmer used to stem words
	 * @param writer
	 *            the writer to which results should be written, may not be
	 *            <code>null</code>
	 * @param pb
	 *            the progress bar to which progress is to be reported; may be
	 *            <code>null</code>
	 * @throws IOException
	 *             if there is a problem writing to the provided writer
	 * @throws NullPointerException
	 *             if any of the concordance set, tagger, or writer are
	 *             <code>null</code>
	 * @since jMWE 1.0.0
	 */
	public void process(IContextID startContext, int startSent, Iterable<? extends IConcordance> cs,
						ListProcessor<List<? extends HasWord>,List<TaggedWord>> posTagger,
						IStemmer stemmer, Writer writer, IProgressBar pb) throws IOException {

		if(cs == null)
			throw new NullPointerException();
		if(writer == null)
			throw new NullPointerException();

		if(pb == null)
			pb = NullProgressBar.getInstance();

		if(startContext != null)
			System.out.println("Starting at context " + startContext.toString());
		if(startSent > 0)
			System.out.println("Starting at sentence " + startSent);

		IConcordance concord;
		IContext context;
		ISentence sent;

		for(Iterator<? extends IConcordance> concordItr = cs.iterator(); concordItr.hasNext(); ){
			concord = concordItr.next();

			for(Iterator<IContext> contextItr = concord.iterator(); contextItr.hasNext(); ){
				context = contextItr.next();

				// consume contexts up to specified context id
				if(startContext != null){
					if(context.getID().equals(startContext)){
						startContext = null;
					} else {
						continue;
					}
				}

				for(Iterator<ISentence> sentItr = context.getSentences().iterator(); sentItr.hasNext(); ){
					sent = sentItr.next();
					// consume sentences up to and including a specified sentence number
					if(startSent > 0){
						if(sent.getNumber() > startSent){
							startSent = -1;
						} else {
							continue;
						}
					}

					// process sentence
					process(context.getID(), sent, posTagger, stemmer, writer);

					// if there is another sentence, write a newline
					if(sentItr.hasNext())
						writer.append('\n');
				}

				// if there is another context, write a newline
				if(contextItr.hasNext())
					writer.append('\n');

				// finished processing this context
				pb.increment();
			}

			// if there is another concordance, write a newline
			if(concordItr.hasNext())
				writer.append('\n');
		}
		pb.finish();
	}

	/**
	 * Tags the provided sentence, using the specified tagger, writing the data
	 * to the specified writer. TThis method does not close the writer.
	 *
	 * @param cid
	 *            the context containing the sentence
	 * @param s
	 *            the sentence being tagged
	 * @param posTagger
	 *            the part of speech tagger to be used to tag the sentences, may
	 *            not be <code>null</code>
	 * @param stemmer
	 *            the stemmer used to stem the tokens, may not be
	 *            <code>null</code>
	 * @param writer
	 *            the writer to which results should be written, may not be
	 *            <code>null</code>
	 * @throws IOException
	 *             if there is a problem writing to the provided writer
	 * @throws NullPointerException
	 *             if any of the sentence, tagger, or writer are
	 *             <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected void process(IContextID cid, ISentence s,
						   ListProcessor<List<? extends HasWord>,List<TaggedWord>> posTagger,
						   IStemmer stemmer, Writer writer) throws IOException {

		// construct sentence for tagger
		List<List<HasWord>> input = new ArrayList(makeSentence(s, stemmer));

		// tag sentence
		List<List<TaggedWord>> output = posTagger.process(input);

		// write context id and sentence number
		writer.append(cid.getConcordanceName());
		writer.append('/');
		writer.append(cid.getContextName());
		writer.append('/');
		writer.append(Integer.toString(s.getNumber()));
		writer.append(' ');

		// write each token and its tag
		TaggerToken token;
		for(int i = 0; i < input.size(); i++){
			token = (TaggerToken)input.get(i);
			token.setTag(output.get(0).get(i).tag());
			writer.append(ConcordanceToken.toString(token));
			if(i < input.size())
				writer.append(' ');
		}
	}

	/**
	 * Returns a Stanford parser sentence that contains all the tokens from the
	 * specified JSemcor sentence, with MWE expressions broken into their
	 * constituent tokens. Each new token is marked with a token number and part
	 * number that indicates its source {@code IToken} object in the original
	 * semcor sentence.
	 *
	 * @param s
	 *            a JSemcor {@code ISentence} object to be transformed
	 * @param stemmer
	 *            the stemmer to use when making the words
	 * @return a sentence object consisting of just the tokens, with MWEs split
	 *         into their constituent tokens
	 * @throws NullPointerException
	 *             if the specified sentence is <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected ArrayList<HasWord> makeSentence(ISentence s, IStemmer stemmer){
		List<TaggerToken> result = new LinkedList<TaggerToken>();
		IToken token;
		for(int i = 0; i < s.size(); i++){
			token = s.get(i);
			if(token instanceof IWordform){
				addWords((IWordform)token, i, result, stemmer);
			} else {
				result.add(new TaggerToken(token.getText(), i, 0, null));
			}
		}
		return new ArrayList<HasWord>(result);
	}

	/**
	 * Stems each of the words in the provided wordform, adding the tagger
	 * tokens created from these stems, words and token number to the given
	 * results list.
	 *
	 * @param wf
	 *            the wordform whose constituent words are to be stemmed
	 * @param tokenNum
	 *            the number of the token to be tagged, inside the wordform
	 * @param result
	 *            the list to which the tagger tokens will be added
	 * @param stemmer
	 *            the stemmer used to stem the tokens, may not be
	 *            <code>null</code>
	 * @since jMWE 1.0.0
	 */
	protected void addWords(IWordform wf, int tokenNum, List<TaggerToken> result, IStemmer stemmer){
		List<String> tokens = wf.getConstituentTokens();
		List<String> stems;
		String token;
		for(int i = 0; i < tokens.size(); i++){
			token = tokens.get(i);
			stems = stem(token, wf, stemmer);
			result.add(new TaggerToken(tokens.get(i), tokenNum, i, stems));
		}
	}

	/**
	 * Stems the given token.
	 *
	 * @param token
	 *            the token to be stemmed
	 * @param wf
	 *            the wordform from which the token is drawn the wordform from
	 *            which the token is drawn
	 * @param stemmer
	 *            the stemmer used to stem the tokens, may not be
	 *            <code>null</code>
	 * @return a list of the stems of the given token if the given wordform has
	 *         no specified/recognizable part of speech or has more than one
	 *         constituent token. Otherwise, returns <code>null</code>.
	 *
	 * @since jMWE 1.0.0
	 */
	protected List<String> stem(String token, IWordform wf, IStemmer stemmer){
		POS pos = JWIPOS.toPOS(wf.getPOSTag());
		boolean shouldStem = wf.getConstituentTokens().size() > 1 || pos != null;
		List<String> stems = null;
		if(shouldStem){
			stems = stemmer.findStems(token, pos);
			for(Iterator<String> itr = stems.iterator(); itr.hasNext(); )
				if(itr.next().trim().length() == 0)
					itr.remove();
		}
		return stems;
	}

	/**
	 * Represents a semcor token that is not yet tagged.
	 *
	 * @author M.A. Finlayson
	 * @version $Id: ConcordanceTagger.java 356 2015-11-25 22:36:46Z markaf $
	 * @since jMWE 1.0.0
	 */
	@SuppressWarnings("serial")
	protected static class TaggerToken extends ConcordanceToken implements HasWord {

		// instance fields
		private final String word;
		private String tag;

		/**
		 * Constructs a new tagger token object with the specified word, token
		 * number, part number and stems.
		 *
		 * @param surfaceText
		 *            the surface form of the token as it appears in the
		 *            sentence, capitalization intact
		 * @param tokenNum
		 *            the token number. Must be greater than or equal to 0.
		 * @param partNum
		 *            the part number representing the index of the token in a
		 *            multi-word expression, 0 if it is not part of one. Must be
		 *            greater than or equal to 0.
		 * @param stems
		 *            the list of stems, possibly empty or <code>null</code>
		 *
		 * @throws NullPointerException
		 *             if the text is <code>null</code>
		 * @throws IllegalArgumentException
		 *             if the text is empty or all whitespace or if the token
		 *             number or part number is less than 0.
		 * @since jMWE 1.0.0
		 */
		public TaggerToken(String surfaceText, int tokenNum, int partNum, List<String> stems){
			super(surfaceText, null, tokenNum, partNum, (stems == null) ?
					new String[0] :
					stems.toArray(new String[stems.size()]));
			PTBTokenizer<Word> tok = PTBTokenizer.newPTBTokenizer(new StringReader(surfaceText));
			List<Word> tokens = tok.tokenize();
			this.word = (tokens.size() == 1) ? tokens.get(0).word() : surfaceText;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see edu.stanford.nlp.ling.HasWord#word()
		 */
		public String word() {
			return word;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see edu.stanford.nlp.ling.HasWord#setWord(java.lang.String)
		 */
		public void setWord(String word) {
			throw new UnsupportedOperationException();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see edu.mit.jmwe.data.Token#getTag()
		 */
		@Override
		public String getTag() {
			return tag;
		}

		/**
		 * Sets the tag field of this tagger token to the specified tag.
		 *
		 * @param tag
		 *            the tag to be assigned to this token
		 * @since jMWE 1.0.0
		 */
		public void setTag(String tag){
			this.tag = tag;
		}
	}
}


package stanford.nlp.pipeline;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.JMWEAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class JMWEAnnotatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(JMWEAnnotatorTest.class);


    @Test
    public void annotate(){

        String index    = new File("src/test/resources/mweindex_wordnet3.0_Semcor1.6.data").getAbsolutePath();

        String text     = "She looked up the world record.";

        // creates the properties for Stanford CoreNLP: tokenize, ssplit, pos, lemma, jmwe
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "false");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.indexData", index);
        props.setProperty("customAnnotatorClass.jmwe.detector", "CompositeConsecutiveProperNouns");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // put the text in the document annotation
        Annotation doc = new Annotation(text);

        // run the CoreNLP pipeline on the document
        pipeline.annotate(doc);

        // loop over the sentences
        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
        System.out.println();
        for(CoreMap sentence: sentences) {
            System.out.println("Sentence: "+sentence);
            // loop over all discovered jMWE token and perform some action
            for (IMWE<IToken> token: sentence.get(JMWEAnnotator.JMWEAnnotation.class)) {
                System.out.println("IMWE<IToken>: "+token+", token.isInflected(): "+token.isInflected()+", token.getForm(): "+token.getForm());
            }
            System.out.println();
        }


    }


}

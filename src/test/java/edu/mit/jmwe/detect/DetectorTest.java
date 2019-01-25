package edu.mit.jmwe.detect;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.MWEIndex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DetectorTest {

    private static final Logger LOG = LoggerFactory.getLogger(DetectorTest.class);

    @Test
    public void multiwords() throws IOException {

        // get handle to file containing MWE index data, // e.g., mweindex_wordnet3.0_Semcor1.6.data
        File idxData = new File("src/test/resources/mweindex_wordnet3.0_Semcor1.6.data");

        // construct an MWE index and open it
        IMWEIndex index = new MWEIndex(idxData);
        index.open();


        // make a basic detector
        IMWEDetector detector = new Consecutive(index);

        // construct a test sentence:
        // "She looked up the world record."
        List<IToken > sentence = new ArrayList<IToken >();
        sentence.add(new Token("She", "DT"));
        sentence.add(new Token("looked", "VBT", "look"));
        sentence.add(new Token("up", "RP"));
        sentence.add(new Token("the", "DT"));
        sentence.add(new Token("world", "NN"));
        sentence.add(new Token("record", "NN"));
        sentence.add(new Token(".", "."));

        // run detector and print out results
        List<IMWE<IToken>> mwes = detector.detect(sentence);
        for(IMWE <IToken > mwe : mwes){
            LOG.info(">" + mwe);
        }

    }

}

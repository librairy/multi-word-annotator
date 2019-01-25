# multi-word-annotator
A lexicon-based multi-word annotator for [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/) 

It is built on the top of [jMWE](http://projects.csail.mit.edu/jmwe/) and [CoreNLP](https://github.com/toliwa/CoreNLP) to use the latest version of [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/) by Maven 

## Use 

Maven users will need to add the following dependency to their pom.xml for this component:

```xml
<dependency>
    <groupId>org.librairy</groupId>
    <artifactId>multi-word-annotator</artifactId>
    <version>1.0</version>
</dependency>
```

And this repository definition at the end of your *pom.xml*:

 ```xml
 <repositories>
     <!-- GitHub Repository -->
     <repository>
         <id>librairy-multi-word-annotator</id>
         <url>https://raw.github.com/librairy/multi-word-annotator/mvn-repo/</url>
         <snapshots>
             <enabled>true</enabled>
             <updatePolicy>always</updatePolicy>
         </snapshots>
     </repository>
 </repositories>
 ```

## Demo

The test class `JMWEAnnotatorTest` shows a basic use case of the new annotator, given a String it will detect and print out MWE information:


```java
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
```
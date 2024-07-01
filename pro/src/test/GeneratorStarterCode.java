package test;

import java.util.ArrayList;
import java.util.List;

class Generator{
  private InputDocument inputDoc;

  // compression ratio for the text summary
  // i.e., here compression is 1/3 of the original text
  private double compressRatio = 0.3;

  private String[] keywords = null;

  private TermCollection termCollection;

  public void loadFile(String inputFile){
    inputDoc = new InputDocument();
    inputDoc.loadFile(inputFile);
  }

  public void setKeywords(String[] keywords){

    List<String> processedTermList = new ArrayList<String>();
    TermPreprocessor tp = new TermPreprocessor();

    String resultTerm = null;
    for(String term : keywords){
      resultTerm = tp.preprocess(term);

      if(resultTerm !=null)
        processedTermList.add(resultTerm);
    }

    this.keywords = processedTermList.toArray(new String[processedTermList.size()]);

  }

  public String generateSummary(){
    String[] significantSentences = generateSignificantSentences();
    // ***TO WRITE REST OF CODE
        return null;
  }

  public String[] generateSignificantSentences(){
    String[] allSentences = getAllSentences();
    double[] scores = calcAllSentenceScores();
    // ***TO WRITE REST OF CODE
                        // to generate Significant Sentences we can add the scores of individual words... to even out the odd of a
                        //longer sentences we can use the avg.. ie score/length of sentence where length is no of words in the sentence
    return null;
  }

  public String[] getAllSentences(){
    return inputDoc.getAllSentences();
  }

  public double[] calcAllSentenceScores(){
    // ***TO WRITE REST OF CODE
      return null;
  }

}
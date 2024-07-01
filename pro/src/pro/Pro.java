/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Sudeshna
 */
public class Pro {

    /**
     * @param args the command line arguments
     */
    static FileReader file_in = null;
    static String input;
    static HashMap<String, Integer> scores_lookup = new HashMap<String, Integer>();
    static HashMap<String, Integer> frequent = new HashMap<String, Integer>();
    static TreeMap<String, Integer> sorted_map = null, sorted_score_map = null;
    static HashMap<String, Integer> sentence_score = null;
    static String[] sentences = null;
        
    static String msg=null;
    
    /**
     *
     */
    public static String summary(String data,String path, String filename) {
       
        try {
            // data contains the content to summarize
            input = data;
            //    System.out.println(input);        //testing the input
            String summ[] = summarize();
            //    System.out.println(input);          //testing after the Summarization
            writeOutput(summ,path, filename);
            
            msg="Output File is generated at "+path+"Summary_"+filename;
        } catch (Exception e) {
            System.out.println("\n\tA Unforeseen exception has crashed the application! Check File Format");
            e.printStackTrace();
            msg="Unforeseen exception has crashed the application!  Please select *.txt file";
        }   
        
        finally{
            return msg;
        }

    }

    private static String[] summarize() throws Exception {
        /*
         * This method takes the input and processes the summarization and returns the top x lines for the summary
         */

        //1.calculating the word frequency for words
        calWordFrequency();

        //2.making a score list lookup table to score words
        makeScoreList();

        //3.scoring the sentences of the input
        scoreSentences();

        //4. selecting the top n sentences for the summary
        return final_summary();

    }

    private static void makeScoreList() throws Exception {
        /*   Todo:
         * this creates a scoring matrix which contains all the words and their repective scores
         *  ----- scoring policy: stopwords:0, cue words:4, common:2, names:3, word frequency:5 ------
         */
        //scores to stop words is: 0      stopwords are stored in eliminate.txt

        System.out.println("Generating the score lookup table");

        FileReader eFile = new FileReader("eliminate.txt");
        BufferedReader in = new BufferedReader(eFile);
        String data;
        while ((data = in.readLine()) != null) {
            data = data.toLowerCase();
            scores_lookup.put(data, new Integer(0));
        }
        in.close();
        eFile.close();

        //scores to cue words: 4
        eFile = new FileReader("cuewords.txt");
        in = new BufferedReader(eFile);
        while ((data = in.readLine()) != null) {
            data = data.toLowerCase();
            scores_lookup.put(data, new Integer(4));
        }
        in.close();
        eFile.close();

        //scores to frequent words: 2
        eFile = new FileReader("common.txt");
        in = new BufferedReader(eFile);
        while ((data = in.readLine()) != null) {
            data = data.toLowerCase();
            scores_lookup.put(data, new Integer(2));
        }
        in.close();
        eFile.close();

        //scores to names: 3
        eFile = new FileReader("names.txt");
        in = new BufferedReader(eFile);
        while ((data = in.readLine()) != null) {
            data = data.toLowerCase();
            scores_lookup.put(data, new Integer(3));
        }

        //assigning scores to words that have higher freqencies i.e, value of 5

        //selecting the top 1/3 of the frequent words, hence we have a probable topic of the content

        int max_score = frequent.get(sorted_map.firstKey()).intValue();
        for (String s : sorted_map.toString().replaceAll("[{}]", "").split(", ")) {
            String c[] = s.split("=");                   // since the output is in the pattern <key>=<value>
            if (Integer.valueOf(c[1]) >= (int) max_score / 3) {
                scores_lookup.put(c[0], new Integer(5));
            }
        }

        scores_lookup.remove("");  // removing the null string score as its not important
         System.out.println(scores_lookup);


    }

    private static void writeOutput(String[] summary,String path ,String file) throws Exception {
        /*
         * generates the output file
         */
        
        System.out.println("length="+summary.length);
        
        FileWriter fout = new FileWriter(path+"Summary_" + file);
        BufferedWriter out = new BufferedWriter(fout);
        System.out.println("\n\nGenerating the output summary:\n------------------------------------------------------------");
        for (String s : summary) {
            System.out.println(s);
            out.write(s);
            out.newLine();
        }
        out.close();
        fout.close();
    }

    private static void calWordFrequency() throws Exception {
        /*
         * calculates the word frequency of all the words in the input
         */
        System.out.println("calculating the word frequency of the words");
        //step 1: removing all the stop words so as to have the important content in the text only for freqency calculation
        FileReader eFile = new FileReader("eliminate.txt");
        BufferedReader in = new BufferedReader(eFile);
        String data = "", temp = input.toLowerCase();
        //removing all the stop words
        while ((data = in.readLine()) != null) {
            temp = temp.replaceAll(("[ \t\n]" + data.toLowerCase() + "[ \t\n]"), " ");          //removes the words that occurs in the middle of the text
            temp = temp.replaceAll(("^" + data.toLowerCase() + "[ \t\n]"), "");               //removes the words in the starting of the line
            temp = temp.replaceAll("[ \t]" + data.toLowerCase() + "\\.,", ".");               //removes the words in the end of the line
        }

        //spliting all the sentences using punctuation marks
        for (String word : temp.split("[ \t\n\r\f\\(\\).,;:!?\"]")) {

            // for each word searching in the frequent list if exists incrementing the count else intializing the new word to 1
            if (!word.matches("[ \t\n\r\f.,;:!?\"]") && word != null) {
                Integer count = frequent.get(word);
                if (count != null) {
                    frequent.put(word, count + 1);
                } else {
                    frequent.put(word, 1);
                }
            }
        }


        ValueComparator bvc = new ValueComparator(frequent);
        sorted_map = new TreeMap<String, Integer>(bvc);


        //System.out.println("unsorted map: " + frequent);
        //System.out.println(frequent.get(""));
        frequent.remove("");

        sorted_map.putAll(frequent);

        //System.out.println("results: " + sorted_map);

    }

    private static void scoreSentences() {
        System.out.println("sentence Scoring started");
        /*
         * this is used to score the sentences and select the top n sentences
         */
        sentences = input.split("\\.");    //dividing the input into sentences
        int avgLen = 0;
        // calculating the average length of the sentences
        for (String s : sentences) {
            avgLen += s.length();
        }

        avgLen /= sentences.length;
        /* scoring all the sentences */
        sentence_score = new HashMap<String, Integer>();
        int i = 0;
        for (String s : sentences) {
            //for each sentence calculating the score
            int sc = 0;       //initializing the score of the sentence to 0
            for (String w : s.split(" ")) {
                //for each word getting the score from lookup hashmap adding else adding 0 to score
                if (scores_lookup.containsKey(w)) {
                    sc += scores_lookup.get(w);
                }
            }
            
            sentence_score.put(String.valueOf(i), new Integer(sc * avgLen / (s.length()!=0?s.length():1)));       //generating the final score
            System.out.println(" Sentence :" + i + "--- Score:" + sc);
            i++;
        }

        /* sorting the sentences with respect to score */


        ValueComparator bvc = new ValueComparator(sentence_score);
        sorted_score_map = new TreeMap<String, Integer>(bvc);
        //System.out.println(sentence_score);
        sorted_score_map.putAll(sentence_score);
        System.out.println(sorted_score_map);

    }

    private static String[] final_summary() {
        /* returns the top n setences for the final summary */
        String summary[] = null;

        int max_score = 0;
        ArrayList<Integer> summ_sentences = new ArrayList<Integer>();      //storing the sentence no for the summary


        String list[] = sorted_score_map.toString().replaceAll("[{}]", "").split(", ");

        System.out.println(sorted_score_map);
        max_score = sentence_score.get(sorted_score_map.firstKey());

        
        int x=0;
/*        for(int score=max_score;score>=(max_score*.66);score=sentence_score.get(x++)){
         
                summ_sentences.add(new Integer(sc[0]));
            
        }
        
  */         
                // calculating the precentage of the summary to be formed
        
        float percentage=(sentences.length>20? 0.5f : 0.8f );
        
        
        for (String s : list) {
            String sc[] = s.split("=");
            if (Integer.valueOf(sc[1]) <= (int) max_score *percentage) {  //selecting 1/3 sentences or selecting top 1/3 scores
                //selecting top 1/3 sentences
                summ_sentences.add(new Integer(sc[0]));
                x++;
            }
        }

        summary = new String[summ_sentences.size()];
        for (int k = 0, i = 0; i < list.length; i++) {
            if (summ_sentences.contains(i)) {
                summary[k++] = sentences[i];
            }
        }

        return summary;
    }
}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}



/*----------------------------------------------------------------------------------------------------------------------------------------------------  
 * //Test code don't include in main source
 
   private static void eliminate() throws Exception {
        FileReader eFile = new FileReader("eliminate.txt");
        BufferedReader in=new BufferedReader(eFile);
        String data;
        while((data=in.readLine())!=null){
            //  Deleting all the frequent words from the input text so what we can rate the sentences.
            data=data.toLowerCase();
            //System.out.print(data+"\t\t\t");
            input=input.replaceAll(("[ \t\n]"+data+"[ \t\n]")," ");          //removes the words that occurs in the middle of the text
            input=input.replaceAll(("^"+data+"[ \t\n]")," ");               //removes the words in the starting of the line
            input=input.replaceAll("[ \t]"+data+"\\.,",".");               //removes the words in the end of the line
            //System.out.println(input);
        }
        System.out.println("After Elimination of Stopwords:\n"+input);
    }
 
 
 */
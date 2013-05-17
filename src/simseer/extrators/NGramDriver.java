import java.util.LinkedList;

public class NGramDriver{

    public static void main (String [] args){
	try{
	    NGramExtractor extractor = new NGramExtractor();
	    extractor.extract("please extract n-grams, ok thanks, ok thanks", 2, true, true);
	    LinkedList<String> ngrams = extractor.getNGrams();
	    for (String s : ngrams){
		System.out.println("Ngram '" + s + "' occurs " + extractor.getNGramFrequency(s) + " times");
	    }
	}
	catch (Exception e){
	    System.err.println(e.toString());
	}
    }
}

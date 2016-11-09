package com.uspresidentials.project.task2;

import java.io.IOException;
import java.util.ArrayList;

import com.uspresidentials.project.utils.PropertiesManager;


/*           ********Supporters and Opponents********
 * 
 * 1.   You can download Sentiwordnet from http://sentiwordnet.isti.cnr.it/  and apply  some very simple algorithm 
 * (e.g. sum of opinionated words in a tweet). Take care of negations: a negation changes the sign of the sentiment of the 
 * 	following word. 
 * 2.  You can also modify Sentiwordnet weights if you feel that, in this domain, the sentiment of certain words should be 
 * different wrt Sentiwordnet. Any other solution you can think of is ok. Just explain what you did and why. 
 * The result should be a reliable classification (NOT MANUAL!!) of those users into supporters, opponents and neutral. 
 * If you feel you can apply the same procedure to all users mentioning a candidate you get extra credit.
 * 
 * 
 * 
 * PER LE NEGAZIONI DARE UN OCCHAITA QUI http://sentiment.christopherpotts.net/lingstruc.html#negation
*/

public class SentimentMain {
	
	private static final String PATH_SENTIMENT_CANDIDATE_FILE = PropertiesManager.getPropertiesFromFile("PATH_SENTIMENT_CANDIDATE_FILE");
	
	static ArrayList<String> posWords = new ArrayList<String>() {{
        add("good"); add("best"); add("better"); add("positive"); add("truly");
    }};
    
    static ArrayList<String> negWords = new ArrayList<String>() {{
        add("bad"); add("worst"); add("worthless"); add("negative"); add("not");
    }};
	
	
	public static void main(String[] args) throws IOException {
//		if (args.length < 1) {
//			System.err.println("Sentiments checker : analizza il file: " +PATHS_ENTIMENT_WORDNET_FILE );
//			return;
//		}

		//test con algoritmo preso dal sito
		SentimentsChecker sentiwordnet = new SentimentsChecker(PATH_SENTIMENT_CANDIDATE_FILE);

		System.out.println("good#a " + sentiwordnet.extract("good", "a"));
//		System.out.println("bad#a " + sentiwordnet.extract("bad", "a"));
		System.out.println("suck#a " + sentiwordnet.extract("suck", "a"));
//		System.out.println("best#a " + sentiwordnet.extract("best", "a"));
//		System.out.println("better#a " + sentiwordnet.extract("better", "a"));

		System.out.println("good#n " + sentiwordnet.extract("good", "n"));
//		System.out.println("best#n " + sentiwordnet.extract("best", "n"));
		System.out.println("suck#n " + sentiwordnet.extract("suck", "n"));

	}
	
	//test con algoritmo custom.
	private static int getSentimentScore(String input) {
		// normalize!
		input = input.toLowerCase();
		input = input.trim();
		// remove all non alpha-numeric non whitespace chars
		input = input.replaceAll("[^a-zA-Z0-9\\s]", "");

		int negCounter = 0;
		int posCounter = 0;

		// so what we got?
		String[] words = input.split(" ");

		// check if the current word appears in our reference lists...
		for (int i = 0; i < words.length; i++) {
			if (posWords.contains(words[i])) {
				posCounter++;
			}
			if (negWords.contains(words[i])) {
				negCounter++;
			}
		}

		// positive matches MINUS negative matches
		int result = (posCounter - negCounter);

		// negative?
		if (result < 0) {
			return -1;
			// or positive?
		} else if (result > 0) {
			return 1;
		}

		// neutral to the rescue!
		return 0;
	}
	
	
	
	

}

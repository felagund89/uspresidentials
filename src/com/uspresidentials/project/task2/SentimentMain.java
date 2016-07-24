package com.uspresidentials.project.task2;

import java.io.IOException;


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
*/



public class SentimentMain {

	private static final String PATHS_ENTIMENT_WORDNET_FILE="/home/felagund89/Scrivania/Progetto web and social/swn/www/admin/dump/SentiWordNet_3.0.0_20130122.txt";
	
	
	public static void main(String[] args) throws IOException {
//		if (args.length < 1) {
//			System.err.println("Sentiments checker : analizza il file: " +PATHS_ENTIMENT_WORDNET_FILE );
//			return;
//		}

		SentimentsChecker sentiwordnet = new SentimentsChecker(PATHS_ENTIMENT_WORDNET_FILE);

		System.out.println("good#a " + sentiwordnet.extract("good", "a"));
		System.out.println("bad#a " + sentiwordnet.extract("bad", "a"));
		System.out.println("blue#a " + sentiwordnet.extract("blue", "a"));
		System.out.println("blue#n " + sentiwordnet.extract("blue", "n"));
	}

}

package com.uspresidentials.project.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.uspresidentials.project.task2.SentimentsChecker;

public class PropertiesManager {

	
	public static void main(String[] args) throws IOException {
		String prop =  PropertiesManager.getPropertiesFromFile("PATH_TEST");
		System.out.println("File path prop letto " + prop);
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(prop + "/ciao.txt"),true));
		
		writer.println("Hello");
		writer.flush();
		writer.close();
	}
	
	public static String getPropertiesFromFile(String propName) {

		Properties propFile = new Properties();
		InputStream input = null;
		String propValue = null;

		try {

			String filename = "ConfigPropertiesTwitter.properties";
			input = PropertiesManager.class.getResourceAsStream(filename);
			if (input == null) {
				System.out.println("File non trovato " + filename);
				return "0";
			}

			// carica il file di properties
			propFile.load(input);

			// prende la property ricercata
			propValue = propFile.getProperty(propName);
			System.out.println("Valore della properties: " + propValue);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return propValue;
	}
}

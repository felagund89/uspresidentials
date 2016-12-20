package com.uspresidentials.project.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

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
			//System.out.println("Valore della properties: " + propValue);

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
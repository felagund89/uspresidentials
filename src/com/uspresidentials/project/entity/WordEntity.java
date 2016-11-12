package com.uspresidentials.project.entity;

import java.io.Serializable;

public class WordEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4141742946547654616L;

	
	String word;
	
	int totalOcc;
	
	int numDocOcc;

	
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getTotalOcc() {
		return totalOcc;
	}

	public void setTotalOcc(int totalOcc) {
		this.totalOcc = totalOcc;
	}

	public int getNumDocOcc() {
		return numDocOcc;
	}

	public void setNumDocOcc(int numDocOcc) {
		this.numDocOcc = numDocOcc;
	}
	
	
	
	
	
}

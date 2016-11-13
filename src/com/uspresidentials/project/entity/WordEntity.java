package com.uspresidentials.project.entity;

import java.io.Serializable;

public class WordEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4141742946547654616L;

	
	String word;
	
	Double totalOcc;
	
	Double numDocOcc;

	
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Double getTotalOcc() {
		return totalOcc;
	}

	public void setTotalOcc(Double totalOcc) {
		this.totalOcc = totalOcc;
	}

	public Double getNumDocOcc() {
		return numDocOcc;
	}

	public void setNumDocOcc(Double numDocOcc) {
		this.numDocOcc = numDocOcc;
	}

	
	
	
	
	
	
}

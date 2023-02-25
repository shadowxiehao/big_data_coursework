
package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;

public class NewsTextualDistanceCalculate implements Serializable{

	private static final long serialVersionUID = 1L;
	String id; // unique article identifier
	String title; 
	Double dphScore; // DPH score of article

	public NewsTextualDistanceCalculate(String id, String title, Double dphScore) {
		this.id = id;
		this.title = title;
		this.dphScore = dphScore;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getDphScore() {
		return dphScore;
	}

	public void setDphScore(Double dphScore) {
		this.dphScore = dphScore;
	}

}
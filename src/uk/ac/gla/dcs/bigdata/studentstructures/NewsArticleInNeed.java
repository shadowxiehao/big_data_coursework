package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;


public class NewsArticleInNeed implements Serializable{

	private static final long serialVersionUID = 1L;
	String id; // unique article identifier
	List<String> terms;
	Double dphScore;

	public NewsArticleInNeed(String id, List<String> terms, Double dphScore) {
		this.id = id;
		this.terms = terms;
		this.dphScore = dphScore;
	}


	public Double getDphScore() {
		return dphScore;
	}


	public void setDphScore(Double dphScore) {
		this.dphScore = dphScore;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public List<String> getTerms() {
		return terms;
	}

	public void setParagraphContents(List<String> terms) {
		this.terms = terms;
	}
}


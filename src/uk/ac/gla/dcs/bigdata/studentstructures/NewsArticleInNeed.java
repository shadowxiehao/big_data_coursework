package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

/**
 * This is a class representing a java object that contains NewsArticle id, corresponding
 * pre-processed data of article(remove stop-words and apply stemming), and DPH score, and DPH score
 * of current NewsArticle. 
 */

public class NewsArticleInNeed implements Serializable{

	private static final long serialVersionUID = 1L;
	String id; // unique article identifier
	List<String> terms; // first five paragraphs of content and title of article after pre-process
	Double dphScore; // DPH score of article

	public NewsArticleInNeed(String id, List<String> terms, Double dphScore) {
		this.id = id;
		this.terms = terms;
		this.dphScore = dphScore;
	}

	public NewsArticleInNeed() {
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

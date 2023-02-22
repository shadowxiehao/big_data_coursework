package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;


public class NewsArticleInNeed implements Serializable{

	private static final long serialVersionUID = 1L;
	String id; // unique article identifier
	String title; // article title
	List<String> paragraphContents;
	Double dphScore;

	public NewsArticleInNeed(String id, String title, List<String> paragraphContents, Double dphScore) {
		this.id = id;
		this.title = title;
		this.paragraphContents = paragraphContents;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getParagraphContents() {
		return paragraphContents;
	}

	public void setParagraphContents(List<String> paragraphContents) {
		this.paragraphContents = paragraphContents;
	}
}


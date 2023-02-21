package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;


public class NewsArticleInNeed implements Serializable{

	private static final long serialVersionUID = 1L;
	String id; // unique article identifier
	String title; // article title
	List<String> paragraphContents;

	public NewsArticleInNeed(String id, String title, List<String> paragraphContents) {
		this.id = id;
		this.title = title;
		this.paragraphContents = paragraphContents;
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

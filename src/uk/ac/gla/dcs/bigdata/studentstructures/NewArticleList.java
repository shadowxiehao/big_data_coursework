package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

public class NewArticleList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3927072645124392638L;
	List<NewsArticleInNeed> newsList;
	
	public NewArticleList() {};
	
	public List<NewsArticleInNeed> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<NewsArticleInNeed> newsList) {
		this.newsList = newsList;
	}

	public NewArticleList(List<NewsArticleInNeed> newsProcessed) {
		super();
		this.newsList = newsProcessed;
	}



}

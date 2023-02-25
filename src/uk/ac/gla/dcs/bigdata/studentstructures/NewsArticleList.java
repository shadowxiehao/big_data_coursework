package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

/**
 * This is a class representing a list of NewsArticleInNeed. 
 */

public class NewsArticleList implements Serializable{
	private static final long serialVersionUID = 1L;
	
	List<NewsArticleInNeed> newsList; // a list of NewsArticleInNeed

	public NewsArticleList() {
	}
	
	public NewsArticleList(List<NewsArticleInNeed> newsArticleInNeedList) {
		this.newsList = newsArticleInNeedList;
	}

	public List<NewsArticleInNeed> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<NewsArticleInNeed> newsList) {
		this.newsList = newsList;
	}

}

package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;



public class NewArticleList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3927072645124392638L;
	List<NewsArticle> newsList;
	
	public NewArticleList() {};
	
	public List<NewsArticle> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<NewsArticle> newsList) {
		this.newsList = newsList;
	}

	public NewArticleList(List<NewsArticle> newsList) {
		super();
		this.newsList = newsList;
	}



}

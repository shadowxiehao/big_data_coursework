package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;

import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;


public class NewsArticleProcessed implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String processedContent;
	NewsArticle news;
	
	public NewsArticleProcessed() {};
	
	public NewsArticleProcessed(NewsArticle news, String processedContent) {
		super();
		this.news = news;
		this.processedContent = processedContent;
	}
	
	public String getProcessedContent() {
		return processedContent;
	}
	
	public NewsArticle getNews() {
		return news;
	}

	public void setNews(NewsArticle news) {
		this.news = news;
	}

	public void setProcessedContent(String processedContent) {
		this.processedContent = processedContent;
	}
	
}

package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;

import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedutilities.TextPreProcessor;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

public class NewsProcessMap implements MapFunction<NewsArticle, NewsArticleInNeed>{
	
	private static final long serialVersionUID = 6475166483071609772L;

	private transient TextPreProcessor processor;
	
	@Override
	public NewsArticleInNeed call(NewsArticle value) throws Exception {
		
		// initial the TextPreProcessor
		if (processor==null) processor = new TextPreProcessor();
		
		// get the value of title of NewsArticle and process the title
		String title = value.getTitle();
		List<String> newTitleString = processor.process(title);
		
		// store the processed title into result 
		List<String> result = newTitleString;
		
		// get the value of contents of NewsArticle
		List<ContentItem> contents = value.getContents();
		
		// iterate the contents
		Iterator<ContentItem> contentsIterator = contents.iterator();
		// count for numbers of paragraph
		int count = 0;
		while (contentsIterator.hasNext()) {
			
			ContentItem currentContent = contentsIterator.next();
			
			String currentContentContent = currentContent.getContent();
			String currentContentSubType = currentContent.getSubtype();
			
			if (currentContentSubType == "paragraph") {
				List<String> resultOfContent = processor.process(currentContentContent);
				result.addAll(resultOfContent);
				count ++;
				
			}
			
			if (count == 5) {
				break;
			}
			
		}
			
//		String finalResult = String.join(",", result);

		return new NewsArticleInNeed(value.getId(),value.getTitle(),result);
	}
	

}
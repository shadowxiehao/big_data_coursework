package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;
import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedutilities.TextPreProcessor;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

public class NewsProcessMap implements FlatMapFunction<NewsArticle, NewsArticleInNeed> {
	
	private static final long serialVersionUID = 6475166483071609772L;

	private transient TextPreProcessor processor;
	
	@Override
	public Iterator<NewsArticleInNeed> call(NewsArticle value) throws Exception {
		
		// initial the TextPreProcessor
		if (processor==null) processor = new TextPreProcessor();
		
		// get the value of title of NewsArticle and process the title
		String title = value.getTitle();
		List<String> newTitleString = null;
		if(title==null||title.isBlank()){

		}else {
			newTitleString = processor.process(title);
		}

		// store the processed title into result 
		List<String> result = (newTitleString==null||newTitleString.isEmpty())?(new ArrayList<String>()):newTitleString;
		
		// get the value of contents of NewsArticle
		List<ContentItem> contents = value.getContents();
		
		// iterate the contents
		Iterator<ContentItem> contentsIterator = contents.iterator();
		// count for numbers of paragraph
		int count = 0;
		while (contentsIterator.hasNext()) {
			
			ContentItem currentContent = contentsIterator.next();
			
			String currentContentContent = currentContent.getContent();
			if(currentContentContent==null||currentContentContent.isBlank()){
				continue;
			}
			String currentContentSubType = currentContent.getSubtype();
			
			if (currentContentSubType!=null && currentContentSubType.equals("paragraph")) {
				List<String> resultOfContent = processor.process(currentContentContent);
				result.addAll(resultOfContent);
				count ++;
			}
			
			if (count == 5) {
				break;
			}
		}
		if (count == 0 || result.isEmpty()) {
//			List<NewsArticleInNeed> nlist = new ArrayList<NewsArticleInNeed>(0);
//			return nlist.iterator();
			return Collections.emptyIterator();
		}
		List<NewsArticleInNeed> nlist = new ArrayList<NewsArticleInNeed>(1);
		nlist.add(new NewsArticleInNeed(value.getId(),result, 0.0));
		return nlist.iterator();
	}

}

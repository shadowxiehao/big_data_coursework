package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.MapFunction;

import uk.ac.gla.dcs.bigdata.providedutilities.DPHScorer;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleList;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;


public class NewsArticleListMap implements FlatMapFunction<DPHInNeed, NewsArticleList> {

	private static final long serialVersionUID = 1L;

	private long sumDocumentCount;
	private Double averageDocumentLength;
	private int sumTermFrequency;

	public NewsArticleListMap( long sumDocumentCount, Double averageDocumentLength, int sumTermFrequency) {
		this.sumDocumentCount = sumDocumentCount;
		this.averageDocumentLength = averageDocumentLength;
		this.sumTermFrequency = sumTermFrequency;
	}


	@Override
	public Iterator<NewsArticleList> call(DPHInNeed dPHInNeed) throws Exception {

		//filter useless data
		if(dPHInNeed.getTermFrequency()==0){
			return Collections.emptyIterator();
		}

		//calculate the DPH score
		Double dphScore = DPHScorer.getDPHScore(
				(short)dPHInNeed.getTermFrequency(),
				sumTermFrequency,
				dPHInNeed.getDocumentlength(),
				averageDocumentLength,
				sumDocumentCount
		);

		//put the score in the data, and return as a single list.
		List<NewsArticleInNeed> asList = new ArrayList<NewsArticleInNeed>(1);
		asList.add(new NewsArticleInNeed(dPHInNeed.getId(),dPHInNeed.getTerms(),dphScore));

		List<NewsArticleList> newsArticleLists = new ArrayList<>(1);
		newsArticleLists.add(new NewsArticleList(asList));

		return newsArticleLists.iterator();
	}
}

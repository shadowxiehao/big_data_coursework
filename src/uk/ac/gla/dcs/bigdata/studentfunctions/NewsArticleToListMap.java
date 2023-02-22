package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;

import uk.ac.gla.dcs.bigdata.studentstructures.NewArticleList;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;


public class NewsArticleToListMap implements MapFunction<NewsArticleInNeed,NewArticleList>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8390177781203483579L;

	@Override
	public NewArticleList call(NewsArticleInNeed n) throws Exception {
		List<NewsArticleInNeed> asList = new ArrayList<NewsArticleInNeed>(1);
		asList.add(n);
		return new NewArticleList(asList);
	}
}


//public class SteamGamesToListMap implements MapFunction<SteamGameStats,SteamGameList> {
//
//	private static final long serialVersionUID = 1L;
//
//	@Override
//	public SteamGameList call(SteamGameStats game) throws Exception {
//		List<SteamGameStats> asList = new ArrayList<SteamGameStats>(1);
//		asList.add(game);
//		return new SteamGameList(asList);
//	}
//
//}

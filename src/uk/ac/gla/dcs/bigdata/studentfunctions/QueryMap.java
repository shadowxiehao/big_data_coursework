//package uk.ac.gla.dcs.bigdata.studentfunctions;
//
//import org.apache.spark.api.java.function.MapFunction;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
//import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
//import uk.ac.gla.dcs.bigdata.providedstructures.Query;
//import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
//import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
//import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleList;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * get results for each query
// */
//public class QueryMap implements MapFunction<Query, DocumentRanking> {
//
//    private Dataset<NewsArticle> news;
//    private Dataset<NewsArticleInNeed> newsArticleInNeed;
//
//    public QueryMap(Dataset<NewsArticle> news, Dataset<NewsArticleInNeed> newsArticleInNeed) {
//        this.news = news;
//        this.newsArticleInNeed = newsArticleInNeed;
//    }
//
//    @Override
//    public DocumentRanking call(Query query) throws Exception {
//
//        // use query terms and NewsArticleInNeed dataset calculate the DPH score of each NewsArticle
//        // store result into a dataset of NewsArticleList called newsAsLists
//        Dataset<NewsArticleList> newsAsLists = DPHCalculator.calculateDPHScore(query.getQueryTerms(), newsArticleInNeed);
//
//        // create a filter to reduce the duplication of similar documents
//        TextualDistanceReducer similarityFilter = new TextualDistanceReducer();
//        // store the filtered result into a NewsArticleList called filterdNewsArticles, which is a list of ranked documents of size 10
//        NewsArticleList filterdNewsArticles = newsAsLists.reduce(similarityFilter);
//        // cast the filterdNewsArticles to a list of NewsArticleInNeed called filteredAndRankedResults
//        List<NewsArticleInNeed> filteredAndRankedResults = filterdNewsArticles.getNewsList();
//
//        // use flatMap to map the filteredAndRankedResults from List<NewsArticleInNeed> to Dataset<RankedResult>
//        RankedResultMap rankedResultMap = new RankedResultMap(filteredAndRankedResults);
//        Dataset<RankedResult> rankedResultDataset = news.flatMap(rankedResultMap, Encoders.bean(RankedResult.class));
//
//        // cast the rankedResultDataset to List<RankedResult>
//        List<RankedResult> rankedResults = rankedResultDataset.collectAsList();
//        // create a reverse Comparator because the given RankedResult's Comparable interface is in the Ascending order
//        Comparator<RankedResult> reverseComparator = Collections.reverseOrder();
//        // sort the result again to make the result follow the order of relevance
//        rankedResults.sort(reverseComparator);
//
//        return new DocumentRanking(query, rankedResults);
//    }
//
//}

package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleList;

import java.util.List;

/**
 * This utility class provides an implementation of the DPH scoring function
 * based on that provided as part of the Terrier.org IR platform.
 * <p>
 * It calculates the relevance score for a single <term,document> pair, given
 * some statistics of the corpus.
 */

/**
 * 	get DPH score(calculate relevance)(provided->DPHScorer.java) , connect with document and query
 */
public class DPHCalculator {

    public static Dataset<NewsArticleList> calculateDPHScore(List<String> queryTerms, Dataset<NewsArticleInNeed> newsArticleInNeed) {

        //--calculate both the TermFrequency (count of the term in the document) and the length of the document (in terms)
        DPHInNeedMap dphInNeedMap = new DPHInNeedMap(queryTerms);

        Encoder<DPHInNeed> dPHInNeedEncoder = Encoders.bean(DPHInNeed.class);
        Dataset<DPHInNeed> dphInNeedDataset = newsArticleInNeed.map(dphInNeedMap, dPHInNeedEncoder);

        //--calculate: The average document length in the corpus (in terms),The sum of term frequencies for the term across all documents,The total number of documents in the corpus
        DPHInNeed reducerResult = dphInNeedDataset.reduce(new DPHInNeedReducer());

        int sumDocumentLength = reducerResult.getDocumentLength();

        long sumDocumentCount = reducerResult.getDocumentCount();//The total number of documents in the corpus
        Double averageDocumentLength = (double) sumDocumentLength / (double) sumDocumentCount;//The average document length in the corpus (in terms)
        //The sum of term frequencies for the term across all documents
        int sumTermFrequency = 0;
        for (Integer termFrequency : reducerResult.getTermFrequencyList()) {
            sumTermFrequency += termFrequency;
        }

        //--calculate the DPH score, and put it in the NewsArticleInNeed, and return as a DataSet<List> for other calculation
        Encoder<NewsArticleList> newsArticleListEncoder = Encoders.bean(NewsArticleList.class);
        NewsArticleListMap newsArticleListMap = new NewsArticleListMap(sumDocumentCount, averageDocumentLength, sumTermFrequency);

        // map the result to 
        return dphInNeedDataset.flatMap(newsArticleListMap, newsArticleListEncoder);
    }


}

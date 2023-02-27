package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeedList;

import java.util.List;

/**
 * This class is responsible for calculating the DPH values
 * based on the query terms and the necessary article information content in NewsArticleInNeed,
 * and converting the results into TextualDistanceInNeedList for subsequent filtering.
 */
public class DPHCalculator {

    /**
     * @param spark             the spark session
     * @param queryTerms        a list of query term
     * @param newsArticleInNeed a data set of NewsArticleInNeed
     * @return the structure needed for similarity detection reducer (id,title,dph,etc.)
     */
    public static Dataset<TextualDistanceInNeedList> calculateDPHScore(SparkSession spark, List<String> queryTerms,
                                                                       Dataset<NewsArticleInNeed> newsArticleInNeed) {

        // broadcast the query terms
        Broadcast<List<String>> queryTermsBroadcast = spark.sparkContext().broadcast(queryTerms,
                scala.reflect.ClassTag$.MODULE$.apply(List.class));

        // user queryTermsBroadcast to calculate both the TermFrequency (count of the
        // term in the document) and the length of the document (in terms)
        DPHInNeedMap dphInNeedMap = new DPHInNeedMap(queryTermsBroadcast);

        // map newsArticleInNeed to DPHInNeed
        Encoder<DPHInNeed> dPHInNeedEncoder = Encoders.bean(DPHInNeed.class);
        Dataset<DPHInNeed> dphInNeedDataset = newsArticleInNeed.map(dphInNeedMap, dPHInNeedEncoder);

        // calculate the average document length in the corpus (in terms), the sum of
        // term frequencies for the term
        // across all documents, the total number of documents in the corpus
        DPHInNeed reducerResult = dphInNeedDataset.reduce(new DPHInNeedReducer());

        // get document length
        int sumDocumentLength = reducerResult.getDocumentLength();
        // The total number of documents in the corpus
        long sumDocumentCount = reducerResult.getDocumentCount();
        // The average document length in the corpus (in terms)
        Double averageDocumentLength = (double) sumDocumentLength / (double) sumDocumentCount;
        // The sum of term frequencies for the term across all documents
        int sumTermFrequency = 0;
        for (Integer termFrequency : reducerResult.getTermFrequencyList()) {
            sumTermFrequency += termFrequency;
        }

        // calculate the DPH score, and put it in the NewsArticleInNeed, and return as a
        // DataSet<List> for other calculation
        Encoder<TextualDistanceInNeedList> TextualDistanceEncoder = Encoders.bean(TextualDistanceInNeedList.class);
        NewsArticleListMap newsArticleListMap = new NewsArticleListMap(sumDocumentCount, averageDocumentLength,
                sumTermFrequency);

        // map the result to Dataset<TextualDistanceInNeedList>
        return dphInNeedDataset.flatMap(newsArticleListMap, TextualDistanceEncoder);
    }

}

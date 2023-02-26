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
 * This class provides an implementation of the DPH scoring function based on
 * provided function. After mapping the input query terms and newsArticle info
 * to DPHInNeed type, we calculate and store the dph score into a list of
 * TextualDistanceInNeed, which is a class contains all information needed for
 * calculating textual distance.
 */

public class DPHCalculator {

	/**
	 * Calculates the DPH score for a list of query term in a data set of documents
	 * 
	 * @param SparkSession      // the spark session
	 * @param queryTerms        // a list of query term
	 * @param newsArticleInNeed // a data set of NewsArticleInNeed
	 * @return
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

package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

import java.util.ArrayList;
import java.util.List;

/**
 * This function is responsible for calculating some of the necessary parts for DPH based on the processed article terms and the query terms, including the following calculations:
 * 1. Calculate the term frequency for each query term and record it in the termFrequencyList variable.
 * 2. Calculate the length of the current article terms and record it in the documentLength variable.
 * 3. Calculate the current article count (i.e. 1) and record it in the documentCount variable.
 * Finally, the above variables are recorded in the DPHInNeed structure for subsequent DPH information calculation.
 */

public class DPHInNeedMap implements MapFunction<NewsArticleInNeed, DPHInNeed> {

    private static final long serialVersionUID = 1L;
    private final List<String> queryTerms;

    /**
     * Default constructor specify the query terms
     *
     * @param queryTerms
     */
    public DPHInNeedMap(Broadcast<List<String>> queryTerms) {
        this.queryTerms = queryTerms.getValue();
    }

    @Override
    public DPHInNeed call(NewsArticleInNeed newsArticleInNeed) throws Exception {
        List<String> documentTerms = newsArticleInNeed.getTerms();

        // calculate TermFrequency (count of the term in the document)
        List<Integer> termCountList = new ArrayList<>();
        for (String term : queryTerms) {
            termCountList.add((int) documentTerms.stream().filter(str -> str.equals(term)).count());
        }

        // The length of the document (in terms)
        int documentLength = documentTerms.size();

        return new DPHInNeed(newsArticleInNeed.getId(), newsArticleInNeed.getTerms(),
                newsArticleInNeed.getTitleLength(), termCountList, documentLength);
    }
}

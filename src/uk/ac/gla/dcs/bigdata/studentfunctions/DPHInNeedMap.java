package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a mapping function that maps NewsArticleInNeed to DPHInNeed. 
 */

public class DPHInNeedMap implements MapFunction<NewsArticleInNeed, DPHInNeed> {
	
    private static final long serialVersionUID = 1L;
    private final List<String> queryTerms;

    public DPHInNeedMap(Broadcast<List<String>> queryTerms) {
        this.queryTerms = queryTerms.getValue();
    }


    @Override
    public DPHInNeed call(NewsArticleInNeed newsArticleInNeed) throws Exception {
        List<String> documentTerms = newsArticleInNeed.getTerms();

        //calculate TermFrequency (count of the term in the document)
        List<Integer> termCountList = new ArrayList<>();
        for (String term : queryTerms) {
            termCountList.add((int) documentTerms.stream().filter(str -> str.equals(term)).count());
        }

        //The length of the document (in terms)
        int documentLength = documentTerms.size();

        return new DPHInNeed(newsArticleInNeed.getId(), newsArticleInNeed.getTerms(), newsArticleInNeed.getTitleLength(), termCountList, documentLength);
    }
}

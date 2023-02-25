package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.providedutilities.DPHScorer;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class NewsArticleListMap implements FlatMapFunction<DPHInNeed, NewsArticleList> {

    private static final long serialVersionUID = 1L;

    private long sumDocumentCount;
    private Double averageDocumentLength;
    private int sumTermFrequency;

    public NewsArticleListMap(long sumDocumentCount, Double averageDocumentLength, int sumTermFrequency) {
        this.sumDocumentCount = sumDocumentCount;
        this.averageDocumentLength = averageDocumentLength;
        this.sumTermFrequency = sumTermFrequency;
    }


    @Override
    public Iterator<NewsArticleList> call(DPHInNeed dPHInNeed) throws Exception {

        //to count the term with non-none termFrequency
        int validTerm = 0;
        Double dphScore = 0.0;

        for (int termFrequency : dPHInNeed.getTermFrequencyList()) {
            if (termFrequency == 0) {
                continue;
            }
            validTerm++;
            //calculate the DPH score
            dphScore += DPHScorer.getDPHScore(
                    (short) termFrequency,
                    sumTermFrequency,
                    dPHInNeed.getDocumentLength(),
                    averageDocumentLength,
                    sumDocumentCount
            );
        }
        //filter useless data
        if (validTerm == 0) {
            return Collections.emptyIterator();
        }
        dphScore /= validTerm;

        //put the score in the data, and return as a single list.
        List<NewsArticleInNeed> asList = new ArrayList<NewsArticleInNeed>(1);
        asList.add(new NewsArticleInNeed(dPHInNeed.getId(), dPHInNeed.getTerms(), dphScore));

        List<NewsArticleList> newsArticleLists = new ArrayList<>(1);
        newsArticleLists.add(new NewsArticleList(asList));

        return newsArticleLists.iterator();
    }
}

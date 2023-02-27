package uk.ac.gla.dcs.bigdata.studentfunctions;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.broadcast.Broadcast;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This function compares the IDs of each original NewsArticle with the IDs
 * of the 10 TextualDistanceInNeed elements selected in the previous step.
 * If they match, the corresponding information is filled into the given RankedResult structure and returned.
 */
public class RankedResultMap implements FlatMapFunction<NewsArticle, RankedResult> {

    private static final long serialVersionUID = 1396281497290131198L;
    private List<TextualDistanceInNeed> textualDistanceInNeedList;

    /**
     * Default constructor specify the textualDistanceInNeed list
     *
     * @param textualDistanceInNeedList the info for calculate textualDistance and
     */
    public RankedResultMap(Broadcast<List<TextualDistanceInNeed>> textualDistanceInNeedList) {
        this.textualDistanceInNeedList = textualDistanceInNeedList.getValue();
    }

    @Override
    public Iterator<RankedResult> call(NewsArticle newsArticle) throws Exception {

        // try to match the articles' news id with the 10 results, if same, then save it
        for (TextualDistanceInNeed t : textualDistanceInNeedList) {
            if (t.getId().equals(newsArticle.getId())) {
                RankedResult[] rankedResults = new RankedResult[]{
                        new RankedResult(t.getId(), newsArticle, t.getDphScore())};
                return new ArrayIterator<>(rankedResults);
            }
        }

        // if not found matched one, return empty
        return Collections.emptyIterator();
    }
}

package uk.ac.gla.dcs.bigdata.studentfunctions;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class uses flatMap that maps NewsArticle to RankedResult.
 */
public class RankedResultMap implements FlatMapFunction<NewsArticle, RankedResult> {

	private static final long serialVersionUID = 1396281497290131198L;
	private List<TextualDistanceInNeed> textualDistanceInNeedList; 

	/**
	 * Default constructor specify the textualDistanceInNeed list
	 * @param textualDistanceInNeedList
	 */
	public RankedResultMap(List<TextualDistanceInNeed> textualDistanceInNeedList) {
		this.textualDistanceInNeedList = textualDistanceInNeedList;
	}

	@Override
	public Iterator<RankedResult> call(NewsArticle newsArticle) throws Exception {

		// try to match the articles' news id with the 10 results, if same, then save it
		for (TextualDistanceInNeed t : textualDistanceInNeedList) {
			if (t.getId().equals(newsArticle.getId())) {
				RankedResult[] rankedResults = new RankedResult[] {
						new RankedResult(t.getId(), newsArticle, t.getDphScore()) };
				return new ArrayIterator<>(rankedResults);
			}
		}

		// if not found matched one, return empty
		return Collections.emptyIterator();
	}
}

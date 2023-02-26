package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;
import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeedList;

import java.util.ArrayList;
import java.util.List;

/**
 * @summary core algorithm of the similarity filtering code: using the approach of sorting by PDH value in descending order
 * and then starting deduplication from the end,
 * to ensure higher efficiency and consistent, correct results every time.
 */
public class TextualDistanceReducer implements ReduceFunction<TextualDistanceInNeedList> {

    private static final long serialVersionUID = -6219052801635837096L;

    /**
     * it will calculate the textual distance of each pair of two
     * TextualDistanceInNeedList, if the value is less then 0.5, the function will
     * return the one with higher dhp score. Finally, return top 10 relevance
     * TextualDistanceInNeed as a list of TextualDistanceInNeed.
     */

    public TextualDistanceInNeedList call(TextualDistanceInNeedList n1, TextualDistanceInNeedList n2) throws Exception {

        List<TextualDistanceInNeed> textualDistanceProcessList = new ArrayList<>(n1.getDistanceInNeedList());
        textualDistanceProcessList.addAll(n2.getDistanceInNeedList());

        // sore by dph score --Descending order
        textualDistanceProcessList.sort((n11, n21) -> n21.getDphScore().compareTo(n11.getDphScore()));

        //Remove articles that are highly similar, and those with low dph value are removed preferentially, that is, the lower data is removed preferentially
        for (int i = textualDistanceProcessList.size() - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                double textDistance = TextDistanceCalculator.similarity(
                        textualDistanceProcessList.get(i).getTitle(),
                        textualDistanceProcessList.get(j).getTitle()
                );
                if (textDistance < 0.5) {
                    textualDistanceProcessList.remove(i);
                    break;
                }
            }
        }
        List<TextualDistanceInNeed> result = new ArrayList<>(textualDistanceProcessList.subList(0, Math.min(textualDistanceProcessList.size(), 10)));
        return new TextualDistanceInNeedList(result);

    }

}

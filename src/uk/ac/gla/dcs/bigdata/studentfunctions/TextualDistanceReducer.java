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
 * <p>
 * This reducer adds the List elements from the two obtained TextualDistanceInNeedList together
 * to obtain a new list called textualDistanceProcessList.
 * The textualDistanceProcessList is then sorted in descending order by DPH score. Starting from the last element,
 * the function compares the similarity of the current element to the elements before it.
 * If the similarity between the current element and the one before it is too high (textDistance < 0.5),
 * then the current element is excluded; otherwise, it is retained.
 * Then, the top 10 elements of textualDistanceProcessList are selected,
 * or the length of the list is used if it is less than 10, and stored in a new list called result.
 * Finally, the function returns this list.
 */
public class TextualDistanceReducer implements ReduceFunction<TextualDistanceInNeedList> {

    private static final long serialVersionUID = -6219052801635837096L;

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

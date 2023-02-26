package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;
import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a reduce function that reduce a data set of
 * TextualDistanceInNeedList to a single TextualDistanceInNeedList.
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

		// create an empty list for return
		List<TextualDistanceInNeed> result = new ArrayList<TextualDistanceInNeed>();

		// make TextualDistanceInNeedList become iterator
		Iterator<TextualDistanceInNeed> iteratorN1 = (n1.getNewsList()).iterator();
		Iterator<TextualDistanceInNeed> iteratorN2 = (n2.getNewsList()).iterator();

		// loop to get each pair of documents
		while (iteratorN1.hasNext()) {
			TextualDistanceInNeed newsN1 = iteratorN1.next();
			String one = newsN1.getTitle();
			while (iteratorN2.hasNext()) {
				TextualDistanceInNeed newsN2 = iteratorN2.next();
				String two = newsN2.getTitle();

				// calculate the textual distance
				double textDist = TextDistanceCalculator.similarity(one, two);

				// if textual distance is less than 0.5, check the dhp score, remove the one
				// with lower dph score.
				if (textDist < 0.5) {
					Double dphN1 = newsN1.getDphScore();
					Double dphN2 = newsN2.getDphScore();
					if (dphN1 >= dphN2) {
						System.out.println(111);
						iteratorN2.remove();
					} else {
						System.out.println(222);
						iteratorN1.remove();
					}
				}
			}
		}

		// add two list to result list
		result.addAll(n1.getNewsList());
		result.addAll(n2.getNewsList());

		// sore by dph score
		result.sort(new Comparator<TextualDistanceInNeed>() {
			public int compare(TextualDistanceInNeed n1, TextualDistanceInNeed n2) {
				return n2.getDphScore().compareTo(n1.getDphScore());
			}
		});

		// return top 10
		List<TextualDistanceInNeed> firstTen = new ArrayList<>(result.subList(0, Math.min(result.size(), 10)));

		return new TextualDistanceInNeedList(firstTen);

	}

}

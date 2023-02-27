package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.providedutilities.DPHScorer;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class uses flatMap that map DPHInNeed to TextualDistanceInNeedList.
 * Using the previously calculated:
 * 1. term frequency of each query term in each document and the length of the current document.
 * 2. total TermFrequency, average article length, and total number of articles obtained by the reducer.
 * This function calculates the DPH score of each query term in the current document
 * and adds up the DPH scores of each term and divides the total by the number of terms to obtain the average DPH score.
 * If the effective average DPH score is greater than 0,
 * the part of the current document's terms that belongs to the title is extracted and converted to a string.
 * Then, the document ID, title, and DPH value are stored in TextualDistanceInNeed,
 * which is stored in TextualDistanceInNeedList for subsequent similarity filtering.
 */
public class NewsArticleListMap implements FlatMapFunction<DPHInNeed, TextualDistanceInNeedList> {

	private static final long serialVersionUID = 1L;

	private final long sumDocumentCount; // The total number of documents in the corpus
	private final Double averageDocumentLength; // The average document length in the corpus (in terms)
	private final int sumTermFrequency; // The sum of term frequencies for the term across all documents

	public NewsArticleListMap(long sumDocumentCount, Double averageDocumentLength, int sumTermFrequency) {
		this.sumDocumentCount = sumDocumentCount;
		this.averageDocumentLength = averageDocumentLength;
		this.sumTermFrequency = sumTermFrequency;
	}

	@Override
	public Iterator<TextualDistanceInNeedList> call(DPHInNeed dPHInNeed) throws Exception {

		// to count the term with non-none termFrequency
		int validTerm = 0;
		double dphScore = 0.0;

		for (int termFrequency : dPHInNeed.getTermFrequencyList()) {
			if (termFrequency == 0) {//means no dph score will come out
				continue;
			}
			validTerm++;
			// calculate the DPH score
			dphScore += DPHScorer.getDPHScore((short) termFrequency, sumTermFrequency, dPHInNeed.getDocumentLength(),
					averageDocumentLength, sumDocumentCount);
		}

		// filter useless data
		if (validTerm == 0) {
			return Collections.emptyIterator();
		}

		// get the average of dph score for all query terms
		dphScore /= dPHInNeed.getTermFrequencyList().size();

		// put the score, title string and article id to a TextualDistanceInNeed
		String titleString = dPHInNeed.getTerms().stream().limit(dPHInNeed.getTitleLength())
				.collect(Collectors.joining(" "));
		List<TextualDistanceInNeed> tdList = new ArrayList<TextualDistanceInNeed>(1);

		// add above list to tdList
		tdList.add(new TextualDistanceInNeed(dPHInNeed.getId(), titleString, dphScore));

		// transfer tdList to textualDistanceInNeedLists
		List<TextualDistanceInNeedList> textualDistanceInNeedLists = new ArrayList<>(1);
		textualDistanceInNeedLists.add(new TextualDistanceInNeedList(tdList));

		return textualDistanceInNeedLists.iterator();
	}
}

package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This function is responsible for calculating the remaining information needed for DPH calculation, namely the total TermFrequency (for calculating the average TermFrequency), the total length of all documents in the corpus, and the total number of documents in the corpus.
 * These values can be obtained by adding up the previously recorded DPHInNeed information in pairs.
 */
public class DPHInNeedReducer implements ReduceFunction<DPHInNeed> {
    private static final long serialVersionUID = 1L;

    @Override
    public DPHInNeed call(DPHInNeed d1, DPHInNeed d2) throws Exception {

        // The sum of term frequencies for the term across all documents
        List<Integer> d1TermFrequencyList = d1.getTermFrequencyList();
        List<Integer> d2TermFrequencyList = d2.getTermFrequencyList();
        List<Integer> sumTermFrequency = IntStream.range(0, d1TermFrequencyList.size())
                .mapToObj(i -> d1TermFrequencyList.get(i) + d2TermFrequencyList.get(i)).collect(Collectors.toList());

        // The sum document length in the corpus (in terms)
        int sumDocumentLength = d1.getDocumentLength() + d2.getDocumentLength();

        // The total number of documents in the corpus
        long sumDocumentCount = d1.getDocumentCount() + d2.getDocumentCount();

        return new DPHInNeed("", null, 0, sumTermFrequency, sumDocumentLength, sumDocumentCount);
    }
}

package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;
import uk.ac.gla.dcs.bigdata.studentstructures.DPHInNeed;

import java.util.ArrayList;

public class DPHInNeedReducer implements ReduceFunction<DPHInNeed> {
    private static final long serialVersionUID = 1L;

    @Override
    public DPHInNeed call(DPHInNeed d1, DPHInNeed d2) throws Exception {

        //The sum of term frequencies for the term across all documents
        int sumTermFrequency = d1.getTermFrequency()+d2.getTermFrequency();

        //The sum document length in the corpus (in terms)
        int sumDocumentLength = d1.getDocumentlength()+d2.getDocumentlength();

        //The total number of documents in the corpus
        long sumDocumentCount = d1.getDocumentCount()+d2.getDocumentCount();

        return new DPHInNeed("",null,sumTermFrequency,sumDocumentLength,sumDocumentCount);
    }
}

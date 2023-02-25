package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

/**
 * This is a class representing a java object that contains NewsArticle id and corresponding
 * data for calculating DHP score for it.
 */

public class DPHInNeed implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // unique article identifier
    private List<String> terms; // origin terms
    private List<Integer> termFrequencyList; // (count) of the term in the document
    private int documentLength; // The length of the document (in terms)
    private Long documentCount; // count of document

    public DPHInNeed(String id, List<String> terms, List<Integer> termFrequencyList, int documentLength) {
        this.id = id;
        this.terms = terms;
        this.termFrequencyList = termFrequencyList;
        this.documentLength = documentLength;
        this.documentCount = 1L;
    }

    public DPHInNeed(String id, List<String> terms, List<Integer> termFrequencyList, int documentLength, Long documentCount) {
        this.id = id;
        this.terms = terms;
        this.termFrequencyList = termFrequencyList;
        this.documentLength = documentLength;
        this.documentCount = documentCount;
    }

    public DPHInNeed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    public Long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Long documentCount) {
        this.documentCount = documentCount;
    }

    public List<Integer> getTermFrequencyList() {
        return termFrequencyList;
    }

    public void setTermFrequencyList(List<Integer> termFrequencyList) {
        this.termFrequencyList = termFrequencyList;
    }

    public int getDocumentLength() {
        return documentLength;
    }

    public void setDocumentLength(int documentLength) {
        this.documentLength = documentLength;
    }
}

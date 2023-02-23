package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

public class DPHInNeed implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // unique article identifier
    private List<String> terms;//origin terms
    private int termFrequency;
    private int documentLength;
    private Long documentCount;

    public DPHInNeed(String id, List<String> terms,int termFrequency, int documentLength ) {
        this.id = id;
        this.terms = terms;
        this.termFrequency = termFrequency;
        this.documentLength = documentLength;
        this.documentCount = 1L;
    }

    public DPHInNeed(String id, List<String> terms, int termFrequency, int documentLength, Long documentCount) {
        this.id = id;
        this.terms = terms;
        this.termFrequency = termFrequency;
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
    public int getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(int termFrequency) {
        termFrequency = termFrequency;
    }

    public int getDocumentlength() {
        return documentLength;
    }

    public void setDocumentlength(int documentlength) {
        documentLength = documentlength;
    }

    public Long getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(Long documentCount) {
        documentCount = documentCount;
    }

}

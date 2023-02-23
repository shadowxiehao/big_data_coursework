
package uk.ac.gla.dcs.bigdata.studentstructures;
import java.io.Serializable;
import java.util.List;

public class NewsArticleList implements Serializable {

    private static final long serialVersionUID = 1L;
    List<NewsArticleInNeed> newsList;

    public NewsArticleList() {
    }

    public NewsArticleList(List<NewsArticleInNeed> newsArticleInNeedList) {
        this.newsList = newsArticleInNeedList;
    }

    public List<NewsArticleInNeed> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<NewsArticleInNeed> newsList) {
        this.newsList = newsList;
    }

    public static class NewsArticleInNeed implements Serializable {

        private static final long serialVersionUID = 1L;
        String id; // unique article identifier
        List<String> terms;
        Double dphScore;

        public NewsArticleInNeed(String id, List<String> terms, Double dphScore) {
            this.id = id;
            this.terms = terms;
            this.dphScore = dphScore;
        }

        public NewsArticleInNeed() {
        }

        public Double getDphScore() {
            return dphScore;
        }

        public void setDphScore(Double dphScore) {
            this.dphScore = dphScore;
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
    }
}

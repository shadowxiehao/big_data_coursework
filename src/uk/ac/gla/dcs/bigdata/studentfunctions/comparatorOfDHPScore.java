package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.Comparator;

import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

public class comparatorOfDHPScore implements Comparator<NewsArticleInNeed>{
    public int compare(NewsArticleInNeed n1, NewsArticleInNeed n2) {
        return (int) (n1.getDphScore() - n2.getDphScore());
    }

}

package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

/**
 * This is a class representing a list of NewsArticleInNeed.
 */

public class TextualDistanceInNeedList implements Serializable {
    private static final long serialVersionUID = 1L;

    List<TextualDistanceInNeed> distanceInNeedList; // a list of NewsArticleInNeed

    public TextualDistanceInNeedList() {
    }

    public TextualDistanceInNeedList(List<TextualDistanceInNeed> distanceInNeedList) {
        this.distanceInNeedList = distanceInNeedList;
    }

    public List<TextualDistanceInNeed> getNewsList() {
        return distanceInNeedList;
    }

    public void setNewsList(List<TextualDistanceInNeed> distanceInNeedList) {
        this.distanceInNeedList = distanceInNeedList;
    }

}

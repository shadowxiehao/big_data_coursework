package uk.ac.gla.dcs.bigdata.studentfunctions;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RankedResultMap implements FlatMapFunction<NewsArticle, RankedResult> {

    private List<NewsArticleInNeed> newsArticleInNeedList = null;

    public RankedResultMap(List<NewsArticleInNeed> newsArticleInNeedList) {
        this.newsArticleInNeedList = newsArticleInNeedList;
    }

    @Override
    public Iterator<RankedResult> call(NewsArticle newsArticle) throws Exception {

        //try to match the articles' news id with the 10 results, if same, then save it
        for(NewsArticleInNeed newsArticleInNeed:newsArticleInNeedList){
            if(newsArticleInNeed.getId().equals(newsArticle.getId())){
                RankedResult[] rankedResults = new RankedResult[]{new RankedResult(newsArticleInNeed.getId(),newsArticle,newsArticleInNeed.getDphScore())};
                return new ArrayIterator<>(rankedResults);
            }
        }

        return Collections.emptyIterator();
    }
}

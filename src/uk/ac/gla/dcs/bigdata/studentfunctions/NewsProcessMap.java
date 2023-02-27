package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.FlatMapFunction;
import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedutilities.TextPreProcessor;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class uses flatMap that map NewsArticle to NewsArticleInNeed.
 * removing unnecessary data and low information-entropy words
 */
public class NewsProcessMap implements FlatMapFunction<NewsArticle, NewsArticleInNeed> {

    private static final long serialVersionUID = 6475166483071609772L;

    private transient TextPreProcessor processor;

    @Override
    public Iterator<NewsArticleInNeed> call(NewsArticle value) throws Exception {

        // initial the TextPreProcessor
        if (processor == null)
            processor = new TextPreProcessor();

        // get the value of title of NewsArticle and process the title
        String title = value.getTitle();

        // set a list of string
        List<String> newTitleString = null;

        // if title is empty, return
        if (title == null || title.isBlank() || title.equals("null")) {
            return Collections.emptyIterator();
        } else {
            // store pre-processed title into newTitleString
            newTitleString = processor.process(title);
        }

        // store the processed title into result, checked again if there is any null
        if (newTitleString == null || newTitleString.isEmpty()) {
            return Collections.emptyIterator();
        }
        List<String> result = newTitleString;

        // store the position of the title in all terms
        int titleLenght = newTitleString.size();

        // get the value of contents of NewsArticle
        List<ContentItem> contents = value.getContents();

        // iterate the contents
        Iterator<ContentItem> contentsIterator = contents.iterator();
        // count for numbers of paragraph
        int count = 0;
        while (contentsIterator.hasNext()) {

            // check if current content is null or not
            ContentItem currentContent = contentsIterator.next();
            if (currentContent == null) {
                continue;
            }
            String currentContentContent = currentContent.getContent();
            if (currentContentContent == null || currentContentContent.isBlank()) {
                continue;
            }

            // get the sub-type of current content
            String currentContentSubType = currentContent.getSubtype();

            // if sub-type is paragraph,non-null after processing, then store into result
            if (currentContentSubType != null && currentContentSubType.equals("paragraph")) {
                List<String> resultOfContent = processor.process(currentContentContent);
                if (resultOfContent != null && !resultOfContent.isEmpty()) {
                    result.addAll(resultOfContent);
                    count++;
                }
            }

            // only take first 5 paragraphs
            if (count == 5) {
                break;
            }
        }

        // if got nothing, return empty
        if (count == 0 || result.isEmpty()) {
            return Collections.emptyIterator();
        }
        // otherwise, store result into NewsArticleInNeed
        List<NewsArticleInNeed> nlist = new ArrayList<NewsArticleInNeed>(1);
        nlist.add(new NewsArticleInNeed(value.getId(), result, titleLenght, 0.0));

        return nlist.iterator();
    }

}

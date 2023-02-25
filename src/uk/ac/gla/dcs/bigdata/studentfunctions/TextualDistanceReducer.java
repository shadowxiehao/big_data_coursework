package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;
import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class TextualDistanceReducer implements ReduceFunction<TextualDistanceInNeedList> {

    /**
     *
     */
    private static final long serialVersionUID = -6219052801635837096L;

    /**
     *
     */


    // textual distance(reduce duplication)(provided->TextDistanceCalculator.java)
    public TextualDistanceInNeedList call(TextualDistanceInNeedList n1, TextualDistanceInNeedList n2) throws Exception {
        List<TextualDistanceInNeed> temp = new ArrayList<TextualDistanceInNeed>();

        Iterator<TextualDistanceInNeed> iteratorN1 = n1.getNewsList().iterator();
        Iterator<TextualDistanceInNeed> iteratorN2 = n2.getNewsList().iterator();
        while (iteratorN1.hasNext()) {
            TextualDistanceInNeed newsN1 = iteratorN1.next();
            while (iteratorN2.hasNext()) {
                TextualDistanceInNeed newsN2 = iteratorN2.next();
                String one = newsN1.getTitle();
                String two = newsN2.getTitle();
                double textDist = TextDistanceCalculator.similarity(one, two);
                if (textDist <= 0.5) {
                    Double dphN1 = newsN1.getDphScore();
                    Double dphN2 = newsN2.getDphScore();
                    if (dphN1 >= dphN2) {
//	    			  temp.add(newsN1);
                        iteratorN2.remove();
                    } else {
                        // temp.add(newsN2);
                        iteratorN1.remove();
                    }
                }
//	    	  else {
//	    		  temp.add(newsN1);
//	    		  temp.add(newsN2);
//	    	  }

            }
        }
        temp.addAll(n1.getNewsList());
        temp.addAll(n2.getNewsList());
        temp.sort(new Comparator<TextualDistanceInNeed>() {
            public int compare(TextualDistanceInNeed n1, TextualDistanceInNeed n2) {
                return n2.getDphScore().compareTo(n1.getDphScore());
            }
        });
//	    comparatorOfDHPScore comparator = new comparatorOfDHPScore();
//	    Collections.sort(temp, comparator);

        List<TextualDistanceInNeed> firstTen = new ArrayList<>(temp.subList(0, Math.min(temp.size(), 10)));
        TextualDistanceInNeedList result = new TextualDistanceInNeedList(firstTen);

//	    if (temp.size()<= 10){
//	    	return new NewsArticleList(temp);
//	    }
//
//	    return new NewsArticleList(temp.subList(0,10));

        return result;

    }

}




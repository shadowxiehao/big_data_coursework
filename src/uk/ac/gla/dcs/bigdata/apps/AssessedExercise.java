package uk.ac.gla.dcs.bigdata.apps;

import org.apache.spark.SparkConf;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.*;
import uk.ac.gla.dcs.bigdata.providedfunctions.NewsFormaterMap;
import uk.ac.gla.dcs.bigdata.providedfunctions.QueryFormaterMap;
import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.studentfunctions.DPHCalculator;
import uk.ac.gla.dcs.bigdata.studentfunctions.NewsProcessMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.RankedResultMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.TextualDistanceReducer;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeed;
import uk.ac.gla.dcs.bigdata.studentstructures.TextualDistanceInNeedList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is the main class where your Spark topology should be specified.
 * <p>
 * By default, running this class will execute the topology defined in the
 * rankDocuments() method in local mode, although this may be overriden by the
 * spark.master environment variable.
 *
 * @author Richard
 */
public class AssessedExercise {

    public static void main(String[] args) {

        File hadoopDIR = new File("resources/hadoop/"); // represent the hadoop directory as a Java file so we can get
        // an absolute path for it
        System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath()); // set the JVM system property so that Spark
        // finds it

        // The code submitted for the assessed exerise may be run in either local or
        // remote modes
        // Configuration of this will be performed based on an environment variable
        String sparkMasterDef = System.getenv("spark.master");
//		String sparkMasterDef = System.getenv("spark.local");
        if (sparkMasterDef == null)
//            sparkMasterDef = "local[2]"; // default is local mode with two executors
            sparkMasterDef = "local[*]"; // full executors for testing

        String sparkSessionName = "BigDataAE"; // give the session a name

        // Create the Spark Configuration
        SparkConf conf = new SparkConf().setMaster(sparkMasterDef).setAppName(sparkSessionName);

        // Create the spark session
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

        // Get the location of the input queries
        String queryFile = System.getenv("bigdata.queries");
        if (queryFile == null)
            queryFile = "data/queries.list"; // default is a sample with 3 queries

        // Get the location of the input news articles
        String newsFile = System.getenv("bigdata.news");
        if (newsFile == null)
//            newsFile = "data/TREC_Washington_Post_collection.v3.example.json"; // default is a sample of 5000 news articles
            newsFile = "data/TREC_Washington_Post_collection.v2.jl.fix.json"; // the 5g data json,we put the data in the .gitignore, so you need to personally download it

        // Call the student's code
        List<DocumentRanking> results = rankDocuments(spark, queryFile, newsFile);

        // Close the spark session
        spark.close();

        // Check if the code returned any results
        if (results == null)
            System.err
                    .println("Topology return no rankings, student code may not be implemented, skiping final write.");
        else {

            // We have set of output rankings, lets write to disk

            // Create a new folder
            File outDirectory = new File("results/" + System.currentTimeMillis());
            if (!outDirectory.exists())
                outDirectory.mkdir();

            // Write the ranking for each query as a new file
            for (DocumentRanking rankingForQuery : results) {
                rankingForQuery.write(outDirectory.getAbsolutePath());
            }
        }

    }

    public static List<DocumentRanking> rankDocuments(SparkSession spark, String queryFile, String newsFile) {
        long startTime = System.currentTimeMillis();// time counter
        // Load queries and news articles
        Dataset<Row> queriesjson = spark.read().text(queryFile);
        Dataset<Row> newsjson = spark.read().text(newsFile); // read in files as string rows, one row per article

        // Perform an initial conversion from Dataset<Row> to Query and NewsArticle Java objects
        Dataset<Query> queries = queriesjson.map(new QueryFormaterMap(), Encoders.bean(Query.class)); // this converts each row into a Query
        Dataset<NewsArticle> news = newsjson.map(new NewsFormaterMap(), Encoders.bean(NewsArticle.class)); // this converts each row into a NewsArticle

        // ----------------------------------------------------------------
        // Your Spark Topology should be defined here
        // ----------------------------------------------------------------

        // map news to a dataset of NewsArticleInNeed, which contains article id, the
        // length of title, pre-process content and title(remove stopwords
        // and apply stemming), and DPH score
        Encoder<NewsArticleInNeed> NewsArticleProcessedEncoder = Encoders.bean(NewsArticleInNeed.class);
        Dataset<NewsArticleInNeed> newsArticleInNeed = news.flatMap(new NewsProcessMap(), NewsArticleProcessedEncoder);

        // create a empty list of DocumentRanking
        List<DocumentRanking> documentRankingList = new ArrayList<>();

        // for each query, rank the text documents by relevance for that query, as well
        // as filter out any overly similar documents in the final ranking,
        // and return top 10 documents
        for (Query query : queries.collectAsList()) {

            // use query terms and NewsArticleInNeed dataset calculate the DPH score of each NewsArticle
            // store result into a dataset of TextualDistanceInNeedList called newsAsLists,
            // TextualDistanceInNeedList contains a list of TextualDistanceInNeed,
            // which is containing all information for calculating textual distance
            Dataset<TextualDistanceInNeedList> newsAsLists = DPHCalculator.calculateDPHScore(spark,
                    query.getQueryTerms(), newsArticleInNeed);

            // create a filter to reduce the duplication of similar documents
            TextualDistanceReducer similarityFilter = new TextualDistanceReducer();
            // store the filtered result into a TextualDistanceInNeedList called
            // filterdNewsArticles, which is a list of ranked documents of size 10
            TextualDistanceInNeedList filterdNewsArticles = newsAsLists.reduce(similarityFilter);
            // cast the filterdNewsArticles to a list of TextualDistanceInNeed called
            // filteredAndRankedResults
            List<TextualDistanceInNeed> filteredAndRankedResults = filterdNewsArticles.getDistanceInNeedList();

            // broadcast the 10 filtered TextualDistanceInNeed
            Broadcast<List<TextualDistanceInNeed>> filteredAndRankedResultsBroadcast = spark.sparkContext().broadcast(filteredAndRankedResults,
                    scala.reflect.ClassTag$.MODULE$.apply(List.class));

            // use flatMap to map the filteredAndRankedResults from
            // List<TextualDistanceInNeed> to Dataset<RankedResult>
            RankedResultMap rankedResultMap = new RankedResultMap(filteredAndRankedResultsBroadcast);
            Dataset<RankedResult> rankedResultDataset = news.flatMap(rankedResultMap,
                    Encoders.bean(RankedResult.class));

            // cast the rankedResultDataset to List<RankedResult>
            List<RankedResult> rankedResults = rankedResultDataset.collectAsList();
            // create a reverse Comparator because the given RankedResult's Comparable
            // interface is in the Ascending order
            Comparator<RankedResult> reverseComparator = Collections.reverseOrder();
            // sort the result again to make the result follow the order of relevance
            rankedResults.sort(reverseComparator);

            // add the ranked top 10 documents to documentRankingList
            documentRankingList.add(new DocumentRanking(query, rankedResults));
        }

        // log the computation, print the documentRankingList
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("total_time:" + timeElapsed + "(millisecond)" + "\n" + "documentRankingList:" + "\n"
                + documentRankingList);

        // return the output
        return documentRankingList;
    }

}

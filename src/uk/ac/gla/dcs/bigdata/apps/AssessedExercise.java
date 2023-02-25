package uk.ac.gla.dcs.bigdata.apps;

import org.apache.spark.SparkConf;
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
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is the main class where your Spark topology should be specified.
 * <p>
 * By default, running this class will execute the topology defined in the
 * rankDocuments() method in local mode, although this may be overriden by
 * the spark.master environment variable.
 *
 * @author Richard
 */
public class AssessedExercise {

    public static void main(String[] args) {

        File hadoopDIR = new File("resources/hadoop/"); // represent the hadoop directory as a Java file so we can get an absolute path for it
        System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath()); // set the JVM system property so that Spark finds it

        // The code submitted for the assessed exerise may be run in either local or remote modes
        // Configuration of this will be performed based on an environment variable
        String sparkMasterDef = System.getenv("spark.master");
//		String sparkMasterDef = System.getenv("spark.local");
        if (sparkMasterDef == null) sparkMasterDef = "local[*]"; // default is local mode with two executors

        String sparkSessionName = "BigDataAE"; // give the session a name

        // Create the Spark Configuration
        SparkConf conf = new SparkConf()
                .setMaster(sparkMasterDef)
                .setAppName(sparkSessionName)
//                .set("spark.local.dir", "temp")
//                .set("spark.eventLog.enabled", "true")
//                .set("spark.eventLog.dir", "temp")
                ;
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        sc.setCheckpointDir("temp");

        // Create the spark session
        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        // Get the location of the input queries
        String queryFile = System.getenv("bigdata.queries");
        if (queryFile == null) queryFile = "data/queries.list"; // default is a sample with 3 queries

        // Get the location of the input news articles
        String newsFile = System.getenv("bigdata.news");
        if (newsFile == null)
//            newsFile = "data/TREC_Washington_Post_collection.v3.example.json"; // default is a sample of 5000 news articles
            newsFile = "data/TREC_Washington_Post_collection.v2.jl.fix.json"; // the 5g data json

        // Call the student's code
        List<DocumentRanking> results = rankDocuments(spark, queryFile, newsFile);

        // Close the spark session
        spark.close();

        // Check if the code returned any results
        if (results == null)
            System.err.println("Topology return no rankings, student code may not be implemented, skiping final write.");
        else {

            // We have set of output rankings, lets write to disk

            // Create a new folder
            File outDirectory = new File("results/" + System.currentTimeMillis());
            if (!outDirectory.exists()) outDirectory.mkdir();

            // Write the ranking for each query as a new file
            for (DocumentRanking rankingForQuery : results) {
                rankingForQuery.write(outDirectory.getAbsolutePath());
            }
        }

    }


    public static List<DocumentRanking> rankDocuments(SparkSession spark, String queryFile, String newsFile) {
        long startTime = System.currentTimeMillis();//time counter
        // Load queries and news articles
        Dataset<Row> queriesjson = spark.read().text(queryFile);
        Dataset<Row> newsjson = spark.read().text(newsFile); // read in files as string rows, one row per article

        // Perform an initial conversion from Dataset<Row> to Query and NewsArticle Java objects
        Dataset<Query> queries = queriesjson.map(new QueryFormaterMap(), Encoders.bean(Query.class)); // this converts each row into a Query
        Dataset<NewsArticle> news = newsjson.map(new NewsFormaterMap(), Encoders.bean(NewsArticle.class)); // this converts each row into a NewsArticle

        //----------------------------------------------------------------
        // Your Spark Topology should be defined here
        //----------------------------------------------------------------

        // map news to a dataset of NewsArticleInNeed, which contains article id, pre-process content and title(remove stopwords 
        // and apply stemming), and DPH score
        Encoder<NewsArticleInNeed> NewsArticleProcessedEncoder = Encoders.bean(NewsArticleInNeed.class);
        Dataset<NewsArticleInNeed> newsArticleInNeed = news.flatMap(new NewsProcessMap(), NewsArticleProcessedEncoder);

//        newsArticleInNeed.persist(StorageLevel.MEMORY_AND_DISK_SER());

        // create a empty list of DocumentRanking
        List<DocumentRanking> documentRankingList = new ArrayList<>();

        // for each query, rank the text documents by relevance for that query, as well as filter out any overly similar documents in the final ranking,
        // and return top 10 documents

        for (Query query : queries.collectAsList()) {

            // use query terms and NewsArticleInNeed dataset calculate the DPH score of each NewsArticle
            // store result into a dataset of NewsArticleList called newsAsLists
            Dataset<NewsArticleList> newsAsLists = DPHCalculator.calculateDPHScore(spark, query.getQueryTerms(), newsArticleInNeed);

            // create a filter to reduce the duplication of similar documents
            TextualDistanceReducer similarityFilter = new TextualDistanceReducer();
            // store the filtered result into a NewsArticleList called filterdNewsArticles, which is a list of ranked documents of size 10
            NewsArticleList filterdNewsArticles = newsAsLists.reduce(similarityFilter);
            // cast the filterdNewsArticles to a list of NewsArticleInNeed called filteredAndRankedResults
            List<NewsArticleInNeed> filteredAndRankedResults = filterdNewsArticles.getNewsList();

            // use flatMap to map the filteredAndRankedResults from List<NewsArticleInNeed> to Dataset<RankedResult>
            RankedResultMap rankedResultMap = new RankedResultMap(filteredAndRankedResults);
            Dataset<RankedResult> rankedResultDataset = news.flatMap(rankedResultMap, Encoders.bean(RankedResult.class));

            // cast the rankedResultDataset to List<RankedResult>
            List<RankedResult> rankedResults = rankedResultDataset.collectAsList();
            // create a reverse Comparator because the given RankedResult's Comparable interface is in the Ascending order
            Comparator<RankedResult> reverseComparator = Collections.reverseOrder();
            // sort the result again to make the result follow the order of relevance
            rankedResults.sort(reverseComparator);

            // add the ranked top 10 documents to documentRankingList
            documentRankingList.add(new DocumentRanking(query, rankedResults));
        }

//        QueryMap queryMap = new QueryMap(news, newsArticleInNeed);
//        Dataset<DocumentRanking> rankedResultDataset = queries.map(queryMap, Encoders.bean(DocumentRanking.class));
//        documentRankingList = rankedResultDataset.collectAsList();

        // log the computation, print the documentRankingList
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("total_time:" + timeElapsed + "(millisecond)" + "\n" + "documentRankingList:" + "\n" + documentRankingList);
        //for testing (see the web ui)
        try {
            Thread.currentThread().sleep(100000000);
        } catch (Exception e) {

        }

        // return the output 
        return documentRankingList;
    }

}
